package com.barrio.presentacion.controladores;

import java.util.List;

import com.barrio.aplicacion.gestores.GestorAccesos;
import com.barrio.presentacion.dto.AccesoDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorAccesos {

    private GestorAccesos gestor;

    public AccesoDTO registrarIngreso(AccesoDTO acceso) {
        return null;
    }

    public void registrarEgreso(Long idAcceso) {
    }

    public List<AccesoDTO> listarAccesos() {
        return null;
    }
}
