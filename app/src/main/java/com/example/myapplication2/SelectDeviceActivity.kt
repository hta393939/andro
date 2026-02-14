package com.example.myapplication2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.annotation.RequiresPermission
import com.example.myapplication2.listeners.CompositeListener
import com.example.myapplication2.listeners.GestureDetectListener
import com.example.myapplication2.senders.RelativeMouseSender
import com.example.myapplication2.senders.SensorSender
import com.example.myapplication2.listeners.ViewListener
//import org.jetbrains.anko.*
import com.example.myapplication2.extraLibraries.CustomGestureDetector
import com.example.myapplication2.senders.KeyboardSender


class SelectDeviceActivity: Activity(),KeyEvent.Callback {

    private var autoPairMenuItem : MenuItem? =null
    private var screenOnMenuItem : MenuItem? =null

    private var bluetoothStatus : MenuItem? =null

    private lateinit var linearLayout: _LinearLayout
    @OptIn(ExperimentalUnsignedTypes::class)
    private var sender: SensorSender? = null
    //private var  viewTouchListener : ViewListener? = null
    private var modifier_checked_state : Int =0
    private var  rMouseSender : RelativeMouseSender? = null

    private var rKeyboardSender : KeyboardSender? = null




    @SuppressLint("ResourceType")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

            verticalLayout {



                        // justify your toolbar






                linearLayout = this
                id = 0x69
                //gravity = Gravity.CENTER
//                button("TEST") {
//                    setOnClickListener {
//                        rMouseSender?.sendTestClick() ?: toast("Not connected")
//
//                    }
//                }



                textView(){
                  id= R.id.mouseView
                  background=getDrawable(R.drawable.view_border)


                    text="Trackpad"
                    gravity=Gravity.CENTER


                }.lparams(width= matchParent,height = matchParent )

            }
    }

    fun getContext(): Context {
        return this
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    public override fun onStart() {
        super.onStart()

        bluetoothStatus?.icon=getDrawable(R.drawable.ic_action_app_not_connected)
        bluetoothStatus?.tooltipText="App not connected via bluetooth"


        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)


        BluetoothController.autoPairFlag= sharedPref.getBoolean(getString(R.string.auto_pair_flag),false)

        autoPairMenuItem?.isChecked= sharedPref.getBoolean(getString(R.string.auto_pair_flag),false)

        screenOnMenuItem?.isChecked= sharedPref.getBoolean(getString(R.string.screen_on_flag),false)

        if(sharedPref.getBoolean(getString(R.string.screen_on_flag),false)) window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        else getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val trackPadView = find<View>(R.id.mouseView)

        BluetoothController.init(this)

        BluetoothController.getSender { hidd, device ->
            Log.wtf("weightages", "Callback called")
            val mainHandler = Handler(getContext().mainLooper)

            mainHandler.post(object : Runnable{
                override fun run() {


                    rKeyboardSender= KeyboardSender(hidd,device)





                    val rMouseSender = RelativeMouseSender(hidd,device)
                    Log.i("TAGdddUI", Thread.currentThread().getName());
                    val viewTouchListener = ViewListener(hidd, device, rMouseSender)
                    val mDetector = CustomGestureDetector(getContext(), GestureDetectListener(rMouseSender))

                    val gTouchListener = object : View.OnTouchListener {

                        override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                            return mDetector.onTouchEvent(event)

                        }

                    }




                    val composite : CompositeListener = CompositeListener()

                    composite.registerListener(gTouchListener)
                    composite.registerListener(viewTouchListener)
                    trackPadView.setOnTouchListener(composite)

                    bluetoothStatus?.icon = getDrawable(R.drawable.ic_action_app_connected)
                    bluetoothStatus?.tooltipText="App Connected via bluetooth"

                    //------------trackPadView.setOnTouchListener(viewTouchListener)
                }

            })

            //========val rMouseSender = RelativeMouseSender(hidd,device)
            //-------this.rMouseSender=rMouseSender
           //val mDetector = GestureDetector(this, GestureDetectListener(rMouseSender))

            Log.i("TAGddd", Thread.currentThread().getName());
            //--------------val viewTouchListener = ViewListener(hidd, device, rMouseSender)//=
//            val gTouchListener = object : View.OnTouchListener {
//
//                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//
//                    return mDetector.onTouchEvent(event)
//
//                }
//
//            }


//
            ////////-----trackPadView.setOnTouchListener(viewTouchListener)
//            myView.setOnClickListener {object : View.OnClickListener {
//                override fun onClick(v: View?) {
//                    rMouseSender.sendTestClick()
//                }
//
//
//            }}

//            val composite : CompositeListener = CompositeListener()
//            composite.registerListener(viewTouchListener)
//            composite.registerListener(gTouchListener)
//            myView.setOnTouchListener(composite)

         //   sender = SensorSender(hidd, device)
         //   initSensor()
        }

        BluetoothController.getDisconnector{
            val mainHandler = Handler(getContext().mainLooper)

            mainHandler.post(object : Runnable {
                override fun run() {
                    bluetoothStatus?.icon=getDrawable(R.drawable.ic_action_app_not_connected)
                    bluetoothStatus?.tooltipText="App not connected via bluetooth"
                }
            })
        }


    }
