package com.barrio.infraestructura.persistencia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.barrio.dominio.estructura.Vivienda;
import com.barrio.dominio.personas.Persona;
import com.barrio.dominio.personas.Residente;
import com.barrio.infraestructura.bd.ConexionH2;

public class RepositorioViviendas implements Repositorio<Vivienda> {

    private final RepositorioPersonas repoPersonas = new RepositorioPersonas();

    @Override
    public void guardar(Vivienda entidad) {
        if (entidad.getId() == null) {
            insertar(entidad);
        } else {
            actualizar(entidad);
        }
    }

    @Override
    public Vivienda buscarPorId(Long id) {
        String sql = "SELECT * FROM VIVIENDAS WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapear(rs);
                    }
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar vivienda por ID", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public List<Vivienda> buscarTodos() {
        String sql = "SELECT * FROM VIVIENDAS";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<Vivienda> viviendas = new ArrayList<>();
                while (rs.next()) {
                    viviendas.add(mapear(rs));
                }
                return viviendas;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar viviendas", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    @Override
    public void eliminar(Long id) {
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE PERSONAS SET VIVIENDA_ID = NULL WHERE VIVIENDA_ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM VIVIENDAS WHERE ID = ?")) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar vivienda", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    /** Asocia un residente a una vivienda (setea PERSONAS.VIVIENDA_ID). */
    public void asociarResidente(Long viviendaId, Long residenteId) {
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement("UPDATE PERSONAS SET VIVIENDA_ID = ? WHERE ID = ?")) {
                ps.setLong(1, viviendaId);
                ps.setLong(2, residenteId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al asociar residente a vivienda", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void insertar(Vivienda entidad) {
        String sql = "INSERT INTO VIVIENDAS (NUMERO) VALUES (?)";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, entidad.getNumero());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        entidad.setId(keys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar vivienda", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private void actualizar(Vivienda entidad) {
        String sql = "UPDATE VIVIENDAS SET NUMERO = ? WHERE ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, entidad.getNumero());
                ps.setLong(2, entidad.getId());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar vivienda", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
    }

    private Vivienda mapear(ResultSet rs) throws SQLException {
        Long id = rs.getLong("ID");
        Vivienda vivienda = new Vivienda();
        vivienda.setId(id);
        vivienda.setNumero(rs.getString("NUMERO"));
        vivienda.setResidentes(cargarResidentes(id));
        return vivienda;
    }

    private List<Residente> cargarResidentes(Long viviendaId) {
        List<Residente> residentes = new ArrayList<>();
        String sql = "SELECT ID FROM PERSONAS WHERE VIVIENDA_ID = ?";
        Connection conn = null;
        try {
            conn = ConexionH2.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, viviendaId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Persona p = repoPersonas.buscarPorId(rs.getLong("ID"));
                        if (p instanceof Residente) {
                            residentes.add((Residente) p);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al cargar residentes de la vivienda", e);
        } finally {
            ConexionH2.liberarConexion(conn);
        }
        return residentes;
    }
}
