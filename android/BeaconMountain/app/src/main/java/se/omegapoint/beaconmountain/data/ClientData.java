package se.omegapoint.beaconmountain.data;

public class ClientData {
    private String nickname;
    private double latitude;
    private double longitude;
    private boolean answer;

    public ClientData(String protocolMessage)
    {
        String parts[] = protocolMessage.split(":");
        if (parts.length != 5) { // HELO:NICK:54.444:12:34:N
            throw new RuntimeException("Illegal number of prtocol parts");
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

        if (parts[4] == "Y" || parts[4] == "N") {
            answer = parts[4] == "Y";
        } else {
            throw new RuntimeException("Illegal answer");
        }

    }

    public String getNickname() {
        return this.nickname;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public boolean answer() {
        return this.answer;
    }
}
