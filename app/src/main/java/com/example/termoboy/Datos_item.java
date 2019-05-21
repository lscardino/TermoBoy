package com.example.termoboy;

public class Datos_item {
    private String nombre;
    private String valor;
    private String medida;

    public Datos_item(String nombre, String valor, String medida) {
        this.nombre = nombre;
        this.valor = valor;
        this.medida = medida;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }
}
