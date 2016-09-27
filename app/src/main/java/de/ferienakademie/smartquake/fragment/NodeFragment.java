package de.ferienakademie.smartquake.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;

import de.ferienakademie.smartquake.R;
import de.ferienakademie.smartquake.model.Node;
import de.ferienakademie.smartquake.view.CanvasView;

/**
 * Created by yuriy on 27/09/16.
 */
public class NodeFragment extends DialogFragment {

    public interface NodeParametersListener {
        public void onChangeNode();
    }

    Node node = null;

    NodeParametersListener caller;

    public void setNode(Node node) { this.node = node; }
    public void setListener(NodeParametersListener caller) { this.caller = caller; }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bob = new AlertDialog.Builder(getActivity());
        LayoutInflater i = getActivity().getLayoutInflater();

        final View view = i.inflate(R.layout.choose_node, null);

        final Switch isHingeButton = (Switch)view.findViewById(R.id.is_hinge);
        if (node.isHinge()) isHingeButton.toggle();

        bob.setView(view)
                .setMessage("Node parameters")
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        node.setHinge(isHingeButton.isChecked());
                        caller.onChangeNode();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return bob.create();
    }

}