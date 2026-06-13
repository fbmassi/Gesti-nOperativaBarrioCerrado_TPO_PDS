package com.barrio.presentacion.controladores;

import java.util.List;

import com.barrio.aplicacion.gestores.GestorPersonas;
import com.barrio.presentacion.dto.PersonaDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Controlador de personas: recibe DTOs, delega en el gestor y retorna DTOs.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControladorPersonas {

    private GestorPersonas gestor;

    public PersonaDTO registrarResidente(PersonaDTO persona) {
        return null;
    }

    public PersonaDTO buscarResidentePorDni(String dni) {
        return null;
    }

    public List<PersonaDTO> listarResidentes() {
        return null;
    }
}
