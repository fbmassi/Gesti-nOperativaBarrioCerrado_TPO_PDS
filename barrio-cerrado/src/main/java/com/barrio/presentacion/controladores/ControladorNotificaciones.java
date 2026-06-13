package com.barrio.presentacion.controladores;

import com.barrio.aplicacion.gestores.GestorNotificaciones;
import com.barrio.presentacion.dto.NotificacionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorNotificaciones {

    private GestorNotificaciones gestor;

    public NotificacionDTO enviarNotificacion(NotificacionDTO notificacion) {
        return null;
    }
}
