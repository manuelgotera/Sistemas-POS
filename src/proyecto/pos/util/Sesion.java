/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.util;

/**
 *
 * @author LENOVO
 */


public class Sesion {

    private static final int EMPLEADO_ID_POR_DEFECTO = 174;

    private static Integer empleadoActualId = null;

    private Sesion() {
    }

    public static void setEmpleadoActualId(int id) {
        empleadoActualId = id;
    }

    public static int getEmpleadoActualId() {
        return empleadoActualId != null ? empleadoActualId : EMPLEADO_ID_POR_DEFECTO;
    }

    public static void cerrarSesion() {
        empleadoActualId = null;
    }
}
