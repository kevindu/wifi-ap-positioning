package xuandu.location.genoa.fingerprintcollector.localizer;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class Detector {
	private Context context;
	
	public Detector(Context context){
		this.context = context;
	}

	
    /**
     * Scan and get a list of available APs in real time
     * @return list of APs
     */
  	public List<ScanResult> getAPList() {
  		WifiManager myWifiManager;
  		myWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
  		myWifiManager.startScan();
  		List<ScanResult> APList = myWifiManager.getScanResults();
  		return APList;
  	}
}
