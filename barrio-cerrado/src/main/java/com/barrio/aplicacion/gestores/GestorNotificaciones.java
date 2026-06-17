package com.barrio.aplicacion.gestores;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.eventos.IObservable;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.notificaciones.CanalEmail;
import com.barrio.dominio.notificaciones.CanalSMS;
import com.barrio.dominio.notificaciones.EstrategiaNotificacion;
import com.barrio.dominio.notificaciones.Notificacion;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.reclamos.IncidenteSeguridad;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.infraestructura.persistencia.RepositorioNotificaciones;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorNotificaciones implements Observador {

    private RepositorioNotificaciones repositorio;
    private RepositorioPersonas repositorioPersonas;
    private HistorialAcciones historial;
    private EstrategiaNotificacion estrategia;

    /**
     * Observer: ante un evento del sujeto, notifica a los interesados según el origen:
     * - Reclamo            -> al residente dueño del reclamo
     * - IncidenteSeguridad -> a los administradores (el guardia es quien lo reporta)
     * - Tarea (cambia el proveedor el estado) -> a los administradores
     * Cada notificación se envía por la estrategia y se persiste.
     */
    @Override
    public void actualizar(IObservable origen, String mensaje) {
        for (Persona destinatario : resolverDestinatarios(origen)) {
            // Cada destinatario recibe la notificación por el canal que él eligió.
            EstrategiaNotificacion canal = canalDe(destinatario);
            Notificacion notificacion = new Notificacion();
            notificacion.setDestinatario(destinatario);
            notificacion.setMensaje(mensaje);
            notificacion.setCanal(canal);
            notificacion.enviar();
            if (repositorio != null && destinatario != null) {
                repositorio.guardar(notificacion);
            }
            if (historial != null) {
                historial.registrarAccion(destinatario, "Notificación: " + mensaje);
            }
        }
    }

    /** Determina los destinatarios de la notificación según el tipo de evento. */
    private List<Persona> resolverDestinatarios(IObservable origen) {
        if (origen instanceof Reclamo) {
            return unoSiNoEsNulo(((Reclamo) origen).getResidente());
        }
        if (origen instanceof IncidenteSeguridad) {
            // El guardia reporta el incidente; se notifica a los administradores.
            return administradores();
        }
        if (origen instanceof TareaDeMantenimiento) {
            // El proveedor cambia el estado; se notifica a los administradores.
            return administradores();
        }
        return Collections.emptyList();
    }

    /** Devuelve la estrategia de canal según la preferencia del destinatario (SMS o EMAIL). */
    private EstrategiaNotificacion canalDe(Persona destinatario) {
        if (destinatario != null && "SMS".equalsIgnoreCase(destinatario.getCanalNotificacion())) {
            return new CanalSMS();
        }
        return new CanalEmail();
    }

    private List<Persona> administradores() {
        List<Persona> admins = new ArrayList<>();
        if (repositorioPersonas != null) {
            for (Persona p : repositorioPersonas.buscarTodos()) {
                if (p instanceof Administrador) {
                    admins.add(p);
                }
            }
        }
        return admins;
    }

    private List<Persona> unoSiNoEsNulo(Persona p) {
        return p != null ? Collections.singletonList(p) : Collections.emptyList();
    }

    public Notificacion enviarNotificacion(Persona destinatario, String mensaje, EstrategiaNotificacion canal) {
        Notificacion notificacion = new Notificacion();
        notificacion.setDestinatario(destinatario);
        notificacion.setMensaje(mensaje);
        notificacion.setCanal(canal);

        // Strategy: el envío se delega en el canal indicado.
        notificacion.enviar();

        if (repositorio != null) {
            repositorio.guardar(notificacion);
        }
        if (historial != null) {
            historial.registrarAccion(destinatario, "Notificación enviada: " + mensaje);
        }
        return notificacion;
    }

    public void setEstrategia(EstrategiaNotificacion estrategia) {
        this.estrategia = estrategia;
    }

    public List<Notificacion> listarNotificaciones() {
        return repositorio.buscarTodos();
    }
}
