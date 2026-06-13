package com.barrio.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.barrio.dominio.categorias.CategoriaSolicitud;
import com.barrio.dominio.categorias.PrioridadSolicitud;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.reclamos.Cerrado;
import com.barrio.dominio.reclamos.EnProceso;
import com.barrio.dominio.reclamos.EstadoSolicitud;
import com.barrio.dominio.reclamos.IncidenteSeguridad;
import com.barrio.dominio.reclamos.Pendiente;
import com.barrio.dominio.reclamos.Reclamo;
import com.barrio.dominio.reclamos.Resuelto;
import com.barrio.dominio.reclamos.Solicitud;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioSolicitudes implements Repositorio<Solicitud> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(Solicitud entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public Optional<Solicitud> buscarPorId(Long id) {
        String sql = "SELECT * FROM SOLICITUDES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapearSolicitud(rs));
                    }
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar solicitud por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<Solicitud> buscarTodos() {
        String sql = "SELECT * FROM SOLICITUDES";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<Solicitud> solicitudes = new ArrayList<>();
                while (rs.next()) {
                    solicitudes.add(mapearSolicitud(rs));
                }
                return solicitudes;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar solicitudes", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM SOLICITUDES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar solicitud", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(Solicitud entidad) {
        String sql = "INSERT INTO SOLICITUDES (TIPO, TITULO, DESCRIPCION, FECHA_CREACION, ESTADO, "
                + "CATEGORIA, PRIORIDAD, RESIDENTE_ID, ADMINISTRADOR_ID, GUARDIA_ID, URGENCIA) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setearParametros(ps, entidad);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        entidad.setId(keys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar solicitud", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(Solicitud entidad) {
        String sql = "UPDATE SOLICITUDES SET TIPO=?, TITULO=?, DESCRIPCION=?, FECHA_CREACION=?, ESTADO=?, "
                + "CATEGORIA=?, PRIORIDAD=?, RESIDENTE_ID=?, ADMINISTRADOR_ID=?, GUARDIA_ID=?, URGENCIA=? "
                + "WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(12, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar solicitud", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, Solicitud entidad) throws SQLException {
        ps.setString(1, obtenerTipo(entidad));
        ps.setString(2, entidad.getTitulo());
        ps.setString(3, entidad.getDescripcion());
        ps.setTimestamp(4, entidad.getFechaCreacion() != null
                ? Timestamp.valueOf(entidad.getFechaCreacion()) : Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(5, estadoAString(entidad.getEstado()));
        if (entidad.getCategoria() != null) {
            ps.setString(6, entidad.getCategoria().name());
        } else {
            ps.setNull(6, Types.VARCHAR);
        }
        if (entidad.getPrioridad() != null) {
            ps.setString(7, entidad.getPrioridad().name());
        } else {
            ps.setNull(7, Types.VARCHAR);
        }

        if (entidad instanceof Reclamo) {
            Reclamo r = (Reclamo) entidad;
            if (r.getResidente() != null && r.getResidente().getId() != null) {
                ps.setLong(8, r.getResidente().getId());
            } else {
                ps.setNull(8, Types.BIGINT);
            }
            if (r.getAdministrador() != null && r.getAdministrador().getId() != null) {
                ps.setLong(9, r.getAdministrador().getId());
            } else {
                ps.setNull(9, Types.BIGINT);
            }
            ps.setNull(10, Types.BIGINT);
            ps.setNull(11, Types.BOOLEAN);
        } else if (entidad instanceof IncidenteSeguridad) {
            IncidenteSeguridad inc = (IncidenteSeguridad) entidad;
            ps.setNull(8, Types.BIGINT);
            ps.setNull(9, Types.BIGINT);
            if (inc.getGuardia() != null && inc.getGuardia().getId() != null) {
                ps.setLong(10, inc.getGuardia().getId());
            } else {
                ps.setNull(10, Types.BIGINT);
            }
            ps.setBoolean(11, inc.isUrgencia());
        } else {
            ps.setNull(8, Types.BIGINT);
            ps.setNull(9, Types.BIGINT);
            ps.setNull(10, Types.BIGINT);
            ps.setNull(11, Types.BOOLEAN);
        }
    }

    private Solicitud mapearSolicitud(ResultSet rs) throws SQLException {
        String tipo = rs.getString("TIPO");
        Solicitud solicitud;

        switch (tipo) {
            case "RECLAMO":
                long resId = rs.getLong("RESIDENTE_ID");
                Residente residente = rs.wasNull() ? null
                        : (Residente) repoPersonas.buscarPorId(resId).orElse(null);
                long admId = rs.getLong("ADMINISTRADOR_ID");
                Administrador admin = rs.wasNull() ? null
                        : (Administrador) repoPersonas.buscarPorId(admId).orElse(null);
                solicitud = new Reclamo(residente, admin);
                break;
            case "INCIDENTE_SEGURIDAD":
                long guaId = rs.getLong("GUARDIA_ID");
                Guardia guardia = rs.wasNull() ? null
                        : (Guardia) repoPersonas.buscarPorId(guaId).orElse(null);
                solicitud = new IncidenteSeguridad(guardia, rs.getBoolean("URGENCIA"));
                break;
            default:
                throw new SQLException("Tipo de solicitud desconocido en BD: " + tipo);
        }

        solicitud.setId(rs.getLong("ID"));
        solicitud.setTitulo(rs.getString("TITULO"));
        solicitud.setDescripcion(rs.getString("DESCRIPCION"));
        Timestamp ts = rs.getTimestamp("FECHA_CREACION");
        solicitud.setFechaCreacion(ts != null ? ts.toLocalDateTime() : null);
        solicitud.setEstado(stringAEstado(rs.getString("ESTADO")));
        String cat = rs.getString("CATEGORIA");
        if (cat != null) {
            try { solicitud.setCategoria(CategoriaSolicitud.valueOf(cat)); } catch (IllegalArgumentException ignored) {}
        }
        String pri = rs.getString("PRIORIDAD");
        if (pri != null) {
            try { solicitud.setPrioridad(PrioridadSolicitud.valueOf(pri)); } catch (IllegalArgumentException ignored) {}
        }
        return solicitud;
    }

    private String obtenerTipo(Solicitud entidad) {
        if (entidad instanceof Reclamo) return "RECLAMO";
        if (entidad instanceof IncidenteSeguridad) return "INCIDENTE_SEGURIDAD";
        throw new IllegalArgumentException("Tipo de solicitud desconocido: " + entidad.getClass());
    }

    static String estadoAString(EstadoSolicitud estado) {
        if (estado == null) return "PENDIENTE";
        if (estado instanceof Pendiente) return "PENDIENTE";
        if (estado instanceof EnProceso) return "EN_PROCESO";
        if (estado instanceof Resuelto) return "RESUELTO";
        if (estado instanceof Cerrado) return "CERRADO";
        return "PENDIENTE";
    }

    static EstadoSolicitud stringAEstado(String estado) {
        if (estado == null) return new Pendiente();
        switch (estado) {
            case "EN_PROCESO": return new EnProceso();
            case "RESUELTO":   return new Resuelto();
            case "CERRADO":    return new Cerrado();
            default:           return new Pendiente();
        }
    }
}
