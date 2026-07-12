package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import proyecto.pos.dao.interfaces.ClienteDAO;
import proyecto.pos.model.Cliente;

public class ClienteService {

    private ClienteDAO clienteDAO;

    public ClienteService(Connection conexion) {
        this.clienteDAO = new ClienteDAOImpl(conexion);
    }

    // ========================
    // REGISTRAR CLIENTE
    // ========================
    public void registrarCliente(Cliente cliente) {

        // Lógica de negocio básica (validaciones)
        if (cliente.getNombre() == null || cliente.getNombre().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }

        if (cliente.getDni() == null || cliente.getDni().length() != 8) {
            throw new IllegalArgumentException("DNI inválido");
        }

        // puntos iniciales si no se asignan
        if (cliente.getPuntosFideldiad() < 0) {
            cliente.setPuntosFideldiad(0);
        }

        // fecha de registro automática si viene null
        if (cliente.getFecha_registro() == null) {
            cliente.setFecha_registro(new java.util.Date());
        }

        clienteDAO.insertar(cliente);
    }

    // ========================
    // OBTENER CLIENTE
    // ========================
    public Cliente obtenerClientePorId(int id) {
        return clienteDAO.obtenerPorId(id);
    }

    public Cliente obtenerClientePorDni(String dni) {
        return clienteDAO.obtenerPorDni(dni);
    }

    // ========================
    // LISTAR CLIENTES
    // ========================
    public List<Cliente> listarClientes() {
        return clienteDAO.listar();
    }

    // ========================
    // ACTUALIZAR CLIENTE
    // ========================
    public void actualizarCliente(Cliente cliente) {

        if (cliente.getId() <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido");
        }

        clienteDAO.actualizar(cliente);
    }

    // ========================
    // ELIMINAR CLIENTE
    // ========================
    public void eliminarClientePorId(int id) {
        clienteDAO.eliminar(id);
    }

    public void eliminarClientePorDni(String dni) {
        clienteDAO.eliminarPorDni(dni);
    }

    // ========================
    // REGLAS DE NEGOCIO EXTRA (OPCIONAL)
    // ========================
    
    public void sumarPuntos(int clienteId, int puntos) {
        Cliente cliente = clienteDAO.obtenerPorId(clienteId);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        cliente.setPuntosFideldiad(cliente.getPuntosFideldiad() + puntos);

        clienteDAO.actualizar(cliente);
    }

    public void canjearPuntos(int clienteId, int puntos) {
        Cliente cliente = clienteDAO.obtenerPorId(clienteId);

        if (cliente == null) {
            throw new RuntimeException("Cliente no encontrado");
        }

        if (cliente.getPuntosFideldiad() < puntos) {
            throw new IllegalArgumentException("Puntos insuficientes");
        }

        cliente.setPuntosFideldiad(cliente.getPuntosFideldiad() - puntos);

        clienteDAO.actualizar(cliente);
    }
}