package com.example.veterinariaapp.repository;

import com.example.veterinariaapp.model.Consultas;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsultasDAO {

    private final DataSource dataSource;

    public ConsultasDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Insertar una consulta nueva
    public boolean insertarConsulta(Consultas consulta) {
        String sql = "INSERT INTO consultas (fecha, motivo, precio, id_animal) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(consulta.getFecha()));
            ps.setString(2, consulta.getMotivo());
            ps.setDouble(3, consulta.getPrecio());
            ps.setInt(4, consulta.getId_animal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todas las consultas
    public List<Consultas> listarConsultas() {
        List<Consultas> lista = new ArrayList<>();
        String sql = "SELECT * FROM consultas";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Consultas c = new Consultas(
                        rs.getInt("id"),
                        rs.getDate("fecha").toLocalDate(),
                        rs.getString("motivo"),
                        rs.getDouble("precio"),
                        rs.getInt("id_animal")
                );
                lista.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Modificar una consulta existente
    public boolean actualizarConsulta(Consultas consulta) {
        String sql = "UPDATE consultas SET fecha = ?, motivo = ?, precio = ?, id_animal = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(consulta.getFecha()));
            ps.setString(2, consulta.getMotivo());
            ps.setDouble(3, consulta.getPrecio());
            ps.setInt(4, consulta.getId_animal());
            ps.setInt(5, consulta.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar una consulta por su id
    public boolean eliminarConsulta(int id) {
        String sql = "DELETE FROM consultas WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
