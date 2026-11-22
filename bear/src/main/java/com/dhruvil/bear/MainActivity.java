package com.dhruvil.bear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Full-screen WebView that runs the game, and forwards TV remote / gamepad input to it.
 *
 * Gamepad layout (same as the console original): A = jump, B = punch, A+B or B+A = very long jump.
 * On a plain TV remote, OK/Enter acts as A (there is no B, so use a gamepad to punch).
 */
public class MainActivity extends Activity {

    private WebView webView;
    private float lastX, lastY;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        webView.setBackgroundColor(0xFF8ED1FC);
        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        webView.setWebViewClient(new WebViewClient());
        webView.setFocusable(false);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        setContentView(webView);
        keepScreenOn();
        String server = getString(R.string.server_url).trim();
        String query = server.isEmpty() ? "" : "?server=" + android.net.Uri.encode(server);
        webView.loadUrl("file:///android_asset/index.html" + query);
    }

    private void keepScreenOn() {
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        hideSystemBars();
    }

    private void hideSystemBars() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private static String codeFor(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BUTTON_A:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return "A";
            case KeyEvent.KEYCODE_BUTTON_B:
            case KeyEvent.KEYCODE_BUTTON_X:
                return "B";
            case KeyEvent.KEYCODE_BUTTON_START:
                return "START";
            case KeyEvent.KEYCODE_DPAD_LEFT:
                return "LEFT";
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                return "RIGHT";
            case KeyEvent.KEYCODE_DPAD_UP:
                return "UP";
            case KeyEvent.KEYCODE_DPAD_DOWN:
                return "DOWN";
            default:
                return null;
        }
    }

    private void send(String js) {
        webView.evaluateJavascript(js, null);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        String code = codeFor(event.getKeyCode());
        if (code == null) return super.dispatchKeyEvent(event);   // Back, volume, etc. behave normally
        if (event.getRepeatCount() == 0) {
            boolean down = event.getAction() == KeyEvent.ACTION_DOWN;
            send("window.tvKey&&window.tvKey('" + code + "'," + down + ")");
        }
        return true;
    }

    /** Analog stick and hat (D-pad on some pads) movement. */
    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        if ((ev.getSource() & InputDevice.SOURCE_JOYSTICK) != InputDevice.SOURCE_JOYSTICK
                || ev.getAction() != MotionEvent.ACTION_MOVE) {
            return super.dispatchGenericMotionEvent(ev);
        }
        float x = dead(ev.getAxisValue(MotionEvent.AXIS_X));
        float y = dead(ev.getAxisValue(MotionEvent.AXIS_Y));
        float hx = ev.getAxisValue(MotionEvent.AXIS_HAT_X), hy = ev.getAxisValue(MotionEvent.AXIS_HAT_Y);
        if (hx != 0) x = hx;
        if (hy != 0) y = hy;
        if (x != lastX || y != lastY) {
            lastX = x;
            lastY = y;
            send("window.tvAxis&&window.tvAxis(" + x + "," + y + ")");
        }
        return true;
    }

    private static float dead(float v) {
        return Math.abs(v) < 0.2f ? 0f : v;
    }
}
