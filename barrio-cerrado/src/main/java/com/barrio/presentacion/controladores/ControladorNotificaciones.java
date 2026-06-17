package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorNotificaciones;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.notificaciones.CanalEmail;
import com.barrio.dominio.notificaciones.CanalSMS;
import com.barrio.dominio.notificaciones.EstrategiaNotificacion;
import com.barrio.dominio.notificaciones.Notificacion;
import com.barrio.dominio.personas.Persona;
import com.barrio.presentacion.dto.NotificacionDTO;

public class ControladorNotificaciones {

    private final GestorNotificaciones gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorNotificaciones(GestorNotificaciones gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    public NotificacionDTO enviarNotificacion(NotificacionDTO dto) {
        Persona destinatario = gestorPersonas.buscarPorDni(dto.getDniDestinatario());
        Notificacion n = gestor.enviarNotificacion(destinatario, dto.getMensaje(),
                canalDesde(dto.getCanal()));
        return toDTO(n);
    }

    public List<NotificacionDTO> listarNotificacionesDe(String dni) {
        List<NotificacionDTO> dtos = new ArrayList<>();
        for (Notificacion n : gestor.listarNotificaciones()) {
            if (n.getDestinatario() != null && dni != null
                    && dni.equals(n.getDestinatario().getDni())) {
                dtos.add(toDTO(n));
            }
        }
        return dtos;
    }

    private EstrategiaNotificacion canalDesde(String canal) {
        return "SMS".equalsIgnoreCase(canal) ? new CanalSMS() : new CanalEmail();
    }

    private String nombreCanal(EstrategiaNotificacion e) {
        if (e == null) {
            return "—";
        }
        return e.getClass().getSimpleName().replace("Canal", "").toUpperCase();
    }

    private NotificacionDTO toDTO(Notificacion n) {
        if (n == null) {
            return null;
        }
        NotificacionDTO dto = new NotificacionDTO();
        dto.setDniDestinatario(n.getDestinatario() != null ? n.getDestinatario().getDni() : null);
        dto.setMensaje(n.getMensaje());
        dto.setCanal(nombreCanal(n.getCanal()));
        return dto;
    }
}
