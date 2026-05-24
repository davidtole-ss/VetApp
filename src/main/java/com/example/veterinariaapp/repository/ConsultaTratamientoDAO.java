package com.example.veterinariaapp.repository;

import com.example.veterinariaapp.model.Tratamientos;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConsultaTratamientoDAO {

    private final DataSource dataSource;

    public ConsultaTratamientoDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Obtener los tratamientos asignados a una consulta
    public List<Tratamientos> listarTratamientosDeConsulta(int idConsulta) {
        List<Tratamientos> lista = new ArrayList<>();
        String sql = "SELECT t.* FROM tratamientos t " +
                     "JOIN consulta_tratamiento ct ON t.id = ct.id_tratamiento " +
                     "WHERE ct.id_consulta = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new Tratamientos(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getInt("duracion_dias"),
                        rs.getDouble("precio")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Asignar un tratamiento a una consulta
    public boolean asignarTratamiento(int idConsulta, int idTratamiento) {
        String sql = "INSERT INTO consulta_tratamiento (id_consulta, id_tratamiento) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ps.setInt(2, idTratamiento);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un tratamiento de una consulta
    public boolean eliminarTratamientoDeConsulta(int idConsulta, int idTratamiento) {
        String sql = "DELETE FROM consulta_tratamiento WHERE id_consulta = ? AND id_tratamiento = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idConsulta);
            ps.setInt(2, idTratamiento);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
