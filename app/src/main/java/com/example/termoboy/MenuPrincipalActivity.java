package com.example.termoboy;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MenuPrincipalActivity extends Fragment implements OnClickListener{

    LinearLayout layoutPrincipal;
    TextView txtTemp;
    TextView txtHumedad;
    TextView txtLluvia;
    TextView txtPresion;
    TextView txtDia;
    TextView txtConsejo;
    TextView txtInforGeneral;
    TextView txtNivelPolvo;

    ImageView imgTiempo;
    ImageView imgTiempoNuevo;

    String fecha;
    String presion;
    String velViento;
    String humedad;
    String temeratura;
    String lluvia;
    String polvo;
    String sensacionT;
    String lumens;
    String estadoInicial = "el cielo está despejado";

    double distanciaHastaElNico = 0;

    String estadoACambiar;
    String iluminacionVieja = "el cielo está despejado";
    String iluminacionNueva;

    int getUserEdat;
    String getUserGenero;


    boolean primerChequeo;

    Map<String, Boolean> actualWeather;
    String anteriorWeather = "Soleado";
    String nuevoWeather;
    ArrayList<Datos_item> listaDeDatosIzquierda;
    ArrayList<Datos_item> listaDeDatosDerecha;


    private RecyclerView recyclerViewIZ;
    private RecyclerView.Adapter adapterIZ;
    private RecyclerView.LayoutManager layoutManagerIZ;

    private RecyclerView recyclerViewDR;
    private RecyclerView.Adapter adapterDR;
    private RecyclerView.LayoutManager layoutManagerDR;

    Animation fadeOut;
    Animation fadeIn;
    TransitionDrawable transicionFondo;


    private FirebaseDatabase fireDataBase;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth;
    private FirebaseUser usuario;

    SharedPreferences getPrefUser;
    SharedPreferences recordarUID;

    SharedPreferences.Editor editor;

    private FusedLocationProviderClient client;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager mLocationManager;

    private LocationRequest mLocationRequest;
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */


    public MenuPrincipalActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        usuario = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        usuario = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_principal_menu, container, false);

        iniciarElementos(view);
        listasIniciales(view);

        pillarLocalizacion();
        lasSharedPref(view);

        leerFirebase(view);


        return view;
    }

    private void leerFirebase(final View view) {

        //Conexión Firebase
        fireDataBase = FirebaseDatabase.getInstance();
        databaseReference = fireDataBase.getReference("Dia");
        Query ultimaFehca = databaseReference.orderByKey().limitToLast(1);


        Log.d("KEY", "Ultima Fecha " + ultimaFehca.getRef().getKey());
        ultimaFehca.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("KEY: ", dataSnapshot.getKey());
                //Ultima Hora
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    Log.d("KEY HIJO PRINCIPAL", child.getKey());
                    Query ultimaHora = child.child("Hora").getRef().orderByKey().limitToLast(1);
                    ultimaHora.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.d("VALOR", "Valor a leer " + child.getKey());

                                //Sequenccia de control, pra ver si es en formato 00:00 el "child"
                                    Log.d("VALOR", "Valor a leer(DENTRO) " + child.getKey());
                                    Log.d("VALOR", "Hijos de  " + child.getKey() + ": " + child.getChildrenCount());
                                    String horaFinal = child.getKey();
                                    //Izquierda
//ff
                                        temeratura = child.child("Temperatura").getValue().toString();
                                        humedad = child.child("Humedad").getValue().toString();
                                        lluvia = child.child("Lluvia").getValue().toString();
                                        //Derecha
                                        velViento = child.child("Velocidad viento").getValue().toString();
                                        presion = child.child("Presión").getValue().toString();
                                        sensacionT = child.child("Sensacion").getValue().toString();

                                        //Abajo
                                        polvo = child.child("Polvo").getValue().toString();
                                        txtNivelPolvo.setText(getActivity().getString(R.string.nivelDePolvo) + polvo);

                                        //Lumens
                                        lumens = child.child("Lumens").getValue().toString();

                                        actualizarDatos(view);




                                        Log.d("Valor", "cantidad de datos: " + child.getChildrenCount());

                                        String eee = Calculos.comoEstaElTiempoEh(lluvia, velViento, sensacionT, getActivity());

                                        Log.d("VALOR de eee: ", eee);

                                        //Por alguna razo ensto no funcina -- era por el punto
                                        String[] todoSeparado = eee.split("-");
                                        for (String aa :
                                                todoSeparado) {
                                            Log.d("VALOR elementosDARO: ", aa);
                                        }

                                        String vehiculo = todoSeparado[todoSeparado.length - 2];
                                        String tiempo = todoSeparado[todoSeparado.length - 1];

                                        //Comprobación lógica, cruza la distqancia con el vehiculo que haya escogido, no te va a recomedar ir en bici si vives a 30 km
                                        vehiculo = vivesToLejos(vehiculo);


                                        txtConsejo.setText(getString(R.string.hoy) + " " + tiempo + getString(R.string.recomendamos) + " " + vehiculo);

                                        Log.d("VALOR Vehiculo: ", vehiculo);

                                        Log.d("VALOR Icono: ", tiempo);



                                        estadoACambiar = tiempo;
                                        iluminacionNueva = Calculos.devolverLuminosidad(lumens, getActivity());
                                        if (!estadoACambiar.equals(estadoInicial)) {
                                            if (tiempo.equals(getString(R.string.despejadoSinGuion)) && Float.parseFloat(lumens)< 40) {
                                                cambiarIcono(getString(R.string.noche));
                                                estadoInicial = getString(R.string.noche);
                                                // Y la luminosidad está baja - pon una luna
                                            }else {
                                                cambiarIcono(estadoACambiar);
                                                estadoInicial = estadoACambiar;
                                            }

                                        } //SI no, los iconos se quedan igual


                                        if (!iluminacionNueva.equals(iluminacionVieja)) {
                                            //la iluminacion es difernete
                                            //DEberias
                                            cambiarFondo(iluminacionVieja,iluminacionNueva);
                                            if (tiempo.equals(getString(R.string.despejadoSinGuion)) && Float.parseFloat(lumens)< 40) {
                                                cambiarIcono(getString(R.string.noche));
                                                estadoInicial = getString(R.string.noche);
                                            }else if (tiempo.equals(getString(R.string.despejadoSinGuion)) && Float.parseFloat(lumens)<= 2000){
                                                cambiarIcono(getString(R.string.nublado));
                                                estadoInicial = getString(R.string.nublado);
                                            }

                                            iluminacionVieja = iluminacionNueva;

                                        }


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

    //Quizas haya que poner getContext más que getActivity
    private void pillarLocalizacion() {
        client = LocationServices.getFusedLocationProviderClient(getContext());

        if (ActivityCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("LOCATION","No accedo localizacion");
            //requestlocation();
            return;
        }
        client.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("LOCATION","La location NO es null");
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    Location instituto = new Location("nico");

                    instituto.setLatitude(41.569363);
                    instituto.setLongitude(1.995336);

                    distanciaHastaElNico = location.distanceTo(instituto)/1000;

                    //txtConsejo.setText("Distancia con el Nico " + dsitancia/1000 + "Km");
                    Log.d("LOCATION","La location es " + location.toString());


                }else{
                    Log.d("LOCATION","La location es null");
                }
            }
        });
    }

    private void requestlocation(){
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_FINE_LOCATION},1);
    }


    //Hace los calculas para saber que decirle al user, quizas sería menester poner esto en otra
    //clase
    public String estadoActual() {
        float presionF = Float.parseFloat(presion);
        float velVientoF = Float.parseFloat(velViento);
        float humedadF = Float.parseFloat(humedad);
        float temeraturaF = Float.parseFloat(temeratura);
//        float mmCubicosF = Float.parseFloat(mmCubicos);

        String actual;

        if (temeraturaF > 50.0) {
            //Está lloviendo
            actual = getString(R.string.nublado);
            Log.d("DATOS", "Segun los datos está Nublado");

        } else if (velVientoF > 100) {
            //Hace viento
            actual = getString(R.string.ventoso);
        } else {
            //Hace sol - de hecho habría que mirar lo de la luminosidad.
            actual = getString(R.string.soleado);
            Log.d("DATOS", "segun lso datos está Soleado");
        }

        return actual;

        //limpiarBools(actual);
    }

    ///OJO SI CAMBIAS LOS STRINGS
    private String vivesToLejos(String vehiculo){
        switch (vehiculo){
            //Bici
            case "venir en bici":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }
                break;
            case "to come biking":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }
                break;
            case "vindre en bici":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }
                break;
                //A pie
            case "venir a pie":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }else if(distanciaHastaElNico > 5){
                    vehiculo = getString(R.string.venirBici);
                }
                break;
            case "vindre a peu":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }else if(distanciaHastaElNico > 5){
                    vehiculo = getString(R.string.venirBici);
                }
                break;
            case "to come by foot":
                if (distanciaHastaElNico > 10){
                    vehiculo = getString(R.string.venirCoche);
                }else if(distanciaHastaElNico > 5){
                    vehiculo = getString(R.string.venirBici);
                }
                break;
            default:
                break;
        }
        return vehiculo;
    }

    private void cambiarIcono(String tiempoNuevo) {


        if (tiempoNuevo.contains(getString(R.string.contieneLluvia)) ||
                tiempoNuevo.contains(getString(R.string.contieneGotas))
                || tiempoNuevo.contains(getString(R.string.contieneLloviendo))
                || tiempoNuevo.contains(getString(R.string.contieneLlueve))) {
            tiempoNuevo = getString(R.string.Lluvia);
        } else if (tiempoNuevo.contains(getString(R.string.contieneHuracan))) {
            tiempoNuevo = getString(R.string.contieneHuracan);
            //Huracan
        } else if (tiempoNuevo.contains(getString(R.string.conteineVientos))) {
            tiempoNuevo = getString(R.string.Viento);
            //Viento
        } else if(tiempoNuevo.contains(getString(R.string.despejado))){
            if (Integer.valueOf(lumens)<=40) {
                tiempoNuevo = getString(R.string.noche);
            }else if(Integer.valueOf(lumens)<=2000){
                tiempoNuevo = getString(R.string.nublado);
            }else{
                tiempoNuevo = getString(R.string.Solete);
            }
            //despejado
            //Hay que mirar lo de los lumnes aki y de hecho
            //Cambiar el icono tmb
        }

        //Esto lo hace al revés, pero porque le pasas el valor del tiempo anterior, no del Nuevo.
        //YA NO, já!
        switch (tiempoNuevo) {
            case "Viento":
            case "Vent":
            case "Wind":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_viento));
                //transicionFondo.reverseTransition(5000);
                break;
                //NUBLADO NO SALE NUNCA
            case "Nublado":
            case "Ennuvolat":
            case "Cloudy":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube));
                //transicionFondo.startTransition(5000);
                break;
            case "Lluvia":
            case "Pluja":
            case "Rain":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_lluvia));
                //transicionFondo.startTransition(5000);
                break;
            case "HURACAN":
            case "HURACÀ":
            case "HURRICANE":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_tornado));
                //transicionFondo.startTransition(5000);
                break;
            case "Solete":
            case "Solet":
            case "Sunny":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_solete_amarillo));
                //transicionFondo.startTransition(5000);
                break;
            case "Noche":
            case "Nit":
            case "Night":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_luna));
                break;
        }

        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                imgTiempo.setImageDrawable(imgTiempoNuevo.getDrawable());
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imgTiempo.startAnimation(fadeOut);
        imgTiempoNuevo.startAnimation(fadeIn);


    }

    public void cambiarFondo(String fondoViejo, String fondoNew){
        if (fondoNew.equals(getString(R.string.nublado))) {
            if (!fondoViejo.equals(getString(R.string.noche))){
                transicionFondo.startTransition(5000);
            }
        }else if(fondoNew.equals(getString(R.string.noche))){
            if (!fondoViejo.equals(getString(R.string.nublado))){
                transicionFondo.startTransition(5000);
            }
        }else{
            transicionFondo.reverseTransition(5000);
        }
    }

    public void iniciarElementos(View view){

        //Elementos del layout
        layoutPrincipal = view.findViewById(R.id.layoutPrincipal);
        txtDia = view.findViewById(R.id.txtDia);
        // txtInforGeneral = view.findViewById(R.id.txtInformacionGeneral);
        txtConsejo = view.findViewById(R.id.txtConsejo);
        // txtInforGeneral = view.findViewById(R.id.txtInformacionGeneral);
        txtNivelPolvo = view.findViewById(R.id.txtPolvo);
        imgTiempo = view.findViewById(R.id.imgTiempoViejo);
        imgTiempoNuevo = view.findViewById(R.id.imgTiempoNuevo);

        imgTiempo.bringToFront();

        //Animaciones
        transicionFondo = (TransitionDrawable) layoutPrincipal.getBackground();
        //transicionIcono =  (TransitionDrawable) ContextCompat.getDrawable(this, R.drawable.transicion_sol_nube);
        fadeOut = AnimationUtils.loadAnimation(view.getContext()
                , R.anim.fadeout);
        fadeIn = AnimationUtils.loadAnimation(view.getContext()
                , R.anim.fadein);


        //DATE y TIME's
        DateFormat df = new DateFormat();
        fecha = df.format("dd-MM", new Date()).toString();
        txtDia.setText(fecha);

        //primitivo
        primerChequeo = true;


    }

    private void actualizarDatos (View view){
        listaDeDatosIzquierda.clear();
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.TEMP), temeratura, "ºC"));
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.HUM), humedad, "%"));
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.LLUVI), lluvia, "mm/h"));

        listaDeDatosDerecha.clear();
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.VELVIENTO), velViento, "Km/H"));
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.STERMC), sensacionT, "ºC"));
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.PRES), presion, "Pa"));


        recyclerViewIZ = view.findViewById(R.id.RecycledDatosIzquierda);
        recyclerViewIZ.setHasFixedSize(true);
        layoutManagerIZ = new LinearLayoutManager(view.getContext());
        adapterIZ = new Datos_Adapter(listaDeDatosIzquierda);
        recyclerViewIZ.setLayoutManager(layoutManagerIZ);
        recyclerViewIZ.setAdapter(adapterIZ);


        recyclerViewDR = view.findViewById(R.id.RecycledDatosDerecha);
        recyclerViewDR.setHasFixedSize(true);
        layoutManagerDR = new LinearLayoutManager(view.getContext());
        adapterDR = new Datos_Adapter(listaDeDatosDerecha);
        recyclerViewDR.setLayoutManager(layoutManagerDR);
        recyclerViewDR.setAdapter(adapterDR);
    }

    public void listasIniciales(View view) {
        //Arrays y listas
        actualWeather = new HashMap<>();
        actualWeather.put(getString(R.string.soleado), false);
        actualWeather.put(getString(R.string.nublado), false);
        actualWeather.put(getString(R.string.ventoso), false);
        actualWeather.put(getString(R.string.contieneLloviendo), false);

        listaDeDatosIzquierda = new ArrayList<>();
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.TEMP), "30", "ºC"));
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.HUM), "45", "%"));
        listaDeDatosIzquierda.add(new Datos_item(getString(R.string.LLUVI), "0", "mm/h"));

        listaDeDatosDerecha = new ArrayList<>();
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.VELVIENTO), "20", "Km/H"));
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.STERMC), "23", "ºC"));
        listaDeDatosDerecha.add(new Datos_item(getString(R.string.PRES), "34", "Pa"));

        //txtConsejo.setText("Hace un día estupendo para ir en bici!");

        //Recycled views
        recyclerViewIZ = view.findViewById(R.id.RecycledDatosIzquierda);
        recyclerViewIZ.setHasFixedSize(true);
        layoutManagerIZ = new LinearLayoutManager(view.getContext());
        adapterIZ = new Datos_Adapter(listaDeDatosIzquierda);
        recyclerViewIZ.setLayoutManager(layoutManagerIZ);
        recyclerViewIZ.setAdapter(adapterIZ);


        recyclerViewDR = view.findViewById(R.id.RecycledDatosDerecha);
        recyclerViewDR.setHasFixedSize(true);
        layoutManagerDR = new LinearLayoutManager(view.getContext());
        adapterDR = new Datos_Adapter(listaDeDatosDerecha);
        recyclerViewDR.setLayoutManager(layoutManagerDR);
        recyclerViewDR.setAdapter(adapterDR);

    }

    public void lasSharedPref (View view){
        //Sharedpref
        getPrefUser = this.getActivity().getSharedPreferences("MisPrefs", Context.MODE_PRIVATE);
        getUserEdat = getPrefUser.getInt("edatUser", 99);
        getUserGenero = getPrefUser.getString("generoUser", "none");

        recordarUID = this.getActivity().getSharedPreferences("prefsUID", Context.MODE_PRIVATE);
        editor = recordarUID.edit();
        //editor.putString("Uid",usuario.getUid() + "-" + getUserEdat + "-" + getUserGenero);
        //editor.putString("generoUser",datoSpinner);
        // editor.apply();
        //El de la uid

    }


    @Override
    public void onClick(View v) {
        if (usuario != null) {
            Log.d("DATOS", "Clicl userID : " + usuario.getUid());
            databaseReference = fireDataBase.getReference("movidas");
            databaseReference.child("User/" + usuario.getUid() + "-" + getUserGenero + "-" + getUserEdat)
                    .setValue("Buenas");
        }else{
            Log.d("DATOS", "Clicl userID : NO FUNCIONA");

        }
    }



}
