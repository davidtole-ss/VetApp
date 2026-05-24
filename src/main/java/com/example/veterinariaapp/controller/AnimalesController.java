package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Animales;
import com.example.veterinariaapp.repository.AnimalesDAO;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/animales")
public class AnimalesController {

    private final AnimalesDAO animalesDAO;

    public AnimalesController(AnimalesDAO animalesDAO) {
        this.animalesDAO = animalesDAO;
    }

    // GET/animales:devuelve todos los animales (sin imagen)
    @GetMapping
    public List<Animales> listarAnimales() {
        return animalesDAO.listarAnimales();
    }

    // GET/animales/{id}/imagen : devuelve la imagen del animal
    @GetMapping("/{id}/imagen")
    public ResponseEntity<byte[]> obtenerImagen(@PathVariable int id) {
        byte[] imagen = animalesDAO.obtenerImagen(id);

        if (imagen == null || imagen.length == 0) {
            return ResponseEntity.notFound().build();
        }

        // Detectamos el tipo de imagen por sus primeros bytes
        MediaType tipo = detectarTipoImagen(imagen);

        return ResponseEntity.ok()
                .contentType(tipo)
                .body(imagen);
    }

    // POST/animales: inserta un animal nuevo con imagen opcional
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean insertarAnimal(
            @RequestParam("nombre") String nombre,
            @RequestParam("especie") String especie,
            @RequestParam("raza") String raza,
            @RequestParam("edad") Integer edad,
            @RequestParam("peso") Double peso,
            @RequestParam("id_cliente") Integer id_cliente,
            @RequestParam(value = "imagen", required = false) MultipartFile imagenFile) {
        try {
            byte[] imagenBytes = null;
            if (imagenFile != null && !imagenFile.isEmpty()) {
                imagenBytes = imagenFile.getBytes();
            }
            Animales animal = new Animales(null, nombre, especie, raza, edad, peso, imagenBytes, id_cliente);
            return animalesDAO.insertarAnimal(animal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // PUT /animales: modifica un animal existente con imagen opcional
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean actualizarAnimal(
            @RequestParam("id") Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("especie") String especie,
            @RequestParam("raza") String raza,
            @RequestParam("edad") Integer edad,
            @RequestParam("peso") Double peso,
            @RequestParam("id_cliente") Integer id_cliente,
            @RequestParam(value = "imagen", required = false) MultipartFile imagenFile
    ) {
        try {
            byte[] imagenBytes = null;
            if (imagenFile != null && !imagenFile.isEmpty()) {
                imagenBytes = imagenFile.getBytes();
            }
            Animales animal = new Animales(id, nombre, especie, raza, edad, peso, imagenBytes, id_cliente);
            return animalesDAO.actualizarAnimal(animal);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE /animales/{id}:elimina un animal por id
    @DeleteMapping("/{id}")
    public boolean eliminarAnimal(@PathVariable int id) {
        return animalesDAO.eliminarAnimal(id);
    }

    // Detecta si la imagen es JPG, PNG o GIF mirando sus primeros bytes
    private MediaType detectarTipoImagen(byte[] bytes) {
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) {
            return MediaType.IMAGE_JPEG;
        }
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50) {
            return MediaType.IMAGE_PNG;
        }
        if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.IMAGE_JPEG; // por defecto
    }
}
