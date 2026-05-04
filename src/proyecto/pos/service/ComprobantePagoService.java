/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.service;

import java.util.Date;
import java.util.List;
import proyecto.pos.dao.interfaces.VentaDAO;
import proyecto.pos.model.ComprobantePago;

/**
 *
 * @author HP
 */
public class ComprobantePagoService {
    private VentaDAO ventaDAO;

    public ComprobantePagoService(VentaDAO ventaDAO) {
        this.ventaDAO = ventaDAO;
    }
    
}

}
