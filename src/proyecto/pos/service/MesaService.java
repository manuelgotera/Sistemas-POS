package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;

import proyecto.pos.dao.interfaces.MesaDAO;
import proyecto.pos.dao.impl.MesaDAOImpl;
import proyecto.pos.model.Mesa;

public class MesaService {

    private MesaDAO mesaDAO;

    // =========================
    // ESTADOS
    // =========================
    public static final int LIBRE = 0;
    public static final int OCUPADA = 1;
    public static final int RESERVADA = 2;

    public MesaService(Connection conexion) {

        this.mesaDAO =
                new MesaDAOImpl(conexion);
    }

    // =====================================================
    // REGISTRAR MESA
    // =====================================================
    public void registrarMesa(Mesa mesa) {

        // =========================
        // VALIDAR OBJETO
        // =========================
        if (mesa == null) {

            throw new IllegalArgumentException(
                    "La mesa no puede ser null"
            );
        }

        // =========================
        // VALIDAR NUMERO
        // =========================
        if (mesa.getNumero_mesa() <= 0) {

            throw new IllegalArgumentException(
                    "Número de mesa inválido"
            );
        }

        // =========================
        // VALIDAR DUPLICADOS
        // =========================
        Mesa existente =
                mesaDAO.obtenerPorNumeroMesa(
                        mesa.getNumero_mesa()
                );

        if (existente != null) {

            throw new IllegalArgumentException(
                    "Ya existe una mesa con ese número"
            );
        }

        // =========================
        // VALIDAR CAPACIDAD
        // =========================
        if (mesa.getCapacidad() <= 0) {

            throw new IllegalArgumentException(
                    "La capacidad debe ser mayor a 0"
            );
        }

        // =========================
        // VALIDAR ESTADO
        // =========================
        if (
                mesa.getEstado_mesa() != LIBRE
                &&
                mesa.getEstado_mesa() != OCUPADA
                &&
                mesa.getEstado_mesa() != RESERVADA
        ) {

            mesa.setEstado_mesa(LIBRE);
        }

        mesaDAO.insertar(mesa);
    }

    // =====================================================
    // LISTAR MESAS
    // =====================================================
    public List<Mesa> listarMesas() {

        return mesaDAO.listar();
    }

    // =====================================================
    // OBTENER POR ID
    // =====================================================
    public Mesa obtenerMesaPorId(int mesaId) {

        if (mesaId <= 0) {

            throw new IllegalArgumentException(
                    "ID de mesa inválido"
            );
        }

        return mesaDAO.obtenerPorId(mesaId);
    }

    // =====================================================
    // OBTENER POR NUMERO
    // =====================================================
    public Mesa obtenerPorNumeroMesa(
            int numeroMesa
    ) {

        if (numeroMesa <= 0) {

            throw new IllegalArgumentException(
                    "Número de mesa inválido"
            );
        }

        return mesaDAO.obtenerPorNumeroMesa(
                numeroMesa
        );
    }

    // =====================================================
    // ACTUALIZAR MESA
    // =====================================================
    public void actualizarMesa(Mesa mesa) {

        // =========================
        // VALIDAR OBJETO
        // =========================
        if (mesa == null) {

            throw new IllegalArgumentException(
                    "La mesa no puede ser null"
            );
        }

        // =========================
        // VALIDAR ID
        // =========================
        if (mesa.getMesaId() <= 0) {

            throw new IllegalArgumentException(
                    "ID de mesa inválido"
            );
        }

        // =========================
        // VALIDAR NUMERO
        // =========================
        if (mesa.getNumero_mesa() <= 0) {

            throw new IllegalArgumentException(
                    "Número de mesa inválido"
            );
        }

        // =========================
        // VALIDAR CAPACIDAD
        // =========================
        if (mesa.getCapacidad() <= 0) {

            throw new IllegalArgumentException(
                    "Capacidad inválida"
            );
        }

        // =========================
        // VALIDAR ESTADO
        // =========================
        if (
                mesa.getEstado_mesa() != LIBRE
                &&
                mesa.getEstado_mesa() != OCUPADA
                &&
                mesa.getEstado_mesa() != RESERVADA
        ) {

            throw new IllegalArgumentException(
                    "Estado de mesa inválido"
            );
        }

        // =========================
        // VALIDAR EXISTENCIA
        // =========================
        Mesa actual =
                mesaDAO.obtenerPorId(
                        mesa.getMesaId()
                );

        if (actual == null) {

            throw new RuntimeException(
                    "Mesa no encontrada"
            );
        }

        // =========================
        // VALIDAR DUPLICADO
        // =========================
        Mesa otraMesa =
                mesaDAO.obtenerPorNumeroMesa(
                        mesa.getNumero_mesa()
                );

        if (
                otraMesa != null
                &&
                otraMesa.getMesaId()
                != mesa.getMesaId()
        ) {

            throw new IllegalArgumentException(
                    "Ya existe otra mesa con ese número"
            );
        }

        mesaDAO.actualizar(mesa);
    }

    // =====================================================
    // CAMBIAR ESTADO
    // =====================================================
    public void cambiarEstadoMesa(
            int mesaId,
            int estado
    ) {

        if (mesaId <= 0) {

            throw new IllegalArgumentException(
                    "ID de mesa inválido"
            );
        }

        if (
                estado != LIBRE
                &&
                estado != OCUPADA
                &&
                estado != RESERVADA
        ) {

            throw new IllegalArgumentException(
                    "Estado inválido"
            );
        }

        Mesa mesa =
                mesaDAO.obtenerPorId(mesaId);

        if (mesa == null) {

            throw new RuntimeException(
                    "Mesa no encontrada"
            );
        }

        mesaDAO.cambiarEstado(
                mesaId,
                estado
        );
    }

    // =====================================================
    // LIBERAR MESA
    // =====================================================
    public void liberarMesa(int mesaId) {

        cambiarEstadoMesa(
                mesaId,
                LIBRE
        );
    }

    // =====================================================
    // OCUPAR MESA
    // =====================================================
    public void ocuparMesa(int mesaId) {

        cambiarEstadoMesa(
                mesaId,
                OCUPADA
        );
    }

    // =====================================================
    // RESERVAR MESA
    // =====================================================
    public void reservarMesa(int mesaId) {

        cambiarEstadoMesa(
                mesaId,
                RESERVADA
        );
    }
}