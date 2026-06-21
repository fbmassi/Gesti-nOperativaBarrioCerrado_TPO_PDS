package com.barrio.dominio.reclamos;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    /**
     * Patrón State: solo cambia de estado si la transición es válida; luego notifica (Observer).
     */
    public void cambiarEstado(EstadoSolicitud nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        if (estado != null && !estado.puedeTransicionarA(nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición no permitida: " + estado.getNombre() + " -> " + nuevoEstado.getNombre());
        }
        this.estado = nuevoEstado;
        notificarObservadores();
    }

    @Override
    public void agregarObservador(Observador o) {
        if (observadores == null) {
            observadores = new ArrayList<>();
        }
        if (o != null && !observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void quitarObservador(Observador o) {
        if (observadores != null) {
            observadores.remove(o);
        }
    }

    @Override
    public void notificarObservadores() {
        if (observadores == null) {
            return;
        }
        String mensaje = "Solicitud '" + titulo + "' cambió a estado "
                + (estado != null ? estado.getNombre() : "?");
        for (Observador o : observadores) {
            o.actualizar(this, mensaje);
        }
    }
}
