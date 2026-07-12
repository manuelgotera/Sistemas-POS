package proyecto.pos.dao.interfaces;
import java.util.List;
import proyecto.pos.model.Venta;

public interface VentaDAO {
    void insertar(Venta venta);
    void actualizar(Venta venta);
    void eliminar(int ventaId);
    Venta obtenerPorId(int id);
    List<Venta> listar();
    List<Venta> listarPorRangoFecha(java.util.Date inicio, java.util.Date fin);

    // HU-06: Anulación de venta liquidada
    List<String> anularVenta(int ventaId, int empleadoId);
}