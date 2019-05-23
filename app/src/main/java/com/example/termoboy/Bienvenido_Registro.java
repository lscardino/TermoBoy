package com.example.termoboy;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;

public class Bienvenido_Registro extends AppCompatActivity {

    Spinner spinnerGenero;
    private FirebaseAuth mAuth;
    FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido__registro);

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
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    //Todo guay
                    Log.d("DATOS","Usuario In");
                }
            }
        });

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void mostrarPopUp() {
       FireMissilesDialogFragment a = new FireMissilesDialogFragment();
       a.show(getSupportFragmentManager(),"ee");

    }
}

