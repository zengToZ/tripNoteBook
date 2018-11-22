package com.zz.trip_recorder_3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Activity_Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, Activity_01.class));
        finish();
    }
}
