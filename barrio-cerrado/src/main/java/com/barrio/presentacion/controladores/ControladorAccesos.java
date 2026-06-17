package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorAccesos;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.acceso.TipoAcceso;
import com.barrio.dominio.personas.Persona;
import com.barrio.presentacion.dto.AccesoDTO;

public class ControladorAccesos {

    private final GestorAccesos gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorAccesos(GestorAccesos gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    public AccesoDTO registrarIngreso(AccesoDTO dto) {
        Persona actor = gestorPersonas.buscarPorDni(dto.getDniActor());
        RegistroAcceso reg = gestor.registrarIngreso(actor, TipoAcceso.valueOf(dto.getTipoAcceso()));
        return toDTO(reg);
    }

    public void registrarEgreso(Long idAcceso) {
        gestor.registrarEgreso(idAcceso);
    }

    public boolean validarAcceso(String dniActor, String tipo) {
        Persona actor = gestorPersonas.buscarPorDni(dniActor);
        return gestor.validarAcceso(actor, TipoAcceso.valueOf(tipo));
    }

    public List<AccesoDTO> listarAccesos() {
        List<AccesoDTO> dtos = new ArrayList<>();
        for (RegistroAcceso reg : gestor.listarAccesos()) {
            dtos.add(toDTO(reg));
        }
        return dtos;
    }

    private AccesoDTO toDTO(RegistroAcceso reg) {
        if (reg == null) {
            return null;
        }
        AccesoDTO dto = new AccesoDTO();
        dto.setId(reg.getId());
        dto.setDniActor(reg.getActor() != null ? reg.getActor().getDni() : null);
        dto.setFechaHoraIngreso(reg.getFechaHoraIngreso() != null
                ? reg.getFechaHoraIngreso().toString() : null);
        dto.setFechaHoraEgreso(reg.getFechaHoraEgreso() != null
                ? reg.getFechaHoraEgreso().toString() : null);
        if (reg.getProtocolo() != null) {
            dto.setTipoAcceso(reg.getProtocolo().getClass().getSimpleName()
                    .replace("Protocolo", "").toUpperCase());
        }
        return dto;
    }
}
