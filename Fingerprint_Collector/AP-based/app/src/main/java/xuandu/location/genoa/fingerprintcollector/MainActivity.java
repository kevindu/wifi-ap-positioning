package xuandu.location.genoa.fingerprintcollector;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import xuandu.location.genoa.fingerprintcollector.localizer.Detector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import xuandu.location.genoa.fingerprintcollector.map.SystemConfig;
import xuandu.location.genoa.fingerprintcollector.radiomap.ObtainRSSbyAPTask;
import xuandu.location.genoa.fingerprintcollector.radiomap.RSSObservation;
import xuandu.location.genoa.probabilisticpositioning.ProbabilisticPositioning;
import xuandu.location.indoormapapi.mapping.views.Graphic;
import xuandu.location.indoormapapi.mapping.views.ItemizedOverlay;
import xuandu.location.indoormapapi.mapping.views.MapView;

public class MainActivity extends AppCompatActivity {

    static boolean collecting = false;
    static long collectStartTime;
    public boolean isTimerRunning = false;

    /*
     * Map related objects
     */
    private MapView mMapView = null; // 地图View

    private ItemizedOverlay RPsOverlay = null;

    private ArrayList<Graphic> RPsGraphic = new ArrayList<>();

    public TextView tv_MAC;
    public static String MAC;

    public static List<String> APsID = new ArrayList<>();
    public static List<String> APsBSSID = new ArrayList<>();

    // MAC address as ID in database
    final String AP_1_MAC = "c0:4a:00:51:27:b8";
    final String AP_2_MAC = "c0:4a:00:51:15:5e";
    final String AP_3_MAC = "c2:4a:00:5d:a7:9c";
    final String AP_4_MAC = "a0:f3:c1:5b:bb:a4";
    final String AP_5_MAC = "64:70:02:40:a7:4c";
    final String AP_6_MAC = "c0:4a:00:51:0a:fe";
    final String AP_7_MAC = "c0:4a:00:5d:a7:9c";
    final String AP_8_MAC = "c0:4a:00:51:11:4e";

    // MAC address seen by UE
    final String AP_1_MAC_BSSID = "c0:4a:00:51:27:b8";
    final String AP_2_MAC_BSSID = "c2:4a:00:51:15:5e";
    final String AP_3_MAC_BSSID = "c2:4a:00:5d:a7:9c";
    final String AP_4_MAC_BSSID = "a2:f3:c1:5b:bb:a4";
    final String AP_5_MAC_BSSID = "66:70:02:40:a7:4c";
    final String AP_6_MAC_BSSID = "c2:4a:00:51:0a:fe";
    final String AP_7_MAC_BSSID = "c2:4a:00:5d:a7:9c";
    final String AP_8_MAC_BSSID = "c2:4a:00:51:11:4e";

    public TextView tv_AP_1_rss;
    public TextView tv_AP_2_rss;
    public TextView tv_AP_3_rss;
    public TextView tv_AP_4_rss;
    public TextView tv_AP_5_rss;
    public TextView tv_AP_6_rss;
    public TextView tv_AP_7_rss;
    public TextView tv_AP_8_rss;

    public TextView tv_timer;

    public Button bt_start;
    public Button bt_insert;

    public static MyIPSDatabase db;
    public Timer timer = new Timer();
    public TimerTask timerTask;

    public static Detector localAPscanner = null;

    public static ProbabilisticPositioning myPositioning;

    public static int x;
    public static int y;

    public static WifiManager wifiManager;

    BatteryManager batteryManager;
    String DIRECTORY_TO_POWERLOG = null;
    List<Integer> currentLog = new ArrayList<>();

