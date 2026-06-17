package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorAccesos;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.personas.Persona;
import com.barrio.presentacion.dto.AccesoDTO;
import com.barrio.presentacion.dto.PersonaDTO;

public class ControladorAccesos {

    private final GestorAccesos gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorAccesos(GestorAccesos gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    /** Lista las personas no residentes (visitantes, familiares, proveedores) ya registradas. */
    public List<PersonaDTO> listarNoResidentes() {
        List<PersonaDTO> dtos = new ArrayList<>();
        for (Persona p : gestorPersonas.listarNoResidentes()) {
            dtos.add(ControladorPersonas.toDTO(p));
        }
        return dtos;
    }

    /** Autoriza el ingreso de alguien que YA existe en el sistema (por DNI). */
    public PersonaDTO autorizarExistente(String dni) {
        return ControladorPersonas.toDTO(gestorPersonas.autorizarAcceso(dni));
    }

    /** Autoriza el ingreso creando a la persona en el momento (familiar o visitante). */
    public PersonaDTO autorizarNuevo(String categoria, String nombre, String apellido, String dni) {
        if ("FAMILIAR".equalsIgnoreCase(categoria)) {
            gestorPersonas.registrarFamiliar(nombre, apellido, dni);
        } else {
            gestorPersonas.registrarVisitante(nombre, apellido, dni);
        }
        return ControladorPersonas.toDTO(gestorPersonas.autorizarAcceso(dni));
    }

    public AccesoDTO registrarIngreso(String dniActor) {
        return toDTO(gestor.registrarIngreso(resolverActor(dniActor)));
    }

    /**
     * El guardia da de alta una persona de emergencia (queda con acceso vigente).
     * Luego registra su ingreso y egreso con las opciones normales.
     */
    public PersonaDTO registrarEmergencia(String nombre, String apellido, String dni) {
        return ControladorPersonas.toDTO(gestorPersonas.registrarEmergencia(nombre, apellido, dni));
    }

    public void registrarEgreso(String dniActor) {
        gestor.registrarEgreso(resolverActor(dniActor));
    }

    public boolean validarAcceso(String dniActor) {
        return gestor.validarAcceso(resolverActor(dniActor));
    }

    private Persona resolverActor(String dni) {
        Persona actor = gestorPersonas.buscarPorDni(dni);
        if (actor == null) {
            throw new IllegalArgumentException("No existe una persona registrada con el DNI " + dni);
        }
        return actor;
    }

    private AccesoDTO toDTO(RegistroAcceso reg) {
        if (reg == null) {
            return null;
        }
        AccesoDTO dto = new AccesoDTO();
        dto.setId(reg.getId());
        if (reg.getActor() != null) {
            dto.setDniActor(reg.getActor().getDni());
            dto.setNombreActor(reg.getActor().getNombreCompleto());
        }
        dto.setTipoAcceso(reg.getTipo() != null ? reg.getTipo().name() : null);
        dto.setPermitido(reg.isPermitido());
        dto.setFechaHoraIngreso(reg.getFechaHoraIngreso() != null ? reg.getFechaHoraIngreso().toString() : null);
        dto.setFechaHoraEgreso(reg.getFechaHoraEgreso() != null ? reg.getFechaHoraEgreso().toString() : null);
        return dto;
    }
}
