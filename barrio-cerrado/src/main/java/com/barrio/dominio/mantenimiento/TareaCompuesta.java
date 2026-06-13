package com.barrio.dominio.mantenimiento;

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
    }

    public void quitarSubtarea(TareaDeMantenimiento t) {
    }

    @Override
    public void cambiarEstado(EstadoTarea nuevoEstado) {
    }

    @Override
    public EstadoTarea getEstado() {
        return super.getEstado();
    }
}
