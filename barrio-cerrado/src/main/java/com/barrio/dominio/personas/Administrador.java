package com.barrio.dominio.personas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Administrador del barrio, responsable de gestionar reclamos y solicitudes.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Administrador extends Persona {

    private String legajo;
}
