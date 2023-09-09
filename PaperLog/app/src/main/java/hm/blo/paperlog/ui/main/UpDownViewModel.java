package hm.blo.paperlog.ui.main;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import hm.blo.paperlog.model.Printable;
import hm.blo.paperlog.model.Printing;

/**
 * View model of a fragment displaying log data.
 * Values can be changed according to fixed increments.
 * Data will be provided to printer if changed.
 */
public class UpDownViewModel extends ViewModel implements Printable {
    @Override
    public String getPrintString() {
        String valueString = this.valueToString();
        mToOutput.setValue(false);
        return String.format(this.printTemplate, valueString);
    }

    @Override
    public boolean hasContent() {
        return mToOutput.getValue().booleanValue();
    }

    public enum DataType {Double, CompassCourse}

    protected MutableLiveData<Double> mValue = new MutableLiveData<Double>();
    protected MutableLiveData<Boolean> mToOutput = new MutableLiveData<Boolean>();
    private DataType type;
    private String uiTemplate;
    protected double increment;
    private String printTemplate;


    public void initialize(String typeName, String uiTemplate, double increment, double initial, String printTemplate) {
        this.type = Enum.valueOf(DataType.class, typeName);
        this.uiTemplate = uiTemplate;
        this.increment = increment;
        this.printTemplate = printTemplate;
        mValue.setValue(initial);
        mToOutput.setValue(false);
        Printing.addPrintable(this);
    }

    public void up() {
        this.update(true);
    }

    public void down() {
        this.update(false);
    }

    public LiveData<Double> getValue() {
        return mValue;
    }

    public LiveData<Boolean> getToOutput() {
        return this.mToOutput;
    }

    protected void update(boolean up) {
        double update = up ? this.increment : -this.increment;
        mValue.setValue(mValue.getValue().doubleValue() + update);
        this.mToOutput.setValue(true);
    }

    protected String valueToString(){
        return mValue.getValue().toString();
    }

    public String asUiString() {
        String valueString = this.valueToString();
        return String.format(this.uiTemplate, valueString);
    }

    public void toBundle(@NonNull Bundle outState, String keyPrefix) {
        outState.putString(keyPrefix + "_type", this.type.toString());
        outState.putString(keyPrefix + "_ui_template", this.uiTemplate);
        outState.putString(keyPrefix + "_print_template", this.printTemplate);
        outState.putDouble(keyPrefix + "_increment", this.increment);
        outState.putDouble(keyPrefix + "_value", this.mValue.getValue());
    }

    public boolean fromBundle(@NonNull Bundle inState, String keyPrefix) {
        boolean success = inState.containsKey(keyPrefix + "_type") && inState.containsKey(keyPrefix + "_ui_template") && inState.containsKey(keyPrefix + "_print_template") && inState.containsKey(keyPrefix + "_increment") && inState.containsKey(keyPrefix + "_value");
        if (success) {
            this.type = Enum.valueOf(DataType.class, inState.getString(keyPrefix + "_type"));
            this.uiTemplate = inState.getString(keyPrefix + "_ui_template");
            this.printTemplate = inState.getString(keyPrefix + "_print_template");
            this.increment = inState.getDouble(keyPrefix + "_increment");
            this.mValue.setValue(inState.getDouble(keyPrefix + "_value"));
            mToOutput.setValue(false);
            Printing.addPrintable(this);
        }

        return success;
    }
}
