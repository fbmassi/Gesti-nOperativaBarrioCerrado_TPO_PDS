package com.barrio.dominio.reportes;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReporteReclamos extends Reporte {

    @Override
    public String generar() {
        return "===== " + getTitulo() + " =====\n"
                + "Tipo: Reclamos\n"
                + "Generado: " + getFechaGeneracion() + "\n"
                + (getContenido() != null ? getContenido() : "(sin datos)");
    }
}
