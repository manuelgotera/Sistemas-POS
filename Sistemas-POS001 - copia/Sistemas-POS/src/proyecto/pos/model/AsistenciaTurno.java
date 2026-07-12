/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.model;
import java.time.LocalDateTime;
/**
 *
 * @author HP
 */
public class AsistenciaTurno {

    private int asistenciaId;
    private Empleado empleado;
    private LocalDateTime entrada;
    private LocalDateTime salida;

    public AsistenciaTurno(int asistenciaId, Empleado empleado,
                           LocalDateTime entrada, LocalDateTime salida) {
        this.asistenciaId = asistenciaId;
        this.empleado = empleado;
        this.entrada = entrada;
        this.salida = salida;
    }
}