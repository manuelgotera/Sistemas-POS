/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;

/**
 *
 * @author HP
 */
public class Insumo {

    private int insumoId;
    private String nombre;
    private String unidadMedida;
    private double stockMinimo;
    private Proveedor proveedor;
    private float costo;
    private float cantidad;
    private String categoria;
    
    
    public Insumo() {
    }

    public Insumo(String nombre, String unidadMedida, double stockMinimo, Proveedor proveedor, float costo, float cantidad) {
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
        this.costo = costo;
        this.cantidad = cantidad;
    }
    
    public Insumo(int insumoId, String nombre, String unidadMedida, double stockMinimo, Proveedor proveedor, float costo, float cantidad) {
        this.proveedor = proveedor;
        this.insumoId = insumoId;
        this.nombre = nombre;
        this.unidadMedida = unidadMedida;
        this.stockMinimo = stockMinimo;
        this.costo = costo;
        this.cantidad = cantidad;
    }

    public int getInsumoId() {
        return insumoId;
    }

    public void setInsumoId(int insumoId) {
        this.insumoId = insumoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(double stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public float getCosto() {
        return costo;
    }

    public void setCosto(float costo) {
        this.costo = costo;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }
    
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    
}