package com.barrio.aplicacion.trazabilidad;

import java.util.List;

import com.barrio.dominio.personas.Persona;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistorialAcciones {

    private static final HistorialAcciones instancia = new HistorialAcciones();

    private List<EntradaHistorial> entradas;

    public static HistorialAcciones getInstance() {
        return instancia;
    }

    public void registrarAccion(Persona autor, String descripcion) {
    }

    public List<EntradaHistorial> obtenerAcciones() {
        return null;
    }
}
