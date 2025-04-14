package webtech.com.courierdriver.events;

/**
 * Created by darshanz on 10/9/16.
 */

public class LocationChangedEvent {
    private double lat;
    private double lng;


    public LocationChangedEvent(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
