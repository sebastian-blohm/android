package hm.blo.paperlog.ui.main;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

import hm.blo.paperlog.R;
import hm.blo.paperlog.databinding.FragmentUpDownBinding;

/**
 * A fragment displaying log data.
 * Values can be changed according to fixed increments.
 * Data will be provided to printer if changed.
 */
public class UpDownFragment extends Fragment implements GestureDetector.OnGestureListener, View.OnClickListener {

    private UpDownViewModel upDownViewModel;
    private FragmentUpDownBinding binding;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_DATATYPE = "data_type";
    private static final String ARG_UI_TEMPLATE = "ui_template";
    private static final String ARG_INCREMENT = "increment";
    private static final String ARG_INITIAL = "initial";
    private static final String ARG_PRINT_TEMPLATE = "print_template";

    private String mParamDatatype;
    private String mParamUiTemplate;
    private String mParamIncrement;
    private String mParamInitial;
    private String mParamPrintTemplate;

    public UpDownFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mParamDatatype Data type to be logged.
     * @param mParamUiTemplate Determines how the value is shown on the UI.
     * @param mParamIncrement How much to update on a single action.
     * @param mParamInitial The initial value.
     * @param mParamPrintTemplate Determines how the value is shown in print.
     *
     * @return A new instance of fragment UpDownFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpDownFragment newInstance(
            String mParamDatatype,
            String mParamUiTemplate,
            String mParamIncrement,
            String mParamInitial,
            String mParamPrintTemplate
    ) {
        UpDownFragment fragment = new UpDownFragment();
        Bundle args = new Bundle();

        args.putString(ARG_DATATYPE, mParamDatatype);
        args.putString(ARG_UI_TEMPLATE, mParamUiTemplate);
        args.putString(ARG_INCREMENT, mParamIncrement);
        args.putString(ARG_INITIAL, mParamInitial);
        args.putString(ARG_PRINT_TEMPLATE, mParamPrintTemplate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamDatatype = getArguments().getString(ARG_DATATYPE);
            mParamUiTemplate = getArguments().getString(ARG_UI_TEMPLATE);
            mParamIncrement = getArguments().getString(ARG_INCREMENT);
            mParamInitial = getArguments().getString(ARG_INITIAL);
            mParamPrintTemplate = getArguments().getString(ARG_PRINT_TEMPLATE);
        }

        switch (mParamDatatype.toLowerCase(Locale.ROOT)) {
            case "double":
                upDownViewModel = new ViewModelProvider(this).get(UpDownViewModel.class);
                break;
            case "compasscourse":
                upDownViewModel = new ViewModelProvider(this).get(CompassCourseViewModel.class);
                break;
            default:
                upDownViewModel = new ViewModelProvider(this).get(UpDownViewModel.class);
                break;
        }

        upDownViewModel.initialize(mParamDatatype, mParamUiTemplate, Double.parseDouble(mParamIncrement), Double.parseDouble(mParamInitial), mParamPrintTemplate);
    }

    // following https://stackoverflow.com/questions/8641575/custom-attributes-in-android-fragments
    @Override
    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if (context != null && attrs != null && mParamDatatype == null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UpDownFragment_MembersInjector);
            if (ta.hasValue(R.styleable.UpDownFragment_MembersInjector_data_type)) {
                mParamDatatype = ta.getString(R.styleable.UpDownFragment_MembersInjector_data_type);
            }
            ta.recycle();
        }

        if (context != null && attrs != null && mParamUiTemplate == null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UpDownFragment_MembersInjector);
            if (ta.hasValue(R.styleable.UpDownFragment_MembersInjector_ui_template)) {
                mParamUiTemplate = ta.getString(R.styleable.UpDownFragment_MembersInjector_ui_template);
            }
            ta.recycle();
        }

        if (context != null && attrs != null && mParamIncrement == null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UpDownFragment_MembersInjector);
            if (ta.hasValue(R.styleable.UpDownFragment_MembersInjector_increment)) {
                mParamIncrement = ta.getString(R.styleable.UpDownFragment_MembersInjector_increment);
            }
            ta.recycle();
        }

        if (context != null && attrs != null && mParamInitial == null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UpDownFragment_MembersInjector);
            if (ta.hasValue(R.styleable.UpDownFragment_MembersInjector_initial)) {
                mParamInitial = ta.getString(R.styleable.UpDownFragment_MembersInjector_initial);
            } else {
                mParamInitial = "0.0";
            }
            ta.recycle();
        }

        if (context != null && attrs != null && mParamPrintTemplate == null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.UpDownFragment_MembersInjector);
            if (ta.hasValue(R.styleable.UpDownFragment_MembersInjector_print_template)) {
                mParamPrintTemplate = ta.getString(R.styleable.UpDownFragment_MembersInjector_print_template);
            }
            ta.recycle();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_up_down, container, false);

        binding = FragmentUpDownBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textViewValue;
        upDownViewModel.getValue().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(@Nullable Double s) {
                textView.setText(upDownViewModel.asUiString());
            }
        });

        upDownViewModel.getToOutput().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean s) {
                textView.setTypeface(null, s.booleanValue() ? Typeface.BOLD : Typeface.NORMAL);
            }
        });

        Button button = (Button) binding.buttonUp;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                upDownViewModel.up();
            }
        });

        button = (Button) binding.buttonDown;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                upDownViewModel.down();
            }
        });
        return root;
    }

    // TODO: the below don't get called. It appears that a surrounding fragment/view captures too many touch events

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        upDownViewModel.up();
        return true;
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
        upDownViewModel.down();
    }

    @Override
    public void onClick(View view) {
        upDownViewModel.up();
    }
}