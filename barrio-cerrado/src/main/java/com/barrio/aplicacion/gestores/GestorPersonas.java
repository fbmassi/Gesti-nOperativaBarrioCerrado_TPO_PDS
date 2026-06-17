package com.barrio.aplicacion.gestores;

import java.util.ArrayList;
import java.util.List;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Emergencia;
import com.barrio.dominio.personas.EspecialidadProveedor;
import com.barrio.dominio.personas.Familiar;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.personas.Visitante;
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

    public Residente registrarResidente(String nombre, String apellido, String dni, String email,
                                        String password, int numeroLote) {
        Residente residente = new Residente(numeroLote);
        completarDatos(residente, nombre, apellido, dni, email, password);
        // El residente tiene acceso al barrio siempre válido.
        residente.setAccesoAutorizado(true);
        repositorio.guardar(residente);
        registrar(residente, "Alta de residente: ");
        return residente;
    }

    public Proveedor registrarProveedor(String nombre, String apellido, String dni, String email,
                                        String password, String especialidad) {
        Proveedor proveedor = new Proveedor(EspecialidadProveedor.desde(especialidad));
        completarDatos(proveedor, nombre, apellido, dni, email, password);
        repositorio.guardar(proveedor);
        registrar(proveedor, "Alta de proveedor: ");
        return proveedor;
    }

    public Guardia registrarGuardia(String nombre, String apellido, String dni, String email,
                                    String password, String legajo) {
        Guardia guardia = new Guardia(legajo);
        completarDatos(guardia, nombre, apellido, dni, email, password);
        repositorio.guardar(guardia);
        registrar(guardia, "Alta de guardia: ");
        return guardia;
    }

    public Administrador registrarAdministrador(String nombre, String apellido, String dni, String email,
                                                String password, String legajo) {
        Administrador admin = new Administrador(legajo);
        completarDatos(admin, nombre, apellido, dni, email, password);
        repositorio.guardar(admin);
        registrar(admin, "Alta de administrador: ");
        return admin;
    }

    /** Alta de un visitante al autorizar su ingreso (solo nombre, apellido y DNI). */
    public Visitante registrarVisitante(String nombre, String apellido, String dni) {
        Visitante visitante = new Visitante();
        completarDatos(visitante, nombre, apellido, dni, null, null);
        repositorio.guardar(visitante);
        registrar(visitante, "Alta de visitante: ");
        return visitante;
    }

    /** Alta de un familiar al autorizar su ingreso (solo nombre, apellido y DNI). */
    public Familiar registrarFamiliar(String nombre, String apellido, String dni) {
        Familiar familiar = new Familiar();
        completarDatos(familiar, nombre, apellido, dni, null, null);
        repositorio.guardar(familiar);
        registrar(familiar, "Alta de familiar: ");
        return familiar;
    }

    /** Alta de una persona de emergencia (la registra el guardia); queda con acceso vigente. */
    public Emergencia registrarEmergencia(String nombre, String apellido, String dni) {
        Emergencia emergencia = new Emergencia();
        completarDatos(emergencia, nombre, apellido, dni, null, null);
        emergencia.setAccesoAutorizado(true);
        repositorio.guardar(emergencia);
        registrar(emergencia, "Alta de emergencia: ");
        return emergencia;
    }

    /** Autoriza el ingreso de una persona (visitante/familiar/proveedor): deja su acceso vigente. */
    public Persona autorizarAcceso(String dni) {
        Persona persona = buscarPorDni(dni);
        if (persona == null) {
            throw new IllegalArgumentException("No existe una persona registrada con el DNI " + dni);
        }
        persona.setAccesoAutorizado(true);
        repositorio.guardar(persona);
        registrar(persona, "Ingreso autorizado: ");
        return persona;
    }

    /** Revoca el acceso de una persona (al egresar). Los residentes conservan el acceso. */
    public void revocarAcceso(Persona persona) {
        if (persona == null || persona instanceof Residente) {
            return;
        }
        persona.setAccesoAutorizado(false);
        repositorio.guardar(persona);
    }

    /** Personas que NO son residentes (visitantes, familiares, proveedores) para autorizarlas. */
    public List<Persona> listarNoResidentes() {
        List<Persona> lista = new ArrayList<>();
        for (Persona p : repositorio.buscarTodos()) {
            if (p instanceof Visitante || p instanceof Familiar
                    || p instanceof Proveedor || p instanceof Emergencia) {
                lista.add(p);
            }
        }
        return lista;
    }

    /** Asigna o cambia la contraseña de una persona ya existente (buscada por DNI). Uso administrativo. */
    public Persona asignarPassword(String dni, String password) {
        Persona persona = buscarPorDni(dni);
        if (persona == null) {
            throw new IllegalArgumentException("No existe una persona con DNI " + dni);
        }
        persona.setPassword(password);
        repositorio.guardar(persona);
        registrar(persona, "Cambio de contraseña: ");
        return persona;
    }

    /** Cambio de contraseña por el propio usuario: valida la contraseña actual antes de cambiarla. */
    public Persona cambiarPassword(String dni, String passwordActual, String passwordNueva) {
        Persona persona = buscarPorDni(dni);
        if (persona == null) {
            throw new IllegalArgumentException("No existe una persona con DNI " + dni);
        }
        if (!persona.validarPassword(passwordActual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        persona.setPassword(passwordNueva);
        repositorio.guardar(persona);
        registrar(persona, "Cambio de contraseña: ");
        return persona;
    }

    private void completarDatos(Persona persona, String nombre, String apellido, String dni,
                                String email, String password) {
        validarUnicidad(dni, email);
        persona.setNombre(nombre);
        persona.setApellido(apellido);
        persona.setDni(dni);
        persona.setEmail(email);
        persona.setPassword(password);
    }

    /** Verifica que el DNI y el email no estén ya en uso, con un mensaje claro. */
    private void validarUnicidad(String dni, String email) {
        if (dni != null && buscarPorDni(dni) != null) {
            throw new IllegalArgumentException("Ya existe una persona con el DNI " + dni);
        }
        if (email != null && !email.trim().isEmpty() && repositorio.buscarPorEmail(email) != null) {
            throw new IllegalArgumentException("Ya existe una persona con el email " + email);
        }
    }

    private void registrar(Persona persona, String prefijo) {
        if (historial != null) {
            historial.registrarAccion(persona, prefijo + persona.getNombreCompleto());
        }
    }

    public Residente buscarResidentePorDni(String dni) {
        for (Persona p : repositorio.buscarTodos()) {
            if (p instanceof Residente && p.getDni() != null && p.getDni().equals(dni)) {
                return (Residente) p;
            }
        }
        return null;
    }

    /** Cambia el medio por el que una persona quiere recibir notificaciones ("EMAIL"/"SMS"). */
    public Persona cambiarCanalNotificacion(String dni, String canal) {
        Persona persona = buscarPorDni(dni);
        if (persona == null) {
            throw new IllegalArgumentException("No existe una persona con DNI " + dni);
        }
        persona.setCanalNotificacion(canal);
        repositorio.actualizarCanal(persona.getId(), canal);
        return persona;
    }

    /** Lista los proveedores; si se pasa una especialidad, filtra por ella. */
    public List<Proveedor> listarProveedoresPorEspecialidad(EspecialidadProveedor especialidad) {
        List<Proveedor> proveedores = new ArrayList<>();
        for (Persona p : repositorio.buscarTodos()) {
            if (p instanceof Proveedor
                    && (especialidad == null || ((Proveedor) p).getEspecialidad() == especialidad)) {
                proveedores.add((Proveedor) p);
            }
        }
        return proveedores;
    }

    /** Busca cualquier persona (residente, proveedor, guardia, etc.) por DNI. */
    public Persona buscarPorDni(String dni) {
        for (Persona p : repositorio.buscarTodos()) {
            if (p.getDni() != null && p.getDni().equals(dni)) {
                return p;
            }
        }
        return null;
    }

    public List<Residente> listarResidentes() {
        List<Residente> residentes = new ArrayList<>();
        for (Persona p : repositorio.buscarTodos()) {
            if (p instanceof Residente) {
                residentes.add((Residente) p);
            }
        }
        return residentes;
    }
}
