package com.thirteendollars.cameracar.ui.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.thirteendollars.cameracar.R;
import com.thirteendollars.cameracar.connection.Settings;
import com.thirteendollars.cameracar.connection.controllers.CameraController;
import com.thirteendollars.cameracar.connection.controllers.VehicleController;
import com.thirteendollars.cameracar.orientation.HeadMoveListener;
import com.thirteendollars.cameracar.orientation.HeadTracker;
import com.thirteendollars.cameracar.ui.views.CompassView;
import com.thirteendollars.cameracar.utils.DisplayUtils;
import com.thirteendollars.cameracar.utils.WiFiUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/13/16
 * ============================================================================
 */

public class MainActivity extends PadControllerActivity implements HeadMoveListener {


    private Handler mHandler;

    private VehicleController mVehicleController;
    private CameraController mCameraController;
    private HeadTracker mHeadTracker;


    @BindView(R.id.message_view)
    LinearLayout mMessageLayout;

    @BindView(R.id.message_text_l)
    TextView mMessageTextL;

    @BindView(R.id.message_text_r)
    TextView mMessageTextR;

    @BindView(R.id.status_whole_view)
    LinearLayout mStatusWholeView;

    @BindView(R.id.status_layout_left)
    LinearLayout mStatusLayoutLeft;

    @BindView(R.id.status_layout_right)
    LinearLayout mStatusLayoutRight;

    @BindView(R.id.left_compass_layout)
    LinearLayout mLeftCompassLayout;

    @BindView(R.id.right_compass_layout)
    LinearLayout mRightCompassLayout;

    private CompassView mCompassR;
    private TextView mSsidR;
    private TextView mWiFiStatusR;
    private TextView mSigStrengthR;
    private TextView mVehicleIpR;
    private TextView mVehicleStatusR;
    private TextView mCameraIpR;
    private TextView mCameraStatusR;

