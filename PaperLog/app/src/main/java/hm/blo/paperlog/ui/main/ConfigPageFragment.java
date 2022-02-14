package hm.blo.paperlog.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hm.blo.paperlog.R;
import hm.blo.paperlog.model.Printing;

/**
 * A {@link Fragment} subclass for the configuration tab.
 * Use the {@link ConfigPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigPageFragment extends Fragment {

    public ConfigPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment ConfigPageFragment.
     */
    public static ConfigPageFragment newInstance() {
        ConfigPageFragment fragment = new ConfigPageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_config_page, container, false);

        TextView textViewPrinterName = (TextView) view.findViewById(R.id.printer_name);

        Button button = (Button) view.findViewById(R.id.select_printer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Printing.browseBluetoothDevice(view.getContext(), (name) -> {textViewPrinterName.setText(name);});
            }
        });
//        button = (Button) findViewById(R.id.button_bluetooth);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                printBluetooth(ConfigPageFragment.this);
//            }
//        });

        return view;
    }
}