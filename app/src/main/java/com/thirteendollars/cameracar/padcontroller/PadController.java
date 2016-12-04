package com.thirteendollars.cameracar.padcontroller;

import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/3/16
 * ============================================================================
 */

public class PadController {

    private PadControllerListener mListener;

    public boolean dispatchPadKeyEvent(KeyEvent event) {

        // ignore if PadControllerListener is not set
        if (mListener == null) {
            return false;
        }

        // else handle action
        if (event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
            int buttonCode = event.getKeyCode();
            boolean newButtonValue = !PadControllerListener.isButtonActive.get(buttonCode);
            PadControllerListener.isButtonActive.put(buttonCode, newButtonValue);
            mListener.onPadButtonPressed(buttonCode, newButtonValue);
        }
        return true;
    }

    public boolean dispatchPadGenericMotionEvent(MotionEvent event) {

        // ignore if PadControllerListener is not set
        if (mListener == null) {
            return false;
        }

        // else handle action
        int historySize = event.getHistorySize();
        for (int i = 0; i < historySize; i++) {
            // Process the event at historical position i
            mListener.onJoystickMove( -event.getHistoricalAxisValue(MotionEvent.AXIS_Y,i),event.getHistoricalAxisValue(MotionEvent.AXIS_Z,i) );
        }
        mListener.onJoystickMove( -event.getAxisValue(MotionEvent.AXIS_Y),event.getAxisValue(MotionEvent.AXIS_Z) );
        return true;
    }

    public void setOnPadControllerListener(PadControllerListener listener) {
        mListener = listener;
    }

}