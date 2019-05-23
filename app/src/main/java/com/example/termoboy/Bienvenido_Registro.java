package com.example.termoboy;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.lang.reflect.Array;

public class Bienvenido_Registro extends AppCompatActivity {

    Spinner spinnerGenero;

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


    }

    public void entrarApp(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void mostrarPopUp() {
       FireMissilesDialogFragment a = new FireMissilesDialogFragment();
       a.show(getSupportFragmentManager(),"ee");

    }
}

