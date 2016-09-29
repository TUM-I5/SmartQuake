package de.ferienakademie.smartquake.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import de.ferienakademie.smartquake.R;

public class SaveDialogFragment extends DialogFragment {
    private String nameSuggestion;

    public void setNameSuggestion(String nameSuggestion) {
        this.nameSuggestion = nameSuggestion;
    }

    public interface SaveDialogListener {
    public void onNameChosen(String s);
}



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        final View view = i.inflate(R.layout.dialog_save, null);
        EditText t = (EditText) view.findViewById(R.id.fileNameEditor);
        if(nameSuggestion!=null) {
            try{
                t.setText(nameSuggestion.substring(0,nameSuggestion.length()-10));
            }catch (IndexOutOfBoundsException e){
                Log.e("SaveDialogFragment", "IndexOutOfBoundsException");
                //do nothing
            }

        }
//        ((EditText)view.findViewById(R.id.fileNameEditor));

        bob.setView(view)
                .setMessage("Choose structure name")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // view returned is null...
                        //View v = SaveDialogFragment.this.getView();
                        EditText t = (EditText) view.findViewById(R.id.fileNameEditor);
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
