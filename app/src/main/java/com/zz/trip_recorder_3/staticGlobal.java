package com.zz.trip_recorder_3;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

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
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.zz.trip_recorder_3.tools.iniHelper_tools;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class staticGlobal {
    public static Context context;
    public static File fDir;
    //private static File iniFile = new File(Environment.getExternalStorageDirectory().toString()+"/trip_recorder_setting.ini");
    private final static File iniFile = new File(fDir, "settings.tn");

    private final static String TAG = "thisOne";

    public final static String imgFolder = "tripNoteBook";

    // The minimum distance to change Updates in meters
    public static final long MIN_DISTANCE_UPDATES = 20; //  meters
    public static final long URGENT_DISTANCE_UPDATES = (long)0.2; //  meters

    // The minimum time between updates in milliseconds
    public static final long MIN_TIME_UPDATES = 1000 * 60 * 60; // milliseconds - 1 hour
    public static final long URGENT_TIME_UPDATES = 1000; // milliseconds - 1 sec

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

    // region: initilize file part...:
    public static void initializeIniFile() {
        try{
            File ff = context.getFileStreamPath(iniFile.getName());
            if(!ff.exists()) {
                iniHelper_tools currentIniFile = new iniHelper_tools(iniFile,context);
                currentIniFile.setLineSeparator("|");
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

    public static Uri getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        OutputStream os = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
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

    private static Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public static boolean isGooglePlayServicesAvailable(Context context) {
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        return status == ConnectionResult.SUCCESS;
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
