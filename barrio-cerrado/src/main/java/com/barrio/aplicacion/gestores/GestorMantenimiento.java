package com.barrio.aplicacion.gestores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.mantenimiento.EstadoTarea;
import com.barrio.dominio.mantenimiento.TareaCompuesta;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.mantenimiento.TareaSimple;
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

    /**
     * Crea una tarea como TareaCompuesta (Composite) para que pueda contener subtareas.
     */
    public TareaDeMantenimiento registrarTarea(String titulo, String descripcion) {
        TareaCompuesta tarea = new TareaCompuesta(new ArrayList<>());
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstado(EstadoTarea.PENDIENTE);
        tarea.setFechaCreacion(LocalDateTime.now());

        if (notificador != null) {
            tarea.agregarObservador(notificador);
        }

        repositorio.guardar(tarea);
        if (historial != null) {
            historial.registrarAccion(null, "Nueva tarea: " + titulo);
        }
        return tarea;
    }

    /**
     * Crea una tarea simple (hoja, sin subtareas) asignada opcionalmente a un proveedor.
     */
    public TareaSimple registrarTareaSimple(String titulo, String descripcion, Proveedor proveedor) {
        TareaSimple tarea = new TareaSimple(proveedor);
        tarea.setTitulo(titulo);
        tarea.setDescripcion(descripcion);
        tarea.setEstado(EstadoTarea.PENDIENTE);
        tarea.setFechaCreacion(LocalDateTime.now());

        if (notificador != null) {
            tarea.agregarObservador(notificador);
        }

        repositorio.guardar(tarea);
        if (historial != null) {
            String asignado = proveedor != null ? " (asignada a " + proveedor.getNombreCompleto() + ")" : "";
            historial.registrarAccion(null, "Nueva tarea simple: " + titulo + asignado);
        }
        return tarea;
    }

    public void registrarSubtarea(Long idTareaPadre, String titulo, String descripcion) {
        registrarSubtarea(idTareaPadre, titulo, descripcion, null);
    }

    /**
     * Agrega una subtarea (hoja) a una tarea compuesta, asignándole opcionalmente un proveedor
     * en el momento de la creación.
     */
    public void registrarSubtarea(Long idTareaPadre, String titulo, String descripcion, Proveedor proveedor) {
        TareaDeMantenimiento padre = repositorio.buscarPorId(idTareaPadre);
        if (padre == null) {
            throw new IllegalArgumentException("Tarea padre no encontrada: " + idTareaPadre);
        }
        if (!(padre instanceof TareaCompuesta)) {
            throw new IllegalStateException("La tarea " + idTareaPadre + " no admite subtareas");
        }
        TareaSimple subtarea = new TareaSimple(proveedor);
        subtarea.setTitulo(titulo);
        subtarea.setDescripcion(descripcion);
        subtarea.setEstado(EstadoTarea.PENDIENTE);
        subtarea.setFechaCreacion(LocalDateTime.now());

        // Composite: la subtarea se agrega al árbol del padre.
        ((TareaCompuesta) padre).agregarSubtarea(subtarea);
        repositorio.guardar(padre);
        if (historial != null) {
            String asignado = proveedor != null ? " (asignada a " + proveedor.getNombreCompleto() + ")" : "";
            historial.registrarAccion(null, "Subtarea agregada a " + idTareaPadre + ": " + titulo + asignado);
        }
    }

    public void asignarPersonal(Long idTarea, Proveedor proveedor) {
        TareaDeMantenimiento tarea = repositorio.buscarPorId(idTarea);
        if (tarea == null) {
            throw new IllegalArgumentException("Tarea no encontrada: " + idTarea);
        }
        // Composite: si es simple, se asigna a ella; si es compuesta, se asigna a todas sus hojas.
        int asignadas = asignarRecursivo(tarea, proveedor);
        if (asignadas == 0) {
            throw new IllegalStateException("La tarea " + idTarea
                    + " no tiene partes asignables (una tarea compuesta necesita subtareas)");
        }
        repositorio.guardar(tarea);
        if (historial != null) {
            historial.registrarAccion(null, "Personal asignado a la tarea " + idTarea
                    + " (" + asignadas + " subtarea/s)");
        }
    }

    /** Asigna el proveedor a la tarea simple, o a todas las subtareas hoja si es compuesta. */
    private int asignarRecursivo(TareaDeMantenimiento tarea, Proveedor proveedor) {
        if (tarea instanceof TareaSimple) {
            ((TareaSimple) tarea).asignarPersonal(proveedor);
            return 1;
        }
        int total = 0;
        if (tarea instanceof TareaCompuesta) {
            List<TareaDeMantenimiento> subs = ((TareaCompuesta) tarea).getSubtareas();
            if (subs != null) {
                for (TareaDeMantenimiento sub : subs) {
                    total += asignarRecursivo(sub, proveedor);
                }
            }
        }
        return total;
    }

    public void cambiarEstado(Long idTarea, EstadoTarea nuevoEstado) {
        TareaDeMantenimiento tarea = repositorio.buscarPorId(idTarea);
        if (tarea == null) {
            throw new IllegalArgumentException("Tarea no encontrada: " + idTarea);
        }
        // Suscribe el notificador a toda la jerarquía, para que cada subtarea
        // avise a su propio proveedor cuando el cambio se propague (Composite + Observer).
        if (notificador != null) {
            suscribir(tarea, notificador);
        }
        // Composite: si es compuesta, propaga el estado a sus subtareas.
        tarea.cambiarEstado(nuevoEstado);
        repositorio.guardar(tarea);
        if (historial != null) {
            historial.registrarAccion(null, "Tarea " + idTarea + " -> " + nuevoEstado);
        }
    }

    public List<TareaDeMantenimiento> listarTareas() {
        return repositorio.buscarTodos();
    }

    /** Suscribe el observador a la tarea y, si es compuesta, a todas sus subtareas (recursivo). */
    private void suscribir(TareaDeMantenimiento tarea, Observador observador) {
        tarea.agregarObservador(observador);
        if (tarea instanceof TareaCompuesta) {
            List<TareaDeMantenimiento> subs = ((TareaCompuesta) tarea).getSubtareas();
            if (subs != null) {
                for (TareaDeMantenimiento sub : subs) {
                    suscribir(sub, observador);
                }
            }
        }
    }
}
