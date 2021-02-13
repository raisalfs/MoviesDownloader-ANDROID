package com.rafslab.movie.dl.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.Objects;

import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_DISABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_ENABLED;
import static androidx.core.net.ConnectivityManagerCompat.RESTRICT_BACKGROUND_STATUS_WHITELISTED;

public class Connectivity {

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }


    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * Check if there is fast connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnectedFast(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && Connectivity.isConnectionFast(info.getType(), info.getSubtype()));
    }

    /**
     * Check if the connection is fast
     *
     * @param type
     * @param subType
     * @return
     */
    public static boolean isConnectionFast(int type, int subType) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            return true;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return true; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return true; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return true; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }
    }

    public static String getConnectionStrength(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        if (info != null && info.isConnected()) {
            return Connectivity.getInternetStrength(info.getType(), info.getSubtype(), context);
        } else {
            return "Not Connected";
        }
    }


    public static String getInternetStrength(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            return "" + level/* + " out of 5"*/;
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "" + 1; //Poor
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "" + 3; //Fair
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "" + 5; //Good
                //No Connection
                default:
                    return "" + 0;
            }
        } else {
            return "Not Connected";
        }
    }

    /***
     * Get Device Connection Status
     * @param context Calling Context.
     * @return Connectivity signal status value which is based on Network Info
     */
//    public static String getConnectionStatus(Context context) {
//        NetworkInfo info = Connectivity.getNetworkInfo(context);
//        if (info == null || !info.isConnected()) {
//            return SyncStateContract.Constants.ConnectionSignalStatus.NO_CONNECTIVITY;
//        } else if (Connectivity.getInternetStatus(info.getType(), info.getSubtype(), context) == 3
//                && Utils.getBatteryPercentageDouble(context) > 20) {
//            return SyncStateContract.Constants.ConnectionSignalStatus.GOOD_STRENGTH;
//        } else if (Connectivity.getInternetStatus(info.getType(), info.getSubtype(), context) >= 2
//                && Utils.getBatteryPercentageDouble(context) > 20) {
//            return SyncStateContract.Constants.ConnectionSignalStatus.FAIR_STRENGTH;
//        } else if (Connectivity.getInternetStatus(info.getType(), info.getSubtype(), context) >= 2
//                && Utils.getBatteryPercentageDouble(context) <= 20) {
//            return SyncStateContract.Constants.ConnectionSignalStatus.BATTERY_LOW;
//        } else {
//            return SyncStateContract.Constants.ConnectionSignalStatus.POOR_STRENGTH;
//        }
//    }

    public static int getInternetStatus(int type, int subType, Context context) {
        if (type == ConnectivityManager.TYPE_WIFI) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int numberOfLevels = 5;
            assert wifiManager != null;
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
            if (level < 2) {
                return 2; //Fair
            } else {
                return 3; //Good
            }
        } else if (type == ConnectivityManager.TYPE_MOBILE) {
            switch (subType) {
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return 1; //Poor
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return 1; //Fair
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return 3; //Good
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return 0; //No Connection
                default:
                    return 0;
            }
        } else {
            return 0;
        }
    }

    /**
     * Return the availability of cellular data access in background.
     *
     * @param context Application or Activity context.
     *
     * @return Availability of cellular data access in background.
     */
    @SuppressLint("SwitchIntDef")
    public static boolean isBackgroundDataAccessAvailable(Context context) {

        boolean isBackgroundDataAccessAvailable = true;

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr != null) {
            // Checks if the device is on a metered network
            if (connMgr.isActiveNetworkMetered()) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    // Checks user’s Data Saver settings.
                    switch (connMgr.getRestrictBackgroundStatus()) {

                        case RESTRICT_BACKGROUND_STATUS_DISABLED:

                        case RESTRICT_BACKGROUND_STATUS_WHITELISTED:
                            // The app is whitelisted. Wherever possible,
                            // the app should use less data in the foreground and background.
                            // Data Saver is disabled. Since the device is connected to a
                            // metered network, the app should use less data wherever possible.
                            isBackgroundDataAccessAvailable = true;
                            break;

                        case RESTRICT_BACKGROUND_STATUS_ENABLED:
                            // Background data usage is blocked for this app. Wherever possible,
                            // the app should also use less data in the foreground.
                            isBackgroundDataAccessAvailable = false;
                            break;
                    }
                } else {
                    NetworkInfo.State state = Objects.requireNonNull(connMgr.getActiveNetworkInfo()).getState();
                    isBackgroundDataAccessAvailable = state != NetworkInfo.State.DISCONNECTED;
                }

            } else {
                // The device is not on a metered network.
                // Use data as required to perform syncs, downloads, and updates.
                isBackgroundDataAccessAvailable = true;
            }
        } else {
            isBackgroundDataAccessAvailable = true;
        }

        return isBackgroundDataAccessAvailable;
    }
}
