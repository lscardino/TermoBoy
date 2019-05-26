package com.example.termoboy;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.app.Fragment;
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

public class MenuPrincipalActivity extends Fragment implements OnClickListener {

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
    String estadoInicial = "Soleado";
    String luminosidadInicial = "Dia";

    String estadoACambiar;
    String luminosidadNueva;


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

    public MenuPrincipalActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_principal_menu, container, false);

        //Elementos del layout
        layoutPrincipal = view.findViewById(R.id.layoutPrincipal);
        txtDia = view.findViewById(R.id.txtDia);
        txtInforGeneral = view.findViewById(R.id.txtInformacionGeneral);
        txtConsejo = view.findViewById(R.id.txtConsejo);
        txtInforGeneral = view.findViewById(R.id.txtInformacionGeneral);
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

        //Arrays y listas
        actualWeather = new HashMap<>();
        actualWeather.put("Soleado", false);
        actualWeather.put("Nublado", false);
        actualWeather.put("Ventoso", false);
        actualWeather.put("Lloviendo", false);

        listaDeDatosIzquierda = new ArrayList<>();
        listaDeDatosIzquierda.add(new Datos_item("TEMP", "30", "ºC"));
        listaDeDatosIzquierda.add(new Datos_item("%HUM", "45", "%"));
        listaDeDatosIzquierda.add(new Datos_item("LLUV", "0", "mm3"));

        listaDeDatosDerecha = new ArrayList<>();
        listaDeDatosDerecha.add(new Datos_item("VEL. VIENTO", "20", "Km/H"));
        listaDeDatosDerecha.add(new Datos_item("S. TERMICA", "23", "ºC"));
        listaDeDatosDerecha.add(new Datos_item("PRES", "34", "Atm"));

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

        //Primitivos
        //quizás esto habrái que guardarlo en otra parte...
        //Y de hecho, una vez tengamos lo de los lumens, esto será innecesario -
        //El fondo se cambiará en otro metodo ya que depende de variables difernetes
        //a aquellas que controlan los iconos.
        primerChequeo = true;

        //User DB
        mAuth = FirebaseAuth.getInstance();
        usuario = mAuth.getCurrentUser();

        //Boton prueba a borrar
        Button prueba = view.findViewById(R.id.btPrueba);
        prueba.setOnClickListener(this);

        //Conexión Firebase
        fireDataBase = FirebaseDatabase.getInstance();
        databaseReference = fireDataBase.getReference("Dia");
        Query ultimaFehca = databaseReference.orderByKey().limitToLast(1);
        //Query ultimaHora = ultimaFehca.orderByKey().limitToLast(1);

        ultimaFehca.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("KEY: ", dataSnapshot.getKey());
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.d("KEY HIJO PRINCIPAL", child.getKey());
                    Query ultimaHora = child.getRef().orderByKey().limitToLast(2);
                    ultimaHora.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot child : dataSnapshot.getChildren()) {
                                Log.d("VALOR", "Valor a leer " + child.getKey());

                                //Sequenccia de control, pra ver si es en formato 00:00 el "child"
                                if (!child.getKey().equals("Transporte")) {
                                    Log.d("VALOR", "Valor a leer(DENTRO) " + child.getKey());
                                    Log.d("VALOR", "Hijos de  " + child.getKey() + ": " + child.getChildrenCount());
                                    String horaFinal = child.getKey();
                                    //Izquierda
//ff
                                    if (child.getChildrenCount() == 9) {
                                        temeratura = child.child("Temperatura").getValue().toString();
                                        humedad = child.child("Humedad").getValue().toString();
                                        lluvia = child.child("Lluvia").getValue().toString();
                                        //Derecha
                                        velViento = child.child("Velocidad viento").getValue().toString();
                                        presion = child.child("Presión").getValue().toString();
                                        sensacionT = child.child("Sensacion").getValue().toString();

                                        //Abajo
                                        polvo = child.child("Polvo").getValue().toString();
                                        //

                                        listaDeDatosIzquierda.clear();
                                        listaDeDatosIzquierda.add(new Datos_item("TEMP.", temeratura, "ºC"));
                                        listaDeDatosIzquierda.add(new Datos_item("%HUM.", humedad, "%"));
                                        listaDeDatosIzquierda.add(new Datos_item("LLUV.", lluvia, "mm/h"));

                                        listaDeDatosDerecha.clear();
                                        listaDeDatosDerecha.add(new Datos_item("VEL. VIENTO", velViento, "Km/H"));
                                        listaDeDatosDerecha.add(new Datos_item("S. TERMICA", sensacionT, "ºC"));
                                        listaDeDatosDerecha.add(new Datos_item("PRES.", presion, "Pa"));


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


                                        Log.d("Valor", "cantidad de datos: " + child.getChildrenCount());

                                        String eee = Calculos.comoEstaElTiempoEh(lluvia,velViento,sensacionT,presion);

                                        Log.d("VALOR de eee: " , eee);

                                        //Por alguna razo ensto no funcina -- era por el punto
                                        String[] todoSeparado = eee.split("-");
                                        for (String aa:
                                             todoSeparado) {
                                            Log.d("VALOR elementosDARO: " , aa );
                                        }

                                        String vehiculo = todoSeparado[todoSeparado.length-2];
                                        String tiempo = todoSeparado[todoSeparado.length-1];
                                        txtConsejo.setText("Hoy " + tiempo + ", te recomendamos " + vehiculo);

                                        Log.d("VALOR Vehiculo: ", vehiculo );

                                        Log.d("VALOR Icono: ", tiempo );




                                        /*
                                        if (tiempo.contains("lluvia") || tiempo.contains("gotas")
                                                || tiempo.contains("lloviendo")
                                                || tiempo.contains("llueve")){
                                            //Icono de lluvia
                                        }else if (tiempo.contains("HURACAN")){
                                            //Huracan
                                        }else if(tiempo.contains("vientos")){
                                            //Viento
                                        }else{
                                            //despejado
                                            //Hay que mirar lo de los lumnes aki y de hecho
                                            //Cambiar el icono tmb
                                        }
                                         */
                                        //Lo primero, el fondo.
                                        //Cuando tengamos lo de lso lumens.
                                        //luminosidadNueva = Calculos.devolverLuminosidad(lumens);

                                        //cambiarIcono(tiempo);
                                        //Cambiar la funcion estado actual para qeu haga las movidas chungas de calculos.
                                        estadoACambiar = tiempo;
                                        if (!estadoACambiar.equals(estadoInicial)) {
                                            cambiarIcono(estadoACambiar);
                                            estadoInicial = estadoACambiar;

                                        } else {
                                            //Los iconos se quedan igual
                                        }


                                    }

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


        return view;
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

        return actual;

        //limpiarBools(actual);
    }


    private void cambiarIcono(String tiempoNuevo) {


        if (tiempoNuevo.contains("lluvia") || tiempoNuevo.contains("gotas")
                || tiempoNuevo.contains("lloviendo")
                || tiempoNuevo.contains("llueve")){
            tiempoNuevo = "Lluvia";
        }else if (tiempoNuevo.contains("HURACAN")){
            tiempoNuevo = "HURACAN";
            //Huracan
        }else if(tiempoNuevo.contains("vientos")){
            tiempoNuevo = "Viento";
            //Viento
        }else{
            tiempoNuevo = "Solete";
            //despejado
            //Hay que mirar lo de los lumnes aki y de hecho
            //Cambiar el icono tmb
        }

        //Esto lo hace al revés, pero porque le pasas el valor del tiempo anterior, no del Nuevo.
        //YA NO, já!
        switch (tiempoNuevo) {
            case "Viento":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_viento));
                transicionFondo.reverseTransition(5000);
                break;
            case "Nublado":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube));
                transicionFondo.startTransition(5000);
                break;
            case "Lluvia":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_lluvia));
                transicionFondo.startTransition(5000);
                break;
            case "HURACAN":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_tornado));
                transicionFondo.startTransition(5000);
                break;
            case "Solete":
                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_solete_amarillo));
                transicionFondo.startTransition(5000);
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

    public void escribirBasura(View view) {
        databaseReference = fireDataBase.getReference("movidas");
        databaseReference.child("User/" + usuario.getUid()).setValue("Buenas");

    }

    @Override
    public void onClick(View v) {
        Log.d("DATOS", "Clicl");
        databaseReference = fireDataBase.getReference("movidas");
        databaseReference.child("User/" + usuario.getUid()).setValue("Buenas");
    }

    /*
    public void cambiarFondo() {
        float temp = Float.parseFloat(temeratura);

        Log.d("DATOS", "Tiempo anterior antes del switch? : " + anteriorWeather);


        switch (anteriorWeather) {
            case "Soleado":
                cambiarIcono("Soleado");
                break;
            case "Nublado":
                cambiarIcono("Nublado");
                break;
        }



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


    }
    */

        /*
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
    /*
                txtConsejo.setText("Hace un día estupendo para ir en bici!");
                transicionFondo.reverseTransition(5000);

                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_solete_amarillo));
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imgTiempo.setImageDrawable(getResources().getDrawable(R.drawable.ic_solete_amarillo)); }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });

                imgTiempo.startAnimation(fadeOut);
                imgTiempoNuevo.startAnimation(fadeIn);

                /*imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_solete_amarillo));
                imgTiempoNuevo.setVisibility(View.VISIBLE);
                imgTiempoNuevo.startAnimation(fadeIn);
                imgTiempo = imgTiempoNuevo;

                break;
            default:
                break;

        }
    }
     */
    /*

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
        /*
                txtConsejo.setText("Mejor que hoy vayas en bus, y ojo no te olvides el paraguas!");
                transicionFondo.startTransition(5000);
                //imgTiempo.setImageDrawable(transicionIcono);
                //transicionIcono.startTransition(5000);

                imgTiempoNuevo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube));
                fadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) { }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imgTiempo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube)); }
                    @Override
                    public void onAnimationRepeat(Animation animation) { }
                });
                //imgTiempoNuevo.setVisibility(View.VISIBLE);
                imgTiempoNuevo.startAnimation(fadeIn);
                imgTiempo.startAnimation(fadeOut);
                //imgTiempo = imgTiempoNuevo;
                //imgTiempoNuevo.setVisibility(View.INVISIBLE);

                //imgTiempo.setImageDrawable(getResources().getDrawable(R.drawable.ic_nube));

                break;
            default:
                break;
        }
    }
*/

}
