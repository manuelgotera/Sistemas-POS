/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.service;

/**
 *
 * @author USER
 */

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import proyecto.pos.dao.impl.PedidoDAOImpl;
import proyecto.pos.dao.interfaces.PedidoDAO;
import proyecto.pos.model.Pedido;
import proyecto.pos.model.Plato;
import proyecto.pos.model.Repartidor;

public class PedidoService {

    private PedidoDAO pedidoDAO;

    public PedidoService(Connection conexion) {
        this.pedidoDAO = new PedidoDAOImpl(conexion);
    }

    public int registrarPedido(Pedido pedido) {
        validarPedido(pedido);
        return pedidoDAO.registrarPedido(pedido);
    }

    public List<Pedido> listarPedidos() {
        return pedidoDAO.listarPedidos();
    }

    public List<Pedido> listarPedidosPendientes() {
        return pedidoDAO.listarPedidosPendientes();
    }

    public boolean asignarRepartidor(int pedidoId, int repartidorId) {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }

        if (repartidorId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un repartidor válido.");
        }

        return pedidoDAO.asignarRepartidor(pedidoId, repartidorId);
    }

    public List<Pedido> listarPedidosPorRepartidor(int repartidorId) {
        if (repartidorId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un repartidor válido.");
        }

        return pedidoDAO.listarPedidosPorRepartidor(repartidorId);
    }

    public boolean cambiarEstadoPedido(int pedidoId, String nuevoEstado, String evidencia, String motivo) {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un pedido válido.");
        }

        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un estado.");
        }

        nuevoEstado = nuevoEstado.trim().toUpperCase();

        List<String> estadosPermitidos = Arrays.asList(
                "PENDIENTE",
                "ASIGNADO",
                "EN_CAMINO",
                "ENTREGADO",
                "NO_ENTREGADO",
                "REPROGRAMADO"
        );

        if (!estadosPermitidos.contains(nuevoEstado)) {
            throw new IllegalArgumentException("Estado no permitido: " + nuevoEstado);
        }

        if ("NO_ENTREGADO".equals(nuevoEstado)) {
            if (motivo == null || motivo.trim().isEmpty()) {
                throw new IllegalArgumentException("Debe ingresar el motivo de no entrega.");
            }
        }

        return pedidoDAO.cambiarEstadoPedido(pedidoId, nuevoEstado, evidencia, motivo);
    }

    public List<Repartidor> listarRepartidores() {
        return pedidoDAO.listarRepartidores();
    }

    public List<Plato> listarProductosParaGestion() {
        return pedidoDAO.listarProductosParaGestion();
    }

    public boolean actualizarDisponibilidadProducto(int platoId, int disponible) {
        if (platoId <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un producto válido.");
        }

        if (disponible != 0 && disponible != 1) {
            throw new IllegalArgumentException("La disponibilidad solo puede ser 0 o 1.");
        }

        return pedidoDAO.actualizarDisponibilidadProducto(platoId, disponible);
    }

    private void validarPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede estar vacío.");
        }

        if (pedido.getClienteNombre() == null || pedido.getClienteNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el nombre del cliente.");
        }

        if (pedido.getDireccionEntrega() == null || pedido.getDireccionEntrega().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar la dirección de entrega.");
        }

        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto al pedido.");
        }

        pedido.recalcularTotal();

        if (pedido.getTotal() <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser mayor a cero.");
        }
    }
}