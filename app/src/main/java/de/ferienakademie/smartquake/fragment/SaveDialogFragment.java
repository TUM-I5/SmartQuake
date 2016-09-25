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

public class SaveDialogFragment extends DialogFragment {
    public interface SaveDialogListener {
        public void onNameChosen(String s);
    }

    /*
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }*/



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        bob.setView(i.inflate(R.layout.dialog_save, null))
                .setMessage("Choose structure name")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // view returned is null...
                        View v = SaveDialogFragment.this.getView();
                        EditText t = (EditText) getActivity().findViewById(R.id.fileNameEditor);
                        ((SaveDialogListener) getActivity()).onNameChosen(t.getText().toString());
                    }
                })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return bob.create();
    }

}
