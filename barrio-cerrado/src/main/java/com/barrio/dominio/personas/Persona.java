package com.barrio.dominio.personas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Clase base abstracta para todas las personas del barrio.
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class Persona {

    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String password;
    /** Medio por el que la persona quiere recibir notificaciones: "EMAIL" o "SMS". */
    private String canalNotificacion = "EMAIL";
    /** Indica si la persona tiene el ingreso al barrio autorizado en este momento. */
    private boolean accesoAutorizado;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Valida la contraseña ingresada contra la almacenada (sin encriptación, solo desarrollo).
     */
    public boolean validarPassword(String passwordIngresada) {
        return this.password != null && this.password.equals(passwordIngresada);
    }
}
