package se.omegapoint.beaconmountain;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import android.widget.Toast;

import se.omegapoint.beaconmountain.data.Database;
import se.omegapoint.beaconmountain.service.DataService;
import se.omegapoint.beaconmountain.data.ClientData;

import static se.omegapoint.beaconmountain.MessageSenderHelper.readOneMessage;
import static se.omegapoint.beaconmountain.MessageSenderHelper.sendAnswerMessage;
import static se.omegapoint.beaconmountain.MessageSenderHelper.sendOneMessage;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1234;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_SMS = 1235;

    private DataService service;
    private Preferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = new Preferences(this);
        startService(new Intent(this, DataService.class));
        requestUserId();
        requestIp();
        if (prefs.getServerIp() != null) {
            startClient();
        } else {
            startServer();
        }
    }

    private void startClient() {
        //TODO

    }

    private void startServer() {
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
                            ClientData clientData = new ClientData(msg);
                            Database.update(clientData);
                            Log.v("msg", "message parsed");
                            sendAnswerMessage(client.getOutputStream(), clientData);
                            client.close();
                        } else {
                            Log.v("msg", "YAPP error");
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
        pickContact();
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

    private void requestIp() {
        DialogHelper.selectClientOrServerDialog(this,prefs,"Enter server IP or leave blank:");
    }
    private void requestPermissionForLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else{
            service.requestLocationUpdates();
        }
    }

    private void requestPermissionForSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS},
                    MY_PERMISSIONS_REQUEST_ACCESS_SMS);
        }else{
            service.requestLocationUpdates();
        }
    }


    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request it is that we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    Utils.sendSms(this, Utils.getIPAddress(), number);
                }
            }
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder binder) {
            DataService.MyBinder b = (DataService.MyBinder) binder;
            service = b.getService();
            requestPermissionForLocation();
            requestPermissionForSms();
            Toast.makeText(MainActivity.this, "Connected to service",
                    Toast.LENGTH_SHORT).show();
        }

        public void onServiceDisconnected(ComponentName className) {
            service = null;
        }
    };

}
