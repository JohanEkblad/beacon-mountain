package se.omegapoint.beaconmountain.service;


import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

import javax.net.SocketFactory;

import se.omegapoint.beaconmountain.MessageSenderHelper;
import se.omegapoint.beaconmountain.Preferences;


public class LocationListener implements android.location.LocationListener {
    private static final String TAG = LocationListener.class.getSimpleName();
    public static final int GPS_MIN_TIME_MILLIS = 10 * 1000; //10 seconds
    private static final int POSITION_ACCURACY = 50;
    private static final int MINIMAL_DISTANCE = 25;

    private Preferences prefs;
    private Location lastLocation;
    private DataService service;

    public LocationListener(DataService service, Preferences prefs) {
        this.service = service;
        this.prefs = prefs;
    }


    public void onLocationChanged(Location location) {
        if (prefs.getUserId() == null)
            return;

        if (location.getAccuracy() < POSITION_ACCURACY) {
            //activity.addText("Got accurate location: " + location.getAccuracy());
            Log.v(TAG, "Got accurate location: " + location.getAccuracy());
            if (lastLocation == null) { //First location, store it
                notifyLocationUpdate(location);
            } else {
                if (location.distanceTo(lastLocation) > MINIMAL_DISTANCE) {
                    notifyLocationUpdate(location);
                }
            }
            lastLocation = location;
        } else {
            Log.v(TAG, "Got inaccurate location: " + location.getAccuracy());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void storeData(Location location) {
        notifyLocationUpdate(location);
    }

    private void notifyLocationUpdate(Location location) {
        Log.v(TAG, "Sending broadcast");
        Log.v(TAG, "HELO:" + prefs.getUserId() + ":" + location.getLatitude() + ":" + location.getLongitude() + ":Y\0");
        new SendLocationTask().execute(location);
    }

    private class SendLocationTask extends AsyncTask<Location, Integer, Void> {
        protected Void doInBackground(Location... location) {
            Log.v(TAG, "Doing stuff in background");
            if (prefs.getServerIp() != null) {
                try {
                    Socket socket = SocketFactory.getDefault().createSocket(prefs.getServerIp(), 4711);
                    MessageSenderHelper.sendOneMessage("HELO:" + prefs.getUserId() + ":" + location[0].getLatitude() + ":" + location[0].getLongitude() + ":Y\0", socket.getOutputStream());
                    String msg = MessageSenderHelper.readOneMessage(socket.getInputStream());
                    Log.v(TAG, "Got message: " + msg);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }

}


