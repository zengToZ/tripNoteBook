package com.zz.trip_recorder_3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zz.trip_recorder_3.adapter.tripCardAdapter;
import com.zz.trip_recorder_3.data_models.tripCardModel;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Activity_Triplist extends AppCompatActivity {
    final private static String TAG = "thisOne";

    private int id = -1;
    private boolean doDel = false;
    private static String createNewTripName;

    private RecyclerView RecyclerView;
    private RecyclerView.Adapter Adapter;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__triplist);
        actionBar = getSupportActionBar();

        // create new trip json file if received "isNew = true"
        if(getIntent().getBooleanExtra("isNew",false)) {
            id = getIntent().getExtras().getInt("newTripID");
            createNewTripName = getIntent().getExtras().getString("createNewTripName");
            FileOutputStream outputStream;
            JSONObject newTripJson = new JSONObject();
            String filename = staticGlobal.getTripJsonName(id);
            String fileContents = "";
            File file = new File(getApplication().getBaseContext().getFilesDir(), filename);
            try {
                newTripJson.put("trip id", id);
                newTripJson.put("trip count",0);
                newTripJson.put("trip name",createNewTripName);
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
        String unitID = null;
        Log.i(TAG,"on Resume trip list");
        super.onResume();

        List<tripCardModel> cardList = new ArrayList();

        if(id<0){
            id = getIntent().getExtras().getInt("tripPackgeID");
        }

        if(getIntent().getBooleanExtra("doDel",false)){
            //unitID = getIntent().getStringExtra("unitID");
            doDel = true;
        }

        if(getIntent().getBooleanExtra("doneDel",false)){
            unitID = getIntent().getStringExtra("unitID");
            createAlertDlg("DELETE","Delete Note " + unitID, "Delete","Cancel",unitID);
            return;
        }

        File file = new File(getApplication().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(id));
        if(file.exists()){
            //String jsonStr = staticGlobal.getJson(getApplication().getBaseContext(),staticGlobal.getTripJsonName(id));
            try {
                // unitID: 101_2018-01-01
                /**Json format
                 * Json sample:
                 * "{\"trip id\":103,
                 * \"trip count\":0,
                 * \"trip title\": Locale.. + Sunday, 2018, Jan, 01
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
                        t.doDelete = doDel;
                        if(!innerValue.equals("")){
                            t.background = Uri.parse(innerValue);
                            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                                grantUriPermission(t.background);
                            innerValue = "";
                        }
                        if(name.length()>=14)
                            t.dateTitle = name.substring(name.length()-10,name.length());
                        cardList.add(t);
                        jsonReader.endObject();
                    }
                    else if(JsonToken.STRING.equals(nextToken)){
                        String value =  jsonReader.nextString();
                        if(name.equals("trip name")){
                            if (actionBar!=null){
                                actionBar.setDisplayHomeAsUpEnabled(true);
                                actionBar.setTitle(value);
                            }
                        }
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

        if(doDel){

            if(cardList.size()==0)
                onBackPressed();
            Button back_btn = findViewById(R.id.triplist_back);
            back_btn.setVisibility(View.VISIBLE);
            back_btn.setBackgroundResource(android.R.color.transparent);
            back_btn.setText("BACK");
            try {
                back_btn.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v1) {
                        //onBackPressed();
                        finish();
                    }
                });
            }catch (Exception e){
                Log.i(TAG,e.toString());
            }
        }
        else {
            // create new unit button click
            tripCardModel btn = new tripCardModel();
            //btn.background =
            btn.creNew = true;
            btn.parentID = id;
            btn.id = "";    // set to "" for recognizing it is a plus sign button
            cardList.add(btn);
        }

        RecyclerView = (RecyclerView) findViewById(R.id.tripunit_list);
        RecyclerView.setHasFixedSize(true);
        RecyclerView.setLayoutManager(new GridLayoutManager(this,3));   // 3 items in a row
        Adapter = new tripCardAdapter(cardList,this);
        RecyclerView.setAdapter(Adapter);
    }

    private void onDelete(String unitID){
        File file = new File(getApplication().getBaseContext().getFilesDir(), staticGlobal.getTripJsonName(id));
        if(file.exists()){
            try {
                String jsonStr = staticGlobal.getJson(getApplication().getBaseContext(),staticGlobal.getTripJsonName(id));
                JSONObject jOb = new JSONObject(jsonStr);
                if(staticGlobal.getCurrEditorID().equals(unitID)){
                    staticGlobal.setCurrEditorID("");
                    jOb.remove("trip_bg");
                }
                jOb.remove(unitID);
                FileOutputStream outputStream = openFileOutput(staticGlobal.getTripJsonName(id), Context.MODE_PRIVATE);
                outputStream.write(jOb.toString().getBytes());
                outputStream.close();
                doDel = false;
            }catch (Exception e){
                Log.i(TAG, "error when delete unit from trip package jason file(trip_list):"+e.toString());
            }
        }
    }

    // create ok-cancel alert box
    private void createAlertDlg(String title, String message, String Y, String N, String unitID){
        final String delID = unitID;
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(Y,null)
                .setNegativeButton(N,null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button yesButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button noButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                yesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onDelete(delID);
                        //onBackPressed();
                        finish();
                        alertDialog.dismiss();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //onBackPressed();
                        finish();
                        alertDialog.dismiss();
                    }
                });
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onStop(){
        Log.i(TAG,"onStop trip list");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.i(TAG,"on pause trip list");
        super.onPause();
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
