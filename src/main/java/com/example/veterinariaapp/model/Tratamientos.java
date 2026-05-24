package com.example.veterinariaapp.model;

import lombok.Data;

@Data
public class Tratamientos {

    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer duracion_dias;
    private Double precio;

    public Tratamientos() {}

    public Tratamientos(Integer id, String nombre, String descripcion, Integer duracion_dias, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracion_dias = duracion_dias;
        this.precio = precio;
    }
}
