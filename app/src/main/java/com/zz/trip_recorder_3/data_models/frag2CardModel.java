package com.zz.trip_recorder_3.data_models;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

public class frag2CardModel {
    public Uri background;
    public Bitmap hidden_BG;
    public String title;
    public String description;
    public String editToday;
    public boolean edittoday = false;
    public int id;
    public String currUnitID;
    public Context context;
    public boolean doDelet = false;
    public boolean firstEver = false;
    public boolean isFrag1 = false; // for card 1 in frag1
    public boolean isStatic = false;
}
