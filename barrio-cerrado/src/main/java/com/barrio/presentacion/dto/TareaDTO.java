package com.barrio.presentacion.dto;

import lombok.Data;

/**
 * DTO de tareas de mantenimiento.
 */
@Data
public class TareaDTO {

    private Long id;
    private String titulo;
    private String descripcion;
    private String estado;
    private String tipo;
}
