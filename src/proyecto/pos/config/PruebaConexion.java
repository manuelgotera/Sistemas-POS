/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto.pos.config;

import java.sql.Connection;

public class PruebaConexion {

    public static void main(String[] args) {
        DatabaseConnection db = new DatabaseConnection();
        Connection conn = db.conectar();

        if (conn != null) {
            System.out.println("La base de datos ya está conectada con el proyecto.");
            db.desconectar(conn);
        } else {
            System.out.println("No se pudo conectar a la base de datos.");
        }
    }
}
