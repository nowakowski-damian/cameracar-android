package com.thirteendollars.cameracar.connection.servers;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/13/16
 * ============================================================================
 */

public abstract class VehicleControlConnection implements Connectable {

    private InetAddress mServerInetAddr;
    private int mServerPort;

    private Socket mSocket;
    private OutputStream mOutStream;

    public VehicleControlConnection(String serverAddress, int serverPort) throws UnknownHostException{
        mServerInetAddr = InetAddress.getByName(serverAddress);
        mServerPort=serverPort;
    }

    @Override
    public void start() {
        stop();
        new Connect().start();
    }

    @Override
    public void stop() {
        if(mOutStream!=null){
            try {
                mOutStream.close();
                mOutStream=null;
                Log.d(getClass().getCanonicalName(),"OutStream closed");
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(getClass().getCanonicalName(),"OutStream closing error");
            }
        }
        if(mSocket!=null){
            try {
                mSocket.close();
                mSocket=null;
                Log.d(getClass().getCanonicalName(),"Socket closed");
            } catch (IOException e1) {
                e1.printStackTrace();
                Log.e(getClass().getCanonicalName(),"Socket closing error");
            }
        }
    }

    @Override
    public void send(byte[] data) throws IOException{
        if(mSocket==null || !mSocket.isConnected() ){
            throw new IOException("Not connected");
        }
        else {
            new Send(data).start();
        }
    }

    public abstract void onConnected();
    public abstract void onDisconnected();

    private class Connect extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mSocket = new Socket(mServerInetAddr, mServerPort);
                mSocket.setTcpNoDelay(false);
                mOutStream = mSocket.getOutputStream();
                onConnected();
                Log.d(getClass().getCanonicalName(),"Connected");
            }
            catch (IOException exception){
                exception.printStackTrace();
                onDisconnected();
                Log.e(getClass().getCanonicalName(),"Connecting error");
                try {
                    if(mSocket!=null) {
                        mSocket.close();
                        mSocket = null;
                        Log.d(getClass().getCanonicalName(),"Socket closed");
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                    Log.e(getClass().getCanonicalName(),"Socket closing error");
                }

            }
        }
    }

    private class Send extends Thread{
        byte[] message;
        Send(byte[] mssg){
            message = mssg;
        }

        @Override
        public void run() {
            super.run();
            try {
                mOutStream.write(message);
                Log.d(getClass().getCanonicalName(),"Sent: "+ Arrays.toString(message));
            } catch (IOException e1) {
                e1.printStackTrace();
                onDisconnected();
                Log.e(getClass().getCanonicalName(),"Sending error: "+ Arrays.toString(message));
            }
        }
    }

}
