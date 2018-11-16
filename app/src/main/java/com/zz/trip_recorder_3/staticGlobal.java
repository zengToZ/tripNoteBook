package com.zz.trip_recorder_3;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zz.trip_recorder_3.tools.iniHelper_tools;

import org.json.JSONObject;
import org.json.JSONStringer;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class staticGlobal {
    public static Context context;
    public static File fDir;

    public final static int MAX_CITY_IMG_DL = 10;
    private final static String CITY_JSON_PATH = "CITY_JSON.json";
    public final static String APP_NAME = "zz_trip_notebook";

    private static valueModifiedListener modifiedListener;

    //private static File iniFile = new File(Environment.getExternalStorageDirectory().toString()+"/trip_recorder_setting.ini");
    private final static File iniFile = new File(fDir, "settings.tn");

    private final static String TAG = "thisOne";

    public final static String imgFolder = "tripNoteBook";

    /*
    * ini attribute:
    * under "Global setting"
    * isNewTripOpen - when open a new trip it is set true
    * tripCount - how many trips
    * currentTripID - current active trip id before next new trip created, start with 101
    * currentShownTripID - current active trip id when last trip unit is saved
    * currentEditorID - current active trip unit id (edited and closed last time, not finished)
    *
     */

    public static String getJson(Context context, String fileName){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream file = context.openFileInput(fileName);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BufferedInputStream(file)));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            Log.i(TAG, "getJson error:"+e.toString());
        }
        return stringBuilder.toString();
    }

    public static JsonReader getjsonReader (Context context, String fileName)throws IOException{
        FileInputStream file = context.openFileInput(fileName);
        return new JsonReader(new InputStreamReader(new BufferedInputStream(file)));
    }

    public static String getTripJsonName(int tripID) {
        return "tn_"+Integer.toString(tripID)+".json";
    }

    // convert 1 digit 0-9 to 00-09
    public static String paddingZero(int i){
        if((i>=0)&&(i<=9)){
            return "0"+Integer.toString(i);
        }
        else return Integer.toString(i);
    }

    // get today date return String 2018-01-01
    public static String  getTodayDateStr(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(today);
        return formattedDate;
    }

    // nice date shown in title, input should be 8digit like 20180101, converted to 2018-01-01

    public static String niceDate(@NonNull String date){
        if(date.length()!=8) return "";
        else{
            return date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
        }
    }

    // even nicer date shown as
    public static String beautifulDate(Date date, boolean isToday){
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd YYYY EEEE");
        if(isToday){
            return df.format(Calendar.getInstance().getTime());
        }
        else {
            if (date != null) return df.format(date);
            else return df.format(Calendar.getInstance().getTime());
        }
    }

    // parse view item (text view, image view..), named after (text 1, img 2..)
    public static String[] parseViewItem(String itemname){
        int i=0,l=itemname.length();
        StringBuilder s = new StringBuilder();
        for(char c:itemname.toCharArray()){
            i++;
            if (c==' ') break;
            s.append(c);
        }
        String[] result = {s.toString(),itemname.substring(i,l)};
        return result;
    }

    // region: initialize file part...:
    public static void initializeIniFile() {
        try{
            File ff = context.getFileStreamPath(iniFile.getName());
            if(!ff.exists()) {
                iniHelper_tools currentIniFile = new iniHelper_tools(iniFile,context);
                currentIniFile.setLineSeparator("|");
                currentIniFile.set("Global Setting","userName", ""); // initialized with empty string
                currentIniFile.set("Global Setting","cityName", ""); // save current location
                currentIniFile.set("Global Setting","stateName", ""); // save current location
                currentIniFile.set("Global Setting","countryName", ""); // save current location
                currentIniFile.set("Global Setting","isNewTripOpen","false");
                currentIniFile.set("Global Setting","tripCount",0);
                currentIniFile.set("Global Setting","currentTripID",100); // start with 101
                currentIniFile.set("Global Setting","currentShownTripID",-1); //currentShownTripID
                currentIniFile.set("Global Setting","currentEditorID","");  // current editting one
                currentIniFile.save();
            }
        }catch (Exception e){
            Log.i(TAG,e.toString());
        }
    }

    public static boolean deleteIniFile() throws IOException{
        File ff = context.getFileStreamPath(iniFile.getName());
        if(ff.exists()) {
            context.deleteFile(iniFile.getName());
            return true;
        }
        else return false;
    }

    public static boolean isNewTripOpened() throws IOException{
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return Boolean.parseBoolean((String) INI.get("Global Setting", "isNewTripOpen"));
    }

    public static void setIsNewTripOpened(String bool){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        INI.setLineSeparator("|");
        if ((bool.equals("True")) || (bool.equals("true")) || (bool.equals("TRUE"))) {
            INI.set("Global Setting","isNewTripOpen","true");
        }
        else if((bool.equals("False")) || (bool.equals("false")) || (bool.equals("FALSE"))){
            INI.set("Global Setting","isNewTripOpen","false");
        }
        INI.save();
    }

    public static void addTripCount(int c){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        int oc = Integer.parseInt((String) INI.get("Global Setting", "tripCount"));
        int currID = Integer.parseInt((String)INI.get("Global Setting","currentTripID"));
        oc = oc + c;
        if(c>=0)
            currID = currID + c;
        INI.setLineSeparator("|");
        INI.set("Global Setting","tripCount",(Object)oc);
        INI.set("Global Setting","currentTripID",(Object)currID);
        INI.save();
    }
     public static int getTripCount(){
         iniHelper_tools INI = new iniHelper_tools(iniFile, context);
         return Integer.parseInt((String)INI.get("Global Setting","tripCount"));
     }

    public static int getCurrTripID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        return Integer.parseInt((String)INI.get("Global Setting","currentTripID"));
    }

    public static void setCurrShowingTripID(int i){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","currentShownTripID",i);
        INI.save();
    }

    public static int getCurrShowingTripID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        return Integer.parseInt((String) INI.get("Global Setting","currentShownTripID"));
    }

    public static void setCurrEditorID(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","currentEditorID",s);
        INI.save();
    }

    public static String getCurrEditorID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return (String)INI.get("Global Setting","currentEditorID");
    }

    public static void setUserName(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","userName",s);
        INI.save();
        if(modifiedListener != null) modifiedListener.onModified_01();
    }

    public static String getUserName(){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return (String)INI.get("Global Setting","userName");
    }

    public static void setCityName(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","cityName",s);
        INI.save();
    }

    public static String getCityName(){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return (String)INI.get("Global Setting","cityName");
    }

    public static void setStateName(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","stateName",s);
        INI.save();
    }

    public static String getStateName(){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return (String)INI.get("Global Setting","stateName");
    }

    public static void setCountryName(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile,context);
        INI.setLineSeparator("|");
        INI.set("Global Setting","countryName",s);
        INI.save();
    }

    public static String getCountryName(){
        iniHelper_tools INI = new iniHelper_tools(iniFile, context);
        return (String)INI.get("Global Setting","countryName");
    }

    public static Uri getImageUrlWithAuthority(Context context, Uri uri){
        InputStream is = null;
        //OutputStream os = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp, uri.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage, String uriName) {
        File file = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+staticGlobal.imgFolder,
                uriName.substring(uriName.length()-10,uriName.length())+".jpg");
        if(!file.exists()){
            try{
                file.createNewFile();

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                byte[] bitmapData = bytes.toByteArray();

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapData);
                fos.flush();
                fos.close();
                bytes.flush();
                bytes.close();
            }catch (Exception e){
                Log.i(TAG, "writeToTempImageAndGetPathUri: "+ e.toString());
            }
        }
        Uri photoURI = FileProvider.getUriForFile(inContext,
                "com.zz.trip_recorder_3.fileprovider",
                file);  // saved to Pictures/tripNotebook
        return photoURI;
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return status == ConnectionResult.SUCCESS;
    }

    public static void downloadToExtFromURL(Context context, String url, String fileName) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //request.setTitle("");
        request.allowScanningByMediaScanner();
        //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,Environment.DIRECTORY_PICTURES,fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    public static void createCityJson(Context context){
        JSONObject newJson = new JSONObject();
        File file = new File(context.getApplicationContext().getFilesDir(), staticGlobal.CITY_JSON_PATH);
        try {
            if(!file.exists()){
                FileOutputStream outputStream = context.openFileOutput(staticGlobal.CITY_JSON_PATH, Context.MODE_PRIVATE);
                outputStream.write(newJson.toString().getBytes());
                Log.i(TAG,newJson.toString());
                outputStream.close();
            }
        }catch (Exception e){
            Log.i(TAG,"createCityJson: "+e.toString());
        }
    }

    public static void writeCityJson(Context context, String cityName, String[] inputStr){
        try{
            String jsonStr = staticGlobal.getJson(context.getApplicationContext(), staticGlobal.CITY_JSON_PATH);
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONStringer JsonStringer = new JSONStringer();
            JsonStringer.object();
            for(int i=0; i<staticGlobal.MAX_CITY_IMG_DL; i++){
                JsonStringer.key(Integer.toString(i));
                JsonStringer.value(inputStr[i]);
            }
            JsonStringer.endObject();
            jsonObject.put(cityName,new JSONObject(JsonStringer.toString()));

            FileOutputStream outputStream = context.openFileOutput(staticGlobal.CITY_JSON_PATH, Context.MODE_PRIVATE);
            outputStream.write(jsonObject.toString().getBytes());
        }catch (Exception e){
            Log.i(TAG,"writeCityJson: "+e.toString());
        }
    }

    public static String[] readCityJson(Context context, String cityName){
        String[] output = new String[staticGlobal.MAX_CITY_IMG_DL];
        try{
            JsonReader jsonReader = staticGlobal.getjsonReader(context.getApplicationContext(), staticGlobal.CITY_JSON_PATH);
            String CityName="";
            int i=0;
            jsonReader.beginObject();
            while(jsonReader.hasNext()) {
                JsonToken nextToken = jsonReader.peek();
                if(JsonToken.NAME.equals(nextToken)) {
                    CityName = jsonReader.nextName();
                    Log.i(TAG, CityName);
                }
                else if(JsonToken.BEGIN_OBJECT.equals(nextToken)){
                    jsonReader.beginObject();
                    if(!CityName.equals(cityName)){
                        jsonReader.skipValue();
                        continue;
                    }
                    while (jsonReader.hasNext()) {
                        JsonToken innerToken = jsonReader.peek();
                        if(JsonToken.NAME.equals(innerToken)){
                            i = Integer.parseInt(jsonReader.nextName());
                        }
                        else if(JsonToken.STRING.equals(innerToken)){
                            output[i] =  jsonReader.nextString();
                            Log.i(TAG,output[i]);
                        }
                        else {
                            jsonReader.skipValue();
                        }
                    }
                }
                else{
                    jsonReader.skipValue();
                }
            }

        }catch (Exception e){
            Log.i(TAG,"readCityJson: "+e.toString());
        }
            return output;
    }

    public static Locale getAppLocale(Context context){
        final SharedPreferences settings = context.getApplicationContext().getSharedPreferences(staticGlobal.APP_NAME, 0);
        String checked = settings.getString("langSet", "EN");
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if(checked == "EN"){
            setAppLocale(resources,configuration,Locale.US,context,displayMetrics);
            return Locale.US;
        }
        else if(checked == "CH"){
            setAppLocale(resources,configuration,Locale.US,context,displayMetrics);
            return Locale.CHINA;
        }
        else{
            setAppLocale(resources,configuration,Locale.US,context,displayMetrics);
            return Locale.US;
        }
    }
    private static void setAppLocale(Resources resources, Configuration configuration, Locale locale, Context context, DisplayMetrics displayMetrics){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
            resources.updateConfiguration(configuration,displayMetrics);
        } else{
            configuration.locale=locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            context.getApplicationContext().createConfigurationContext(configuration);
        } else {
            resources.updateConfiguration(configuration,displayMetrics);
        }
    }

    public static void setValueModifiedListener(valueModifiedListener valueModifiedListener){
        modifiedListener = valueModifiedListener;
    }

    public interface valueModifiedListener{
        void onModified_01();
    }

