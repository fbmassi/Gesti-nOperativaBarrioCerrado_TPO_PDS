package com.barrio.dominio.personas;

/**
 * Especialidades disponibles para los proveedores del barrio.
 */
public enum EspecialidadProveedor {

    JARDINERIA("Jardinería"),
    PLOMERIA("Plomería"),
    ELECTRICIDAD("Electricidad"),
    GAS("Gas"),
    ALBANILERIA("Albañilería"),
    PINTURA("Pintura"),
    CARPINTERIA("Carpintería"),
    LIMPIEZA("Limpieza"),
    ALIMENTOS("Alimentos"),
    SEGURIDAD("Seguridad"),
    OTROS("Otros");

    private final String descripcion;

    EspecialidadProveedor(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Convierte un texto al enum; devuelve OTROS si no coincide y null si viene vacío. */
    public static EspecialidadProveedor desde(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return EspecialidadProveedor.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return OTROS;
        }
    }
}
