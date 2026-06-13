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

import com.barrio.aplicacion.trazabilidad.EntradaHistorial;
import com.barrio.dominio.personas.Persona;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioHistorial implements Repositorio<EntradaHistorial> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(EntradaHistorial entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public EntradaHistorial buscarPorId(Long id) {
        String sql = "SELECT * FROM HISTORIAL_ACCIONES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearEntrada(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar entrada de historial por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<EntradaHistorial> buscarTodos() {
        String sql = "SELECT * FROM HISTORIAL_ACCIONES";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<EntradaHistorial> entradas = new ArrayList<>();
                while (rs.next()) {
                    entradas.add(mapearEntrada(rs));
                }
                return entradas;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar historial de acciones", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM HISTORIAL_ACCIONES WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar entrada de historial", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(EntradaHistorial entidad) {
        String sql = "INSERT INTO HISTORIAL_ACCIONES (FECHA, AUTOR_ID, DESCRIPCION) VALUES (?, ?, ?)";
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
            throw new RuntimeException("Error al insertar entrada de historial", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(EntradaHistorial entidad) {
        String sql = "UPDATE HISTORIAL_ACCIONES SET FECHA=?, AUTOR_ID=?, DESCRIPCION=? WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(4, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar entrada de historial", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, EntradaHistorial entidad) throws SQLException {
        ps.setTimestamp(1, entidad.getFecha() != null
                ? Timestamp.valueOf(entidad.getFecha()) : null);
        if (entidad.getAutor() != null && entidad.getAutor().getId() != null) {
            ps.setLong(2, entidad.getAutor().getId());
        } else {
            ps.setNull(2, Types.BIGINT);
        }
        ps.setString(3, entidad.getDescripcion());
    }

    private EntradaHistorial mapearEntrada(ResultSet rs) throws SQLException {
        long autorId = rs.getLong("AUTOR_ID");
        Persona autor = rs.wasNull() ? null
                : repoPersonas.buscarPorId(autorId);

        Timestamp ts = rs.getTimestamp("FECHA");

        EntradaHistorial entrada = new EntradaHistorial();
        entrada.setId(rs.getLong("ID"));
        entrada.setFecha(ts != null ? ts.toLocalDateTime() : null);
        entrada.setAutor(autor);
        entrada.setDescripcion(rs.getString("DESCRIPCION"));
        return entrada;
    }
}
