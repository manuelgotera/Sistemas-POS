/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import proyecto.pos.model.Venta;
import proyecto.pos.service.VentaService;

public class VentaController {



        private VentaService ventaService;

        public VentaController(VentaService ventaService) {
            this.ventaService = ventaService;
        }

        // ============================
        // 📌 REGISTRAR VENTA
        // ============================
        public boolean registrarVenta(Venta venta) {
            try {
                ventaService.registrarVenta(venta);
                return true;

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al registrar la venta");
            }
            return false;
        }

        // ============================
        // 📌 LISTAR VENTAS
        // ============================
        public List<Venta> listarVentas() {
            try {
                return ventaService.listarVentas();

            } catch (Exception e) {
                mostrarError("Error al listar ventas");
                return new ArrayList<>();
            }
        }

        // ============================
        // 📌 OBTENER VENTA
        // ============================
        public Venta obtenerVenta(int ventaId) {
            try {
                return ventaService.obtenerVenta(ventaId);

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al obtener la venta");
            }
            return null;
        }

        // ============================
        // 📌 ACTUALIZAR VENTA
        // ============================
        public boolean actualizarVenta(Venta venta) {
            try {
                ventaService.actualizarVenta(venta);
                return true;

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al actualizar la venta");
            }
            return false;
        }

        // ============================
        // 📌 ELIMINAR VENTA
        // ============================
        public boolean eliminarVenta(int ventaId) {
            try {
                ventaService.eliminarVenta(ventaId);
                return true;

            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            } catch (Exception e) {
                mostrarError("Error al eliminar la venta");
            }
            return false;
        }
        public List<Venta> listarVentasPorFecha(Date inicio, Date fin) {
            try {
                return ventaService.listarVentasPorFecha(inicio, fin);

            } catch (Exception e) {
                mostrarError("Error al filtrar ventas");
                return new ArrayList<>();
            }
        }


        private void mostrarError(String mensaje) {
            System.out.println("ERROR: " + mensaje);

            // Para Swing (recomendado):
            // JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

