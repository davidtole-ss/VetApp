package com.example.veterinariaapp.controller;

import com.example.veterinariaapp.model.Clientes;
import com.example.veterinariaapp.repository.ClientesDAO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClientesController {

    private final ClientesDAO clientesDAO;

    public ClientesController(ClientesDAO clientesDAO) {
        this.clientesDAO = clientesDAO;
    }

    // GET /clientes: devuelve todos los clientes
    @GetMapping
    public List<Clientes> listarClientes() {
        return clientesDAO.listarClientes();
    }

    // POST /clientes : inserta un cliente nuevo
    @PostMapping
    public boolean insertarCliente(@RequestBody Clientes cliente) {
        return clientesDAO.insertarCliente(cliente);
    }

    // PUT /clientes: modifica un cliente existente
    @PutMapping
    public boolean actualizarCliente(@RequestBody Clientes cliente) {
        return clientesDAO.actualizarCliente(cliente);
    }

    // DELETE /clientes/{id}: elimina un cliente por id
    @DeleteMapping("/{id}")
    public boolean eliminarCliente(@PathVariable int id) {
        return clientesDAO.eliminarCliente(id);
    }
}
