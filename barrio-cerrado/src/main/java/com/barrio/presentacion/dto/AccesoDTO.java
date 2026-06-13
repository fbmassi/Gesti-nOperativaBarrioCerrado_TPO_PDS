package com.barrio.presentacion.dto;

import lombok.Data;

@Data
public class AccesoDTO {

    private Long id;
    private String dniActor;
    private String fechaHoraIngreso;
    private String fechaHoraEgreso;
    private String tipoAcceso;
}
