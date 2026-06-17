package com.barrio.aplicacion.gestores;

import java.time.LocalDateTime;

import com.barrio.aplicacion.trazabilidad.HistorialAcciones;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.reclamos.IncidenteSeguridad;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.dominio.reclamos.Solicitud;
import com.barrio.dominio.reportes.Reporte;
import com.barrio.dominio.reportes.ReporteAccesos;
import com.barrio.dominio.reportes.ReporteIncidentes;
import com.barrio.dominio.reportes.ReporteReclamos;
import com.barrio.infraestructura.persistencia.RepositorioAccesos;
import com.barrio.infraestructura.persistencia.RepositorioSolicitudes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GestorReportes {

    private RepositorioSolicitudes repositorioSolicitudes;
    private RepositorioAccesos repositorioAccesos;
    private HistorialAcciones historial;

    public Reporte generarReporte(String tipo) {
        Reporte reporte;
        switch (tipo == null ? "" : tipo.trim().toUpperCase()) {
            case "RECLAMOS":
                reporte = new ReporteReclamos();
                reporte.setTitulo("Reporte de Reclamos");
                reporte.setContenido(contenidoReclamos());
                break;
            case "ACCESOS":
                reporte = new ReporteAccesos();
                reporte.setTitulo("Reporte de Accesos");
                reporte.setContenido(contenidoAccesos());
                break;
            case "INCIDENTES":
                reporte = new ReporteIncidentes();
                reporte.setTitulo("Reporte de Incidentes");
                reporte.setContenido(contenidoIncidentes());
                break;
            default:
                throw new IllegalArgumentException("Tipo de reporte desconocido: " + tipo);
        }
        reporte.setFechaGeneracion(LocalDateTime.now());
        if (historial != null) {
            historial.registrarAccion(null, "Reporte generado: " + tipo);
        }
        return reporte;
    }

    private String contenidoReclamos() {
        int total = 0;
        StringBuilder sb = new StringBuilder();
        for (Solicitud s : repositorioSolicitudes.buscarTodos()) {
            if (s instanceof Reclamo) {
                total++;
                sb.append("- ").append(s.getTitulo())
                        .append(" [").append(s.getEstado() != null ? s.getEstado().getNombre() : "?").append("]")
                        .append(" | categoría=").append(s.getCategoria() != null ? s.getCategoria() : "-")
                        .append(" | prioridad=").append(s.getPrioridad() != null ? s.getPrioridad() : "-")
                        .append("\n");
            }
        }
        return "Total de reclamos: " + total + "\n" + sb;
    }

    private String contenidoIncidentes() {
        int total = 0;
        StringBuilder sb = new StringBuilder();
        for (Solicitud s : repositorioSolicitudes.buscarTodos()) {
            if (s instanceof IncidenteSeguridad) {
                total++;
                IncidenteSeguridad inc = (IncidenteSeguridad) s;
                sb.append("- ").append(inc.getTitulo())
                        .append(inc.isUrgencia() ? " [URGENTE]" : "")
                        .append(" [").append(inc.getEstado() != null ? inc.getEstado().getNombre() : "?").append("]\n");
            }
        }
        return "Total de incidentes: " + total + "\n" + sb;
    }

    private String contenidoAccesos() {
        int total = 0;
        StringBuilder sb = new StringBuilder();
        for (RegistroAcceso a : repositorioAccesos.buscarTodos()) {
            total++;
            String actor = a.getActor() != null ? a.getActor().getNombreCompleto() : "?";
            sb.append("- ").append(actor)
                    .append(a.isPermitido() ? " (permitido)" : " (denegado)")
                    .append(" | tipo=").append(a.getTipo() != null ? a.getTipo() : "-")
                    .append(" | ingreso=").append(a.getFechaHoraIngreso())
                    .append(" | egreso=").append(a.getFechaHoraEgreso() != null ? a.getFechaHoraEgreso() : "(sin egreso)")
                    .append("\n");
        }
        return "Total de accesos: " + total + "\n" + sb;
    }
}
