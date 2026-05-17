package proyecto.pos.controller;

import java.sql.Connection;
import java.util.List;

import proyecto.pos.model.Mesa;
import proyecto.pos.service.MesaService;

public class MesaController {

    private MesaService mesaService;

    public MesaController(Connection conexion) {

        this.mesaService =
                new MesaService(conexion);
    }

    // =====================================================
    // REGISTRAR MESA
    // =====================================================
    public void registrarMesa(Mesa mesa) {

        mesaService.registrarMesa(mesa);
    }

    // =====================================================
    // LISTAR MESAS
    // =====================================================
    public List<Mesa> listarMesas() {

        return mesaService.listarMesas();
    }

    // =====================================================
    // OBTENER MESA POR ID
    // =====================================================
    public Mesa obtenerMesaPorId(int mesaId) {

        return mesaService.obtenerMesaPorId(
                mesaId
        );
    }

    // =====================================================
    // OBTENER MESA POR NUMERO
    // =====================================================
    public Mesa obtenerPorNumeroMesa(
            int numeroMesa
    ) {

        return mesaService.obtenerPorNumeroMesa(
                numeroMesa
        );
    }

    // =====================================================
    // ACTUALIZAR MESA
    // =====================================================
    public void actualizarMesa(Mesa mesa) {

        mesaService.actualizarMesa(mesa);
    }

    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================
    public void cambiarEstadoMesa(
            int mesaId,
            int estado
    ) {

        mesaService.cambiarEstadoMesa(
                mesaId,
                estado
        );
    }

    // =====================================================
    // LIBERAR MESA
    // =====================================================
    public void liberarMesa(int mesaId) {

        mesaService.liberarMesa(mesaId);
    }

    // =====================================================
    // OCUPAR MESA
    // =====================================================
    public void ocuparMesa(int mesaId) {

        mesaService.ocuparMesa(mesaId);
    }

    // =====================================================
    // RESERVAR MESA
    // =====================================================
    public void reservarMesa(int mesaId) {

        mesaService.reservarMesa(mesaId);
    }
}