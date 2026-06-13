package com.barrio.dominio.mantenimiento;

import com.barrio.dominio.personas.Proveedor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TareaSimple extends TareaDeMantenimiento {

    private Proveedor personalAsignado;

    @Override
    public void cambiarEstado(EstadoTarea nuevoEstado) {
    }

    public void asignarPersonal(Proveedor p) {
    }
}
