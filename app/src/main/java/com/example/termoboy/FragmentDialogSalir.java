package com.example.termoboy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class FragmentDialogSalir extends DialogFragment {
    private Activity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.popUpSalir)
                .setPositiveButton(getString(R.string.popUpNegativo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.popUpPositivo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity = getActivity();
                        activity.finish();
                    }
                });
        return builder.create();
    }
}
