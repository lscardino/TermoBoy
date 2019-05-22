package com.example.termoboy;

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
        return luminosidadActual;
    }

    //Procesa los datos recividos para indicar el tiempo que hace.
    public static String comoEstaElTiempoEh(String temperatura, String humedad, String mmCubicos,
                                            String velViento, String sensacionTermica, String presion) {

        String comoEstaElClima = ".";

        float presionF = Float.valueOf(presion);
        float velVientoF = Float.valueOf(velViento);
        float humedadF = Float.valueOf(humedad);
        float temeraturaF = Float.valueOf(temperatura);
        float mmCubicosF = Float.valueOf(mmCubicos);
        float sensacionTermicaF = Float.valueOf(sensacionTermica);

        boolean calorAbrasador = false;
        boolean frioPeroQueMuyFrio = false;

        //Sensacion termica.
        if (sensacionTermicaF > 30) {
            comoEstaElClima += "Calor abrasador.";
        } else if (sensacionTermicaF > 25) {
            comoEstaElClima += "Calorcete.";
        } else if (sensacionTermicaF > 15) {
            comoEstaElClima += "Temperatura agradable.";
        } else if (sensacionTermicaF > 7) {
            comoEstaElClima += "Fresca.";
        } else if (sensacionTermicaF > 0) {
            comoEstaElClima += "Frio.";
        } else {
            comoEstaElClima += "Congelado.";
        }

        //Viento
        if (velVientoF > 110) {
            comoEstaElClima += "HURACAN.";
        } else if (velVientoF > 40) {
            comoEstaElClima += "Fuertes Vientos.";
        } else {
            comoEstaElClima += "Brisa agradable.";
        }

        //Lluvia Intensidad (mm/h)
        if (mmCubicosF > 100){
            comoEstaElClima += "Lluvia Torrencial.";
        }else if(mmCubicosF > 40){
            comoEstaElClima += "Fuerte Lluvia.";
        }else if(mmCubicosF>10){
            comoEstaElClima += "Lluvia Moderada.";
        }else if(mmCubicosF > 2.5){
            comoEstaElClima += "Ligera Lluvia.";
        }else if (mmCubicosF > 0){
            comoEstaElClima += "Llovizna.";
        }

        return comoEstaElClima;
    }

}