    private CompassView mCompassL;
    private TextView mSsidL;
    private TextView mWiFiStatusL;
    private TextView mSigStrengthL;
    private TextView mVehicleIpL;
    private TextView mVehicleStatusL;
    private TextView mCameraIpL;
    private TextView mCameraStatusL;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupDisplay();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        findViewsById();
        initWebView();
        mHandler = new Handler();
        mVehicleController = getReadyVehicleController();
        mCameraController = getReadyCameraController();
        mHeadTracker = new HeadTracker(this);
        mHeadTracker.setOnHeadMoveListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateStatusTexts();
        mHeadTracker.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHeadTracker.unregister();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVehicleController.disconnect();
        mCameraController.closeSocket();
    }

    private void setupDisplay() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE_STICKY|SYSTEM_UI_FLAG_FULLSCREEN|SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    private void findViewsById() {
        // left eye views
        mCompassL = ButterKnife.findById(mLeftCompassLayout,R.id.compass_view);
        mSsidL = ButterKnife.findById(mStatusLayoutLeft,R.id.ssid);
        mWiFiStatusL = ButterKnife.findById(mStatusLayoutLeft,R.id.status);
        mSigStrengthL = ButterKnife.findById(mStatusLayoutLeft,R.id.sig_strength);
        mVehicleIpL = ButterKnife.findById(mStatusLayoutLeft,R.id.vehicle_server_ip);
        mVehicleStatusL = ButterKnife.findById(mStatusLayoutLeft,R.id.vehicle_server_status);
        mCameraIpL = ButterKnife.findById(mStatusLayoutLeft,R.id.camera_server_ip);
        mCameraStatusL = ButterKnife.findById(mStatusLayoutLeft,R.id.camera_server_status);
        // right eye views
        mCompassR = ButterKnife.findById(mRightCompassLayout,R.id.compass_view);
        mSsidR = ButterKnife.findById(mStatusLayoutRight,R.id.ssid);
        mWiFiStatusR = ButterKnife.findById(mStatusLayoutRight,R.id.status);
        mSigStrengthR = ButterKnife.findById(mStatusLayoutRight,R.id.sig_strength);
        mVehicleIpR = ButterKnife.findById(mStatusLayoutRight,R.id.vehicle_server_ip);
        mVehicleStatusR = ButterKnife.findById(mStatusLayoutRight,R.id.vehicle_server_status);
        mCameraIpR = ButterKnife.findById(mStatusLayoutRight,R.id.camera_server_ip);
        mCameraStatusR = ButterKnife.findById(mStatusLayoutRight,R.id.camera_server_status);
    }

    private void updateStatusTexts() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                updateCameraStatusText();
                updateVehicleStatusText();
                String ssid = WiFiUtils.getSsid(getApplicationContext());
                String wifiStatus = WiFiUtils.isWifiEnabled(getApplicationContext());
                String sigStrength = WiFiUtils.getSignalStrength(getApplicationContext())+"%";

                mSsidR.setText(ssid);
                mWiFiStatusR.setText(wifiStatus);
                mSigStrengthR.setText(sigStrength);

                mSsidL.setText(ssid);
                mWiFiStatusL.setText(wifiStatus);
                mSigStrengthL.setText(sigStrength);
            }
        });
    }
    private void updateVehicleStatusText() {
                String vehicleIp = Settings.SERVER_ADDRESS;
                String vehicleStatus;
                int color;
                if( mVehicleController.isConnected() ) {
                    vehicleStatus = getString(R.string.status_connected);
                    color = Color.GREEN;
                } else {
                    vehicleStatus = getString(R.string.status_disconnected);
                    color = Color.RED;
                }
                mVehicleIpR.setText(vehicleIp);
                mVehicleStatusR.setText(vehicleStatus);
                mVehicleStatusR.setTextColor(color);
                mVehicleIpL.setText(vehicleIp);
                mVehicleStatusL.setText(vehicleStatus);
                mVehicleStatusL.setTextColor(color);
    }
    private void updateCameraStatusText() {
                String cameraIp = Settings.SERVER_ADDRESS;
                String cameraStatus;
                int color;
                if( mCameraController.isActive() ) {
                    cameraStatus = getString(R.string.status_active);
                    color = Color.GREEN;
                } else {
                    cameraStatus = getString(R.string.status_error);
                    color = Color.RED;
                }
                mCameraIpR.setText(cameraIp);
                mCameraStatusR.setText(cameraStatus);
                mCameraStatusR.setTextColor(color);
                mCameraIpL.setText(cameraIp);
                mCameraStatusL.setText(cameraStatus);
                mCameraStatusL.setTextColor(color);
    }

    private void initWebView() {
        WebView webview = (WebView)findViewById(R.id.webview);
        // set layout params
        DisplayMetrics metrics = DisplayUtils.getMetrics(this);
        webview.setLayoutParams( new RelativeLayout.LayoutParams(metrics.widthPixels,metrics.heightPixels) );
        webview.setWebViewClient( new WebViewClient() );
        webview.setInitialScale(100);
        // set web settings
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(false); // zoom y/n
        settings.setUseWideViewPort(false); // true = like desktop
        settings.setBuiltInZoomControls(false);
        // load url
        webview.loadUrl(Settings.VIDEO_STREAM_ADDRESS);
    }

    private void showMessage(final String message,final int color){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMessageTextL.setTextColor(color);
                mMessageTextL.setText(message);
                mMessageTextR.setTextColor(color);
                mMessageTextR.setText(message);
                mMessageLayout.setVisibility(View.VISIBLE);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mMessageLayout.setVisibility(View.GONE);
                    }
                },2000);
            }
        });
    }

    private VehicleController getReadyVehicleController() {
        return new VehicleController() {
            @Override
            public void onError(String message) {
                showMessage(message, Color.RED);
                updateStatusTexts();
            }

            @Override
            public void onConnectionEstablished() {
                showMessage("Vehicle controller connnected",Color.GREEN);
                updateStatusTexts();
            }

            @Override
            public void onConnectionAborted() {
                showMessage("Vehicle controller disconnected", Color.RED);
                updateStatusTexts();
            }
        };
    }

    private CameraController getReadyCameraController() {
        return new CameraController() {
            @Override
            public void onError(String message) {
                showMessage(message, Color.RED);
                updateStatusTexts();
            }
        };
    }

    private void resetAllConnections() {
        mVehicleController.reconnect();
        mCameraController.reopenSocket();
    }


    @Override
    public void onJoystickMove(float axisY, float axisX) {
        Log.d("onJoystickMove","y:"+axisY+" x:"+axisX );
        int Y = Math.round(100*axisY);
        int X = Math.round(100*axisX);
        mVehicleController.setMotorSpeed(Y,X);
    }

    @Override
    public void onPadButtonPressed(int BUTTON_CODE, boolean isActive) {
        Log.d("onPadButtonPressed",BUTTON_CODE+"");
        switch (BUTTON_CODE){
            case KeyEvent.KEYCODE_BUTTON_A:
                mHeadTracker.calibrate();
                break;
            case KeyEvent.KEYCODE_BUTTON_SELECT:
                resetAllConnections();
                break;
            case KeyEvent.KEYCODE_BUTTON_START:
                if( mStatusWholeView.getVisibility() == View.VISIBLE){
                    mStatusWholeView.setVisibility(View.GONE);
                }
                else {
                    updateVehicleStatusText();
                    mStatusWholeView.setVisibility(View.VISIBLE);
                }
                break;
            default:
                mVehicleController.setSwitch(BUTTON_CODE,isActive);
        }
    }

    @Override
    public void onHeadPositionChange(int xDegree, int yDegree, int zDegree) {
        // x : left/right
        // y : rotation
        // z : up/down
        Log.d("onHeadPositionChange","left/right:"+xDegree+" rotation:"+yDegree+" up/down:"+zDegree);
        mCameraController.setPosition(xDegree,zDegree);
        mCompassL.setProgress(xDegree);
        mCompassR.setProgress(xDegree);
    }
}
