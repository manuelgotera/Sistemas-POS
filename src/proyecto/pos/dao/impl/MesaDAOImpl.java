package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.MesaDAO;
import proyecto.pos.model.Mesa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesaDAOImpl implements MesaDAO {

    private Connection conexion;

    public MesaDAOImpl() {

    }

    public MesaDAOImpl(Connection conexion) {

        this.conexion = conexion;
    }

    // =====================================================
    // INSERTAR
    // =====================================================
    @Override
    public void insertar(Mesa mesa) {

        String sql = """
            INSERT INTO mesas
            (
                numero_mesa,
                capacidad,
                estado_mesa
            )
            VALUES (?, ?, ?)
        """;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
        ) {

            // numero_mesa
            ps.setInt(
                    1,
                    mesa.getNumero_mesa()
            );

            // capacidad
            ps.setInt(
                    2,
                    mesa.getCapacidad()
            );

            // estado_mesa
            ps.setInt(
                    3,
                    mesa.getEstado_mesa()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // LISTAR
    // =====================================================
    @Override
    public List<Mesa> listar() {

        List<Mesa> lista = new ArrayList<>();

        String sql = """
            SELECT
                mesa_id,
                numero_mesa,
                capacidad,
                estado_mesa
            FROM mesas
            ORDER BY numero_mesa
        """;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql);

            ResultSet rs =
                    ps.executeQuery()
        ) {

            while (rs.next()) {

                lista.add(
                        mapearMesa(rs)
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return lista;
    }

    // =====================================================
    // OBTENER POR ID
    // =====================================================
    @Override
    public Mesa obtenerPorId(int id) {

        String sql = """
            SELECT
                mesa_id,
                numero_mesa,
                capacidad,
                estado_mesa
            FROM mesas
            WHERE mesa_id = ?
        """;

        Mesa mesa = null;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, id);

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                if (rs.next()) {

                    mesa = mapearMesa(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }

        return mesa;
    }

    // =====================================================
    // OBTENER POR NUMERO DE MESA
    // =====================================================
    @Override
    public Mesa obtenerPorNumeroMesa(int numeroMesa) {
        System.out.println("ptmrrrrrrrrrrrrrr");
        String sql = """
            SELECT
                mesa_id,
                numero_mesa,
                capacidad,
                estado_mesa
            FROM mesas
            WHERE numero_mesa = ?
        """;

        Mesa mesa = null;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, numeroMesa);

            try (
                ResultSet rs =
                        ps.executeQuery()
            ) {

                if (rs.next()) {

                    mesa = mapearMesa(rs);
                }
            }

        } catch (SQLException e) {

            e.printStackTrace();
        }
        System.out.println("xddd");
        return mesa;
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================
    @Override
    public void actualizar(Mesa mesa) {

        String sql = """
            UPDATE mesas
            SET
                numero_mesa = ?,
                capacidad = ?,
                estado_mesa = ?
            WHERE mesa_id = ?
        """;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
        ) {

            ps.setInt(
                    1,
                    mesa.getNumero_mesa()
            );

            ps.setInt(
                    2,
                    mesa.getCapacidad()
            );

            ps.setInt(
                    3,
                    mesa.getEstado_mesa()
            );

            ps.setInt(
                    4,
                    mesa.getMesaId()
            );

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================
    @Override
    public void cambiarEstado(
            int mesaId,
            int estado
    ) {

        String sql = """
            UPDATE mesas
            SET estado_mesa = ?
            WHERE mesa_id = ?
        """;

        try (
            PreparedStatement ps =
                    conexion.prepareStatement(sql)
        ) {

            ps.setInt(1, estado);

            ps.setInt(2, mesaId);

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();
        }
    }

    // =====================================================
    // MAPEAR MESA
    // =====================================================
    private Mesa mapearMesa(ResultSet rs)
            throws SQLException {

        Mesa mesa = new Mesa();

        mesa.setMesaId(
                rs.getInt("mesa_id")
        );

        mesa.setNumero_mesa(
                rs.getInt("numero_mesa")
        );

        mesa.setCapacidad(
                rs.getInt("capacidad")
        );

        mesa.setEstado_mesa(
                rs.getInt("estado_mesa")
        );

        return mesa;
    }


}