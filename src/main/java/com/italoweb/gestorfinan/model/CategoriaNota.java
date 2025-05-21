package com.italoweb.gestorfinan.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias_notas")
public class CategoriaNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria_nota")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "categoriaNota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Nota> notas;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Nota> getNotas() {
        return notas;
    }

    public void setNotas(List<Nota> notas) {
        this.notas = notas;
    }
}

