package com.barrio.aplicacion.gestores;

import java.util.List;

import com.barrio.aplicacion.servicios.FabricaProtocolos;
import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
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
        return null;
    }

    public void registrarEgreso(Long idAcceso) {
    }

    public boolean validarAcceso(Persona actor, TipoAcceso tipo) {
        return false;
    }

    public List<RegistroAcceso> listarAccesos() {
        return null;
    }
}
