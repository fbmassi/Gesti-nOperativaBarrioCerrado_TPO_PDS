package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProtocoloEmergencia implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        // El guardia dio de alta la emergencia (acceso vigente); se permite el ingreso.
        return actor != null && actor.isAccesoAutorizado();
    }
}
