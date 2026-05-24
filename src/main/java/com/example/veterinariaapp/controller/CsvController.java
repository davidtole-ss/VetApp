package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Animales;
import com.example.veterinariaapp.model.Clientes;
import com.example.veterinariaapp.repository.AnimalesDAO;
import com.example.veterinariaapp.repository.ClientesDAO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/csv")
public class CsvController {

    private final ClientesDAO clientesDAO;
    private final AnimalesDAO animalesDAO;

    public CsvController(ClientesDAO clientesDAO, AnimalesDAO animalesDAO) {
        this.clientesDAO = clientesDAO;
        this.animalesDAO = animalesDAO;
    }

    //  POST /csv/clientes/importar
    //  Lee un CSV con columnas: nombre,telefono,email
    //  e inserta cada fila como cliente en la base de datos
    @PostMapping("/clientes/importar")
    public ResponseEntity<String> importarClientes(@RequestParam("file") MultipartFile file) {
        int insertados = 0;
        int errores = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean esCabecera = true;

            while ((linea = br.readLine()) != null) {

                // Saltamos la primera línea (cabecera del CSV)
                if (esCabecera) {
                    esCabecera = false;
                    continue;
                }

                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split(",", -1);

                if (campos.length < 3) {
                    errores++;
                    continue;
                }

                try {
                    Clientes cliente = new Clientes(
                            null,
                            campos[0].trim(),   // nombre
                            campos[1].trim(),   // telefono
                            campos[2].trim());  //emaik
                    if (clientesDAO.insertarCliente(cliente)) {
                        insertados++;
                    } else {
                        errores++;
                    }
                } catch (Exception e) {
                    errores++;
                }
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al leer el archivo: " + e.getMessage());
        }

        return ResponseEntity.ok("Clientes importados: " + insertados + " | Errores: " + errores);
    }


    //  GET /csv/clientes/exportar
    //  Lee todos los clientes de la BD y genera un CSV para descargar

    @GetMapping("/clientes/exportar")
    public ResponseEntity<byte[]> exportarClientes() {
        List<Clientes> clientes = clientesDAO.listarClientes();

        StringBuilder sb = new StringBuilder();
        sb.append("id,nombre,telefono,email\n");

        for (Clientes c : clientes) {
            sb.append(c.getId()).append(",")
                    .append(escaparCsv(c.getNombre())).append(",")
                    .append(escaparCsv(c.getTelefono())).append(",")
                    .append(escaparCsv(c.getEmail())).append("\n");
        }

        byte[] contenido = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"clientes.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(contenido);
    }

    //  POST /csv/animales/importar
    //  Lee un CSV con columnas: nombre,especie,raza,edad,peso,id_cliente
    //  e inserta cada fila como animal en la base de datos
    // -las imágenes no se importan por CSV, se suben desde el formulario
    @PostMapping("/animales/importar")
    public ResponseEntity<String> importarAnimales(@RequestParam("file") MultipartFile file) {
        int insertados = 0;
        int errores = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String linea;
            boolean esCabecera = true;

            while ((linea = br.readLine()) != null) {

                if (esCabecera) {
                    esCabecera = false;
                    continue;
                }

                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split(",", -1);

                if (campos.length < 6) {
                    errores++;
                    continue;
                }

                try {
                    Animales animal = new Animales(
                            null,
                            campos[0].trim(),                       // nombre
                            campos[1].trim(),                       // especie
                            campos[2].trim(),                       // raza
                            Integer.parseInt(campos[3].trim()),     // edad
                            Double.parseDouble(campos[4].trim()),   // peso
                            null,                                   // imagen(se sube desde el formulario)
                            Integer.parseInt(campos[5].trim()));    // id_cliente

                    if (animalesDAO.insertarAnimal(animal)) {
                        insertados++;
                    } else {
                        errores++;
                    }
                } catch (Exception e) {
                    errores++;
                }
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al leer el archivo: " + e.getMessage());
        }

        return ResponseEntity.ok("Animales importados: " + insertados + " | Errores: " + errores);
    }

    //  GET /csv/animales/exportar
    //  Lee todos los animales de la BD y genera un CSV para descargar
    //  Nota: la imagen no se exporta al CSV porque es datos binarios
    @GetMapping("/animales/exportar")
    public ResponseEntity<byte[]> exportarAnimales() {
        List<Animales> animales = animalesDAO.listarAnimales();

        StringBuilder sb = new StringBuilder();
        sb.append("id_animal,nombre,especie,raza,edad,peso,id_cliente\n");

        for (Animales a : animales) {
            sb.append(a.getId()).append(",")
                    .append(escaparCsv(a.getNombre())).append(",")
                    .append(escaparCsv(a.getEspecie())).append(",")
                    .append(escaparCsv(a.getRaza())).append(",")
                    .append(a.getEdad()).append(",")
                    .append(a.getPeso()).append(",")
                    .append(a.getId_cliente()).append("\n");
        }

        byte[] contenido = sb.toString().getBytes(StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"animales.csv\"")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(contenido);
    }

    // Si un campo tiene comas o comillas lo ponemos entre comillas dobles
    private String escaparCsv(String valor) {
        if (valor == null) return "";
        if (valor.contains(",") || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }
}
