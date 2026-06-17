package com.barrio.presentacion.dto;

import lombok.Data;

/**
 * DTO de personas del barrio.
 */
@Data
public class PersonaDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;
    private String tipo;
    private int numeroLote;
    private String especialidad;
    private String legajo;
    private String canalNotificacion;
    private boolean accesoAutorizado;
}
