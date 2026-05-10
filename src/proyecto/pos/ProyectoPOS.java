/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pos;
import proyecto.pos.config.DatabaseConnection;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import proyecto.pos.dao.impl.CajaDAOImpl;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import proyecto.pos.dao.impl.EmpleadoDAOImpl;
import proyecto.pos.dao.impl.InsumoDAOImpl;
import proyecto.pos.dao.impl.MermaDAOImpl;
import proyecto.pos.dao.impl.PlatoDAOImpl;
import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.dao.interfaces.*;
import proyecto.pos.gui.ArticulosStockFrame;
import proyecto.pos.gui.HistorialTransaccionesFrame;
import proyecto.pos.gui.TestHistorial;
import proyecto.pos.model.*;
import proyecto.pos.service.VentaService;

/**
 *
 * @author User
 */
public class ProyectoPOS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        Connection c = db.conectar();
        
           EmpleadoDAO empleado_dao = new EmpleadoDAOImpl(c);
        ClienteDAO cliente_dao = new ClienteDAOImpl(c);
        Cliente cl = cliente_dao.listar().get(2);
        Empleado e = empleado_dao.listar().get(3);
        
        
        System.out.println(cl.toString());
    }
    
    public static void venta(){
        //metodo de recontramrd 
        DatabaseConnection db = new DatabaseConnection();
        Connection conexion = db.conectar();
        ClienteDAOImpl cliente_dao = new ClienteDAOImpl(conexion);
        PlatoDAOImpl plato_dao = new PlatoDAOImpl(conexion);
        EmpleadoDAOImpl empleado_dao = new EmpleadoDAOImpl(conexion);
        VentaDAOImpl venta_dao = new VentaDAOImpl(conexion);
        
        ArrayList<VentaDetalle> ventas_detalles = new ArrayList<>();
        ArrayList<ComprobantePago> comprobantes = new ArrayList<>();
        
        Cliente c1 = cliente_dao.obtenerPorId(1);
        Cliente c2 = cliente_dao.obtenerPorId(2);
        
        Empleado e1 = empleado_dao.obtenerPorId(24);
        Empleado e2 = empleado_dao.obtenerPorId(25);
        
        Date f1 = new Date();
        Date f2 = new Date();
        
        Mesa m1 = new Mesa(1,2,2,2);
    
        Plato p1 = plato_dao.obtenerPorId(1);
        Plato p2 = plato_dao.obtenerPorId(2);
    
        VentaDetalle vd1 = new VentaDetalle(p1,3,2,5,"xd");
        VentaDetalle vd2 = new VentaDetalle(p2,3,2,5,"xd");
        
        ventas_detalles.add(vd1);
        ventas_detalles.add(vd2);
        
        
        MetodoPago mp1 = new MetodoPago(1,"YAPE");
        MetodoPago mp2 = new MetodoPago(2,"BCP");
    
        ComprobantePago cp1 = new ComprobantePago("FACTURA", "221", mp1, f1, "PAGADO");
        ComprobantePago cp2 = new ComprobantePago("BOLETA", "1423", mp2, f1, "PAGADO");
        
        comprobantes.add(cp1);
        comprobantes.add(cp2);
        
        /*Venta venta = new Venta(c1,e1,f1,ventas_detalles,9,7,6,m1,EstadoPago.PAGADO,comprobantes);
        venta_dao.insertar(venta);*/
        
        ArrayList<Venta> ventas = (ArrayList<Venta>) venta_dao.listar();
        for (Venta v : ventas){
            System.out.println(v.toString());
        }
        /*System.out.println(ventas.toString());
        Venta v1 = venta_dao.obtenerPorId(42);
        System.out.println(v1);*/
        
        /*cp1.setSerie_numero("xddxxddx1");
        cp2.setSerie_numero("eoo2");
        Venta v2 = venta_dao.obtenerPorId(13);
        v2.setComprobantes(comprobantes);
        v2.setDetalles(ventas_detalles);
        venta_dao.actualizar(v2);
        v2 = venta_dao.obtenerPorId(13);
        System.out.println(v2.toString());*/
    }
     
 }


 