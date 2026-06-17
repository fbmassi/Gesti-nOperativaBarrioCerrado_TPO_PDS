package com.barrio.presentacion.controladores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.gestores.GestorMantenimiento;
import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.dominio.mantenimiento.EstadoTarea;
import com.barrio.dominio.mantenimiento.TareaCompuesta;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.mantenimiento.TareaSimple;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.presentacion.dto.TareaDTO;

public class ControladorMantenimiento {

    private final GestorMantenimiento gestor;
    private final GestorPersonas gestorPersonas;

    public ControladorMantenimiento(GestorMantenimiento gestor, GestorPersonas gestorPersonas) {
        this.gestor = gestor;
        this.gestorPersonas = gestorPersonas;
    }

    public TareaDTO registrarTarea(TareaDTO dto) {
        TareaDeMantenimiento t = gestor.registrarTarea(dto.getTitulo(), dto.getDescripcion());
        return toDTO(t);
    }

    /** Crea una tarea simple (sin subtareas) asignada al proveedor del DTO (si trae dni). */
    public TareaDTO registrarTareaSimple(TareaDTO dto) {
        Proveedor prov = resolverProveedor(dto.getDniProveedor());
        TareaDeMantenimiento t = gestor.registrarTareaSimple(dto.getTitulo(), dto.getDescripcion(), prov);
        return toDTO(t);
    }

    public void asignarPersonal(Long idTarea, String dniProveedor) {
        Proveedor prov = (Proveedor) gestorPersonas.buscarPorDni(dniProveedor);
        gestor.asignarPersonal(idTarea, prov);
    }

    /**
     * Agrega una subtarea a una tarea compuesta. Si el DTO trae dniProveedor, la asigna al crearla.
     */
    public void registrarSubtarea(Long idTareaPadre, TareaDTO dto) {
        Proveedor prov = resolverProveedor(dto.getDniProveedor());
        gestor.registrarSubtarea(idTareaPadre, dto.getTitulo(), dto.getDescripcion(), prov);
    }

    /** Resuelve un DNI a Proveedor; null si viene vacío; error si no corresponde a un proveedor. */
    private Proveedor resolverProveedor(String dni) {
        if (dni == null || dni.trim().isEmpty()) {
            return null;
        }
        Persona p = gestorPersonas.buscarPorDni(dni.trim());
        if (!(p instanceof Proveedor)) {
            throw new IllegalArgumentException("El DNI " + dni + " no corresponde a un proveedor");
        }
        return (Proveedor) p;
    }

    public void cambiarEstado(Long idTarea, String estado) {
        gestor.cambiarEstado(idTarea, EstadoTarea.valueOf(estado));
    }

    public List<TareaDTO> listarTareas() {
        List<TareaDTO> dtos = new ArrayList<>();
        for (TareaDeMantenimiento t : gestor.listarTareas()) {
            dtos.add(toDTO(t));
        }
        return dtos;
    }

    /**
     * Devuelve las tareas (hojas) asignadas a un proveedor, recorriendo recursivamente
     * el árbol del Composite (las subtareas viven dentro de las tareas compuestas).
     */
    public List<TareaDTO> listarTareasDeProveedor(String dniProveedor) {
        List<TareaDTO> dtos = new ArrayList<>();
        for (TareaDeMantenimiento t : gestor.listarTareas()) {
            recolectarAsignadas(t, dniProveedor, dtos);
        }
        return dtos;
    }

    private void recolectarAsignadas(TareaDeMantenimiento t, String dni, List<TareaDTO> acc) {
        if (t instanceof TareaSimple) {
            TareaSimple s = (TareaSimple) t;
            if (s.getPersonalAsignado() != null && dni != null
                    && dni.equals(s.getPersonalAsignado().getDni())) {
                acc.add(toDTO(s));
            }
        } else if (t instanceof TareaCompuesta) {
            List<TareaDeMantenimiento> subs = ((TareaCompuesta) t).getSubtareas();
            if (subs != null) {
                for (TareaDeMantenimiento sub : subs) {
                    recolectarAsignadas(sub, dni, acc);
                }
            }
        }
    }

    private TareaDTO toDTO(TareaDeMantenimiento t) {
        if (t == null) {
            return null;
        }
        TareaDTO dto = new TareaDTO();
        dto.setId(t.getId());
        dto.setTitulo(t.getTitulo());
        dto.setDescripcion(t.getDescripcion());
        dto.setEstado(t.getEstado() != null ? t.getEstado().name() : null);
        dto.setTipo(t instanceof TareaCompuesta ? "COMPUESTA" : "SIMPLE");
        if (t instanceof TareaSimple && ((TareaSimple) t).getPersonalAsignado() != null) {
            dto.setDniProveedor(((TareaSimple) t).getPersonalAsignado().getDni());
        }
        return dto;
    }
}
