/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Receta {
    int receta_id;
    Plato plato;
    Insumo insumo;
    float cantidad_requerida;

    public Receta(int receta_id, Plato plato, Insumo insumo, float cantidad_requerida) {
        this.receta_id = receta_id;
        this.plato = plato;
        this.insumo = insumo;
        this.cantidad_requerida = cantidad_requerida;
    }

    public Receta(Plato plato, Insumo insumo, float cantidad_requerida) {
        this.plato = plato;
        this.insumo = insumo;
        this.cantidad_requerida = cantidad_requerida;
    }

    public Receta() {
    }

    
    
    public int getReceta_id() {
        return receta_id;
    }

    public void setReceta_id(int receta_id) {
        this.receta_id = receta_id;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public float getCantidad_requerida() {
        return cantidad_requerida;
    }

    public void setCantidad_requerida(float cantidad_requerida) {
        this.cantidad_requerida = cantidad_requerida;
    }
    
    
}
