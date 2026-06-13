package com.barrio;

import com.barrio.aplicacion.gestores.GestorAutenticacion;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import com.barrio.presentacion.consola.ConsolaUI;

/**
 * Punto de entrada del sistema de gestión del barrio cerrado.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Repositorio de personas (login)
        RepositorioPersonas repositorioPersonas = new RepositorioPersonas();

        // 2. Historial de acciones (Singleton)
        HistorialAcciones historial = HistorialAcciones.getInstance();

        // 3. Gestor de autenticación
        GestorAutenticacion autenticacion = new GestorAutenticacion(repositorioPersonas, historial);

        // 4. Interfaz de consola con login
        ConsolaUI ui = new ConsolaUI(autenticacion);

        // 5. Arrancar
        ui.mostrarMenuPrincipal();
    }
}
