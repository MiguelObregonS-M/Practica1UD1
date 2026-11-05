package com.miguelo.editormvc.arteurb;

import com.miguelo.editormvc.arteurb.gui.ArteUrbanoControlador;
import com.miguelo.editormvc.arteurb.gui.ArteUrbanoModelo;
import com.miguelo.editormvc.arteurb.gui.Vista;

/**
 * Arranca la aplicacion
 */
public class Principal {
    public static void main(String[] args) {
        Vista vista = new Vista();
        ArteUrbanoModelo modelo = new ArteUrbanoModelo();
        ArteUrbanoControlador controlador = new ArteUrbanoControlador(vista, modelo);
    }
}