/*
    // get Uri localized
    public static Uri getImageUriLocalized(Context context, Uri uri){
        FileChannel source = null;
        FileChannel destination = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp +"_tmp.jpg";
        if(uri.getAuthority()!=null){
            try{
                File sFile = new File(getRealPathFromURI(context,uri));
                File dFile = new File(getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+"/"+imgFolder,imageFileName);

                if(!dFile.exists()){
                    dFile.createNewFile();
                }

                source = new FileInputStream(sFile).getChannel();
                destination = new FileOutputStream(dFile).getChannel();

                destination.transferFrom(source,0,source.size());

                return Uri.parse(dFile.getPath());
            }catch (Exception e){
                Log.i(TAG, "static global get uri(1):"+e.toString());
            }finally {
                try{
                    source.close();
                    destination.close();
                }catch (Exception e){
                    Log.i(TAG, "static global get uri(2):"+e.toString());
                }
            }
        }
        return null;
    }

    private static String getRealPathFromURI(Context context, Uri contentUri) {

        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static File bmpToFile(){
        FileOutputStream fos = null;
        try {
            //create a file to write bitmap data
            File f = new File(context.getCacheDir(), filename);
            f.createNewFile();

            //Convert bitmap to byte array
            Bitmap bitmap = your bitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*//*, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            fos = new FileOutputStream(f);
            fos.write(bitmapdata);

        }catch (IOException e){
            Log.i(TAG,"static global bmp to file(1): "+e.toString());
        }
        finally {
            try{
            fos.flush();
            fos.close();
            }catch (Exception e){
                Log.i(TAG,"static global bmp to file(2): "+e.toString());
            }

        }
    }
*/


}
