package com.barrio.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.barrio.dominio.notificaciones.CanalEmail;
import com.barrio.dominio.notificaciones.CanalSMS;
import com.barrio.dominio.notificaciones.EstrategiaNotificacion;
import com.barrio.dominio.notificaciones.Notificacion;
import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioNotificaciones implements Repositorio<Notificacion> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(Notificacion entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public Notificacion buscarPorId(Long id) {
        String sql = "SELECT * FROM NOTIFICACIONES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearNotificacion(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar notificacion por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<Notificacion> buscarTodos() {
        String sql = "SELECT * FROM NOTIFICACIONES";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<Notificacion> notificaciones = new ArrayList<>();
                while (rs.next()) {
                    notificaciones.add(mapearNotificacion(rs));
                }
                return notificaciones;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar notificaciones", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM NOTIFICACIONES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar notificacion", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(Notificacion entidad) {
        String sql = "INSERT INTO NOTIFICACIONES (DESTINATARIO_ID, MENSAJE, FECHA_ENVIO, CANAL) "
                + "VALUES (?, ?, ?, ?)";
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
            throw new RuntimeException("Error al insertar notificacion", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(Notificacion entidad) {
        String sql = "UPDATE NOTIFICACIONES SET DESTINATARIO_ID=?, MENSAJE=?, FECHA_ENVIO=?, CANAL=? "
                + "WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(5, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar notificacion", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, Notificacion entidad) throws SQLException {
        if (entidad.getDestinatario() != null && entidad.getDestinatario().getId() != null) {
            ps.setLong(1, entidad.getDestinatario().getId());
        } else {
            ps.setNull(1, Types.BIGINT);
        }

        ps.setString(2, entidad.getMensaje());
        ps.setTimestamp(3, entidad.getFechaEnvio() != null
                ? Timestamp.valueOf(entidad.getFechaEnvio()) : null);
        ps.setString(4, canalAString(entidad.getCanal()));
    }

    private Notificacion mapearNotificacion(ResultSet rs) throws SQLException {
        long destId = rs.getLong("DESTINATARIO_ID");
        Persona destinatario = rs.wasNull() ? null
                : repoPersonas.buscarPorId(destId);

        Timestamp ts = rs.getTimestamp("FECHA_ENVIO");

        return new Notificacion(
                rs.getLong("ID"),
                destinatario,
                rs.getString("MENSAJE"),
                ts != null ? ts.toLocalDateTime() : null,
                stringACanal(rs.getString("CANAL"))
        );
    }

    private String canalAString(EstrategiaNotificacion canal) {
        if (canal instanceof CanalEmail) return "EMAIL";
        if (canal instanceof CanalSMS) return "SMS";
        return "EMAIL";
    }

    private EstrategiaNotificacion stringACanal(String canal) {
        if (canal == null) return new CanalEmail();
        switch (canal) {
            case "SMS": return new CanalSMS();
            default:    return new CanalEmail();
        }
    }
}
