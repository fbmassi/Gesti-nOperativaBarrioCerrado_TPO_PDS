package com.barrio.dominio.reclamos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Resuelto implements EstadoSolicitud {

    @Override
    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        // Resuelto puede cerrarse o reabrirse (volver a En Proceso).
        return destino instanceof Cerrado || destino instanceof EnProceso;
    }

    @Override
    public String getNombre() {
        return "RESUELTO";
    }
}
