package com.barrio.aplicacion.gestores;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.reportes.Reporte;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorReportes {

    private HistorialAcciones historial;

    public Reporte generarReporte(String tipo) {
        return null;
    }
}
