package com.zz.trip_recorder_3.view_holders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zz.trip_recorder_3.Activity_01;
import com.zz.trip_recorder_3.Activity_Editor;
import com.zz.trip_recorder_3.Activity_Triplist;
import com.zz.trip_recorder_3.Activity_Viewer;
import com.zz.trip_recorder_3.Fragment2;
import com.zz.trip_recorder_3.R;
import com.zz.trip_recorder_3.data_models.frag2CardModel;
import com.zz.trip_recorder_3.staticGlobal;

public class frag2ViewHolder extends RecyclerView.ViewHolder {
    public ImageView background;
    public ImageView layer1;
    public TextView title;
    public TextView description;
    public TextView editTodayClk;
    public int id; // unique ordinal id for each trip package start from 101
    public String currUnitID;
    public boolean edittoday;
    public Context context;
    public boolean doDelet;

    public frag2ViewHolder(View v, frag2CardModel m){
        super(v);
        background = v.findViewById(R.id.card_bg);
        layer1 = v.findViewById(R.id.layer1);
        title = v.findViewById(R.id.card_title);
        description = v.findViewById(R.id.card_description);
        editTodayClk = v.findViewById(R.id.editTodayClk);
        id = m.id;
        currUnitID = m.currUnitID;
        edittoday = m.edittoday;
        context = m.context;
        doDelet = m.doDelet;

        if(!doDelet){
            background.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Triplist.class);
                    intent.putExtra("isNew",false);
                    intent.putExtra("tripPackgeID",id);
                    v1.getContext().startActivity(intent);
                }
            });
            background.setOnLongClickListener(new ImageView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Fragment2 fragment2 = Fragment2.newInstance("doDel",null,-1,context);
                    try{
                        final FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_container, fragment2);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    catch (Exception e){
                        Log.i("thisOne",e.toString());
                    }
                    return true;
                }
            });
        }
        else {
            background.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Fragment2 fragment2 = Fragment2.newInstance(null,"doneDel", id, context);
                    try{
                        final FragmentTransaction transaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_container, fragment2);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    catch (Exception e){
                        Log.i("thisOne",e.toString());
                    }
                }
            });
        }

        if(edittoday){
            editTodayClk.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Editor.class);
                    intent.putExtra("parentID",id);
                    intent.putExtra("unitID",currUnitID);
                    intent.putExtra("isEdit",true); // edit mode, need reload file
                    v1.getContext().startActivity(intent);
                }
            });
        }


    }
}
