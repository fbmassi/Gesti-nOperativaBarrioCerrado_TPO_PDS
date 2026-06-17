package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.aplicacion.gestores.GestorReclamos;
import com.barrio.dominio.categorias.CategoriaSolicitud;
import com.barrio.dominio.categorias.PrioridadSolicitud;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.reclamos.Cerrado;
import com.barrio.dominio.reclamos.EnProceso;
import com.barrio.dominio.reclamos.EstadoSolicitud;
import com.barrio.dominio.reclamos.IncidenteSeguridad;
import com.barrio.dominio.reclamos.Pendiente;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.dominio.reclamos.Resuelto;
import com.barrio.dominio.reclamos.Solicitud;
import com.barrio.presentacion.dto.SolicitudDTO;

public class ControladorReclamos {

    private final GestorReclamos gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorReclamos(GestorReclamos gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    public SolicitudDTO registrarReclamo(SolicitudDTO dto) {
        Residente residente = (Residente) gestorPersonas.buscarPorDni(dto.getDniResidente());
        Reclamo r = gestor.registrarReclamo(residente, dto.getTitulo(), dto.getDescripcion(),
                CategoriaSolicitud.valueOf(dto.getCategoria()),
                PrioridadSolicitud.valueOf(dto.getPrioridad()));
        return toDTO(r);
    }

    public SolicitudDTO registrarIncidente(SolicitudDTO dto) {
        Guardia guardia = (Guardia) gestorPersonas.buscarPorDni(dto.getDniGuardia());
        IncidenteSeguridad i = gestor.registrarIncidente(guardia, dto.getTitulo(), dto.getDescripcion(),
                CategoriaSolicitud.SEGURIDAD,
                PrioridadSolicitud.valueOf(dto.getPrioridad()),
                dto.isUrgencia());
        return toDTO(i);
    }

    public void cambiarEstado(Long idReclamo, String nuevoEstado) {
        gestor.cambiarEstado(idReclamo, estadoDesde(nuevoEstado));
    }

    public List<SolicitudDTO> listarReclamos() {
        List<SolicitudDTO> dtos = new ArrayList<>();
        for (Reclamo r : gestor.listarReclamos()) {
            dtos.add(toDTO(r));
        }
        return dtos;
    }

    public List<SolicitudDTO> listarIncidentes() {
        List<SolicitudDTO> dtos = new ArrayList<>();
        for (IncidenteSeguridad i : gestor.listarIncidentes()) {
            dtos.add(toDTO(i));
        }
        return dtos;
    }

    private EstadoSolicitud estadoDesde(String estado) {
        switch (estado == null ? "" : estado.trim().toUpperCase()) {
            case "EN_PROCESO": return new EnProceso();
            case "RESUELTO": return new Resuelto();
            case "CERRADO": return new Cerrado();
            case "PENDIENTE": return new Pendiente();
            default: throw new IllegalArgumentException("Estado desconocido: " + estado);
        }
    }

    private SolicitudDTO toDTO(Solicitud s) {
        if (s == null) {
            return null;
        }
        SolicitudDTO dto = new SolicitudDTO();
        dto.setId(s.getId());
        dto.setTitulo(s.getTitulo());
        dto.setDescripcion(s.getDescripcion());
        dto.setEstado(s.getEstado() != null ? s.getEstado().getNombre() : null);
        dto.setCategoria(s.getCategoria() != null ? s.getCategoria().name() : null);
        dto.setPrioridad(s.getPrioridad() != null ? s.getPrioridad().name() : null);
        if (s instanceof Reclamo && ((Reclamo) s).getResidente() != null) {
            dto.setDniResidente(((Reclamo) s).getResidente().getDni());
        }
        return dto;
    }
}
