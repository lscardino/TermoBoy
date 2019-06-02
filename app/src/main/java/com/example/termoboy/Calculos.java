package com.example.termoboy;

import android.util.Log;

import java.util.Map;
import java.util.TreeMap;

public class Calculos {

    //Depende la luminosidad, cambias el fondo.
    public static String devolverLuminosidad(String luxes) {
        float luxesF = Float.parseFloat(luxes);
        String luminosidadActual = "";

        if (luxesF <= 40) {
            luminosidadActual = "Noche";
        } else if (luxesF <= 2000) {
            luminosidadActual = "Nublado";
        } else {
            luminosidadActual = "el cielo está despejado";
        }

        return luminosidadActual;
    }

    //Procesa los datos recividos para indicar el tiempo que hace.
    public static String comoEstaElTiempoEh(String mmCubicos,
                                            String velViento, String sensacionTermica, String presion) {

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
            comoEstaElClima += "hace un calor abrasador-";
            apie--;
            tPub -= 2;
            coche += 2;

        } else if (sensacionTermicaF > 25) {
            comoEstaElClima += "hace calorcete-";
            tPub--;


        } else if (sensacionTermicaF > 15) {
            comoEstaElClima += "la temperatura es agradable-";
            bici += 2;
            apie++;
            coche -= 2;


        } else if (sensacionTermicaF > 7) {
            tPub += 2;
            apie++;
            coche--;

            comoEstaElClima += "hace fresca-";
        } else if (sensacionTermicaF > 0) {
            bici--;
            apie--;


            comoEstaElClima += "hace frio-";
            coche++;
            tPub += 2;
            bici -= 2;

        } else {
            comoEstaElClima += "las temperaturas están bajo cero-";
            coche += 3;
            apie -= 3;
            bici -= 4;
        }


        //Viento
        if (velVientoF > 110) {
            comoEstaElClima += "HURACAN-";
            casa += 20;

        } else if (velVientoF > 40) {
            comoEstaElClima += "fuertes vientos-";
            bici -= 3;
            apie -= 2;
            coche++;
            tPub += 2;

        } else {
            comoEstaElClima += "una brisa agradable-";
            coche -= 4;
            bici += 3;
            apie += 3;
        }

        if (mmCubicosF > 100) {
            comoEstaElClima += "está lloviendo de forma torrencial-";
            casa += 3;
            coche += 3;
            apie -= 6;
            bici -= 8;
            tPub--;

        } else if (mmCubicosF > 40) {
            comoEstaElClima += "Está cayendo una fuerte lluvia-";
            coche += 3;
            apie -= 4;
            bici -= 5;
            tPub--;


        } else if (mmCubicosF > 10) {
            comoEstaElClima += "llueve moderadamente-";
            coche++;
            apie -= 2;
            bici -= 2;

        } else if (mmCubicosF > 2.5) {
            comoEstaElClima += "está lloviendo ligeramente-";
            coche++;
            bici--;


        } else if (mmCubicosF > 0) {
            comoEstaElClima += "están cayendo cuatro gotas-";
            tPub++;
            coche--;


        } else {
            comoEstaElClima += "el clima está despejado-";
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
