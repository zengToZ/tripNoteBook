package com.zz.trip_recorder_3.ExtraViewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.zz.trip_recorder_3.R;

public class ImageViewer extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        String imgUri = getIntent().getExtras().getString("imgUri");

        final ImageView img = findViewById(R.id.shownImg);

        if(imgUri!=null){
            Uri uri = Uri.parse(imgUri);
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                grantUriPermission(uri);
            img.setImageURI(uri);
        }
        img.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                finish();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void grantUriPermission(Uri uri){
        this.grantUriPermission(this.getPackageName(), uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION|
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION|
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        this.getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
    }
}
