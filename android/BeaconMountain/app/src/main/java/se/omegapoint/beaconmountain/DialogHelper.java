package se.omegapoint.beaconmountain;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

/**
 * Created by mattias on 2016-12-29.
 */

public class DialogHelper {


    public static void selectUserDialog(final Activity activity, final Preferences prefs, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        final EditText edittext = new EditText(activity);
        alert.setMessage(message);
        alert.setTitle("User");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String userId = edittext.getText().toString();
                prefs.setUserId(userId);
            }
        });
        alert.show();
    }

    public static void selectClientOrServerDialog(final Activity activity, final Preferences prefs, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        final EditText edittext = new EditText(activity);

        alert.setMessage(message);
        alert.setTitle("Server ip");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String ip = edittext.getText().toString();
                if(ip != null && !ip.isEmpty()) {
                    prefs.setServerIp(ip);
                }
            }
        });
        alert.show();
    }

}
