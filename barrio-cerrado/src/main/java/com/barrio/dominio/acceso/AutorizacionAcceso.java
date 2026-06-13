package com.barrio.dominio.acceso;

import java.time.LocalDateTime;

import com.barrio.dominio.personas.Residente;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AutorizacionAcceso {

    private Long id;
    private Residente otorgadaPor;
    private LocalDateTime validaHasta;

    public boolean estaVigente() {
        return false;
    }
}
