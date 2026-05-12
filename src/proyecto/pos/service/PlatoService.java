package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.PlatoDAOImpl;
import proyecto.pos.dao.interfaces.PlatoDAO;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;

public class PlatoService {

    private PlatoDAO platoDAO;

    public PlatoService(Connection conexion) {
        this.platoDAO = new PlatoDAOImpl(conexion);
    }

    // ========================
    // REGISTRAR PLATO
    // ========================
    public void registrarPlato(Plato plato) {

        validarPlato(plato);

        platoDAO.insertar(plato);
    }

    // ========================
    // OBTENER PLATO
    // ========================
    public Plato obtenerPlatoPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID de plato inválido");
        }

        Plato plato = platoDAO.obtenerPorId(id);

        if (plato == null) {
            throw new RuntimeException("Plato no encontrado");
        }

        return plato;
    }

    // ========================
    // LISTAR PLATOS
    // ========================
    public List<Plato> listarPlatos() {
        return platoDAO.listar();
    }
    
    public List<CategoriaMenu> listarCategorias(){
        return platoDAO.listarCategorias();
    }

    // ========================
    // ACTUALIZAR PRECIO
    // ========================
    public void actualizarPrecio(int platoId, float precio) {

        validarId(platoId);

        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }

        if (precio > 9999.99f) {
            throw new IllegalArgumentException("El precio es demasiado alto");
        }

        verificarExistencia(platoId);

        platoDAO.actualizarPrecio(platoId, precio);
    }

    // ========================
    // ACTUALIZAR NOMBRE
    // ========================
    public void actualizarNombre(int platoId, String nombre) {

        validarId(platoId);

        validarNombre(nombre);

        verificarExistencia(platoId);

        platoDAO.actualizarNombre(platoId, nombre.trim());
    }

    // ========================
    // ACTUALIZAR DISPONIBILIDAD
    // ========================
    public void actualizarDisponibilidad(int platoId, int disponible) {

        validarId(platoId);

        if (disponible != 0 && disponible != 1) {
            throw new IllegalArgumentException(
                    "La disponibilidad solo puede ser 0 o 1");
        }

        verificarExistencia(platoId);

        platoDAO.actualizarDisponibilidad(platoId, disponible);
    }

    // ========================
    // ACTUALIZAR CATEGORÍA
    // ========================
    public void actualizarCategoria(int platoId, CategoriaMenu categoria) {

        validarId(platoId);

        validarCategoria(categoria);

        verificarExistencia(platoId);

        platoDAO.actualizarCategoria(platoId, categoria);
    }

    // ========================
    // HABILITAR / DESHABILITAR
    // ========================
    public void habilitarPlato(int platoId) {

        validarId(platoId);

        verificarExistencia(platoId);

        platoDAO.actualizarDisponibilidad(platoId, 1);
    }

    public void deshabilitarPlato(int platoId) {

        validarId(platoId);

        verificarExistencia(platoId);

        platoDAO.actualizarDisponibilidad(platoId, 0);
    }

    // ========================
    // VALIDACIONES PRIVADAS
    // ========================
    private void validarPlato(Plato plato) {

        if (plato == null) {
            throw new IllegalArgumentException("El plato no puede ser null");
        }

        validarNombre(plato.getNombre());

        if (plato.getPrecio() <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor a 0");
        }

        if (plato.getPrecio() > 9999.99f) {
            throw new IllegalArgumentException(
                    "El precio excede el límite permitido");
        }

        validarCategoria(plato.getCategoria());

        if (plato.getDisponible() != 0 &&
                plato.getDisponible() != 1) {

            throw new IllegalArgumentException(
                    "La disponibilidad debe ser 0 o 1");
        }

        if (plato.getImagen() != null &&
                plato.getImagen().length() > 255) {

            throw new IllegalArgumentException(
                    "La ruta de imagen es demasiado larga");
        }
    }

    private void validarNombre(String nombre) {

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del plato no puede estar vacío");
        }

        if (nombre.trim().length() < 3) {
            throw new IllegalArgumentException(
                    "El nombre debe tener mínimo 3 caracteres");
        }

        if (nombre.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "El nombre es demasiado largo");
        }
    }

    private void validarCategoria(CategoriaMenu categoria) {

        if (categoria == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar una categoría");
        }

        if (categoria.getCategoriaId() <= 0) {
            throw new IllegalArgumentException(
                    "ID de categoría inválido");
        }
    }

    private void validarId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID inválido");
        }
    }

    private void verificarExistencia(int platoId) {

        Plato plato = platoDAO.obtenerPorId(platoId);

        if (plato == null) {
            throw new RuntimeException(
                    "El plato no existe");
        }
    }
}