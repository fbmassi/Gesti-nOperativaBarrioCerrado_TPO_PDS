package com.barrio.dominio.estructura;

import java.util.List;

import lombok.Data;

@Data
public class Barrio {

    private Long id;
    private String nombre;
    private String ubicacion;
    private List<Vivienda> viviendas;
    private List<SectorComun> sectores;

    public void agregarVivienda(Vivienda v) {
    }
}
