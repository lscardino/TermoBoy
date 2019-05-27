package com.example.termoboy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;

public class Bienvenido_Registro extends AppCompatActivity {

    Spinner generoUser;
    EditText edatUser;

    private FirebaseAuth mAuth;
    FirebaseUser currentUser;

    SharedPreferences setDatosUser;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido__registro);


        edatUser = findViewById(R.id.etxtEdat);
        generoUser = findViewById(R.id.spinnerGenero);
        LinearLayout layoutprincipal = findViewById(R.id.layoutrootBienvenida);

        AnimationDrawable animationDrawable = (AnimationDrawable) layoutprincipal.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        mostrarPopUp();

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null){
            //Iniciar la actividá
            Log.d("DATOS","Usuario existe already");
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    public void entrarApp(View view) {
        if  (!edatUser.getText().toString().isEmpty()) {

            String datoSpinner = generoUser.getSelectedItem().toString();
            Log.d("PREFS","Valor spinner " + datoSpinner);

            setDatosUser = getSharedPreferences("MisPrefs", Context.MODE_PRIVATE);
            editor = setDatosUser.edit();
            editor.putInt("edatUser",Integer.parseInt(edatUser.getText().toString()));
            editor.putString("generoUser",datoSpinner);
            Log.d("PREFS",setDatosUser.getString("generoUser","mall"));
            Log.d("PREFS",String.valueOf(setDatosUser.getInt("edatUser",33)));


            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Todo guay
                        Log.d("DATOS", "Usuario In");
                    }
                }
            });
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }else{
            Toast.makeText(getApplicationContext(),"Por favor rellena todos los campos",
                    Toast.LENGTH_LONG).show();

        }
    }

    public void mostrarPopUp() {
       FragmentoDialogoAviso a = new FragmentoDialogoAviso();
       a.show(getSupportFragmentManager(),"ee");
    }
}

