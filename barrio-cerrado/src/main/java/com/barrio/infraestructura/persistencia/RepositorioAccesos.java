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

import com.barrio.dominio.acceso.ProtocoloAcceso;
import com.barrio.dominio.acceso.ProtocoloEmergencia;
import com.barrio.dominio.acceso.ProtocoloFamiliar;
import com.barrio.dominio.acceso.ProtocoloProveedor;
import com.barrio.dominio.acceso.ProtocoloVisitante;
import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioAccesos implements Repositorio<RegistroAcceso> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(RegistroAcceso entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public RegistroAcceso buscarPorId(Long id) {
        String sql = "SELECT * FROM ACCESOS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearAcceso(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar acceso por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<RegistroAcceso> buscarTodos() {
        String sql = "SELECT * FROM ACCESOS";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<RegistroAcceso> accesos = new ArrayList<>();
                while (rs.next()) {
                    accesos.add(mapearAcceso(rs));
                }
                return accesos;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar accesos", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM ACCESOS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar acceso", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(RegistroAcceso entidad) {
        String sql = "INSERT INTO ACCESOS (ACTOR_ID, FECHA_HORA_INGRESO, FECHA_HORA_EGRESO, PROTOCOLO, PERMITIDO) "
                + "VALUES (?, ?, ?, ?, ?)";
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
            throw new RuntimeException("Error al insertar acceso", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(RegistroAcceso entidad) {
        String sql = "UPDATE ACCESOS SET ACTOR_ID=?, FECHA_HORA_INGRESO=?, FECHA_HORA_EGRESO=?, PROTOCOLO=?, PERMITIDO=? "
                + "WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(6, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar acceso", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, RegistroAcceso entidad) throws SQLException {
        if (entidad.getActor() != null && entidad.getActor().getId() != null) {
            ps.setLong(1, entidad.getActor().getId());
        } else {
            ps.setNull(1, Types.BIGINT);
        }

        ps.setTimestamp(2, entidad.getFechaHoraIngreso() != null
                ? Timestamp.valueOf(entidad.getFechaHoraIngreso()) : null);
        ps.setTimestamp(3, entidad.getFechaHoraEgreso() != null
                ? Timestamp.valueOf(entidad.getFechaHoraEgreso()) : null);
        ps.setString(4, protocoloAString(entidad.getProtocolo()));
        ps.setBoolean(5, entidad.isPermitido());
    }

    private RegistroAcceso mapearAcceso(ResultSet rs) throws SQLException {
        long actorId = rs.getLong("ACTOR_ID");
        Persona actor = rs.wasNull() ? null
                : repoPersonas.buscarPorId(actorId);

        Timestamp tsIngreso = rs.getTimestamp("FECHA_HORA_INGRESO");
        Timestamp tsEgreso = rs.getTimestamp("FECHA_HORA_EGRESO");

        RegistroAcceso acceso = new RegistroAcceso(
                rs.getLong("ID"),
                actor,
                tsIngreso != null ? tsIngreso.toLocalDateTime() : null,
                tsEgreso != null ? tsEgreso.toLocalDateTime() : null,
                stringAProtocolo(rs.getString("PROTOCOLO")),
                rs.getBoolean("PERMITIDO")
        );
        return acceso;
    }

    private String protocoloAString(ProtocoloAcceso protocolo) {
        if (protocolo instanceof ProtocoloFamiliar) return "FAMILIAR";
        if (protocolo instanceof ProtocoloVisitante) return "VISITANTE";
        if (protocolo instanceof ProtocoloProveedor) return "PROVEEDOR";
        if (protocolo instanceof ProtocoloEmergencia) return "EMERGENCIA";
        return "VISITANTE";
    }

    private ProtocoloAcceso stringAProtocolo(String protocolo) {
        if (protocolo == null) return new ProtocoloVisitante();
        switch (protocolo) {
            case "FAMILIAR":    return new ProtocoloFamiliar();
            case "PROVEEDOR":   return new ProtocoloProveedor();
            case "EMERGENCIA":  return new ProtocoloEmergencia();
            default:            return new ProtocoloVisitante();
        }
    }
}
