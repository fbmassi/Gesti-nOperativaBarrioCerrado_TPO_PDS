package com.barrio.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.barrio.dominio.personas.Administrador;
import com.barrio.dominio.personas.Guardia;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Proveedor;
import com.barrio.dominio.personas.Residente;
import com.barrio.dominio.personas.Visitante;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioPersonas implements Repositorio<Persona> {

    @Override
    public void guardar(Persona entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public Persona buscarPorId(Long id) {
        String sql = "SELECT * FROM PERSONAS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearPersona(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    /**
     * Busca una persona por su email. Usado para el login. Retorna null si no existe.
     */
    public Persona buscarPorEmail(String email) {
        String sql = "SELECT * FROM PERSONAS WHERE EMAIL = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearPersona(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar persona por email", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<Persona> buscarTodos() {
        String sql = "SELECT * FROM PERSONAS";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<Persona> personas = new ArrayList<>();
                while (rs.next()) {
                    personas.add(mapearPersona(rs));
                }
                return personas;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar personas", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        String sql = "DELETE FROM PERSONAS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar persona", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(Persona entidad) {
        String sql = "INSERT INTO PERSONAS (TIPO, NOMBRE, APELLIDO, DNI, EMAIL, PASSWORD, "
                + "NUMERO_LOTE, LEGAJO, ESPECIALIDAD, RESIDENTE_AUTORIZANTE_ID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            throw new RuntimeException("Error al insertar persona", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(Persona entidad) {
        String sql = "UPDATE PERSONAS SET TIPO=?, NOMBRE=?, APELLIDO=?, DNI=?, EMAIL=?, PASSWORD=?, "
                + "NUMERO_LOTE=?, LEGAJO=?, ESPECIALIDAD=?, RESIDENTE_AUTORIZANTE_ID=? "
                + "WHERE ID=?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                setearParametros(ps, entidad);
                ps.setLong(11, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar persona", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void setearParametros(PreparedStatement ps, Persona entidad) throws SQLException {
        ps.setString(1, obtenerTipo(entidad));
        ps.setString(2, entidad.getNombre());
        ps.setString(3, entidad.getApellido());
        ps.setString(4, entidad.getDni());
        ps.setString(5, entidad.getEmail());
        ps.setString(6, entidad.getPassword());

        if (entidad instanceof Residente) {
            ps.setInt(7, ((Residente) entidad).getNumeroLote());
        } else {
            ps.setNull(7, Types.INTEGER);
        }

        if (entidad instanceof Administrador) {
            ps.setString(8, ((Administrador) entidad).getLegajo());
        } else if (entidad instanceof Guardia) {
            ps.setString(8, ((Guardia) entidad).getLegajo());
        } else {
            ps.setNull(8, Types.VARCHAR);
        }

        if (entidad instanceof Proveedor) {
            ps.setString(9, ((Proveedor) entidad).getEspecialidad());
        } else {
            ps.setNull(9, Types.VARCHAR);
        }

        if (entidad instanceof Visitante) {
            Residente autorizante = ((Visitante) entidad).getResidenteAutorizante();
            if (autorizante != null && autorizante.getId() != null) {
                ps.setLong(10, autorizante.getId());
            } else {
                ps.setNull(10, Types.BIGINT);
            }
        } else {
            ps.setNull(10, Types.BIGINT);
        }
    }

    private String obtenerTipo(Persona entidad) {
        if (entidad instanceof Residente) return "RESIDENTE";
        if (entidad instanceof Administrador) return "ADMINISTRADOR";
        if (entidad instanceof Guardia) return "GUARDIA";
        if (entidad instanceof Proveedor) return "PROVEEDOR";
        if (entidad instanceof Visitante) return "VISITANTE";
        throw new IllegalArgumentException("Tipo de persona desconocido: " + entidad.getClass());
    }

    Persona mapearPersona(ResultSet rs) throws SQLException {
        String tipo = rs.getString("TIPO");
        Persona persona;

        switch (tipo) {
            case "RESIDENTE":
                persona = new Residente(rs.getInt("NUMERO_LOTE"));
                break;
            case "ADMINISTRADOR":
                persona = new Administrador(rs.getString("LEGAJO"));
                break;
            case "GUARDIA":
                persona = new Guardia(rs.getString("LEGAJO"));
                break;
            case "PROVEEDOR":
                persona = new Proveedor(rs.getString("ESPECIALIDAD"));
                break;
            case "VISITANTE":
                long autId = rs.getLong("RESIDENTE_AUTORIZANTE_ID");
                Residente autorizante = rs.wasNull() ? null
                        : (Residente) buscarPorId(autId);
                persona = new Visitante(autorizante);
                break;
            default:
                throw new SQLException("Tipo de persona desconocido en BD: " + tipo);
        }

        persona.setId(rs.getLong("ID"));
        persona.setNombre(rs.getString("NOMBRE"));
        persona.setApellido(rs.getString("APELLIDO"));
        persona.setDni(rs.getString("DNI"));
        persona.setEmail(rs.getString("EMAIL"));
        persona.setPassword(rs.getString("PASSWORD"));
        return persona;
    }
}
