package hm.blo.paperlog.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hm.blo.paperlog.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaypointPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaypointPageFragment extends Fragment {
    public WaypointPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WaypointPageFragment.
     */
    public static WaypointPageFragment newInstance() {
        WaypointPageFragment fragment = new WaypointPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_waypoint_page, container, false);
    }
}