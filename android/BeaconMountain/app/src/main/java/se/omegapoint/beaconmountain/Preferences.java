package se.omegapoint.beaconmountain;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	public static final String PREFS_NAME = "BeaconMountainPrefs";
	private SharedPreferences settings;
	private SharedPreferences.Editor editor;
	
	public Preferences(Context context) {
		settings = context.getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
	}

	public String getUserId(){
		return settings.getString("user_id", null);
	}

	public String getServerIp() {
		return settings.getString("server_ip", null);
	}

	public boolean isClient() {return settings.getBoolean("is_client", true);}

	public void setUserId(String user_id){
		editor.putString("user_id", user_id).commit();
	}

	public void setServerIp(String serverIp) {
		editor.putString("server_ip", serverIp).commit();
	}

	public void setIsClient(boolean isClient) {editor.putBoolean("is_client", isClient).commit();}


}
