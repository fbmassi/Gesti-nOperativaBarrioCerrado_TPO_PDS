package com.barrio.presentacion.dto;

import lombok.Data;

@Data
public class SolicitudDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String categoria;
    private String prioridad;
    private String dniResidente;
    // Sólo para incidentes de seguridad (los reporta un guardia)
    private String dniGuardia;
    private boolean urgencia;
}
