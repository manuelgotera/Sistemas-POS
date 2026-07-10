package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.ClienteDAO;
import proyecto.pos.model.Cliente;
import proyecto.pos.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    private Connection conexion;

    public ClienteDAOImpl(Connection conexion){
        this.conexion = conexion;
    }

    @Override
    public void insertar(Cliente cliente) {

        String sql = "INSERT INTO clientes (tipo_cliente, nombre, apellido,"
                + " dni, telefono, email, direccion,"
                + " puntos_fidelidad, fecha_registro) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getTipoCliente());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getDireccion());
            ps.setInt(8, cliente.getPuntosFideldiad());
            ps.setDate(9, new java.sql.Date(cliente.getFecha_registro().getTime()));

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException("Error al insertar cliente", e);
        }
    }

    @Override
    public Cliente obtenerPorId(int id) {

        String sql = "SELECT * FROM clientes WHERE cliente_id = ?";
        Cliente cliente = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    cliente = mapearCliente(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error al obtener cliente por ID: " + id, e);
        }

        return cliente;
    }

    public Cliente obtenerPorDni(String dni) {

        String sql = "SELECT * FROM clientes WHERE dni = ?";
        Cliente cliente = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    cliente = mapearCliente(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error al obtener cliente por DNI: " + dni, e);
        }

        return cliente;
    }

    @Override
    public List<Cliente> listar() {

        List<Cliente> lista = new ArrayList<>();

        String sql = "SELECT cliente_id, tipo_cliente, nombre, apellido, "
                + "dni, telefono, email, direccion, puntos_fidelidad, "
                + "fecha_registro "
                + "FROM clientes";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearCliente(rs));
            }

        } catch (SQLException e) {

            throw new RuntimeException("Error al listar clientes", e);
        }

        return lista;
    }

    @Override
    public void actualizar(Cliente cliente) {

        String sql = "UPDATE clientes SET "
                + "tipo_cliente=?, "
                + "nombre=?, "
                + "apellido=?, "
                + "dni=?, "
                + "telefono=?, "
                + "email=?, "
                + "direccion=?, "
                + "puntos_fidelidad=? "
                + "WHERE cliente_id=?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, cliente.getTipoCliente());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getApellidos());
            ps.setString(4, cliente.getDni());
            ps.setString(5, cliente.getTelefono());
            ps.setString(6, cliente.getEmail());
            ps.setString(7, cliente.getDireccion());
            ps.setInt(8, cliente.getPuntosFideldiad());
            ps.setInt(9, cliente.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar cliente con ID: " + cliente.getId(),
                    e
            );
        }
    }

    @Override
    public void eliminar(int id) {

        String sql = "DELETE FROM clientes WHERE cliente_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al eliminar cliente con ID: " + id,
                    e
            );
        }
    }

    public void eliminarPorDni(String dni) {

        String sql = "DELETE FROM clientes WHERE dni = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, dni);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al eliminar cliente con DNI: " + dni,
                    e
            );
        }
    }

    public Cliente mapearCliente(ResultSet rs) throws SQLException {

        Cliente c = new Cliente();

        c.setId(rs.getInt("cliente_id"));
        c.setTipoCliente(rs.getString("tipo_cliente"));
        c.setNombre(rs.getString("nombre"));
        c.setApellidos(rs.getString("apellido"));
        c.setDni(rs.getString("dni"));
        c.setTelefono(rs.getString("telefono"));
        c.setEmail(rs.getString("email"));
        c.setDireccion(rs.getString("direccion"));
        c.setPuntosFideldiad(rs.getInt("puntos_fidelidad"));
        c.setFecha_registro(rs.getDate("fecha_registro"));

        return c;
    }
}
        
   
    
