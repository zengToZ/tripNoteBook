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

import com.zz.trip_recorder_3.R;
import com.zz.trip_recorder_3.tools.recorder_tools;
import com.zz.trip_recorder_3.view_holders.frag2CardViewHolder;
import com.zz.trip_recorder_3.data_models.frag2CardModel;

import java.io.IOException;
import java.util.List;

public class frag2CardAdapter extends RecyclerView.Adapter<frag2CardViewHolder> {
    private List<frag2CardModel> frag2CardModelList;
    private Context context;
    private int count=0;

    public frag2CardAdapter(List<frag2CardModel> frag2CardModelList, Context context)  {
        this.frag2CardModelList = frag2CardModelList;
        this.context = context;;
    }

    @Override
    public int getItemCount(){
        return this.frag2CardModelList.size();
    }

    @Override
    public void onBindViewHolder(frag2CardViewHolder frag2CardViewHolder, int i) {
        frag2CardModel CM = frag2CardModelList.get(i);
        try {
            if (CM.background != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), CM.background);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                if(w*h>=3000*2000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.25*bitmap.getWidth()),(int)(0.25*bitmap.getHeight()));
                }
                else if(w*h<=3000*2000 && w*h>=1500*1000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.5*bitmap.getWidth()),(int)(0.5*bitmap.getHeight()));
                }

                frag2CardViewHolder.background.setImageBitmap(bitmap);
                frag2CardViewHolder.background.setAlpha(180);
                frag2CardViewHolder.background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.i("thisOne", "showing: " + CM.background.toString()+bitmap.toString());
            }
            else{
                frag2CardViewHolder.background.setImageResource(R.mipmap.umbrella);
                frag2CardViewHolder.background.setAlpha(180);
                frag2CardViewHolder.background.setScaleType(ImageView.ScaleType.CENTER_CROP);
                Log.i("thisOne", "showing: default card img");
            }
            if(CM.doDelet){
                frag2CardViewHolder.layer1.setVisibility(View.VISIBLE);
                frag2CardViewHolder.background.setAlpha(50);
                frag2CardViewHolder.layer1.setImageResource(R.drawable.delet_sign);
                frag2CardViewHolder.layer1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            }
        }catch (IOException e) {
            Log.i("thisOne","error"+e.toString());
        }

        if (CM.title!=null) {
            frag2CardViewHolder.title.setText(CM.title);
            frag2CardViewHolder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f);
        }
        if (CM.description!=null) {
            frag2CardViewHolder.description.setText(CM.description);
            frag2CardViewHolder.description.setTextSize(TypedValue.COMPLEX_UNIT_SP,24f);
        }
        if (CM.editToday!=null){
            frag2CardViewHolder.editTodayClk.setText(CM.editToday);
            frag2CardViewHolder.editTodayClk.setTextSize(TypedValue.COMPLEX_UNIT_SP,16f);
        }
    }

    @Override
    public frag2CardViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.frag2_card_layout, viewGroup, false);
        frag2CardViewHolder f = new frag2CardViewHolder(itemView,frag2CardModelList.get(count));    // pass frag2CardModel by list sequence order
        count++;
        return f;
    }
}
