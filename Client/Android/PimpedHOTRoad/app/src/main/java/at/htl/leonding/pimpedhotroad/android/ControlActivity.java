package at.htl.leonding.pimpedhotroad.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import at.htl.leonding.pimpedhotroad.model.Impulse;

import org.narcotek.pimpedhotroad.android.R;

import java.io.IOException;
import java.util.Stack;

/**
 * Activity for controlling the vehicle.
 *
 * @author Marijanovic Bernard
 */
public class ControlActivity extends Activity {

    private VehicleClient client;

    private Button forwardButton;
    private Button rightButton;
    private Button backwardButton;
    private Button leftButton;
    private Button stopButton;

    private Stack<Impulse> stackedImpulses;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = VehicleClient.getInstance();

        setContentView(R.layout.control_activity);

        WebView wv = (WebView) findViewById(R.id.vision);
        wv.setWebChromeClient(new WebChromeClient());
        wv.getSettings().setJavaScriptEnabled(true);
//        wv.loadUrl("http://" + client.getIpAddress() + "/?action=stream");
        wv.loadUrl("http://" + client.getIpAddress());


        forwardButton = (Button) findViewById(R.id.forward);
        rightButton = (Button) findViewById(R.id.right);
        backwardButton = (Button) findViewById(R.id.backward);
        leftButton = (Button) findViewById(R.id.left);
        stopButton = (Button) findViewById(R.id.stop);

        View.OnTouchListener impulseButtonTouched = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Impulse viewImpulse = getImpulseFromView(v);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!stackedImpulses.contains(viewImpulse)) {
                        stackedImpulses.push(viewImpulse);
                        sendImpulse(viewImpulse);
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Impulse i = stackedImpulses.lastElement();
                    stackedImpulses.remove(getImpulseFromView(v));

                    if (i == viewImpulse && !stackedImpulses.isEmpty()) {
                        sendImpulse(stackedImpulses.lastElement());
                    } else if (stackedImpulses.isEmpty() && i != Impulse.STOP) {
                        sendImpulse(Impulse.STOP);
                    }
                }
                return true;
            }
        };

        forwardButton.setOnTouchListener(impulseButtonTouched);
        rightButton.setOnTouchListener(impulseButtonTouched);
        backwardButton.setOnTouchListener(impulseButtonTouched);
        leftButton.setOnTouchListener(impulseButtonTouched);
        stopButton.setOnTouchListener(impulseButtonTouched);

        stackedImpulses = new Stack<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.playback_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.play:
                sendImpulse(Impulse.PLAY_SONG);
                return true;
            case R.id.pause:
                sendImpulse(Impulse.PAUSE_SONG);
                return true;
            case R.id.next:
                sendImpulse(Impulse.NEXT_SONG);
                return true;
            case R.id.prev:
                sendImpulse(Impulse.PREV_SONG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            VehicleClient.destroyInstance();
        } catch (IOException e) {
            Log.d(getClass().getSimpleName(), e.toString());
        }
    }

    /**
     * Returns an impulse by view. E. g. forward button returns a forward impulse.
     *
     * @param v A control button
     */

    public Impulse getImpulseFromView(View v) {
        switch (v.getId()) {
            case R.id.forward:
                return Impulse.FORWARD;
            case R.id.right:
                return Impulse.RIGHT;
            case R.id.backward:
                return Impulse.BACKWARD;
            case R.id.left:
                return Impulse.LEFT;
            default:
                return Impulse.STOP;
        }
    }

    /**
     * Sends an impulse. Utilizes the clients send function.
     *
     * @param impulse The impulse to send
     */
    private void sendImpulse(Impulse impulse) {

        final Impulse impulse1 = impulse;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.send(impulse1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
