package com.example.termoboy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Bienvenido_Registro extends AppCompatActivity {

    private Spinner generoUser;
    private EditText edatUser;
    private Button btnEntra;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private SharedPreferences setDatosUser;
    private SharedPreferences.Editor editor;

    private FusedLocationProviderClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido__registro);


        edatUser = findViewById(R.id.etxtEdat);
        generoUser = findViewById(R.id.spinnerGenero);
        btnEntra = findViewById(R.id.btnEntra);
        LinearLayout layoutprincipal = findViewById(R.id.layoutrootBienvenida);

        AnimationDrawable animationDrawable = (AnimationDrawable) layoutprincipal.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        mostrarPopUp();

        mAuth = FirebaseAuth.getInstance();




        if (mAuth.getCurrentUser() != null){
            //Iniciar la actividá
            //Log.d("DATOS","Usuario existe already - id: " +  currentUser.getUid());
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Todo guay
                        currentUser = mAuth.getCurrentUser();
                        Log.d("DATOS", "Usuario In - id: " + currentUser.getUid());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }

                }
            });
        }


        client = LocationServices.getFusedLocationProviderClient(this);
        //pillarLocalizacion();
        requestlocation();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
    }

    public void entrarApp(View view) {
        btnEntra.setEnabled(true);
        if  (!edatUser.getText().toString().isEmpty()) {

            String datoSpinner = generoUser.getSelectedItem().toString();
            Log.d("PREFS","Valor spinner " + datoSpinner);

            setDatosUser = getSharedPreferences("MisPrefs", Context.MODE_PRIVATE);
            editor = setDatosUser.edit();
            editor.putInt("edatUser",Integer.parseInt(edatUser.getText().toString()));
            editor.putString("generoUser",datoSpinner);
            editor.apply();
            Log.d("PREFS",setDatosUser.getString("generoUser","mall"));
            Log.d("PREFS",String.valueOf(setDatosUser.getInt("edatUser",33)));


            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Todo guay
                        currentUser = mAuth.getCurrentUser();

                        Log.d("DATOS", "Usuario In - id: " + currentUser.getUid());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));

                    }
                }
            });
        }else{
            Toast.makeText(getApplicationContext(),"Por favor rellena todos los campos",
                    Toast.LENGTH_LONG).show();

        }
        btnEntra.setEnabled(false);
    }

    public void mostrarPopUp() {
       FragmentoDialogoAviso a = new FragmentoDialogoAviso();
       a.show(getSupportFragmentManager(),"ee");
    }

    private void requestlocation(){
        ActivityCompat.requestPermissions(this,new String[]{ACCESS_FINE_LOCATION},1);
    }

    private void pillarLocalizacion() {

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION","No accedo localizacion");
            requestlocation();
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("LOCATION","La location NO es null");
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    Location paco = new Location("nico");

                    paco.setLatitude(41.569363);
                    paco.setLongitude(1.995336);

                    double dsitancia = location.distanceTo(paco);

                    Log.d("LOCATION","dsitancia total " + dsitancia/1000 + "Km");


                }else{
                    Log.d("LOCATION","La location es null");
                }
            }
        });
    }
}

