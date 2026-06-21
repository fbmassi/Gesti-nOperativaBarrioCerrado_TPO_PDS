package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.trazabilidad.EntradaHistorial;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;

/**
 * Controlador de trazabilidad: expone el historial de acciones (CU-10) como líneas formateadas.
 */
public class ControladorHistorial {

    private final HistorialAcciones historial;

    public ControladorHistorial(HistorialAcciones historial) {
        this.historial = historial;
    }

    public List<String> listarAcciones() {
        List<String> lineas = new ArrayList<>();
        for (EntradaHistorial e : historial.obtenerAcciones()) {
            lineas.add(formatear(e));
        }
        return lineas;
    }

    public List<String> listarAccionesDe(String dni) {
        List<String> lineas = new ArrayList<>();
        for (EntradaHistorial e : historial.obtenerAcciones()) {
            if (e.getAutor() != null && dni != null && dni.equals(e.getAutor().getDni())) {
                lineas.add(formatear(e));
            }
        }
        return lineas;
    }

    private String formatear(EntradaHistorial e) {
        String autor = e.getAutor() != null ? e.getAutor().getNombreCompleto() : "Sistema";
        return e.getFecha() + " | " + autor + " | " + e.getDescripcion();
    }
}
