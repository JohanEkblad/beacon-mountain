package se.omegapoint.beaconmountain.data;

import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Map<String,ClientData> database = new HashMap<String,ClientData>();

    public static void update(ClientData clientData) {
        database.put(clientData.getNickname(),clientData);
    }

    public static ClientData[] getClients() {
        return database.values().toArray(new ClientData[0]);
    }
}
