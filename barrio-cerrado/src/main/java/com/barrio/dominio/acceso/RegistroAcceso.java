package com.barrio.dominio.acceso;

import java.time.LocalDateTime;

import com.barrio.dominio.personas.Persona;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroAcceso {

    private Long id;
    private Persona actor;
    private TipoAcceso tipo;
    private LocalDateTime fechaHoraIngreso;
    private LocalDateTime fechaHoraEgreso;
    private boolean permitido;

    public void registrarEgreso() {
        this.fechaHoraEgreso = LocalDateTime.now();
    }
}
