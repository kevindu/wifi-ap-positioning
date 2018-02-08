package com.example.jiuzhou.fingerprintcollector;

import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.util.Log;

import com.example.jiuzhou.fingerprintcollector.localizer.Detector;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.List;

public class StartButtonTask extends AsyncTask<String, Void, String> {
    //update
    //final String AP2_2_MAC = "10:0d:7f:47:39:87";
   // final String AP2_3_MAC = "10:0d:7f:4b:64:06";
    //final String AP2_4_MAC = "a0:f3:c1:5b:bb:a4";
    //final String AP2_5_MAC = "00:23:cd:05:45:0e";
    //final String AP2_6_MAC = "00:23:cd:05:45:14";
    //final String AP2_7_MAC = "00:23:cd:05:45:2c";

    final String AP2_1_MAC = "C0:4A:00:51:27:B8";
    final String AP2_2_MAC = "C0:4A:00:51:15:5E";
    final String AP2_3_MAC = "10:0d:7f:4b:64:06";
    final String AP2_4_MAC = "A0:F3:C1:5B:BB:A4";
    final String AP2_5_MAC = "64:70:02:40:A7:4C";
    final String AP2_6_MAC = "C0:4A:00:51:0A:FE";
    final String AP2_7_MAC = "C0:4A:00:5D:A7:9C";
    final String AP2_8_MAC = "C0:4A:00:51:11:4E";

    //BSSID detected by UE in non-capital letters
    final String AP1_1_MAC = "c0:4a:00:51:27:b8";
    final String AP1_2_MAC = "c2:4a:00:51:15:5e";
    final String AP1_3_MAC = "10:0d:7f:4b:64:06";
    final String AP1_4_MAC = "a2:f3:c1:5b:bb:a4";
    final String AP1_5_MAC = "66:70:02:40:a7:4c";
    final String AP1_6_MAC = "c2:4a:00:51:0a:fe";
    final String AP1_7_MAC = "c2:4a:00:5d:a7:9c";
    final String AP1_8_MAC = "c2:4a:00:51:11:4e";

    protected String doInBackground(String... params) {
        try {
            MainActivity.AP2_1_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_1_MAC);
            MainActivity.AP2_2_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_2_MAC);
            MainActivity.AP2_3_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_3_MAC);
            MainActivity.AP2_4_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_4_MAC);
            MainActivity.AP2_5_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_5_MAC);
            MainActivity.AP2_6_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_6_MAC);
            MainActivity.AP2_7_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_7_MAC);
            MainActivity.AP2_8_rss += MainActivity.db.getRSS(MainActivity.MAC, AP2_8_MAC);

            int[] APscani = new int[8];
            for(int i=0; i<8; i++) {
                APscani[i] = -100;
            }

            if(MainActivity.scanResults != null) {
                for (ScanResult APi : MainActivity.scanResults) {
//                    Log.i("debug", "" + APi.BSSID + APi.SSID);
                    if (APi.BSSID.equals(AP1_1_MAC)) {
                        APscani[0] = APi.level;
//                        Log.i("debug", "AP1 mac: " + APi.BSSID + ", level: " + APi.level);
                    }
                    if (APi.BSSID.equals(AP1_2_MAC))
                        APscani[1] = APi.level;
//                Log.i("debug", "AP2 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_3_MAC))
                        APscani[2] = APi.level;
//                Log.i("debug", "AP3 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_4_MAC))
                        APscani[3] = APi.level;
//                Log.i("debug", "AP4 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_5_MAC))
                        APscani[4] = APi.level;
//                Log.i("debug", "AP5 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_6_MAC))
                        APscani[5] = APi.level;
//                Log.i("debug", "AP6 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_7_MAC))
                        APscani[6] = APi.level;
//                Log.i("debug", "AP7 mac: "+APi.BSSID + ", level: "+APi.level);}
                    if (APi.BSSID.equals(AP1_8_MAC))
                        APscani[7] = APi.level;
//                Log.i("debug", "AP8 mac: "+APi.BSSID + ", level: "+APi.level);}
                }
            }

            MainActivity.AP1_1_rss += APscani[0];
            MainActivity.AP1_2_rss += APscani[1];
            MainActivity.AP1_3_rss += APscani[2];
            MainActivity.AP1_4_rss += APscani[3];
            MainActivity.AP1_5_rss += APscani[4];
            MainActivity.AP1_6_rss += APscani[5];
            MainActivity.AP1_7_rss += APscani[6];
            MainActivity.AP1_8_rss += APscani[7];

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    //This Method is called when Network-Request finished
    protected void onPostExecute(String serverData) {

    }
}
