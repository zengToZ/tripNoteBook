package com.zz.trip_recorder_3;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.trip_recorder_3.ExtraViewer.ImageViewer;
import com.zz.trip_recorder_3.data_models.tripCardModel;
import com.zz.trip_recorder_3.tools.recorder_tools;

import org.json.JSONException;

import java.io.IOException;

public class Activity_Viewer extends AppCompatActivity {

    final private static String TAG = "thisOne";
    private static int parentID;
    private static String id;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__viewer);

        parentID = getIntent().getExtras().getInt("parentID");
        id = getIntent().getExtras().getString("unitID");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(id.substring(id.length()-10,id.length()));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume(){
        Log.i(TAG,"on Resume viewer");
        super.onResume();
        linearLayout = this.findViewById(R.id.viewer_linearLayout);
        linearLayout.removeAllViews();
        try {
            JsonReader jsonReader = staticGlobal.getjsonReader(getApplication().getBaseContext(), staticGlobal.getTripJsonName(parentID));
            String name="", innerName="";
            jsonReader.beginObject();
            while(jsonReader.hasNext()){
                JsonToken nextToken = jsonReader.peek();
                Log.i(TAG,nextToken.toString());
                if(JsonToken.NAME.equals(nextToken)){
                    name  =  jsonReader.nextName();
                    Log.i(TAG,name);
                }
                else if(JsonToken.BEGIN_OBJECT.equals(nextToken)){
                    jsonReader.beginObject();
                    while (jsonReader.hasNext()){
                        JsonToken innerToken = jsonReader.peek();
                        Log.i(TAG,innerToken.toString());
                        if(!name.equals(id)) {
                            jsonReader.skipValue();
                            continue;
                        }

                        if(JsonToken.NAME.equals(innerToken)){
                            innerName = jsonReader.nextName();
                            Log.i(TAG,innerName);
                        }
                        else if(JsonToken.STRING.equals(innerToken)){
                            String innerValue = jsonReader.nextString();
                            String[] str = staticGlobal.parseViewItem(innerName);
                            if(str[0].equals("text")){
                                createText(innerValue);
                            }
                            else if(str[0].equals("img")){
                                createImg(innerValue);
                            }
                            Log.i(TAG,innerValue);
                        }
                        else if(JsonToken.NUMBER.equals(innerToken)){
                            int innerNum = jsonReader.nextInt();
                            Log.i(TAG,Integer.toString(innerNum));
                        }
                    }
                    jsonReader.endObject();
                }
                else if(JsonToken.STRING.equals(nextToken)){
                    String value =  jsonReader.nextString();
                    Log.i(TAG,value);
                }
                else if(JsonToken.NUMBER.equals(nextToken)){
                    int num =  jsonReader.nextInt();
                    Log.i(TAG,Integer.toString(num));
                }
            }
            jsonReader.endObject();
        }catch (Exception e){
            Log.i(TAG, "Viewer error at reading Json file: "+ e.toString());
        }

        Button btn = new Button(this);
        btn.setBackgroundResource(android.R.color.transparent);
        btn.setText("Click to Edit");
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v1) {
                Intent intent = new Intent(v1.getContext(), Activity_Editor.class);
                intent.putExtra("parentID",parentID);
                intent.putExtra("unitID",id);
                intent.putExtra("isEdit",true); // edit mode, need reload file
                v1.getContext().startActivity(intent);
            }
        });
        LinearLayout.LayoutParams thislayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(btn,thislayout);
    }

    // create text
    private void createText(String textContent){
        try{
            TextView newText = new EditText(Activity_Viewer.this);
            newText.setText(textContent);
            newText.setBackgroundResource(android.R.color.transparent);
            newText.setEnabled(false);
            newText.setRawInputType(InputType.TYPE_NULL);
            newText.setTextIsSelectable(true);
            newText.setPaintFlags(0);
            linearLayout.addView(newText);
            Log.i(TAG, "Text view added");
        }
        catch (Exception e){
            Log.i(TAG,"Error adding textview:"+e.toString());
        }
    }

    // create Image
    private void createImg(String img_Uri){
        final Uri imgUri = Uri.parse(img_Uri);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            grantUriPermission(imgUri);
        try {
            if (imgUri != null) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                if(w*h>=3000*2000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.2*bitmap.getWidth()),(int)(0.2*bitmap.getHeight()));
                }
                else if(w*h<=3000*2000 && w*h>=1500*1000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.5*bitmap.getWidth()),(int)(0.5*bitmap.getHeight()));
                }

                ImageView newImg = new ImageView(Activity_Viewer.this);
                newImg.setImageBitmap(bitmap);
                //newImg.setBackgroundResource(R.drawable.shadow_below);
                newImg.setScaleType(ImageView.ScaleType.FIT_CENTER);

                newImg.setOnClickListener(new ImageView.OnClickListener(){
                    @Override
                    public void onClick(View v1) {
                        final String uriStr = imgUri.toString();
                        Intent intent = new Intent(v1.getContext(), ImageViewer.class);
                        intent.putExtra("imgUri",uriStr);
                        v1.getContext().startActivity(intent);
                    }
                });

                LinearLayout.LayoutParams thislayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linearLayout.addView(newImg, thislayout);
                Log.i(TAG,"Image view added");
            }
        }catch (Exception e){
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        }
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
