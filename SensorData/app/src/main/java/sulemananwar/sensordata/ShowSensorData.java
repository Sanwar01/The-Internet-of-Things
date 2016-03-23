package sulemananwar.sensordata;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class ShowSensorData extends ActionBarActivity {
    private static final String TAG="MainActivitySensorEg";
    public static String sensorServerURL = "http://10.0.2.2:8081/PhidgetServer/sensorToDB";
    public static String sensorToGet = "motor";
    public static String sensorUpdatedValue = "90";

    public TextView sensorValueField;
    public TextView sensorDescriptorField;

    private Button mUpdateSensorValueButton;
    private Button mRetrieveSensorValueButton;

    AsyncHttpClient sensorServerClient = new AsyncHttpClient();
    //SensorServerClient sensorServerClient = new SensorServerClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_sensor_data);

        sensorValueField = (TextView) findViewById(R.id.sensorvalueTV);
        sensorDescriptorField = (TextView) findViewById(R.id.sensornameTV) ;
        mRetrieveSensorValueButton = (Button) findViewById(R.id.getSensorValueButton);
        mUpdateSensorValueButton = (Button) findViewById(R.id.updateSensorValueButton);

        mRetrieveSensorValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGettingData();


            }
        });

        mUpdateSensorValueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuttingData();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_sensor_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startGettingData() {
        RequestParams params = new RequestParams();
        params.put("getdata", "true");
        params.put("sensorname", sensorToGet);
        sensorServerClient.get(sensorServerURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                sensorValueField.setText(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace(System.out);
            }
        });
        Log.i(TAG, "started thread to get sensor data");
    }

    private void updateSensorDisplay(){

    }

    private void startPuttingData() {
        RequestParams params = new RequestParams();
        params.put("sensorvalue", sensorUpdatedValue);
        params.put("sensorname", sensorToGet);
        sensorServerClient.get(sensorServerURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                sensorValueField.setText(response);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace(System.out);
            }
        });
        Log.i(TAG, "started thread to put sensor data");
    }




}
