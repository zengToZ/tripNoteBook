package com.zz.trip_recorder_3;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.zz.trip_recorder_3.tools.iniHelper_tools;

public class staticGlobal {
    private static File iniFile = new File(Environment.getExternalStorageDirectory().toString()+"/trip_recorder_setting.ini");
    final private static String TAG = "thisOne";

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

    // "content://com.android.providers.media.documents/document/image%3A717724"
    final public static String defaultImgStr = "content://com.android.providers.media.documents/document/image%3A717724";

    public static boolean needUpdate_triplist = true;

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
        return "tr"+Integer.toString(tripID)+".json";
    }

    // convert 1 digit 0-9 to 00-09
    public static String paddingZero(int i){
        if((i>=0)&&(i<=9)){
            return "0"+Integer.toString(i);
        }
        else return Integer.toString(i);
    }

    // nice date shown in title, input should be 8digit like 20180101, converted to 2018-01-01
    @NonNull
    public static String niceDate(String date){
        if(date.length()!=8) return "";
        else{
            return date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
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

    public static void initializeIniFile() {
        try{
            if(!iniFile.exists()) {
                if (iniFile.createNewFile()){
                    iniHelper_tools currentIniFile = new iniHelper_tools(iniFile);
                    currentIniFile.setLineSeparator("|");
                    currentIniFile.set("Global Setting","isNewTripOpen","false");
                    currentIniFile.set("Global Setting","tripCount",0);
                    currentIniFile.set("Global Setting","currentTripID",100); // start with 101
                    currentIniFile.set("Global Setting","currentShownTripID",-1); //currentShownTripID
                    currentIniFile.set("Global Setting","currentEditorID","");  // current editting one
                    currentIniFile.save();
                }
            }
        }catch (IOException e){
            Log.i(TAG,e.toString());
        }
    }

    public static boolean deleteIniFile() throws IOException{
        if (iniFile.exists()){
            iniFile.delete();
            return true;
        }
        else return false;
    }

    // get today date return String
    public static String  getTodayDate(){
        Date today = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(today);
        return formattedDate;
    }

    public static boolean isNewTripOpened() throws IOException{
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        return Boolean.parseBoolean((String) INI.get("Global Setting", "isNewTripOpen"));
    }

    public static void setIsNewTripOpened(String bool){
        if (iniFile.exists()) {
            iniHelper_tools INI = new iniHelper_tools(iniFile);
            INI.setLineSeparator("|");
            if ((bool.equals("True")) || (bool.equals("true")) || (bool.equals("TRUE"))) {
                INI.set("Global Setting","isNewTripOpen","true");
            }
            else if((bool.equals("False")) || (bool.equals("false")) || (bool.equals("FALSE"))){
                INI.set("Global Setting","isNewTripOpen","false");
            }
            INI.save();
        }
    }

    public static void addTripCount(int c){
        if (iniFile.exists()) {
            iniHelper_tools INI = new iniHelper_tools(iniFile);
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
    }
     public static int getTripCount(){
         iniHelper_tools INI = new iniHelper_tools(iniFile);
         return Integer.parseInt((String)INI.get("Global Setting","tripCount"));
     }

    public static int getCurrTripID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        return Integer.parseInt((String)INI.get("Global Setting","currentTripID"));
    }

    public static void setCurrShowingTripID(int i){
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        INI.setLineSeparator("|");
        INI.set("Global Setting","currentShownTripID",i);
        INI.save();
    }

    public static int getCurrShowingTripID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        return Integer.parseInt((String) INI.get("Global Setting","currentShownTripID"));
    }

    public static void setCurrEditorID(String s){
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        INI.setLineSeparator("|");
        INI.set("Global Setting","currentEditorID",s);
        INI.save();
    }

    public static String getCurrEditorID(){
        iniHelper_tools INI = new iniHelper_tools(iniFile);
        return (String)INI.get("Global Setting","currentEditorID");
    }

}
