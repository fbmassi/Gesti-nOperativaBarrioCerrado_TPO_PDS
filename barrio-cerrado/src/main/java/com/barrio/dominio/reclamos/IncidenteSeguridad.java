package com.barrio.dominio.reclamos;

import com.barrio.dominio.personas.Guardia;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Incidente de seguridad reportado por un guardia.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class IncidenteSeguridad extends Solicitud {

    private Guardia guardia;
    private boolean urgencia;
}
