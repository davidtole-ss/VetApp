package com.example.veterinariaapp.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class Consultas {

    private Integer id;
    private LocalDate fecha;
    private String motivo;
    private Double precio;
    private Integer id_animal;

    public Consultas() {}

    public Consultas(Integer id, LocalDate fecha, String motivo, Double precio, Integer id_animal) {
        this.id = id;
        this.fecha = fecha;
        this.motivo = motivo;
        this.precio = precio;
        this.id_animal = id_animal;
    }
}
