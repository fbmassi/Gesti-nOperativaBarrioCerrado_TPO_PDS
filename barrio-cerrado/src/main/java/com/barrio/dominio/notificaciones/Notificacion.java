package com.barrio.dominio.notificaciones;

import java.time.LocalDateTime;

import com.barrio.dominio.personas.Persona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacion {

    private Long id;
    private Persona destinatario;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private EstrategiaNotificacion canal;

    /**
     * Patrón Strategy: delega el envío en el canal configurado.
     */
    public void enviar() {
        if (canal == null) {
            throw new IllegalStateException("La notificación no tiene un canal asignado");
        }
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        canal.enviar(this);
    }
}
