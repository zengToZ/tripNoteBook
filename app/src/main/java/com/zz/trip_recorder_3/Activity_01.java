package com.zz.trip_recorder_3;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.app.FragmentManager;

import java.io.File;

public class Activity_01 extends AppCompatActivity implements
        Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener,
        Fragment3.OnFragmentInteractionListener{

    //private TextView mTextMessage;
    //private android.support.v4.app.Fragment frag;

    private static int REQUEST_CODE=1;
    final private static String TAG = "thisOne";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_01:
                    //mTextMessage.setText(R.string.title_01);
                    Fragment1 fragment1 =  Fragment1.newInstance(null,null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment1).commit();
                    return true;
                case R.id.navigation_02:
                    //mTextMessage.setText(R.string.title_02);
                    Fragment2 fragment2 = Fragment2.newInstance(null,null, -1,Activity_01.this);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment2).commit();
                    return true;
                case R.id.navigation_03:
                    //mTextMessage.setText(R.string.title_03);
                    Fragment3 fragment3 = Fragment3.newInstance(null,null);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment3).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_01);

        // for debug use
        staticGlobal.initializeIniFile();
        //File file; for(int i=100;i<200;i++){file = new File(getApplication().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(i));if(file.exists()){file.delete(); } }

        /***---check all permissions--***/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_CODE);
            }

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }

            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_CODE);
            }
        }

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment1 frag = new Fragment1();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container,frag).commit();
    }

    @Override
    protected void onResume(){
        Log.i(TAG,"on Resume Act1");
        super.onResume();
    }

    @Override
    protected void onStop(){

        Log.i(TAG,"onStop Act1");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.i(TAG,"on pause Act1");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on Destroy Act1");
    }

    public void onFragmentInteraction(Uri uri){
        // by default
    }

}
