package com.barrio.dominio.eventos;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Evento del barrio que será comunicado a los observadores.
 */
@Data
public class Evento {

    private Long id;
    private String descripcion;
    private LocalDateTime fecha;
}
