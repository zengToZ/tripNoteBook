package com.zz.trip_recorder_3.view_holders;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zz.trip_recorder_3.Activity_Editor;
import com.zz.trip_recorder_3.Activity_Viewer;
import com.zz.trip_recorder_3.R;
import com.zz.trip_recorder_3.data_models.tripCardModel;

public class tripCardViewHolder extends RecyclerView.ViewHolder{
    public ImageView tripCardBg;
    public TextView tripCardTitle;
    public TextView tripCardEdit;
    public int parentID;
    public String id;
    public boolean creNew;

    public tripCardViewHolder(View v,tripCardModel tripModel){
        super(v);
        tripCardBg = v.findViewById(R.id.trip_card_bg);
        tripCardTitle = v.findViewById(R.id.trip_card_title);
        tripCardEdit = v.findViewById(R.id.trip_card_edit);
        parentID = tripModel.parentID;
        id = tripModel.id;
        creNew = tripModel.creNew;

        if(creNew){
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
        else{
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
    }
}
