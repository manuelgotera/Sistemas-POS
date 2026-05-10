package proyecto.pos.controller;

import proyecto.pos.service.EmpleadoService;
import java.sql.Connection;
import java.util.List;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Rol;
import java.util.Date;
        
        
public class EmpleadoController {

    private EmpleadoService empleadoService;

    public EmpleadoController(Connection conexion) {
        this.empleadoService = new EmpleadoService(conexion);
    }

    // ========================
    // REGISTRAR EMPLEADO
    // ========================
    public void registrarEmpleado(Rol rol,
                                  String nombre,
                                  String apellido,
                                  String dni,
                                  String telefono,
                                  String email,
                                  EstadoEmpleado estado,
                                  Date fechaContratacion) {

        Empleado empleado = new Empleado();

        empleado.setRol(rol);
        empleado.setNombre(nombre);
        empleado.setApellidos(apellido);
        empleado.setDni(dni);
        empleado.setTelefono(telefono);
        empleado.setEmail(email);
        empleado.setEstado(estado);
        empleado.setFecha_contratación(fechaContratacion);

        empleadoService.registrarEmpleado(empleado);
    }

    // ========================
    // OBTENER EMPLEADO
    // ========================
    public Empleado obtenerPorId(int id) {
        return empleadoService.obtenerEmpleadoPorId(id);
    }

    public Empleado obtenerPorDni(String dni) {
        return empleadoService.obtenerEmpleadoPorDni(dni);
    }

    // ========================
    // LISTAR EMPLEADOS
    // ========================
    public List<Empleado> listarEmpleados() {
        return empleadoService.listarEmpleados();
    }

    // ========================
    // ACTUALIZAR EMPLEADO
    // ========================
    public void actualizarEmpleado(int id,
                                   Rol rol,
                                   EstadoEmpleado estado,
                                   String nombre,
                                   String apellido,
                                   String dni,
                                   String telefono,
                                   String email,
                                   Date fechaContratacion) {

        Empleado empleado = new Empleado();

        empleado.setId(id);
        empleado.setRol(rol);
        empleado.setEstado(estado);
        empleado.setNombre(nombre);
        empleado.setApellidos(apellido);
        empleado.setDni(dni);
        empleado.setTelefono(telefono);
        empleado.setEmail(email);
        empleado.setFecha_contratación(fechaContratacion);

        empleadoService.actualizarEmpleado(empleado);
    }

    // ========================
    // CAMBIAR ESTADO
    // ========================
    public void cambiarEstado(int empleadoId, EstadoEmpleado nuevoEstado) {
        empleadoService.cambiarEstado(empleadoId, nuevoEstado);
    }

    public void activarEmpleado(int empleadoId) {
        empleadoService.activarEmpleado(empleadoId);
    }

    public void desactivarEmpleado(int empleadoId) {
        empleadoService.desactivarEmpleado(empleadoId);
    }
}