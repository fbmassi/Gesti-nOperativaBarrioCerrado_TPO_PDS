package com.barrio.aplicacion.gestores;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.aplicacion.servicios.FabricaProtocolos;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.acceso.ProtocoloAcceso;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.acceso.TipoAcceso;
import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.persistencia.RepositorioAccesos;
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
    private HistorialAcciones historial;
    private FabricaProtocolos fabricaProtocolos;

    public RegistroAcceso registrarIngreso(Persona actor, TipoAcceso tipo) {
        // Factory: obtiene el protocolo correspondiente al tipo de acceso.
        ProtocoloAcceso protocolo = fabricaProtocolos.crearProtocolo(tipo);
        boolean permitido = protocolo.validar(actor);

        RegistroAcceso registro = new RegistroAcceso();
        registro.setActor(actor);
        registro.setFechaHoraIngreso(LocalDateTime.now());
        registro.setProtocolo(protocolo);
        registro.setPermitido(permitido);

        repositorio.guardar(registro);
        if (historial != null) {
            historial.registrarAccion(actor,
                    "Ingreso " + tipo + (permitido ? " permitido" : " denegado"));
        }
        return registro;
    }

    public void registrarEgreso(Long idAcceso) {
        RegistroAcceso registro = repositorio.buscarPorId(idAcceso);
        if (registro == null) {
            throw new IllegalArgumentException("Registro de acceso no encontrado: " + idAcceso);
        }
        registro.registrarEgreso();
        repositorio.guardar(registro);
        if (historial != null) {
            historial.registrarAccion(registro.getActor(), "Egreso del registro " + idAcceso);
        }
    }

    public boolean validarAcceso(Persona actor, TipoAcceso tipo) {
        // Factory + Strategy: el protocolo decide si el acceso es válido.
        return fabricaProtocolos.crearProtocolo(tipo).validar(actor);
    }

    public List<RegistroAcceso> listarAccesos() {
        return repositorio.buscarTodos();
    }
}
