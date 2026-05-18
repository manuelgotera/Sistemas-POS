package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.UsuarioController; // 📌 Importamos tu controlador
import proyecto.pos.model.Usuario; // 📌 Importamos el modelo si necesitas datos del usuario logueado

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;
    private JButton btnNuevoUsuario;
    private JButton btnOlvido;

    private static final Color AZUL = new Color(26, 35, 126);
    private static final Color FONDO = new Color(240, 242, 245);
    private static final Color CARD = Color.WHITE;
    
    private Connection conexion;
    private UsuarioController usuarioController; // 📌 Instancia del controlador para validar credenciales
    
    public LoginFrame() {
        DatabaseConnection db = new DatabaseConnection();
        conexion = db.conectar();
        
        // 📌 Inicializamos el controlador pasándole la conexión activa
        this.usuarioController = new UsuarioController(conexion);
        
        setTitle("Sistema POS - Login");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(300, 500));
        leftPanel.setBackground(AZUL);
        leftPanel.setLayout(new BorderLayout());

        JLabel logo = new JLabel("POS", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 28));

        JLabel subtitle = new JLabel("Sistema de Caja", SwingConstants.CENTER);
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel leftContent = new JPanel(new GridLayout(2,1));
        leftContent.setOpaque(false);
        leftContent.add(logo);
        leftContent.add(subtitle);

        leftPanel.add(leftContent, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(FONDO);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(350, 320));
        card.setBackground(CARD);
        card.setLayout(new GridLayout(5,1,10,10));
        card.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel titulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        txtUsuario = new JTextField();
        txtUsuario.setBorder(BorderFactory.createTitledBorder("Usuario"));

        txtPassword = new JPasswordField();
        txtPassword.setBorder(BorderFactory.createTitledBorder("Contraseña"));

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBackground(AZUL);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);

        btnNuevoUsuario = new JButton("Nuevo Usuario");
        btnNuevoUsuario.setBounds(60, 220, 220, 38);
        add(btnNuevoUsuario);
        
        btnNuevoUsuario.setBackground(Color.WHITE);
        btnNuevoUsuario.setForeground(new Color(26, 83, 160));
        btnNuevoUsuario.setBorder(BorderFactory.createLineBorder(new Color(26, 83, 160)));
        btnNuevoUsuario.setFocusPainted(false);
        btnNuevoUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnOlvido = new JButton("¿Olvidé mi contraseña?");
        btnOlvido.setBounds(60, 270, 220, 38);
        add(btnOlvido);
        
        btnOlvido.setBackground(new Color(18, 65, 128));
        btnOlvido.setForeground(Color.WHITE);
        btnOlvido.setFocusPainted(false);
        btnOlvido.setBorderPainted(false);
        btnOlvido.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel footer = new JLabel("Sistema POS © 2026", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(Color.GRAY);

        card.add(titulo);
        card.add(txtUsuario);
        card.add(txtPassword);
        card.add(btnIngresar);
        card.add(footer);

        center.add(card);

        root.add(leftPanel, BorderLayout.WEST);
        root.add(center, BorderLayout.CENTER);

        add(root);

        btnIngresar.addActionListener((ActionEvent e) -> validarLogin());
        btnNuevoUsuario.addActionListener(e -> {
            new RegistrarUsuarioDialog(this, conexion).setVisible(true);
        });

        // Busca esto dentro de tu método initComponents() en LoginFrame.java
        btnOlvido.addActionListener(e -> {
            // 📌 Cambiamos el mensaje genérico por la apertura de nuestro nuevo diálogo conectado a la BD
            new OlvidePasswordDialog(this, conexion).setVisible(true);
        });
    }

    /**
     * 📌 Lógica modificada para validar credenciales reales en Oracle
     */
    private void validarLogin() {
        String usuarioDigitado = txtUsuario.getText().trim();
        String passwordDigitada = new String(txtPassword.getPassword());

        if (usuarioDigitado.isEmpty() || passwordDigitada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Buscamos el usuario en la base de datos usando el controlador
            Usuario usuarioBD = usuarioController.obtenerPorUsername(usuarioDigitado);

            // 2. Si el usuario existe, validamos el hash o texto plano de la contraseña
            // (Si usas encriptación BCrypt, cámbialo aquí por BCrypt.checkpw)
            if (usuarioBD != null && usuarioBD.getPassword().equals(passwordDigitada)) {
                
                // 3. Opcional: Validar si el estado de la cuenta es ACTIVO
                if (!"ACTIVO".equalsIgnoreCase(usuarioBD.getEstado())) {
                    JOptionPane.showMessageDialog(this, "El usuario está inactivo. Contacte al administrador.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 4. Éxito: Damos la bienvenida e ingresamos al sistema de caja
                JOptionPane.showMessageDialog(this, "¡Bienvenido al sistema, " + usuarioBD.getUsername() + "!");
                
                new Caja_GUI().setVisible(true); // Abre tu panel principal de ventas
                this.dispose(); // Cierra el login
                
            } else {
                // Si el usuario no existe en la BD o la contraseña no coincide
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            // Captura errores de sintaxis SQL u ORA-XXXX que puedan surgir durante la consulta de login
            JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}