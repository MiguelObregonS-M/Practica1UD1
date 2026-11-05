package com.miguelo.editormvc.arteurb.gui;

import com.miguelo.editormvc.arteurb.base.Lienzo;
import com.miguelo.editormvc.arteurb.base.ObraUrbana;
import com.miguelo.editormvc.arteurb.base.Serigrafia;
import com.miguelo.editormvc.arteurb.util.Util;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.*;
import java.util.Properties;

/**
 * Es el enlace entre la parte gráfica y la parte lógica
 */
public class ArteUrbanoControlador implements ActionListener, ListSelectionListener, WindowListener {
    private Vista vista;
    private ArteUrbanoModelo modelo;
    private File ultimaRutaExportada;

    public ArteUrbanoControlador(Vista vista, ArteUrbanoModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
        try {
            cargarDatosConfiguracion();
        } catch (IOException e) {
            System.out.println("No existe fichero de configuracion " + e.getMessage());
        }
        addActionListener(this);
        addListSelectionListener(this);
        addWindowListener(this);
    }

    private boolean hayCamposVacios() {
        if (vista.codigotxt.getText().isEmpty() ||
                vista.titulotxt.getText().isEmpty() ||
                vista.artistatxt.getText().isEmpty() ||
                vista.preciotxt.getText().isEmpty() ||
                vista.tecnicatxt.getText().isEmpty() ||
                vista.fechaDPicker.getText().isEmpty()) {
            return true;
        }
        return false;
    }

    private void limpiarCampos() {
        vista.list1.clearSelection();
        vista.codigotxt.setText(null);
        vista.titulotxt.setText(null);
        vista.artistatxt.setText(null);
        vista.preciotxt.setText(null);
        vista.tecnicatxt.setText(null);
        vista.fechaDPicker.setText(null);
        vista.codigotxt.requestFocus();
    }

    private void refrescarCampos() {
        vista.dlmarte.clear();
        for (ObraUrbana unaObra : modelo.obtenerListaObras()) {
            vista.dlmarte.addElement(unaObra);
        }
    }

    private void addActionListener(ActionListener listener) {
        vista.nuevoButton.addActionListener(listener);
        vista.exportarButton.addActionListener(listener);
        vista.importarButton.addActionListener(listener);
        vista.eliminarButton.addActionListener(listener);
        vista.lienzoRadioButton.addActionListener(listener);
        vista.serigrafiaRadioButton.addActionListener(listener);
    }

    private void addWindowListener(WindowListener listener) {
        vista.frame.addWindowListener(listener);
    }

    private void addListSelectionListener(ListSelectionListener listener) {
        vista.list1.addListSelectionListener(listener);
    }

    private void cargarDatosConfiguracion() throws IOException {
        Properties configuracion = new Properties();
        configuracion.load(new FileReader("obraurbana.conf"));
        String ruta = configuracion.getProperty("ultimaRutaExportada");
        if (ruta != null) {
            ultimaRutaExportada = new File(ruta);
        }
    }

    private void actualizarDatosConfiguracion(File ultimaRutaExportada) {
        this.ultimaRutaExportada = ultimaRutaExportada;
    }

