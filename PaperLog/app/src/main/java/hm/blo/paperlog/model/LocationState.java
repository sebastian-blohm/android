package hm.blo.paperlog.model;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
Global state of the location module
TODO: this may not be needed if we can use LatLongViewModel for all items displaying locations
 */
public class LocationState {

    private static LocationState myState;

    public static final double METERS_PER_NAUTICAL_MILE = 1852; // https://en.wikipedia.org/wiki/Nautical_mile

    // from https://www.regextester.com/97401, format: 40°26′46″N,79°58′56″W
    private static final String dmsRegex = "([0-9]{1,2})[:|°]([0-9]{1,2})[:|'|′]?([0-9]{1,2}(?:\\.[0-9]+){0,1})?[\"|″]([N|S]),([0-9]{1,3})[:|°]([0-9]{1,2})[:|'|′]?([0-9]{1,2}(?:\\.[0-9]+){0,1})?[\"|″]([E|W])";
    private static Pattern dmsPattern = Pattern.compile(dmsRegex);

    //format: 40°26'.46N,79°58'.56W and 40°26,46'N,079°58,56'W
    private static final String dmRegex = "([0-9]{1,2})[:|°]([0-9]{1,2})[:|'|′]?(?:[\\.,]([0-9]{1,4})'?)?([N|S]),([0-9]{1,3})[:|°]([0-9]{1,2})[:|'|′]?(?:[\\.,]([0-9]{1,4})'?)?([EW])";
    private static Pattern dmPattern = Pattern.compile(dmRegex);


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
        char latDirection = lat >= 0 ? 'N' : 'S';
        lat = Math.abs(lat);
        int latDegree = (int)Math.floor(lat);
        double latMinutes = (lat - latDegree) * 60;

        char longDirection = longV >= 0 ? 'E' : 'W';
        longV = Math.abs(longV);
        int longDegree = (int)Math.floor(longV);
        double longMinutes = (longV - longDegree) * 60;


        return String.format("%02d°%05.2f' %c, %03d°%05.2f' %c",latDegree, latMinutes, latDirection, longDegree, longMinutes, longDirection);
    }

    public static String latLongToDmsString(double lat, double longV){
        int latDegree = (int)Math.floor(lat);
        double latMinutes = Math.floor((lat - latDegree) * 60);
        double latSeconds = (((lat - latDegree) * 60) - latMinutes) * 60;
        char latDirection = lat >= 0 ? 'N' : 'S';
        latDegree = Math.abs(latDegree);

        int longDegree = (int)Math.floor(longV);
        double longMinutes = Math.floor((longV - longDegree) * 60);
        double longSeconds = (((longV - longDegree) * 60) - longMinutes) * 60;
        char longDirection = longV >= 0 ? 'E' : 'W';
        longDegree = Math.abs(longDegree);

        return String.format("%02d°%02.0f'%01.1f\" %c, %03d°%02.0f'%01.1f\" %c",latDegree, latMinutes, latSeconds, latDirection, longDegree, longMinutes, longSeconds, longDirection);
    }

    public static double[] DmsStringToLatLong(String dmsString) {
        Matcher dmMatcher = dmPattern.matcher(dmsString);
        if (dmMatcher.find()) {
            double latitude = dmToDecimal(Integer.parseInt(dmMatcher.group(1)), Integer.parseInt(dmMatcher.group(2)), Integer.parseInt(dmMatcher.group(3)));
            if (dmMatcher.group(4).equalsIgnoreCase("s")) {
                latitude = -latitude;
            }

            double longitude = dmToDecimal(Integer.parseInt(dmMatcher.group(5)), Integer.parseInt(dmMatcher.group(6)), Integer.parseInt(dmMatcher.group(7)));
            if (dmMatcher.group(8).equalsIgnoreCase("w")) {
                longitude = -longitude;
            }

            return new double[] {latitude, longitude};
        }

        Matcher dmsMatcher = dmsPattern.matcher(dmsString);
        if (dmsMatcher.find()) {
            double latitude = dmsToDecimal(Integer.parseInt(dmsMatcher.group(1)), Integer.parseInt(dmsMatcher.group(2)), Integer.parseInt(dmsMatcher.group(3)));
            if (dmsMatcher.group(4).equalsIgnoreCase("s")) {
                latitude = -latitude;
            }

            double longitude = dmsToDecimal(Integer.parseInt(dmsMatcher.group(5)), Integer.parseInt(dmsMatcher.group(6)), Integer.parseInt(dmsMatcher.group(7)));
            if (dmsMatcher.group(8).equalsIgnoreCase("w")) {
                longitude = -longitude;
            }

            return new double[] {latitude, longitude};
        }

        return null;
    }

    private static double dmsToDecimal(int degrees, int minutes, int seconds) {
        return degrees + minutes / 60.0 + seconds / 3600.0;
    }

    private static double dmToDecimal(int degrees, int minutes, int minutesDecimals) {
        double totalMinutes = minutes + (minutesDecimals > 0 ? (minutesDecimals / Math.pow(10,(Math.floor(Math.log10(minutesDecimals)) +1))) : 0);
        return degrees + totalMinutes / 60.0;
    }

    public static int[] ToDegreesMinutesDecimals(double value, int numDecimals) {
        int degrees = (int)Math.floor(value);
        double minutes = (value - degrees) * 60;
        double decimals = (minutes - Math.floor(minutes)) * Math.pow(10,numDecimals);
        return new int[] {degrees, (int)minutes, (int)decimals};
    }
}
