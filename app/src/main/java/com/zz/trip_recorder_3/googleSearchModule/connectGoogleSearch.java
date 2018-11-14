package com.zz.trip_recorder_3.googleSearchModule;

import android.util.Log;

import com.zz.trip_recorder_3.tools.HttpGetter_tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class connectGoogleSearch {
    private static final String TAG = "thisOne";
    private static final String BaseAPIURL = "https://www.googleapis.com/customsearch/v1";
    private static final String APIkey = "AIzaSyC-trr122rhVUEn5K8aDXJtziJAQWdMGJw";
    private static final String cxID = "017569750213297149046:mpdrfmbypk0";

    private String httpResponse;
    private String searchString;
    private int resultIdx=0;
    private String title;
    private String url;
    private boolean success;

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public connectGoogleSearch(String searchString, int resultIdx){
        this.searchString = searchString;
        this.resultIdx = resultIdx;
        this.httpResponse = callGoogleAPI(this.searchString);
        try {
            this.success = parseResponse(this.httpResponse);
        } catch (JSONException e) {
            Log.i(TAG,"connectGoogleSearch" + e.toString());
        }
    }

    private String callGoogleAPI(String searchStr) {
        // Sorted by view count
        String url = BaseAPIURL + "?searchType=image&num=10&imgType=photo&start=1&q="+searchStr+"&key="+APIkey+"&cx="+cxID;
        Log.i(TAG,"callGoogleAPI on " + url);
        try {
            String response  = HttpGetter_tools.doGet(url);
            return response;
        } catch (Exception e) {
            Log.i(TAG,"callGoogleAPI: " + e.toString());
            return null;
        }
    }

    private boolean parseResponse(String response) throws JSONException{
        Log.i(TAG, "parseResponse connectGoogleSearch");
        JSONObject json = new JSONObject(response);
        JSONArray items = json.getJSONArray("items");
        JSONObject item = items.getJSONObject(resultIdx);
        String title = item.getString("title");
        String resultUrl = item.getString("link");
        //JSONObject image = item.getJSONObject("image");


        if(resultUrl!=null && title!=null){
            this.url = resultUrl;
            this.title = title;
            return true;
        }
        return false;
    }
}
