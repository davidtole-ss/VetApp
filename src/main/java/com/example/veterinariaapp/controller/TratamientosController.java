package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Tratamientos;
import com.example.veterinariaapp.repository.TratamientosDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tratamientos")
public class TratamientosController {

    private final TratamientosDAO tratamientosDAO;

    public TratamientosController(TratamientosDAO tratamientosDAO) {
        this.tratamientosDAO = tratamientosDAO;
    }

    // GET /tratamientos: devuelve todos los tratamientos
    @GetMapping
    public List<Tratamientos> listarTratamientos() {
        return tratamientosDAO.listarTratamientos();
    }

    // POST /tratamientos:inserta un tratamiento nuevo
    @PostMapping
    public boolean insertarTratamiento(@RequestBody Tratamientos tratamiento) {
        return tratamientosDAO.insertarTratamiento(tratamiento);
    }

    // PUT /tratamientos: modifica un tratamiento existente
    @PutMapping
    public boolean actualizarTratamiento(@RequestBody Tratamientos tratamiento) {
        return tratamientosDAO.actualizarTratamiento(tratamiento);
    }

    // DELETE /tratamientos/{id}: elimina un tratamiento por id
    @DeleteMapping("/{id}")
    public boolean eliminarTratamiento(@PathVariable int id) {
        return tratamientosDAO.eliminarTratamiento(id);
    }
}
