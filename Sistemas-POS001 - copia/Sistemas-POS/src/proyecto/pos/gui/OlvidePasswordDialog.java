package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import proyecto.pos.controller.EmpleadoController;
import proyecto.pos.controller.UsuarioController;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.Usuario;

public class OlvidePasswordDialog extends JDialog {

    private JTextField txtUsername;
    private JTextField txtDni;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;
    
    private JButton btnVerificar;
    private JButton btnCambiarPassword;
    private JButton btnCancelar;
    
    private JPanel panelVerificacion;
    private JPanel panelCambioClave;

    // 📌 Controladores conectados a la BD
    private UsuarioController usuarioController;
    private EmpleadoController empleado_coontroller;
    private Usuario usuarioEncontrado; 

    private static final Color AZUL = new Color(26, 35, 126);
    private static final Color FONDO = new Color(240, 242, 245);
    private static final Color CARD = Color.WHITE;

    public OlvidePasswordDialog(JFrame parent, Connection conexion) {
        super(parent, "Recuperar Contraseña", true);
        
        this.empleado_coontroller = new EmpleadoController(conexion);
        // 📌 Inicializamos controladores con la conexión activa de Oracle
        this.usuarioController = new UsuarioController(conexion);

        setSize(420, 460);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);

        JPanel card = new JPanel(new CardLayout()); 
        card.setBackground(CARD);
        card.setBorder(new EmptyBorder(25, 30, 25, 30));

        Dimension dimCampos = new Dimension(280, 45);

        // ==========================================
        // PASO 1: PANEL DE VERIFICACIÓN (Username + DNI)
        // ==========================================
        panelVerificacion = new JPanel(new GridBagLayout());
        panelVerificacion.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblTitulo1 = new JLabel("Verifica tu Identidad", SwingConstants.CENTER);
        lblTitulo1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 0;
        panelVerificacion.add(lblTitulo1, gbc);

        txtUsername = new JTextField();
        txtUsername.setBorder(BorderFactory.createTitledBorder("Nombre de Usuario"));
        txtUsername.setPreferredSize(dimCampos);
        gbc.gridy = 1;
        panelVerificacion.add(txtUsername, gbc);

        txtDni = new JTextField();
        txtDni.setBorder(BorderFactory.createTitledBorder("DNI Registrado (8 dígitos)"));
        txtDni.setPreferredSize(dimCampos);
        gbc.gridy = 2;
        panelVerificacion.add(txtDni, gbc);

        btnVerificar = new JButton("Verificar Datos");
        btnVerificar.setBackground(AZUL);
        btnVerificar.setForeground(Color.WHITE);
        btnVerificar.setPreferredSize(new Dimension(280, 40));
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelVerificacion.add(btnVerificar, gbc);

        // ==========================================
        // PASO 2: PANEL DE CAMBIO DE CONTRASEÑA
        // ==========================================
        panelCambioClave = new JPanel(new GridBagLayout());
        panelCambioClave.setOpaque(false);

        JLabel lblTitulo2 = new JLabel("Establecer Nueva Contraseña", SwingConstants.CENTER);
        lblTitulo2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridy = 0;
        panelCambioClave.add(lblTitulo2, gbc);

        txtNuevaPassword = new JPasswordField();
        txtNuevaPassword.setBorder(BorderFactory.createTitledBorder("Nueva Contraseña"));
        txtNuevaPassword.setPreferredSize(dimCampos);
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        panelCambioClave.add(txtNuevaPassword, gbc);

        txtConfirmarPassword = new JPasswordField();
        txtConfirmarPassword.setBorder(BorderFactory.createTitledBorder("Confirmar Contraseña"));
        txtConfirmarPassword.setPreferredSize(dimCampos);
        gbc.gridy = 2;
        panelCambioClave.add(txtConfirmarPassword, gbc);

        btnCambiarPassword = new JButton("Actualizar Contraseña");
        btnCambiarPassword.setBackground(new Color(46, 125, 50)); 
        btnCambiarPassword.setForeground(Color.WHITE);
        btnCambiarPassword.setPreferredSize(new Dimension(280, 40));
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 0, 0, 0);
        panelCambioClave.add(btnCambiarPassword, gbc);

        card.add(panelVerificacion, "Paso1");
        card.add(panelCambioClave, "Paso2");

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBorderPainted(false);
        btnCancelar.setContentAreaFilled(false);
        btnCancelar.setForeground(Color.GRAY);
        
        root.add(card, BorderLayout.CENTER);
        root.add(btnCancelar, BorderLayout.SOUTH);
        add(root);

        // Eventos
        btnCancelar.addActionListener(e -> dispose());
        btnVerificar.addActionListener(e -> verificarIdentidad((CardLayout) card.getLayout(), card));
        btnCambiarPassword.addActionListener(e -> procesarCambioClave());
    }

    /**
     * 📌 VALIDACIÓN REAL CONTRA ORACLE
     */
    private void verificarIdentidad(CardLayout cl, JPanel contenedor) {
        String username = txtUsername.getText().trim();
        String dniDigitado = txtDni.getText().trim();

        if (username.isEmpty() || dniDigitado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete ambos campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Buscamos si el usuario existe en la base de datos
            usuarioEncontrado = usuarioController.obtenerPorUsername(username);

            if (usuarioEncontrado == null) {
                JOptionPane.showMessageDialog(this, "El nombre de usuario no existe en el sistema.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Traemos los datos completos del empleado asociado a ese usuario usando su controlador
            Empleado empleadoAsociado = usuarioEncontrado.getEmpleado();
            
            if (empleadoAsociado != null) {

            } else {
                JOptionPane.showMessageDialog(this, "El DNI ingresado no coincide con el empleado asignado a este usuario.", "Validación Fallida", JOptionPane.ERROR_MESSAGE);
            }
            Empleado empleado = empleado_coontroller.obtenerPorId(empleadoAsociado.getId());
            if(dniDigitado.equals(empleado.getDni())){
                JOptionPane.showMessageDialog(this, "Identidad verificada correctamente para: " + empleadoAsociado.getNombre());
                cl.show(contenedor, "Paso2");
            }else{
                JOptionPane.showMessageDialog(this, "El DNI ingresado no coincide con el empleado asignado a este usuario.", "Validación Fallida", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de consulta en la BD: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 📌 ACTUALIZACIÓN REAL EN BASE DE DATOS
     */
    private void procesarCambioClave() {
        String nuevaClave = new String(txtNuevaPassword.getPassword());
        String confirmacion = new String(txtConfirmarPassword.getPassword());

        if (nuevaClave.isEmpty() || confirmacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe rellenar ambos campos de contraseña.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nuevaClave.equals(confirmacion)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden. Inténtelo de nuevo.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 📌 Llamamos al método del controlador que creamos en el Paso 1
            // Usamos el ID del usuario que encontramos en el Paso 1 (usuarioEncontrado.getUsuario_id())
            boolean actualizado = usuarioController.actualizarPassword(usuarioEncontrado.getUsuarioId(), nuevaClave);

            if (actualizado) {
                JOptionPane.showMessageDialog(this, "¡Tu contraseña ha sido restablecida con éxito en la base de datos!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Cierra el diálogo de recuperación
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar la contraseña. Intente nuevamente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al escribir en la base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }
}