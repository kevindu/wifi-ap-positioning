package com.example.jiuzhou.fingerprintcollector;

import android.os.AsyncTask;
import java.sql.SQLException;

public class InsertButtonTask extends AsyncTask<String, Void, String> {

    protected String doInBackground(String... params) {
        try {
            MainActivity.db.saveFingerprint(MainActivity.x, MainActivity.y, MainActivity.rss1,MainActivity.rss2, MainActivity.rss3, MainActivity.rss4, MainActivity.rss5, MainActivity.rss6, MainActivity.rss7,MainActivity.rss8);

            MainActivity.db.saveFingerprint2(MainActivity.x, MainActivity.y, MainActivity.rss11,MainActivity.rss12, MainActivity.rss13, MainActivity.rss14, MainActivity.rss15, MainActivity.rss16, MainActivity.rss17,MainActivity.rss18);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //This Method is called when Network-Request finished
    protected void onPostExecute(String serverData) {

    }
}
