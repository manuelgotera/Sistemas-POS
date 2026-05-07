package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class Caja_GUI extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color VERDE = new Color(40, 167, 69);

    private String[][] platosPrincipales = {
        {"Ceviche", "12.00", "Disponible", "/img/Ceviche.png"},
        {"Lomo Saltado", "18.00", "No disponible", "/img/LomoSaltado.png"},
        {"Aji de Gallina", "15.00", "Disponible", "/img/AjiDeGallina.png"},
        {"Anticucho", "12.00", "Disponible", "/img/Anticucho.png"},
        {"Papa a la Huancaina", "10.00", "No disponible", "/img/Ceviche.png"}
    };

    private String[][] bebidas = {
        {"Pisco Sour", "14.00", "Disponible", "/img/PiscoSour.png"},
        {"Inka Kola", "8.00", "Disponible", "/img/InkaKola.png"},
        {"Chicha Morada", "8.00", "No disponible", "/img/ChichaMorada.png"},
        {"Limonada", "8.00", "Disponible", "/img/InkaKola.png"}
    };

    private JPanel panelCarritoContenedor; 
    private JPanel contenedorPrincipal;    
    private JTextField txtBuscar;
    private JTextField txtCliente, txtMesa; 
    private JButton btnPagar, btnVaciar;
    private JLabel lblTotalPagar;
    private double totalAcumulado = 0.0;

    public Caja_GUI() {
        configurarVentana();
        initComponents();
        filtrarProductos(); 
    }

    private void configurarVentana() { 
        setTitle("Sistema de Caja - POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 720); 
        setMinimumSize(new Dimension(1000, 620)); 
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR ---
        add(new MenuSidebar(this, "Cajero"), BorderLayout.WEST);

        // --- 2. ÁREA CENTRAL (BUSCADOR + GRID) ---
        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);
        cabeceraCompleta.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 40, 10, 40));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);

        JLabel lblPagina = new JLabel("Caja");
        lblPagina.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblEmpresa = new JLabel("La Buena Vida");
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmpresa.setForeground(Color.GRAY);

        panelTitulo.add(lblPagina);
        panelTitulo.add(lblEmpresa);
        topBar.add(panelTitulo, BorderLayout.WEST);

        JPanel panelPerfil = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelPerfil.setBackground(Color.WHITE);

        JLabel lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        javax.swing.Timer timerReloj = new javax.swing.Timer(1000, e -> {
            String horaActual = sdf.format(new Date());
            lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + horaActual + "</font></html>");
        });
        timerReloj.start();
        lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + sdf.format(new Date()) + "</font></html>");

        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        
        ImageIcon fotoPerfil = MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 40, 40);
        if (fotoPerfil != null) {
            lblAvatar.setIcon(fotoPerfil);
        } else {
            lblAvatar.setBackground(Color.LIGHT_GRAY);
            lblAvatar.setOpaque(true);
            lblAvatar.setText("ID"); 
            lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        }

        panelPerfil.add(lblHora);
        panelPerfil.add(lblUsuario);
        panelPerfil.add(lblAvatar);
        topBar.add(panelPerfil, BorderLayout.EAST);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 10));
        panelBuscador.setBackground(Color.WHITE);

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(350, 40)); 
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto (F2)...");
        txtBuscar.putClientProperty("JComponent.roundRect", true); 
        
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });
        
        panelBuscador.add(txtBuscar);

        cabeceraCompleta.add(topBar);
        cabeceraCompleta.add(panelBuscador);
        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Color.WHITE);
        contenedorPrincipal.setBorder(new EmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        areaCentro.add(scroll, BorderLayout.CENTER);
        add(areaCentro, BorderLayout.CENTER);

        // --- 3. RESUMEN DE PAGO (DERECHA) ---
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setPreferredSize(new Dimension(280, 0)); 
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        JPanel superiorResumen = new JPanel(new BorderLayout());
        superiorResumen.setBackground(Color.WHITE);
        
        JPanel panelDatos = new JPanel(new GridLayout(4, 1, 0, 2));
        panelDatos.setBackground(Color.WHITE);
        panelDatos.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        JLabel lblCliente = new JLabel("DNI Cliente:"); 
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCliente = new JTextField();
        txtCliente.putClientProperty("JTextField.placeholderText", "Ingrese DNI (8 dígitos)...");
        
        ((AbstractDocument) txtCliente.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= 8 && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if ((fb.getDocument().getLength() + text.length() - length) <= 8 && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JLabel lblMesa = new JLabel("Mesa:");
        lblMesa.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtMesa = new JTextField();
        txtMesa.putClientProperty("JTextField.placeholderText", "Número de mesa...");

        ((AbstractDocument) txtMesa.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string != null && string.matches("\\d+")) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("\\d+")) super.replace(fb, offset, length, text, attrs);
            }
        });
        
        panelDatos.add(lblCliente);
        panelDatos.add(txtCliente);
        panelDatos.add(lblMesa);
        panelDatos.add(txtMesa);
        
        JLabel titleRes = new JLabel("Artículos seleccionados");
        titleRes.setBorder(new EmptyBorder(10, 10, 5, 10));
        titleRes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel headerResumen = new JPanel(new BorderLayout());
        headerResumen.setBackground(Color.WHITE);
        headerResumen.add(panelDatos, BorderLayout.NORTH);
        headerResumen.add(titleRes, BorderLayout.SOUTH);

        panelCarritoContenedor = new JPanel();
        panelCarritoContenedor.setLayout(new BoxLayout(panelCarritoContenedor, BoxLayout.Y_AXIS));
        panelCarritoContenedor.setBackground(new Color(248, 249, 251));
        
        JScrollPane scrollCart = new JScrollPane(panelCarritoContenedor);
        scrollCart.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollCart.getVerticalScrollBar().setUnitIncrement(16);

        superiorResumen.add(headerResumen, BorderLayout.NORTH);
        superiorResumen.add(scrollCart, BorderLayout.CENTER);

        JPanel inferiorResumen = new JPanel(new GridLayout(0, 1, 0, 10)); 
        inferiorResumen.setBackground(Color.WHITE);
        inferiorResumen.setBorder(new EmptyBorder(10, 10, 15, 10));

        lblTotalPagar = new JLabel("Total: 0.00 so");
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTotalPagar.setForeground(new Color(26, 79, 156));

        btnPagar = new JButton("Pagar (F9)");
        btnPagar.setEnabled(false); 
        btnPagar.setPreferredSize(new Dimension(0, 45)); 
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        
        btnPagar.addActionListener(e -> {
            if (totalAcumulado > 0) {
                String dni = txtCliente.getText().trim();
                String mesa = txtMesa.getText().trim();

                if (dni.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar el DNI del cliente para proceder con el pago.", "Validación Requerida", JOptionPane.WARNING_MESSAGE);
                    txtCliente.requestFocus();
                    return;
                }

                if (dni.length() != 8) {
                    JOptionPane.showMessageDialog(this, "El DNI ingresado está incompleto. Debe tener exactamente 8 dígitos.", "Validación Requerida", JOptionPane.WARNING_MESSAGE);
                    txtCliente.requestFocus();
                    return; 
                }

                if (mesa.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe asignar un número de mesa para proceder con el pago.", "Validación Requerida", JOptionPane.WARNING_MESSAGE);
                    txtMesa.requestFocus();
                    return;
                }

                String clienteFinal = "DNI " + dni;
                String mesaFinal = "Mesa " + mesa;
                
                // Asegúrate de que esta clase exista en tu proyecto.
                Pago_GUI pago = new Pago_GUI(this, totalAcumulado, clienteFinal + " - " + mesaFinal);
                pago.setVisible(true);
            }
        });

        btnVaciar = new JButton("Vaciar carrito");
        btnVaciar.setForeground(Color.GRAY);
        btnVaciar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVaciar.addActionListener(e -> vaciarTodo());

        inferiorResumen.add(lblTotalPagar);
        inferiorResumen.add(btnPagar);
        inferiorResumen.add(btnVaciar);

        panelResumen.add(superiorResumen, BorderLayout.CENTER);
        panelResumen.add(inferiorResumen, BorderLayout.SOUTH);
        add(panelResumen, BorderLayout.EAST);
    }

    private void filtrarProductos() {
        String textoBuscado = txtBuscar.getText().toLowerCase().trim();
        contenedorPrincipal.removeAll();

        List<String[]> platosFiltrados = new ArrayList<>();
        for (String[] p : platosPrincipales) {
            if (p[0].toLowerCase().contains(textoBuscado)) platosFiltrados.add(p);
        }
        if (!platosFiltrados.isEmpty()) {
            agregarSeccion(contenedorPrincipal, "Platos Principales", platosFiltrados.toArray(new String[0][0]));
        }

        List<String[]> bebidasFiltradas = new ArrayList<>();
        for (String[] b : bebidas) {
            if (b[0].toLowerCase().contains(textoBuscado)) bebidasFiltradas.add(b);
        }
        if (!bebidasFiltradas.isEmpty()) {
            agregarSeccion(contenedorPrincipal, "Bebidas", bebidasFiltradas.toArray(new String[0][0]));
        }

        if (platosFiltrados.isEmpty() && bebidasFiltradas.isEmpty()) {
            JLabel lblVacio = new JLabel("No se encontraron productos.");
            lblVacio.setBorder(new EmptyBorder(20, 20, 20, 20));
            lblVacio.setForeground(Color.GRAY);
            contenedorPrincipal.add(lblVacio);
        }

        contenedorPrincipal.revalidate();
        contenedorPrincipal.repaint();
    }

    private void agregarSeccion(JPanel contenedor, String titulo, String[][] productos) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(15, 0, 10, 0)); 
        contenedor.add(lbl);

        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 15)); 
        grid.setBackground(Color.WHITE);
        
        for (String[] p : productos) {
            JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            cardWrapper.setBackground(Color.WHITE);
            cardWrapper.add(crearCard(p[0], p[1], p[2], p[3])); 
            grid.add(cardWrapper);
        }
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(grid, BorderLayout.NORTH);
        
        contenedor.add(gridWrapper);
    }

    private JPanel crearCard(String nombre, String precio, String estado, String imgPath) {
        JPanel card = new JPanel(new BorderLayout());
        Dimension fixedSize = new Dimension(150, 240);
        card.setPreferredSize(fixedSize);
        card.setMinimumSize(fixedSize);
        card.setMaximumSize(fixedSize);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(150, 120));
        lblImg.setOpaque(true);
        lblImg.setBackground(new Color(245, 245, 245)); 

        try {
            java.net.URL imgURL = getClass().getResource(imgPath);
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(140, 115, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(imagenEscalada));
            } else {
                lblImg.setText("No image");
                lblImg.setForeground(Color.GRAY);
            }
        } catch (Exception e) {}

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(8, 10, 8, 10));
        info.setBackground(Color.WHITE);

        JLabel n = new JLabel(nombre);
        n.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel p = new JLabel(precio + " so");
        p.setForeground(AZUL);
        p.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel s = new JLabel(estado);
        s.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JButton btnAdd = new JButton("+");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (estado.equalsIgnoreCase("Disponible")) {
            s.setForeground(VERDE);
            btnAdd.setBackground(AZUL);
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setEnabled(true);
        } else {
            s.setForeground(ROJO);
            btnAdd.setBackground(Color.LIGHT_GRAY);
            btnAdd.setForeground(Color.DARK_GRAY);
            btnAdd.setEnabled(false);
        }

        info.add(n);
        info.add(Box.createVerticalStrut(3));
        info.add(p);
        info.add(Box.createVerticalStrut(3));
        info.add(s);

        btnAdd.addActionListener(e -> agregarAlCarrito(nombre, Double.parseDouble(precio)));

        card.add(lblImg, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }

    private void agregarAlCarrito(String nombre, double precio) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblNombre = new JLabel("<html><b>" + nombre + "</b><br><font color='#1A53A0'>S/ " + String.format("%.2f", precio) + "</font></html>");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JButton btnRemove = new JButton("X");
        btnRemove.setForeground(ROJO);
        btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemove.setContentAreaFilled(false);
        btnRemove.setBorderPainted(false);
        btnRemove.setMargin(new Insets(0, 0, 0, 0));
        btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnRemove.addActionListener(e -> {
            panelCarritoContenedor.remove(itemPanel);
            totalAcumulado -= precio;
            actualizarTotal();
            panelCarritoContenedor.revalidate();
            panelCarritoContenedor.repaint();
        });
        
        itemPanel.add(lblNombre, BorderLayout.CENTER);
        itemPanel.add(btnRemove, BorderLayout.EAST);
        
        panelCarritoContenedor.add(itemPanel);
        totalAcumulado += precio;
        actualizarTotal();
        
        panelCarritoContenedor.revalidate();
        panelCarritoContenedor.repaint();
    }

    private void actualizarTotal() {
        if (totalAcumulado < 0.01) totalAcumulado = 0.0; 
        
        lblTotalPagar.setText("Total: " + String.format("%.2f", totalAcumulado) + " so");
        
        if (totalAcumulado > 0) {
            btnPagar.setEnabled(true);
            btnPagar.setBackground(AZUL);
            btnPagar.setForeground(Color.WHITE);
            btnVaciar.setForeground(ROJO);
        } else {
            btnPagar.setEnabled(false);
            btnPagar.setBackground(Color.LIGHT_GRAY);
            btnPagar.setForeground(Color.DARK_GRAY);
            btnVaciar.setForeground(Color.GRAY);
        }
    }

    public void vaciarTodo() {
        panelCarritoContenedor.removeAll();
        totalAcumulado = 0.0;
        actualizarTotal();
        txtCliente.setText("");
        txtMesa.setText("");
        panelCarritoContenedor.revalidate();
        panelCarritoContenedor.repaint();
    }
}