package com.example.jiuzhou.fingerprintcollector;

import android.os.AsyncTask;

public class WiFiScan  extends AsyncTask<Void, Void, Void> {

    @Override
    protected Void doInBackground(Void... params) {
        MainActivity.wifiManager.startScan();
        return null;
    }

    protected void onPostExecute(Void result){
                new WiFiScan().execute();
        }
    }

