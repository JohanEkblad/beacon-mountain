package se.omegapoint.beaconmountain;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.omegapoint.beaconmountain.data.ClientData;
import se.omegapoint.beaconmountain.data.Database;

/**
 * Created by jonasparo on 2017-10-13.
 */

public class MessageSenderHelper {

    @NonNull
    public static String readOneMessage(InputStream inputStream) throws IOException {
        String msg = "";
        int c = 0;
        while ((c = inputStream.read()) != -1) {
            if (c == '\0') {
                break;
            }
            msg += (char) c;
        }
        Log.v("incoming msg",msg);
        return msg;
    }

    public static void sendOneMessage(String msg, final OutputStream outputStream) throws IOException {
        Log.v("outgoing msg", msg);
        for(byte b : msg.getBytes()) {
            outputStream.write(b);
        }
        outputStream.write('\0');
        outputStream.flush();
    }

    public static void sendAnswerMessage(final OutputStream outputStream, ClientData clientData) throws IOException {
        StringBuffer sb=new StringBuffer();
        sb.append(clientData.answer()?"DATA":"YOLO");
        if (clientData.answer()) {
            ClientData clients[] = Database.getClients();
            sb.append(':');
            sb.append(clients.length);
            for (ClientData client:clients) {
                sb.append(':');
                sb.append(client.getNickname());
                sb.append(':');
                sb.append(client.getLatitude());
                sb.append(':');
                sb.append(client.getLongitude());
            }
        }
        sendOneMessage(sb.toString(), outputStream);
    }
}
