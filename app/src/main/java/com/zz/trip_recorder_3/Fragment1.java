package com.zz.trip_recorder_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zz.trip_recorder_3.adapter.frag2CardAdapter;
import com.zz.trip_recorder_3.data_models.frag2CardModel;
import com.zz.trip_recorder_3.data_models.localeModel;
import com.zz.trip_recorder_3.googleSearchModule.doConnect;
import com.zz.trip_recorder_3.googleSearchModule.searchReceiver;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment implements LocationListener,OnMapReadyCallback,searchReceiver.Receiver {

    private View frag1View;
    private Context frag1context;

    private static final int requestCode_1 = 0xfffa;
    private static final int requestCode_2 = 0xfffb;

    final private static String TAG = "thisOne";

    private VideoView frag1Video;

    private searchReceiver receriver;
    private static String googleSearchImgUrl;
    private static String googleSearchTitle;

    private LocationManager locationManager;
    public static localeModel locale;   // public because might use it in fragment 2
    private static Geocoder geocoder;
    private double latitude;
    private double longitude;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */

    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG,"on Resume Frag2");
        // locale service start on..
        String cityName = staticGlobal.getCityName();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                updateLocation();
                if(cityName==null || !cityName.equals(locale.CityName))
                    staticGlobal.setCityName(locale.CityName);
            }
        }
        List<frag2CardModel> cardList = new ArrayList();
        // showing just one card on current editing status
        frag2CardModel m1 = new frag2CardModel();
        frag2CardModel m2 = new frag2CardModel();
        String showingTitle = "Hi, "+staticGlobal.getUserName();
        if(locale != null) {
            showingTitle += "\nNow at ";
            if(locale.Address1 != null) showingTitle += locale.Address1 + " ";
            if(locale.CityName != null) {
                showingTitle += locale.CityName + " ";
            }
            if(locale.State != null) {
                showingTitle += locale.State + " ";
            }
            if(locale.Country != null) {
                showingTitle += locale.Country;
            }
        }
        m1.title = showingTitle;
        m1.context = frag1context;
        m1.isFrag1 = true;

        if(cityName!=null){
            makeConnGoogle(cityName);
            if(googleSearchImgUrl!=null){
                m2.title = googleSearchTitle;
                m2.background = Uri.parse(googleSearchImgUrl);
            }
        }
        m2.isFrag1 = true;

        int showID = staticGlobal.getCurrShowingTripID();
        String lastEditID = staticGlobal.getCurrEditorID();
        if(showID == -1){
            m1.description =
                    "Welcome to Trip NoteBook!"+
                            "\n\t\t\tClick to add new JOURNEY!";
            m1.doDelet = false;
            m1.firstEver = true;
            m1.edittoday = false;
            cardList.add(m1);
        }
        else {
            String JsonContent = null;
            JSONObject jb= null;
            if(getActivity()!=null)
                JsonContent = staticGlobal.getJson(getActivity().getBaseContext(), staticGlobal.getTripJsonName(showID));
            try {
                if(JsonContent!=null)
                    jb = new JSONObject(JsonContent);
                if (jb.opt("trip_bg") !=null ) {
                    m1.background = Uri.parse(jb.getString("trip_bg"));
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        grantUriPermission(m1.background);
                }
                m1.id = jb.getInt("trip id");
                if(lastEditID!=null){
                    m1.currUnitID = lastEditID;
                    m1.editToday = "CLICK to last edit " + lastEditID.substring(lastEditID.length() - 10, lastEditID.length());
                    m1.edittoday = true;
                }
                if(jb.optString("trip name") != null)
                    m1.description = jb.getString("trip name");
                cardList.add(m1);
                cardList.add(m2);
            } catch (Exception e) {
                Log.i(TAG, "show current trip id: " + e.toString());
            }
        }

        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        android.support.v7.widget.RecyclerView recyclerView = (RecyclerView) frag1View.findViewById(R.id.card_list);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(frag1View.getContext());
        recyclerView.setLayoutManager(layoutManager);

        RecyclerView.Adapter adapter = new frag2CardAdapter(cardList, this.getContext());
        recyclerView.setAdapter(adapter);

        //getVideo(frag1View);

    }

    // update and get current location
    private void updateLocation() {
        locale = new localeModel();
        geocoder = new Geocoder(frag1context, Locale.getDefault());
        // check permission is granted or not
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(frag1context, "Location permission not granted for location update\nplease grant it", Toast.LENGTH_LONG).show();
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode_1);
                return;
            }
        }
        locationManager = (LocationManager) frag1context.getSystemService(Context.LOCATION_SERVICE);
        // Getting GPS status
        if(locationManager == null) return;
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // Getting network status
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Getting third party app required status
        boolean isPassiveEnabled = locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);

        // case when all disabled
        Location location;
        if (!isGPSEnabled && !isNetworkEnabled && !isPassiveEnabled){
            Toast.makeText(frag1context, "No available location service!\nEither turn on GPS or network", Toast.LENGTH_LONG).show();
            return;
        }
        // case when either enabled, to choose a newer one, but update request send by GPS first
        else {
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
                Log.i(TAG, "requestLocationUpdates by GPS");
            } else if(isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, this);
                Log.i(TAG, "requestLocationUpdates by Network");
            } else{
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,0,0, this);
                Log.i(TAG, "requestLocationUpdates by passive");
            }
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if(locationGPS == null && locationNet == null && locationPassive == null){
                locationManager.requestSingleUpdate(new Criteria(), new LocationListener() {
                    @Override
                    public void onLocationChanged(Location loc) {
                        getLocale(loc);
                        Log.i(TAG,"latitude " + latitude + "|longitude " + longitude + "|city "+ locale.CityName + "|country "+ locale.Country);
                    }
                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) { }
                    @Override
                    public void onProviderEnabled(String provider) { }
                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(frag1context, "Location Provider Disabled\nEither turn on GPS or network", Toast.LENGTH_LONG).show();
                    }
                },null);
            }
            long GPSLocationTime = 0;
            if (locationGPS != null)
                GPSLocationTime = locationGPS.getTime();

            long PassiveLocationTime = 0;
            if(locationPassive != null)
                PassiveLocationTime = locationPassive.getTime();

            long NetLocationTime = 0;
            if (locationNet != null)
                NetLocationTime = locationNet.getTime();

            long newerTime = 0;
            if(GPSLocationTime == Math.max(GPSLocationTime,NetLocationTime)){
                location = locationGPS;
                newerTime = GPSLocationTime;
            }
            else{
                location = locationNet;
                newerTime = NetLocationTime;
            }
            if(PassiveLocationTime == Math.max(newerTime,PassiveLocationTime)){
                location = locationPassive;
            }
        }
        getLocale(location);
        updateMap();
    }

    private void getLocale(Location loc){
        List<Address> addresses = null;
        if(loc != null){
            try{
                latitude = loc.getLatitude();
                longitude = loc.getLongitude();
                addresses = geocoder.getFromLocation(latitude,longitude,1);
            }catch (IOException e){
                Log.i(TAG,"get locale error: "+e.toString());
            }
            if(addresses != null && addresses.size()>0){
                locale.Address1 = addresses.get(0).getAddressLine(0);
                locale.Address2 = addresses.get(0).getAddressLine(1);
                locale.CityName = addresses.get(0).getLocality();
                locale.State = addresses.get(0).getAdminArea();
                locale.Country = addresses.get(0).getCountryName();
            }
        }
        else {
            Log.i(TAG,"location is null");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocale(location);
        Log.i(TAG,"latitude " + latitude + "|longitude " + longitude + "|city "+ locale.CityName + "|country "+ locale.Country);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(frag1context, "Location Provider Disabled\nEither turn on GPS or network", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    private void updateMap() throws NullPointerException{
        FragmentManager fragmentManager = getFragmentManager();
        try{
            SupportMapFragment mapFragment = (SupportMapFragment)
                    (fragmentManager != null ? getChildFragmentManager().findFragmentById(R.id.frag1Map) : null);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }catch (Exception e){
            Log.i(TAG, "Update map "+ e.toString());
        }
    }

    @Override
    public void onMapReady(GoogleMap GMap) {
        GoogleMap googleMap = GMap;
        if(googleMap !=null){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        frag1context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(frag1context, "Location permission not granted for map update\nplease grant it", Toast.LENGTH_LONG).show();
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                            android.Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode_2);
                    return;
                }
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng currLocale = new LatLng(latitude,longitude);
            MarkerOptions MO = new MarkerOptions().position(currLocale).title(locale.CityName).snippet(locale.Address1);
            googleMap.addMarker(MO);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocale,14f));
            UiSettings uiSettings = googleMap.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantedResults){

        for (int grantResult : grantedResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(frag1context, "You deny one permission!\nplease grant it", Toast.LENGTH_LONG).show();
                return;
            }
        }
        switch (requestCode){
            case requestCode_1:
                updateLocation();
                break;
            case requestCode_2:
                updateMap();
                break;
        }
    }

    private void getVideo(View v){
        frag1Video = v.findViewById(R.id.frag1Video);
        frag1Video.setVideoPath("http://www.html5videoplayer.net/videos/toystory.mp4");
        MediaController mediaController = new MediaController(v.getContext());
        mediaController.setAnchorView(frag1Video);
        frag1Video.setMediaController(mediaController);
        frag1Video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(false);
            }
        });
        frag1Video.setOnClickListener(new VideoView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                frag1Video.start();
            }
        });
    }

    private void makeConnGoogle(String searchStr){
        int rand = (int)(Math.random()*7);  // 7 is the lucky number =)
        receriver = new searchReceiver(new Handler());
        receriver.setmReceiver(this);
        Intent intent = new Intent(frag1context, doConnect.class);
        intent.putExtra("searchString",searchStr);
        intent.putExtra("receiverTag",receriver);
        intent.putExtra("searchIdx",0);
        if(getActivity()!=null)
            getActivity().getBaseContext().startService(intent);
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        googleSearchImgUrl = resultData.getString("receivedURL");
        googleSearchTitle = resultData.getString("title");
        Toast.makeText(frag1context,googleSearchImgUrl+googleSearchTitle , Toast.LENGTH_LONG).show();
        Log.i(TAG, "Link got!");
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        frag1View = inflater.inflate(R.layout.fragment_fragment1, container, false);
        // Inflate the layout for this fragment
        return frag1View;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG,"on attach Frag1");
        frag1context = context;

        staticGlobal sg = new staticGlobal();
        sg.setValueModifiedListener(new staticGlobal.valueModifiedListener() {
            @Override
            public void onModified() {
                onResume();
            }
        });

        if(staticGlobal.getUserName()==null){
            createUserNameDlg();
        }
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        locationManager.removeUpdates(this);
        mListener = null;
    }

    private void createUserNameDlg(){
        final EditText input = new EditText(frag1context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText("RP");
        input.setSelectAllOnFocus(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(frag1context)
                .setTitle("What's your name? ^^")
                .setView(input)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        staticGlobal.setUserName(input.getText().toString());
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        staticGlobal.setUserName("RP");
                        dialog.cancel();
                    }
                });
        builder.show();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void grantUriPermission(Uri uri){
        getActivity().grantUriPermission(getActivity().getPackageName(), uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION|
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION|
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        getActivity().getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
}
