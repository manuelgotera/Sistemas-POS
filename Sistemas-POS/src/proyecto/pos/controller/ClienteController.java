package proyecto.pos.controller;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import proyecto.pos.model.Cliente;
import proyecto.pos.service.ClienteService;

public class ClienteController {

    private ClienteService clienteService;

    public ClienteController(Connection conexion) {
        this.clienteService = new ClienteService(conexion);
    }

    // ========================
    // REGISTRAR CLIENTE
    // ========================
    public void registrarCliente(String tipoCliente, String nombre, String apellido,
                                 String dni, String telefono, String email,
                                 String direccion, Date fecha) {

        Cliente cliente = new Cliente();
        cliente.setTipoCliente(tipoCliente);
        cliente.setNombre(nombre);
        cliente.setApellidos(apellido);
        cliente.setDni(dni);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setDireccion(direccion);
        cliente.setFecha_registro(fecha);
        cliente.setPuntosFideldiad(0);
        clienteService.registrarCliente(cliente);
    }

    // ========================
    // OBTENER CLIENTE
    // ========================
    public Cliente obtenerPorId(int id) {
        return clienteService.obtenerClientePorId(id);
    }

    public Cliente obtenerPorDni(String dni) {
        return clienteService.obtenerClientePorDni(dni);
    }

    // ========================
    // LISTAR CLIENTES
    // ========================
    public List<Cliente> listarClientes() {
        return clienteService.listarClientes();
    }

    // ========================
    // ACTUALIZAR CLIENTE
    // ========================
    public void actualizarCliente(int id, String tipoCliente, String nombre,
                                  String apellido,String dni, String telefono,
                                  String email, String direccion, Date fecha,
                                  int puntos) {

        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setTipoCliente(tipoCliente);
        cliente.setNombre(nombre);
        cliente.setApellidos(apellido);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setDireccion(direccion);
        cliente.setPuntosFideldiad(puntos);
        cliente.setFecha_registro(fecha);
        clienteService.actualizarCliente(cliente);
    }

    // ========================
    // ELIMINAR CLIENTE
    // ========================
    public void eliminarPorId(int id) {
        clienteService.eliminarClientePorId(id);
    }

    public void eliminarPorDni(String dni) {
        clienteService.eliminarClientePorDni(dni);
    }

    // ========================
    // PUNTOS DE FIDELIDAD
    // ========================
    public void sumarPuntos(int idCliente, int puntos) {
        clienteService.sumarPuntos(idCliente, puntos);
    }

    public void canjearPuntos(int idCliente, int puntos) {
        clienteService.canjearPuntos(idCliente, puntos);
    }
}