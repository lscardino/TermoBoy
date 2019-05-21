package com.example.termoboy;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;

public class MainActivity extends AppCompatActivity {

    LinearLayout layoutPrincipal;
    TextView txtTemp;
    TextView txtHumedad;
    TextView txtLluvia;
    TextView txtPresion;
    TextView txtDia;
    TextView txtConsejo;
    ImageView imgTiempo;

    String fecha;
    String presion;
    String velViento;
    String humedad;
    String temeratura;
    String mmCubicos;

    Map<String, Boolean> actualWeather;
    String anteriorWeather = "Soleado";
    String nuevoWeather;
    ArrayList<transporte_item> listaDeTrasnportes;
    ArrayList<Datos_item> listaDeDatosIzquierda;
    ArrayList<Datos_item> listaDeDatosDerecha;


    private RecyclerView recyclerViewIZ;
    private RecyclerView.Adapter adapterIZ;
    private RecyclerView.LayoutManager layoutManagerIZ;

    private RecyclerView recyclerViewDR;
    private RecyclerView.Adapter adapterDR;
    private RecyclerView.LayoutManager layoutManagerDR;

    AnimationDrawable animationDrawable;
    Animation fadeOut;
    Animation fadeIn;
    TransitionDrawable transicionFondo;
    TransitionDrawable transicionIcono;


    private FirebaseDatabase fireDataBase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //Elementos del layout
        layoutPrincipal = findViewById(R.id.layoutPrincipal);
        //txtHumedad = findViewById(R.id.txtHumedad);
        //txtTemp = findViewById(R.id.txtTemperatura);
        //txtLluvia = findViewById(R.id.txtLluvia);
        txtDia = findViewById(R.id.txtDia);
        txtConsejo = findViewById(R.id.txtConsejo);
        imgTiempo = findViewById(R.id.imgTiempoViejo);

        //Animaciones
        transicionFondo = (TransitionDrawable) layoutPrincipal.getBackground();
        transicionIcono =  (TransitionDrawable) ContextCompat.getDrawable(this, R.drawable.transicion_sol_nube);
        fadeOut = AnimationUtils.loadAnimation(getApplicationContext()
                ,R.anim.fadeout);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext()
                ,R.anim.fadein);


        //DATE y TIME's
        DateFormat df = new DateFormat();
        fecha = df.format("dd-MM", new Date()).toString();
        txtDia.setText(fecha);

        //Arrays y listas
        actualWeather = new HashMap<>();
        actualWeather.put("Soleado", false);
        actualWeather.put("Nublado", false);
        actualWeather.put("Ventoso", false);
        actualWeather.put("Lloviendo", false);

        listaDeTrasnportes = new ArrayList<>();
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_bici,"Bici",0));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_coche,"Coche",0));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_tren,"Transporte Público",0));
        listaDeTrasnportes.add(new transporte_item(R.drawable.ic_apie,"Caminando",0));

        listaDeDatosIzquierda = new ArrayList<>();
        listaDeDatosIzquierda.add(new Datos_item("TEMP","30","ºC"));
        listaDeDatosIzquierda.add(new Datos_item("%HUM","45","%"));
        listaDeDatosIzquierda.add(new Datos_item("LLUV","0","mm3"));

        listaDeDatosDerecha = new ArrayList<>();
        listaDeDatosDerecha.add(new Datos_item("VEL. VIENTO","20","Km/H"));
        listaDeDatosDerecha.add(new Datos_item("S. TERMICA","23","ºC"));
        listaDeDatosDerecha.add(new Datos_item("PRES","34","Atm"));

        txtConsejo.setText("Hace un día estupendo para ir en bici!");

        //Recycled views
        recyclerViewIZ = findViewById(R.id.RecycledDatosIzquierda);
        recyclerViewIZ.setHasFixedSize(true);
        layoutManagerIZ = new LinearLayoutManager(this);
        adapterIZ = new Datos_Adapter(listaDeDatosIzquierda);
        recyclerViewIZ.setLayoutManager(layoutManagerIZ);
        recyclerViewIZ.setAdapter(adapterIZ);


        recyclerViewDR = findViewById(R.id.RecycledDatosDerecha);
        recyclerViewDR.setHasFixedSize(true);
        layoutManagerDR = new LinearLayoutManager(this);
        adapterDR = new Datos_Adapter(listaDeDatosDerecha);
        recyclerViewDR.setLayoutManager(layoutManagerDR);
        recyclerViewDR.setAdapter(adapterDR);


        //Conexión Firebase
        fireDataBase = FirebaseDatabase.getInstance();
        databaseReference = fireDataBase.getReference("Dia");
        Query ultimaFehca = databaseReference.orderByKey().limitToLast(1);
        //Query ultimaHora = ultimaFehca.orderByKey().limitToLast(1);

        ultimaFehca.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("VALOR", child.getKey());
                    Query ultimaHora = child.getRef().orderByKey().limitToLast(1);
                    ultimaHora.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.d("VALOR", "segundo valor" + child.getKey());
                                String horaFinal = child.getKey();
                                temeratura = child.child("Temperatura").getValue().toString();
                                humedad = child.child("Humedad").getValue().toString();
                                presion = child.child("Presión").getValue().toString();
                                velViento = child.child("Velocidad viento").getValue().toString();
