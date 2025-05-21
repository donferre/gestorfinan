package com.italoweb.gestorfinan.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notas")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nota")
    private Long id;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "contenido")
    private String contenido;

    @ManyToOne
    @JoinColumn(name = "id_categoria_nota", nullable = false)
    private CategoriaNota categoriaNota;

    @Column(name = "posicion")
    private int posicion;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public CategoriaNota getCategoriaNota() {
        return categoriaNota;
    }

    public void setCategoriaNota(CategoriaNota categoriaNota) {
        this.categoriaNota = categoriaNota;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
