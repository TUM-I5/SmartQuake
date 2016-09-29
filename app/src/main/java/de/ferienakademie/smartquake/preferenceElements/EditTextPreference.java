package de.ferienakademie.smartquake.preferenceElements;

/**
 * Created by Vincent Stimper on 29.09.16.
 */
import android.content.Context;
import android.util.AttributeSet;

public class EditTextPreference extends android.preference.EditTextPreference {

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        double val = Double.parseDouble(this.getText());
        double maxVal = 10;
        if (val > maxVal) {
            setText(Double.toString(maxVal));
        } else {
            setText(Double.toString(val));
        }
        setSummary(getSummary());
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
