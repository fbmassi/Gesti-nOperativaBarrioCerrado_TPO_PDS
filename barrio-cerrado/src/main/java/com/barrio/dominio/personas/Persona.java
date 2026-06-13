package com.barrio.dominio.personas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase base abstracta para todas las personas del barrio.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class Persona {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
