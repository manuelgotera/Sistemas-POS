/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.controller;

/**
 *
 * @author USER
 */

import java.sql.Connection;
import java.util.List;
import proyecto.pos.model.Pedido;
import proyecto.pos.model.Plato;
import proyecto.pos.model.Repartidor;
import proyecto.pos.service.PedidoService;

public class PedidoController {

    private PedidoService pedidoService;

    public PedidoController(Connection conexion) {
        this.pedidoService = new PedidoService(conexion);
    }

    public int registrarPedido(Pedido pedido) {
        return pedidoService.registrarPedido(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoService.listarPedidos();
    }

    public List<Pedido> listarPedidosPendientes() {
        return pedidoService.listarPedidosPendientes();
    }

    public boolean asignarRepartidor(int pedidoId, int repartidorId) {
        return pedidoService.asignarRepartidor(pedidoId, repartidorId);
    }

    public List<Pedido> listarPedidosPorRepartidor(int repartidorId) {
        return pedidoService.listarPedidosPorRepartidor(repartidorId);
    }

    public boolean cambiarEstadoPedido(int pedidoId, String nuevoEstado, String evidencia, String motivo) {
        return pedidoService.cambiarEstadoPedido(pedidoId, nuevoEstado, evidencia, motivo);
    }

    public List<Repartidor> listarRepartidores() {
        return pedidoService.listarRepartidores();
    }

    public List<Plato> listarProductosParaGestion() {
        return pedidoService.listarProductosParaGestion();
    }

    public boolean actualizarDisponibilidadProducto(int platoId, int disponible) {
        return pedidoService.actualizarDisponibilidadProducto(platoId, disponible);
    }
}
