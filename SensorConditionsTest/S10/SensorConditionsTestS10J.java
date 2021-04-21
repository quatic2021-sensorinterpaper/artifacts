package com.github.pockethub.android.tests;

import android.Manifest;
import android.app.NotificationManager;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.javatuples.Pair;
import org.junit.BeforeClass;
import org.junit.Rule;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

public class SensorConditionsTestS10J {
    private static ArrayList<Boolean> allStatus;
    private static Pair<String,Boolean> status;
    private static LastStatusJ lastStatus;

    private static final String SENSORSOFF = "Sensors Off";
    private static final String LOCATION = "Location";
    private static final String WIFION = "ParcTE";
    private static final String WIFIOFF = "Wi-Fi";
    private static final String MOBILEDATA = "Mobile\ndata";
    private static final String BLUETOOTH = "Bluetooth";
    private static final String BATTERYSAVER = "Power saving\nmode";
    private static final String AUTOROTATEON = "Auto\nrotate";
    private static  final String AUTOROTATEOFF = "Portrait";
    private static final String DONOTDISTURB = "Do not\ndisturb";

	/*
	autorotateoff (Auto\nrotate) = Auto rotate, Off, Switch
	autorotateon (Portrait) = Auto rotate, On, Switch
	donotdisturboff (Do not\ndisturb) = Do not disturb, Off, Switch
	donotdisturbon (Do not\ndisturb) = Do not disturb, On, Switch
	*/

