package proyecto.pos.dao.interfaces;
import java.util.List;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;

public interface PlatoDAO {
    void insertar(Plato plato);
    void actualizarPrecio(int platoId, float precio);
    void actualizarDisponibilidad(int platoId, int disponible);
    void actualizarNombre(int platoId, String nombre);
    void actualizarCategoria(int platoId, CategoriaMenu categoria);
    List<Plato> listar();
    Plato obtenerPorId(int id);
    Plato obtenerPorNombre(String nombre);
    List<CategoriaMenu> listarCategorias();
    // HU-05: Ajuste masivo de precios con transacción
    List<String> ajusteMasivoPrecios(List<Integer> platoIds, float porcentaje);
}