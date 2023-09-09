package hm.blo.paperlog;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentOnAttachListener;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hm.blo.paperlog.model.Printable;
import hm.blo.paperlog.model.Printing;
import hm.blo.paperlog.ui.main.SectionsPagerAdapter;
import hm.blo.paperlog.databinding.ActivityMainBinding;
import hm.blo.paperlog.ui.main.WayPointFragment;

/**
 * App to display and print logs while at sea using a ticker printer. Log entries can be reviewed
 * and printed by the crew using minimal effort but ensuring attention to what is logged.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);

        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        // TODO: the below doesn't seem to add the listener
        this.SetTabChangeObservers(tabs);
        getSupportFragmentManager().addFragmentOnAttachListener(new FragmentOnAttachListener() {
            @Override
            public void onAttachFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment fragment) {
                if (fragment instanceof WayPointFragment) {
                    tabs.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) fragment);
                }
            }
        });
        FloatingActionButton fab = binding.fab;

        Printing.SetReportPrintError((String error) -> {Snackbar.make(viewPager, error, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();});

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Printing.printPrintables();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_GPS);

        }
    }

    private void SetTabChangeObservers(TabLayout tabs){
        for (Fragment fragment: getSupportFragmentManager().getFragments()) {
            if (fragment instanceof WayPointFragment) {
                tabs.addOnTabSelectedListener((TabLayout.OnTabSelectedListener) fragment);
            }
        }
    }

    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_GPS = 1;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        // SimpleDateFormat format = new SimpleDateFormat("'on' yyyy-MM-dd 'at' HH:mm:ss");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case MainActivity.PERMISSION_BLUETOOTH:
                    Printing.permissionReceived();
                    break;
            }
        }
    }
}