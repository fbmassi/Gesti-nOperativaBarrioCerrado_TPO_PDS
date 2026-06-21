package com.barrio.dominio.personas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Proveedor externo que realiza tareas de mantenimiento en el barrio.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Proveedor extends Persona {
    private EspecialidadProveedor especialidad;
}
