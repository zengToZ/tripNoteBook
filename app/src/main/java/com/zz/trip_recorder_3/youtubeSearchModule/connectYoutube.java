package com.zz.trip_recorder_3.youtubeSearchModule;


        import com.zz.trip_recorder_3.tools.HttpGetter_tools;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

public class connectYoutube {
    private static String ZYouBaseApi = "https://www.googleapis.com/youtube/v3/search";
    private String ZYouWatchURL = "https://www.youtube.com/watch?v=";
    private String ZYouAPIKey = "AIzaSyC-trr122rhVUEn5K8aDXJtziJAQWdMGJw";

    private String httpResponse;
    private String searchString;

    private String url;
    private String title;
    private String description;

    private boolean success;

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean getSuccess(){
        return this.success;
    }

    public connectYoutube(String searchString){
        this.searchString = searchString;
        this.httpResponse = makeAPICall(this.searchString);
        try {
            this.success = parseResponse(this.httpResponse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private String makeAPICall(String query) {
        // Sorted by view count
        String url = ZYouBaseApi + "?part=snippet&maxResults=10&order=Relevance&q="+query+"&key="+ZYouAPIKey;

        System.out.println(url);
        try {
            String response  = HttpGetter_tools.doGet(url);
            return response;

        } catch (Exception e) {

            e.printStackTrace();
            return null;
        }
    }

    private boolean parseResponse(String response) throws JSONException{
        JSONObject json = new JSONObject(response);

        JSONArray items = json.getJSONArray("items");
        JSONObject jObj = items.getJSONObject(0); // first result, loop on items.length to get all result

        // Video Id and youtube link
        JSONObject id = jObj.getJSONObject("id");
        String videoId = id.getString("videoId");
        String URL = ZYouWatchURL + videoId;

        // Title and Description
        JSONObject snippet = jObj.getJSONObject("snippet");
        String title = snippet.getString("title");
        String description = snippet.getString("description");

        if ((!URL.equals(ZYouWatchURL)) && (title!=null) && (description!=null))
        {
            this.url = URL;
            this.title = title;
            this.description = description;
            return true;
        }
        return false;
    }
}
