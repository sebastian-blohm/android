package hm.blo.paperlog.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hm.blo.paperlog.R;
import hm.blo.paperlog.databinding.LatLongFragmentBinding;
import hm.blo.paperlog.model.LocationState;

/**
 * Fragment displaying Latitude/Longitude coordinate information based on the device's GPS.
 */
public class LatLongFragment extends Fragment {

    private LatLongViewModel mViewModel;
    private LatLongFragmentBinding binding;

    // TODO: load lat/long from GPS every n minutes
    // TODO: alert after if not printed for m minutes

    public static LatLongFragment newInstance() {
        return new LatLongFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(this).get(LatLongViewModel.class);
        mViewModel.initialize(this.getContext());

        binding = LatLongFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textViewLatLong = binding.textViewLatLong;

        Observer<Double> latLongObserver = new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double s) {
                textViewLatLong.setText(mViewModel.asUiString());
            }
        };

        mViewModel.getLatitude().observe(getViewLifecycleOwner(), latLongObserver);
        mViewModel.getLongitude().observe(getViewLifecycleOwner(), latLongObserver);

        final TextView textViewStatus = binding.textViewStatusLatLong;
        mViewModel.getStatusText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textViewStatus.setText(s);
            }
        });

        Button button = (Button) binding.buttonRefreshLatLong;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mViewModel.updateLocation();
            }
        });

        return root;
    }
}