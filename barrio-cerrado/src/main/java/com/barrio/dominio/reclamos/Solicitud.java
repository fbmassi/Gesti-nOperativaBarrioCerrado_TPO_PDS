package com.barrio.dominio.reclamos;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.dominio.categorias.CategoriaSolicitud;
import com.barrio.dominio.categorias.PrioridadSolicitud;
import com.barrio.dominio.eventos.IObservable;
import com.barrio.dominio.eventos.Observador;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class Solicitud implements IObservable {

    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaCreacion;
    private EstadoSolicitud estado;
    private CategoriaSolicitud categoria;
    private PrioridadSolicitud prioridad;
    private List<Observador> observadores;

    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
    }

    @Override
    public void agregarObservador(Observador o) {
    }

    @Override
    public void quitarObservador(Observador o) {
    }

    @Override
    public void notificarObservadores() {
    }
}
