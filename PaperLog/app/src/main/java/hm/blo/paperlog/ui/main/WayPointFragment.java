package hm.blo.paperlog.ui.main;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import hm.blo.paperlog.R;
import hm.blo.paperlog.model.LocationState;
import hm.blo.paperlog.model.Printable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WayPointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WayPointFragment extends Fragment implements Printable {

    private NumberPicker latDegrees;
    private NumberPicker latMinutes;
    private NumberPicker latSeconds;
    private NumberPicker longDegrees;
    private NumberPicker longMinutes;
    private NumberPicker longSeconds;
    private Switch switchPrint;
    private Switch switchNorthSouth;
    private Switch switchEastWest;

    private static final String ARG_ParamLatitude = "ParamLatitude";
    private static final String ARG_ParamLongitude = "ParamLongitude";

    protected LocationState locationState = LocationState.LocationStateSingleton();

    private String mParamLatitude;
    private String mParamLongitude;

    public WayPointFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ParamLatitude Parameter 1.
     * @param ParamLongitude Parameter 2.
     * @return A new instance of fragment WayPointFragment.
     */
    public static WayPointFragment newInstance(String ParamLatitude, String ParamLongitude) {
        WayPointFragment fragment = new WayPointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ParamLatitude, ParamLatitude);
        args.putString(ARG_ParamLongitude, ParamLongitude);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamLatitude = getArguments().getString(ARG_ParamLatitude);
            mParamLongitude = getArguments().getString(ARG_ParamLongitude);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View result = inflater.inflate(R.layout.fragment_way_point, container, false);

        EditText textViewName = (EditText) result.findViewById(R.id.textViewName);
        TextView textViewInfo = (TextView) result.findViewById(R.id.textViewInfo);
        Switch switchEdit = (Switch) result.findViewById(R.id.switchEdit);
        switchPrint = (Switch) result.findViewById(R.id.switchPrint);
        switchNorthSouth = (Switch) result.findViewById(R.id.switchNorthSouth);
        switchEastWest = (Switch) result.findViewById(R.id.switchEastWest);
        Button buttonLatest = (Button) result.findViewById(R.id.buttonLatest);

        View editView = result.findViewById(R.id.viewEdit);

        latDegrees = (NumberPicker) result.findViewById(R.id.latDegrees);
        latDegrees.setMinValue(0);
        latDegrees.setMaxValue(90);

        latMinutes = (NumberPicker) result.findViewById(R.id.latMinutes);
        latMinutes.setMinValue(0);
        latMinutes.setMaxValue(59);

        latSeconds = (NumberPicker) result.findViewById(R.id.latSeconds);
        latSeconds.setMinValue(0);
        latSeconds.setMaxValue(59);

        longDegrees = (NumberPicker) result.findViewById(R.id.longDegrees);
        longDegrees.setMinValue(0);
        longDegrees.setMaxValue(180);

        longMinutes = (NumberPicker) result.findViewById(R.id.longMinutes);
        longMinutes.setMinValue(0);
        longMinutes.setMaxValue(59);

        longSeconds = (NumberPicker) result.findViewById(R.id.longSeconds);
        longSeconds.setMinValue(0);
        longSeconds.setMaxValue(59);

        // TODO: put viewmodel behind location and name
        // TODO: any change should trigger re-calculation
        // TODO: parse parameters (and add one for name)
        // TODO: search
            // TODO: special role for "start" and Uhrzeit


        Observer<Double> latLongObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double s) {
                textViewInfo.setText(getLocationString(false));
            }
        };

        buttonLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = locationState.getLatestLocation();
                int[] latCoordinates = LocationState.ToDegreesMinutesSeconds(location.getLatitude());
                latDegrees.setValue(latCoordinates[0]);
                latMinutes.setValue(latCoordinates[1]);
                latSeconds.setValue(latCoordinates[2]);
                int[] longCoordinates = LocationState.ToDegreesMinutesSeconds(location.getLongitude());
                longDegrees.setValue(longCoordinates[0]);
                longMinutes.setValue(longCoordinates[1]);
                longSeconds.setValue(longCoordinates[2]);
            }
        }

        );

        locationState.getLatitude().observe(getViewLifecycleOwner(), latLongObserver);
        locationState.getLongitude().observe(getViewLifecycleOwner(), latLongObserver);

        switchEdit.setChecked(false);
        editView.setVisibility(View.GONE);

        switchEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean value = switchEdit.isChecked();
                editView.setVisibility(value ? View.VISIBLE : View.GONE);
                textViewName.setEnabled(value);
                textViewName.setTextColor(Color.BLACK);
            }
        });

        // Inflate the layout for this fragment
        return result;
    }

    private String getLocationString(boolean forPrint) {
        double pickerLat = WayPointFragment.ToDoubleCoordinates(latDegrees.getValue() * (switchNorthSouth.isChecked() ? 1 : -1), latMinutes.getValue(), latSeconds.getValue());
        double pickerLong = WayPointFragment.ToDoubleCoordinates(longDegrees.getValue() * (switchEastWest.isChecked() ? 1 : -1), longMinutes.getValue(), longSeconds.getValue());
        Location location = LocationState.LocationStateSingleton().getLatestLocation();
        double distance = WayPointFragment.DistanceInNauticalMiles(pickerLat, pickerLong, location.getLatitude(), location.getLongitude());
        double bearing = WayPointFragment.BearingDegrees(location.getLatitude(), location.getLongitude(), pickerLat, pickerLong);
        return String.format("%sDTW: %1.1f nm%sBRG: %1.1f°", forPrint ? "[L]" : "", distance, forPrint ? "[R]" : " ",bearing);
    }

    static final double EARTH_RADIUS = 3443.9308855292; // in nautical miles
    static final double TO_RADIAN = Math.PI / 180.0;
    // using https://www.movable-type.co.uk/scripts/latlong.html
    public static double DistanceInNauticalMiles(double lat1, double long1, double lat2, double long2){
        double phi1 = lat1 * TO_RADIAN;
        double phi2 = lat2 * TO_RADIAN;
        double deltaPhi = (lat2 - lat1) * TO_RADIAN;
        double deltaLambda = (long2 - long1) * TO_RADIAN;

        double a = Math.sin(deltaPhi/2) * Math.sin(deltaPhi/2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda/2) * Math.sin(deltaLambda/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double distance = EARTH_RADIUS * c;

        return distance;
    }

    public static double BearingDegrees(double lat1, double long1, double lat2, double long2) {
        double phi1 = lat1 * TO_RADIAN;
        double phi2 = lat2 * TO_RADIAN;
        double deltaLambda = (long2 - long1) * TO_RADIAN;
        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1)*Math.sin(phi2) -
                        Math.sin(phi1)*Math.cos(phi2)*Math.cos(deltaLambda);
        double theta = Math.atan2(y, x);
        double bearing = (theta/TO_RADIAN + 360) % 360; // in degrees
        return bearing;
    }

    public static double ToDoubleCoordinates(int degrees, int minutes, int seconds){
        return degrees + minutes / 60.0 + seconds / 3600.0;

    }

    @Override
    public String getPrintString() {
        return this.getLocationString(true);
    }

    @Override
    public boolean hasContent() {
        return switchPrint.isChecked();
    }
}