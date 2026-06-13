package com.barrio.dominio.reclamos;

import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Residente;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Reclamo realizado por un residente y gestionado por un administrador.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reclamo extends Solicitud {
    private Residente residente;
    private Administrador administrador;
}
