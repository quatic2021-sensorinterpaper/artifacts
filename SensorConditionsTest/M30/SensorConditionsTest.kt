package org.owntracks.android

import android.Manifest
import androidx.test.rule.GrantPermissionRule
import org.junit.BeforeClass
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import android.bluetooth.BluetoothManager
import android.os.PowerManager
import android.app.NotificationManager
import android.content.Context
import android.provider.Settings
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import org.junit.Rule
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList

open class SensorConditionsTest {
    @get:Rule
    var mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE)

    companion object {
        private val allStatus: ArrayList<Boolean>? = null
        private val status: Pair<String, Boolean>? = null
        private var lastStatus: LastStatus? = null
        private const val SENSORSOFF = "Sensors Off"
        private const val LOCATION = "Location"
        private const val WIFION = "ParcTE"
        private const val WIFIOFF = "Wi-Fi"
        private const val MOBILEDATA = "Mobile\ndata"
        private const val BLUETOOTH = "Bluetooth"
        private const val BATTERYSAVER = "Power\nmode"
        private const val AUTOROTATEON = "Auto\nrotate"
        private const val AUTOROTATEOFF = "Portrait"
        private const val DONOTDISTURB = "Do not\ndisturb"
        private const val SENSORSOFFSTATUS = "sensors"
        private const val LOCATIONSTATUS = "location"
        private const val WIFISTATUS = "wifi"
        private const val MOBILEDATASTATUS = "mobiledata"
        private const val BLUETOOTHSTATUS = "bluetooth"
        private const val BATTERYSAVERSTATUS = "batterysaver"
        private const val AUTOROTATESTATUS = "autorotate"
        private const val DONOTDISTURBSTATUS = "donotdisturb"

        @BeforeClass @JvmStatic
        @Throws(UiObjectNotFoundException::class)
        fun setUpSensorConditions() {
            val mDevice: UiDevice
            val sensorEnabled: Boolean
            val locationEnabled: Boolean
            val wifiEnabled: Boolean
            val mobileDataEnabled: Boolean
            val bluetoothEnabled: Boolean
            val batterySaverEnabled: Boolean
            val autoRotateEnabled: Boolean
            val doNotDisturbEnabled: Boolean
            val UIObjectSensorsOff: UiObject
            var UIObjectLocation: UiObject
            var UIObjectWifi: UiObject?
            var UIObjectMobiledata: UiObject
            var UIObjectBluetooth: UiObject
            var UIObjectBatterySaver: UiObject
            var UIObjectAutoRotate: UiObject
            var UIObjectDoNotDisturb: UiObject
            var lastSensorStatus: Boolean
            var lastLocationStatus: Boolean
            var lastWifiStatus: Boolean
            var lastMobileDataStatus: Boolean
            var lastBluetoothStatus: Boolean
            var lastBatterySaverStatus: Boolean
            var lastAutoRotateStatus: Boolean
            var lastDoNotDisturbStatus: Boolean
            lastStatus = LastStatus()
            deserializeStatus()
            sensorEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("sensorEnabled"))
            locationEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("locationEnabled"))
            wifiEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("wifiEnabled"))
            mobileDataEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("mobiledataEnabled"))
            bluetoothEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("bluetoothEnabled"))
            batterySaverEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("batterysaverEnabled"))
            autoRotateEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("autoRotateEnabled"))
            doNotDisturbEnabled = java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("doNotDisturbEnabled"))

            mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            mDevice.wait(Until.hasObject(By.pkg(launcherPackageName).depth(0)), 1000)

            /*
        if (lastStatus.amountStatusItens() > 0) {
            lastSensorStatus = lastStatus.getStatus(SENSORSOFFSTATUS);
            lastLocationStatus = lastStatus.getStatus(LOCATIONSTATUS);
            lastWifiStatus = lastStatus.getStatus(WIFISTATUS);
            lastMobileDataStatus = lastStatus.getStatus(MOBILEDATASTATUS);
            lastBluetoothStatus = lastStatus.getStatus(BLUETOOTHSTATUS);
            lastBatterySaverStatus = lastStatus.getStatus(BATTERYSAVERSTATUS);
        }
        else {
        */lastSensorStatus = verifySensors(mDevice)
            lastLocationStatus = verifyLocation()
            lastWifiStatus = verifyWifi()
            lastMobileDataStatus = verifyMobileData()
            lastBluetoothStatus = verifyBluetooth()
            lastBatterySaverStatus = verifyBatterySaver()
            lastAutoRotateStatus = verifyAutoRotate()
            lastDoNotDisturbStatus = verifyDoNotDisturb()
            lastStatus!!.addStatus(SENSORSOFFSTATUS, lastSensorStatus)
            lastStatus!!.addStatus(LOCATIONSTATUS, lastLocationStatus)
            lastStatus!!.addStatus(WIFISTATUS, lastWifiStatus)
            lastStatus!!.addStatus(MOBILEDATASTATUS, lastMobileDataStatus)
            lastStatus!!.addStatus(BLUETOOTHSTATUS, lastBluetoothStatus)
            lastStatus!!.addStatus(BATTERYSAVERSTATUS, lastBatterySaverStatus)
            lastStatus!!.addStatus(AUTOROTATESTATUS, lastAutoRotateStatus)
            lastStatus!!.addStatus(DONOTDISTURBSTATUS, lastDoNotDisturbStatus)
            //}

            //Sensors
            if (sensorEnabled) {
                if (!lastSensorStatus) { //Desligado: SensorOff Inativo
                    mDevice.openQuickSettings()
                    UIObjectSensorsOff = mDevice.findObject(UiSelector().text(SENSORSOFF)) //For all sensors, except Radios
                    UIObjectSensorsOff.click()
                    lastSensorStatus = true
                    lastStatus!!.setCurrentStatus(SENSORSOFFSTATUS, lastSensorStatus)
                }
            } else {
                if (lastSensorStatus) { //Ligado: SensorOff Ativo
                    mDevice.openQuickSettings()
                    UIObjectSensorsOff = mDevice.findObject(UiSelector().text(SENSORSOFF)) //For all sensors, except Radios
                    UIObjectSensorsOff.click()
                    lastSensorStatus = false
                    lastStatus!!.setCurrentStatus(SENSORSOFFSTATUS, lastSensorStatus)
                }
            }

            //Location
            if (locationEnabled) {
                if (!lastLocationStatus) {
                    mDevice.openQuickSettings()
                    UIObjectLocation = mDevice.findObject(UiSelector().text(LOCATION)) //For Location (GPS)
                    UIObjectLocation.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectLocation = mDevice.findObject(UiSelector().text("Location, Off, Switch"))
                    UIObjectLocation.click()
                    UIObjectLocation = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectLocation.click()
                    //---------------------------------------------------------------------
                    lastLocationStatus = true
                    lastStatus!!.setCurrentStatus(LOCATIONSTATUS, lastLocationStatus)
                }
            } else {
                if (lastLocationStatus) {
                    mDevice.openQuickSettings()
                    UIObjectLocation = mDevice.findObject(UiSelector().text(LOCATION)) //For Location (GPS)
                    UIObjectLocation.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectLocation = mDevice.findObject(UiSelector().text("Location, On, Switch"))
                    UIObjectLocation.click()
                    UIObjectLocation = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectLocation.click()
                    //---------------------------------------------------------------------
                    lastLocationStatus = false
                    lastStatus!!.setCurrentStatus(LOCATIONSTATUS, lastLocationStatus)
                }
            }

            //Wi-Fi
            if (wifiEnabled) {
                if (!lastWifiStatus) {
                    mDevice.openQuickSettings()
                    UIObjectWifi = mDevice.findObject(UiSelector().text(WIFIOFF))
                    UIObjectWifi.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Wi-Fi, Off, Switch"))
                    UIObjectWifi.click()
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectWifi.click()
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Cancel"))
                    UIObjectWifi?.click()
                    //---------------------------------------------------------------------
                    lastWifiStatus = true
                    lastStatus!!.setCurrentStatus(WIFISTATUS, lastWifiStatus)
                }
            } else {
                if (lastWifiStatus) {
                    mDevice.openQuickSettings()
                    UIObjectWifi = mDevice.findObject(UiSelector().text(WIFION))
                    UIObjectWifi.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Wi-Fi, On, Switch"))
                    UIObjectWifi.click()
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectWifi.click()
                    //---------------------------------------------------------------------
                    lastWifiStatus = false
                    lastStatus!!.setCurrentStatus(WIFISTATUS, lastWifiStatus)
                }
            }

            //Mobile Data
            if (mobileDataEnabled) {
                if (!lastMobileDataStatus) {
                    mDevice.openQuickSettings()
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text(MOBILEDATA))
                    UIObjectMobiledata.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("Mobile data, Off, Switch"))
                    UIObjectMobiledata.click()
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectMobiledata.click()
                    //---------------------------------------------------------------------
                    lastMobileDataStatus = true
                    lastStatus!!.setCurrentStatus(MOBILEDATASTATUS, lastMobileDataStatus)
                }
            } else {
                if (lastMobileDataStatus) {
                    mDevice.openQuickSettings()
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text(MOBILEDATA))
                    UIObjectMobiledata.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("Mobile data, On, Switch"))
                    UIObjectMobiledata.click()
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectMobiledata.click()
                    //---------------------------------------------------------------------
                    lastMobileDataStatus = false
                    lastStatus!!.setCurrentStatus(MOBILEDATASTATUS, lastMobileDataStatus)
                }
            }

            //Bluetooth
            if (bluetoothEnabled) {
                if (!lastBluetoothStatus) {
                    mDevice.openQuickSettings()
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text(BLUETOOTH))
                    UIObjectBluetooth.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("Bluetooth, Off, Switch"))
                    UIObjectBluetooth.click()
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectBluetooth.click()
                    //---------------------------------------------------------------------
                    lastBluetoothStatus = true
                    lastStatus!!.setCurrentStatus(BLUETOOTHSTATUS, lastBluetoothStatus)
                }
            } else {
                if (lastBluetoothStatus) {
                    mDevice.openQuickSettings()
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text(BLUETOOTH))
                    UIObjectBluetooth.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("Bluetooth, On, Switch"))
                    UIObjectBluetooth.click()
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectBluetooth.click()
                    //---------------------------------------------------------------------
                    lastBluetoothStatus = false
                    lastStatus!!.setCurrentStatus(BLUETOOTHSTATUS, lastBluetoothStatus)
                }
            }

            //Battery Saver
            if (batterySaverEnabled) {
                if (!lastBatterySaverStatus) {
                    mDevice.openQuickSettings()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text(BATTERYSAVER))
                    UIObjectBatterySaver.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Medium power saving"))
                    UIObjectBatterySaver.click()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Apply"))
                    UIObjectBatterySaver.click()
                    //---------------------------------------------------------------------
                    lastBatterySaverStatus = true
                    lastStatus!!.setCurrentStatus(BATTERYSAVERSTATUS, lastBatterySaverStatus)
                }
            } else {
                if (lastBatterySaverStatus) {
                    mDevice.openQuickSettings()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text(BATTERYSAVER))
                    UIObjectBatterySaver.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Optimized"))
                    UIObjectBatterySaver.click()
                    //---------------------------------------------------------------------
                    lastBatterySaverStatus = false
                    lastStatus!!.setCurrentStatus(BATTERYSAVERSTATUS, lastBatterySaverStatus)
                }
            }

            //Auto Rotate
            if (autoRotateEnabled) {
                if (!lastAutoRotateStatus) {
                    mDevice.openQuickSettings()
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text(AUTOROTATEOFF))
                    UIObjectAutoRotate.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("Auto rotate, Off, Switch"))
                    UIObjectAutoRotate.click()
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectAutoRotate.click()
                    //---------------------------------------------------------------------
                    lastAutoRotateStatus = true
                    lastStatus!!.setCurrentStatus(AUTOROTATESTATUS, lastAutoRotateStatus)
                }
            } else {
                if (lastAutoRotateStatus) {
                    mDevice.openQuickSettings()
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text(AUTOROTATEON))
                    UIObjectAutoRotate.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("Auto rotate, On, Switch"))
                    UIObjectAutoRotate.click()
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectAutoRotate.click()
                    //---------------------------------------------------------------------
                    lastAutoRotateStatus = false
                    lastStatus!!.setCurrentStatus(AUTOROTATESTATUS, lastAutoRotateStatus)
                }
            }

            //Do Not Disturb
            if (doNotDisturbEnabled) {
                if (!lastDoNotDisturbStatus) {
                    mDevice.openQuickSettings()
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text(DONOTDISTURB))
                    UIObjectDoNotDisturb.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("Do not disturb, Off, Switch"))
                    UIObjectDoNotDisturb.click()
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectDoNotDisturb.click()
                    //---------------------------------------------------------------------
                    lastDoNotDisturbStatus = true
                    lastStatus!!.setCurrentStatus(DONOTDISTURBSTATUS, lastDoNotDisturbStatus)
                }
            } else {
                if (lastDoNotDisturbStatus) {
                    mDevice.openQuickSettings()
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text(DONOTDISTURB))
                    UIObjectDoNotDisturb.click()
                    //For Galaxy M30 -----------------------------------------------------
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("Do not disturb, On, Switch"))
                    UIObjectDoNotDisturb.click()
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectDoNotDisturb.click()
                    //---------------------------------------------------------------------
                    lastDoNotDisturbStatus = false
                    lastStatus!!.setCurrentStatus(DONOTDISTURBSTATUS, lastDoNotDisturbStatus)
                }
            }
            serializeStatus()
            mDevice.pressHome()
        }

        private val launcherPackageName: String
            private get() {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                val context = ApplicationProvider.getApplicationContext<Context>()
                val pm = context.packageManager
                val resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                return resolveInfo!!.activityInfo.packageName
            }

        private fun serializeStatus() {
            val filename = "statusFile.ser"

            // Serialization
            try { //Saving of object in a file
                //FileOutputStream file = new FileOutputStream(filename);
                val ctx = ApplicationProvider.getApplicationContext<Context>()
                val file = ctx.openFileOutput(filename, Context.MODE_PRIVATE)
                val out = ObjectOutputStream(file)

                // Method for serialization of object
                out.writeObject(lastStatus)
                out.close()
                file.close()
                println("Object has been serialized")
            } catch (ex: IOException) {
                println("There is a problem with the object serialization")
                ex.printStackTrace()
            }
        }

        private fun deserializeStatus() {
            val filename = "statusFile.ser"

            // Deserialization
            try {
                // Reading the object from a file
                //FileInputStream file = new FileInputStream(filename);
                val ctx = ApplicationProvider.getApplicationContext<Context>()
                val file = ctx.openFileInput(filename)
                val `in` = ObjectInputStream(file)

                // Method for deserialization of object
                lastStatus = `in`.readObject() as LastStatus
                `in`.close()
                file.close()
                println("Object has been deserialized")
            } catch (ex: IOException) {
                println("There is a problem with the object deserialization")
                ex.printStackTrace()
            } catch (ex: ClassNotFoundException) {
                println("There is a problem with the object deserialization")
                ex.printStackTrace()
            }
        }

        private fun verifySensors(mDevice: UiDevice): Boolean {
            var res = false

            //UiObject UIObjectCamera = mDevice.findObject(new UiSelector().description("Camera"));

            //For Galaxy M30
            val UIObjectCamera = mDevice.findObject(UiSelector().text("Camera"))
            try {
                UIObjectCamera.click()
                val UIObjectPhoto = mDevice.findObject(UiSelector().text("PHOTO"))
                if (UIObjectPhoto.exists()) res = true
            } catch (e: UiObjectNotFoundException) {
                e.printStackTrace()
            }


            /*
        UiObject UIObjectDismissButton = mDevice.findObject(new UiSelector().text("DISMISS"));

        try {
            UIObjectDismissButton.click();
            res = false;
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        */mDevice.pressHome()
            return res
        }

        private fun verifyLocation(): Boolean {
            val service = InstrumentationRegistry.getInstrumentation().context
                    .getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        private fun verifyWifi(): Boolean {
            val service = InstrumentationRegistry.getInstrumentation().context
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
            return service.isWifiEnabled
        }

        /*
    private static boolean verifyMobileData() {
        boolean res = false;
        ConnectivityManager connectivityMgr = (ConnectivityManager) InstrumentationRegistry.getInstrumentation().getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);


        if (connectivityMgr != null) {
            NetworkCapabilities nc = connectivityMgr.getNetworkCapabilities(connectivityMgr.getActiveNetwork());

            if (nc != null) {
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    // connected to mobile data
                    res = true;
                }
            }
            else
                res = false;
        }
        else
            res = false;

        return res;
    }
    */
        private fun verifyMobileData(): Boolean {
            var mobileYN = false
            val context = InstrumentationRegistry.getInstrumentation().context
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            if (tm.simState == TelephonyManager.SIM_STATE_READY) {
                val tel = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                if (tel.isDataEnabled) {
                    mobileYN = true
                }
            }
            return mobileYN
        }

        private fun verifyBluetooth(): Boolean {
            val btMgr = InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            return btMgr.adapter.isEnabled
        }

        private fun verifyBatterySaver(): Boolean {
            val pwMgr = InstrumentationRegistry.getInstrumentation().context
                    .getSystemService(Context.POWER_SERVICE) as PowerManager
            return pwMgr.isPowerSaveMode
        }

        private fun verifyAutoRotate(): Boolean {
            return Settings.System.getInt(InstrumentationRegistry.getInstrumentation().context.contentResolver, Settings.System.ACCELEROMETER_ROTATION, 0) == 1
        }

        private fun verifyDoNotDisturb(): Boolean {
            val notifManager = InstrumentationRegistry.getInstrumentation().context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Sem bloqueio: INTERRUPTION_FILTER_ALL
            //Algum bloqueio: INTERRUPTION_FILTER_PRIORITY
            //Bloqueio total: INTERRUPTION_FILTER_NONE
            return notifManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
        }
    }
}