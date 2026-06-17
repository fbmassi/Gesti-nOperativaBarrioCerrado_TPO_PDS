package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;

/**
 * Protocolo para familiares (y residentes): se permite si tiene el acceso autorizado.
 * Los residentes lo tienen siempre.
 */
public class ProtocoloFamiliar implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        return actor != null && actor.isAccesoAutorizado();
    }
}
