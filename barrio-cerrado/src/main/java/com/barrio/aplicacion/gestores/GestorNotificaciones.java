package com.barrio.aplicacion.gestores;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.eventos.IObservable;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.notificaciones.EstrategiaNotificacion;
import com.barrio.dominio.notificaciones.Notificacion;
import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.persistencia.RepositorioNotificaciones;
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
    private HistorialAcciones historial;
    private EstrategiaNotificacion estrategia;

    @Override
    public void actualizar(IObservable origen, String mensaje) {
    }

    public Notificacion enviarNotificacion(Persona destinatario, String mensaje, EstrategiaNotificacion canal) {
        return null;
    }

    public void setEstrategia(EstrategiaNotificacion estrategia) {
        this.estrategia = estrategia;
    }
}
