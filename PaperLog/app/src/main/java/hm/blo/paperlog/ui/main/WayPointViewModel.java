package hm.blo.paperlog.ui.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import hm.blo.paperlog.model.LocationState;
import hm.blo.paperlog.model.Printable;
import hm.blo.paperlog.model.Printing;

/**
 * ViewModel of a fragment displaying Latitude/Longitude coordinate information based on the device's GPS.
 */
public class WayPointViewModel extends ViewModel implements Printable {

    protected LocationState locationState = LocationState.LocationStateSingleton();

    protected MutableLiveData<Double> mLatitude = new MutableLiveData<>();
    protected MutableLiveData<Double> mLongitude = new MutableLiveData<>();

    protected MutableLiveData<String> mStatusText = new MutableLiveData<String>();
    protected MutableLiveData<String> mName = new MutableLiveData<String>();
    protected MutableLiveData<Boolean> mSwitchPrint = new MutableLiveData<>();

    public WayPointViewModel() {
        this.mLatitude.setValue(0.0);
        this.mLongitude.setValue(0.0);
        Printing.addPrintable(this);
    }

    public void initialize(Context context) {
        mName.setValue("");
        // TODO: initialize lat and long (from config?)
    }


    public MutableLiveData<Double> getLatitude() {
        return this.getLatitude();
    }

    public MutableLiveData<Double> getLongitude() {
        return this.getLongitude();
    }

    public MutableLiveData<String> getStatusText() {
        return mStatusText;
    }

    public MutableLiveData<String> getName() { return mName; }

    public void setLatLong(double latitude, double longitude){
        this.mLatitude.setValue(latitude);
        this.mLongitude.setValue(longitude);
    }

    public String getLocationString(boolean forPrint) {
        double pickerLat = this.mLatitude.getValue();
        double pickerLong = this.mLongitude.getValue();
        Location location = LocationState.LocationStateSingleton().getLatestLocation();
        if (location != null) {
            // double distance = WayPointFragment.DistanceInNauticalMiles(pickerLat, pickerLong, location.getLatitude(), location.getLongitude());
            // double bearing = WayPointFragment.BearingDegrees(location.getLatitude(), location.getLongitude(), pickerLat, pickerLong);
            float[] distanceAndBearing = new float[2];
            Location.distanceBetween(pickerLat, pickerLong, location.getLatitude(), location.getLongitude(), distanceAndBearing);
            if (distanceAndBearing[1] < 0) {
                distanceAndBearing[1] += 360.0;
            }
            return String.format("%sDTW: %1.1f nm%sBRG: %1.1f°", forPrint ? "[L]" : "", distanceAndBearing[0] / LocationState.METERS_PER_NAUTICAL_MILE, forPrint ? "[R]" : " ",distanceAndBearing[1]);
        }

        return "location not initialized";
    }

    @Override
    public String getPrintString() {
        return
                "[L]" + mName.getValue() + "\n" +
                this.getLocationString(true);

    }

    @Override
    public boolean hasContent() {
        // TODO: hook up switch
        return mSwitchPrint.getValue();
    }
}