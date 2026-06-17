package com.barrio.aplicacion.servicios;

import com.barrio.dominio.acceso.ProtocoloAcceso;
import com.barrio.dominio.acceso.ProtocoloEmergencia;
import com.barrio.dominio.acceso.ProtocoloFamiliar;
import com.barrio.dominio.acceso.ProtocoloProveedor;
import com.barrio.dominio.acceso.ProtocoloVisitante;
import com.barrio.dominio.acceso.TipoAcceso;
import lombok.Getter;
import lombok.Setter;

/**
 * Patrón Factory: crea el protocolo de acceso correspondiente al tipo solicitado.
 */
@Getter
@Setter
public class FabricaProtocolos {

    public ProtocoloAcceso crearProtocolo(TipoAcceso tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("El tipo de acceso no puede ser nulo");
        }
        switch (tipo) {
            case FAMILIAR:
                return new ProtocoloFamiliar();
            case VISITANTE:
                return new ProtocoloVisitante();
            case PROVEEDOR:
                return new ProtocoloProveedor();
            case EMERGENCIA:
                return new ProtocoloEmergencia();
            default:
                throw new IllegalArgumentException("Tipo de acceso desconocido: " + tipo);
        }
    }
}
