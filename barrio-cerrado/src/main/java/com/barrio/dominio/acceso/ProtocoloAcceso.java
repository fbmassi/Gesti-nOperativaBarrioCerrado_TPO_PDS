package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;

public interface ProtocoloAcceso {

    boolean validar(Persona actor);
}
