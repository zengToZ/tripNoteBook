package com.zz.trip_recorder_3;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zz.trip_recorder_3.ExtraViewer.AboutViewer;
import com.zz.trip_recorder_3.ExtraViewer.Setting_Viewer;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Environment.getExternalStoragePublicDirectory;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class Activity_01 extends AppCompatActivity implements
        Fragment1.OnFragmentInteractionListener,
        Fragment2.OnFragmentInteractionListener{

    final private static int REQUEST_ALL = 0xa1;

    private static boolean isDrawerOpen = false;

    private static int frag1OpenCount = 0;

    final private String[] PERMISSIONS = {
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,
    };

    final private static String TAG = "thisOne";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_01:
                    frag1OpenCount++;
                    Fragment1 fragment1 =  Fragment1.newInstance(Integer.toString(frag1OpenCount));
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment1).commit();
                    return true;
                case R.id.navigation_02:
                    Fragment2 fragment2 = Fragment2.newInstance(null,null, -1);
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_container,fragment2).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setTitle("Trip Notebook");
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
            //actionBar.hide();
        setContentView(R.layout.activity_01);

        // for initialising
        staticGlobal.fDir = getApplication().getBaseContext().getFilesDir();
        staticGlobal.context = this;


        SharedPreferences settings = getSharedPreferences(staticGlobal.APP_NAME, 0);

        if (settings.getBoolean("is_first_run", true)) {
            //cleanUp();
            staticGlobal.initializeIniFile();
            settings.edit().putBoolean("is_first_run", false).apply();
        }
        staticGlobal.createCityJson(Activity_01.this);
        /*---check all permissions--*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(!hasPermissions(this, PERMISSIONS)){
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_ALL);
            }
        }

        File newDir = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),staticGlobal.imgFolder);
        if (!newDir.exists()) {
            if(newDir.mkdirs()){
                Log.i(TAG,"new private directory picture folder created");
            }
        }
        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        /*final FragmentOnStartup fragmentOnStartup =  FragmentOnStartup.newInstance(null,null);
        getSupportFragmentManager().beginTransaction().add(R.id.main_container, fragmentOnStartup).commit();*/

        final NavigationView sideNavigationView = (NavigationView)findViewById(R.id.nav_view);
        sideNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.drawer_navigation_01:
                        Intent intent1 = new Intent(Activity_01.this,Setting_Viewer.class);
                        startActivity(intent1);
                        return true;
                    case R.id.drawer_navigation_02:
                        Intent intent2 = new Intent(Activity_01.this,AboutViewer.class);
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });


        /*final Fragment1 fragment1 =  Fragment1.newInstance(Integer.toString(frag1OpenCount));
        getSupportFragmentManager().beginTransaction().add(R.id.main_container,fragment1).commit();
        getSupportFragmentManager().beginTransaction().hide(fragment1).commit();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getSupportFragmentManager().beginTransaction().remove(fragmentOnStartup).commit();
                getSupportFragmentManager().beginTransaction().show(fragment1).commit();
            }
        },2000);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        switch (item.getItemId()) {
            case android.R.id.home:
                if(isDrawerOpen)
                    drawerLayout.closeDrawer(Gravity.LEFT);
                else
                    drawerLayout.openDrawer(Gravity.LEFT);
                isDrawerOpen = !isDrawerOpen;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        frag1OpenCount++;
        getSupportFragmentManager().beginTransaction().add(R.id.main_container,Fragment1.newInstance(Integer.toString(frag1OpenCount))).commit();
        //FullScreencall();
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

    /*public void FullScreencall() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }*/

    /*private void cleanUp(){
        File file;
        for(int i=0;i<1024;i++) {
            file = new File(getApplication().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(i));
            if(file.exists()) file.delete();
        }
        try{
            staticGlobal.deleteIniFile();
        }catch (Exception e){
            e.printStackTrace();
        }
    }*/
}
