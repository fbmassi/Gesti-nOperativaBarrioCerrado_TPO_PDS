package com.barrio.aplicacion.trazabilidad;

import java.time.LocalDateTime;

import com.barrio.dominio.personas.Persona;
import lombok.Data;

/**
 * Entrada registrada en el historial de trazabilidad.
 */
@Data
public class EntradaHistorial {

    private Long id;
    private LocalDateTime fecha;
    private Persona autor;
    private String descripcion;
}
