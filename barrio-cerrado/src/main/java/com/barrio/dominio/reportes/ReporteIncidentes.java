package com.barrio.dominio.reportes;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReporteIncidentes extends Reporte {

    @Override
    public String generar() {
        return "===== " + getTitulo() + " =====\n"
                + "Tipo: Incidentes de Seguridad\n"
                + "Generado: " + getFechaGeneracion() + "\n"
                + (getContenido() != null ? getContenido() : "(sin datos)");
    }
}
