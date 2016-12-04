package com.thirteendollars.cameracar.ui.views;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

import com.thirteendollars.cameracar.R;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 12/4/16
 * ============================================================================
 */

public class CompassView extends SeekBar {

    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }



    private void init(Context context) {
        setProgressDrawable( new ColorDrawable(ContextCompat.getColor(context,R.color.transparent) ) );
        setProgress(0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    @Override
    public synchronized void setProgress(int degree ) {
        super.setProgress( degreeToProgress(degree) );
    }


    private int degreeToProgress(int degree) {
        int progress = 50; // center
        progress+= degree*5/9;
        return progress;
    }
}
