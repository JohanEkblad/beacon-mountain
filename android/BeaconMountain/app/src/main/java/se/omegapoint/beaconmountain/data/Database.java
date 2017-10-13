package se.omegapoint.beaconmountain.data;

import android.location.Location;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String,ClientData> database = new HashMap<String,ClientData>();
    private static String serverIp = null;
    private static Boolean isClient = null;
    private static Location lastLocation = null;

    public static void update(ClientData clientData) {
        database.put(clientData.getNickname(),clientData);
    }

    public static ClientData[] getClients() {
        return database.values().toArray(new ClientData[0]);
    }

    public static void setServerIp(String serverIp){
        Database.serverIp = serverIp;
    }

    public static String getServerIp(){
        return serverIp;
    }

    public static void setIsClient(Boolean isClient) {
        Database.isClient = isClient;
    }

    public static Boolean isClient() {
        return isClient;
    }
    public static void setLastLocation(Location location){
        Database.lastLocation = location;
    }

    public static Location getLastLocation(){
        return lastLocation;
    }

}