    // RSS observations: mac address of AP, RSSObservation
    HashMap<String, RSSObservation> rssObservationsByUE;
    HashMap<String, RSSObservation> rssObservationsByAP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 111;
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},  PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        }

        mMapView = (MapView) findViewById(R.id.IMapView);
        // set initial floor of map view
        mMapView.getMapViewController().setFloor(6);

        // Modify this string with your local path
        final String PATH = SystemConfig.getpositionDataDir();

        DIRECTORY_TO_POWERLOG = PATH;

        // Instantiate the object ProbabilisticPositioning
        myPositioning = new ProbabilisticPositioning(this, PATH);
        try {
            myPositioning.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add the APs' mac address and BSSID to list
        APsID.add(0, AP_1_MAC);
        APsID.add(1, AP_2_MAC);
        APsID.add(2, AP_3_MAC);
        APsID.add(3, AP_4_MAC);
        APsID.add(4, AP_5_MAC);
        APsID.add(5, AP_6_MAC);
        APsID.add(6, AP_7_MAC);
        APsID.add(7, AP_8_MAC);

        APsBSSID.add(0, AP_1_MAC_BSSID);
        APsBSSID.add(1, AP_2_MAC_BSSID);
        APsBSSID.add(2, AP_3_MAC_BSSID);
        APsBSSID.add(3, AP_4_MAC_BSSID);
        APsBSSID.add(4, AP_5_MAC_BSSID);
        APsBSSID.add(5, AP_6_MAC_BSSID);
        APsBSSID.add(6, AP_7_MAC_BSSID);
        APsBSSID.add(7, AP_8_MAC_BSSID);

//        localAPscanner = new Detector(this.getApplicationContext());

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        batteryManager = (BatteryManager)this.getApplicationContext().getSystemService(Context.BATTERY_SERVICE);

        MAC = getMacAddr();

        tv_MAC = (TextView) findViewById(R.id.textView2);
        tv_MAC.setText(MAC);

        bt_start = (Button) findViewById(R.id.button);
        bt_insert = (Button) findViewById(R.id.button2);

        tv_AP_1_rss = (TextView) findViewById(R.id.lb_AP2_1_rss);
        tv_AP_2_rss = (TextView) findViewById(R.id.lb_AP2_2_rss);
        tv_AP_3_rss = (TextView) findViewById(R.id.lb_AP2_3_rss);
        tv_AP_4_rss = (TextView) findViewById(R.id.lb_AP2_4_rss);
        tv_AP_5_rss = (TextView) findViewById(R.id.lb_AP2_5_rss);
        tv_AP_6_rss = (TextView) findViewById(R.id.lb_AP2_6_rss);
        tv_AP_7_rss = (TextView) findViewById(R.id.lb_AP2_7_rss);
        tv_AP_8_rss = (TextView) findViewById(R.id.lb_AP2_8_rss);

        tv_timer = (TextView) findViewById(R.id.lb_timer);

        IntentFilter intentScanAvailable = new IntentFilter();
        intentScanAvailable.addAction (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(new BroadcastReceiver(){
            public void onReceive(Context c, Intent i) {
                // Code to execute when SCAN_RESULTS_AVAILABLE_ACTION event occurs
                if (collecting) {
                    try {
                        // savePowerLog the latest rss observed by UE
                        List<ScanResult> scanResults = wifiManager.getScanResults();
                        for (ScanResult each : scanResults) {
                            long timestamp = each.timestamp / 1000 - collectStartTime;
//                            Log.i("signal time ", +collectStartTime + " " + each.timestamp / 1000 + " ");
                            // check the timestamp of rss to make sure the rss is not old observed data
                            if (timestamp > 0) {
                                if (rssObservationsByUE.containsKey(each.BSSID)) {
                                    RSSObservation rssObservation = rssObservationsByUE.get(each.BSSID);
                                    // check the timestamp of rss to see make sure the rss is updated, rather than the old one.
                                    if (timestamp != rssObservation.getLatestTimestamp()) {
                                        rssObservation.addRSS(each.level, timestamp);
                                    }
                                }
                            }
                        }
                        // obtain the latest rss observed by AP
                        // execute at the same time as UE obtaining the rss, because AP can obtain updated rss only when UE sends the probe request.
                        ObtainRSSbyAPTask obtainRSSbyAPTask = new ObtainRSSbyAPTask();
                        AsyncTask<HashMap<String, RSSObservation>, Void, Void> execute = obtainRSSbyAPTask.execute(rssObservationsByAP);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    wifiManager.startScan();
                }
            }
        }, intentScanAvailable );

        // Now you can call this and it should execute the broadcastReceiver's onReceive()
        wifiManager.startScan();

    }

    public void startTimer() {
        isTimerRunning = true;
        currentLog = new ArrayList<>();
        timerTask = new TimerTask() {
            int i = 1;
            public void run() {
                MainActivity.this.runOnUiThread(() -> {
                    try {
                        tv_AP_1_rss.setText(rssObservationsByAP.get(AP_1_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_1_MAC_BSSID).getRSSAverage());
//                        Log.i("debug", rssObservationsByAP.get(AP_1_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_1_MAC_BSSID).getRSSAverage());
                        tv_AP_2_rss.setText(rssObservationsByAP.get(AP_2_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_2_MAC_BSSID).getRSSAverage());
                        tv_AP_3_rss.setText(rssObservationsByAP.get(AP_3_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_3_MAC_BSSID).getRSSAverage());
                        tv_AP_4_rss.setText(rssObservationsByAP.get(AP_4_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_4_MAC_BSSID).getRSSAverage());
                        tv_AP_5_rss.setText(rssObservationsByAP.get(AP_5_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_5_MAC_BSSID).getRSSAverage());
                        tv_AP_6_rss.setText(rssObservationsByAP.get(AP_6_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_6_MAC_BSSID).getRSSAverage());
                        tv_AP_7_rss.setText(rssObservationsByAP.get(AP_7_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_7_MAC_BSSID).getRSSAverage());
                        tv_AP_8_rss.setText(rssObservationsByAP.get(AP_8_MAC).getRSSAverage() + ", " + rssObservationsByUE.get(AP_8_MAC_BSSID).getRSSAverage());
                    }catch (NullPointerException e) {
                        e.printStackTrace();
                    }

                    tv_timer.setText(Integer.toString(i++));
                    if (i == 31) {
                        timer.cancel();
                        isTimerRunning = false;
                        tv_timer.setTextColor(Color.GREEN);

                        // stop collecting when exceed 30 seconds.
                        collecting = false;
                        // close the remote DB connection
                        DBConnection dbConnectionTask = new DBConnection();
                        dbConnectionTask.execute(false);
                        try {
                            savePowerLog();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    int energy = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    int batteryCurrent = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW);
                    Log.i("power consumption: ", "Remaining battery capacity = " + energy + " battery current = " + batteryCurrent + "mA ");
                    currentLog.add(batteryCurrent);
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void onClickStart(View v) {
        wifiManager.startScan();
        collecting = true;
        collectStartTime = SystemClock.elapsedRealtime();

        // initialise the HashMap to savePowerLog rss observations
        rssObservationsByUE = new HashMap<>();
        rssObservationsByAP = new HashMap<>();

        for (String AP_BSSID : APsBSSID) {
            rssObservationsByUE.put(AP_BSSID, new RSSObservation(AP_BSSID));
        }

        for (String AP_ID : APsID) {
            rssObservationsByAP.put(AP_ID, new RSSObservation(AP_ID));
        }

        // create the remote DB connection
        DBConnection dbConnectionTask = new DBConnection();
        dbConnectionTask.execute(true);

        tv_AP_1_rss.setText("0");
        tv_AP_2_rss.setText("0");
        tv_AP_3_rss.setText("0");
        tv_AP_4_rss.setText("0");
        tv_AP_5_rss.setText("0");
        tv_AP_6_rss.setText("0");
        tv_AP_7_rss.setText("0");
        tv_AP_8_rss.setText("0");

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
        try {
            // create the remote DB connection
            DBConnection dbConnectionTask = new DBConnection();
            dbConnectionTask.execute(true);

            InsertButtonTask BtnInsertTask = new InsertButtonTask();
            BtnInsertTask.execute(rssObservationsByAP, rssObservationsByUE);

            // close the remote DB connection
            dbConnectionTask = new DBConnection();
            dbConnectionTask.execute(false);

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

    public void setCollecting(Boolean collecting) {
        this.collecting = collecting;
    }

    public void setCollectStartTime(long collectStartTime) {
        this.collectStartTime = collectStartTime;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 111) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
        }
    }

    public void savePowerLog() throws IOException {
        System.out.print("Start writing the powerlog....");

        File pdata = new File(DIRECTORY_TO_POWERLOG);

        // Make sure the path directory exists.
        if(!pdata.exists())
        {
            // Make it, if it doesn't exit
            pdata.mkdirs();
        }

        File file = new File(DIRECTORY_TO_POWERLOG, "powerlog.txt");
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file));

        for (Integer current : currentLog) {
            outputStreamWriter.append(current + "\n");
        }
        outputStreamWriter.close();

        System.out.println("DONE!");
    }
}
