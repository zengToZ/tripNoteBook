package com.zz.trip_recorder_3;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import java.io.File;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Activity_01 extends AppCompatActivity implements
        Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener,
        Fragment3.OnFragmentInteractionListener{

    //private TextView mTextMessage;
    //private android.support.v4.app.Fragment frag;

    /*final private static int REQUEST_CODE_1=0xa1;
    final private static int REQUEST_CODE_2=0xa2;
    final private static int REQUEST_CODE_3=0xa3;
    final private static int REQUEST_CODE_4=0xa4;*/

    final private static int REQUEST_ALL = 0xa1;
    final private String[] PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_WIFI_STATE
    };

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

        // for initialising
        staticGlobal.fDir = getApplication().getBaseContext().getFilesDir();
        staticGlobal.context = this;
        staticGlobal.initializeIniFile();

        /***---check all permissions--***/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ALL);
            }
        }

        File newDir = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),staticGlobal.imgFolder);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Fragment1 frag = new Fragment1();
        getSupportFragmentManager().beginTransaction().add(R.id.main_container,frag).commit();
    }

    // check permission since Android M version
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
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
