package se.omegapoint.beaconmountain;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ToggleButton;

import se.omegapoint.beaconmountain.data.Database;

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

    public static void serverIPDialog(final Activity activity, final Preferences prefs, String message) {
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
                    Database.setServerIp(ip);
                }
            }
        });
        alert.show();
    }

    public static void selectClientOrServerDialog(final Activity activity, final Preferences prefs) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setCancelable(false);
        final RadioButton radioButton = new RadioButton(activity);

        alert.setTitle("Are you a Client or Server?");

        final String items[] = {"Client","Server"};

        alert.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Database.setIsClient(items[item].equals("Client"));
            }

            });

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if(Database.isClient() == null) {
                    Database.setIsClient(true);
                }
                if(Database.isClient()) {
                    serverIPDialog(activity, prefs, "Enter IP or leave blank for receiving IP via SMS");
                }
            }
        });

        alert.show();
    }
}
