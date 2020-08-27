package util;
/**
 * A custom LatLng class, necessary for storing locations in the fireStore database
 * @author  David Elfving Long
 * @version 1.0
 * @since   2020-08-27
 */
public class LatLng {
    private Double longitude;
    private Double latitude;

    public LatLng() {
    }

    public LatLng(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
}
