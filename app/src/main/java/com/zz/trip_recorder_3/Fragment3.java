package com.zz.trip_recorder_3;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zz.trip_recorder_3.data_models.frag2CardModel;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment3 extends Fragment {
    final private String TAG = "thisOne";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Fragment3() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment3.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment3 newInstance(String param1, String param2) {
        Fragment3 fragment = new Fragment3();
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
        final View v = inflater.inflate(R.layout.fragment_fragment3, container, false);
        // Inflate the layout for this fragment

        Button btn = v.findViewById(R.id.resetIni);
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v1) {
                /** check iniFile**/
                File file;
                String fileName, JsonContent;
                LinearLayout f = v.findViewById(R.id.frag_3_llyt);
                try {
                    for (int i = 100; i <= staticGlobal.getCurrTripID(); i++) {
                        fileName = staticGlobal.getTripJsonName(i);
                        file = new File(getActivity().getBaseContext().getFilesDir(), fileName);
                        if (file.exists()) {
                            JsonContent = staticGlobal.getJson(getActivity().getBaseContext(), fileName);
                            final JSONObject jb = new JSONObject(JsonContent);
                            Button b = new Button(v.getContext());
                            b.setText(jb.getString("trip id"));
                            b.setOnClickListener(new Button.OnClickListener(){
                                @Override
                                public void onClick(View v2) {
                                    try{
                                    Log.i(TAG,jb.toString(1));
                                    }catch (Exception e){
                                        Log.i(TAG,e.toString());
                                    }
                                }
                            });
                            f.addView(b);
                        }
                    }
                }catch (Exception e){
                    Log.i(TAG,e.toString());
                }

            }
        });

        Button btn2 = v.findViewById(R.id.rest_all);
        btn2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v2) {
                try{
                    staticGlobal.deleteIniFile();
                }catch (IOException e){
                    e.printStackTrace();
                }
                File file; for(int i=0;i<200;i++){file = new File(getActivity().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(i));if(file.exists()){file.delete(); } }
            }
        });




        return v;
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
}