    private void guardarConfiguracion() throws IOException {
        if (ultimaRutaExportada == null) {
            return;
        }
        Properties configuracion = new Properties();
        configuracion.setProperty("ultimaRutaExportada", ultimaRutaExportada.getAbsolutePath());
        configuracion.store(new PrintWriter("obraurbana.conf"), "Datos configuracion obra urbana");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        switch (actionCommand) {
            case "Nuevo":
                if (hayCamposVacios() || vista.fechaDPicker.getDate() == null) {
                    Util.mensajeError("Los siguientes campos no pueden estar vacíos: \n" +
                            "Código \n Título \n Artista \n Fecha \n Precio \n"
                            + vista.tiradaTecnicaLbl.getText());
                    break;
                }
                if (modelo.existeCodigo(vista.codigotxt.getText())) {
                    Util.mensajeError("Ya existe una obra con ese código \n" +
                            vista.codigotxt.getText());
                    break;
                }
                try {
                    if (vista.lienzoRadioButton.isSelected()) {
                        modelo.altaLienzo(vista.codigotxt.getText(),
                                vista.titulotxt.getText(),
                                vista.artistatxt.getText(),
                                vista.fechaDPicker.getDate(),
                                Double.parseDouble(vista.preciotxt.getText()),
                                vista.tecnicatxt.getText());
                    } else {
                        modelo.altaSerigrafia(vista.codigotxt.getText(),
                                vista.titulotxt.getText(),
                                vista.artistatxt.getText(),
                                vista.fechaDPicker.getDate(),
                                Double.parseDouble(vista.preciotxt.getText()),
                                Integer.parseInt(vista.tecnicatxt.getText()));
                    }
                    limpiarCampos();
                    refrescarCampos();
                } catch (NumberFormatException e1) {
                    Util.mensajeError("El precio y la tirada deben ser números");
                }
                break;
            case "Importar":
                JFileChooser selectorFichero = Util.crearSelectorFichero(ultimaRutaExportada,
                        "Archivos XML", "xml");
                int opcion = selectorFichero.showOpenDialog(null);
                if (opcion == JFileChooser.APPROVE_OPTION) {
                    try {
                        modelo.importarXML(selectorFichero.getSelectedFile());
                        refrescarCampos();
                    } catch (ParserConfigurationException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (SAXException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case "Exportar":
                JFileChooser selectorFichero2 = Util.crearSelectorFichero(ultimaRutaExportada,
                        "Archivos XML", "xml");
                int opcion2 = selectorFichero2.showSaveDialog(null);
                if (opcion2 == JFileChooser.APPROVE_OPTION) {
                    File fichero = selectorFichero2.getSelectedFile();
                    if (!fichero.getName().toLowerCase().endsWith(".xml")) {
                        fichero = new File(fichero.getAbsolutePath() + ".xml");
                    }
                    try {
                        modelo.exportar(fichero);
                        actualizarDatosConfiguracion(fichero);
                    } catch (ParserConfigurationException ex) {
                        ex.printStackTrace();
                    } catch (TransformerException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            case "Eliminar":
                ObraUrbana obraEliminar = (ObraUrbana) vista.list1.getSelectedValue();
                if (obraEliminar == null) {
                    Util.mensajeError("Selecciona primero una obra de la lista");
                    break;
                }
                int respuesta = Util.mensajeConfirmacion("¿Desea eliminar la obra "
                        + obraEliminar.getCodigo() + "?", "Eliminar");
                if (respuesta == JOptionPane.YES_OPTION) {
                    modelo.eliminarObra(obraEliminar);
                    limpiarCampos();
                    refrescarCampos();
                }
                break;
            case "Lienzo":
                vista.tiradaTecnicaLbl.setText("Técnica");
                break;
            case "Serigrafía":
                vista.tiradaTecnicaLbl.setText("Tirada");
                break;
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting() && vista.list1.getSelectedValue() != null) {
            ObraUrbana obraSeleccionada = (ObraUrbana) vista.list1.getSelectedValue();
            vista.codigotxt.setText(obraSeleccionada.getCodigo());
            vista.titulotxt.setText(obraSeleccionada.getTitulo());
            vista.artistatxt.setText(obraSeleccionada.getArtista());
            vista.fechaDPicker.setDate(obraSeleccionada.getFecha());
            vista.preciotxt.setText(String.valueOf(obraSeleccionada.getPrecio()));
            if (obraSeleccionada instanceof Lienzo) {
                vista.tecnicatxt.setText(((Lienzo) obraSeleccionada).getTecnica());
                vista.lienzoRadioButton.doClick();
            } else {
                vista.tecnicatxt.setText(String.valueOf(((Serigrafia) obraSeleccionada).getTirada()));
                vista.serigrafiaRadioButton.doClick();
            }
        }
    }

    @Override
    public void windowClosing(WindowEvent e) {
        int resp = Util.mensajeConfirmacion("¿Desea cerrar la ventana?", "Salir");
        if (resp == JOptionPane.YES_OPTION) {
            try {
                guardarConfiguracion();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.exit(0);
        }
    }

    //no los uso
    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

}
