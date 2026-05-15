package proyecto.pos.controller;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;
import proyecto.pos.service.PlatoService;

public class PlatoController {

    private PlatoService platoService;

    public PlatoController(Connection conexion) {
        this.platoService = new PlatoService(conexion);
    }

    // ========================
    // REGISTRAR PLATO
    // ========================
    public void registrarPlato(
            String nombre,
            float precio,
            CategoriaMenu categoria,
            int disponible,
            String imagen
    ) {

        Plato plato = new Plato(
                nombre,
                precio,
                categoria,
                disponible,
                imagen
        );

        platoService.registrarPlato(plato);
    }

    // ========================
    // OBTENER PLATO
    // ========================
    public Plato obtenerPlatoPorId(int id) {
        return platoService.obtenerPlatoPorId(id);
        
    }
    
    public Plato obtenerPlatoPorNombre(String nombre){
        return platoService.obtenerPlatoPorNombre(nombre);
    }
    // ========================
    // LISTAR PLATOS
    // ========================
    public List<Plato> listarPlatos() {
        return platoService.listarPlatos();
    }
    
    public List<CategoriaMenu> listarCategorias(){
        return platoService.listarCategorias();
    }

    // ========================
    // ACTUALIZAR NOMBRE
    // ========================
    public void actualizarNombre(int platoId, String nombre) {
        platoService.actualizarNombre(platoId, nombre);
    }

    // ========================
    // ACTUALIZAR PRECIO
    // ========================
    public void actualizarPrecio(int platoId, float precio) {
        platoService.actualizarPrecio(platoId, precio);
    }

    // ========================
    // ACTUALIZAR CATEGORÍA
    // ========================
    public void actualizarCategoria(
            int platoId,
            CategoriaMenu categoria
    ) {

        platoService.actualizarCategoria(
                platoId,
                categoria
        );
    }

    // ========================
    // ACTUALIZAR DISPONIBILIDAD
    // ========================
    public void actualizarDisponibilidad(
            int platoId,
            int disponible
    ) {

        platoService.actualizarDisponibilidad(
                platoId,
                disponible
        );
    }

    // ========================
    // HABILITAR PLATO
    // ========================
    public void habilitarPlato(int platoId) {
        platoService.habilitarPlato(platoId);
    }

    // ========================
    // DESHABILITAR PLATO
    // ========================
    public void deshabilitarPlato(int platoId) {
        platoService.deshabilitarPlato(platoId);
    }
}