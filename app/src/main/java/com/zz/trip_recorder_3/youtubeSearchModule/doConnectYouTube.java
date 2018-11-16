package com.zz.trip_recorder_3.youtubeSearchModule;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class doConnectYouTube extends IntentService {
    public static final int resultCodeYoutube = 0xe2;

    public doConnectYouTube(){
        super("tryConn");
    }

    protected void onHandleIntent(Intent intent){
        try {
            // get receiverTag and searchString from MainActivity
            ResultReceiver resultReceiver = intent.getParcelableExtra("receiverTag");
            String s;
            s = intent.getStringExtra("searchString");

            if (s!=""){
                connectYoutube con = new connectYoutube(s);
                // send data received back to MainActivity
                Bundle bun = new Bundle();
                bun.putString("receivedURL",con.getUrl());
                bun.putString("Title",con.getTitle());
                bun.putString("Description",con.getDescription());
                resultReceiver.send(resultCodeYoutube,bun);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}