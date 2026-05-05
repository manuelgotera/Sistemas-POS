package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnIngresar;

    private static final Color AZUL = new Color(26, 35, 126);
    private static final Color FONDO = new Color(240, 242, 245);
    private static final Color CARD = Color.WHITE;

    public LoginFrame() {
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

        JLabel footer = new JLabel("Sistema POS © 2025", SwingConstants.CENTER);
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
    }

    private void validarLogin() {

        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos");
            return;
        }

        if (usuario.equals("admin") && password.equals("1234")) {
            new HistorialTransaccionesFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}