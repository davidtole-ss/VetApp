package com.example.veterinariaapp.model;

import lombok.Data;

@Data
public class Animales {

    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private Integer edad;
    private Double peso;
    private byte[] imagen;      // Se guarda en base de datos como LONGBLOB
    private Integer id_cliente;

    public Animales() {}

    public Animales(Integer id, String nombre, String especie, String raza,
                    Integer edad, Double peso, byte[] imagen, Integer id_cliente) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.edad = edad;
        this.peso = peso;
        this.imagen = imagen;
        this.id_cliente = id_cliente;
    }
}
