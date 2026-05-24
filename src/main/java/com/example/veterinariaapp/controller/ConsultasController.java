package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Consultas;
import com.example.veterinariaapp.repository.ConsultasDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultasController {

    private final ConsultasDAO consultasDAO;

    public ConsultasController(ConsultasDAO consultasDAO) {
        this.consultasDAO = consultasDAO;
    }

    // GET /consultas: devuelve todas las consultas
    @GetMapping
    public List<Consultas> listarConsultas() {
        return consultasDAO.listarConsultas();
    }

    // POST /consultas: inserta una consulta nueva
    @PostMapping
    public boolean insertarConsulta(@RequestBody Consultas consulta) {
        return consultasDAO.insertarConsulta(consulta);
    }

    // PUT /consultas: modifica una consulta existente
    @PutMapping
    public boolean actualizarConsulta(@RequestBody Consultas consulta) {
        return consultasDAO.actualizarConsulta(consulta);
    }

    // DELETE /consultas/{id} :elimina una consulta por id
    @DeleteMapping("/{id}")
    public boolean eliminarConsulta(@PathVariable int id) {
        return consultasDAO.eliminarConsulta(id);
    }
}
