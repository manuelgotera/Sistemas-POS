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

            // estado_mesa (CORREGIDO: Convertimos int a String para la BD)
            String estadoTexto = (mesa.getEstado_mesa() == 1) ? "Disponible" : "Ocupada";
            ps.setString(
                    3,
                    estadoTexto
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
        System.out.println("Buscando mesa...");
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
        System.out.println("Búsqueda finalizada.");
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

            // estado_mesa (CORREGIDO: Convertimos int a String para la BD)
            String estadoTexto = (mesa.getEstado_mesa() == 1) ? "Disponible" : "Ocupada";
            ps.setString(
                    3,
                    estadoTexto
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

            // CORREGIDO: Convertimos int a String para la BD
            String estadoTexto = (estado == 1) ? "Disponible" : "Ocupada";
            ps.setString(1, estadoTexto);

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

        // =====================================================
        // CORRECCIÓN PRINCIPAL DEL ERROR ORA-17059
        // Leemos el String de Oracle y lo convertimos a int para Java
        // =====================================================
        String estadoStr = rs.getString("estado_mesa");
        if (estadoStr != null && (estadoStr.equalsIgnoreCase("Disponible") || estadoStr.equals("1"))) {
            mesa.setEstado_mesa(1); // 1 = Disponible
        } else {
            mesa.setEstado_mesa(0); // 0 = Ocupada
        }

        return mesa;
    }
}