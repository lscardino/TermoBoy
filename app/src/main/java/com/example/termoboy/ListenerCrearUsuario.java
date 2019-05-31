package com.example.termoboy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ListenerCrearUsuario implements OnCompleteListener<AuthResult> {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseDatabase firebaseDatabase;

    private Activity activity;

    public ListenerCrearUsuario(Activity _activity) {
        this.activity = _activity;
    }

    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            //Todo guay
            mAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
            new SubirUsuarioDatos().start();
            activity.finish();
        }else{
            activity.findViewById(R.id.btnEntra).setEnabled(true);
        }
    }

    private class SubirUsuarioDatos extends Thread {
        @Override
        public void run() {
            currentUser = mAuth.getCurrentUser();

            Log.d("DATOS", "Usuario In - id: " + currentUser.getUid());

            //Subir datos a FireBase

            SharedPreferences getPrefUser = activity.getSharedPreferences("MisPrefs", Context.MODE_PRIVATE);
            int getUserEdat = getPrefUser.getInt("edatUser", -1);
            String getUserGenero = getPrefUser.getString("generoUser", null);

            HashMap<String, Object> datos = new HashMap<>();
            datos.put("Edad", getUserEdat);
            datos.put("Sexo", getUserGenero);

            DatabaseReference databaseReference = firebaseDatabase.getReference("Usuario");

            //Crea si no existe la crea y si existe escribe encima (no hya mucha diferencia)
            databaseReference.child(currentUser.getUid()).updateChildren(datos);
        }
    }
}
