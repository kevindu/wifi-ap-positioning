package com.example.jiuzhou.fingerprintcollector.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.jiuzhou.fingerprintcollector.MainActivity;
import com.example.jiuzhou.fingerprintcollector.R;

import xuandu.location.indoormapapi.update.MapUpdate;
import xuandu.location.indoormapapi.update.MapUpdateListener;

/**
 * Created by xuandu on 28/10/2015.
 *
 * The launcher activity
 *
 * check map updates and if ok redirect to main activity
 */
public class LandingActivity extends Activity implements MapUpdateListener{

    TextView updateInfoView = null;
    ProgressBar updateProgressBar = null;

    Intent mainIntent = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        updateInfoView = (TextView) findViewById(R.id.updateInfoView);
        updateProgressBar = (ProgressBar) findViewById(R.id.updatingProgressBar);

        SystemConfig.init(this);

        Log.i("debug","start map check");
        MapUpdate.getInstance().checkMap(this, this);
        Log.i("debug", "finish map check");

        mainIntent = new Intent(LandingActivity.this, MainActivity.class);

    }


    @Override
    public void onFailure(String msg) {
        // skip update check failure since it needs the Internet

//        new AlertDialog.Builder(this)
//                .setTitle("Loading Error")
//                .setCancelable(false)
//                .setMessage(msg)
//                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        dialog.dismiss();
//                        System.exit(0);
//                    }
//                }).create().show();

        LandingActivity.this.startActivity(mainIntent);
        LandingActivity.this.finish();
    }

    @Override
    public void onWarning(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setCancelable(false)
                .setMessage(msg)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        System.exit(0);
                    }
                }).create().show();
    }

    @Override
    public void noUpdate() {
        updateInfoView.setText("No updates available.");

        LandingActivity.this.startActivity(mainIntent);
        LandingActivity.this.finish();
    }

    @Override
    public void needUpdate(String msg) {
        updateInfoView.setText("Updating...");
        MapUpdate.getInstance().downLoadMapUpdate();
    }

    @Override
    public void needDownLoad(String msg) {
        updateInfoView.setText("Downloading...");
        MapUpdate.getInstance().downLoadMapUpdate();
    }

    @Override
    public void onProgress(int max, int current) {
        if(updateProgressBar != null && updateProgressBar.isShown()){
            updateProgressBar.setMax(max);
            updateProgressBar.setProgress(current);
        }
    }

    @Override
    public void onProgressMsg(String title) {
        updateInfoView.setText(title);
    }

    @Override
    public void onSuccess() {
        updateInfoView.setText("Update successfully.");

        LandingActivity.this.startActivity(mainIntent);
        LandingActivity.this.finish();
    }
}
