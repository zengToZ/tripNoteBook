package com.zz.trip_recorder_3;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zz.trip_recorder_3.adapter.tripCardAdapter;
import com.zz.trip_recorder_3.data_models.frag2CardModel;
import com.zz.trip_recorder_3.data_models.tripCardModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Activity_Triplist extends AppCompatActivity {
    final private static String TAG = "thisOne";

    private int id = -1;
    //private frag2CardModel model;
    private RecyclerView RecyclerView;
    private RecyclerView.Adapter Adapter;
    private RecyclerView.LayoutManager LayoutManager;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__triplist);
        actionBar = getSupportActionBar();

        // create new trip json file if received "isNew = true"
        if(getIntent().getExtras().getBoolean("isNew")) {
            id = getIntent().getExtras().getInt("newTripID");
            FileOutputStream outputStream;
            JSONObject newTripJson = new JSONObject();
            String filename = staticGlobal.getTripJsonName(id);
            String fileContents = "";
            File file = new File(getApplication().getBaseContext().getFilesDir(), filename);
            try {
                newTripJson.put("trip id", id);
                newTripJson.put("trip count",0);
                fileContents = newTripJson.toString(1);
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(fileContents.getBytes());
                Log.i(TAG,newTripJson.toString());
                outputStream.close();
            }catch (Exception e){
                Log.i(TAG,e.toString());
            }
        }

        Log.i(TAG,"on Create trip list");
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
        Log.i(TAG,"on Resume trip list");
        super.onResume();

        List<tripCardModel> cardList = new ArrayList();

        if(id<0){
            id = getIntent().getExtras().getInt("tripPackgeID");
        }

        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Sample " + Integer.toString(id));
        }

        File file = new File(getApplication().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(id));
        if(file.exists()){

            //String jsonStr = staticGlobal.getJson(getApplication().getBaseContext(),staticGlobal.getTripJsonName(id));
            try {
                // unitID: 101_2018-01-01
                /**Json sample:
                 * "{\"trip id\":103,
                 * \"trip count\":0,
                 * \"103_2018-11-25\":
                 * {\"item count\":3,
                 * \"text 0\":\"\",
                 * \"text 1\":\"\",
                 * \"text 2\":\"\"}
                 * }";**/
                JsonReader jsonReader = staticGlobal.getjsonReader(getApplication().getBaseContext(),staticGlobal.getTripJsonName(id));
                String name="",innerName="",innerValue="";
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
                            if(JsonToken.NAME.equals(innerToken)){
                                innerName = jsonReader.nextName();
                                if(!innerName.equals("unit_bg")){
                                    jsonReader.skipValue();
                                    continue;
                                }
                                Log.i(TAG,innerName);
                            }
                            else if(JsonToken.STRING.equals(innerToken)){
                                innerValue = jsonReader.nextString();
                                Log.i(TAG,innerValue);
                            }
                        }

                        tripCardModel t = new tripCardModel();
                        t.parentID = id;
                        t.id = name;
                        t.edit = "edit";
                        if(!innerValue.equals("")){
                            t.background = Uri.parse(innerValue);
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                                grantUriPermission(t.background);
                            innerValue = "";
                        }
                        if(name.length()>=14)
                            t.dateTitle = name.substring(name.length()-10,name.length());
                        //t.background = Uri.parse(staticGlobal.defaultImgStr);
                        cardList.add(t);

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
                Log.i(TAG, "error when open trip package jason file(trip_list):"+e.toString());
            }
        }

        // create new unit button click
        tripCardModel btn = new tripCardModel();
        //btn.background =
        btn.creNew = true;
        btn.parentID = id;
        btn.id = "";
        cardList.add(btn);

        RecyclerView = (RecyclerView) findViewById(R.id.tripunit_list);
        RecyclerView.setHasFixedSize(true);
        RecyclerView.setLayoutManager(new GridLayoutManager(this,3));   // 3 items in a row
        Adapter = new tripCardAdapter(cardList,this);
        RecyclerView.setAdapter(Adapter);
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
