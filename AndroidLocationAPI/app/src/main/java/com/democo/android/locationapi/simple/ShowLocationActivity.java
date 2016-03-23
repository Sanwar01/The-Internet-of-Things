package com.democo.android.locationapi.simple;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class ShowLocationActivity extends Activity implements LocationListener {

    private static final String TAG="ShowLocation";
    // temporary id to send to server
    private static final String USERID="billynomates@anywhere.com";

    // replace url string with IP of server
    private static final String baseurl="http://10.0.2.2:8081/LocationRecorder/UploadLocation";

    private TextView latituteField;
    private TextView longitudeField;
    private TextView SensorData;
    private LocationManager locationManager;
    private String provider;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // IMPORTANT: Strict mode only here to allow networking in main thread. Ideally create an AsyncTask
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_show_location);
        latituteField = (TextView) findViewById(R.id.TextView02);
        longitudeField = (TextView) findViewById(R.id.TextView04);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latituteField.setText("Location not available");
            longitudeField.setText("Location not available");
        }
    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(final Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());
        latituteField.setText(String.valueOf(lat));
        longitudeField.setText(String.valueOf(lng));

        new Thread(new Runnable() {

            @Override
            public void run() {
                sendPosToServer(location.getLatitude(), location.getLongitude());
                Log.i(TAG, "done thread");
            }
        }).run();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,  Toast.LENGTH_SHORT).show();
    }

    public void sendPosToServer(double lat, double lon)
    {
        Log.i(TAG,"sending position to server");
        // sending position and userid
        String url = baseurl+ "?lat="+lat+"&lon="+lon+"&email="+USERID;
        HttpClient httpclient = new DefaultHttpClient();
        Log.i(TAG, url);
        // Prepare a request object
        HttpGet httpget = new HttpGet(url);

        // Execute the request
        HttpResponse response;
        try {
            Log.i(TAG,"httpget is "+httpget. getURI());
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i(TAG,response.getStatusLine().toString());
        } catch (Exception e) {Log.i(TAG,"error in executing get "+e);}
    }

    public void postToServer(double lat, double lon) throws URISyntaxException, ClientProtocolException, IOException  {
        String msgBody =  "lat="+lat+"\nlon="+lon;
        // Sets the IP address of localhost on the Windows machine where the service is running
        URI serviceUri = new URI(baseurl);
        Log.i(TAG,"post to server "+serviceUri);
        // Creates a new HttpPut instance around the supplied service uri
        HttpPost postRequest = new HttpPost(serviceUri);
        // Adds the the content type header to the request, set the value to application/json
        postRequest.addHeader("content-type", "text/html");
        postRequest.setEntity(new StringEntity(msgBody));

        // Creates a response handler (BasicResponseHandler) to process the results of the request
        ResponseHandler<String> handler = new BasicResponseHandler();

        // Creates an instance of the DefaultHttpClient
        DefaultHttpClient httpclient = new DefaultHttpClient();

        // Uses the client to execute a request passing the HttpPut and ResponseHandler as parameters 
        String result = httpclient.execute(postRequest, handler);
        Log.i(TAG, "Put to Server. Result: " + result);

        // Shutdowns the HttpClient
        httpclient.getConnectionManager().shutdown();
    }

    //Task3
    public void sensorDataToServer() {

    }
} 