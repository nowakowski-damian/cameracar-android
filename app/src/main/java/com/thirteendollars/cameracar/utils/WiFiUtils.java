package com.thirteendollars.cameracar.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * ============================================================================
 * Author      : Damian Nowakowski
 * Contact   : damian.nowakowski@aol.com
 * Date : 10/24/16
 * ============================================================================
 */

public class WiFiUtils {


    public static String isWifiEnabled(Context context){
        return ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getWifiState()==WifiManager.WIFI_STATE_ENABLED ? "enabled" : "disabled";
    }

    public static String getSsid(Context context){
        return ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID();
    }

    public static int getSignalStrength(Context context){
        return WifiManager.calculateSignalLevel( ((WifiManager)context.getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getRssi(),101 );
    }
}
