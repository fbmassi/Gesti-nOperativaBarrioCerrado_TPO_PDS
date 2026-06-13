package com.barrio.dominio.eventos;

public interface Observador {

    void actualizar(IObservable origen, String mensaje);
}
