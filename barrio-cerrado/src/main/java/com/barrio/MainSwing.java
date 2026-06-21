package com.barrio;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

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
import com.barrio.presentacion.controladores.ControladorAccesos;
import com.barrio.presentacion.controladores.ControladorEstructura;
import com.barrio.presentacion.controladores.ControladorHistorial;
import com.barrio.presentacion.controladores.ControladorMantenimiento;
import com.barrio.presentacion.controladores.ControladorNotificaciones;
import com.barrio.presentacion.controladores.ControladorPersonas;
import com.barrio.presentacion.controladores.ControladorReclamos;
import com.barrio.presentacion.controladores.ControladorReportes;
import com.barrio.presentacion.vistas.Contexto;
import com.barrio.presentacion.vistas.LoginView;

/**
 * Punto de entrada de la interfaz gráfica (Swing).
 * Reutiliza los mismos controladores/gestores que la consola.
 */
public class MainSwing {

    public static void main(String[] args) {
        // Repositorios
        RepositorioPersonas repoPersonas = new RepositorioPersonas();
        RepositorioSolicitudes repoSolicitudes = new RepositorioSolicitudes();
        RepositorioAccesos repoAccesos = new RepositorioAccesos();
        RepositorioTareas repoTareas = new RepositorioTareas();
        RepositorioNotificaciones repoNotificaciones = new RepositorioNotificaciones();
        RepositorioViviendas repoViviendas = new RepositorioViviendas();

        HistorialAcciones historial = HistorialAcciones.getInstance();
        FabricaProtocolos fabricaProtocolos = new FabricaProtocolos();

        // Gestores
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
        GestorReportes gestorReportes = new GestorReportes(repoSolicitudes, repoAccesos, historial);
        GestorEstructura gestorEstructura = new GestorEstructura(repoViviendas, historial);

        // Controladores
        Contexto ctx = new Contexto(
                autenticacion,
                new ControladorPersonas(gestorPersonas),
                new ControladorReclamos(gestorReclamos, gestorPersonas),
                new ControladorAccesos(gestorAccesos, gestorPersonas),
                new ControladorMantenimiento(gestorMantenimiento, gestorPersonas),
                new ControladorNotificaciones(gestorNotificaciones, gestorPersonas),
                new ControladorReportes(gestorReportes),
                new ControladorHistorial(historial),
                new ControladorEstructura(gestorEstructura, gestorPersonas));

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si falla, se usa el Look and Feel por defecto.
            }
            new LoginView(ctx).setVisible(true);
        });
    }
}
