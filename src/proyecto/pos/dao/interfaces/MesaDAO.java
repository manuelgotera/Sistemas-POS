package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.Mesa;

public interface MesaDAO {

    void insertar(Mesa mesa);

    List<Mesa> listar();

    Mesa obtenerPorId(int id);

    void actualizar(Mesa mesa);

    void cambiarEstado(int mesaId, int estado);
    
    Mesa obtenerPorNumeroMesa(int numeroMesa);

}