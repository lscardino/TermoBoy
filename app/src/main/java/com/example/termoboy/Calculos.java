package com.example.termoboy;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Calculos {
    float presionF;
    float velVientoF;
    float humedadF;
    float temeraturaF;
    float mmCubicosF;
    float sensacionTermicaF;
    float cantidadPolvoF;


    //Depende la luminosidad, cambias el fondo.
    public static String devolverLuminosidad(String luxes) {
        float luxesF = Float.parseFloat(luxes);
        String luminosidadActual = "";

        if (luxesF >= 40) {
            luminosidadActual = "Noche";
        } else {
            luminosidadActual = "Dia";
        }

        /*
        if (luxesF >= 100000) {
            luminosidadActual = "Muy iluminado";
        } else if (luxesF >= 10000) {
            //Esto es un día nublado o el medio dia
            luminosidadActual = "Nublado";
        } else if (luxesF >= 400) {
            //Tormenta o dia lluvioso
            luminosidadActual = "Oscuro";
        } else if (luxesF >= 40) {
            //muy nublado
            luminosidadActual = "Muy oscuro";
        } else {
            luminosidadActual = "Noche";
        }
        */
        return luminosidadActual;
    }

    //Procesa los datos recividos para indicar el tiempo que hace.
    public static String comoEstaElTiempoEh(String mmCubicos,
                                            String velViento, String sensacionTermica, String presion) {

        String comoEstaElClima = ".";
        float velVientoF = Float.valueOf(velViento);
        float mmCubicosF = Float.valueOf(mmCubicos);
        float sensacionTermicaF = Float.valueOf(sensacionTermica);

        int coche = 0;
        int bici = 0;
        int apie = 0;
        int tPub = 0;
        int casa = 0;


        HashMap<String, Integer> listaVehiculos = new HashMap<>();
        listaVehiculos.put("Coche", 0);
        listaVehiculos.put("Bici", 0);
        listaVehiculos.put("A pie", 0);
        listaVehiculos.put("T publico", 0);


        //Sensacion termica.
        if (sensacionTermicaF > 30) {
            comoEstaElClima += "Calor abrasador.";
            apie--;
            tPub -=2;
            coche+=2;

        } else if (sensacionTermicaF > 25) {
            comoEstaElClima += "Calorcete.";
            tPub--;



        } else if (sensacionTermicaF > 15) {
            comoEstaElClima += "Temperatura agradable.";
            bici = +2;
            apie++;
            coche -= 2;


        } else if (sensacionTermicaF > 7) {
            tPub+=2;
            apie++;
            coche--;

            comoEstaElClima += "Fresca.";
        } else if (sensacionTermicaF > 0) {
            bici--;
            apie--;


            comoEstaElClima += "Frio.";
            coche++;
            tPub = +2;
            bici = -2;

        } else {
            comoEstaElClima += "Congelado.";
            coche = +3;
            casa += 7;
            apie -= 3;
            bici -= 4;
        }

        //Luminosidad
        //float luxesF = Float.parseFloat(luxes);


        //Viento
        //Quizas 3 valores es muy poco
        if (velVientoF > 110) {
            comoEstaElClima += "HURACAN.";
            casa += 20;

        } else if (velVientoF > 40) {
            comoEstaElClima += "Fuertes Vientos.";
            bici = -3;
            apie -= 2;
            coche++;
            tPub+=2;

        } else {
            comoEstaElClima += "Brisa agradable.";
            coche -= 4;
            bici += 3;
            apie += 3;


        }

        //Lluvia Intensidad (mm/h)

        if (mmCubicosF > 100) {


            comoEstaElClima += "Lluvia Torrencial.";
            casa = +3;
            coche = +3;
            apie -= 6;
            bici -= 8;
            tPub--;

        } else if (mmCubicosF > 40) {
            comoEstaElClima += "Fuerte Lluvia.";
            casa = +1;
            coche = +3;
            apie -= 4;
            bici -= 5;
            tPub--;


        } else if (mmCubicosF > 10) {
            comoEstaElClima += "Lluvia Moderada.";
            coche++;
            apie-=2;
            bici -=2;

        } else if (mmCubicosF > 2.5) {
            comoEstaElClima += "Ligera Lluvia.";
            coche++;
            bici--;


        } else if (mmCubicosF > 0) {
            comoEstaElClima += "Llovizna.";
            tPub++;
            coche--;


        } else {
            comoEstaElClima += "Despejado.";
            apie +=3;
            bici +=5;
            coche-=4;
        }

        Log.d("VALOR", "Coche: " + coche);
        Log.d("VALOR", "Bici: " + bici);
        Log.d("VALOR", "TPub: " + tPub);
        Log.d("VALOR", "A pie: " + apie);

        List<Integer> listaFinal = new ArrayList<>(listaVehiculos.values());



        int maxValor =0;
        String trasnporteEscogido;

        /*
        Iterator it = listaVehiculos.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pareja = (Map.Entry)it.next();
            if (pareja.getValue())

        }*/




        comoEstaElClima += "final";


        return comoEstaElClima;
    }

}
