package com.barrio.aplicacion.gestores;

import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.mantenimiento.EstadoTarea;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.infraestructura.persistencia.RepositorioTareas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorMantenimiento {

    private RepositorioTareas repositorio;
    private HistorialAcciones historial;
    private Observador notificador;

    public TareaDeMantenimiento registrarTarea(String titulo, String descripcion) {
        return null;
    }

    public void registrarSubtarea(Long idTareaPadre, String titulo, String descripcion) {
    }

    public void asignarPersonal(Long idTarea, Proveedor proveedor) {
    }

    public void cambiarEstado(Long idTarea, EstadoTarea nuevoEstado) {
    }

    public List<TareaDeMantenimiento> listarTareas() {
        return null;
    }
}
