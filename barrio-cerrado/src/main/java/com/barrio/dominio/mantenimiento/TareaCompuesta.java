package com.barrio.dominio.mantenimiento;

import java.util.ArrayList;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TareaCompuesta extends TareaDeMantenimiento {

    private List<TareaDeMantenimiento> subtareas;

    public void agregarSubtarea(TareaDeMantenimiento t) {
        if (subtareas == null) {
            subtareas = new ArrayList<>();
        }
        if (t != null && !subtareas.contains(t)) {
            subtareas.add(t);
        }
    }

    public void quitarSubtarea(TareaDeMantenimiento t) {
        if (subtareas != null) {
            subtareas.remove(t);
        }
    }

    /**
     * Composite: propaga el cambio de estado a todas las subtareas además de a sí misma.
     */
    @Override
    public void cambiarEstado(EstadoTarea nuevoEstado) {
        if (subtareas != null) {
            for (TareaDeMantenimiento sub : subtareas) {
                sub.cambiarEstado(nuevoEstado);
            }
        }
        super.cambiarEstado(nuevoEstado);
    }

    /**
     * Composite: el estado se deriva de las subtareas.
     * Todas finalizadas -> FINALIZADA; alguna en proceso -> EN_PROCESO; si no -> PENDIENTE.
     */
    @Override
    public EstadoTarea getEstado() {
        if (subtareas == null || subtareas.isEmpty()) {
            return super.getEstado();
        }
        boolean todasFinalizadas = true;
        boolean algunaEnProceso = false;
        for (TareaDeMantenimiento sub : subtareas) {
            EstadoTarea e = sub.getEstado();
            if (e != EstadoTarea.FINALIZADA) {
                todasFinalizadas = false;
            }
            if (e == EstadoTarea.EN_PROCESO) {
                algunaEnProceso = true;
            }
        }
        if (todasFinalizadas) {
            return EstadoTarea.FINALIZADA;
        }
        return algunaEnProceso ? EstadoTarea.EN_PROCESO : EstadoTarea.PENDIENTE;
    }
}
