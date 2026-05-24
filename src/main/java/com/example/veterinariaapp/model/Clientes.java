package com.example.veterinariaapp.model;

import lombok.Data;

@Data
public class Clientes {

    private Integer id;
    private String nombre;
    private String telefono;
    private String email;

    public Clientes() {}

    public Clientes(Integer id, String nombre, String telefono, String email) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }
}
