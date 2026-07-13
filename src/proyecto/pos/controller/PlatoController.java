package proyecto.pos.controller;

import java.sql.Connection;
import java.util.List;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;
import proyecto.pos.service.PlatoService;

public class PlatoController {

    private PlatoService platoService;

    public PlatoController(Connection conexion) {
        this.platoService = new PlatoService(conexion);
    }

    // ========================
    // REGISTRAR PLATO
    // ========================
    public void registrarPlato(
            String nombre,
            float precio,
            CategoriaMenu categoria,
            int disponible,
            String imagen
    ) {

        Plato plato = new Plato(
                nombre,
                precio,
                categoria,
                disponible,
                imagen
        );

        platoService.registrarPlato(plato);
    }

    // ========================
    // OBTENER PLATO
    // ========================
    public Plato obtenerPlatoPorId(int id) {
        return platoService.obtenerPlatoPorId(id);
        
    }
    
    public Plato obtenerPlatoPorNombre(String nombre){
        return platoService.obtenerPlatoPorNombre(nombre);
    }
    // ========================
    // LISTAR PLATOS
    // ========================
    public List<Plato> listarPlatos() {
        return platoService.listarPlatos();
    }
    
    public List<CategoriaMenu> listarCategorias(){
        return platoService.listarCategorias();
    }

    // ========================
    // ACTUALIZAR NOMBRE
    // ========================
    public void actualizarNombre(int platoId, String nombre) {
        platoService.actualizarNombre(platoId, nombre);
    }

    // ========================
    // ACTUALIZAR PRECIO
    // ========================
    public void actualizarPrecio(int platoId, float precio) {
        platoService.actualizarPrecio(platoId, precio);
    }

    // ========================
    // ACTUALIZAR CATEGORÍA
    // ========================
    public void actualizarCategoria(
            int platoId,
            CategoriaMenu categoria
    ) {

        platoService.actualizarCategoria(
                platoId,
                categoria
        );
    }

    // ========================
    // ACTUALIZAR DISPONIBILIDAD
    // ========================
    // ========================
    // ACTUALIZAR DISPONIBILIDAD (Modificado para HU-10)
    // ========================
    public void actualizarDisponibilidad(int platoId, int disponible) {
        // HU-10: Si se intenta cambiar el estado a 0 (No disponible), interceptar y pedir clave de gerente
        if (disponible == 0) {
            if (!solicitarAutorizacionGerente()) {
                System.out.println("[HU-10] Operación de desactivación cancelada: No autorizado.");
                return; // Corta la ejecución y no actualiza en la base de datos
            }
        }
        platoService.actualizarDisponibilidad(platoId, disponible);
    }

    // ========================
    // HABILITAR PLATO
    // ========================
    public void habilitarPlato(int platoId) {
        platoService.habilitarPlato(platoId);
    }

    // ========================
    // DESHABILITAR PLATO (Modificado para HU-10)
    // ========================
    public void deshabilitarPlato(int platoId) {
        // HU-10: Exigir pase de seguridad para dar de baja un producto de la carta
        if (solicitarAutorizacionGerente()) {
            platoService.deshabilitarPlato(platoId);
        } else {
            System.out.println("[HU-10] Desactivación denegada.");
        }
    }

    // =========================================================================
    // METODO AUXILIAR HU-10: Ventana de diálogo de seguridad ágil de Swing
    // =========================================================================
    private boolean solicitarAutorizacionGerente() {
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridLayout(2, 1, 5, 5));
        javax.swing.JLabel label = new javax.swing.JLabel("Autorización de Gerente Requerida (HU-10):");
        javax.swing.JPasswordField txtPassword = new javax.swing.JPasswordField();
        panel.add(label);
        panel.add(txtPassword);

        int option = javax.swing.JOptionPane.showConfirmDialog(
                null, 
                panel, 
                "Control de Acceso Manager", 
                javax.swing.JOptionPane.OK_CANCEL_OPTION, 
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == javax.swing.JOptionPane.OK_OPTION) {
            String pass = new String(txtPassword.getPassword());
            // Credenciales maestras estables de contingencia del sistema
            if ("admin123".equals(pass) || "gerente2026".equals(pass)) {
                return true;
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                        null, 
                        "Código incorrecto. Acción cancelada.", 
                        "Error de Autenticación", 
                        javax.swing.JOptionPane.ERROR_MESSAGE
                );
            }
        }
        return false;
    }
}