package com.barrio.dominio.reclamos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cerrado implements EstadoSolicitud {

    @Override
    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        return false;
    }

    @Override
    public String getNombre() {
        return "CERRADO";
    }
}
