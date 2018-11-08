package com.zz.trip_recorder_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.support.v7.app.AppCompatActivity;

import com.zz.trip_recorder_3.adapter.cardAdapter;
import com.zz.trip_recorder_3.data_models.frag2CardModel;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    final private static String TAG = "thisOne";

    private static View view;
    private static boolean DoDel = false;

    private android.support.v7.widget.RecyclerView RecyclerView;
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager LayoutManager;

    private static File file;
    private static String fileName, JsonContent;
    private JSONObject jb;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String trip_id = "trip_id";

    //
    private String mParam1;
    private String mParam2;
    private int Trip_id_forDel;
    private static Context context;

    private OnFragmentInteractionListener mListener;

    public Fragment2() {
        // Required empty public constructor
    }
    /** my insertion  **/

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment2.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment2 newInstance(String param1, String param2, int id, Context param3) {
        Fragment2 fragment = new Fragment2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(trip_id, id);
        fragment.setArguments(args);
        context = param3;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            Trip_id_forDel = getArguments().getInt(trip_id);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_fragment2, container, false);
        return view;
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
    public void onResume(){
        super.onResume();
        if(mParam1!=null && mParam1.equals("doDel")){
            DoDel = true;
            mParam1 = null;
        }
        if(mParam2!=null && mParam2.equals("doneDel")){
            String delFile = staticGlobal.getTripJsonName(Trip_id_forDel);
            createAlertDlg("DELETE","Delete Record " + delFile, "Delete","Cancel",delFile);
            mParam2 = null;
        }
        if(view!=null){
            updateView(view, DoDel);
            DoDel = false;
            mParam1 = null;
            mParam2 = null;
        }
        Log.i(TAG,"on Resume Frag2");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void delRecord(String delFile){
        File f = new File(getActivity().getBaseContext().getFilesDir(), delFile);
        if (f.exists()){
            if(f.delete()){
                staticGlobal.addTripCount(-1);
                if(staticGlobal.getCurrShowingTripID() == Trip_id_forDel){
                    staticGlobal.setCurrShowingTripID(-1);
                    staticGlobal.setCurrEditorID("");
                    Log.i(TAG,"done del file: "+delFile);
                }
                if(view!=null)
                    updateView(view, false);
            }
        }
    }

    private void updateView(View v, boolean doDel){
        // create card list for Recycle View
        List<frag2CardModel> cardList = new ArrayList();
        if(RecyclerView!=null && RecyclerView.getChildCount()>0){
            RecyclerView.removeAllViews();
        }


        /** create new **/
        Button cre_btn = v.findViewById(R.id.cre_new);
        cre_btn.setBackgroundResource(android.R.color.transparent);
        if(doDel){
            cre_btn.setText("BACK");
            try {
                cre_btn.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        updateView(view,false);
                    }
                });
            }catch (Exception e){
                Log.i(TAG,e.toString());
            }
        }
        else{
            cre_btn.setText("Click to Create NEW Trip!");
            try {
                cre_btn.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        Intent intent = new Intent(v1.getContext(), Activity_Triplist.class);
                        intent.putExtra("isNew",true);
                        intent.putExtra("newTripID",staticGlobal.getCurrTripID()+1);
                        staticGlobal.addTripCount(1);                                           // add trip count also update current trip ID
                        startActivity(intent);
                    }
                });
            }catch (Exception e){
                Log.i(TAG,e.toString());
            }
        }

        // collect information from JSON files to get full list of saved trip packages

        try {
            for (int i = 100; i <= staticGlobal.getCurrTripID(); i++) {
                fileName = staticGlobal.getTripJsonName(i);
                file = new File(getActivity().getBaseContext().getFilesDir(), fileName);
                if (file.exists()) {
                    JsonContent = staticGlobal.getJson(getActivity().getBaseContext(), fileName);
                    jb = new JSONObject(JsonContent);
                    frag2CardModel m = new frag2CardModel();
                    m.id = jb.optInt("trip id");
                    if (jb.opt("trip_bg") !=null ) {
                        m.background = Uri.parse(jb.getString("trip_bg"));
                        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                            grantUriPermission(m.background);
                    }
                    m.edittoday = false;
                    m.title = "Sample" +" "+ Integer.toString(m.id);
                    m.context = context;
                    m.doDelet = doDel;
                    cardList.add(m);
                }
            }
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }

        // set Adapter into Recycle View
        this.RecyclerView = (RecyclerView) v.findViewById(R.id.trip_package_list);
        RecyclerView.setHasFixedSize(true);
        LayoutManager = new LinearLayoutManager(v.getContext());
        RecyclerView.setLayoutManager(LayoutManager);

        Adapter = new cardAdapter(cardList,this.getContext());
        RecyclerView.setAdapter(Adapter);
    }

    // create ok-cancel alert box
    private void createAlertDlg(String title, String message, String Y, String N, String DelFile){
        final String delFile = DelFile;
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(Y,null)
                .setNegativeButton(N,null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button yesButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button noButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        delRecord(delFile);
                        alertDialog.dismiss();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
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
