/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.controller;

import proyecto.pos.model.AsistenciaTurno;
import proyecto.pos.service.AsistenciaTurnoService;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author HP
 */
public class AsistenciaTurnoController {

    private final AsistenciaTurnoService service;

    public AsistenciaTurnoController(Connection conexion) {
        this.service = new AsistenciaTurnoService(conexion);
    }

    public void registrarEntrada(int empleadoId) {
        service.registrarEntrada(empleadoId);
    }

    public void registrarSalida(int empleadoId) {
        service.registrarSalida(empleadoId);
    }

    public List<AsistenciaTurno> listarPorRangoFecha(LocalDate desde, LocalDate hasta) {
        return service.listarPorRangoFecha(desde, hasta);
    }

    public List<AsistenciaTurno> listarPorEmpleadoYRango(int empleadoId, LocalDate desde, LocalDate hasta) {
        return service.listarPorEmpleadoYRango(empleadoId, desde, hasta);
    }

    public List<AsistenciaTurno> listarTardanzasEInasistencias(LocalDate desde, LocalDate hasta) {
        return service.listarTardanzasEInasistencias(desde, hasta);
    }
}

