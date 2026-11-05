package com.miguelo.editormvc.arteurb.base;

import java.time.LocalDate;

/**
 * Herencia de obraurbana.
 * Obra de tipo serigrafia, con una tirada.
 */
public class Serigrafia extends ObraUrbana {

    private int tirada;

    public Serigrafia() {
    }

    public Serigrafia(String codigo, String titulo, String artista, LocalDate fecha, double precio, int tirada) {
        super(codigo, titulo, artista, fecha, precio);
        this.tirada = tirada;
    }

    public int getTirada() {
        return tirada;
    }

    public void setTirada(int tirada) {
        this.tirada = tirada;
    }

    @Override
    public String toString() {
        return "Serigrafia: " + getCodigo() + " " + getTitulo() + " " + getArtista();
    }
}
