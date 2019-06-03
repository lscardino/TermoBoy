package com.example.termoboy;

import android.app.Activity;
import android.util.Log;

import java.util.Map;
import java.util.TreeMap;

public class Calculos {

    //Depende la luminosidad, cambias el fondo.
    public static String devolverLuminosidad(String luxes, Activity activity) {
        float luxesF = Float.parseFloat(luxes);
        String luminosidadActual = "";

        if (luxesF <= 40) {
            luminosidadActual = activity.getString(R.string.noche);
        } else if (luxesF <= 2000) {
            luminosidadActual = activity.getString(R.string.nublado);
        } else {
            luminosidadActual = activity.getString(R.string.cieloDespejado);
        }

        return luminosidadActual;
    }

    //Procesa los datos recividos para indicar el tiempo que hace.
    public static String comoEstaElTiempoEh(String mmCubicos,
                                            String velViento, String sensacionTermica, Activity activity) {

        String comoEstaElClima = "";
        float velVientoF = Float.valueOf(velViento);
        float mmCubicosF = Float.valueOf(mmCubicos);
        float sensacionTermicaF = Float.valueOf(sensacionTermica);

        int coche = 0;
        int bici = 0;
        int apie = 0;
        int tPub = 0;
        int casa = 0;

        //Sensacion termica.
        if (sensacionTermicaF > 30) {
            comoEstaElClima += activity.getString(R.string.sTermica30);
            apie--;
            tPub -= 2;
            coche += 2;

        } else if (sensacionTermicaF > 25) {
            comoEstaElClima += activity.getString(R.string.sTermica25);
            tPub--;


        } else if (sensacionTermicaF > 15) {
            comoEstaElClima += activity.getString(R.string.sTermica15);
            bici += 2;
            apie++;
            coche -= 2;


        } else if (sensacionTermicaF > 7) {
            comoEstaElClima += activity.getString(R.string.sTermica7);
            tPub += 2;
            apie++;
            coche--;

        } else if (sensacionTermicaF > 0) {

            comoEstaElClima += activity.getString(R.string.sTermica0);
            coche++;
            tPub += 2;
            bici -= 2;

        } else {
            comoEstaElClima += activity.getString(R.string.sTermicaM0);
            coche += 3;
            apie -= 3;
            bici -= 4;
        }


        //Viento
        if (velVientoF > 110) {
            comoEstaElClima += activity.getString(R.string.viento110);
            casa += 20;

        } else if (velVientoF > 40) {
            comoEstaElClima += activity.getString(R.string.viento40);
            bici -= 3;
            apie -= 2;
            coche++;
            tPub += 2;

        } else {
            comoEstaElClima += activity.getString(R.string.viento0);
            coche -= 4;
            bici += 3;
            apie += 3;
        }

        //luvia
        if (mmCubicosF > 100) {
            comoEstaElClima += activity.getString(R.string.mmh100);
            casa += 3;
            coche += 3;
            apie -= 6;
            bici -= 8;
            tPub--;

        } else if (mmCubicosF > 40) {
            comoEstaElClima += activity.getString(R.string.mmh40);
            coche += 3;
            apie -= 4;
            bici -= 5;
            tPub--;


        } else if (mmCubicosF > 10) {
            comoEstaElClima += activity.getString(R.string.mmh10);
            coche++;
            apie -= 2;
            bici -= 2;

        } else if (mmCubicosF > 2.5) {
            comoEstaElClima += activity.getString(R.string.mmh2_5);
            coche++;
            bici--;


        } else if (mmCubicosF > 0) {
            comoEstaElClima += activity.getString(R.string.mmh0_);
            tPub++;
            coche--;


        } else {
            comoEstaElClima += activity.getString(R.string.mmhNada);
            apie += 3;
            bici += 5;
            coche -= 4;
        }

        Log.d("VALOR", "Coche: " + coche);
        Log.d("VALOR", "Bici: " + bici);
        Log.d("VALOR", "TPub: " + tPub);
        Log.d("VALOR", "A pie: " + apie);

        Map<Integer, String> listaVehiculos = new TreeMap<>();
        listaVehiculos.put(coche, "venir en coche");
        listaVehiculos.put(bici, "venir en bici");
        listaVehiculos.put(apie, "venir a pie");
        listaVehiculos.put(tPub, "coger el transporte publico");
        listaVehiculos.put(casa, "quedarte en casa");

        Log.d("VALOR", "Mapa " + listaVehiculos);

        String[] seleccionaricono = comoEstaElClima.split("-");

        String enviar = "el cielo está despejado";
        for (String dato :
                seleccionaricono) {
            Log.d("VALOR", "elemento " + dato);

            if (dato.equals("HURACAN")) {
                enviar = "HAY UN HURACAN";
                break;
            } else if (dato.contains("lluvia") || dato.contains("gotas")
                    || dato.contains("lloviendo")
                    || dato.contains("llueve")) {
                enviar = dato;
                if (dato.contains("vientos")) {
                    enviar = "hay tormenta";
                    break;
                }
                break;
            } else {
                if (dato.contains("vientos")) {
                    enviar = "hay fuertes vientos";
                }
            }
            Log.d("VALOR", "dato a enviar de momento " + enviar);

        }
        Log.d("VALOR", "dato a enviar " + enviar);


        comoEstaElClima += ((TreeMap<Integer, String>) listaVehiculos).lastEntry().getValue() + "-" + enviar;


        return comoEstaElClima;
    }

}
