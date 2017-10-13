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
            Log.v("input",""+(char)c);
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

    public static void sendString( final OutputStream outputStream, String string) throws IOException {
        for(byte b : string.getBytes()) {
            outputStream.write(b);
        }

    }

    public static void sendSeparator(final OutputStream outputStream) throws IOException {
        outputStream.write(':');
    }

    public static void sendAnswerMessage(final OutputStream outputStream, ClientData clientData) throws IOException {
        String initString=clientData.answer()?"DATA":"YOLO";
        Log.v("outgoing msg", initString);
        for(byte b : initString.getBytes()) {
            outputStream.write(b);
        }
        if (clientData.answer()) {
            ClientData clients[] = Database.getClients();
            sendSeparator(outputStream);
            sendString(outputStream,""+clients.length);
            for (ClientData client:clients) {
                sendSeparator(outputStream);
                sendString(outputStream, client.getNickname());
                sendSeparator(outputStream);
                sendString(outputStream, ""+client.getLatitude());
                sendSeparator(outputStream);
                sendString(outputStream, ""+client.getLongitude());
            }

        }
        outputStream.write('\0');
        outputStream.flush();
    }

}
