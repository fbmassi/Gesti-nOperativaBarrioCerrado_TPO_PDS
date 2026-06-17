package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Residente;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProtocoloFamiliar implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        // El protocolo familiar solo admite residentes del barrio.
        return actor instanceof Residente;
    }
}
