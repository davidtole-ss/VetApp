package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Tratamientos;
import com.example.veterinariaapp.repository.ConsultaTratamientoDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consulta-tratamiento")
public class ConsultaTratamientoController {

    private final ConsultaTratamientoDAO consultaTratamientoDAO;

    public ConsultaTratamientoController(ConsultaTratamientoDAO consultaTratamientoDAO) {
        this.consultaTratamientoDAO = consultaTratamientoDAO;
    }

    // GET /consulta-tratamiento/{idConsulta} : devuelve los tratamientos de una consulta
    @GetMapping("/{idConsulta}")
    public List<Tratamientos> listarTratamientos(@PathVariable int idConsulta) {
        return consultaTratamientoDAO.listarTratamientosDeConsulta(idConsulta);
    }

    // POST /consulta-tratamiento/{idConsulta}/{idTratamiento} : asigna un tratamiento a una consulta
    @PostMapping("/{idConsulta}/{idTratamiento}")
    public boolean asignarTratamiento(@PathVariable int idConsulta, @PathVariable int idTratamiento) {
        return consultaTratamientoDAO.asignarTratamiento(idConsulta, idTratamiento);
    }

    // DELETE /consulta-tratamiento/{idConsulta}/{idTratamiento} : elimina un tratamiento de una consulta
    @DeleteMapping("/{idConsulta}/{idTratamiento}")
    public boolean eliminarTratamiento(@PathVariable int idConsulta, @PathVariable int idTratamiento) {
        return consultaTratamientoDAO.eliminarTratamientoDeConsulta(idConsulta, idTratamiento);
    }
}
