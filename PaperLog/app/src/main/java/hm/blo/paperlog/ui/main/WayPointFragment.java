package hm.blo.paperlog.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

import hm.blo.paperlog.R;
import hm.blo.paperlog.model.LocationState;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WayPointFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WayPointFragment extends Fragment implements TabLayout.OnTabSelectedListener{

    private WayPointViewModel mViewModel;
    private NumberPicker latDegrees;
    private NumberPicker latMinutes;
    private NumberPicker latSeconds;
    private NumberPicker longDegrees;
    private NumberPicker longMinutes;
    private NumberPicker longSeconds;
    private Switch switchPrint;
    private Switch switchNorthSouth;
    private Switch switchEastWest;

    private static final String ARG_ParamName = "ParamName";
    private static final String ARG_ParamPosition = "ParamPosition";

    protected LocationState locationState = LocationState.LocationStateSingleton();

    private String mParamName = "Waypoint";
    private String mParamPosition = "!latest";

    Switch switchEdit;
    View editView;
    EditText textViewName;

    public WayPointFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param ParamName Name of the waypoint. Convention: if the name starts with !, it is run through the search functionality.
     * @return A new instance of fragment WayPointFragment.
     */
    public static WayPointFragment newInstance(String ParamName) {
        WayPointFragment fragment = new WayPointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ParamName, ParamName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(WayPointViewModel.class);
        mViewModel.initialize(this.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View result = inflater.inflate(R.layout.fragment_way_point, container, false);

        textViewName = (EditText) result.findViewById(R.id.textViewName);
        TextView textViewInfo = (TextView) result.findViewById(R.id.textViewInfo);
        switchEdit = (Switch) result.findViewById(R.id.switchEdit);
        switchPrint = (Switch) result.findViewById(R.id.switchPrint);
        switchNorthSouth = (Switch) result.findViewById(R.id.switchNorthSouth);
        switchEastWest = (Switch) result.findViewById(R.id.switchEastWest);
        Button buttonLatest = (Button) result.findViewById(R.id.buttonLatest);
        Button buttonSearch = (Button) result.findViewById(R.id.buttonSearchName);

        editView = result.findViewById(R.id.viewEdit);

        NumberPicker.OnValueChangeListener pickerListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                updateLocationFromPickers();
            }
        };

        switchPrint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mViewModel.mSwitchPrint.setValue(b);
            }
        });

        textViewName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                String newText = charSequence.toString();
//                if (!mViewModel.getName().getValue().equals(newText)) {
//                    mViewModel.getName().setValue(newText);
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        textViewName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String newText = textViewName.getText().toString();
                    if (!mViewModel.getName().getValue().equals(newText)) {
                        mViewModel.getName().setValue(newText);
                    }
                }
            }
        });


        latDegrees = (NumberPicker) result.findViewById(R.id.latDegrees);
        latDegrees.setMinValue(0);
        latDegrees.setMaxValue(90);
        latDegrees.setOnValueChangedListener(pickerListener);

        latMinutes = (NumberPicker) result.findViewById(R.id.latMinutes);
        latMinutes.setMinValue(0);
        latMinutes.setMaxValue(59);
        latMinutes.setOnValueChangedListener(pickerListener);

        latSeconds = (NumberPicker) result.findViewById(R.id.latSeconds);
        latSeconds.setMinValue(0);
        latSeconds.setMaxValue(99);
        latSeconds.setOnValueChangedListener(pickerListener);

        longDegrees = (NumberPicker) result.findViewById(R.id.longDegrees);
        longDegrees.setMinValue(0);
        longDegrees.setMaxValue(180);
        longDegrees.setOnValueChangedListener(pickerListener);

        longMinutes = (NumberPicker) result.findViewById(R.id.longMinutes);
        longMinutes.setMinValue(0);
        longMinutes.setMaxValue(59);
        longMinutes.setOnValueChangedListener(pickerListener);

        longSeconds = (NumberPicker) result.findViewById(R.id.longSeconds);
        longSeconds.setMinValue(0);
        longSeconds.setMaxValue(99);
        longSeconds.setOnValueChangedListener(pickerListener);

        // TODO: (optional) remove edit mode on tab change
            // TODO: get by name the tab window
            // TODO: do TabView.AddOnTabSelect...
            // TODO: whenever that is, switch off edit mode


        // DONE: parse parameters
            // DONE: allow only name parameter
            // DONE: reuse UpDownFragment code to get parameter from XML
        // DONE: use methods in Location class for distance and bearing calculation
        // TODO: Edit mode
            // TODO: name editor should lose focus when edit turned off
            // TODO: (optional) should be turned off when another tab gets opened
            // TODO: (optional) Name should lose focus when clicked elsewhere or when return is hit
        // TODO: search
            // TODO: select search options if multiple values
            // TODO: if name starts with ! feed it through the search mechanism


        Observer<Double> latLongObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double s) {
                updateLocationString();
            }
        };

        buttonLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Location location = locationState.getLatestLocation();
                setPickerValues(location.getLatitude(), location.getLongitude());
                updateLocationFromPickers();
                updateLocationString();
            }
        });

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });

        locationState.getLatitude().observe(getViewLifecycleOwner(), latLongObserver);
        locationState.getLongitude().observe(getViewLifecycleOwner(), latLongObserver);
        mViewModel.mSwitchPrint.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                switchPrint.setChecked(aBoolean);
            }
        });

        mViewModel.mName.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textViewName.setText(s);
            }
        });

        mViewModel.getStatusText().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        textViewInfo.setText(s);
                    }
                }

        );

        switchEdit.setChecked(false);
        editView.setVisibility(View.GONE);
        mViewModel.mSwitchPrint.setValue(false);
        if (!loadSavedState(savedInstanceState) && mViewModel.getName().getValue().isEmpty()) {
            if (getArguments() != null) {
                mParamName = getArguments().getString(ARG_ParamName);
            }
            if (getArguments() != null) {
                mParamPosition = getArguments().getString(ARG_ParamPosition);
            }

            mViewModel.getName().setValue(mParamName);
            setPosition(mParamPosition);
        }


        switchEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean value = switchEdit.isChecked();
                setEditable(value);
