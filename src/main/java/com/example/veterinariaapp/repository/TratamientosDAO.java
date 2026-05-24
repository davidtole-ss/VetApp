package com.example.veterinariaapp.repository;

import com.example.veterinariaapp.model.Tratamientos;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TratamientosDAO {

    private final DataSource dataSource;

    public TratamientosDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Insertar un tratamiento nuevo
    public boolean insertarTratamiento(Tratamientos t) {
        String sql = "INSERT INTO tratamientos (nombre, descripcion, duracion_dias, precio) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getDuracion_dias());
            ps.setDouble(4, t.getPrecio());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los tratamientos
    public List<Tratamientos> listarTratamientos() {
        List<Tratamientos> lista = new ArrayList<>();
        String sql = "SELECT * FROM tratamientos";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tratamientos t = new Tratamientos(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("duracion_dias"),
                        rs.getDouble("precio")
                );
                lista.add(t);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Modificar un tratamiento existente
    public boolean actualizarTratamiento(Tratamientos t) {
        String sql = "UPDATE tratamientos SET nombre = ?, descripcion = ?, duracion_dias = ?, precio = ? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, t.getNombre());
            ps.setString(2, t.getDescripcion());
            ps.setInt(3, t.getDuracion_dias());
            ps.setDouble(4, t.getPrecio());
            ps.setInt(5, t.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un tratamiento por su id
    public boolean eliminarTratamiento(int id) {
        String sql = "DELETE FROM tratamientos WHERE id = ?";

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
