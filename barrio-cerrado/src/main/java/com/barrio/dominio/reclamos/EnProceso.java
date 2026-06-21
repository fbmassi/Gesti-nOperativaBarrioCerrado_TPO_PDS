package com.barrio.dominio.reclamos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EnProceso implements EstadoSolicitud {

    @Override
    public boolean puedeTransicionarA(EstadoSolicitud destino) {
        // En Proceso puede resolverse o cerrarse.
        return destino instanceof Resuelto || destino instanceof Cerrado;
    }

    @Override
    public String getNombre() {
        return "EN_PROCESO";
    }
}
