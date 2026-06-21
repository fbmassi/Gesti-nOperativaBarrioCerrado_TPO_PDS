package com.barrio.dominio.mantenimiento;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
public abstract class TareaDeMantenimiento implements IObservable {

    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoTarea estado;
    private LocalDateTime fechaCreacion;
    private List<Observador> observadores;

    public void cambiarEstado(EstadoTarea nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo");
        }
        this.estado = nuevoEstado;
        notificarObservadores();
    }

    public EstadoTarea getEstado() {
        return estado;
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
        String mensaje = "Tarea '" + titulo + "' cambió a estado " + getEstado();
        for (Observador o : observadores) {
            o.actualizar(this, mensaje);
        }
    }
}
