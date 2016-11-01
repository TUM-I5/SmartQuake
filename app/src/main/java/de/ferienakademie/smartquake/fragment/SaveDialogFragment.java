// This file is part of SmartQuake - Interactive Simulation of 2D Structures in Earthquakes for Android
// Copyright (C) 2016 Chair of Scientific Computing in Computer Science (SCCS) at Technical University of Munich (TUM)
// <http://www5.in.tum.de>
//
// All copyrights remain with the respective authors.
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        final View view = i.inflate(R.layout.dialog_save, null);

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
