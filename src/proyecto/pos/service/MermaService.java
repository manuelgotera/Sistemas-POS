package proyecto.pos.service;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import proyecto.pos.dao.impl.MermaDAOImpl;
import proyecto.pos.dao.interfaces.MermaDAO;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Merma;

public class MermaService {

    private MermaDAO mermaDAO;
    private InsumoService insumo_service;
    public MermaService(Connection conexion) {
        this.mermaDAO = new MermaDAOImpl(conexion);
        this.insumo_service = new InsumoService(conexion);
    }

    // ========================
    // REGISTRAR MERMA
    // ========================
    public void registrarMerma(Merma merma) {

        validarMerma(merma);

        // Fecha automática
        if (merma.getFecha_registro() == null) {
            merma.setFecha_registro(new Date());
        }

        mermaDAO.insertar(merma);
    }

    // ========================
    // OBTENER MERMA
    // ========================
    public Merma obtenerMermaPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID de merma inválido");
        }

        return mermaDAO.obtenerPorId(id);
    }

    // ========================
    // LISTAR MERMAS
    // ========================
    public List<Merma> listarMermas() {
        return mermaDAO.listar();
    }

    // ========================
    // ACTUALIZAR MERMA
    // ========================
    public void actualizarMerma(Merma merma) {

        if (merma == null) {
            throw new IllegalArgumentException("La merma no puede ser null");
        }

        if (merma.getMermaId() <= 0) {
            throw new IllegalArgumentException("ID de merma inválido");
        }

        validarMerma(merma);

        mermaDAO.actualizar(merma);
    }

    // ========================
    // ELIMINAR MERMA
    // ========================
    public void eliminarMermaPorId(int id) {

        if (id <= 0) {
            throw new IllegalArgumentException("ID de merma inválido");
        }

        Merma merma = mermaDAO.obtenerPorId(id);

        if (merma == null) {
            throw new RuntimeException("La merma no existe");
        }

        mermaDAO.eliminar(id);
    }

    // ========================
    // VALIDACIONES
    // ========================
    private void validarMerma(Merma merma) {

        // ========================
        // VALIDAR OBJETO
        // ========================
        if (merma == null) {
            throw new IllegalArgumentException("La merma no puede ser null");
        }

        // ========================
        // VALIDAR INSUMO
        // ========================
        if (merma.getInsumo() == null) {
            throw new IllegalArgumentException("Debe seleccionar un insumo");
        }

        Insumo insumo = merma.getInsumo();

        if (insumo.getInsumoId() <= 0) {
            throw new IllegalArgumentException("ID de insumo inválido");
        }

        // ========================
        // VALIDAR EMPLEADO
        // ========================
        if (merma.getEmpleado() == null) {
            throw new IllegalArgumentException("Debe seleccionar un empleado");
        }

        if (merma.getEmpleado().getId() <= 0) {
            throw new IllegalArgumentException("ID de empleado inválido");
        }

        // ========================
        // VALIDAR CANTIDAD
        // ========================
        if (merma.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }

        if (merma.getCantidad() > 100000) {
            throw new IllegalArgumentException("La cantidad es demasiado grande");
        }

        // ========================
        // VALIDAR STOCK
        // ========================
        if (insumo.getCantidad() < merma.getCantidad()) {
            throw new IllegalArgumentException(
                    "La cantidad de merma supera el stock disponible"
            );
        }

        // ========================
        // VALIDAR MOTIVO
        // ========================
        if (merma.getMotivo() == null) {
            throw new IllegalArgumentException("El motivo no puede ser null");
        }

        String motivo = merma.getMotivo().trim();

        if (motivo.isEmpty()) {
            throw new IllegalArgumentException("El motivo no puede estar vacío");
        }

        if (motivo.length() < 5) {
            throw new IllegalArgumentException(
                    "El motivo debe tener al menos 5 caracteres"
            );
        }

        if (motivo.length() > 255) {
            throw new IllegalArgumentException(
                    "El motivo no puede superar los 255 caracteres"
            );
        }

        // ========================
        // VALIDAR FECHA
        // ========================
        if (merma.getFecha_registro() != null) {

            Date fechaActual = new Date();

            if (merma.getFecha_registro().after(fechaActual)) {
                throw new IllegalArgumentException(
                        "La fecha no puede ser futura"
                );
            }
        }
    }
    
    
    public void actualizarInsumo(Merma merma){
        int id_insumo = merma.getInsumo().getInsumoId();
        insumo_service.disminuirStock(id_insumo, (float) merma.getCantidad());
    }
    // ========================
    // REGLAS DE NEGOCIO EXTRA
    // ========================

    public boolean esMermaAlta(Merma merma) {

        if (merma == null) {
            return false;
        }

        return merma.getCantidad() >= 10;
    }

    public boolean tieneMotivo(Merma merma, String texto) {

        if (merma == null || merma.getMotivo() == null) {
            return false;
        }

        if (texto == null || texto.trim().isEmpty()) {
            return false;
        }

        return merma.getMotivo()
                .toLowerCase()
                .contains(texto.toLowerCase());
    }
}