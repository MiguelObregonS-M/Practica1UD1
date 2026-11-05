package com.miguelo.editormvc.arteurb.base;

import java.time.LocalDate;

/**
 * Herencia de obraurbana.
 * Obra de tipo lienzo, con una técnica.
 */
public class Lienzo extends ObraUrbana {

    private String tecnica;

    public Lienzo() {
    }

    public Lienzo(String codigo, String titulo, String artista, LocalDate fecha, double precio, String tecnica) {
        super(codigo, titulo, artista, fecha, precio);
        this.tecnica = tecnica;
    }

    public String getTecnica() {
        return tecnica;
    }

    public void setTecnica(String tecnica) {
        this.tecnica = tecnica;
    }

    @Override
    public String toString() {
        return "Lienzo: " + getCodigo() + " " + getTitulo() + " " + getArtista();
    }
}
