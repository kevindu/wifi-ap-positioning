package xuandu.location.genoa.fingerprintcollector.radiomap;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.SQLException;
import java.util.HashMap;

import xuandu.location.genoa.fingerprintcollector.MainActivity;
import xuandu.location.genoa.fingerprintcollector.MyIPSDatabase;

import static xuandu.location.genoa.fingerprintcollector.MainActivity.APsID;

public class ObtainRSSbyAPTask extends AsyncTask<HashMap<String, RSSObservation>, Void, Void> {

    @Override
    protected Void doInBackground(HashMap<String, RSSObservation>... params) {
        for (String AP_ID : APsID) {
            int rss;
            try {
                rss = MainActivity.db.getRSS(MainActivity.MAC, AP_ID);
            } catch (SQLException e) {
                e.printStackTrace();
                rss = -110;
            }

            params[0].get(AP_ID).addRSS(rss, 0);
        }
        return null;
    }
}
