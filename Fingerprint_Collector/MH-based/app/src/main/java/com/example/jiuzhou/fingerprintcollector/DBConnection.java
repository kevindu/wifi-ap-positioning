package com.example.jiuzhou.fingerprintcollector;

import android.os.AsyncTask;

public class DBConnection  extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... params) {

        MainActivity.db = new MyIPSDatabase();
        return null;
    }
    //This Method is called when Network-Request finished
    protected void onPostExecute(String serverData) {
    }
}
