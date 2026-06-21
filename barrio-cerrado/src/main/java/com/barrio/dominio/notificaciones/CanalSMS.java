package com.barrio.dominio.notificaciones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CanalSMS implements EstrategiaNotificacion {

    @Override
    public void enviar(Notificacion n) {
        String destino = (n.getDestinatario() != null)
                ? n.getDestinatario().getNombreCompleto() : "desconocido";
        System.out.println("[SMS] Para: " + destino + " | " + n.getMensaje());
    }
}
