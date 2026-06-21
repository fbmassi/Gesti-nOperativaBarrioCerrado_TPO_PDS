package com.barrio.aplicacion.gestores;

import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.estructura.Vivienda;
import com.barrio.dominio.personas.Residente;
import com.barrio.infraestructura.persistencia.RepositorioViviendas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Gestor de la estructura del barrio: alta de viviendas y asociación de residentes.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorEstructura {

    private RepositorioViviendas repositorio;
    private HistorialAcciones historial;

    public Vivienda registrarVivienda(String numero) {
        Vivienda vivienda = new Vivienda();
        vivienda.setNumero(numero);
        repositorio.guardar(vivienda);
        if (historial != null) {
            historial.registrarAccion(null, "Alta de vivienda: " + numero);
        }
        return vivienda;
    }

    public void asignarResidente(Long viviendaId, Residente residente) {
        if (residente == null || residente.getId() == null) {
            throw new IllegalArgumentException("Residente inválido");
        }
        if (repositorio.buscarPorId(viviendaId) == null) {
            throw new IllegalArgumentException("No existe la vivienda " + viviendaId);
        }
        repositorio.asociarResidente(viviendaId, residente.getId());
        if (historial != null) {
            historial.registrarAccion(residente,
                    "Residente " + residente.getNombreCompleto() + " asignado a vivienda " + viviendaId);
        }
    }

    public List<Vivienda> listarViviendas() {
        return repositorio.buscarTodos();
    }
}
