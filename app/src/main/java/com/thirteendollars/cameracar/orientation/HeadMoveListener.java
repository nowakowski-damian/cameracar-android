package com.thirteendollars.cameracar.orientation;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/3/16
 * ============================================================================
 */

public interface HeadMoveListener {
    void onHeadPositionChange(int xDegree, int yDegree, int zDegree);
}
