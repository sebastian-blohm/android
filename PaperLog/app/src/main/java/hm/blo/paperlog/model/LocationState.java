package hm.blo.paperlog.model;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

/*
Global state of the location module
TODO: this may not be needed if we can use LatLongViewModel for all items displaying locations
 */
public class LocationState {

    private static LocationState myState;

    protected MutableLiveData<Double> mLatitude = new MutableLiveData<>();
    protected MutableLiveData<Double> mLongitude = new MutableLiveData<>();
    protected Location latestLocation;


    public static LocationState LocationStateSingleton() {
        if (myState == null) {
            myState = new LocationState();
        }

        return myState;
    }

    public LocationState() {
        mLatitude.setValue(0.0);
        mLongitude.setValue(0.0);
    }

    public void setLocation(Location location) {
        mLatitude.setValue(location.getLatitude());
        mLongitude.setValue(location.getLongitude());
        this.latestLocation = location;
    }

    public MutableLiveData<Double> getLatitude() {
        return mLatitude;
    }

    public MutableLiveData<Double> getLongitude() {
        return mLongitude;
    }

    public Location getLatestLocation(){
        return latestLocation;
    }

    public String latestLocationToString(){
        if (mLongitude == null || mLatitude == null) {
            return "null";
        }
        double lat = mLatitude.getValue();
        double longV = mLongitude.getValue();
        return latLongToString(lat, longV);
    }

    public static String latLongToString(double lat, double longV){
        int latDegree = (int)Math.floor(lat);
        double latMinutes = Math.floor((lat - latDegree) * 60);
        double latSeconds = (latMinutes - Math.floor(latMinutes)) * 60;
        char latDirection = lat >= 0 ? 'N' : 'S';

        int longDegree = (int)Math.floor(longV);
        double longMinutes = Math.floor((longV - longDegree) * 60);
        double longSeconds = (longMinutes - Math.floor(longMinutes)) * 60;
        char longDirection = longV >= 0 ? 'E' : 'W';

        return String.format("%02d°%02.0f'%01.1f\" %c, %03d°%02.0f'%01.1f\" %c",latDegree, latMinutes, latSeconds, latDirection, longDegree, longMinutes, longSeconds, longDirection);
    }

    public static int[] ToDegreesMinutesSeconds(double value) {
        int degrees = (int)Math.floor(value);
        double minutes = (value - degrees) * 60;
        double seconds = (minutes - Math.floor(minutes)) * 60;
        return new int[] {degrees, (int)minutes, (int)seconds};
    }
}
