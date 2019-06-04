package com.example.termoboy;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public class FragmentoDialogoAviso extends DialogFragment {
    private Activity activity;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.popUpBienvenida)
                .setPositiveButton(getString(R.string.popUpVale), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(getString(R.string.popUpAnonimo), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        activity = getActivity();
                        activity.findViewById(R.id.btnEntra).setEnabled(false);
                        FirebaseAuth mAuth;
                        mAuth = FirebaseAuth.getInstance();
                        mAuth.signInAnonymously().addOnCompleteListener(activity, new ListenerCrearUsuario(activity));
                    }
                });
        return builder.create();
    }
}
