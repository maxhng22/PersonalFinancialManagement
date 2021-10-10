package com.example.personalfinancialmanagement;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.security.Timestamp;
import java.util.Date;

public class myService extends Service {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference ref;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 40000;
    private static final float LOCATION_DISTANCE = 100f;
    private double your_Longitude=0.0;
    private double your_Latitude=0.0;
//    private static Context myService;

    Alarm alarm = new Alarm();
    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);

            your_Latitude=location.getLatitude();
            your_Longitude=location.getLongitude();

            getNearbyPlace(location);
            mLastLocation.set(location);

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    public void updateLocation(double  lati, double longti){
//        if(HomeActivity.homeActivity!=null){
//            HomeActivity.homeActivity.setLocation(location.getLatitude(),location.getLongitude());
//        }
        ref.child(user.getUid()).child("Last_Visited").child("latitude").setValue(lati);
        ref.child(user.getUid()).child("Last_Visited").child("longitude").setValue(longti);
        Date date = new Date();
        ref.child(user.getUid()).child("Last_Visited").child("time").setValue(date);
    }

    public void getNearbyPlace(Location location){
//        ShowNotification();
        Log.i("here url", "first place");
        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(location);

        myService.DownloadTask downloadTask = new myService.DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    private String getDirectionsUrl(Location location) {
        // Origin of route
        String str_origin = "location=" + location.getLatitude()+ "," + location.getLongitude();
        // Radius
        String radius = "radius=3000";
        //types
//        String mode = "types=tourist_attraction";
        // Key
        String key = "key=" + "AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + radius + "&" + key;
//        location=51.503186,-0.126446&radius=5000&types=train_station&key=AIzaSyCcgAmTUTcrnaQb5BCFGr8lW0YwIIOjlMM"
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + parameters;
//        String url="https://maps.googleapis.com/maps/api/place/textsearch/json?query=new+york+city+point+of+interest&language=en&key="+key;
        Log.i("url",url);
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

                boolean status=true;
                int p = 0;
                do {
                    boolean missingValue = false;
                    LatLng placeLL = null;
                    try {
                        //attempt to retrieve place data values
                        missingValue = false;
                        //get place at this index
                        JSONObject placeObject = placesArray.getJSONObject(p);
                        //get location section
                        JSONObject loc = placeObject.getJSONObject("geometry")
                                .getJSONObject("location");
                        //read lat lng
                        placeLL = new LatLng(Double.parseDouble(loc.getString("lat")),
                                Double.parseDouble(loc.getString("lng")));
                        float[] dist = new float[1];
                        Location.distanceBetween(your_Latitude,your_Longitude,Double.parseDouble(loc.getString("lat")), Double.parseDouble(loc.getString("lng")),dist);

                        if(dist[0] < 100){
                            status=false;
                            ShowNotification();
                            Log.v("activate", "missing here here lol lol lol ");
                            alarm.setAlarm(getApplicationContext());
                            updateLocation(Double.parseDouble(loc.getString("lat")),Double.parseDouble(loc.getString("lng")));
                        }
                    } catch (JSONException jse) {
                        Log.v("PLACES", "missing value");
                        missingValue = true;
                        jse.printStackTrace();
                    }

                    p++;
                }
                while (p < placesArray.length()&&status);

//                alarm.setAlarm(getApplicationContext())
//                for (int p = 0; p < placesArray.length(); p++) {
//                    boolean missingValue = false;
//                    LatLng placeLL = null;
//
//
//                    try {
//                        //attempt to retrieve place data values
//                        missingValue = false;
//                        //get place at this index
//                        JSONObject placeObject = placesArray.getJSONObject(p);
//                        //get location section
//                        JSONObject loc = placeObject.getJSONObject("geometry")
//                                .getJSONObject("location");
//                        //read lat lng
//                        placeLL = new LatLng(Double.parseDouble(loc.getString("lat")),
//                                Double.parseDouble(loc.getString("lng")));
//
//
//
//
//                    } catch (JSONException jse) {
//                        Log.v("PLACES", "missing value");
//                        missingValue = true;
//                        jse.printStackTrace();
//                    }
//
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }



    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();

        firebaseDatabase = FirebaseDatabase.getInstance();
        ref = firebaseDatabase.getReference();
//        myService=this;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


//
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    public class LocalBinder extends Binder {
        public myService getServerInstance() {
            return myService.this;
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public void ShowNotification()
    {

        Intent in = new Intent(getApplicationContext(), AddExpenseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, in, 0);
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),"notification_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Reminder!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentText("Remember to key in expense!!")
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .build();
        assert notificationManager != null;
        notificationManager.notify(0, notification);
        //the notification is not showing

    }
}