/*
    private fun initSensor() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        sensorManager.registerListener(sender, sensor, SensorManager.SENSOR_DELAY_GAME)
    }
*/


    public override fun onPause() {
        super.onPause()

    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    public override fun onStop() {
        super.onStop()
        BluetoothController.btHid?.unregisterApp()

        BluetoothController.hostDevice=null
        BluetoothController.btHid=null
    }


    public override fun onCreateOptionsMenu(menu: Menu?): Boolean {

       // val trackPadView = find<View>(R.id.mouseView)

        menuInflater.inflate(R.menu.select_device_activity_menu, menu)

        bluetoothStatus = menu?.findItem(R.id.ble_app_connection_status)
        autoPairMenuItem= menu?.findItem(R.id.action_autopair)

        screenOnMenuItem = menu?.findItem(R.id.action_screen_on)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)

        screenOnMenuItem?.isChecked = sharedPref.getBoolean(getString(R.string.screen_on_flag),false);
        Log.i("crown","jewel")
        autoPairMenuItem?.isChecked= sharedPref.getBoolean(getString(R.string.auto_pair_flag),false)


//        val checkBox = menu?.findItem(R.id.check_modifier_state)?.actionView as CheckBox
//        checkBox.text = "Modifier Released"

        //getMenuInflater().inflate(R.menu.select_device_activity_menu, menu);
        return super.onCreateOptionsMenu(menu)
    }

//    companion object {
//        fun callAlert(){
//            val builder = AlertDialog.Builder(SelectDeviceActivity.this)
//            builder.setTitle("Make your selection")
//            builder.setItems(items, DialogInterface.OnClickListener { dialog, item ->
//                // Do something with the selection
//                mDoneButton.setText(items[item])
//            })
//            val alert = builder.create()
//            alert.show()
//        }
//    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {

        Log.d("keyeventdown_tag","desc is - $event")


        if(rKeyboardSender !=null && event !=null) {
            var rvalue: Boolean? = false
            //rvalue = rKeyboardSender?.sendKeyboard(keyCode, event,modifier_checked_state)

            if (rvalue == true) return true


            else return super.onKeyDown(keyCode, event)

        }
        else return super.onKeyDown(keyCode, event)


    }


    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {


        Log.d("keyeventup_tag","desc is - $event")

        if(rKeyboardSender !=null && event !=null) {
            var rvalue: Boolean? = false
            rvalue = rKeyboardSender?.sendKeyboard(keyCode, event,modifier_checked_state)

            if (rvalue == true) return true


            else return super.onKeyDown(keyCode, event)

        }
        else return super.onKeyUp(keyCode, event)


    }


    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            // User chose the "Settings" item, show the app settings UI...
            true
        }

        R.id.action_keyboard -> {




                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0)




            true
        }

        R.id.check_modifier_state -> {

//            item.isChecked = !item.isChecked
//            Log.i("bbbb","${item.isChecked}")
//            if(item.isChecked)
//                modifier_checked_state=1
//            else modifier_checked_state=0
            if(modifier_checked_state==1)
            {
                modifier_checked_state=0
                item.title="(N)"
                rKeyboardSender?.sendNullKeys()

            }

            else
            {
                modifier_checked_state=1
                item.title="(P)"

            }



            true
        }
        R.id.action_disconnect -> {

            BluetoothController.btHid?.disconnect(BluetoothController.hostDevice)
            bluetoothStatus?.icon=getDrawable(R.drawable.ic_action_app_not_connected)
            bluetoothStatus?.tooltipText="App not connected via bluetooth"
            true
        }

        R.id.action_screen_on -> {
            val sharedPref = this?.getPreferences(MODE_PRIVATE)
            if(item.isChecked) {
                item.isChecked = false

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                with(sharedPref.edit())
                {
                    putBoolean(getString(R.string.screen_on_flag), false)
                    commit()
                }

            }
            else
            {
                item.isChecked=true
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

                with(sharedPref.edit())
                {
                    putBoolean(getString(R.string.screen_on_flag), true)
                    commit()
                }

            }

            true
        }

        R.id.action_autopair -> {
            val sharedPref = this?.getPreferences(MODE_PRIVATE)
            if(item.isChecked) {
                item.isChecked = false
                BluetoothController.autoPairFlag=false

                with(sharedPref.edit())
                {
                    putBoolean(getString(R.string.auto_pair_flag), BluetoothController.autoPairFlag)
                    commit()
                }

            }
            else
            {
                item.isChecked=true
                BluetoothController.autoPairFlag=true
                if(BluetoothController.btHid?.getConnectionState(BluetoothController.mpluggedDevice)==0 && BluetoothController.mpluggedDevice!= null && BluetoothController.autoPairFlag ==true)
                {
                    BluetoothController.btHid?.connect(BluetoothController.mpluggedDevice)
                    //hostDevice.toString()
                }
                with(sharedPref.edit())
                {
                    putBoolean(getString(R.string.auto_pair_flag), BluetoothController.autoPairFlag)
                    commit()
                }

            }
            true
        }


        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }



}