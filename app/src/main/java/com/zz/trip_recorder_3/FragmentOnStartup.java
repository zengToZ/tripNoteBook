package com.zz.trip_recorder_3;

import android.content.Context;
import android.location.Criteria;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentOnStartup.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentOnStartup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentOnStartup extends Fragment {
    final private String TAG = "thisOne";
    private View frag3View;
    private Context frag3context;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentOnStartup() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOnStartup.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentOnStartup newInstance(String param1, String param2) {
        FragmentOnStartup fragment = new FragmentOnStartup();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "frag3 on resume");
        //LinearLayout llyt = frag3View.findViewById(R.id.frag_3_llyt);
        TextView batteryStatus = new TextView(frag3context);
        batteryStatus.setEnabled(false);

        //getPowerInfo(Criteria(LocationManager.GPS_PROVIDER))

        //batteryStatus.setText();

    }

    private int getPowerInfo(Criteria criteria){
        return criteria.getPowerRequirement();
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
        // Inflate the layout for this fragment
        frag3View = inflater.inflate(R.layout.fragment_on_startup, container, false);
        return frag3View;
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
        frag3context = context;
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


    /*
     Stashed buttons:
     private void showBtns(View v){
     Button btn = v.findViewById(R.id.resetIni);
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v1) {

            File file;
            String fileName, JsonContent;
            LinearLayout f = v.findViewById(R.id.frag_3_llyt);
            Button b1 = new Button(v.getContext());
                        b1.setText("More content to come");
                        f.addView(b1);
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
     }

     */

}
