package com.barrio.aplicacion.gestores;

import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.personas.Residente;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Gestor de alta y consulta de personas del barrio.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorPersonas {

    private RepositorioPersonas repositorio;
    private HistorialAcciones historial;

    public Residente registrarResidente(String nombre, String apellido, String dni, String email, int numeroLote) {
        return null;
    }

    public Residente buscarResidentePorDni(String dni) {
        return null;
    }

    public List<Residente> listarResidentes() {
        return null;
    }
}
