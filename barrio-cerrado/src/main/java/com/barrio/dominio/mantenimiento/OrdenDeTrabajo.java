package com.barrio.dominio.mantenimiento;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Proveedor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrdenDeTrabajo {

    private Long id;
    private Administrador administrador;
    private Proveedor proveedor;
    private List<TareaDeMantenimiento> tareas;
    private LocalDateTime fechaEmision;

    public void agregarTarea(TareaDeMantenimiento t) {
    }
}
