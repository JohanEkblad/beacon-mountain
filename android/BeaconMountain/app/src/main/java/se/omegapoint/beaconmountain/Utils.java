package se.omegapoint.beaconmountain;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;

import java.net.*;
import java.util.*;

public class Utils {

    static final String APP_NAME = "BeaconMountain";

    public static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;
                        if (isIPv4)
                            return sAddr;
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    public static void sendSms(AppCompatActivity activity, String message, String srcNumber){
        Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + srcNumber));
        intent.putExtra( "sms_body", APP_NAME + ":" + message );
        activity.startActivity(intent);
    }



}
