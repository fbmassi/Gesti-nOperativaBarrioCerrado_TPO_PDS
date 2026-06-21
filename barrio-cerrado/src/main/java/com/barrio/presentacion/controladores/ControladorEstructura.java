package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorEstructura;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.estructura.Vivienda;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Residente;
import com.barrio.presentacion.dto.ViviendaDTO;

/**
 * Controlador de estructura: alta de viviendas y asociación de residentes.
 */
public class ControladorEstructura {

    private final GestorEstructura gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorEstructura(GestorEstructura gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    public ViviendaDTO registrarVivienda(String numero) {
        return toDTO(gestor.registrarVivienda(numero));
    }

    public void asignarResidente(Long viviendaId, String dniResidente) {
        Persona p = gestorPersonas.buscarPorDni(dniResidente);
        if (!(p instanceof Residente)) {
            throw new IllegalArgumentException("El DNI " + dniResidente + " no corresponde a un residente");
        }
        gestor.asignarResidente(viviendaId, (Residente) p);
    }

    public List<ViviendaDTO> listarViviendas() {
        List<ViviendaDTO> dtos = new ArrayList<>();
        for (Vivienda v : gestor.listarViviendas()) {
            dtos.add(toDTO(v));
        }
        return dtos;
    }

    private ViviendaDTO toDTO(Vivienda v) {
        if (v == null) {
            return null;
        }
        ViviendaDTO dto = new ViviendaDTO();
        dto.setId(v.getId());
        dto.setNumero(v.getNumero());
        List<String> res = new ArrayList<>();
        if (v.getResidentes() != null) {
            for (Residente r : v.getResidentes()) {
                res.add(r.getNombreCompleto() + " (DNI " + r.getDni() + ")");
            }
        }
        dto.setResidentes(res);
        return dto;
    }
}
