package com.barrio.dominio.reclamos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Pendiente implements EstadoSolicitud {

    @Override
    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        // Pendiente puede pasar a En Proceso o cerrarse directamente.
        return destino instanceof EnProceso || destino instanceof Cerrado;
    }

    @Override
    public String getNombre() {
        return "PENDIENTE";
    }
}
