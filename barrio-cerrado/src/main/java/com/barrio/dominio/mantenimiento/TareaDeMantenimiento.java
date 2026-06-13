package com.barrio.dominio.mantenimiento;

import java.time.LocalDateTime;
import java.util.List;

import com.barrio.dominio.eventos.IObservable;
import com.barrio.dominio.eventos.Observador;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public abstract class TareaDeMantenimiento implements IObservable {

    private Long id;
    private String titulo;
    private String descripcion;
    private EstadoTarea estado;
    private LocalDateTime fechaCreacion;
    private List<Observador> observadores;

    public void cambiarEstado(EstadoTarea nuevoEstado) {
    }

    public EstadoTarea getEstado() {
        return estado;
    }

    @Override
    public void agregarObservador(Observador o) {
    }

    @Override
    public void quitarObservador(Observador o) {
    }

    @Override
    public void notificarObservadores() {
    }
}