    private static final String SENSORSOFFSTATUS = "sensors";
    private static final String LOCATIONSTATUS = "location";
    private static final String WIFISTATUS = "wifi";
    private static final String MOBILEDATASTATUS = "mobiledata";
    private static final String BLUETOOTHSTATUS = "bluetooth";
    private static final String BATTERYSAVERSTATUS = "batterysaver";
    private static final String AUTOROTATESTATUS = "autorotate";
    private static final String DONOTDISTURBSTATUS = "donotdisturb";

    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE);

    @BeforeClass
    public static void setUpSensorConditions() throws UiObjectNotFoundException, InterruptedException {
        UiDevice mDevice;
        boolean sensorEnabled;
        boolean locationEnabled;
        boolean wifiEnabled;
        boolean mobileDataEnabled;
        boolean bluetoothEnabled;
        boolean batterySaverEnabled;
        boolean autoRotateEnabled;
        boolean doNotDisturbEnabled;
        UiObject UIObjectSensorsOff;
        UiObject UIObjectLocation;
        UiObject UIObjectWifi;
        UiObject UIObjectMobiledata;
        UiObject UIObjectBluetooth;
        UiObject UIObjectBatterySaver;
        UiObject UIObjectAutoRotate;
        UiObject UIObjectDoNotDisturb;
        boolean lastSensorStatus;
        boolean lastLocationStatus;
        boolean lastWifiStatus;
        boolean lastMobileDataStatus;
        boolean lastBluetoothStatus;
        boolean lastBatterySaverStatus;
        boolean lastAutoRotateStatus;
        boolean lastDoNotDisturbStatus;

        lastStatus = new LastStatusJ();
        deserializeStatus();

        sensorEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("sensorEnabled"));
        locationEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("locationEnabled"));
        wifiEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("wifiEnabled"));
        mobileDataEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("mobiledataEnabled"));
        bluetoothEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("bluetoothEnabled"));
        batterySaverEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("batterysaverEnabled"));
        autoRotateEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("autoRotateEnabled"));
        doNotDisturbEnabled = Boolean.parseBoolean(InstrumentationRegistry.getArguments().getString("doNotDisturbEnabled"));

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.wait(Until.hasObject(By.pkg(getLauncherPackageName()).depth(0)), 1000);

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
        */

            lastSensorStatus = verifySensors(mDevice);
            lastLocationStatus = verifyLocation();
            lastWifiStatus = verifyWifi();
            lastMobileDataStatus = verifyMobileData();
            lastBluetoothStatus = verifyBluetooth();
            lastBatterySaverStatus = verifyBatterySaver();
            lastAutoRotateStatus = verifyAutoRotate();
            lastDoNotDisturbStatus = verifyDoNotDisturb();
            lastStatus.addStatus(SENSORSOFFSTATUS,lastSensorStatus);
            lastStatus.addStatus(LOCATIONSTATUS,lastLocationStatus);
            lastStatus.addStatus(WIFISTATUS,lastWifiStatus);
            lastStatus.addStatus(MOBILEDATASTATUS,lastMobileDataStatus);
            lastStatus.addStatus(BLUETOOTHSTATUS,lastBluetoothStatus);
            lastStatus.addStatus(BATTERYSAVERSTATUS,lastBatterySaverStatus);
            lastStatus.addStatus(AUTOROTATESTATUS,lastAutoRotateStatus);
            lastStatus.addStatus(DONOTDISTURBSTATUS,lastDoNotDisturbStatus);
        //}

        //Sensors
        if (sensorEnabled) {
            if (!lastSensorStatus) { //Desligado: SensorOff Inativo
                mDevice.openQuickSettings();
                UIObjectSensorsOff = mDevice.findObject(new UiSelector().text(SENSORSOFF)); //For all sensors, except Radios
                UIObjectSensorsOff.click();
                lastSensorStatus = true;
                lastStatus.setCurrentStatus(SENSORSOFFSTATUS,lastSensorStatus);
            }
        } else {
            if (lastSensorStatus) { //Ligado: SensorOff Ativo
                mDevice.openQuickSettings();
                UIObjectSensorsOff = mDevice.findObject(new UiSelector().text(SENSORSOFF)); //For all sensors, except Radios
                UIObjectSensorsOff.click();
                lastSensorStatus = false;
                lastStatus.setCurrentStatus(SENSORSOFFSTATUS,lastSensorStatus);
            }
        }

        //Location
        if (locationEnabled) {
            if (!lastLocationStatus) {
                mDevice.openQuickSettings();
                UIObjectLocation = mDevice.findObject(new UiSelector().text(LOCATION)); //For Location (GPS)
                UIObjectLocation.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectLocation = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectLocation.click();
                UIObjectLocation = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectLocation.click();
                //---------------------------------------------------------------------
                lastLocationStatus = true;
                lastStatus.setCurrentStatus(LOCATIONSTATUS,lastLocationStatus);
            }
        } else {
            if (lastLocationStatus) {
                mDevice.openQuickSettings();
                UIObjectLocation = mDevice.findObject(new UiSelector().text(LOCATION)); //For Location (GPS)
                UIObjectLocation.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectLocation = mDevice.findObject(new UiSelector().text("On"));
                UIObjectLocation.click();
                UIObjectLocation = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectLocation.click();
                //---------------------------------------------------------------------
                lastLocationStatus = false;
                lastStatus.setCurrentStatus(LOCATIONSTATUS,lastLocationStatus);
            }
        }

        //Wi-Fi
        if (wifiEnabled) {
            if (!lastWifiStatus) {
                mDevice.openQuickSettings();
                UIObjectWifi = mDevice.findObject(new UiSelector().text(WIFIOFF));
                UIObjectWifi.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectWifi = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectWifi.click();
                UIObjectWifi = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectWifi.click();
                UIObjectWifi = mDevice.findObject(new UiSelector().text("Cancel"));
                if (UIObjectWifi != null)
                    UIObjectWifi.click();
                //---------------------------------------------------------------------
                lastWifiStatus = true;
                lastStatus.setCurrentStatus(WIFISTATUS,lastWifiStatus);
            }
        } else {
            if (lastWifiStatus) {
                mDevice.openQuickSettings();
                UIObjectWifi = mDevice.findObject(new UiSelector().text(WIFION));
                UIObjectWifi.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectWifi = mDevice.findObject(new UiSelector().text("On"));
                UIObjectWifi.click();
                UIObjectWifi = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectWifi.click();
                //---------------------------------------------------------------------
                lastWifiStatus = false;
                lastStatus.setCurrentStatus(WIFISTATUS,lastWifiStatus);
            }
        }

        //Mobile Data
        if (mobileDataEnabled) {
            if (!lastMobileDataStatus) {
                mDevice.openQuickSettings();
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text(MOBILEDATA));
                UIObjectMobiledata.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectMobiledata.click();
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectMobiledata.click();
                //---------------------------------------------------------------------
                lastMobileDataStatus = true;
                lastStatus.setCurrentStatus(MOBILEDATASTATUS,lastMobileDataStatus);
            }
        } else {
            if (lastMobileDataStatus) {
                mDevice.openQuickSettings();
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text(MOBILEDATA));
                UIObjectMobiledata.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text("On"));
                UIObjectMobiledata.click();
                UIObjectMobiledata = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectMobiledata.click();
                //---------------------------------------------------------------------
                lastMobileDataStatus = false;
                lastStatus.setCurrentStatus(MOBILEDATASTATUS,lastMobileDataStatus);
            }
        }

        //Bluetooth
        if (bluetoothEnabled) {
            if (!lastBluetoothStatus) {
                mDevice.openQuickSettings();
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text(BLUETOOTH));
                UIObjectBluetooth.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectBluetooth.click();
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectBluetooth.click();
                //---------------------------------------------------------------------
                lastBluetoothStatus = true;
                lastStatus.setCurrentStatus(BLUETOOTHSTATUS,lastBluetoothStatus);
            }
        } else {
            if (lastBluetoothStatus) {
                mDevice.openQuickSettings();
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text(BLUETOOTH));
                UIObjectBluetooth.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text("On"));
                UIObjectBluetooth.click();
                UIObjectBluetooth = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectBluetooth.click();
                //---------------------------------------------------------------------
                lastBluetoothStatus = false;
                lastStatus.setCurrentStatus(BLUETOOTHSTATUS,lastBluetoothStatus);
            }
        }

        //Battery Saver
        if (batterySaverEnabled) {
            if (!lastBatterySaverStatus) {
                mDevice.openQuickSettings();
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text(BATTERYSAVER));
                UIObjectBatterySaver.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectBatterySaver.click();
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectBatterySaver.click();
                Thread.sleep(10000);
                //---------------------------------------------------------------------
                lastBatterySaverStatus = true;
                lastStatus.setCurrentStatus(BATTERYSAVERSTATUS,lastBatterySaverStatus);
            }
        } else {
            if (lastBatterySaverStatus) {
                mDevice.openQuickSettings();
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text(BATTERYSAVER));
                UIObjectBatterySaver.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text("On"));
                UIObjectBatterySaver.click();
                UIObjectBatterySaver = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectBatterySaver.click();
                Thread.sleep(10000);
                //---------------------------------------------------------------------
                lastBatterySaverStatus = false;
                lastStatus.setCurrentStatus(BATTERYSAVERSTATUS,lastBatterySaverStatus);
            }
        }

        //Auto Rotate
        if (autoRotateEnabled) {
            if (!lastAutoRotateStatus) {
                mDevice.openQuickSettings();
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text(AUTOROTATEOFF));
                UIObjectAutoRotate.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectAutoRotate.click();
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectAutoRotate.click();
                //---------------------------------------------------------------------
                lastAutoRotateStatus = true;
                lastStatus.setCurrentStatus(AUTOROTATESTATUS,lastAutoRotateStatus);
            }
        } else {
            if (lastAutoRotateStatus) {
                mDevice.openQuickSettings();
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text(AUTOROTATEON));
                UIObjectAutoRotate.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text("On"));
                UIObjectAutoRotate.click();
                UIObjectAutoRotate = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectAutoRotate.click();
                //---------------------------------------------------------------------
                lastAutoRotateStatus = false;
                lastStatus.setCurrentStatus(AUTOROTATESTATUS,lastAutoRotateStatus);
            }
        }

        //Do Not Disturb
        if (doNotDisturbEnabled) {
            if (!lastDoNotDisturbStatus) {
                mDevice.openQuickSettings();
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text(DONOTDISTURB));
                UIObjectDoNotDisturb.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text("Off"));
                UIObjectDoNotDisturb.click();
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectDoNotDisturb.click();
                //---------------------------------------------------------------------
                lastDoNotDisturbStatus = true;
                lastStatus.setCurrentStatus(DONOTDISTURBSTATUS,lastDoNotDisturbStatus);
            }
        } else {
            if (lastDoNotDisturbStatus) {
                mDevice.openQuickSettings();
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text(DONOTDISTURB));
                UIObjectDoNotDisturb.click();
                //For Galaxy S10 -----------------------------------------------------
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text("On"));
                UIObjectDoNotDisturb.click();
                UIObjectDoNotDisturb = mDevice.findObject(new UiSelector().text("Done"));
                UIObjectDoNotDisturb.click();
                //---------------------------------------------------------------------
                lastDoNotDisturbStatus = false;
                lastStatus.setCurrentStatus(DONOTDISTURBSTATUS,lastDoNotDisturbStatus);
            }
        }


        serializeStatus();

        mDevice.pressHome();

    }

    private static String getLauncherPackageName() {
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        Context context = ApplicationProvider.getApplicationContext();
        PackageManager pm = context.getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }

    private static void serializeStatus() {
        String filename = "statusFileJ.ser";

        // Serialization
        try
          {//Saving of object in a file
            //FileOutputStream file = new FileOutputStream(filename);
              Context ctx = ApplicationProvider.getApplicationContext();
            FileOutputStream file =  ctx.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(file);

            // Method for serialization of object
            out.writeObject(lastStatus);

            out.close();
            file.close();

            System.out.println("Object has been serialized");

        }
        catch(IOException ex)
        {
            System.out.println("There is a problem with the object serialization");
            ex.printStackTrace();
        }
    }

    private static void deserializeStatus() {
        String filename = "statusFileJ.ser";

        // Deserialization
        try
        {
            // Reading the object from a file
            //FileInputStream file = new FileInputStream(filename);
            Context ctx = ApplicationProvider.getApplicationContext();
            FileInputStream file = ctx.openFileInput(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            lastStatus = (LastStatusJ)in.readObject();

            in.close();
            file.close();

            System.out.println("Object has been deserialized");
        }

        catch(IOException | ClassNotFoundException ex)
        {
            System.out.println("There is a problem with the object deserialization");
            ex.printStackTrace();
        }
    }

    private static boolean verifySensors(UiDevice mDevice) {
        boolean res = false;

        //UiObject UIObjectCamera = mDevice.findObject(new UiSelector().description("Camera"));

        //For Galaxy S10
        UiObject UIObjectCamera = mDevice.findObject(new UiSelector().text("Camera"));

        try {
            UIObjectCamera.click();

            UiObject UIObjectPhoto = mDevice.findObject(new UiSelector().text("Go to Settings"));

            if (UIObjectPhoto.exists())
                res = true;

        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }


        /*
        UiObject UIObjectDismissButton = mDevice.findObject(new UiSelector().text("DISMISS"));

        try {
            UIObjectDismissButton.click();
            res = false;
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }
        */

        mDevice.pressHome();
        return res;
    }

    private static boolean verifyLocation() {
        LocationManager service = (LocationManager) InstrumentationRegistry.getInstrumentation().getContext()
                .getSystemService(Context.LOCATION_SERVICE);

        return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static boolean verifyWifi() {
        WifiManager service = (WifiManager) InstrumentationRegistry.getInstrumentation().getContext()
                .getSystemService(Context.WIFI_SERVICE);

        return service.isWifiEnabled();
    }

    private static boolean verifyMobileData(){
        boolean mobileYN = false;
        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
            TelephonyManager tel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            if(tel.isDataEnabled()){
                mobileYN = true;
            }

        }

        return mobileYN;
    }

    private static boolean verifyBluetooth() {
        BluetoothManager btMgr = (BluetoothManager) InstrumentationRegistry.getInstrumentation().getTargetContext().
                getSystemService(Context.BLUETOOTH_SERVICE);

        return btMgr.getAdapter().isEnabled();
    }

    private static boolean verifyBatterySaver() {
        PowerManager pwMgr = (PowerManager) InstrumentationRegistry.getInstrumentation().getContext()
                .getSystemService(Context.POWER_SERVICE);

        return pwMgr.isPowerSaveMode();
    }

    private static boolean verifyAutoRotate() {
        return Settings.System.getInt(InstrumentationRegistry.getInstrumentation().getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, 0) == 1;
    }

    private static boolean verifyDoNotDisturb() {
        NotificationManager notifManager = (NotificationManager) InstrumentationRegistry.getInstrumentation().getContext().
            getSystemService(Context.NOTIFICATION_SERVICE);

        boolean res = notifManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY;

        //Sem bloqueio: INTERRUPTION_FILTER_ALL
        //Algum bloqueio: INTERRUPTION_FILTER_PRIORITY
        //Bloqueio total: INTERRUPTION_FILTER_NONE
        return res;
    }

}
