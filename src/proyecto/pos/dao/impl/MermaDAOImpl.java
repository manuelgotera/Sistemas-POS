package proyecto.pos.dao.impl;

import proyecto.pos.dao.interfaces.MermaDAO;
import proyecto.pos.model.Merma;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Empleado;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MermaDAOImpl implements MermaDAO {

    private final Connection conexion;

    public MermaDAOImpl(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void insertar(Merma merma) {
        String sql = "INSERT INTO mermas (insumo_id, empleado_id, cantidad, motivo, fecha_registro) VALUES (?, ?, ?, ?, ?)";
        ejecutarUpdate(sql, 
            merma.getInsumo().getInsumoId(), 
            merma.getEmpleado().getId(), 
            merma.getCantidad(), 
            merma.getMotivo(), 
            new java.sql.Date(merma.getFecha_registro().getTime())
        );
    }

    @Override
    public Merma obtenerPorId(int id) {
        String sql = "SELECT * FROM mermas WHERE merma_id = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearMerma(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Merma> listar() {
        List<Merma> lista = new ArrayList<>();
        String sql = "SELECT * FROM mermas";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMerma(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lista;
    }

    @Override
    public void actualizar(Merma merma) {
        String sql = "UPDATE mermas SET insumo_id=?, empleado_id=?, cantidad=?, motivo=? WHERE merma_id=?";
        ejecutarUpdate(sql, 
            merma.getInsumo().getInsumoId(), 
            merma.getEmpleado().getId(), 
            merma.getCantidad(), 
            merma.getMotivo(), 
            merma.getMermaId()
        );
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM mermas WHERE merma_id = ?";
        ejecutarUpdate(sql, id);
    }

    private void ejecutarUpdate(String sql, Object... params) {
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Merma mapearMerma(ResultSet rs) throws SQLException {
        Merma m = new Merma();
        m.setMermaId(rs.getInt("merma_id"));
        
        Insumo ins = new Insumo();
        ins.setInsumoId(rs.getInt("insumo_id"));
        m.setInsumo(ins);
        
        Empleado emp = new Empleado();
        emp.setId(rs.getInt("empleado_id"));
        m.setEmpleado(emp);
        
        m.setCantidad(rs.getDouble("cantidad"));
        m.setMotivo(rs.getString("motivo"));
        m.setFecha_registro(rs.getDate("fecha_registro"));
        
        return m;
    }
}