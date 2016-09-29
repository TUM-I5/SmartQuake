package de.ferienakademie.smartquake.preferenceElements;

/**
 * Created by Vincent Stimper on 29.09.16.
 */
import android.content.Context;
import android.util.AttributeSet;

public class EditTextPreferenceInt extends android.preference.EditTextPreference {

    public EditTextPreferenceInt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        CharSequence text = getSummary();
        int val = Integer.parseInt((String) text);
        int maxVal = 1000;
        int minVal = 2;
        if (val > maxVal) {
            setText(Integer.toString(maxVal));
        } else if (val< minVal) {
            setText(Integer.toString(minVal));
        } else {
                setText(Integer.toString(val));
        }
        setSummary(text);
    }

    @Override
    public CharSequence getSummary() {
        return this.getText();
    }
}
