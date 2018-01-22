package com.example.jiuzhou.fingerprintcollector;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.jiuzhou.fingerprintcollector.localizer.Detector;

import java.net.NetworkInterface;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import xuandu.location.indoormapapi.dboverlay.GPSPoint;
import xuandu.location.indoormapapi.dboverlay.Location;
import xuandu.location.indoormapapi.dboverlay.PointD;
import xuandu.location.indoormapapi.mapping.views.GeoPoint;
import xuandu.location.indoormapapi.mapping.views.Geometry;
import xuandu.location.indoormapapi.mapping.views.Graphic;
import xuandu.location.indoormapapi.mapping.views.GraphicsOverlay;
import xuandu.location.indoormapapi.mapping.views.ItemizedOverlay;
import xuandu.location.indoormapapi.mapping.views.MapView;
import xuandu.location.indoormapapi.mapping.views.OverlayItem;
import xuandu.location.indoormapapi.mapping.views.PoiInfo;
import xuandu.location.indoormapapi.mapping.views.Symbol;

public class MainActivity extends AppCompatActivity {

    /*
     * Map related objects
     */
    private MapView mMapView = null; // 地图View

    private ItemizedOverlay RPsOverlay = null;

    private ArrayList<Graphic> RPsGraphic = new ArrayList<>();

    public TextView tv_MAC;
    public static String MAC;

    static int AP2_1_rss = 0;
    static int AP2_2_rss = 0;
    static int AP2_3_rss = 0;
    static int AP2_4_rss = 0;
    static int AP2_5_rss = 0;
    static int AP2_6_rss = 0;
    static int AP2_7_rss = 0;
    static int AP2_8_rss = 0;

    static int AP1_1_rss = 0;
    static int AP1_2_rss = 0;
    static int AP1_3_rss = 0;
    static int AP1_4_rss = 0;
    static int AP1_5_rss = 0;
    static int AP1_6_rss = 0;
    static int AP1_7_rss = 0;
    static int AP1_8_rss = 0;

    public TextView tv_AP2_1_rss;
    public TextView tv_AP2_2_rss;
    public TextView tv_AP2_3_rss;
    public TextView tv_AP2_4_rss;
    public TextView tv_AP2_5_rss;
    public TextView tv_AP2_6_rss;
    public TextView tv_AP2_7_rss;
    public TextView tv_AP2_8_rss;

    public TextView tv_timer;

    public EditText et_x;
    public EditText et_y;

    public Button bt_start;
    public Button bt_insert;

    public static MyIPSDatabase db;
    public Timer timer = new Timer();
    public TimerTask timerTask;
    public boolean isTimerRunning = false;

    public static Detector localAPscanner = null;

    static List<ScanResult> scanResults = null;

    public static int x;
    public static int y;
    public static int rss1;
    public static int rss2;
    public static int rss3;
    public static int rss4;
    public static int rss5;
    public static int rss6;
    public static int rss7;
    public static int rss8;

    public static int rss11;
    public static int rss12;
    public static int rss13;
    public static int rss14;
    public static int rss15;
    public static int rss16;
    public static int rss17;
    public static int rss18;

    public void initVariables() {
        AP2_1_rss = 0;
        AP2_2_rss = 0;
        AP2_3_rss = 0;
        AP2_4_rss = 0;
        AP2_5_rss = 0;
        AP2_6_rss = 0;
        AP2_7_rss = 0;
        AP2_8_rss = 0;

        AP1_1_rss = 0;
        AP1_2_rss = 0;
        AP1_3_rss = 0;
        AP1_4_rss = 0;
        AP1_5_rss = 0;
        AP1_6_rss = 0;
        AP1_7_rss = 0;
        AP1_8_rss = 0;

        tv_AP2_1_rss = (TextView) findViewById(R.id.lb_AP2_1_rss);
        tv_AP2_2_rss = (TextView) findViewById(R.id.lb_AP2_2_rss);
        tv_AP2_3_rss = (TextView) findViewById(R.id.lb_AP2_3_rss);
        tv_AP2_4_rss = (TextView) findViewById(R.id.lb_AP2_4_rss);
        tv_AP2_5_rss = (TextView) findViewById(R.id.lb_AP2_5_rss);
        tv_AP2_6_rss = (TextView) findViewById(R.id.lb_AP2_6_rss);
        tv_AP2_7_rss = (TextView) findViewById(R.id.lb_AP2_7_rss);
        tv_AP2_8_rss = (TextView) findViewById(R.id.lb_AP2_8_rss);

        tv_timer = (TextView) findViewById(R.id.lb_timer);

        tv_AP2_1_rss.setText("0");
        tv_AP2_2_rss.setText("0");
        tv_AP2_3_rss.setText("0");
        tv_AP2_4_rss.setText("0");
        tv_AP2_5_rss.setText("0");
        tv_AP2_6_rss.setText("0");
        tv_AP2_7_rss.setText("0");
        tv_AP2_8_rss.setText("0");
    }

