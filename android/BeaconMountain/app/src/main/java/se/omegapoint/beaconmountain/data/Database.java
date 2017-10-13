package se.omegapoint.beaconmountain.data;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String,ClientData> database = new HashMap<String,ClientData>();
    private static String serverIp = null;

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
}
