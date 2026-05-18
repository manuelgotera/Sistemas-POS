package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import proyecto.pos.controller.EmpleadoController;
import proyecto.pos.controller.UsuarioController;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Rol;

public class RegistrarUsuarioDialog extends JDialog {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtDni;
    private JComboBox<String> cbRol; 

    private JButton btnGuardar;
    private JButton btnCancelar;

    private UsuarioController usuarioController;
    private EmpleadoController empleado_controller;
    private List<Rol> roles;
    
    private static final Color AZUL = new Color(26, 35, 126);
    private static final Color FONDO = new Color(240, 242, 245);
    private static final Color CARD = Color.WHITE;

    public RegistrarUsuarioDialog(JFrame parent, Connection conexion) {
        super(parent, "Registro de Usuario", true);
        this.roles = new ArrayList<>();
        
        // Inicialización de controladores con la conexión activa
        this.usuarioController = new UsuarioController(conexion);
        this.empleado_controller = new EmpleadoController(conexion);
        
        setSize(450, 480); 
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        cargarRolesDesdeBD(); 
    }

    private void initComponents() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(FONDO);

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));
        card.setPreferredSize(new Dimension(360, 380));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0); 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.gridx = 0;

        // Título
        JLabel titulo = new JLabel("Crear Usuario", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridy = 0;
        card.add(titulo, gbc);

        Dimension textDim = new Dimension(280, 45); 

        // Inicializar componentes
        txtUsername = new JTextField();
        txtUsername.setBorder(BorderFactory.createTitledBorder("Username"));
        txtUsername.setPreferredSize(textDim); 
        gbc.gridy = 1;
        card.add(txtUsername, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Password"));
        txtPassword.setPreferredSize(textDim); 
        gbc.gridy = 2;
        card.add(txtPassword, gbc);

        txtDni = new JTextField();
        txtDni.setBorder(BorderFactory.createTitledBorder("DNI del Empleado (8 dígitos)"));
        txtDni.setPreferredSize(textDim); 
        gbc.gridy = 3;
        card.add(txtDni, gbc);

        cbRol = new JComboBox<>();
        cbRol.setBorder(BorderFactory.createTitledBorder("Rol"));
        cbRol.setPreferredSize(textDim); 
        gbc.gridy = 4;
        card.add(cbRol, gbc);

        // Botones
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(AZUL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setPreferredSize(new Dimension(120, 35)); 

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setForeground(AZUL);
        btnCancelar.setPreferredSize(new Dimension(120, 35)); 

        JPanel botones = new JPanel(new GridLayout(1, 2, 15, 0));
        botones.setOpaque(false);
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 0, 0); 
        card.add(botones, gbc);

        root.add(card);
        add(root);

        // Eventos
        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardarUsuario());
    }

    private void cargarRolesDesdeBD() {
        try {
            roles = usuarioController.listarRoles();
            cbRol.removeAllItems(); // Limpia elementos previos por seguridad
            for (Rol r : roles) {
                cbRol.addItem(r.getNombre_rol().toUpperCase());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar roles desde la base de datos: " + e.getMessage(), "Error de Carga", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void guardarUsuario() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String dni = txtDni.getText().trim();
        String rolSeleccionado = (String) cbRol.getSelectedItem();

        // 1. Validaciones básicas de la interfaz de usuario
        if (username.isEmpty() || password.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!dni.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(this, "El DNI debe contener exactamente 8 dígitos numéricos.", "Formato Inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (rolSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un Rol válido.", "Rol no Seleccionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 2. Validar existencia del empleado mediante su DNI real en la Base de Datos
            Empleado empleado = empleado_controller.obtenerPorDni(dni);
            if (empleado == null) {
                JOptionPane.showMessageDialog(this, "El DNI ingresado no pertenece a ningún empleado registrado.", "Empleado No Encontrado", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // 3. Mapear el String del ComboBox al objeto Rol correspondiente
            Rol rol = encontrarRol(rolSeleccionado);
            if (rol == null) {
                JOptionPane.showMessageDialog(this, "El rol seleccionado no es válido.", "Error de Rol", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 4. Ejecutar la persistencia real en la base de datos dentro del entorno seguro try-catch
            usuarioController.registrarUsuario(
                    username, 
                    password, 
                    "ACTIVO", 
                    empleado,
                    rol
            );

            // 5. Notificación de éxito y cierre de ventana
            JOptionPane.showMessageDialog(this, "¡Usuario creado y asociado a " + empleado.getNombre() + " con éxito!", "Registro Exitoso", JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } catch (Exception e) {
            // Captura violaciones de unicidad (UK_USERNAME), fallos en campos de base de datos u ORA-XXXX
            JOptionPane.showMessageDialog(this, "Error al procesar el registro: " + e.getMessage(), "Error en Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private Rol encontrarRol(String nombre_rol) {
        for (Rol r : roles) {
            String rol_nombre = r.getNombre_rol().toUpperCase();
            if (nombre_rol.equals(rol_nombre)) {
                return r;
            }
        }
        return null;
    }
}