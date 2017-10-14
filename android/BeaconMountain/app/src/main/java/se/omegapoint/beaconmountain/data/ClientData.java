package se.omegapoint.beaconmountain.data;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;

public class ClientData {
    private String nickname;
    private double latitude = -1d;
    private double longitude = -1d;
    private Location location = null;
    private boolean answer;

    public static List<ClientData> fromDATA(String data) {
        List<ClientData>clientData = new ArrayList<>();
        String[] split = data.split(":");
        String nickname = null;
        double lat = -1d;
        double lng = -1d;
        for(int i=2; i<split.length; i++) {
            if(i%3==0) {
                lat = Double.parseDouble(split[i]);
            } else if(i%3==1) {
                lng = Double.parseDouble(split[i]);
            } else if(i%3==2) {
                nickname = split[i];
            }
            if(i%3==1) {
                clientData.add(new ClientData(nickname, lat, lng));
            }
        }
        return clientData;
    }

    public ClientData(String nick, double lat, double lng) {
        this.nickname = nick;
        this.latitude = lat;
        this.longitude = lng;
        if(longitude != -1d && latitude != -1d){
            this.location = new Location("");
            this.location.setLatitude(latitude);
            this.location.setLongitude(longitude);
        }
    }

    public ClientData(String protocolMessage) {
        String parts[] = protocolMessage.split(":");
        if (parts.length != 5) { // HELO:NICK:54.444:12:34:N
            throw new RuntimeException("Illegal number of protocol parts");
        }
        nickname=parts[1];

        try
        {
            latitude = Double.parseDouble(parts[2]);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("Illegal latitude");
        }

        try
        {
            longitude = Double.parseDouble(parts[3]);
        }
        catch (NumberFormatException nfe) {
            throw new RuntimeException("Illegal longitude");
        }

        if(longitude != -1d && latitude != -1d){
            location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
        }

        if (parts[4].equals("Y") || parts[4].equals("N")) {
            answer = parts[4].equals("Y");
        } else {
            throw new RuntimeException("Illegal answer");
        }

    }

    public String getNickname() {
        return this.nickname;
    }

    public Location getLocation() {
        return this.location;
    }

    public double getLatitude(){ return this.latitude;}

    public double getLongitude(){return this.longitude;}

    public boolean answer() {
        return this.answer;
    }

    public String toString(){
        StringBuffer sb = new StringBuffer("");
        sb.append(nickname);
        sb.append("Latitude: ").append(latitude);
        sb.append("Longitude: ").append(longitude);
        return sb.toString();
    }

    public String distanceString(){
        StringBuffer sb = new StringBuffer("");
        sb.append(nickname);
        Location ownLocation = Database.getLastLocation();
        if(location!= null && ownLocation != null)
            sb.append(" - distance: ").append(location.distanceTo(Database.getLastLocation()));
        else
            sb.append(" either own location or remote location not retrieved");
        return sb.toString();
    }


}
