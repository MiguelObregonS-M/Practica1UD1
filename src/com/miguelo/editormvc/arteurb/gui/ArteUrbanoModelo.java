package com.miguelo.editormvc.arteurb.gui;

import com.miguelo.editormvc.arteurb.base.Lienzo;
import com.miguelo.editormvc.arteurb.base.ObraUrbana;
import com.miguelo.editormvc.arteurb.base.Serigrafia;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Guarda la lista de obras y lee y escribe el XML
 * Parte lógica del programa
 */
public class ArteUrbanoModelo {
    public ArrayList<ObraUrbana> listaObras;

    public ArteUrbanoModelo() {
        listaObras = new ArrayList<ObraUrbana>();
    }

    public ArrayList<ObraUrbana> obtenerListaObras() {
        return listaObras;
    }

    //altaLienzo
    public void altaLienzo(String codigo, String titulo, String artista,
                           LocalDate fecha, double precio, String tecnica) {
        Lienzo nuevoLienzo = new Lienzo(codigo, titulo, artista, fecha, precio, tecnica);
        listaObras.add(nuevoLienzo);
    }

    //altaSerigrafia
    public void altaSerigrafia(String codigo, String titulo, String artista,
                               LocalDate fecha, double precio, int tirada) {
        Serigrafia nuevaSerigrafia = new Serigrafia(codigo, titulo, artista, fecha, precio, tirada);
        listaObras.add(nuevaSerigrafia);
    }

    //existeCodigo
    /**
     * Comprueba si ya existe una obra con ese código.
     * @param codigo código a buscar
     * @return true si existe, false si no
     */
    public boolean existeCodigo(String codigo) {
        for (ObraUrbana unaObra : listaObras) {
            if (unaObra.getCodigo().equals(codigo)) {
               return true;
                }
            }
            return false;
        }

    //eliminarObra
    public void eliminarObra(ObraUrbana obra) {
        listaObras.remove(obra);
    }

    //exportar
    /**
     * Guarda todas las obras en un fichero XML.
     * @param fichero fichero de destino
     * @throws ParserConfigurationException si falla la creación del documento
     * @throws TransformerException si falla la escritura del fichero
     */
    public void exportar(File fichero) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation dom = builder.getDOMImplementation();
        Document documento = dom.createDocument(null, "xml", null);

        //añado el nodo raiz Obras
        Element raiz = documento.createElement("Obras");
        documento.getDocumentElement().appendChild(raiz);

        //otros elementos
        Element nodoObra = null;
        Element nodoDatos = null;
        Text texto = null;

        for (ObraUrbana unaObra : listaObras) {
            //añado dentro de la etiqueta raiz Obras (lienzo o serigrafia)
            if (unaObra instanceof Lienzo) {
                nodoObra = documento.createElement("Lienzo");
            } else {
                nodoObra = documento.createElement("Serigrafia");
            }
            raiz.appendChild(nodoObra);

            //dentro de cada lienzo, serigrafia tengo que poner las etiquetas codigo, titulo,...
            nodoDatos = documento.createElement("codigo");
            nodoObra.appendChild(nodoDatos);

            texto = documento.createTextNode(unaObra.getCodigo());
            nodoDatos.appendChild(texto);

            nodoDatos = documento.createElement("titulo");
            nodoObra.appendChild(nodoDatos);

            texto = documento.createTextNode(unaObra.getTitulo());
            nodoDatos.appendChild(texto);

            nodoDatos = documento.createElement("artista");
            nodoObra.appendChild(nodoDatos);

            texto = documento.createTextNode(unaObra.getArtista());
            nodoDatos.appendChild(texto);

            nodoDatos = documento.createElement("fecha");
            nodoObra.appendChild(nodoDatos);

            texto = documento.createTextNode(unaObra.getFecha().toString());
            nodoDatos.appendChild(texto);

            nodoDatos = documento.createElement("precio");
            nodoObra.appendChild(nodoDatos);

            texto = documento.createTextNode(String.valueOf(unaObra.getPrecio()));
            nodoDatos.appendChild(texto);

            //nos falta el dato diferente (tecnica, tirada)
            if (unaObra instanceof Lienzo) {
                nodoDatos = documento.createElement("tecnica");
                nodoObra.appendChild(nodoDatos);

                texto = documento.createTextNode(((Lienzo) unaObra).getTecnica());
                nodoDatos.appendChild(texto);
            } else {
                nodoDatos = documento.createElement("tirada");
                nodoObra.appendChild(nodoDatos);

                texto = documento.createTextNode(String.valueOf(((Serigrafia) unaObra).getTirada()));
                nodoDatos.appendChild(texto);
            }

        }
        //guardo en fichero
        Source source = new DOMSource(documento);
        Result resultado = new StreamResult(fichero);

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, resultado);
    }

    //importar
    /**
     * Carga todas las obras de un fichero XML.
     * @param fichero fichero que quiero importar
     * @throws ParserConfigurationException si falla la creación del documento
     */
    public void importarXML(File fichero) throws ParserConfigurationException, IOException, SAXException {
        listaObras = new ArrayList<ObraUrbana>();
        Lienzo nuevoLienzo = null;
        Serigrafia nuevaSerigrafia = null;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document documento = builder.parse(fichero);

        NodeList listaElementos = documento.getElementsByTagName("*");

        for (int i = 0; i < listaElementos.getLength(); i++) {
            Element nodoObra = (Element) listaElementos.item(i);
            if (nodoObra.getTagName().equals("Lienzo")) {
                nuevoLienzo = new Lienzo();
                nuevoLienzo.setCodigo(nodoObra.getChildNodes().item(0).getTextContent());
                nuevoLienzo.setTitulo(nodoObra.getChildNodes().item(1).getTextContent());
                nuevoLienzo.setArtista(nodoObra.getChildNodes().item(2).getTextContent());
                nuevoLienzo.setFecha(LocalDate.parse(nodoObra.getChildNodes().item(3).getTextContent()));
                nuevoLienzo.setPrecio(Double.parseDouble(nodoObra.getChildNodes().item(4).getTextContent()));
                nuevoLienzo.setTecnica(nodoObra.getChildNodes().item(5).getTextContent());
                listaObras.add(nuevoLienzo);
            } else {
                if (nodoObra.getTagName().equals("Serigrafia")) {
                    nuevaSerigrafia = new Serigrafia();
                    nuevaSerigrafia.setCodigo(nodoObra.getChildNodes().item(0).getTextContent());
                    nuevaSerigrafia.setTitulo(nodoObra.getChildNodes().item(1).getTextContent());
                    nuevaSerigrafia.setArtista(nodoObra.getChildNodes().item(2).getTextContent());
                    nuevaSerigrafia.setFecha(LocalDate.parse(nodoObra.getChildNodes().item(3).getTextContent()));
                    nuevaSerigrafia.setPrecio(Double.parseDouble(nodoObra.getChildNodes().item(4).getTextContent()));
                    nuevaSerigrafia.setTirada(Integer.parseInt(nodoObra.getChildNodes().item(5).getTextContent()));
                    listaObras.add(nuevaSerigrafia);
                }
            }

        }

    }

}
