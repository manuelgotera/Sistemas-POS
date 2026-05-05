package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class Pago_GUI extends JDialog {

    private double total;
    private JButton btnCash, btnTarjeta;

    public Pago_GUI(JFrame parent, double total) {
        super(parent, "Pago", true); // true hace que sea modal
        this.total = total;
        configurarVentana();
        initComponents();
    }

    private void configurarVentana() {
        setSize(500, 400);
        setLocationRelativeTo(getOwner());
        setUndecorated(true); // Quita la barra de título nativa para estilo moderno
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- CABECERA ---
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("Pago");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        JButton btnCerrar = new JButton("X");
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());
        
        cabecera.add(lblTitulo, BorderLayout.WEST);
        cabecera.add(btnCerrar, BorderLayout.EAST);

        // --- CUERPO ---
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);

        JLabel lblInstruccion = new JLabel("Por favor, seleccione el método de pago");
        lblInstruccion.setForeground(Color.GRAY);
        lblInstruccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Banner del Total
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(new Color(242, 246, 255));
        panelTotal.setMaximumSize(new Dimension(500, 60));
        panelTotal.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel txtTotal = new JLabel("Total a pagar");
        txtTotal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel numTotal = new JLabel("s/ " + String.format("%.2f", total));
        numTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        numTotal.setForeground(new Color(26, 79, 156));
        
        panelTotal.add(txtTotal, BorderLayout.WEST);
        panelTotal.add(numTotal, BorderLayout.EAST);

        // Botones de Método
        JPanel panelMetodos = new JPanel(new GridLayout(1, 2, 15, 0));
        panelMetodos.setBackground(Color.WHITE);
        panelMetodos.setMaximumSize(new Dimension(500, 80));
        
        btnCash = crearBotonMetodo("Cash", "/img/cash_icon.png"); // Asegúrate de tener los iconos
        btnTarjeta = crearBotonMetodo("Tarjeta", "/img/card_icon.png");
        
        panelMetodos.add(btnCash);
        panelMetodos.add(btnTarjeta);

        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(lblInstruccion);
        cuerpo.add(Box.createVerticalStrut(15));
        cuerpo.add(panelTotal);
        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(new JLabel("Método de pago"));
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(panelMetodos);

        // --- PIE (BOTONES DE ACCIÓN) ---
        JPanel pie = new JPanel(new GridLayout(1, 2, 10, 0));
        pie.setBackground(Color.WHITE);
        pie.setPreferredSize(new Dimension(0, 50));

        JButton btnGuardarImprimir = new JButton("Guardar e imprimir recibo");
        btnGuardarImprimir.setBackground(new Color(45, 75, 150));
        btnGuardarImprimir.setForeground(Color.WHITE);
        btnGuardarImprimir.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JButton btnGuardarSolo = new JButton("Guardar sin imprimir");
        btnGuardarSolo.setBackground(Color.WHITE);
        btnGuardarSolo.setBorder(BorderFactory.createLineBorder(new Color(45, 75, 150)));
        btnGuardarSolo.setForeground(new Color(45, 75, 150));

        // Acción final
        btnGuardarImprimir.addActionListener(e -> finalizado());
        btnGuardarSolo.addActionListener(e -> finalizado());

        pie.add(btnGuardarImprimir);
        pie.add(btnGuardarSolo);

        panelPrincipal.add(cabecera, BorderLayout.NORTH);
        panelPrincipal.add(cuerpo, BorderLayout.CENTER);
        panelPrincipal.add(pie, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private JButton crearBotonMetodo(String texto, String iconPath) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        // Podrías añadir lógica aquí para cambiar el borde al seleccionar
        return btn;
    }

    private void finalizado() {
        JOptionPane.showMessageDialog(this, "¡Venta realizada con éxito!");
        this.dispose();
        // Aquí podrías llamar a un método en Caja_GUI para limpiar el carrito
    }
}