package xuandu.location.genoa.fingerprintcollector;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import xuandu.location.genoa.fingerprintcollector.radiomap.RSSObservation;

import static xuandu.location.genoa.fingerprintcollector.MainActivity.APsBSSID;
import static xuandu.location.genoa.fingerprintcollector.MainActivity.APsID;

public class InsertButtonTask extends AsyncTask<HashMap<String, RSSObservation>, Void, String> {

    protected String doInBackground(HashMap<String, RSSObservation>... params) {
        HashMap<String, RSSObservation> rssObservationsByAP = params[0];
        HashMap<String, RSSObservation> rssObservationsByUE = params[1];

        try {
            int[] RPcoo;
            double[] means, vars;

            RPcoo = new int[2];
            RPcoo[0] = MainActivity.x;
            RPcoo[1] = MainActivity.y;

            means = new double[8];
            means[0] = rssObservationsByAP.get(APsID.get(0)).getRSSAverage();
            means[1] = rssObservationsByAP.get(APsID.get(1)).getRSSAverage();
            means[2] = rssObservationsByAP.get(APsID.get(2)).getRSSAverage();
            means[3] = rssObservationsByAP.get(APsID.get(3)).getRSSAverage();
            means[4] = rssObservationsByAP.get(APsID.get(4)).getRSSAverage();
            means[5] = rssObservationsByAP.get(APsID.get(5)).getRSSAverage();
            means[6] = rssObservationsByAP.get(APsID.get(6)).getRSSAverage();
            means[7] = rssObservationsByAP.get(APsID.get(7)).getRSSAverage();

            vars = new double[8];
            vars[0] = rssObservationsByAP.get(APsID.get(0)).getRSSVariance();
            vars[1] = rssObservationsByAP.get(APsID.get(1)).getRSSVariance();
            vars[2] = rssObservationsByAP.get(APsID.get(2)).getRSSVariance();
            vars[3] = rssObservationsByAP.get(APsID.get(3)).getRSSVariance();
            vars[4] = rssObservationsByAP.get(APsID.get(4)).getRSSVariance();
            vars[5] = rssObservationsByAP.get(APsID.get(5)).getRSSVariance();
            vars[6] = rssObservationsByAP.get(APsID.get(6)).getRSSVariance();
            vars[7] = rssObservationsByAP.get(APsID.get(7)).getRSSVariance();

            MainActivity.myPositioning.insertSingleRP(RPcoo, means, vars);

            try {
                MainActivity.myPositioning.save();
            } catch (IOException e) {
                e.printStackTrace();
            }

            MainActivity.db.saveFingerprint(MainActivity.x, MainActivity.y,
                    rssObservationsByAP.get(APsID.get(0)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(1)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(2)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(3)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(4)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(5)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(6)).getRSSAverage(),
                    rssObservationsByAP.get(APsID.get(7)).getRSSAverage());

            MainActivity.db.saveFingerprint2(MainActivity.x, MainActivity.y,
                    rssObservationsByUE.get(APsBSSID.get(0)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(1)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(2)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(3)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(4)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(5)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(6)).getRSSAverage(),
                    rssObservationsByUE.get(APsBSSID.get(7)).getRSSAverage());

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    //This Method is called when Network-Request finished
    protected void onPostExecute(String serverData) {

    }

}
