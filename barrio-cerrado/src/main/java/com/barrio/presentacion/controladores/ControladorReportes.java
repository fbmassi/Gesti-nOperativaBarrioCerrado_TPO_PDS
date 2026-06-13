package com.barrio.presentacion.controladores;

import com.barrio.aplicacion.gestores.GestorReportes;
import com.barrio.presentacion.dto.ReporteDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorReportes {

    private GestorReportes gestor;

    public ReporteDTO generarReporte(String tipo) {
        return null;
    }
}
