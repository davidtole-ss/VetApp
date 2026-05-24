package com.example.veterinariaapp.repository;

import com.example.veterinariaapp.model.Animales;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AnimalesDAO {

    private final DataSource dataSource;

    public AnimalesDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Insertar un animal nuevo
    public boolean insertarAnimal(Animales animal) {
        String sql = "INSERT INTO animales (nombre, especie, raza, edad, peso, imagen, id_cliente) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, animal.getNombre());
            ps.setString(2, animal.getEspecie());
            ps.setString(3, animal.getRaza());
            ps.setInt(4, animal.getEdad());
            ps.setDouble(5, animal.getPeso());

            // La imagen se guarda como BLOB (puede ser null si no se sube)
            if (animal.getImagen() != null) {
                ps.setBytes(6, animal.getImagen());
            } else {
                ps.setNull(6, Types.BLOB);
            }

            ps.setInt(7, animal.getId_cliente());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los animales (sin imagen para no sobrecargar la respuesta)
    public List<Animales> listarAnimales() {
        List<Animales> lista = new ArrayList<>();
        String sql = "SELECT id_animal, nombre, especie, raza, edad, peso, id_cliente FROM animales";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Animales a = new Animales(
                        rs.getInt("id_animal"),
                        rs.getString("nombre"),
                        rs.getString("especie"),
                        rs.getString("raza"),
                        rs.getInt("edad"),
                        rs.getDouble("peso"),
                        null,   // No cargamos la imagen aquí
                        rs.getInt("id_cliente")
                );
                lista.add(a);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    // Obtener solo la imagen de un animal por su id
    public byte[] obtenerImagen(int id) {
        String sql = "SELECT imagen FROM animales WHERE id_animal = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBytes("imagen");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Modificar un animal existente
    // Si se sube imagen nueva se actualiza; si no, se deja la que habia
    public boolean actualizarAnimal(Animales animal) {

        String sql;
        boolean hayImagenNueva = animal.getImagen() != null && animal.getImagen().length > 0;

        if (hayImagenNueva) {
            sql = "UPDATE animales SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, imagen = ?, id_cliente = ? WHERE id_animal = ?";
        } else {
            sql = "UPDATE animales SET nombre = ?, especie = ?, raza = ?, edad = ?, peso = ?, id_cliente = ? WHERE id_animal = ?";
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, animal.getNombre());
            ps.setString(2, animal.getEspecie());
            ps.setString(3, animal.getRaza());
            ps.setInt(4, animal.getEdad());
            ps.setDouble(5, animal.getPeso());

            if (hayImagenNueva) {
                ps.setBytes(6, animal.getImagen());
                ps.setInt(7, animal.getId_cliente());
                ps.setInt(8, animal.getId());
            } else {
                ps.setInt(6, animal.getId_cliente());
                ps.setInt(7, animal.getId());
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Eliminar un animal por su id
    public boolean eliminarAnimal(int id) {
        String sql = "DELETE FROM animales WHERE id_animal = ?";

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
