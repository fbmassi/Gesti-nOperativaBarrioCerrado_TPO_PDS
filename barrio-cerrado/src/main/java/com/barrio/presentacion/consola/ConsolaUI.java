package com.barrio.presentacion.consola;

import com.barrio.presentacion.controladores.ControladorAccesos;
import com.barrio.presentacion.controladores.ControladorMantenimiento;
import com.barrio.presentacion.controladores.ControladorNotificaciones;
import com.barrio.presentacion.controladores.ControladorPersonas;
import com.barrio.presentacion.controladores.ControladorReclamos;
import com.barrio.presentacion.controladores.ControladorReportes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsolaUI {

    private ControladorReclamos ctrlReclamos;
    private ControladorAccesos ctrlAccesos;
    private ControladorMantenimiento ctrlMantenimiento;
    private ControladorNotificaciones ctrlNotificaciones;
    private ControladorReportes ctrlReportes;
    private ControladorPersonas ctrlPersonas;

    public static void main(String[] args) {
    }

    public void mostrarMenuPrincipal() {
    }

    public void mostrarMenuReclamos() {
    }

    public void mostrarMenuAccesos() {
    }

    public void mostrarMenuMantenimiento() {
    }

    public void mostrarMensaje(String mensaje) {
    }
}
