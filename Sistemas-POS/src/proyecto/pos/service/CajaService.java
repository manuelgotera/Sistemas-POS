/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.service;

import proyecto.pos.dao.interfaces.CajaDAO;
import proyecto.pos.model.Caja;
import proyecto.pos.model.Empleado;

import java.util.Date;
import java.util.List;
/**
 *
 * @author HP
 */
public class CajaService {


    private CajaDAO cajaDAO;

    public CajaService(CajaDAO cajaDAO) {
        this.cajaDAO = cajaDAO;
    }

    // ABRIR CAJA
    public void abrirCaja(double montoInicial, Empleado empleado) {

        try {
            validarApertura(montoInicial, empleado);

            if (cajaDAO.obtenerCajaAbierta() != null) {
                throw new IllegalArgumentException("Ya existe una caja abierta");
            }

            Caja caja = new Caja();
            caja.setFecha_apertura(new Date());
            caja.setMonto_inicial(montoInicial);
            caja.setEstado("ABIERTA");
            caja.setEmpleado(empleado);

            cajaDAO.insertar(caja);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al abrir caja", e);
        }
    }

    // CERRAR CAJA
    public void cerrarCaja(double montoFinal) {

        try {
            Caja caja = cajaDAO.obtenerCajaAbierta();

            if (caja == null) {
                throw new IllegalArgumentException("No hay caja abierta");
            }

            if (montoFinal < 0) {
                throw new IllegalArgumentException("El monto final no puede ser negativo");
            }

            caja.setFecha_cierre(new Date());
            caja.setMonto_final(montoFinal);
            caja.setEstado("CERRADA");

            double diferencia = montoFinal - caja.getMonto_inicial();
            caja.setDiferencia(diferencia);

            cajaDAO.actualizar(caja);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al cerrar caja", e);
        }
    }

    // LISTAR CAJAS
    
    public List<Caja> listarCajas() {
        try {
            return cajaDAO.listar();
        } catch (Exception e) {
            throw new RuntimeException("Error al listar cajas", e);
        }
    }

   // OBTENER CAJA POR ID 
    public Caja obtenerCaja(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        try {
            Caja caja = cajaDAO.obtenerPorId(id);

            if (caja == null) {
                throw new IllegalArgumentException("Caja no encontrada");
            }

            return caja;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener caja", e);
        }
    }

    // VALIDACIONES 
    private void validarApertura(double montoInicial, Empleado empleado) {

        if (montoInicial <= 0) {
            throw new IllegalArgumentException("El monto inicial debe ser mayor a 0");
        }

        if (empleado == null) {
            throw new IllegalArgumentException("Debe existir un empleado");
        }

        if (empleado.getId() <= 0) {
            throw new IllegalArgumentException("Empleado inválido");
        }
    }
}

