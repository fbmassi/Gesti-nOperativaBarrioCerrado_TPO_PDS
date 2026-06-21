package com.barrio.dominio.estructura;

import java.util.ArrayList;
import java.util.List;

import com.barrio.dominio.personas.Residente;
import lombok.Data;

@Data
public class Vivienda {

    private Long id;
    private String numero;
    private List<Residente> residentes;

    public void agregarResidente(Residente r) {
        if (residentes == null) {
            residentes = new ArrayList<>();
        }
        if (r != null && !residentes.contains(r)) {
            residentes.add(r);
        }
    }
}
