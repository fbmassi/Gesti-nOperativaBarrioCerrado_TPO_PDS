package com.barrio.dominio.estructura;

import lombok.Data;

/**
 * Lote del barrio cerrado.
 */
@Data
public class Lote {

    private Long id;
    private int numero;
    private String manzana;
}
