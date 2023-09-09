package hm.blo.paperlog.ui.main;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import hm.blo.paperlog.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NavPaneIoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NavPaneIoFragment extends Fragment {

    // https://developer.android.com/guide/topics/providers/document-provider.html
    // https://stackoverflow.com/questions/8586691/how-to-open-file-save-dialog-in-android (newest result)
    View planItemContainer;
    View localView;


    public NavPaneIoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FileIo.
     */
    public static NavPaneIoFragment newInstance() {
        NavPaneIoFragment fragment = new NavPaneIoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    ReadJourneyFile(uri);
                }
            });

    ActivityResultLauncher<String> mCreateDocument = registerForActivityResult(new ActivityResultContracts.CreateDocument(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    WriteJourneyFile(uri);
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.fragment_nav_pane_io, container, false);
        Button buttonLoad = (Button) result.findViewById(R.id.button_load);
        Button buttonSave = (Button) result.findViewById(R.id.button_save);
        Button buttonNewWaypoint = (Button) result.findViewById(R.id.button_new_waypoint);
        Button buttonNewNote = (Button) result.findViewById(R.id.button_new_note);
        localView = result;

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGetContent.launch("*/*");

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCreateDocument.launch("journeyPlan.json");
            }
        });
        return result;
    }

    // https://stackoverflow.com/questions/7784418/get-all-child-views-inside-linearlayout-at-once
    // https://stackoverflow.com/questions/31743695/how-can-i-get-fragment-from-view
    private List<Fragment> getWayPointFragments() {
        List<Fragment> result = new ArrayList<Fragment>();
        LinearLayout ll = (LinearLayout) localView.getRootView().findViewById(R.id.plan_item_container);
        final int childCount = ll.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = ll.getChildAt(i);
            //v.getTag(R.id.fragment_container_view_tag);
            Fragment neighborFragment = FragmentManager.findFragment(v);
            if (neighborFragment instanceof WayPointFragment) {
                result.add(neighborFragment);
            }
        }

        return result;
    }

    private void WriteJourneyFile(Uri uri) {
        try {
            OutputStream outStream = getContext().getContentResolver().openOutputStream(uri);
            OutputStreamWriter fWriter = new OutputStreamWriter(outStream);
            List<Fragment> wayPoints = this.getWayPointFragments();
            fWriter.write("This is a file test: " + wayPoints.size());
            fWriter.flush();
            fWriter.close();
            this.showSnackBar("Journey Plan stored");
        } catch (FileNotFoundException e) {
            this.showSnackBar("FileNotFoundException:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            this.showSnackBar("IOException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void ReadJourneyFile(Uri uri) {
        try {
            InputStream inStream = getContext().getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
            this.showSnackBar("Read:" + reader.readLine());
        } catch (FileNotFoundException e) {
            this.showSnackBar("FileNotFoundException:" + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            this.showSnackBar("IOException:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showSnackBar(String text) {
        Snackbar.make(this.getView(), text,
                Snackbar.LENGTH_LONG)
                .show();
    }
}