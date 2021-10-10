package com.example.personalfinancialmanagement;
import android.os.AsyncTask;
import android.util.Log;

import com.google.maps.GeoApiContext;
import com.google.maps.PlacesApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceType;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.RankBy;
import com.google.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NearbySearch {

    public void run(){
        Log.i("here url", "first place");
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl();

        NearbySearch.DownloadTask downloadTask = new NearbySearch.DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl() {
        // Origin of route
        String str_origin = "location=" + "3.068374"+ "," + "101.602486";
        // Radius
        String radius = "radius=3000";
        //types
        String rank = "rankby=distance";
        // Key
        String key = "key=" + "AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + radius + "&" + rank + "&" + key;
//        location=51.503186,-0.126446&radius=5000&types=train_station&key=AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM"
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters;
//        String url="https://maps.googleapis.com/maps/api/place/textsearch/json?query=new+york+city+point+of+interest&language=en&key="+key;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();

        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        //process data retrieved from doInBackground
        protected void onPostExecute(String result) {
            //parse place data returned from Google Places
            //remove existing markers
            try {
                //parse JSON

                //create JSONObject, pass stinrg returned from doInBackground
                JSONObject resultObject = new JSONObject(result);
                //get "results" array
                JSONArray placesArray = resultObject.getJSONArray("results");
                //marker options for each place returned

                Log.d("test", "The placesArray length is " + placesArray.length() + "...............");

                for (int p = 0; p < placesArray.length(); p++) {
                    //parse each place
                    //if any values are missing we won't show the marker
                    boolean missingValue = false;
                    LatLng placeLL = null;
                    String placeName = "";
                    String vicinity = "";

                    try {
                        //attempt to retrieve place data values
                        missingValue = false;
                        //get place at this index
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        //get location section
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        //read lat lng
                        placeLL = new LatLng(Double.valueOf(loc.getString("lat")),
                                Double.valueOf(loc.getString("lng")));
                        //get types
                        JSONArray types = placeObject.getJSONArray("types");
                        //loop through types

                        //vicinity
                        vicinity = placeObject.getString("vicinity");
                        //name
                        placeName = placeObject.getString("name");
                    } catch (JSONException jse) {
                        Log.v("PLACES", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
