package com.barrio.dominio.acceso;

import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProtocoloProveedor implements ProtocoloAcceso {

    @Override
    public boolean validar(Persona actor) {
        // El protocolo de proveedor solo admite proveedores registrados.
        return actor instanceof Proveedor;
    }
}
