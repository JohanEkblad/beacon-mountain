package se.omegapoint.beaconmountain.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import se.omegapoint.beaconmountain.MainActivity;
import se.omegapoint.beaconmountain.Preferences;


public class DataService extends Service {
    private static final String TAG = "DataService";
    private static final int NOTIFICATON_ID = 1134;

    private final IBinder mBinder = new MyBinder();
    private LocationListener locationListener;
    private Preferences prefs;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = new Preferences(this);
        locationManager = (LocationManager) this.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("DataService", "Removing location listener");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            android.util.Log.v(TAG, "Not permitted to access fine location");
            return;
        }
        if(locationListener != null)
          locationManager.removeUpdates(locationListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("DataService", "Starting service");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification n  = new NotificationCompat.Builder(this)
                .setContentTitle("BeaconMountain")
                .setContentText("Information about application/status")
                //.setSmallIcon()
                .setContentIntent(pIntent).build();
        startForeground(NOTIFICATON_ID, n);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        return false;
    }

    public class MyBinder extends Binder {
        public DataService getService() {
            return DataService.this;
        }
    }

    public void requestLocationUpdates() {
        Log.v(TAG, "requestLocationUpdates");

        if(locationListener != null)
            return;

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Not permitted to access fine location");
            return;
        }

        boolean enabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            Log.v(TAG, "Provider not enabled - enabling...");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivity(intent);
        }

        locationListener = new LocationListener(this, prefs);
        locationListener.notifyLocationUpdate(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, locationListener.GPS_MIN_TIME_MILLIS,
                0, locationListener);

    }
}
