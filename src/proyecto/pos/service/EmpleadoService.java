package proyecto.pos.service;

import java.sql.*;
import java.util.List;
import proyecto.pos.dao.impl.EmpleadoDAOImpl;
import proyecto.pos.dao.interfaces.EmpleadoDAO;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;


public class EmpleadoService {

    private EmpleadoDAO empleadoDAO;

    public EmpleadoService(Connection conexion) {
        this.empleadoDAO = new EmpleadoDAOImpl(conexion);
    }

    // ========================
    // REGISTRAR EMPLEADO
    // ========================
    public void registrarEmpleado(Empleado empleado) {

        // Validaciones básicas

        if (empleado.getNombre() == null || empleado.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (empleado.getApellidos() == null || empleado.getApellidos().isEmpty()) {
            throw new IllegalArgumentException("Los apellidos no pueden estar vacíos");
        }

        if (empleado.getDni() == null || empleado.getDni().length() != 8) {
            throw new IllegalArgumentException("DNI inválido");
        }

        if (empleado.getRol() == null) {
            throw new IllegalArgumentException("El empleado debe tener un rol");
        }

        // Estado por defecto
        if (empleado.getEstado() == null) {
            empleado.setEstado(EstadoEmpleado.ACTIVO);
        }

        // Fecha automática si viene null
        if (empleado.getFecha_contratación() == null) {
            empleado.setFecha_contratación(new java.util.Date());
        }

        empleadoDAO.insertar(empleado);
    }

    // ========================
    // OBTENER EMPLEADO
    // ========================
    public Empleado obtenerEmpleadoPorId(int id) {
        return empleadoDAO.obtenerPorId(id);
    }

    public Empleado obtenerEmpleadoPorDni(String dni) {
        return empleadoDAO.obtenerPorDni(dni);
    }

    // ========================
    // LISTAR EMPLEADOS
    // ========================
    public List<Empleado> listarEmpleados() {
        return empleadoDAO.listar();
    }

    // ========================
    // ACTUALIZAR EMPLEADO
    // ========================
    public void actualizarEmpleado(Empleado empleado) {

        if (empleado.getId() <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido");
        }

        if (empleado.getRol() == null) {
            throw new IllegalArgumentException("El empleado debe tener un rol");
        }

        empleadoDAO.actualizar(empleado);
    }

    // ========================
    // REGLAS DE NEGOCIO EXTRA
    // ========================

    public void cambiarEstado(int empleadoId, EstadoEmpleado nuevoEstado) {

        Empleado empleado = empleadoDAO.obtenerPorId(empleadoId);

        if (empleado == null) {
            throw new RuntimeException("Empleado no encontrado");
        }

        empleado.setEstado(nuevoEstado);

        empleadoDAO.actualizar(empleado);
    }

    public void desactivarEmpleado(int empleadoId) {

        Empleado empleado = empleadoDAO.obtenerPorId(empleadoId);

        if (empleado == null) {
            throw new RuntimeException("Empleado no encontrado");
        }

        empleado.setEstado(EstadoEmpleado.INACTIVO);

        empleadoDAO.actualizar(empleado);
    }

    public void activarEmpleado(int empleadoId) {

        Empleado empleado = empleadoDAO.obtenerPorId(empleadoId);

        if (empleado == null) {
            throw new RuntimeException("Empleado no encontrado");
        }

        empleado.setEstado(EstadoEmpleado.ACTIVO);

        empleadoDAO.actualizar(empleado);
    }
}