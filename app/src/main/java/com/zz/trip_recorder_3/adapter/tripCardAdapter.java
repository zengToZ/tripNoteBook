package com.zz.trip_recorder_3.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zz.trip_recorder_3.data_models.tripCardModel;
import com.zz.trip_recorder_3.tools.recorder_tools;
import com.zz.trip_recorder_3.view_holders.tripCardViewHolder;

import java.io.IOException;
import java.util.List;

import static com.zz.trip_recorder_3.R.*;

public class tripCardAdapter extends RecyclerView.Adapter<tripCardViewHolder> {

    private List<tripCardModel> tripCardModelList;
    private Context context;
    private int count = 0;

    public tripCardAdapter(List<tripCardModel> tripCardModelList, Context context) {
        this.tripCardModelList = tripCardModelList;
        this.context = context;
    }

    @Override
    public int getItemCount(){
        return this.tripCardModelList.size();
    }

    @Override
    public void onBindViewHolder(tripCardViewHolder tripCardViewHolder, int i) {
        tripCardModel tCM = tripCardModelList.get(i);
        try {
            if (tCM.background != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), tCM.background);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                if(w*h>=3000*2000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.25*bitmap.getWidth()),(int)(0.25*bitmap.getHeight()));
                }
                else if(w*h<=3000*2000 && w*h>=1500*1000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.5*bitmap.getWidth()),(int)(0.5*bitmap.getHeight()));
                }

                tripCardViewHolder.tripCardBg.setImageBitmap(bitmap);
                tripCardViewHolder.tripCardBg.setAlpha(150);
                tripCardViewHolder.tripCardBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.i("thisOne", "showing: " + tCM.background.toString()+bitmap.toString());
            }
            else if(tCM.creNew){
                tripCardViewHolder.tripCardBg.setImageResource(drawable.add_new);
                tripCardViewHolder.tripCardBg.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
            else{
                tripCardViewHolder.tripCardBg.setImageResource(mipmap.umbrella);
                tripCardViewHolder.tripCardBg.setAlpha(150);
                tripCardViewHolder.tripCardBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.i("thisOne", "showing: default trip card img");
            }
        }catch (IOException e) {
            Log.i("thisOne","error"+e.toString());
        }

        if (tCM.dateTitle!=null) {
            tripCardViewHolder.tripCardTitle.setText(tCM.dateTitle);
            tripCardViewHolder.tripCardTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,24f);
        }
        if (tCM.edit!=null) {
            tripCardViewHolder.tripCardEdit.setText(tCM.edit);
            tripCardViewHolder.tripCardEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);
        }
    }

    @Override
    public tripCardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(layout.trip_card_layout, viewGroup, false);
        tripCardViewHolder t = new tripCardViewHolder(itemView,tripCardModelList.get(count));   // pass tripCardModel by list sequence order
        count++;
        return t;
    }
}
