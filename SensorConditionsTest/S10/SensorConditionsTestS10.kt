package com

import android.Manifest
import android.app.NotificationManager
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.wifi.WifiManager
import android.os.PowerManager
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject
import androidx.test.uiautomator.UiObjectNotFoundException
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.javatuples.Pair
import org.junit.BeforeClass
import org.junit.Rule
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.ArrayList

open class SensorConditionsTestS10 {
    @get:Rule var mRuntimePermissionRule = GrantPermissionRule.grant(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

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
        private const val BATTERYSAVER = "Power saving\nmode"
        private const val AUTOROTATEON = "Auto\nrotate"
        private const val AUTOROTATEOFF = "Portrait"
        private const val DONOTDISTURB = "Do not\ndisturb"

        /*
	    autorotateoff (Auto\nrotate) = Auto rotate, Off, Switch
	    autorotateon (Portrait) = Auto rotate, On, Switch
	    donotdisturboff (Do not\ndisturb) = Do not disturb, Off, Switch
	    donotdisturbon (Do not\ndisturb) = Do not disturb, On, Switch
	    */

        private const val SENSORSOFFSTATUS = "sensors"
        private const val LOCATIONSTATUS = "location"
        private const val WIFISTATUS = "wifi"
        private const val MOBILEDATASTATUS = "mobiledata"
        private const val BLUETOOTHSTATUS = "bluetooth"
        private const val BATTERYSAVERSTATUS = "batterysaver"
        private const val AUTOROTATESTATUS = "autorotate"
        private const val DONOTDISTURBSTATUS = "donotdisturb"

        @BeforeClass @JvmStatic
        @Throws(UiObjectNotFoundException::class, InterruptedException::class)
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
            sensorEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("sensorEnabled"))
            locationEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("locationEnabled"))
            wifiEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("wifiEnabled"))
            mobileDataEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("mobiledataEnabled"))
            bluetoothEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("bluetoothEnabled"))
            batterySaverEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("batterysaverEnabled"))
            autoRotateEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("autoRotateEnabled"))
            doNotDisturbEnabled =
                java.lang.Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("doNotDisturbEnabled"))

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
        */  lastSensorStatus = verifySensors(mDevice)
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
                    UIObjectSensorsOff =
                        mDevice.findObject(UiSelector().text(SENSORSOFF)) //For all sensors, except Radios
                    UIObjectSensorsOff.click()
                    lastSensorStatus = true
                    lastStatus!!.setCurrentStatus(SENSORSOFFSTATUS, lastSensorStatus)
                }
            } else {
                if (lastSensorStatus) { //Ligado: SensorOff Ativo
                    mDevice.openQuickSettings()
                    UIObjectSensorsOff =
                        mDevice.findObject(UiSelector().text(SENSORSOFF)) //For all sensors, except Radios
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectLocation = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectLocation = mDevice.findObject(UiSelector().text("On"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectWifi = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectWifi = mDevice.findObject(UiSelector().text("On"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectMobiledata = mDevice.findObject(UiSelector().text("On"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectBluetooth = mDevice.findObject(UiSelector().text("On"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Off"))
                    UIObjectBatterySaver.click()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectBatterySaver.click()
                    Thread.sleep(10000)
                    //---------------------------------------------------------------------
                    lastBatterySaverStatus = true
                    lastStatus!!.setCurrentStatus(BATTERYSAVERSTATUS, lastBatterySaverStatus)
                }
            } else {
                if (lastBatterySaverStatus) {
                    mDevice.openQuickSettings()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text(BATTERYSAVER))
                    UIObjectBatterySaver.click()
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("On"))
                    UIObjectBatterySaver.click()
                    UIObjectBatterySaver = mDevice.findObject(UiSelector().text("Done"))
                    UIObjectBatterySaver.click()
                    Thread.sleep(10000)
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectAutoRotate = mDevice.findObject(UiSelector().text("On"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("Off"))
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
                    //For Galaxy S10 -----------------------------------------------------
                    UIObjectDoNotDisturb = mDevice.findObject(UiSelector().text("On"))
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
                return resolveInfo.activityInfo.packageName
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

            //For Galaxy S10
            val UIObjectCamera = mDevice.findObject(UiSelector().text("Camera"))
            try {
                UIObjectCamera.click()
                val UIObjectPhoto = mDevice.findObject(UiSelector().text("Go to Settings"))
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
            val btMgr =
                InstrumentationRegistry.getInstrumentation().targetContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            return btMgr.adapter.isEnabled
        }

        private fun verifyBatterySaver(): Boolean {
            val pwMgr = InstrumentationRegistry.getInstrumentation().context
                .getSystemService(Context.POWER_SERVICE) as PowerManager
            return pwMgr.isPowerSaveMode
        }

        private fun verifyAutoRotate(): Boolean {
            return Settings.System.getInt(
                InstrumentationRegistry.getInstrumentation().context.contentResolver,
                Settings.System.ACCELEROMETER_ROTATION,
                0
            ) === 1
        }

        private fun verifyDoNotDisturb(): Boolean {
            val notifManager =
                InstrumentationRegistry.getInstrumentation().context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            //Sem bloqueio: INTERRUPTION_FILTER_ALL
            //Algum bloqueio: INTERRUPTION_FILTER_PRIORITY
            //Bloqueio total: INTERRUPTION_FILTER_NONE
            return notifManager.currentInterruptionFilter == NotificationManager.INTERRUPTION_FILTER_PRIORITY
        }
    }
}
