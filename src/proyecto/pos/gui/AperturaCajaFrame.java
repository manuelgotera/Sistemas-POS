package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.CajaDAOImpl;
import proyecto.pos.dao.interfaces.CajaDAO;
import proyecto.pos.model.Empleado;
import proyecto.pos.service.CajaService;

/**
 * Frame simple de apertura de caja.
 * Se abre después de un login exitoso cuando NO hay ninguna caja abierta.
 * Reutiliza CajaService.abrirCaja(...), que ya valida:
 *  - monto inicial > 0
 *  - empleado válido
 *  - que no exista ya una caja abierta (doble seguro, aunque LoginFrame ya filtra este caso)
 */
public class AperturaCajaFrame extends JFrame {

    private static final Color AZUL       = new Color(26, 83, 160);
    private static final Color AZUL_HOVER = new Color(18, 65, 128);
    private static final Color FONDO      = new Color(240, 242, 245);
    private static final Color CARD       = Color.WHITE;
    private static final Color TEXTO_SUAVE= new Color(105, 113, 128);
    private static final Color ROJO       = new Color(220, 53, 69);

    private final Empleado empleado;
    private final Connection conexion;
    private final CajaService cajaService;

    private JTextField txtMontoInicial;
    private JButton btnAbrir;

    public AperturaCajaFrame(Empleado empleado) {
        this.empleado = empleado;

        FlatLightLaf.setup();
        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();

        CajaDAO cajaDAO = new CajaDAOImpl(conexion);
        this.cajaService = new CajaService(cajaDAO);

        configurarVentana();
        initComponents();
    }

    private void configurarVentana() {
        setTitle("Apertura de Caja");
        setSize(420, 360);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setPreferredSize(new Dimension(340, 300));
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(new Color(225, 229, 236), 14),
                new EmptyBorder(24, 26, 24, 26)));

        JLabel titulo = new JLabel("Apertura de Caja");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bienvenida = new JLabel("Bienvenido/a, " + empleado.getNombre());
        bienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        bienvenida.setForeground(TEXTO_SUAVE);
        bienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("No hay ninguna caja abierta. Ingresa el monto inicial para comenzar.");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXTO_SUAVE);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblMonto = new JLabel("Monto inicial (S/.)");
        lblMonto.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMonto.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMonto.setBorder(new EmptyBorder(18, 0, 6, 0));

        txtMontoInicial = new JTextField();
        txtMontoInicial.setMaximumSize(new Dimension(999, 38));
        txtMontoInicial.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMontoInicial.setHorizontalAlignment(SwingConstants.CENTER);
        txtMontoInicial.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtMontoInicial.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 222)),
                new EmptyBorder(6, 10, 6, 10)));

        btnAbrir = new JButton("Abrir Caja");
        btnAbrir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAbrir.setForeground(Color.WHITE);
        btnAbrir.setBackground(AZUL);
        btnAbrir.setFocusPainted(false);
        btnAbrir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbrir.setBorder(new EmptyBorder(10, 14, 10, 14));
        btnAbrir.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnAbrir.setMaximumSize(new Dimension(999, 44));
        btnAbrir.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btnAbrir.setBackground(AZUL_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btnAbrir.setBackground(AZUL); }
        });
        btnAbrir.addActionListener(this::onAbrirCaja);

        card.add(titulo);
        card.add(Box.createVerticalStrut(4));
        card.add(bienvenida);
        card.add(Box.createVerticalStrut(10));
        card.add(sub);
        card.add(lblMonto);
        card.add(txtMontoInicial);
        card.add(Box.createVerticalStrut(20));
        card.add(btnAbrir);

        root.add(card);
    }

    private void onAbrirCaja(ActionEvent evt) {
        String texto = txtMontoInicial.getText().trim().replace(",", ".");

        if (texto.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el monto inicial de la caja.",
                    "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double montoInicial;
        try {
            montoInicial = Double.parseDouble(texto);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El monto ingresado no es un número válido.",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnAbrir.setEnabled(false);
        btnAbrir.setText("Abriendo...");

        try {
            cajaService.abrirCaja(montoInicial, empleado);

            JOptionPane.showMessageDialog(this,
                    "Caja abierta correctamente con S/. " + String.format("%.2f", montoInicial),
                    "Caja abierta", JOptionPane.INFORMATION_MESSAGE);

            new Caja_GUI().setVisible(true);
            this.dispose();

        } catch (IllegalArgumentException ex) {
            // Regla de negocio violada: monto inválido, empleado inválido, o ya hay caja abierta
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "No se pudo abrir la caja", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error inesperado al abrir la caja: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            btnAbrir.setEnabled(true);
            btnAbrir.setText("Abrir Caja");
        }
    }
}