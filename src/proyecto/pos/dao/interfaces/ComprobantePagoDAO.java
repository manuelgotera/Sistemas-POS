/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package proyecto.pos.dao.interfaces;

import java.util.List;
import proyecto.pos.model.ComprobantePago;

/**
 *
 * @author HP
 */
public interface ComprobantePagoDAO {
    void insertar(ComprobantePago comprobantePago);
    ComprobantePago obtenerPorId(int id);
    ComprobantePago obtenerPorNumeroSerie(String numeroSerie);
    ComprobantePago obtenerPorVentaId(int venta_id);
    List<ComprobantePago> listar();
    void actualizar(ComprobantePago comprobantePago);
    void eliminar(int id);
    void eliminarPorNumeroSerie(String numeroSerie);
}
