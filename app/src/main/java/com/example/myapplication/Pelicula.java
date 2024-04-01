package com.example.myapplication;

import com.google.firebase.Timestamp;

public class Pelicula {
    private String nombre;
    private String descripcion;
    private String fotoUrl;
    private String trailerUrl;
    private String plataforma;
    private Timestamp estreno;
    private String genero;
    private String tipo;
    private String push;
    private String director;

    public Pelicula(String nombre, String descripcion, String fotoUrl, String trailerUrl, String plataforma, Timestamp estreno, String genero, String tipo, String push, String director) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fotoUrl = fotoUrl;
        this.trailerUrl = trailerUrl;
        this.plataforma = plataforma;
        this.estreno = estreno;
        this.tipo = tipo;
        this.push = push;
        this.director = director;
        this.genero = genero;

    }

    public Timestamp getEstreno() {
        return estreno;
    }

    public void setEstreno(Timestamp estreno) {
        this.estreno = estreno;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPush() {
        return push;
    }

    public void setPush(String push) {
        this.push = push;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public String getPlataforma() {
        return plataforma;
    }
}
