package com.barrio.aplicacion.gestores;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.dominio.personas.Residente;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Gestor de autenticación: maneja login/logout y la sesión del usuario actual.
 * Contraseñas en texto plano: únicamente para desarrollo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorAutenticacion {

    private RepositorioPersonas repositorioPersonas;
    private HistorialAcciones historial;

    /** Usuario con sesión activa (null si no hay nadie logueado). */
    private Persona usuarioActual;

    public GestorAutenticacion(RepositorioPersonas repositorioPersonas, HistorialAcciones historial) {
        this.repositorioPersonas = repositorioPersonas;
        this.historial = historial;
    }

    /**
     * Valida email + password contra la BD. Retorna la persona si es correcto, null en caso contrario.
     */
    public Persona login(String email, String password) {
        Persona persona = repositorioPersonas.buscarPorEmail(email);

        if (persona == null || !persona.validarPassword(password)) {
            return null;
        }

        this.usuarioActual = persona;
        if (historial != null) {
            historial.registrarAccion(persona, "Login");
        }
        return persona;
    }

    public void logout() {
        if (usuarioActual != null) {
            if (historial != null) {
                historial.registrarAccion(usuarioActual, "Logout");
            }
            this.usuarioActual = null;
        }
    }

    public boolean estaLogueado() {
        return usuarioActual != null;
    }

    public boolean esResidente() {
        return usuarioActual instanceof Residente;
    }

    public boolean esAdministrador() {
        return usuarioActual instanceof Administrador;
    }

    public boolean esGuardia() {
        return usuarioActual instanceof Guardia;
    }

    public boolean esProveedor() {
        return usuarioActual instanceof Proveedor;
    }
}
