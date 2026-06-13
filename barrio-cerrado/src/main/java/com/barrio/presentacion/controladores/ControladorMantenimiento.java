package com.barrio.presentacion.controladores;

import java.util.List;

import com.barrio.aplicacion.gestores.GestorMantenimiento;
import com.barrio.presentacion.dto.TareaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorMantenimiento {

    private GestorMantenimiento gestor;

    public TareaDTO registrarTarea(TareaDTO tarea) {
        return null;
    }

    public void asignarPersonal(Long idTarea, String dniProveedor) {
    }

    public List<TareaDTO> listarTareas() {
        return null;
    }
}
