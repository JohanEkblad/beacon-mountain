package se.omegapoint.beaconmountain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.SocketFactory;

public class MainActivity extends AppCompatActivity {

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
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
