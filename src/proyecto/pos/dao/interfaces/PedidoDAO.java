/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.dao.interfaces;

/**
 *
 * @author USER
 */
import java.util.List;
import proyecto.pos.model.Pedido;
import proyecto.pos.model.Plato;
import proyecto.pos.model.Repartidor;

public interface PedidoDAO {

    int registrarPedido(Pedido pedido);

    List<Pedido> listarPedidos();

    List<Pedido> listarPedidosPendientes();

    boolean asignarRepartidor(int pedidoId, int repartidorId);

    List<Pedido> listarPedidosPorRepartidor(int repartidorId);

    boolean cambiarEstadoPedido(int pedidoId, String nuevoEstado, String evidencia, String motivo);

    List<Repartidor> listarRepartidores();

    List<Plato> listarProductosParaGestion();

    boolean actualizarDisponibilidadProducto(int platoId, int disponible);
}
