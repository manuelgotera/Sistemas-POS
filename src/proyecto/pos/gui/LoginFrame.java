package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.UsuarioController;
import proyecto.pos.dao.impl.CajaDAOImpl;
import proyecto.pos.dao.interfaces.CajaDAO;
import proyecto.pos.model.Caja;
import proyecto.pos.model.Usuario;

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
    private UsuarioController usuarioController;
    private CajaDAO cajaDAO; // 📌 Para verificar si ya existe una caja abierta antes de pedir apertura

    public LoginFrame() {
        DatabaseConnection db = new DatabaseConnection();
        conexion = db.conectar();

        this.usuarioController = new UsuarioController(conexion);
        this.cajaDAO = new CajaDAOImpl(conexion);

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

        btnOlvido.addActionListener(e -> {
            new OlvidePasswordDialog(this, conexion).setVisible(true);
        });
    }

    /**
     * Valida credenciales reales en Oracle y, según si ya existe una caja
     * abierta, decide si ir directo a Caja_GUI o pedir la apertura primero.
     */
    private void validarLogin() {
        String usuarioDigitado = txtUsuario.getText().trim();
        String passwordDigitada = new String(txtPassword.getPassword());

        if (usuarioDigitado.isEmpty() || passwordDigitada.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario usuarioBD = usuarioController.obtenerPorUsername(usuarioDigitado);

            if (usuarioBD != null && usuarioBD.getPassword().equals(passwordDigitada)) {

                if (!"ACTIVO".equalsIgnoreCase(usuarioBD.getEstado())) {
                    JOptionPane.showMessageDialog(this, "El usuario está inactivo. Contacte al administrador.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this, "¡Bienvenido al sistema, " + usuarioBD.getUsername() + "!");

                // 📌 Verificamos si ya existe una caja abierta antes de decidir a dónde navegar
                Caja cajaAbierta = cajaDAO.obtenerCajaAbierta();

                if (cajaAbierta == null) {
                    // No hay caja abierta: pedimos monto inicial antes de entrar a vender
                    new AperturaCajaFrame(usuarioBD.getEmpleado()).setVisible(true);
                } else {
                    // Ya hay una caja abierta (por ejemplo, la dejó abierta otro turno): entra directo
                    new Caja_GUI().setVisible(true);
                }

                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error de autenticación", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error de conexión con la base de datos: " + e.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}