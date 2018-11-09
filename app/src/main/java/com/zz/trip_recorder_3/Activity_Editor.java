package com.zz.trip_recorder_3;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.ArrayMap;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import com.zz.trip_recorder_3.tools.recorder_tools;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class Activity_Editor extends AppCompatActivity implements ItemList_img_choose.Listener {
    private enum mediaTypes{
        camera,
        video,
        audio,
    }
    private mediaTypes mediaType = mediaTypes.camera;

    private static String jsonFileStr = "";

    private int COUNT       = 0;
    private int parentID    = 0;
    private String unitID   = "";

    private static int insertId = 0;

    private static boolean isInsert = false;

    final private static String TAG = "thisOne";

    private String pickedDate;
    private int year,month,day;
    private EditText title;

    //private static int REQUEST_CODE=1;

    // for photo
    private static String CurrentPhotoPath;
    private static Uri CurrentPhotoUri;
    private static final int TAKE_PHOTO     = 1;
    private static final int PICK_IMAGE     = 2;
    private static final int RECORD_VIDEO   = 3;
    private static final int PICK_VIDEO     = 4;

    // set static title for bottom fragment
    final private String[] cameraTitle = {"Take a New Photo", "Open Galleary"};
    final private String[] videoTitle = {"Record a New Video", "Open Galleary"};

    private Map<Integer,String> position_content;
    private Map<String,EditText> textMap;
    private Map<String,Uri> imgMap;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__editor);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // get mapping for storing view elements added
        position_content = new ArrayMap<Integer, String>();
        textMap = new ArrayMap<String,EditText>();
        imgMap = new ArrayMap<String,Uri>();

        // get parent trip id for current trip unit; unit ID is set as parentID_dateString
        parentID = getIntent().getExtras().getInt("parentID");
        unitID = getIntent().getExtras().getString("unitID");

        // set title at first place
        title = findViewById(R.id.draft_title);
        title.setEnabled(false);
        if(unitID.equals("")){
            title.setText(staticGlobal.getTodayDate());
        }
        else{
            title.setText(unitID.substring(unitID.length()-10,unitID.length()));
        }

        // read old Json file
        jsonFileStr = staticGlobal.getJson(getApplication().getBaseContext(), staticGlobal.getTripJsonName(parentID));
        if(getIntent().getExtras().getBoolean("isEdit")){
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
                            if(!name.equals(unitID)) {
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
                                    addText("text "+Integer.toString(COUNT),innerValue,false);
                                }
                                else if(str[0].equals("img")){
                                    Uri u = Uri.parse(innerValue);
                                    addImg("img "+Integer.toString(COUNT),u);
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

            // add space at bottom
            LinearLayout linearLayout = findViewById(R.id.draft_lilayout);
            Space space = new Space(this);
            space.setMinimumHeight(120);
            space.setMinimumWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            linearLayout.addView(space,-1);
        }
        else {
            // get date picker and pick date for title
            Calendar currDate = Calendar.getInstance();
            year = currDate.get(Calendar.YEAR);
            month = currDate.get(Calendar.MONTH);
            day = currDate.get(Calendar.DAY_OF_MONTH);
            final DatePickerDialog datePickerDialog = new DatePickerDialog(Activity_Editor.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datepicker,
                                              int year, int month, int day) {
                            pickedDate = Integer.toString(year) +
                                    staticGlobal.paddingZero(month + 1) +
                                    staticGlobal.paddingZero(day);
                            title.setText(staticGlobal.niceDate(pickedDate));

                            unitID = Integer.toString(parentID) + "_" + title.getText().toString(); // unitID = 101_2018-01-01
                            try {
                                JSONObject oldJsonObject = new JSONObject(jsonFileStr);
                                if (oldJsonObject.has(unitID)) {
                                    createAlertDlg("Hey!",
                                            "You have a record on same date, do you still want to overwrite it?",
                                            "ok",
                                            "cancel");
                                }
                            } catch (JSONException e) {
                                Log.i(TAG, "Reading Json error (editor):" + e.toString());
                            }

                        }
                    },
                    year, month, day);
            datePickerDialog.show();
        }

        /** set all button clicks**/
        ImageView img1 = findViewById(R.id.imageView1);
        img1.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1){

            }
        });

        ImageView img2 = findViewById(R.id.imageView2);
        img2.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {

            }
        });

        ImageView img3 = findViewById(R.id.imageView3);
        img3.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                addText("text "+Integer.toString(COUNT),null,true); // addText itemName text 0
            }
        });

        ImageView img4 = findViewById(R.id.imageView4);
        img4.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                ItemList_img_choose.newInstance(2,cameraTitle, COUNT).show(getSupportFragmentManager(), "dialog");// new instance = 2 is 1.Camera 2.Gallery
                mediaType = mediaTypes.camera;
            }
        });

        ImageView img5 = findViewById(R.id.imageView5);
        img5.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                ItemList_img_choose.newInstance(2,videoTitle, COUNT).show(getSupportFragmentManager(), "dialog");
                mediaType = mediaTypes.video;
            }
        });

        Log.i(TAG, "onCreate complete");
    }

    /** 1. add text **/
    private void addText(final String itemName, String itemContent, boolean isEmpty){
        try{
        EditText newText = new EditText(Activity_Editor.this);
        if(!isEmpty)
            newText.setText(itemContent);
        newText.setTextIsSelectable(true);
        newText.setFocusableInTouchMode(true);
        newText.setFocusable(true);
        newText.requestFocus();
        newText.setBackgroundResource(R.drawable.edit_bg);
        newText.setSingleLine(false);
        newText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        newText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        newText.setLines(3);
        newText.setMaxLines(101);
        newText.setVerticalScrollBarEnabled(true);
        newText.setMovementMethod(ScrollingMovementMethod.getInstance());
        newText.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        LinearLayout linearLayout = findViewById(R.id.draft_lilayout);
        LinearLayout.LayoutParams thislayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.addView(newText,thislayout);
        textMap.put(itemName,newText);
        position_content.put(COUNT,itemName);

        // view separator, able to insert views
        ImageView separator = new ImageView(Activity_Editor.this);
        separator.setBackgroundResource(R.mipmap.separator);
        separator.setOnClickListener(new ImageView.OnClickListener(){
            @Override
            public void onClick(View v1) {
                //createDlgOnInsertion(insertionID);
            }
        });
        LinearLayout.LayoutParams slayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        linearLayout.addView(separator, slayout);


            Log.i(TAG, "New Text Box added as (global count "+ Integer.toString(COUNT)+": "+itemName);
        COUNT++;
        }
        catch (Exception e){
            Log.i(TAG,e.toString());
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /** 2. add images **/


    // Open camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error:" + ex.toString(), Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(Activity_Editor.this,
                        "com.zz.trip_recorder_3.fileprovider",
                        photoFile);
                CurrentPhotoUri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                //setResult(RESULT_OK,takePictureIntent);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
                Log.i(TAG,"New Photo Taken as:"+photoURI.toString());
            }
        }
    }
    // Save Img
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),staticGlobal.imgFolder);
        //File image = File.createTempFile( imageFileName,".jpg",storageDir);
        File image = new File(storageDir,imageFileName+".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        //this.CurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    // Add img to linear layout
/*    private void addImg(String itemName){
        ImageView newImg = new ImageView(Activity_Editor.this);
        Bitmap bmp = null;
        long beforeLoop=0;
        long inLoop = 0;
        try{
            BitmapFactory.Options op = new BitmapFactory.Options ();
            op.inSampleSize = 6;
            if (this.CurrentPhotoPath!=null) {
                File imgFile = new  File(this.CurrentPhotoPath);
                beforeLoop = System.currentTimeMillis();
                while(bmp == null){
                    bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), op);
                    inLoop = System.currentTimeMillis();
                    if((inLoop-beforeLoop)>5000) break;                             // if looping time>5sec break out the loop
                }
                newImg.setImageBitmap(bmp);
                //newImg.setMaxHeight(100);
                newImg.setAdjustViewBounds(true);
                newImg.setVisibility(View.VISIBLE);
            }
            else if (this.CurrentPhotoUri!=null){
                newImg.setImageURI(this.CurrentPhotoUri);
                newImg.setAdjustViewBounds(true);
                newImg.setVisibility(View.VISIBLE);
            }

            LinearLayout linearLayout = this.findViewById(R.id.draft_lilayout);
            LinearLayout.LayoutParams thislayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            linearLayout.addView(newImg, thislayout);
            imgMap.put(itemName, newImg);
            this.CurrentPhotoUri = null;
            this.CurrentPhotoPath = null;
        }catch (Exception e){
            //
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }*/

    private void addImg(final String itemName, Uri imgUri){
        try {
            if (imgUri != null) {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
                    grantUriPermission(imgUri);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
                if(w*h>=3000*2000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.2*bitmap.getWidth()),(int)(0.2*bitmap.getHeight()));
                }
                else if(w*h<=3000*2000 && w*h>=1500*1000){
                    bitmap = recorder_tools.getResizedBitmap(bitmap,(int)(0.5*bitmap.getWidth()),(int)(0.5*bitmap.getHeight()));
                }

                ImageView newImg = new ImageView(Activity_Editor.this);
                newImg.setImageBitmap(bitmap);
                LinearLayout linearLayout = this.findViewById(R.id.draft_lilayout);
                LinearLayout.LayoutParams thislayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 600);

                if(isInsert){
                    linearLayout.addView(newImg, 2*(insertId+1), thislayout);   // 2 * due to separator take odd number position, views are at even number position
                    createSeparator(itemName,2*(insertId+1)+1);
                    insertArrMap(insertId);
                    imgMap.put(itemName, imgUri);
                    position_content.put(insertId+1,itemName);
                }
                else{
                    linearLayout.addView(newImg, -1, thislayout);
                    createSeparator(itemName,-1);
                    imgMap.put(itemName, imgUri);
                    position_content.put(COUNT,itemName);
                }

                Log.i(TAG,"New Image Added as (global count "+ Integer.toString(COUNT)+": "+itemName);
                COUNT++;
                isInsert = false;
            }
        }catch (Exception e){
            Toast.makeText(this, "Error:" + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    // Open gallery
    private void openGallery(){
        //Intent intent = new Intent(Intent.ACTION_PICK);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent.setFlags(Intent
                .FLAG_GRANT_READ_URI_PERMISSION), "Select Picture"), PICK_IMAGE);
        Log.i(TAG,"Gallery Opened");
    }


    /** 3. add audio **/

    /** 4. add video **/

    /**create separator for each view added**/
    private void createSeparator(String itemName, int position){
        final String[] line = staticGlobal.parseViewItem(itemName);
        // view separator, able to insert views
        ImageView separator = new ImageView(Activity_Editor.this);
        separator.setOnClickListener(new ImageView.OnClickListener(){
            final int insertID = Integer.parseInt(line[1]); // id for Insertion marking
            @Override
            public void onClick(View v1) {
                createDlgOnInsertion(insertID);
            }
        });
        separator.setBackgroundResource(R.mipmap.separator);
        LinearLayout linearLayout = this.findViewById(R.id.draft_lilayout);
        LinearLayout.LayoutParams slayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50);
        linearLayout.addView(separator, position, slayout);
    }



    /**update all map when inserting views**/
    private void insertArrMap(int id){
        for(int j=COUNT;j>id;j--){
            EditText tv = textMap.remove("text "+Integer.toString(j));
            if(tv!=null){
                textMap.put("text "+Integer.toString(j+1),tv);
            }

            Uri u = imgMap.remove("img "+Integer.toString(j));
            if(u!=null){
                imgMap.put("img "+Integer.toString(j+1),u);
            }

            String s = position_content.remove(j);

            if(s!=null){
                position_content.put(j+1,s);
            }
        }
    }



    /**Detect clicks**/
    public void onItemClicked(int position, int id){
        insertId = id;
        switch (position){
            case 0:
                if(mediaType==mediaTypes.camera) dispatchTakePictureIntent();
                else if(mediaType==mediaTypes.video){}
                break;
            case 1:
                if(mediaType==mediaTypes.camera) openGallery();
                else if(mediaType==mediaTypes.video){}
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) { return; }
        switch (requestCode){
            case TAKE_PHOTO:
                addImg("img "+Integer.toString(insertId+1),CurrentPhotoUri);    // addImg itemName img 0
                break;
            case PICK_IMAGE:
                CurrentPhotoUri = staticGlobal.getImageUrlWithAuthority(getApplication().getBaseContext(),data.getData());
                //CurrentPhotoUri = staticGlobal.getImageUrlWithAuthority(getApplication().getBaseContext(), data.getData());
                //CurrentPhotoUri = data.getData();
                addImg("img "+Integer.toString(insertId+1),CurrentPhotoUri);    // addImg itemName img 0
                break;
        }
    }

    @Override
    protected void onResume(){
        Log.i(TAG,"on Resume editor");
        super.onResume();
    }

    @Override
    protected void onStop(){

        Log.i(TAG,"onStop editor");
        super.onStop();
    }

    @Override
    protected void onPause(){
        Log.i(TAG,"on pause editor");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "on Destroy editor");
    }

    @Override
    public void onBackPressed(){
        createQuitDlg("Just a Second","Do you want to save the record you just type in?");
    }

    private void createDlgOnInsertion(int id){
        final int para = id;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Insert")
                .setItems(R.array.editor_insertion_select, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:

                                break;
                            case 1:
                                ItemList_img_choose.newInstance(2,cameraTitle, para).show(getSupportFragmentManager(), "dialog");// new instance = 2 is 1.Camera 2.Gallery
                                mediaType = mediaTypes.camera;
                                isInsert = true;
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    // create quit alert box: save, leave without saving, cancel
    private void createQuitDlg(String title, String message){
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Save",null)
                .setNeutralButton("Cancel",null)
                .setNegativeButton("Leave without saving",null)
                .create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button saveButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                Button cancelButton = (alertDialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                Button leaveButton = (alertDialog).getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        save();
                        finish();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                leaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        });
        alertDialog.show();
    }

    // create ok-cancel alert box
    private void createAlertDlg(String title, String message, String Y, String N){
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
                        alertDialog.dismiss();
                    }
                });
                noButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        finish();
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void save(){
        TextView tv;
        Uri imgUri = null;
        String itemName;
        String[] line;

        FileOutputStream outputStream;

        JSONStringer jsonStringer = new JSONStringer();
        JSONObject newJsonObject;

        //unitID = Integer.toString(parentID) + "_" + title.getText().toString();
        try {
            jsonStringer.object();
            jsonStringer.key("item count");
            jsonStringer.value(position_content.size());
            for(int i=0;i<=COUNT;i++){
                if (position_content.containsKey(i)) {
                    itemName = (String) position_content.get(i);
                    line = staticGlobal.parseViewItem(itemName);
                    switch (line[0]){
                        case "text":
                            jsonStringer.key(itemName);
                            tv = (TextView) textMap.get(itemName);
                            jsonStringer.value(tv.getText().toString());
                            break;
                        case "img":
                            jsonStringer.key(itemName);
                            imgUri = (Uri) imgMap.get(itemName);
                            jsonStringer.value(imgUri.toString());
                            break;
                    }
                }
            }

            // generate background for whole package display
            String unit_bg = "";
            if(imgMap.size()>0 && imgUri!=null){
                jsonStringer.key("unit_bg");
                unit_bg = imgUri.toString();
                jsonStringer.value(unit_bg);
            }

            jsonStringer.endObject();
            newJsonObject = new JSONObject(jsonFileStr);
            unitID = Integer.toString(parentID) + "_" + title.getText().toString(); // unitID = 101_2018-01-01
            staticGlobal.setCurrEditorID(unitID);
            staticGlobal.setCurrShowingTripID(parentID);
            if(!newJsonObject.has(unitID))
                newJsonObject.put("trip count",newJsonObject.getInt("trip count")+1);
            newJsonObject.put(unitID,new JSONObject(jsonStringer.toString()));
            if(!unit_bg.equals("")){
                newJsonObject.put("trip_bg",unit_bg);
            }
            outputStream = openFileOutput(staticGlobal.getTripJsonName(parentID), Context.MODE_PRIVATE);
            outputStream.write(newJsonObject.toString().getBytes());
            Log.i(TAG, "New saved Json: " + newJsonObject.toString(1));
            outputStream.close();
            COUNT = 0;
        }catch (Exception e){
            Log.i(TAG,e.toString());
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
