package com.barrio.aplicacion.gestores;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.categorias.CategoriaSolicitud;
import com.barrio.dominio.categorias.PrioridadSolicitud;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.reclamos.EstadoSolicitud;
import com.barrio.dominio.reclamos.IncidenteSeguridad;
import com.barrio.dominio.reclamos.Pendiente;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.dominio.reclamos.Solicitud;
import com.barrio.infraestructura.persistencia.RepositorioSolicitudes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorReclamos {

    private RepositorioSolicitudes repositorio;
    private HistorialAcciones historial;
    private Observador notificador;

    public Reclamo registrarReclamo(Residente residente, String titulo, String descripcion,
                                    CategoriaSolicitud categoria, PrioridadSolicitud prioridad) {
        Reclamo reclamo = new Reclamo(residente, null);
        reclamo.setTitulo(titulo);
        reclamo.setDescripcion(descripcion);
        reclamo.setFechaCreacion(LocalDateTime.now());
        reclamo.setEstado(new Pendiente());
        reclamo.setCategoria(categoria);
        reclamo.setPrioridad(prioridad);

        // Observer: el notificador queda suscripto a los cambios de estado del reclamo.
        if (notificador != null) {
            reclamo.agregarObservador(notificador);
        }

        repositorio.guardar(reclamo);
        if (historial != null) {
            historial.registrarAccion(residente, "Nuevo reclamo: " + titulo);
        }
        return reclamo;
    }

    public IncidenteSeguridad registrarIncidente(Guardia guardia, String titulo, String descripcion,
                                                 CategoriaSolicitud categoria, PrioridadSolicitud prioridad,
                                                 boolean urgencia) {
        IncidenteSeguridad incidente = new IncidenteSeguridad(guardia, urgencia);
        incidente.setTitulo(titulo);
        incidente.setDescripcion(descripcion);
        incidente.setFechaCreacion(LocalDateTime.now());
        incidente.setEstado(new Pendiente());
        incidente.setCategoria(categoria);
        incidente.setPrioridad(prioridad);

        if (notificador != null) {
            incidente.agregarObservador(notificador);
        }

        repositorio.guardar(incidente);
        // Un incidente se notifica a los administradores apenas el guardia lo reporta (Observer).
        if (notificador != null) {
            notificador.actualizar(incidente, "Nuevo incidente de seguridad reportado: " + titulo
                    + (urgencia ? " (URGENTE)" : ""));
        }
        if (historial != null) {
            historial.registrarAccion(guardia, "Nuevo incidente de seguridad: " + titulo);
        }
        return incidente;
    }

    public List<IncidenteSeguridad> listarIncidentes() {
        List<IncidenteSeguridad> incidentes = new ArrayList<>();
        for (Solicitud s : repositorio.buscarTodos()) {
            if (s instanceof IncidenteSeguridad) {
                incidentes.add((IncidenteSeguridad) s);
            }
        }
        return incidentes;
    }

    public void cambiarEstado(Long idReclamo, EstadoSolicitud nuevoEstado) {
        Solicitud solicitud = repositorio.buscarPorId(idReclamo);
        if (solicitud == null) {
            throw new IllegalArgumentException("Reclamo no encontrado: " + idReclamo);
        }
        // Los observadores no se persisten: se re-suscribe el notificador antes de notificar.
        if (notificador != null) {
            solicitud.agregarObservador(notificador);
        }
        // Patrón State: valida la transición y notifica (Observer) si corresponde.
        solicitud.cambiarEstado(nuevoEstado);
        repositorio.guardar(solicitud);
        if (historial != null) {
            historial.registrarAccion(null, "Reclamo " + idReclamo + " -> " + nuevoEstado.getNombre());
        }
    }

    public List<Reclamo> listarReclamos() {
        List<Reclamo> reclamos = new ArrayList<>();
        for (Solicitud s : repositorio.buscarTodos()) {
            if (s instanceof Reclamo) {
                reclamos.add((Reclamo) s);
            }
        }
        return reclamos;
    }
}
