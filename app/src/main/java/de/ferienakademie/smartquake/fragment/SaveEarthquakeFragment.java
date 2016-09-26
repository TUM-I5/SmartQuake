package de.ferienakademie.smartquake.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import de.ferienakademie.smartquake.R;

/**
 * Created by yuriy on 26/09/16.
 */
public class SaveEarthquakeFragment extends DialogFragment {
    public interface SaveEarthquakeListener {
        public void onNameChosen(String s);
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        final View view = i.inflate(R.layout.dialog_save_earthquake, null);

        bob.setView(view)
                .setMessage("Earthquake name")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText t = (EditText) view.findViewById(R.id.fileNameEditor);
                        ((SaveEarthquakeListener) getActivity()).onNameChosen(t.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return bob.create();
    }

}