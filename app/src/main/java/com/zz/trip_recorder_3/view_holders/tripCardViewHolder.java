package com.zz.trip_recorder_3.view_holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zz.trip_recorder_3.Activity_Editor;
import com.zz.trip_recorder_3.Activity_Triplist;
import com.zz.trip_recorder_3.Activity_Viewer;
import com.zz.trip_recorder_3.R;
import com.zz.trip_recorder_3.data_models.tripCardModel;

public class tripCardViewHolder extends RecyclerView.ViewHolder{
    public ImageView tripCardBg;
    public ImageView layer2;
    public TextView tripCardTitle;
    public TextView tripCardEdit;
    public int parentID;
    public String id;
    public boolean creNew;
    public boolean doDelete;

    public tripCardViewHolder(View v,tripCardModel tripModel){
        super(v);
        tripCardBg = v.findViewById(R.id.trip_card_bg);
        layer2 = v.findViewById(R.id.layer2);
        tripCardTitle = v.findViewById(R.id.trip_card_title);
        tripCardEdit = v.findViewById(R.id.trip_card_edit);
        parentID = tripModel.parentID;
        id = tripModel.id;
        creNew = tripModel.creNew;
        doDelete = tripModel.doDelete;

        // Create New card
        if(creNew && !doDelete){
            tripCardBg.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Editor.class);
                    intent.putExtra("parentID",parentID);
                    intent.putExtra("unitID",id);
                    intent.putExtra("isEdit",false);
                    v1.getContext().startActivity(intent);
                }
            });
        }
        // normal card
        else if(!doDelete){
            tripCardBg.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Viewer.class);
                    intent.putExtra("parentID",parentID);
                    intent.putExtra("unitID",id);
                    intent.putExtra("isEdit",false);
                    v1.getContext().startActivity(intent);
                }
            });

            tripCardBg.setOnLongClickListener(new ImageView.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Triplist.class);
                    intent.putExtra("doDel",true);
                    intent.putExtra("tripPackgeID",parentID);  // tripPackgeID to Activity_Triplist
                    //intent.putExtra("unitID",id);
                    /*intent.putExtra("parentID",parentID);
                    intent.putExtra("isEdit",false); // edit mode, need reload file*/
                    v1.getContext().startActivity(intent);
                    return true;
                }
            });

            tripCardEdit.setOnClickListener(new TextView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Editor.class);
                    intent.putExtra("parentID",parentID);
                    intent.putExtra("unitID",id);
                    intent.putExtra("isEdit",true); // edit mode, need reload file
                    v1.getContext().startActivity(intent);
                }
            });
        }
        // delete mode card
        else{
            tripCardBg.setOnClickListener(new ImageView.OnClickListener(){
                @Override
                public void onClick(View v1) {
                    Intent intent = new Intent(v1.getContext(), Activity_Triplist.class);
                    intent.putExtra("doneDel",true);
                    intent.putExtra("tripPackgeID",parentID);  // tripPackgeID to Activity_Triplist
                    intent.putExtra("unitID",id);
                    v1.getContext().startActivity(intent);
                }
            });
        }
    }
}
