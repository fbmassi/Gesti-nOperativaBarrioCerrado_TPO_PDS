package com.barrio.dominio.reportes;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class Reporte {

    private String titulo;
    private LocalDateTime fechaGeneracion;
    private String contenido;

    public abstract String generar();
}
