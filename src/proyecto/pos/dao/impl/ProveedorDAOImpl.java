package proyecto.pos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import proyecto.pos.dao.interfaces.ProveedorDAO;
import proyecto.pos.model.Proveedor;

public class ProveedorDAOImpl implements ProveedorDAO {

    private Connection conexion;

    public ProveedorDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    // =========================
    // INSERTAR
    // =========================
    @Override
    public void insertar(Proveedor proveedor) {

        String sql = """
            INSERT INTO proveedores (
                ruc,
                nombre_empresa,
                contacto_nombre,
                telefono,
                email,
                direccion,
                codigo,
                tipo_insumo,
                region,
                cumplimiento,
                activo
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, proveedor.getRucDni());
            ps.setString(2, proveedor.getNombre());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getTelefono());
            ps.setString(5, proveedor.getEmail());
            ps.setString(6, proveedor.getDireccion());
            ps.setString(7, proveedor.getCodigo());
            ps.setString(8, proveedor.getTipoInsumo());
            ps.setString(9, proveedor.getRegion());
            ps.setInt(10, proveedor.getCumplimiento());
            ps.setInt(11, proveedor.isActivo() ? 1 : 0);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al insertar proveedor",
                    e
            );
        }
    }

    // =========================
    // OBTENER POR ID
    // =========================
    @Override
    public Proveedor obtenerPorId(int id) {

        String sql = """
            SELECT *
            FROM proveedores
            WHERE proveedor_id = ?
        """;

        Proveedor proveedor = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    proveedor = mapearProveedor(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener proveedor por ID: " + id,
                    e
            );
        }

        return proveedor;
    }

    // =========================
    // OBTENER POR RUC
    // =========================
    public Proveedor obtenerPorRUC(String ruc) {

        String sql = """
            SELECT *
            FROM proveedores
            WHERE ruc = ?
        """;

        Proveedor proveedor = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, ruc);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    proveedor = mapearProveedor(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener proveedor por RUC: " + ruc,
                    e
            );
        }

        return proveedor;
    }

    // =========================
    // LISTAR
    // =========================
    @Override
    public List<Proveedor> listar() {

        List<Proveedor> lista = new ArrayList<>();

        String sql = """
            SELECT
                proveedor_id,
                ruc,
                nombre_empresa,
                contacto_nombre,
                telefono,
                email,
                direccion,
                codigo,
                tipo_insumo,
                region,
                cumplimiento,
                activo
            FROM proveedores
            ORDER BY proveedor_id
        """;

        try (
                PreparedStatement ps = conexion.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        mapearProveedor(rs)
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al listar proveedores",
                    e
            );
        }

        return lista;
    }

    // =========================
    // ACTUALIZAR
    // =========================
    @Override
    public void actualizar(Proveedor proveedor) {

        String sql = """
            UPDATE proveedores SET
                ruc = ?,
                nombre_empresa = ?,
                contacto_nombre = ?,
                telefono = ?,
                email = ?,
                direccion = ?,
                codigo = ?,
                tipo_insumo = ?,
                region = ?,
                cumplimiento = ?,
                activo = ?
            WHERE proveedor_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, proveedor.getRucDni());
            ps.setString(2, proveedor.getNombre());
            ps.setString(3, proveedor.getContacto());
            ps.setString(4, proveedor.getTelefono());
            ps.setString(5, proveedor.getEmail());
            ps.setString(6, proveedor.getDireccion());
            ps.setString(7, proveedor.getCodigo());
            ps.setString(8, proveedor.getTipoInsumo());
            ps.setString(9, proveedor.getRegion());
            ps.setInt(10, proveedor.getCumplimiento());
            ps.setInt(11, proveedor.isActivo() ? 1 : 0);
            ps.setInt(12, proveedor.getId());

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al actualizar proveedor con ID: "
                    + proveedor.getId(),
                    e
            );
        }
    }

    // =========================
    // ELIMINAR
    // =========================
    @Override
    public void eliminar(int id) {

        String sql = """
            DELETE FROM proveedores
            WHERE proveedor_id = ?
        """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al eliminar proveedor con ID: " + id,
                    e
            );
        }
    }

    // =========================
    // MAPEAR PROVEEDOR
    // =========================
    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {

        Proveedor p = new Proveedor();

        p.setId(rs.getInt("proveedor_id"));
        p.setRucDni(rs.getString("ruc"));
        p.setNombre(rs.getString("nombre_empresa"));
        p.setContacto(rs.getString("contacto_nombre"));
        p.setTelefono(rs.getString("telefono"));
        p.setEmail(rs.getString("email"));
        p.setDireccion(rs.getString("direccion"));
        p.setCodigo(rs.getString("codigo"));
        p.setTipoInsumo(rs.getString("tipo_insumo"));
        p.setRegion(rs.getString("region"));
        p.setCumplimiento(rs.getInt("cumplimiento"));
        p.setActivo(rs.getInt("activo") == 1);

        return p;
    }

    @Override
    public Proveedor obtenerPorCodigo(String codigo) {
        String sql = """
            SELECT *
            FROM proveedores
            WHERE codigo = ?
        """;

        Proveedor proveedor = null;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, codigo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    proveedor = mapearProveedor(rs);
                }
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error al obtener proveedor por RUC: " + codigo,
                    e
            );
        }

        return proveedor;
    }
}