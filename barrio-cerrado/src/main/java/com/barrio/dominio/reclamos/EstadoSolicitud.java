package com.barrio.dominio.reclamos;

public interface EstadoSolicitud {

    boolean puedeTransicionarA(EstadoSolicitud destino);

    String getNombre();
}
