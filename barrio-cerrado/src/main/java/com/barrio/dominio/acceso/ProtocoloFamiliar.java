package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProtocoloFamiliar implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        return false;
    }
}
