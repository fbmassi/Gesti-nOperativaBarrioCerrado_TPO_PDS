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

import com.barrio.dominio.mantenimiento.EstadoTarea;
import com.barrio.dominio.mantenimiento.TareaCompuesta;
import com.barrio.dominio.mantenimiento.TareaDeMantenimiento;
import com.barrio.dominio.mantenimiento.TareaSimple;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioTareas implements Repositorio<TareaDeMantenimiento> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(TareaDeMantenimiento entidad) {
        guardarRecursivo(entidad, null);
    }

    @Override
    public TareaDeMantenimiento buscarPorId(Long id) {
        String sql = "SELECT * FROM TAREAS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearTarea(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tarea por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<TareaDeMantenimiento> buscarTodos() {
        String sql = "SELECT * FROM TAREAS WHERE TAREA_PADRE_ID IS NULL";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<TareaDeMantenimiento> tareas = new ArrayList<>();
                while (rs.next()) {
                    tareas.add(mapearTarea(rs));
                }
                return tareas;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tareas", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TAREAS WHERE TAREA_PADRE_ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM TAREAS WHERE ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tarea", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void guardarRecursivo(TareaDeMantenimiento entidad, Long padreId) {
        if (entidad.getId() == null) {
            insertar(entidad, padreId);
        } else {
            actualizar(entidad, padreId);
        }

        if (entidad instanceof TareaCompuesta) {
            TareaCompuesta compuesta = (TareaCompuesta) entidad;
            if (compuesta.getSubtareas() != null) {
                for (TareaDeMantenimiento subtarea : compuesta.getSubtareas()) {
                    guardarRecursivo(subtarea, entidad.getId());
                }
            }
        }
    }

    private void insertar(TareaDeMantenimiento entidad, Long padreId) {
        String sql = "INSERT INTO TAREAS (TIPO, TITULO, DESCRIPCION, ESTADO, FECHA_CREACION, "
                + "PROVEEDOR_ID, TAREA_PADRE_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                setearParametros(ps, entidad, padreId);
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        entidad.setId(keys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar tarea", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(TareaDeMantenimiento entidad, Long padreId) {
        String sql = "UPDATE TAREAS SET TIPO=?, TITULO=?, DESCRIPCION=?, ESTADO=?, FECHA_CREACION=?, "
                + "PROVEEDOR_ID=?, TAREA_PADRE_ID=? WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad, padreId);
                ps.setLong(8, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar tarea", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, TareaDeMantenimiento entidad, Long padreId)
            throws SQLException {
        ps.setString(1, entidad instanceof TareaCompuesta ? "COMPUESTA" : "SIMPLE");
        ps.setString(2, entidad.getTitulo());
        ps.setString(3, entidad.getDescripcion());
        ps.setString(4, estadoTareaAString(entidad.getEstado()));
        ps.setTimestamp(5, entidad.getFechaCreacion() != null
                ? Timestamp.valueOf(entidad.getFechaCreacion()) : null);

        if (entidad instanceof TareaSimple) {
            Proveedor prov = ((TareaSimple) entidad).getPersonalAsignado();
            if (prov != null && prov.getId() != null) {
                ps.setLong(6, prov.getId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
        } else {
            ps.setNull(6, Types.BIGINT);
        }

        if (padreId != null) {
            ps.setLong(7, padreId);
        } else {
            ps.setNull(7, Types.BIGINT);
        }
    }

    private TareaDeMantenimiento mapearTarea(ResultSet rs) throws SQLException {
        String tipo = rs.getString("TIPO");
        TareaDeMantenimiento tarea;

        if ("COMPUESTA".equals(tipo)) {
            List<TareaDeMantenimiento> subtareas = cargarSubtareas(rs.getLong("ID"));
            tarea = new TareaCompuesta(subtareas);
        } else {
            long provId = rs.getLong("PROVEEDOR_ID");
            Proveedor proveedor = rs.wasNull() ? null
                    : (Proveedor) repoPersonas.buscarPorId(provId);
            tarea = new TareaSimple(proveedor);
        }

        tarea.setId(rs.getLong("ID"));
        tarea.setTitulo(rs.getString("TITULO"));
        tarea.setDescripcion(rs.getString("DESCRIPCION"));
        tarea.setEstado(stringAEstadoTarea(rs.getString("ESTADO")));
        Timestamp ts = rs.getTimestamp("FECHA_CREACION");
        tarea.setFechaCreacion(ts != null ? ts.toLocalDateTime() : null);
        return tarea;
    }

    private List<TareaDeMantenimiento> cargarSubtareas(Long padreId) {
        String sql = "SELECT * FROM TAREAS WHERE TAREA_PADRE_ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, padreId);
                try (ResultSet rs = ps.executeQuery()) {
                    List<TareaDeMantenimiento> subtareas = new ArrayList<>();
                    while (rs.next()) {
                        subtareas.add(mapearTarea(rs));
                    }
                    return subtareas;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar subtareas", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    static String estadoTareaAString(EstadoTarea estado) {
        if (estado == null) return "PENDIENTE";
        return estado.name();
    }

    static EstadoTarea stringAEstadoTarea(String estado) {
        if (estado == null) return EstadoTarea.PENDIENTE;
        try {
            return EstadoTarea.valueOf(estado);
        } catch (IllegalArgumentException e) {
            return EstadoTarea.PENDIENTE;
        }
    }
}
