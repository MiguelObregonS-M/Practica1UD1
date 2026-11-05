package com.miguelo.editormvc.arteurb.base;

import java.time.LocalDate;

/**
 * Clase abstracta que representa una obra de arte urbano.
 * Guarda los datos comunes a todas las obras.
 */
public abstract class ObraUrbana {

    private String codigo;
    private String titulo;
    private String artista;
    private LocalDate fecha;
    private double precio;

    public ObraUrbana(){
    }

    public ObraUrbana(String codigo, String titulo, String artista, LocalDate fecha, double precio) {
        this.codigo = codigo;
        this.titulo = titulo;
        this.artista = artista;
        this.fecha = fecha;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
