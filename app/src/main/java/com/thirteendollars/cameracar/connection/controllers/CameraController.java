package com.thirteendollars.cameracar.connection.controllers;

import com.thirteendollars.cameracar.connection.Settings;
import com.thirteendollars.cameracar.connection.servers.CameraControlConnection;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/13/16
 * ============================================================================
 */

public abstract class CameraController {

    public CameraController(){
        reopenSocket();
    }

    public abstract void onError(String message);

    public void setPosition(int positionX, int positionY) {
        // check if no over byte limit
        if(positionX>Byte.MAX_VALUE){
            positionX=Byte.MAX_VALUE;
        }
        else if( positionX<Byte.MIN_VALUE ){
            positionX=Byte.MIN_VALUE;
        }
        if(positionY>Byte.MAX_VALUE){
            positionY=Byte.MAX_VALUE;
        }
        else if( positionY<Byte.MIN_VALUE ){
            positionY=Byte.MIN_VALUE;
        }

        try {
            mControlService.send( mApiManager.setServos(positionX,positionY) );
            mIsActive =true;
        } catch (IOException e) {
            onError( e.getMessage() );
            mIsActive = false;
        }
    }

    public boolean isActive(){
        return mIsActive;
    }

    public void reopenSocket() {

        if( mApiManager == null ){
            mApiManager = new ApiManager();
        }

        if( mControlService == null ){
            try {
                mControlService = new CameraControlConnection(Settings.SERVER_ADDRESS,Settings.SERVER_PORT_CAMERA_CONTROL);
                mIsActive = true;
            } catch (UnknownHostException e) {
                e.printStackTrace();
                mIsActive =false;
                onError( e.getMessage() );
                return;
            }
        }
        else {
            mControlService.stop();
        }
        mControlService.start();
        mIsActive = true;
    }

    public void closeSocket(){
        mIsActive = false;
        mControlService.stop();
    }


    private CameraControlConnection mControlService;
    private ApiManager mApiManager;
    private boolean mIsActive;

    private class ApiManager{

        private static final int MAX_PACKET_LENGTH =2;

        byte[] setServos(int servoX, int servoY ){
            byte[] pack = new byte[MAX_PACKET_LENGTH];
            pack[0]=(byte)servoX;
            pack[1]=(byte)servoY;
            return pack;
        }

    }
}
