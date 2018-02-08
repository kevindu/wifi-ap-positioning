package xuandu.location.genoa.fingerprintcollector;

import android.os.AsyncTask;

public class DBConnection  extends AsyncTask<Boolean, Void, String> {

    protected String doInBackground(Boolean... params) {
        // true to create connection and false to close the connection
        if (params[0]) {
            MainActivity.db = new MyIPSDatabase();
        }else {
            MainActivity.db.disconnect();
        }
        return null;
    }

    //This Method is called when Network-Request finished
    protected void onPostExecute(String serverData) {
    }
}
