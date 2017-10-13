package se.omegapoint.beaconmountain;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public static final String PREFS_NAME = "LocHatPrefs";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	public Preferences(Context context) {
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
	}

	public String getUserId(){
		return settings.getString("user_id", null);
	}

	public void setUserId(String user_id){
		editor.putString("user_id", user_id).commit();
	}

}
