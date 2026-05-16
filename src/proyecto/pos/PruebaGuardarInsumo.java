///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package proyecto.pos;
//import java.sql.Connection;
//import proyecto.pos.dao.impl.InsumoDAOImpl;
//import proyecto.pos.model.Insumo;
//import proyecto.pos.model.Proveedor;
///**
// *
// * @author USER
// */
//
//public class PruebaGuardarInsumo {
//    public static void main(String[]args){
//Connection conexion = Conexion.getConexion();                            
//        if(conexion == null){
//            System.out.println("No se pudo conectar a la base de datos ");
//        return;
//        }
//        Proveedor proveedor = new Proveedor();
//        proveedor.setProveedorId(1);
//        
//        Insumo insumo = new Insumo();
//        insumo.setNombre("Arroz prueba");
//        insumo.setUnidadMedida("Kg");
//        insumo.setProveedor(proveedor);
//        insumo.setCosto(4.50f);
//        insumo.setCantidad(20);
//            
//        InsumoDAOImpl dao = new InsumoDAOImpl(conexion);
//        dao.insertar(insumo);
//        
//        
//        System.out.println("Insumo guardado correctamente");
//    }
//}
