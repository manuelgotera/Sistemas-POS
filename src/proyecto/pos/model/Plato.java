/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

import java.util.Objects;

/**
 *
 * @author HP
 */
public class Plato {

    private int platoId;
    private String nombre;
    private float precio;
    private CategoriaMenu categoria;
    private int disponible;
    private String imagen;
    
    public Plato() {
    }

    public Plato(int platoId, String nombre, float precio, CategoriaMenu categoria,
            int disponible, String imagen) {
        this.platoId = platoId;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.imagen = imagen;
    }

    public Plato(String nombre, float precio, CategoriaMenu categoria,
            int disponible, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
        this.imagen = imagen;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    
    
    public int getPlatoId() {
        return platoId;
    }

    public void setPlatoId(int platoId) {
        this.platoId = platoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public CategoriaMenu getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaMenu categoria) {
        this.categoria = categoria;
    }

    public int getDisponible() {
        return disponible;
    }

    public void setDisponible(int disponible) {
        this.disponible = disponible;
    }
    
    @Override
    public String toString() {
        return "Plato {" +
                "ID = " + platoId +
                ", Nombre = '" + nombre + '\'' +
                ", Precio = " + precio +
                ", Disponible = " + (disponible) +
                ", Categoría = " + (categoria != null ? categoria.getNombre() : "N/A") +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Plato plato = (Plato) o;

        return platoId == plato.platoId;
    }

    @Override
    public int hashCode() {

        return Objects.hash(platoId);
    }
}