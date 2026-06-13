package com.barrio.dominio.notificaciones;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CanalEmail implements EstrategiaNotificacion {

    @Override
    public void enviar(Notificacion n) {
    }
}
