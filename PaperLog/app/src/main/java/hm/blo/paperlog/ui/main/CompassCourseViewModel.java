package hm.blo.paperlog.ui.main;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * ViewModel of a specialized LogData instance for compass courses.
 * 16 point cardinal directions will be translated to common abbreviations.
 */
public class CompassCourseViewModel extends UpDownViewModel {

    NumberFormat courseFormatter = new DecimalFormat("#0.0");

    @Override
    protected String valueToString() {
        String value = courseFormatter.format(this.getValue().getValue());
        switch(value) {
            case "0.0": { return "N"; }
            case "22.5": { return "NNE"; }
            case "45.0": { return "NE"; }
            case "67.5": { return "ENE"; }
            case "90.0": { return "E"; }
            case "112.5": { return "ESE"; }
            case "135.0": { return "SE"; }
            case "157.5": { return "SSE"; }
            case "180.0": { return "S"; }
            case "202.5": { return "SSW"; }
            case "225.0": { return "SW"; }
            case "247.5": { return "WSW"; }
            case "270.0": { return "W"; }
            case "292.5": { return "WNW"; }
            case "315.0": { return "NW"; }
            case "337.5": { return "NNW"; }
        }

        return value + "°";
    }

    @Override
    protected void update(boolean up) {
        double update = up ? this.increment : -this.increment;
        double value = mValue.getValue().doubleValue() + update;
        if (value < 0.0) {
            value += 360.0;
        }

        if (value >= 360.0) {
            value -= 360.0;
        }

        mValue.setValue(value);
        this.mToOutput.setValue(true);
    }
}
