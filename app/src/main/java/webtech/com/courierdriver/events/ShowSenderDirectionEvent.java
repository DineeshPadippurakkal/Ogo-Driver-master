package webtech.com.courierdriver.events;

/*
Created by ​
Hannure Abdulrahim


on 10/24/2018.
 */
public class ShowSenderDirectionEvent {
    private String lat;
    private String lon;

    public ShowSenderDirectionEvent(String senderLat, String senderLong) {
        this.lat = senderLat;
        this.lon = senderLong;
    }




    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }


}
