package com.barrio.aplicacion.gestores;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.aplicacion.servicios.FabricaProtocolos;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.acceso.ProtocoloAcceso;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.acceso.TipoAcceso;
import com.barrio.dominio.personas.Emergencia;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.personas.Visitante;
import com.barrio.infraestructura.persistencia.RepositorioAccesos;
import com.barrio.infraestructura.persistencia.RepositorioPersonas;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorAccesos {

    private RepositorioAccesos repositorio;
    private RepositorioPersonas repositorioPersonas;
    private HistorialAcciones historial;
    private FabricaProtocolos fabricaProtocolos;

    /**
     * El guardia registra el ingreso: aplica el protocolo según el tipo de persona (Factory + Strategy)
     * y deja el registro de acceso (permitido si la persona tiene el acceso autorizado).
     */
    public RegistroAcceso registrarIngreso(Persona actor) {
        TipoAcceso tipo = tipoDe(actor);
        ProtocoloAcceso protocolo = fabricaProtocolos.crearProtocolo(tipo);
        boolean permitido = protocolo.validar(actor);
        RegistroAcceso registro = crearRegistro(actor, tipo, permitido);
        if (historial != null) {
            historial.registrarAccion(actor, "Ingreso " + tipo + (permitido ? " permitido" : " denegado"));
        }
        return registro;
    }

    /** El guardia registra el egreso; al salir, el no residente pierde el acceso hasta nueva autorización. */
    public void registrarEgreso(Persona actor) {
        RegistroAcceso registro = repositorio.buscarAbiertoPorActor(actor.getDni());
        if (registro == null) {
            throw new IllegalStateException("No hay un ingreso abierto (sin egreso) para el DNI " + actor.getDni());
        }
        registro.registrarEgreso();
        repositorio.guardar(registro);
        if (!(actor instanceof Residente)) {
            actor.setAccesoAutorizado(false);
            repositorioPersonas.guardar(actor);
        }
        if (historial != null) {
            historial.registrarAccion(actor, "Egreso del registro " + registro.getId());
        }
    }

    /** Valida (sin registrar) si la persona puede ingresar según su protocolo. */
    public boolean validarAcceso(Persona actor) {
        return fabricaProtocolos.crearProtocolo(tipoDe(actor)).validar(actor);
    }

    public List<RegistroAcceso> listarAccesos() {
        return repositorio.buscarTodos();
    }

    private RegistroAcceso crearRegistro(Persona actor, TipoAcceso tipo, boolean permitido) {
        RegistroAcceso registro = new RegistroAcceso();
        registro.setActor(actor);
        registro.setTipo(tipo);
        registro.setFechaHoraIngreso(LocalDateTime.now());
        registro.setPermitido(permitido);
        repositorio.guardar(registro);
        return registro;
    }

    /** Determina el tipo de acceso (protocolo) según el tipo de persona. */
    private TipoAcceso tipoDe(Persona actor) {
        if (actor instanceof Proveedor) {
            return TipoAcceso.PROVEEDOR;
        }
        if (actor instanceof Visitante) {
            return TipoAcceso.VISITANTE;
        }
        if (actor instanceof Emergencia) {
            return TipoAcceso.EMERGENCIA;
        }
        // Residente y Familiar usan el protocolo familiar.
        return TipoAcceso.FAMILIAR;
    }
}
