package com.thirteendollars.cameracar.ui.activities;

import android.os.Bundle;
import android.support.v4.view.InputDeviceCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.thirteendollars.cameracar.padcontroller.PadController;
import com.thirteendollars.cameracar.padcontroller.PadControllerListener;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/3/16
 * ============================================================================
 */

public abstract class PadControllerActivity extends AppCompatActivity implements PadControllerListener {

    private PadController mPadController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPadController = new PadController();
        mPadController.setOnPadControllerListener(this);
    }

    @Override
    public boolean dispatchGenericMotionEvent(final MotionEvent event) {

        // if event source not from gamepad, handle it in a classic way
        if( (event.getSource()& InputDeviceCompat.SOURCE_JOYSTICK)!=InputDeviceCompat.SOURCE_JOYSTICK ){
            return super.dispatchGenericMotionEvent(event);
        }
        // else handle as gamepad move
        return mPadController.dispatchPadGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();

        // ignore volume buttons functionality
        if( keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
                keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ) {
            return true;
        }

        // if event source not from gamepad, handle it in a classic way
        if( (event.getSource()&InputDeviceCompat.SOURCE_GAMEPAD)!=InputDeviceCompat.SOURCE_GAMEPAD ){
            return super.dispatchKeyEvent(event);
        }

        // else handle as gamepad move
        return mPadController.dispatchPadKeyEvent(event);
    }

}
