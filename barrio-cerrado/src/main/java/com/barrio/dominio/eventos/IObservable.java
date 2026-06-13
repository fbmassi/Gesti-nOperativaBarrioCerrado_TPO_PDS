package com.barrio.dominio.eventos;

public interface IObservable {

    void agregarObservador(Observador o);

    void quitarObservador(Observador o);

    void notificarObservadores();
}
