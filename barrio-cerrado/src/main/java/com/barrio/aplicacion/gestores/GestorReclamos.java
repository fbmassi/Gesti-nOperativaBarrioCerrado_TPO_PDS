package com.barrio.aplicacion.gestores;

import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.categorias.CategoriaSolicitud;
import com.barrio.dominio.categorias.PrioridadSolicitud;
import com.barrio.dominio.eventos.Observador;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.reclamos.EstadoSolicitud;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.infraestructura.persistencia.RepositorioSolicitudes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorReclamos {

    private RepositorioSolicitudes repositorio;
    private HistorialAcciones historial;
    private Observador notificador;

    public Reclamo registrarReclamo(Residente residente, String titulo, String descripcion,
                                    CategoriaSolicitud categoria, PrioridadSolicitud prioridad) {
        return null;
    }

    public void cambiarEstado(Long idReclamo, EstadoSolicitud nuevoEstado) {
    }

    public List<Reclamo> listarReclamos() {
        return null;
    }
}
