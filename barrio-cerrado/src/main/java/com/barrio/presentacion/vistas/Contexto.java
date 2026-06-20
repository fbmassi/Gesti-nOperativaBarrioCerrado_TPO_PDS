package com.barrio.presentacion.vistas;

import com.barrio.aplicacion.gestores.GestorAutenticacion;
import com.barrio.presentacion.controladores.ControladorAccesos;
import com.barrio.presentacion.controladores.ControladorEstructura;
import com.barrio.presentacion.controladores.ControladorHistorial;
import com.barrio.presentacion.controladores.ControladorMantenimiento;
import com.barrio.presentacion.controladores.ControladorNotificaciones;
import com.barrio.presentacion.controladores.ControladorPersonas;
import com.barrio.presentacion.controladores.ControladorReclamos;
import com.barrio.presentacion.controladores.ControladorReportes;

/**
 * Contenedor de dependencias para las vistas Swing (autenticación + controladores).
 * Evita constructores con muchos parámetros y centraliza el cableado.
 */
public class Contexto {

    public final GestorAutenticacion autenticacion;
    public final ControladorPersonas personas;
    public final ControladorReclamos reclamos;
    public final ControladorAccesos accesos;
    public final ControladorMantenimiento mantenimiento;
    public final ControladorNotificaciones notificaciones;
    public final ControladorReportes reportes;
    public final ControladorHistorial historial;
    public final ControladorEstructura estructura;

    public Contexto(GestorAutenticacion autenticacion,
                    ControladorPersonas personas,
                    ControladorReclamos reclamos,
                    ControladorAccesos accesos,
                    ControladorMantenimiento mantenimiento,
                    ControladorNotificaciones notificaciones,
                    ControladorReportes reportes,
                    ControladorHistorial historial,
                    ControladorEstructura estructura) {
        this.autenticacion = autenticacion;
        this.personas = personas;
        this.reclamos = reclamos;
        this.accesos = accesos;
        this.mantenimiento = mantenimiento;
        this.notificaciones = notificaciones;
        this.reportes = reportes;
        this.historial = historial;
        this.estructura = estructura;
    }
}
