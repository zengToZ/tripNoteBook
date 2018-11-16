package com.zz.trip_recorder_3.googleSearchModule;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class doConnect extends IntentService {
    private static final String TAG = "thisOne";
    public static final int resultCodeGoogle = 0xe1;
    public doConnect(){
        super("doConnect");
    }

    protected void onHandleIntent(Intent intent){
        try {
            // get receiverTag and searchString from MainActivity
            ResultReceiver resultReceiver = intent.getParcelableExtra("receiverTag");
            String searchStr;
            searchStr = intent.getStringExtra("searchString");

            Log.i(TAG, "start search");
            if (!searchStr.equals("")){
                connectGoogleSearch conn = new connectGoogleSearch(searchStr);
                // send data received back to MainActivity
                Bundle bun = new Bundle();
                bun.putStringArray("receivedURL",conn.getUrl());
                bun.putStringArray("title",conn.getTitle());
                resultReceiver.send(resultCodeGoogle,bun);
            }
        }catch (Exception e) {
            Log.i(TAG,"doConnect onHandleIntent:" + e.toString());
        }
    }
}
