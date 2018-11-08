package com.zz.trip_recorder_3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zz.trip_recorder_3.adapter.cardAdapter;
import com.zz.trip_recorder_3.data_models.frag2CardModel;
import com.zz.trip_recorder_3.tools.recorder_tools;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    final private static String TAG = "thisOne";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private RecyclerView RecyclerView;
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager LayoutManager;

    private VideoView frag1Video;
    private MediaController mediaController;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment1() {
        // Required empty public constructor
    }

    /** my insertion  **/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment1.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment1 newInstance(String param1, String param2) {
        Fragment1 fragment = new Fragment1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

        List<frag2CardModel> cardList = new ArrayList();

        frag2CardModel m1 = new frag2CardModel();

        /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
        bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.2*bitmap.getWidth()),(int)(0.2*bitmap.getHeight()));
        ImageView newImg = new ImageView(Activity_Editor.this);
        newImg.setImageBitmap(bitmap);*/

        //
        //
        int showID = staticGlobal.getCurrShowingTripID();
        String lastEditID = staticGlobal.getCurrEditorID();
        if(showID == -1){
            m1.description = "Welcome to Trip NoteBook!";
            cardList.add(m1);
        }
        else {
            String JsonContent = staticGlobal.getJson(getActivity().getBaseContext(), staticGlobal.getTripJsonName(showID));
            try {
                JSONObject jb = new JSONObject(JsonContent);
                if (jb.opt("trip_bg") !=null ) {
                    m1.background = Uri.parse(jb.getString("trip_bg"));
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                        grantUriPermission(m1.background);
                }
                m1.id = jb.getInt("trip id");
                m1.currUnitID = lastEditID;
                m1.title = Integer.toString(jb.getInt("trip id"));
                m1.editToday = "LAST EDIT " + lastEditID.substring(lastEditID.length() - 10, lastEditID.length());
                m1.edittoday = true;
                cardList.add(m1);
            } catch (Exception e) {
                Log.i(TAG, "show current trip id: " + e.toString());
            }
        }


        View v = inflater.inflate(R.layout.fragment_fragment1, container, false);
        this.RecyclerView = (RecyclerView) v.findViewById(R.id.card_list);
        RecyclerView.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(v.getContext());
        RecyclerView.setLayoutManager(LayoutManager);

        Adapter = new cardAdapter(cardList,this.getContext());
        RecyclerView.setAdapter(Adapter);

        getVideo(v);
        // Inflate the layout for this fragment
        return v;
    }

    private void getVideo(View v){
        frag1Video = v.findViewById(R.id.frag1Video);
        frag1Video.setVideoPath("http://www.html5videoplayer.net/videos/toystory.mp4");
        mediaController = new MediaController(v.getContext());
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
        mListener = null;
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
