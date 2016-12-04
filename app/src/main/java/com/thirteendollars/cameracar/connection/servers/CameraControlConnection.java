package com.thirteendollars.cameracar.connection.servers;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/13/16
 * ============================================================================
 */

public class CameraControlConnection implements Connectable {

    private InetAddress mServerInetAddr;
    private int mServerPort;
    private DatagramSocket mSocket;

    public CameraControlConnection(String serverAddress, int serverPort) throws UnknownHostException {
        mServerInetAddr = InetAddress.getByName(serverAddress);
        mServerPort=serverPort;
    }

    @Override
    public void start() {
        stop();
        new OpenSocket().start();
    }

    @Override
    public void stop() {
        if(mSocket!=null){
            mSocket.close();
            mSocket=null;
            Log.d(getClass().getCanonicalName(),"Socket closed");
        }
    }

    @Override
    public void send(byte[] data) throws IOException{
        if(mSocket==null){
            throw new IOException("No socket opened");
        }
        else {
            new Send(data).start();
        }
    }

    private class OpenSocket extends Thread {

        @Override
        public void run() {
            super.run();
            try {
                mSocket = new DatagramSocket(mServerPort);
                Log.d(getClass().getCanonicalName(),"Socket opened");
            }
            catch (IOException exception) {
                exception.printStackTrace();
                Log.e(getClass().getCanonicalName(),"Socket opening error");
                if(mSocket!=null) {
                    mSocket.close();
                    mSocket = null;
                    Log.d(getClass().getCanonicalName(), "Socket closed");
                }
            }
        }
    }

    private class Send extends Thread {
        byte[] message;
        Send(byte[] mssg){
            message = mssg;
        }

        @Override
        public void run() {
            super.run();
            DatagramPacket packet = new DatagramPacket(message, message.length, mServerInetAddr, mServerPort);
            try {
                mSocket.send(packet);
                Log.d(getClass().getCanonicalName(),"Sent: "+ Arrays.toString(message));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(getClass().getCanonicalName(),"Sending error: "+ Arrays.toString(message));
            }
        }

    }

}


