package at.htl.leonding.pimpedhotroad.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.narcotek.pimpedhotroad.android.R;

/**
 * Activity for connecting to the vehicle.
 *
 * @author Bernard Marijanovic
 */
public class ConnectActivity extends Activity {

    private final int magicNumber = 6;
    private int secretCount;

    private ProgressDialog connectionProgress;
    private EditText ipAddressText;
    private EditText portText;

    VehicleClientCreatorTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        secretCount = 0;

        ((WifiManager) this.getSystemService(Context.WIFI_SERVICE)).setWifiEnabled(true);
        Toast.makeText(this, R.string.wlan_enabled, Toast.LENGTH_SHORT).show();
        setContentView(R.layout.connect_activity);

        connectionProgress = new ProgressDialog(this);
        connectionProgress.setMessage(getResources().getString(R.string.progress_info));

        ipAddressText = (EditText) findViewById(R.id.ip_address);
        portText = (EditText) findViewById(R.id.port);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.about_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                secretCount++;
                if (secretCount >= magicNumber) {
                    MediaPlayer mp = MediaPlayer.create(this, R.raw.secret);
                    Toast.makeText(this, R.string.secret_text, Toast.LENGTH_LONG).show();
                    mp.start();
                } else
                    Toast.makeText(this, R.string.about_text, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Is called from a Button (View) to create a new VehicleClient instance. Therefore the parameter.
     *
     * @param v The calling button
     */
    public void connect(View v) {
        connectionProgress.show();
        task = new VehicleClientCreatorTask();
        task.execute();
    }

    /**
     * Task specifically for creating the connection (VehicleClient) with the server and starting the
     * ControlActivity once connected.
     *
     * @author Marijanovic Bernard
     */
    private class VehicleClientCreatorTask extends AsyncTask {
        /*
         PLEASE READ: Some functions are called from the background thread (= AsyncTask) and others
         are called from the UI thread (= Activity).
         */
        private VehicleClient vehicleClient;

        // AsyncTask
        @Override
        protected Object doInBackground(Object[] objects) {
            try {
                vehicleClient = VehicleClient.getInstance();
                // TODO Check input text
                vehicleClient.initializeClient(ipAddressText.getText().toString(), Integer.parseInt(portText.getText().toString()));
            } catch (Exception e) {
                Log.d(getClass().getSimpleName(), e.toString());
                publishProgress();
            }
            publishProgress();
            return null;
        }

        // AsyncTask
        @Override
        protected void onPostExecute(Object o) {
            if (vehicleClient.isInitialized()) {
                /*
                 Serialization won't work because of java.net.Sockets non-serilizable attribute!
                 Instead the Singleton pattern for CarClient is implemented.
                */
                Intent i = new Intent(ConnectActivity.this, ControlActivity.class);
                startActivity(i);
            } else
                Toast.makeText(ConnectActivity.this, R.string.connecting_failed, Toast.LENGTH_SHORT).show();
        }

        // Activity (useless???)
        @Override
        protected void onProgressUpdate(Object[] values) {
            connectionProgress.dismiss();
        }
    }
}
