package com.barrio.presentacion.consola;

import java.util.Scanner;

import com.barrio.aplicacion.gestores.GestorAutenticacion;
import com.barrio.dominio.personas.Persona;

/**
 * Interfaz de consola con login y menús por rol.
 * La lógica de cada opción se implementará en la Fase 4; por ahora son placeholders.
 */
public class ConsolaUI {

    private final GestorAutenticacion autenticacion;
    private final Scanner scanner;

    public ConsolaUI(GestorAutenticacion autenticacion) {
        this.autenticacion = autenticacion;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║    SISTEMA GESTIÓN BARRIO CERRADO      ║");
        System.out.println("║    TPO - Programación de Sistemas      ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\n⚠️  En desarrollo - Contraseñas sin encriptación");
        System.out.println("Cuentas de prueba:");
        System.out.println("  admin@barrio.com / admin123");
        System.out.println("  residente@barrio.com / res123");
        System.out.println("  guardia@barrio.com / guard123");
        System.out.println("  proveedor@barrio.com / prov123");

        while (scanner.hasNextLine()) {
            Persona usuario = mostrarMenuLogin();
            if (usuario == null) {
                continue;
            }

            while (autenticacion.estaLogueado()) {
                System.out.println("\n╔════════════════════════════════════════╗");
                System.out.println("  Usuario: " + usuario.getNombreCompleto()
                        + " (" + usuario.getClass().getSimpleName() + ")");
                System.out.println("╚════════════════════════════════════════╝");

                if (autenticacion.esResidente()) {
                    mostrarOpcionesResidente();
                } else if (autenticacion.esAdministrador()) {
                    mostrarOpcionesAdmin();
                } else if (autenticacion.esGuardia()) {
                    mostrarOpcionesGuardia();
                } else if (autenticacion.esProveedor()) {
                    mostrarOpcionesProveedor();
                }

                System.out.println("0. Logout");
                System.out.print("\nOpción: ");

                if (!scanner.hasNextLine()) {
                    break;
                }
                String input = scanner.nextLine();
                try {
                    int opcion = Integer.parseInt(input);
                    if (opcion == 0) {
                        autenticacion.logout();
                        System.out.println("✓ Sesión cerrada");
                        break;
                    }
                    System.out.println("→ Opción " + opcion + ": funcionalidad pendiente (Fase 4)");
                } catch (NumberFormatException e) {
                    System.out.println("✗ Opción inválida");
                }
            }
        }
        System.out.println("\n¡Hasta luego!");
    }

    public Persona mostrarMenuLogin() {
        System.out.println("\n=== LOGIN ===");
        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        Persona persona = autenticacion.login(email, password);

        if (persona != null) {
            System.out.println("\n✓ Bienvenido " + persona.getNombreCompleto());
            return persona;
        }
        System.out.println("\n✗ Email o contraseña incorrectos");
        return null;
    }

    public void mostrarOpcionesResidente() {
        System.out.println("\n1. Registrar reclamo");
        System.out.println("2. Ver mis reclamos");
        System.out.println("3. Ver notificaciones");
    }

    public void mostrarOpcionesAdmin() {
        System.out.println("\n1. Registrar residente");
        System.out.println("2. Ver todos los reclamos");
        System.out.println("3. Cambiar estado de reclamo");
        System.out.println("4. Asignar tarea de mantenimiento");
        System.out.println("5. Ver historial");
        System.out.println("6. Generar reportes");
    }

    public void mostrarOpcionesGuardia() {
        System.out.println("\n1. Registrar ingreso/egreso");
        System.out.println("2. Validar acceso");
        System.out.println("3. Ver accesos registrados");
    }

    public void mostrarOpcionesProveedor() {
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Cambiar estado de tarea");
        System.out.println("3. Ver historial de trabajos");
    }
}
