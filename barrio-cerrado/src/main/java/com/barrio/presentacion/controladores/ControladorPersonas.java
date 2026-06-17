package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.EspecialidadProveedor;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.dominio.personas.Residente;
import com.barrio.presentacion.dto.PersonaDTO;

/**
 * Controlador de personas: recibe DTOs, delega en el gestor y retorna DTOs.
 */
public class ControladorPersonas {

    private final GestorPersonas gestor;

    public ControladorPersonas(GestorPersonas gestor) {
        this.gestor = gestor;
    }

    public PersonaDTO registrarResidente(PersonaDTO dto) {
        Residente r = gestor.registrarResidente(dto.getNombre(), dto.getApellido(),
                dto.getDni(), dto.getEmail(), dto.getPassword(), dto.getNumeroLote());
        return toDTO(r);
    }

    public PersonaDTO registrarProveedor(PersonaDTO dto) {
        Proveedor p = gestor.registrarProveedor(dto.getNombre(), dto.getApellido(),
                dto.getDni(), dto.getEmail(), dto.getPassword(), dto.getEspecialidad());
        return toDTO(p);
    }

    public PersonaDTO registrarGuardia(PersonaDTO dto) {
        Guardia g = gestor.registrarGuardia(dto.getNombre(), dto.getApellido(),
                dto.getDni(), dto.getEmail(), dto.getPassword(), dto.getLegajo());
        return toDTO(g);
    }

    /** Asigna/cambia la contraseña de una persona existente (por DNI). Uso administrativo. */
    public PersonaDTO asignarPassword(String dni, String password) {
        return toDTO(gestor.asignarPassword(dni, password));
    }

    /** El propio actor elige por qué medio quiere ser notificado ("EMAIL"/"SMS"). */
    public PersonaDTO cambiarCanalNotificacion(String dni, String canal) {
        return toDTO(gestor.cambiarCanalNotificacion(dni, canal));
    }

    /** Lista proveedores filtrando por especialidad (nombre del enum) si se indica. */
    public List<PersonaDTO> listarProveedores(String especialidad) {
        EspecialidadProveedor esp = especialidad != null ? EspecialidadProveedor.desde(especialidad) : null;
        List<PersonaDTO> dtos = new ArrayList<>();
        for (Proveedor p : gestor.listarProveedoresPorEspecialidad(esp)) {
            dtos.add(toDTO(p));
        }
        return dtos;
    }

    /** Cambio de contraseña por el propio usuario (valida la actual). */
    public PersonaDTO cambiarPassword(String dni, String actual, String nueva) {
        return toDTO(gestor.cambiarPassword(dni, actual, nueva));
    }

    public PersonaDTO buscarResidentePorDni(String dni) {
        return toDTO(gestor.buscarResidentePorDni(dni));
    }

    public PersonaDTO buscarPorDni(String dni) {
        return toDTO(gestor.buscarPorDni(dni));
    }

    public List<PersonaDTO> listarResidentes() {
        List<PersonaDTO> dtos = new ArrayList<>();
        for (Residente r : gestor.listarResidentes()) {
            dtos.add(toDTO(r));
        }
        return dtos;
    }

    static PersonaDTO toDTO(Persona p) {
        if (p == null) {
            return null;
        }
        PersonaDTO dto = new PersonaDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setApellido(p.getApellido());
        dto.setDni(p.getDni());
        dto.setEmail(p.getEmail());
        dto.setTipo(p.getClass().getSimpleName());
        dto.setCanalNotificacion(p.getCanalNotificacion());
        dto.setAccesoAutorizado(p.isAccesoAutorizado());
        if (p instanceof Residente) {
            dto.setNumeroLote(((Residente) p).getNumeroLote());
        }
        if (p instanceof Proveedor && ((Proveedor) p).getEspecialidad() != null) {
            dto.setEspecialidad(((Proveedor) p).getEspecialidad().name());
        }
        if (p instanceof Guardia) {
            dto.setLegajo(((Guardia) p).getLegajo());
        }
        if (p instanceof Administrador) {
            dto.setLegajo(((Administrador) p).getLegajo());
        }
        return dto;
    }
}
