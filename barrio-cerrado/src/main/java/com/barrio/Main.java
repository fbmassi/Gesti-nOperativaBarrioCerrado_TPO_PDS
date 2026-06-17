package com.barrio;

import com.barrio.aplicacion.gestores.GestorAccesos;
import com.barrio.aplicacion.gestores.GestorAutenticacion;
import com.barrio.aplicacion.gestores.GestorEstructura;
import com.barrio.aplicacion.gestores.GestorMantenimiento;
import com.barrio.aplicacion.gestores.GestorNotificaciones;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.aplicacion.gestores.GestorReclamos;
import com.barrio.aplicacion.gestores.GestorReportes;
import com.barrio.aplicacion.servicios.FabricaProtocolos;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.notificaciones.CanalEmail;
import com.barrio.infraestructura.persistencia.RepositorioAccesos;
import com.barrio.infraestructura.persistencia.RepositorioNotificaciones;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import com.barrio.infraestructura.persistencia.RepositorioSolicitudes;
import com.barrio.infraestructura.persistencia.RepositorioTareas;
import com.barrio.infraestructura.persistencia.RepositorioViviendas;
import com.barrio.presentacion.consola.ConsolaUI;
import com.barrio.presentacion.controladores.ControladorAccesos;
import com.barrio.presentacion.controladores.ControladorEstructura;
import com.barrio.presentacion.controladores.ControladorHistorial;
import com.barrio.presentacion.controladores.ControladorMantenimiento;
import com.barrio.presentacion.controladores.ControladorNotificaciones;
import com.barrio.presentacion.controladores.ControladorPersonas;
import com.barrio.presentacion.controladores.ControladorReclamos;
import com.barrio.presentacion.controladores.ControladorReportes;

/**
 * Punto de entrada del sistema de gestión del barrio cerrado.
 * Instancia repositorios, gestores y la UI, y arranca el flujo con login.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Repositorios (persistencia H2)
        RepositorioPersonas repoPersonas = new RepositorioPersonas();
        RepositorioSolicitudes repoSolicitudes = new RepositorioSolicitudes();
        RepositorioAccesos repoAccesos = new RepositorioAccesos();
        RepositorioTareas repoTareas = new RepositorioTareas();
        RepositorioNotificaciones repoNotificaciones = new RepositorioNotificaciones();
        RepositorioViviendas repoViviendas = new RepositorioViviendas();

        // 2. Singleton de trazabilidad y fábrica de protocolos
        HistorialAcciones historial = HistorialAcciones.getInstance();
        FabricaProtocolos fabricaProtocolos = new FabricaProtocolos();

        // 3. Gestores (capa de aplicación)
        GestorNotificaciones gestorNotificaciones =
                new GestorNotificaciones(repoNotificaciones, repoPersonas, historial, new CanalEmail());
        GestorAutenticacion autenticacion = new GestorAutenticacion(repoPersonas, historial);
        GestorPersonas gestorPersonas = new GestorPersonas(repoPersonas, historial);
        GestorReclamos gestorReclamos =
                new GestorReclamos(repoSolicitudes, historial, gestorNotificaciones);
        GestorAccesos gestorAccesos =
                new GestorAccesos(repoAccesos, repoPersonas, historial, fabricaProtocolos);
        GestorMantenimiento gestorMantenimiento =
                new GestorMantenimiento(repoTareas, historial, gestorNotificaciones);
        GestorReportes gestorReportes =
                new GestorReportes(repoSolicitudes, repoAccesos, historial);
        GestorEstructura gestorEstructura = new GestorEstructura(repoViviendas, historial);

        // 4. Controladores (capa de presentación → gestores)
        ControladorPersonas ctrlPersonas = new ControladorPersonas(gestorPersonas);
        ControladorReclamos ctrlReclamos = new ControladorReclamos(gestorReclamos, gestorPersonas);
        ControladorAccesos ctrlAccesos = new ControladorAccesos(gestorAccesos, gestorPersonas);
        ControladorMantenimiento ctrlMantenimiento =
                new ControladorMantenimiento(gestorMantenimiento, gestorPersonas);
        ControladorNotificaciones ctrlNotificaciones =
                new ControladorNotificaciones(gestorNotificaciones, gestorPersonas);
        ControladorReportes ctrlReportes = new ControladorReportes(gestorReportes);
        ControladorHistorial ctrlHistorial = new ControladorHistorial(historial);
        ControladorEstructura ctrlEstructura = new ControladorEstructura(gestorEstructura, gestorPersonas);

        // 5. Interfaz de consola con login
        ConsolaUI ui = new ConsolaUI(autenticacion, ctrlPersonas, ctrlReclamos, ctrlAccesos,
                ctrlMantenimiento, ctrlNotificaciones, ctrlReportes, ctrlHistorial, ctrlEstructura);

        // 6. Arrancar
        ui.mostrarMenuPrincipal();
    }
}
