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

import com.barrio.dominio.acceso.RegistroAcceso;
import com.barrio.dominio.acceso.TipoAcceso;
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
        return buscarUno("SELECT * FROM ACCESOS WHERE ID = ?", ps -> ps.setLong(1, id));
    }

    /** Último ingreso de la persona que todavía no tiene egreso registrado. */
    public RegistroAcceso buscarAbiertoPorActor(String dni) {
        String sql = "SELECT a.* FROM ACCESOS a JOIN PERSONAS p ON a.ACTOR_ID = p.ID "
                + "WHERE p.DNI = ? AND a.FECHA_HORA_EGRESO IS NULL AND a.PERMITIDO = TRUE ORDER BY a.ID DESC";
        return buscarUno(sql, ps -> ps.setString(1, dni));
    }

    @Override
    public List<RegistroAcceso> buscarTodos() {
        String sql = "SELECT * FROM ACCESOS ORDER BY ID";
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
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ACCESOS WHERE ID = ?")) {
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
        String sql = "INSERT INTO ACCESOS (ACTOR_ID, TIPO, FECHA_HORA_INGRESO, FECHA_HORA_EGRESO, PERMITIDO) "
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
        String sql = "UPDATE ACCESOS SET ACTOR_ID=?, TIPO=?, FECHA_HORA_INGRESO=?, FECHA_HORA_EGRESO=?, PERMITIDO=? "
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
        ps.setString(2, entidad.getTipo() != null ? entidad.getTipo().name() : TipoAcceso.VISITANTE.name());
        ps.setTimestamp(3, entidad.getFechaHoraIngreso() != null
                ? Timestamp.valueOf(entidad.getFechaHoraIngreso()) : null);
        ps.setTimestamp(4, entidad.getFechaHoraEgreso() != null
                ? Timestamp.valueOf(entidad.getFechaHoraEgreso()) : null);
        ps.setBoolean(5, entidad.isPermitido());
    }

    private RegistroAcceso mapearAcceso(ResultSet rs) throws SQLException {
        long actorId = rs.getLong("ACTOR_ID");
        Persona actor = rs.wasNull() ? null : repoPersonas.buscarPorId(actorId);
        Timestamp tsIngreso = rs.getTimestamp("FECHA_HORA_INGRESO");
        Timestamp tsEgreso = rs.getTimestamp("FECHA_HORA_EGRESO");
        return new RegistroAcceso(
                rs.getLong("ID"),
                actor,
                TipoAcceso.valueOf(rs.getString("TIPO")),
                tsIngreso != null ? tsIngreso.toLocalDateTime() : null,
                tsEgreso != null ? tsEgreso.toLocalDateTime() : null,
                rs.getBoolean("PERMITIDO")
        );
    }

    private interface Setter {
        void set(PreparedStatement ps) throws SQLException;
    }

    private RegistroAcceso buscarUno(String sql, Setter setter) {
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setter.set(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? mapearAcceso(rs) : null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar acceso", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }
}