    public void startTimer() {
        isTimerRunning = true;
        BtnStartTask = new StartButtonTask();
        timerTask = new TimerTask() {
            int i = 1;

            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        BtnStartTask = new StartButtonTask();
                        try {
                            String str_result = BtnStartTask.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        tv_AP2_1_rss.setText(Integer.toString(AP2_1_rss / i) + ", " + AP1_1_rss / i);
                        tv_AP2_2_rss.setText(Integer.toString(AP2_2_rss / i) + ", " + AP1_2_rss / i);
                        tv_AP2_3_rss.setText(Integer.toString(AP2_3_rss / i) + ", " + AP1_3_rss / i);
                        tv_AP2_4_rss.setText(Integer.toString(AP2_4_rss / i) + ", " + AP1_4_rss / i);
                        tv_AP2_5_rss.setText(Integer.toString(AP2_5_rss / i) + ", " + AP1_5_rss / i);
                        tv_AP2_6_rss.setText(Integer.toString(AP2_6_rss / i) + ", " + AP1_6_rss / i);
                        tv_AP2_7_rss.setText(Integer.toString(AP2_7_rss / i) + ", " + AP1_7_rss / i);
                        tv_AP2_8_rss.setText(Integer.toString(AP2_8_rss / i) + ", " + AP1_8_rss / i);

                        tv_timer.setText(Integer.toString(i++));
                        if (i == 31) {
                            rss11 = AP1_1_rss / 30;
                            rss12 = AP1_2_rss / 30;
                            rss13 = AP1_3_rss / 30;
                            rss14 = AP1_4_rss / 30;
                            rss15 = AP1_5_rss / 30;
                            rss16 = AP1_6_rss / 30;
                            rss17 = AP1_7_rss / 30;
                            rss18 = AP1_8_rss / 30;

                            rss1 = AP2_1_rss / 30;
                            rss2 = AP2_2_rss / 30;
                            rss3 = AP2_3_rss / 30;
                            rss4 = AP2_4_rss / 30;
                            rss5 = AP2_5_rss / 30;
                            rss6 = AP2_6_rss / 30;
                            rss7 = AP2_7_rss / 30;
                            rss8 = AP2_8_rss / 30;

                            timer.cancel();
                            isTimerRunning = false;
                            tv_timer.setTextColor(Color.GREEN);
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void onClickStart(View v) {
        initVariables();

        if (isTimerRunning) {
            timer.cancel();
            timer.purge();
            timer = new Timer();
            timerTask.cancel();
            startTimer();
        } else {
            tv_timer.setTextColor(Color.RED);
            timer = new Timer();
            startTimer();
        }
        bt_start.setText("Restart");

    }

    public void onClickInsert(View v) {

//        et_x = (EditText) findViewById(R.id.editText_x);
//        et_y = (EditText) findViewById(R.id.editText_y);
//        x = Integer.parseInt(et_x.getText().toString());
//        y = Integer.parseInt(et_y.getText().toString());

//        rss1 = Integer.parseInt(tv_AP2_1_rss.getText().toString());
//        rss2 = Integer.parseInt(tv_AP2_2_rss.getText().toString());
//        rss3 = Integer.parseInt(tv_AP2_3_rss.getText().toString());
//        rss4 = Integer.parseInt(tv_AP2_4_rss.getText().toString());
//        rss5 = Integer.parseInt(tv_AP2_5_rss.getText().toString());
//        rss6 = Integer.parseInt(tv_AP2_6_rss.getText().toString());
//        rss7 = Integer.parseInt(tv_AP2_7_rss.getText().toString());
//        rss8 = Integer.parseInt(tv_AP2_8_rss.getText().toString());

        try {

            BtnInsertTask = new InsertButtonTask();

            try {
                String str_result = BtnInsertTask.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("A new fingerprint has been successfully saved");
            builder1.setCancelable(true);
            builder1.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // local database

    }

    StartButtonTask BtnStartTask;
    InsertButtonTask BtnInsertTask;
    public static WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.IMapView);
        // set initial floor of map view
        mMapView.getMapViewController().setFloor(6);

        localAPscanner = new Detector(this.getApplicationContext());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        MAC = getMacAddr();

        tv_MAC = (TextView) findViewById(R.id.textView2);
        tv_MAC.setText(MAC);

        bt_start = (Button) findViewById(R.id.button);
        bt_insert = (Button) findViewById(R.id.button2);

    }

    @Override
    protected void onResume(){
        super.onResume();

        DBConnection dlTask = new DBConnection();
        dlTask.execute();

//        WiFiScan myWiFiScanTask = new WiFiScan();
//        myWiFiScanTask.execute();

        IntentFilter intentScanAvailable = new IntentFilter();
        intentScanAvailable.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(new BroadcastReceiver(){
            public void onReceive(Context c, Intent i){
                // Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
                scanResults = wifiManager.getScanResults();
                wifiManager.startScan();
            }
        }, intentScanAvailable );

        // Now you can call this and it should execute the broadcastReceiver's onReceive()
        wifiManager.startScan();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 111;
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},  PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }else{
            //do something, permission was previously granted; or legacy device
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
    }

    /**
     * Handle the physical key events
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {

            x = (int)mMapView.getCenterCoordinate().x;
            y = (int) mMapView.getCenterCoordinate().y;
            Log.i("centre coordinates", x + ", " + y);
            TextView currentLoc = (TextView) findViewById(R.id.textViewmap);
            currentLoc.setText(x + ", " + y);

//            Location loc = new Location();
//            loc.gps_loc = new GPSPoint(x,y);
//            loc.floor = mMapView.getFloor();
//            OverlayItem RPitem = new OverlayItem(PoiInfo.getPoiInfo(loc),"","");
//            RPsOverlay.addItem(RPitem);
//RPsOverlay.setBf(mMapView.getFloor().getId());
//            mMapView.refresh();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}
