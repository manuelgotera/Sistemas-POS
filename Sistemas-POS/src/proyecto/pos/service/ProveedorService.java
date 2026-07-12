package proyecto.pos.service;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.dao.impl.ProveedorDAOImpl;

import proyecto.pos.dao.interfaces.ProveedorDAO;
import proyecto.pos.model.Proveedor;

public class ProveedorService {

    private ProveedorDAO proveedorDAO;

    public ProveedorService(Connection conexion) {
        this.proveedorDAO = new ProveedorDAOImpl(conexion);
    }

    // =========================
    // REGISTRAR
    // =========================
    public void registrarProveedor(Proveedor proveedor) {

        validarProveedor(proveedor);

        // =========================
        // VALIDAR RUC DUPLICADO
        // =========================
        Proveedor existente =
                proveedorDAO.obtenerPorRUC(
                        proveedor.getRucDni()
                );

        if (existente != null) {

            throw new RuntimeException(
                    "Ya existe un proveedor con el RUC: "
                    + proveedor.getRucDni()
            );
        }

        proveedorDAO.insertar(proveedor);
    }

    // =========================
    // OBTENER POR ID
    // =========================
    public Proveedor obtenerProveedorPorId(int id) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "ID de proveedor inválido"
            );
        }

        return proveedorDAO.obtenerPorId(id);
    }

    // =========================
    // OBTENER POR RUC
    // =========================
    public Proveedor obtenerProveedorPorRUC(String ruc) {

        if (ruc == null || ruc.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El RUC no puede estar vacío"
            );
        }

        if (!ruc.matches("\\d{11}")) {

            throw new IllegalArgumentException(
                    "El RUC debe contener exactamente 11 dígitos"
            );
        }

        return proveedorDAO.obtenerPorRUC(ruc);
    }

    
    public Proveedor obtenerProveedorPorCodigo(String codigo) {

        if (codigo == null || codigo.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El codigo no puede estar vacío"
            );
        }

        return proveedorDAO.obtenerPorCodigo(codigo);
    }
    // =========================
    // LISTAR
    // =========================
    public List<Proveedor> listarProveedores() {

        return proveedorDAO.listar();
    }

    // =========================
    // ACTUALIZAR
    // =========================
    public void actualizarProveedor(Proveedor proveedor) {

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no puede ser null"
            );
        }

        if (proveedor.getId() <= 0) {

            throw new IllegalArgumentException(
                    "ID de proveedor inválido"
            );
        }

        validarProveedor(proveedor);

        // =========================
        // VALIDAR DUPLICADO RUC
        // =========================
        Proveedor existente =
                proveedorDAO.obtenerPorRUC(
                        proveedor.getRucDni()
                );

        if (existente != null
                && existente.getId() != proveedor.getId()) {

            throw new RuntimeException(
                    "Ya existe otro proveedor con el RUC: "
                    + proveedor.getRucDni()
            );
        }

        proveedorDAO.actualizar(proveedor);
    }

    // =========================
    // ELIMINAR
    // =========================
    public void eliminarProveedor(int id) {

        if (id <= 0) {

            throw new IllegalArgumentException(
                    "ID inválido"
            );
        }

        Proveedor proveedor =
                proveedorDAO.obtenerPorId(id);

        if (proveedor == null) {

            throw new RuntimeException(
                    "Proveedor no encontrado"
            );
        }

        proveedorDAO.eliminar(id);
    }

    // =========================
    // ACTIVAR
    // =========================
    public void activarProveedor(int id) {

        Proveedor proveedor =
                proveedorDAO.obtenerPorId(id);

        if (proveedor == null) {

            throw new RuntimeException(
                    "Proveedor no encontrado"
            );
        }

        proveedor.setActivo(true);

        proveedorDAO.actualizar(proveedor);
    }

    // =========================
    // DESACTIVAR
    // =========================
    public void desactivarProveedor(int id) {

        Proveedor proveedor =
                proveedorDAO.obtenerPorId(id);

        if (proveedor == null) {

            throw new RuntimeException(
                    "Proveedor no encontrado"
            );
        }

        proveedor.setActivo(false);

        proveedorDAO.actualizar(proveedor);
    }

    // =========================
    // VALIDACIONES
    // =========================
    private void validarProveedor(Proveedor proveedor) {

        if (proveedor == null) {

            throw new IllegalArgumentException(
                    "El proveedor no puede ser null"
            );
        }

        // =========================
        // NOMBRE
        // =========================
        if (proveedor.getNombre() == null
                || proveedor.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El nombre de empresa no puede estar vacío"
            );
        }

        if (proveedor.getNombre().length() > 150) {

            throw new IllegalArgumentException(
                    "El nombre de empresa no puede superar 150 caracteres"
            );
        }

        // =========================
        // RUC
        // =========================
        if (proveedor.getRucDni() == null
                || proveedor.getRucDni().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "El RUC no puede estar vacío"
            );
        }

        if (!proveedor.getRucDni().matches("\\d{11}")) {

            throw new IllegalArgumentException(
                    "El RUC debe contener exactamente 11 dígitos"
            );
        }

        // =========================
        // CONTACTO
        // =========================
        if (proveedor.getContacto() != null
                && proveedor.getContacto().length() > 100) {

            throw new IllegalArgumentException(
                    "El nombre de contacto no puede superar 100 caracteres"
            );
        }

        // =========================
        // TELEFONO
        // =========================
        if (proveedor.getTelefono() != null
                && !proveedor.getTelefono().trim().isEmpty()) {

            if (!proveedor.getTelefono().matches("\\d{7,20}")) {

                throw new IllegalArgumentException(
                        "El teléfono debe contener entre 7 y 20 dígitos"
                );
            }
        }

        // =========================
        // EMAIL
        // =========================
        if (proveedor.getEmail() != null
                && !proveedor.getEmail().trim().isEmpty()) {

            if (proveedor.getEmail().length() > 100) {

                throw new IllegalArgumentException(
                        "El email no puede superar 100 caracteres"
                );
            }

            if (!proveedor.getEmail().matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                throw new IllegalArgumentException(
                        "Formato de email inválido"
                );
            }
        }

        // =========================
        // DIRECCION
        // =========================
        if (proveedor.getDireccion() != null
                && proveedor.getDireccion().length() > 200) {

            throw new IllegalArgumentException(
                    "La dirección no puede superar 200 caracteres"
            );
        }

        // =========================
        // CODIGO
        // =========================
        if (proveedor.getCodigo() != null
                && proveedor.getCodigo().length() > 30) {

            throw new IllegalArgumentException(
                    "El código no puede superar 30 caracteres"
            );
        }

        // =========================
        // TIPO INSUMO
        // =========================
        if (proveedor.getTipoInsumo() != null
                && proveedor.getTipoInsumo().length() > 100) {

            throw new IllegalArgumentException(
                    "El tipo de insumo no puede superar 100 caracteres"
            );
        }

        // =========================
        // REGION
        // =========================
        if (proveedor.getRegion() != null
                && proveedor.getRegion().length() > 100) {

            throw new IllegalArgumentException(
                    "La región no puede superar 100 caracteres"
            );
        }

        // =========================
        // CUMPLIMIENTO
        // =========================
        if (proveedor.getCumplimiento() < 0
                || proveedor.getCumplimiento() > 100) {

            throw new IllegalArgumentException(
                    "El cumplimiento debe estar entre 0 y 100"
            );
        }
    }
}