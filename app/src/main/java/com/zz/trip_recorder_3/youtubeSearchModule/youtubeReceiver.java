package com.zz.trip_recorder_3.youtubeSearchModule;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class youtubeReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public youtubeReceiver(Handler handler){
        super(handler);
    }

    public interface Receiver{
        void onReceiveResult(int resultCode, Bundle resultData);
    }

    public void setmReceiver(Receiver mReceiver) {
        this.mReceiver = mReceiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData){
        if(mReceiver!=null){
            mReceiver.onReceiveResult(resultCode,resultData);
        }
    }
}