package xuandu.location.genoa.fingerprintcollector.radiomap;

import java.util.ArrayList;

/**
 * Class to hold the time-series rss observations
 *
 * Created by xuandu on 15/10/15.
 */
public class RSSObservation {
    private String ap_mac;
    private String ap_SSID;
    private int ap_frequency;
    private int rss;
    private ArrayList<Integer> rawRssArray;
    private ArrayList<Long> rssTimestampArray;
    private int duration;

    public RSSObservation(String ap_mac, String ap_SSID, int ap_frequency){
        this.ap_mac = ap_mac;
        this.ap_SSID = ap_SSID;
        this.ap_frequency = ap_frequency;
        rawRssArray = new ArrayList<>();
        rssTimestampArray = new ArrayList<>();
    }

    public RSSObservation(String ap_mac){
        this.ap_mac = ap_mac;
        rawRssArray = new ArrayList<>();
        rssTimestampArray = new ArrayList<>();
    }

    public int getRSSAverage(){
        int sum = 0;
        int num = rawRssArray.size();
        if (num == 0) {
            return -110;
        }else {
            for (Integer each : rawRssArray) {
                sum += each;
            }
            int rss_average = sum / num;
            rss = rss_average;
            return rss_average;
        }
    }

    public double getRSSVariance()
    {
        double mean = this.getRSSAverage();
        int num = rawRssArray.size();
        double temp = 0;
        for(double a :rawRssArray)
            temp += (a-mean)*(a-mean);
        return temp/num;
    }

    public void addRSS(Integer rss, long timestamp){
        rawRssArray.add(rss);
        rssTimestampArray.add(timestamp);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public long getLatestTimestamp() {
        if (rssTimestampArray.size() > 0) {
            return rssTimestampArray.get(rssTimestampArray.size() - 1);
        }else {
            return 0;
        }
    }

    public String getAp_mac() {
        return ap_mac;
    }

    public String getAp_SSID() {
        return ap_SSID;
    }

    public int getAp_frequency() {
        return ap_frequency;
    }

}
