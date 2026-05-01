/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pos;
import java.sql.Connection;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.model.Cliente;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import proyecto.pos.dao.impl.InsumoDAOImpl;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Proveedor;
import proyecto.pos.*;
import proyecto.pos.dao.impl.EmpleadoDAOImpl;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Rol;
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
        Connection conexion = db.conectar();
        EmpleadoDAOImpl empleado_dao = new EmpleadoDAOImpl(conexion);
        
        Rol r1 = new Rol(1, "Administrador", "Acceso total al sistema");
        Rol r2 = new Rol(2, "Cajero", "Gestión de ventas y cobros");
        Rol r3 = new Rol(3, "Almacenero", "Control de inventario");
        Rol r4 = new Rol(4, "Supervisor", "Supervisa operaciones");
        Rol r5 = new Rol(5, "Soporte", "Atención al cliente y soporte técnico");
        
        Empleado e1 = new Empleado(r1,EstadoEmpleado.ACTIVO,new Date(),"Juanete","Pérez","12345789","987654321","juan@gmail.com");
        
        Empleado e2 = new Empleado(r2,EstadoEmpleado.ACTIVO,new Date(),"María","Gómez","23456899","987654322","maria@gmail.com");
        
        Empleado e3 = new Empleado(r3, EstadoEmpleado.INACTIVO, new Date(), "Luis","Ramírez","45967890","987654323","luis@gmail.com");
        
        Empleado empleado = empleado_dao.obtenerPorDni("12345789");
        empleado.setNombre("juanete");
        empleado_dao.actualizar(empleado);
        Empleado empleado_ = empleado_dao.obtenerPorDni("12345789");
        System.out.println(empleado_.toString());
    }
     
 }


 