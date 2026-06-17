package com.barrio.dominio.reportes;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReporteAccesos extends Reporte {

    @Override
    public String generar() {
        return "===== " + getTitulo() + " =====\n"
                + "Tipo: Accesos\n"
                + "Generado: " + getFechaGeneracion() + "\n"
                + (getContenido() != null ? getContenido() : "(sin datos)");
    }
}
