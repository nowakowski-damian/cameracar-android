package com.thirteendollars.cameracar.connection.controllers;

import android.view.KeyEvent;

import com.thirteendollars.cameracar.connection.Settings;
import com.thirteendollars.cameracar.connection.servers.VehicleControlConnection;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/13/16
 * ============================================================================
 */

public abstract class VehicleController {

    public VehicleController(){
        reconnect();
    }

    public abstract void onError(String message);
    public abstract void onConnectionEstablished();
    public abstract void onConnectionAborted();

    public void setMotorSpeed(int straight, int turn ){
        int leftLevel;
        int rightLevel;


        if( straight == 0 ) {
            leftLevel = turn ;
            rightLevel = -turn;
        }
        else if( straight>0 ) {
            leftLevel=Math.min(straight+turn,100);
            rightLevel=Math.min(straight-turn,100);
        }
        else{
            leftLevel=Math.max(straight-turn,-100);
            rightLevel=Math.max(straight+turn,-100);
        }

        try {
            mControlService.send( mApiManager.setMotors(leftLevel,rightLevel) );
        }
        catch (IOException e){
            onError(e.getMessage());
        }
    }

    public void setSwitch(int buttonCode, boolean turnOn  ) {
        //     RASPBERRY BUTTON CONFIGURATION:
        //     BUTTON_X=0;
        //     BUTTON_Y=1;
        //     BUTTON_B=2;
        //     BUTTON_L1=3;
        //     BUTTON_R1=4;
        //     BUTTON_L2=5;
        //     BUTTON_R2=6;
        int switchIndex;
        switch (buttonCode) {
            case KeyEvent.KEYCODE_BUTTON_X: switchIndex=0; break;
            case KeyEvent.KEYCODE_BUTTON_Y: switchIndex=1; break;
            case KeyEvent.KEYCODE_BUTTON_B: switchIndex=2; break;
            case KeyEvent.KEYCODE_BUTTON_L1: switchIndex=3; break;
            case KeyEvent.KEYCODE_BUTTON_R1: switchIndex=4; break;
            case KeyEvent.KEYCODE_BUTTON_L2: switchIndex=5; break;
            case KeyEvent.KEYCODE_BUTTON_R2: switchIndex=6; break;
            default: return;
        }

        try {
            mControlService.send( mApiManager.setSwitch(switchIndex, turnOn) );
        }
        catch (IOException e){
            onError(e.getMessage());
        }
    }

    public void reconnect(){

        if(mApiManager==null){
            mApiManager = new ApiManager();
        }

        if( mControlService == null ){
            try {
                mControlService = getPreparedService();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                onError( e.getMessage() );
                return;
            }
        }
        else {
            mControlService.stop();
        }
        mControlService.start();
    }

    public void disconnect(){
        mControlService.stop();
    }

    public boolean isConnected(){
        return mIsConnected;
    }


    private VehicleControlConnection mControlService;
    private ApiManager mApiManager;
    private boolean mIsConnected;

    private VehicleControlConnection getPreparedService() throws UnknownHostException{
        return new VehicleControlConnection(Settings.SERVER_ADDRESS,Settings.SERVER_PORT_VEHICLE_CONTROL) {
            @Override
            public void onConnected() {
                mIsConnected = true;
                onConnectionEstablished();
            }

            @Override
            public void onDisconnected() {
                mIsConnected = false;
                onConnectionAborted();
            }
        };
    }

    private class ApiManager{

        private static final int MAX_PACKET_LENGTH =3;
        private final byte SET_MOTORS_ID=1;
        private final byte SET_SWITCH_ID=2;

        byte[] setMotors(int leftLevel, int rightLevel ){
            byte[] pack = new byte[MAX_PACKET_LENGTH];
            pack[0]=SET_MOTORS_ID;
            pack[1]=(byte)leftLevel;
            pack[2]=(byte)rightLevel;
            return pack;
        }

        byte[] setSwitch(int switchIndex, boolean turnOn  ){
            byte[] pack = new byte[MAX_PACKET_LENGTH];
            pack[0]=SET_SWITCH_ID;
            pack[1]=(byte)switchIndex;
            pack[2]= (byte)( turnOn ? 1:0 );
            return pack;
        }
    }

}
