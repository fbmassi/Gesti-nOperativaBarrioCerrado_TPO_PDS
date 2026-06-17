package com.barrio.presentacion.dto;

import java.util.List;

import lombok.Data;

/**
 * DTO de viviendas del barrio.
 */
@Data
public class ViviendaDTO {

    private Long id;
    private String numero;
    private List<String> residentes;
}
