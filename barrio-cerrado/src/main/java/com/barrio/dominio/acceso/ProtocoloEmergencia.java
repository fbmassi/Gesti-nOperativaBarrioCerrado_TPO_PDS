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
        // El acceso de emergencia siempre se permite, sin importar el tipo de actor.
        return true;
    }
}
