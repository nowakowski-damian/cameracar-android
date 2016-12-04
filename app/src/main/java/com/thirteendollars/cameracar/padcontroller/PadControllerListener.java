package com.thirteendollars.cameracar.padcontroller;

import android.util.SparseBooleanArray;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/3/16
 * ============================================================================
 */

public interface PadControllerListener {

    int NUM_OF_BUTTONS = 10;
    SparseBooleanArray isButtonActive = new SparseBooleanArray(NUM_OF_BUTTONS);

    void onJoystickMove(float axisY,float axisX);
    void onPadButtonPressed(int BUTTON_CODE, boolean isActive);
}
