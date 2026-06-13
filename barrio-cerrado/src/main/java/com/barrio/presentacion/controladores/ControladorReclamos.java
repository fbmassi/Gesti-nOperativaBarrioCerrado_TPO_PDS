package com.barrio.presentacion.controladores;

import java.util.List;

import com.barrio.aplicacion.gestores.GestorReclamos;
import com.barrio.presentacion.dto.SolicitudDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorReclamos {

    private GestorReclamos gestor;

    public SolicitudDTO registrarReclamo(SolicitudDTO solicitud) {
        return null;
    }

    public void cambiarEstado(Long idReclamo, String nuevoEstado) {
    }

    public List<SolicitudDTO> listarReclamos() {
        return null;
    }
}
