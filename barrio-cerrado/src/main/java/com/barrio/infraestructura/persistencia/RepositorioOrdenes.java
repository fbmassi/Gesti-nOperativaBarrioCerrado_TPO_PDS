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

import com.barrio.dominio.mantenimiento.OrdenDeTrabajo;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioOrdenes implements Repositorio<OrdenDeTrabajo> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();
    private final RepositorioTareas repoTareas = new RepositorioTareas();

    @Override
    public void guardar(OrdenDeTrabajo entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
        vincularTareas(entidad);
    }

    @Override
    public OrdenDeTrabajo buscarPorId(Long id) {
        String sql = "SELECT * FROM ORDENES_TRABAJO WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearOrden(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar orden de trabajo por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<OrdenDeTrabajo> buscarTodos() {
        String sql = "SELECT * FROM ORDENES_TRABAJO";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<OrdenDeTrabajo> ordenes = new ArrayList<>();
                while (rs.next()) {
                    ordenes.add(mapearOrden(rs));
                }
                return ordenes;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar ordenes de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            // Desvincula las tareas antes de borrar la orden para no violar la FK.
            try (PreparedStatement ps = conn.prepareStatement("UPDATE TAREAS SET ORDEN_ID = NULL WHERE ORDEN_ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM ORDENES_TRABAJO WHERE ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar orden de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(OrdenDeTrabajo entidad) {
        String sql = "INSERT INTO ORDENES_TRABAJO (ADMINISTRADOR_ID, PROVEEDOR_ID, FECHA_EMISION) "
                + "VALUES (?, ?, ?)";
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
            throw new RuntimeException("Error al insertar orden de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(OrdenDeTrabajo entidad) {
        String sql = "UPDATE ORDENES_TRABAJO SET ADMINISTRADOR_ID=?, PROVEEDOR_ID=?, FECHA_EMISION=? WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(4, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar orden de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, OrdenDeTrabajo entidad) throws SQLException {
        if (entidad.getAdministrador() != null && entidad.getAdministrador().getId() != null) {
            ps.setLong(1, entidad.getAdministrador().getId());
        } else {
            ps.setNull(1, Types.BIGINT);
        }
        if (entidad.getProveedor() != null && entidad.getProveedor().getId() != null) {
            ps.setLong(2, entidad.getProveedor().getId());
        } else {
            ps.setNull(2, Types.BIGINT);
        }
        ps.setTimestamp(3, entidad.getFechaEmision() != null
                ? Timestamp.valueOf(entidad.getFechaEmision()) : null);
    }

    /** Vincula cada tarea (ya persistida) de la orden mediante TAREAS.ORDEN_ID. */
    private void vincularTareas(OrdenDeTrabajo entidad) {
        if (entidad.getTareas() == null || entidad.getId() == null) {
            return;
        }
        String sql = "UPDATE TAREAS SET ORDEN_ID = ? WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (TareaDeMantenimiento tarea : entidad.getTareas()) {
                    if (tarea.getId() != null) {
                        ps.setLong(1, entidad.getId());
                        ps.setLong(2, tarea.getId());
                        ps.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al vincular tareas a la orden de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private OrdenDeTrabajo mapearOrden(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");

        long adminId = rs.getLong("ADMINISTRADOR_ID");
        Administrador admin = rs.wasNull() ? null
                : (Administrador) repoPersonas.buscarPorId(adminId);

        long provId = rs.getLong("PROVEEDOR_ID");
        Proveedor proveedor = rs.wasNull() ? null
                : (Proveedor) repoPersonas.buscarPorId(provId);

        Timestamp ts = rs.getTimestamp("FECHA_EMISION");
        List<TareaDeMantenimiento> tareas = cargarTareas(id);

        return new OrdenDeTrabajo(id, admin, proveedor, tareas,
                ts != null ? ts.toLocalDateTime() : null);
    }

    private List<TareaDeMantenimiento> cargarTareas(Long ordenId) {
        List<TareaDeMantenimiento> tareas = new ArrayList<>();
        String sql = "SELECT ID FROM TAREAS WHERE ORDEN_ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, ordenId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        TareaDeMantenimiento tarea = repoTareas.buscarPorId(rs.getLong("ID"));
                        if (tarea != null) {
                            tareas.add(tarea);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar las tareas de la orden de trabajo", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
        return tareas;
    }
}