//                                txtHumedad.setText("Humedad " + humedad.substring(0, 5) + "%");
  //                              txtTemp.setText("Temperatura " + temeratura.substring(0, 5) + "ºC");
                                //El resto

                                listaDeDatosIzquierda.clear();

                                listaDeDatosIzquierda.add(new Datos_item("TEMP",temeratura.substring(0,5),"ºC"));
                                listaDeDatosIzquierda.add(new Datos_item("%HUM",humedad.substring(0,5),"%"));
                                listaDeDatosIzquierda.add(new Datos_item("LLUV","0","mm3"));


                                recyclerViewIZ = findViewById(R.id.RecycledDatosIzquierda);
                                recyclerViewIZ.setHasFixedSize(true);
                                layoutManagerIZ = new LinearLayoutManager(getApplicationContext());
                                adapterIZ = new Datos_Adapter(listaDeDatosIzquierda);
                                recyclerViewIZ.setLayoutManager(layoutManagerIZ);
                                recyclerViewIZ.setAdapter(adapterIZ);



                                Log.d("Valor", "cantidad de datos: " + child.getChildrenCount());
                                estadoActual();
                                cambiarFondo();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void cambiarFondo() {
        float temp = Float.parseFloat(temeratura);

        Log.d("DATOS", "Tiempo anterior antes del switch? : " + anteriorWeather);
        switch (anteriorWeather) {
            case "Soleado":
                deSoleadoa();
                break;
            case "Nublado":
                deNubladoA();
                break;
        }

/*
        if (temp > 35){

            AnimationDrawable animationDrawable = (AnimationDrawable)layoutPrincipal.getBackground();
            animationDrawable.setEnterFadeDuration(2000);
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.setOneShot(true);
            animationDrawable.start();
            //layoutPrincipal.setBackground(getDrawable(R.drawable.gradiente_lila_gris));
        }else{
            //layoutPrincipal.setBackground(getDrawable(R.drawable.gradiente_1));
        }
        */

    }

    public void estadoActual() {
        float presionF = Float.parseFloat(presion);
        float velVientoF = Float.parseFloat(velViento);
        float humedadF = Float.parseFloat(humedad);
        float temeraturaF = Float.parseFloat(temeratura);
//        float mmCubicosF = Float.parseFloat(mmCubicos);

        String actual;

        if (temeraturaF > 50.0) {
            //Está lloviendo
            actual = "Nublado";
            Log.d("DATOS", "Segun los datos está Nublado");

        } else if (velVientoF > 100) {
            //Hace viento
            actual = "Ventoso";
        } else {
            //Hace sol - de hecho habría que mirar lo de la luminosidad.
            actual = "Soleado";
            Log.d("DATOS", "segun lso datos está Soleado");
        }

        limpiarBools(actual);
    }

    //limpia los booleanos - si está lloviendo no puede estar soleado.
    public void limpiarBools(String esteNo) {
        Iterator it = actualWeather.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pareja = (Map.Entry) it.next();

            if ((boolean) pareja.getValue() == true) {
                //Pilla el tiempo que estaba puesto antes
                anteriorWeather = pareja.getKey().toString();
                Log.d("DATOS", "Tiempo anterior: " + anteriorWeather);
            }

            if (pareja.getKey().equals(esteNo)) {
                pareja.setValue(true);
                nuevoWeather = pareja.getKey().toString();
                Log.d("DATOS", "Tiempo nuevo: " + anteriorWeather);
            } else {
                pareja.setValue(false);
            }
        }
    }

    public void deNubladoA() {

        //animationDrawable.stop();
        switch (nuevoWeather) {
            case "Soleado":
                Log.d("DATOS", "Estaba nublado y pasa a soleado");
                /*layoutPrincipal.setBackground(getDrawable(R.drawable.nublado_a_solead));
                animationDrawable = (AnimationDrawable) layoutPrincipal.getBackground();
                animationDrawable.setEnterFadeDuration(1000);
                animationDrawable.setExitFadeDuration(3000);
                animationDrawable.setOneShot(true);
                animationDrawable.start();
                */
                txtConsejo.setText("Hace un día estupendo para ir en bici!");
                transicionFondo.reverseTransition(5000);
                imgTiempo.setImageDrawable(transicionIcono);
                transicionIcono.reverseTransition(5000);
                break;
            default:
                break;

        }
    }

    public void deSoleadoa() {
        //animationDrawable.stop();
        switch (nuevoWeather) {
            case "Nublado":

                Log.d("DATOS", "Estaba soleado y pasa a nublado");
                                /*layoutPrincipal.setBackground(getDrawable(R.drawable.lista_gradientes));
                animationDrawable = (AnimationDrawable) layoutPrincipal.getBackground();
                animationDrawable.setEnterFadeDuration(1000);
                animationDrawable.setExitFadeDuration(3000);
                animationDrawable.setOneShot(true);
                animationDrawable.start();
                */
                txtConsejo.setText("Mejor que hoy vayas en bus, y ojo no te olvides el paraguas!");
                transicionFondo.startTransition(5000);
                imgTiempo.setImageDrawable(transicionIcono);
                transicionIcono.startTransition(5000);
                //imgTiempo.startAnimation(fadeOut);
                //imgTiempo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube));

                break;
            default:
                break;
        }
    }

    private void anadirFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    }

}
