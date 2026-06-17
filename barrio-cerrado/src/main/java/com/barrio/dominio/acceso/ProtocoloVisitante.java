package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;

/**
 * Protocolo para visitantes: se permite si el admin autorizó su ingreso (acceso vigente).
 */
public class ProtocoloVisitante implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        return actor != null && actor.isAccesoAutorizado();
    }
}