// why was the below ever here?
//                Location location = locationState.getLatestLocation();
//                setPickerValues(location.getLatitude(), location.getLongitude());
            }
        });

        this.setEditable(false);

        return result;
    }

    private void setEditable(boolean makeEditable) {
        if (editView != null) {
            this.switchEdit.setChecked(false);
            if (makeEditable != this.switchEdit.isChecked()) {
                this.switchEdit.setChecked(makeEditable);
            }
            if (!makeEditable) {
                this.textViewName.clearFocus();
                this.textViewName.clearComposingText();
            }
            this.textViewName.setEnabled(makeEditable);
            this.textViewName.setTextColor(Color.BLACK);
            this.editView.setVisibility(makeEditable ? View.VISIBLE : View.GONE);
            if (makeEditable != this.switchEdit.isChecked()) {
                this.switchEdit.setChecked(makeEditable);
            }
        }
    }

    private void setPickerValues(double latitude, double longitude) {
        switchNorthSouth.setChecked(latitude > 0);
        int[] latCoordinates = LocationState.ToDegreesMinutesDecimals(Math.abs(latitude),2);
        latDegrees.setValue(latCoordinates[0]);
        latMinutes.setValue(latCoordinates[1]);
        latSeconds.setValue(latCoordinates[2]);
        switchEastWest.setChecked(longitude > 0);
        int[] longCoordinates = LocationState.ToDegreesMinutesDecimals(Math.abs(longitude),2);
        longDegrees.setValue(longCoordinates[0]);
        longMinutes.setValue(longCoordinates[1]);
        longSeconds.setValue(longCoordinates[2]);
    }

    // following https://stackoverflow.com/questions/8641575/custom-attributes-in-android-fragments
    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if (context != null && attrs != null && mParamName.equals("Waypoint")) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WayPointFragment_MembersInjector);
            if (ta.hasValue(R.styleable.WayPointFragment_MembersInjector_name)) {
                mParamName = ta.getString(R.styleable.WayPointFragment_MembersInjector_name);
            }
            ta.recycle();
        }
        if (context != null && attrs != null && mParamPosition.equals("!latest")) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WayPointFragment_MembersInjector);
            if (ta.hasValue(R.styleable.WayPointFragment_MembersInjector_position)) {
                mParamPosition = ta.getString(R.styleable.WayPointFragment_MembersInjector_position);
            }
            ta.recycle();
        }
        loadSavedState(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        String name = mViewModel != null ? mViewModel.getName().getValue() : textViewName.getText().toString();
        outState.putString(ARG_ParamName, name);
        outState.putString(ARG_ParamPosition, LocationState.latLongToString(locationState.getLatitude().getValue(), locationState.getLongitude().getValue()));
    }

    private boolean loadSavedState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mParamName = savedInstanceState.getString(ARG_ParamName);
            if (mViewModel != null) {
                mViewModel.getName().postValue(mParamName);
            }

            this.setPosition(savedInstanceState.getString(ARG_ParamPosition));
            return true;
        }

        return false;
    }


    private void updateLocationString() {
        mViewModel.getStatusText().postValue(mViewModel.getLocationString(false));
    }

    private void setPosition(String positionString) {
        if (positionString == null || positionString.length() == 0) {
            return;
        }
        // TODO: if the position is !latest, use latest gps coordinates
        if (positionString.equalsIgnoreCase("!latest")) {
            Location location = locationState.getLatestLocation();
            if (location.getLatitude() != 0.0 | location.getLongitude() != 0.0) {
                setPickerValues(location.getLatitude(), location.getLongitude());
            }
            this.mParamPosition = ""; // ensure that latest position is set only once
            updateLocationFromPickers();
            updateLocationString();
        }
        // TODO: if the position starts with "!", perform a search
        // TODO: parse a lat-long human readable
        double[] dmsParsed = LocationState.DmsStringToLatLong(positionString);
        if (dmsParsed != null) {
            setPickerValues(dmsParsed[0], dmsParsed[1]);
            updateLocationFromPickers();
            updateLocationString();
        }


        // TODO: parse a lat-long as double


        updateLocationString();
    }

    private void performSearch() {
        String query = mViewModel.getName().getValue();
        if (query != null && query.length() > 0) {
            // TODO: special role for "start" and Uhrzeit
            // TODO: if name is lat/long of Name: Lat/long use as appropriate
            // TODO: otherwise search using geonames.org
            // TODO: speacial also for bearing and distance
            searchLocation();
            updateLocationString();

        } else {
            mViewModel.getStatusText().setValue("Set a non-empty name to search.");
        }
    }

    private void updateLocationFromPickers() {
        double pickerLat = WayPointFragment.ToDoubleCoordinates(latDegrees.getValue() * (switchNorthSouth.isChecked() ? 1 : -1), latMinutes.getValue(), latSeconds.getValue(), 2);
        double pickerLong = WayPointFragment.ToDoubleCoordinates(longDegrees.getValue() * (switchEastWest.isChecked() ? 1 : -1), longMinutes.getValue(), longSeconds.getValue(), 2);
        mViewModel.setLatLong(pickerLat, pickerLong);
        updateLocationString();
    }

    private void searchLocation() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    String name = mViewModel.getName().getValue();
                    WebService.setGeoNamesServer("api.geonames.org");
                    WebService.setGeoNamesServerFailover("api.geonames.org");
                    WebService.setUserName("sblohm"); // add your username here

                    ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
                    searchCriteria.setQ(name);
                    ToponymSearchResult searchResult = null;
                    try {
                        searchResult = WebService.search(searchCriteria);
                        if (searchResult.getTotalResultsCount() > 0) {
                            String resultName = searchResult.getToponyms().get(0).getName();
                            String resultCountry = searchResult.getToponyms().get(0).getCountryName();
                            String resultClass = searchResult.getToponyms().get(0).getFeatureClassName();
                            double resultLat = searchResult.getToponyms().get(0).getLatitude();
                            double resultLong = searchResult.getToponyms().get(0).getLongitude();
                            mViewModel.setLatLong(resultLat, resultLong);
                            setPickerValues(resultLat, resultLong);
                            mViewModel.getStatusText().postValue(String.format("Set to %s, %s (%s)", resultName, resultCountry, resultClass));

                        }
//            for (Toponym toponym : searchResult.getToponyms()) {
//                System.out.println(toponym.getName()+" "+ toponym.getCountryName());
//            }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mViewModel.getStatusText().setValue(String.format("Search err: %s", e.getMessage()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

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
        double bearing = (theta/TO_RADIAN + 360); // in degrees
        while (bearing >= 360.0) bearing -= 360.0;
        while (bearing < 0.005) bearing += 360.0;

        return bearing;
    }

    public static double ToDoubleCoordinates(int degrees, int minutes, int decimals, int numDecimals){
        return Math.signum(degrees) *  (Math.abs(degrees) + minutes / 60.0 + decimals / (60.0 * Math.pow(10, numDecimals)));

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        this.setEditable(false);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        this.setEditable(false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        this.setEditable(false);
    }
}