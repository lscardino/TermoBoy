package com.example.termoboy;

public class transporte_item {
    private int transporteImg;
    private String transporteNombre;
    private String transporteID;
    private int transporteCantidad;

    public transporte_item(int transporteImg, String transporteNombre, String transporteID, int transporteCantidad) {
        this.transporteImg = transporteImg;
        this.transporteNombre = transporteNombre;
        this.transporteID = transporteID;
        this.transporteCantidad = transporteCantidad;
    }

    public int getTransporteImg() {
        return transporteImg;
    }

    public void setTransporteImg(int transporteImg) {
        this.transporteImg = transporteImg;
    }

    public String getTransporteNombre() {
        return transporteNombre;
    }

    public void setTransporteNombre(String transporteNombre) {
        this.transporteNombre = transporteNombre;
    }

    public int gettransporteCantidad() {
        return transporteCantidad;
    }

    public void settransporteCantidad(int transporteCantidad) {
        this.transporteCantidad = transporteCantidad;
    }

    public String getTransporteID() {
        return transporteID;
    }

    public void setTransporteID(String transporteID) {
        this.transporteID = transporteID;
    }
}
