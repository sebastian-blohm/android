package hm.blo.paperlog.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import hm.blo.paperlog.model.Printable;
import hm.blo.paperlog.model.Printing;

/**
 * ViewModel of a fragment displaying Latitude/Longitude coordinate information based on the device's GPS.
 */
public class LatLongViewModel extends ViewModel implements Printable {

    protected MutableLiveData<Double> mLatitude = new MutableLiveData<Double>();
    protected MutableLiveData<Double> mLongitude = new MutableLiveData<Double>();
    protected MutableLiveData<String> mStatusText = new MutableLiveData<String>();

    LocationManager locationManager;

    Consumer<Location> locationConsumer;

    LocationListener locationListener;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'[R]'HH:mm");


    Executor executor;

    public void initialize(Context context) {
        Printing.addPrintable(this);

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        executor = context.getMainExecutor();

        locationConsumer = new Consumer<Location>() {
            @Override
            public void accept(Location location) {
                setLocation(location);
                mStatusText.setValue(String.format(location.getProvider() + " " + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)));
            }
        };

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setLocation(location);
                mStatusText.setValue(String.format(location.getProvider() + " " + LocalTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                mStatusText.setValue("Status Changed: " + String.valueOf(status));
            }

            @Override
            public void onProviderEnabled(String provider) {
                mStatusText.setValue("Provider Enabled: " + provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                mStatusText.setValue("Provider Disabled: " + provider);
            }
        };



        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(settingsIntent);
        } else {
            updateLocation();
        }
    }

    // https://stackoverflow.com/questions/64853673/how-to-use-locationmarnagergetcurrentlocation-in-android
    @SuppressLint("MissingPermission")
    public void updateLocation() {
        mStatusText.setValue("updating");
        //LocationProvider provider = locationManager.getProvider(LocationManager.GPS_PROVIDER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(locationManager.GPS_PROVIDER, null, executor, locationConsumer);
        } else {
            try {
                locationManager.requestSingleUpdate(LocationManager.FUSED_PROVIDER, locationListener, (Looper) null);
                // TODO: consider going back to the below (makes indoor testing difficult)
                // locationManager.requestSingleUpdate(locationManager.GPS_PROVIDER, locationListener, (Looper) null);
            }catch (SecurityException e) {
                mStatusText.setValue("missing permission");
            }
        }


    }

    public void setLocation(Location location) {
        mLatitude.setValue(location.getLatitude());
        mLongitude.setValue(location.getLongitude());
    }

    protected String valueToString(){
        if (mLongitude == null || mLatitude == null) {
            return "null";
        }
        double lat = mLatitude.getValue();
        double longV = mLongitude.getValue();

        int latDegree = (int)Math.floor(lat);
        double latMinutes = (lat - latDegree) * 60;
        double latSeconds = (latMinutes - Math.floor(latMinutes)) * 60;
        char latDirection = lat >= 0 ? 'N' : 'S';

        int longDegree = (int)Math.floor(longV);
        double longMinutes = (longV - longDegree) * 60;
        double longSeconds = (longMinutes - Math.floor(longMinutes)) * 60;
        char longDirection = longV >= 0 ? 'E' : 'W';

        return String.format("%02d°%02.0f'%01.1f\" %c, %03d°%02.0f'%01.1f\" %c",latDegree, latMinutes, latSeconds, latDirection, longDegree, longMinutes, longSeconds, longDirection);
    }

    public String asUiString() {
        String valueString = this.valueToString();
        return String.format("%s", valueString);
    }


    public MutableLiveData<Double> getLatitude() {
        return mLatitude;
    }

    public MutableLiveData<Double> getLongitude() {
        return mLongitude;
    }

    public MutableLiveData<String> getStatusText() {
        return mStatusText;
    }

    @Override
    public String getPrintString() {
        String date = formatter.format(LocalDateTime.now());
        return date + "\n" + this.valueToString();
    }

    @Override
    public boolean hasContent() {
        return true;
    }
}