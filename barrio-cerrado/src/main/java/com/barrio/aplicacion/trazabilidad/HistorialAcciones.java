package com.barrio.aplicacion.trazabilidad;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.persistencia.RepositorioHistorial;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Patrón Singleton: registro único de trazabilidad de acciones del sistema (RNF-05).
 * Persiste cada acción en H2 vía RepositorioHistorial para garantizar auditoría durable.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HistorialAcciones {

    private static final HistorialAcciones instancia = new HistorialAcciones();

    private final RepositorioHistorial repositorio = new RepositorioHistorial();

    public static HistorialAcciones getInstance() {
        return instancia;
    }

    public void registrarAccion(Persona autor, String descripcion) {
        EntradaHistorial entrada = new EntradaHistorial();
        entrada.setFecha(LocalDateTime.now());
        entrada.setAutor(autor);
        entrada.setDescripcion(descripcion);
        repositorio.guardar(entrada);
    }

    public List<EntradaHistorial> obtenerAcciones() {
        return repositorio.buscarTodos();
    }
}
