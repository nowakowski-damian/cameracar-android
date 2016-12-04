package com.thirteendollars.cameracar.orientation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import static android.content.Context.SENSOR_SERVICE;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 11/13/16
 * ============================================================================
 */

public class HeadTracker implements SensorEventListener{
    // NOTE: "y" coordinate (rotation) is useless in this project, so it's ignored in whole class

    private static final int SENSOR_TYPE = Sensor.TYPE_GAME_ROTATION_VECTOR; // on older devices could be "Sensor.TYPE_ROTATION_VECTOR" as well
    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    private HeadMoveListener mMoveListener;

    private double xReferencePoint,/* yReferencePoint,*/ zReferencePoint;
    private boolean mCalibrate;

    private int mLastValueX;
    //    private int mLastValueY;
    private int mLastValueZ;

    // A - data from rotation vector
    private float[] mRotationMatrix = new float[9];
    private float[] mOrientation = new float[3];


    public HeadTracker(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(SENSOR_TYPE);
        mCalibrate=true;
    }

    public void register(HeadMoveListener listener) {
        mMoveListener = listener;
        mSensorManager.registerListener(this, mRotationVectorSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregister() {
        mSensorManager.unregisterListener(this);
        mMoveListener = null;
    }

    public void calibrate(){
        mCalibrate=true;
    }

    private void processData() {

        // x : left/right
        // y : rotation
        // z : up/down
        double degreesX =mOrientation[0]/Math.PI*180;
//        double degreesY =bestY/Math.PI*180;
        double degreesZ =mOrientation[2]/Math.PI*180;

        if ( mCalibrate ) {
            mCalibrate = false;
            xReferencePoint = degreesX;
//            yReferencePoint = degreesY;
            zReferencePoint = degreesZ;
            mMoveListener.onHeadPositionChange(0,0,0);
            return;
        }

        degreesX-= xReferencePoint;
//        degreesY-= yReferencePoint;
        degreesZ-= zReferencePoint;

        if( degreesX >180 ) {
            degreesX-=360;
        }
        else if (degreesX<-180) {
            degreesX+=360;
        }

//        if( degreesY >180 ){
//            degreesY-=360;
//        }
//        else if (degreesY<-180){
//            degreesY+=360;
//        }

        if( degreesZ >180 ){
            degreesZ-=360;
        }
        else if (degreesZ<-180){
            degreesZ+=360;
        }

        int currentX = (int)Math.round(degreesX);
//        int currentY = (int)Math.round(degreesY);
        int currentZ = (int)Math.round(degreesZ);

        // has data changed?
        if(currentX!=mLastValueX && currentZ!=mLastValueZ /* && currentY!=mLastValueY */ ) {
            mMoveListener.onHeadPositionChange( currentX,/* currentY */ 0, currentZ );

            mLastValueX=currentX;
//            mLastValueY=currentY;
            mLastValueZ=currentZ;
        }

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == SENSOR_TYPE && mMoveListener!=null ) {
            float[] mRotationVector = event.values;
            // get data from rotation vector
            SensorManager.getRotationMatrixFromVector(mRotationMatrix, mRotationVector);
            SensorManager.getOrientation(mRotationMatrix, mOrientation);
            processData();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    public void setOnHeadMoveListener(HeadMoveListener listener) {
        mMoveListener = listener;
    }

}
