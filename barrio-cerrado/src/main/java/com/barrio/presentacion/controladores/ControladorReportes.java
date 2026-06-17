package com.barrio.presentacion.controladores;

import com.barrio.aplicacion.gestores.GestorReportes;
import com.barrio.dominio.reportes.Reporte;
import com.barrio.presentacion.dto.ReporteDTO;

public class ControladorReportes {

    private final GestorReportes gestor;

    public ControladorReportes(GestorReportes gestor) {
        this.gestor = gestor;
    }

    public ReporteDTO generarReporte(String tipo) {
        Reporte reporte = gestor.generarReporte(tipo);
        ReporteDTO dto = new ReporteDTO();
        dto.setTipo(tipo);
        dto.setContenido(reporte.generar());
        dto.setFechaGeneracion(reporte.getFechaGeneracion() != null
                ? reporte.getFechaGeneracion().toString() : null);
        return dto;
    }
}
