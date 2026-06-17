package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Visitante;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProtocoloVisitante implements ProtocoloAcceso {
    @Override
    public boolean validar(Persona actor) {
        // Solo se admite un visitante con un residente que lo autorice.
        return actor instanceof Visitante
                && ((Visitante) actor).getResidenteAutorizante() != null;
    }
}
