package se.omegapoint.beaconmountain;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import android.widget.Toast;

import se.omegapoint.beaconmountain.service.DataService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234;

    private DataService service;
    private Preferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.v("tag","hej");
                    ServerSocket serverSocket = ServerSocketFactory.getDefault().createServerSocket(4711);
                    Log.v("server",serverSocket.toString());
                    Socket client;
                    while((client = serverSocket.accept()) != null) {
                        Log.v("client", client.toString());
                        String msg = readOneMessage(client.getInputStream());
                        if (msg.startsWith("HELO")) {
                            Log.v("msg", "hello message detected");
                            sendOneMessage("YOLO", client.getOutputStream());
                            client.close();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        prefs = new Preferences(this);
        startService(new Intent(this, DataService.class));
        requestUserId();
    }

    @Override
    protected void onResume() {
        bindService(new Intent(this, DataService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unbindService(serviceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, DataService.class));
        super.onDestroy();

    }

    private void requestUserId() {
        String userId = prefs.getUserId();
        if (userId == null)
            DialogHelper.selectUserDialog(this, prefs, "Choose your user");
    }

    private void requestPermissionForLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else{
            service.requestLocationUpdates();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            DataService.MyBinder b = (DataService.MyBinder) binder;
            service = b.getService();
            requestPermissionForLocation();
            Toast.makeText(MainActivity.this, "Connected to service",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };

    @NonNull
    private String readOneMessage(InputStream inputStream) throws IOException {
        String msg = "";
        int c = 0;
        while ((c = inputStream.read()) != -1) {
            Log.v("input",""+(char)c);
            if (c == '\0') {
                break;
            }
            msg += (char) c;
        }
        Log.v("incoming msg",msg);
        return msg;
    }

    private void sendOneMessage(String msg, final OutputStream outputStream) throws IOException {
        Log.v("outgoing msg", msg);
        for(byte b : msg.getBytes()) {
            outputStream.write(b);
        }
        outputStream.write('\0');
        outputStream.flush();
    }
}
