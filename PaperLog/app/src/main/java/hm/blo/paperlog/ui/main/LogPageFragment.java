package hm.blo.paperlog.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import hm.blo.paperlog.databinding.FragmentMainBinding;

/**
 * A fragment displaying log data.
 * Values can be changed according to fixed increments.
 * Data will be provided to printer if changed.
 */
public class LogPageFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    private UpDownViewModel pageViewModel;
    private FragmentMainBinding binding;

    public static LogPageFragment newInstance(int index) {
        LogPageFragment fragment = new LogPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = new ViewModelProvider(this).get(UpDownViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}