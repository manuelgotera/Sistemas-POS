package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.PlatoDAOImpl;
import proyecto.pos.dao.interfaces.PlatoDAO;
import java.sql.*;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import proyecto.pos.dao.interfaces.ClienteDAO;
import proyecto.pos.model.Cliente;
import proyecto.pos.model.Mesa;
import proyecto.pos.model.Plato;

public class Caja_GUI extends JFrame {

    // --- COLORES DEL SIDEBAR ---
    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color SIDEBAR = new Color(250, 251, 253);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color VERDE = new Color(40, 167, 69);
    private  Connection conexion;
    private  ArrayList<Plato> platos_seleccionados = new ArrayList<Plato>();

    
    private ArrayList<Plato> obtenerPlatosBD(){
        PlatoDAO plato_dao = new PlatoDAOImpl(conexion);
        ArrayList<Plato> platos = (ArrayList<Plato>) plato_dao.listar();
        return platos;
    }
    
    
    
    // --- BASE DE DATOS SIMULADA ---
    /*private String[][] platosPrincipales = {
        {"Ceviche", "12.00", "Disponible", "/img/Ceviche.png"},
        {"Lomo Saltado", "18.00", "No disponible", "/img/LomoSaltado.png"},
        {"Aji de Gallina", "15.00", "Disponible", "/img/AjiDeGallina.png"},
        {"Anticucho", "12.00", "Disponible", "/img/Anticucho.png"},
        {"Papa a la Huancaina", "10.00", "No disponible", "/img/Ceviche.png"}
    };*/
    
    private String[][] bebidas = {
        {"Pisco Sour", "14.00", "Disponible", "/img/PiscoSour.png"},
        {"Inka Kola", "8.00", "Disponible", "/img/InkaKola.png"},
        {"Chicha Morada", "8.00", "No disponible", "/img/ChichaMorada.png"},
        {"Limonada", "8.00", "Disponible", "/img/InkaKola.png"}
    };

    // --- VARIABLES GLOBALES ---
    private JPanel panelCarritoContenedor; // Nuevo contenedor interactivo para el carrito
    private JPanel contenedorPrincipal;    // Contenedor del grid de productos
    private JTextField txtBuscar;

    private JTextField txtCliente, txtMesa; 
    private JButton btnPagar, btnVaciar;
    private JLabel lblTotalPagar;
    private double totalAcumulado = 0.0;
    
    
    
    public Caja_GUI() {
        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();
        configurarVentana();
        initComponents();
        filtrarProductos(); // Carga inicial de todos los productos
    }

    private void configurarVentana() { 
        FlatLightLaf.setup();
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
        add(crearSidebar(), BorderLayout.WEST);

        // --- 2. ÁREA CENTRAL (BUSCADOR + GRID) ---
        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);
        // NUEVO: Línea divisoria debajo de la barra de búsqueda para separar el contenido central
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
        Timer timerReloj = new Timer(1000, e -> {
            String horaActual = sdf.format(new Date());
            lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + horaActual + "</font></html>");
        });
        timerReloj.start();
        lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + sdf.format(new Date()) + "</font></html>");

        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        lblAvatar.setBackground(Color.LIGHT_GRAY);
        lblAvatar.setOpaque(true);

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
        
        // --- BUSCADOR EN TIEMPO REAL ---
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });
        
        panelBuscador.add(txtBuscar);

        cabeceraCompleta.add(topBar);
        cabeceraCompleta.add(panelBuscador);
        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        // CONTENEDOR DE PRODUCTOS
        contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Color.WHITE);
        contenedorPrincipal.setBorder(new EmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        // NUEVO: Bloquear desplazamiento de izquierda a derecha (scroll horizontal desactivado)
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        areaCentro.add(scroll, BorderLayout.CENTER);
        add(areaCentro, BorderLayout.CENTER);

        // --- 3. RESUMEN DE PAGO (DERECHA) ---
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setPreferredSize(new Dimension(280, 0)); // Un poco más ancho para el botón X
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        JPanel superiorResumen = new JPanel(new BorderLayout());
        superiorResumen.setBackground(Color.WHITE);
        
        // --- SECCIÓN CLIENTE Y MESA ---
        JPanel panelDatos = new JPanel(new GridLayout(4, 1, 0, 2));
        panelDatos.setBackground(Color.WHITE);
        panelDatos.setBorder(new EmptyBorder(10, 10, 5, 10));
        
        JLabel lblCliente = new JLabel("DNI Cliente:"); 
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        txtCliente = new JTextField();
        txtCliente.putClientProperty("JTextField.placeholderText", "Ingrese DNI (8 dígitos)...");
        
        // VALIDACIÓN: DNI SOLO NÚMEROS Y MÁXIMO 8 DÍGITOS
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

        // VALIDACIÓN: MESA SOLO NÚMEROS
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

        // --- CARRITO INTERACTIVO (NUEVO) ---
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
        
        
        lblAvatar.setPreferredSize(new Dimension(40, 40));

        // Intentar cargar una imagen real
        ImageIcon fotoPerfil = redimensionarIcono("/img/perfilPedro.jpg", 40, 40);
        if (fotoPerfil != null) {
            lblAvatar.setIcon(fotoPerfil);
        } else {
            lblAvatar.setBackground(Color.LIGHT_GRAY);
            lblAvatar.setOpaque(true);
            lblAvatar.setText("ID"); // Texto opcional si no hay foto
            lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
}
        
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
                
                Cliente cliente  = buscarClienteDNI(dni);
                if(cliente == null){
                    JOptionPane.showMessageDialog(this, "El DNI ingresado no pertenece a ningún cliente", "Validación Requerida", JOptionPane.WARNING_MESSAGE);
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
                //System.out.println(platos_seleccionados.get(1).toString());
                Pago_GUI pago = new Pago_GUI(this, totalAcumulado, cliente, platos_seleccionados, new Mesa(Integer.parseInt(mesa),0,0,0), conexion);
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

    // --- SISTEMA DE BÚSQUEDA Y RENDERIZADO ---
    private Cliente buscarClienteDNI(String dni){
        ClienteDAO cliente_dao = new ClienteDAOImpl(conexion);
        Cliente cliente = cliente_dao.obtenerPorDni(dni);
        return cliente;
    }
    
    
    private void filtrarProductos() {

        String textoBuscado = txtBuscar
                .getText()
                .toLowerCase()
                .trim();

        contenedorPrincipal.removeAll();

        ArrayList<Plato> todosLosPlatos = obtenerPlatosBD();

        ArrayList<Plato> platosPrincipales = new ArrayList<>();

        ArrayList<Plato> bebidas = new ArrayList<>();

        // =========================
        // FILTRAR
        // =========================
        for (Plato p : todosLosPlatos) {

            if (!p.getNombre()
                    .toLowerCase()
                    .contains(textoBuscado)) {

                continue;
            }

            String categoria =
                    p.getCategoria()
                     .getNombre()
                     .toLowerCase();

            if (categoria.contains("bebida")) {

                bebidas.add(p);

            } else {

                platosPrincipales.add(p);
            }
        }

        // =========================
        // MOSTRAR SECCIONES
        // =========================
        if (!platosPrincipales.isEmpty()) {

            agregarSeccion(
                    contenedorPrincipal,
                    "Platos Principales",
                    platosPrincipales
            );
        }

        if (!bebidas.isEmpty()) {

            agregarSeccion(
                    contenedorPrincipal,
                    "Bebidas",
                    bebidas
            );
        }

        // =========================
        // MENSAJE VACÍO
        // =========================
        if (platosPrincipales.isEmpty()
                && bebidas.isEmpty()) {

            JLabel lblVacio =
                    new JLabel("No se encontraron productos.");

            lblVacio.setBorder(
                    new EmptyBorder(20, 20, 20, 20)
            );

            lblVacio.setForeground(Color.GRAY);

            contenedorPrincipal.add(lblVacio);
        }

        contenedorPrincipal.revalidate();

        contenedorPrincipal.repaint();
    }

    private void agregarSeccion(JPanel contenedor, String titulo, ArrayList<Plato> platos) {
        
        String disponibilidad;
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(15, 0, 10, 0)); 
        contenedor.add(lbl);

        // MANTENEMOS LA GRILLA DE 4 COLUMNAS COMO SOLICITASTE
        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 15)); 
        grid.setBackground(Color.WHITE);
        
        System.out.println("SECCION: " + titulo);

        for (Plato p : platos) {
            System.out.println(p.getNombre());
        }
        for (Plato p : platos) {
            JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            cardWrapper.setBackground(Color.WHITE);
            // Formato array: Nombre, Precio, Estado, Imagen
            if(p.getDisponible() == 1){
                disponibilidad = "DISPONIBLE";
            }
            else{
                disponibilidad = "AGOTADO";
            }
            cardWrapper.add(crearCard(p)); 
            grid.add(cardWrapper);
        }
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(grid, BorderLayout.NORTH);
        
        contenedor.add(gridWrapper);
    }

    private JPanel crearCard(Plato plato) {
        String disponibilidad;
        //plato.setImagen("/img/AjiDeGallina.png");
        
        JPanel card = new JPanel(new BorderLayout());
        // NUEVO: Dimensiones ajustadas para evitar desbordamiento horizontal y mantener las 4 columnas (de 165 a 150)
        Dimension fixedSize = new Dimension(150, 240);
        card.setPreferredSize(fixedSize);
        card.setMinimumSize(fixedSize);
        card.setMaximumSize(fixedSize);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        System.out.println(getClass().getResource("/img/PiscoSour.png"));
        JLabel lblImg = new JLabel(plato.getImagen(), SwingConstants.CENTER);
        // NUEVO: Se ajusta el área de la imagen en base a los nuevos tamaños de la tarjeta
        lblImg.setPreferredSize(new Dimension(150, 120));
        lblImg.setOpaque(true);
        lblImg.setBackground(new Color(245, 245, 245)); 

        try {
            java.net.URL imgURL = getClass().getResource(plato.getImagen());
            java.net.URL url = getClass().getResource(plato.getImagen());
            System.out.println(url);
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                // NUEVO: Se ajusta la escala de la imagen para que encaje
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

        JLabel n = new JLabel(plato.getNombre());
        n.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        JLabel p = new JLabel(plato.getPrecio() + " so");
        p.setForeground(AZUL);
        p.setFont(new Font("Segoe UI", Font.BOLD, 13));

        if(plato.getDisponible() == 1){disponibilidad = "DISPONIBLE";}else{disponibilidad = "AGOTADO";}
        
        JLabel s = new JLabel(disponibilidad);
        s.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JButton btnAdd = new JButton("+");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // --- LÓGICA DE ESTADO (DISPONIBILIDAD) ---
        if (disponibilidad.equalsIgnoreCase("DISPONIBLE")) {
            s.setForeground(VERDE);
            btnAdd.setBackground(AZUL);
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setEnabled(true);
        } else {
            s.setForeground(ROJO);
            btnAdd.setBackground(Color.LIGHT_GRAY);
            btnAdd.setForeground(Color.DARK_GRAY);
            btnAdd.setEnabled(false); // Bloqueamos el botón
        }

        info.add(n);
        info.add(Box.createVerticalStrut(3));
        info.add(p);
        info.add(Box.createVerticalStrut(3));
        info.add(s);

        btnAdd.addActionListener(e -> {
            agregarAlCarrito(plato);
            System.out.println(platos_seleccionados.isEmpty());
        });
        card.add(lblImg, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }
    // --- LÓGICA DEL CARRITO INTERACTIVO ---

    private void agregarAlCarrito(Plato plato) {
        
        platos_seleccionados.add(plato);
        // Creamos una pequeña tarjeta (panel) para cada item en el carrito
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblNombre = new JLabel("<html><b>" + plato.getNombre() + "</b><br><font color='#1A53A0'>S/ " + String.format("%.2f", plato.getPrecio()) + "</font></html>");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Botón Eliminar Individual (X)
        JButton btnRemove = new JButton("X");
        btnRemove.setForeground(ROJO);
        btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRemove.setContentAreaFilled(false);
        btnRemove.setBorderPainted(false);
        btnRemove.setMargin(new Insets(0, 0, 0, 0));
        btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnRemove.addActionListener(e -> {
            panelCarritoContenedor.remove(itemPanel);
            totalAcumulado -= plato.getPrecio();
            platos_seleccionados.remove(plato);
            actualizarTotal();
            panelCarritoContenedor.revalidate();
            panelCarritoContenedor.repaint();
        });
        
        itemPanel.add(lblNombre, BorderLayout.CENTER);
        itemPanel.add(btnRemove, BorderLayout.EAST);
        
        panelCarritoContenedor.add(itemPanel);
        totalAcumulado += plato.getPrecio();
        actualizarTotal();
        
        panelCarritoContenedor.revalidate();
        panelCarritoContenedor.repaint();
    }
    
    private void agregarAlCarrito1(String nombre, double precio) {
        // Creamos una pequeña tarjeta (panel) para cada item en el carrito
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(Color.WHITE);
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        itemPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        JLabel lblNombre = new JLabel("<html><b>" + nombre + "</b><br><font color='#1A53A0'>S/ " + String.format("%.2f", precio) + "</font></html>");
        lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Botón Eliminar Individual (X)
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
        if (totalAcumulado < 0.01) totalAcumulado = 0.0; // Evitar el "-0.00" por fallos de decimales
        
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

    // --- MÉTODOS DEL SIDEBAR ---

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDE));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(crearHeaderSidebar());
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(34));

        JButton btnCajero = crearBotonMenu("Cajero", "/img/carrito.png", true);
        JButton btnStock = crearBotonMenu("Artículos y Stock", "/img/stock.png", false);
        JButton btnHistorial = crearBotonMenu("Historial de Trans.", "/img/Historial.png", false);
        JButton btnReportes = crearBotonMenu("Reportes", "/img/Reporte.png", false);
        JButton btnGastos = crearBotonMenu("Gastos", "/img/billetera.png", false);
        JButton btnConfig = crearBotonMenu("Configuración", "/img/configuracion.png", false);

        btnStock.addActionListener(e -> {
            new ArticulosStockFrame().setVisible(true);
            dispose();
        });
        
        btnReportes.addActionListener(e -> {
            new ReportesFrame().setVisible(true);
            this.dispose();
        });
        
        btnConfig.addActionListener(e -> {
            new ConfiguracionFrame().setVisible(true);
            this.dispose();
        });
                
        btnHistorial.addActionListener(e -> {
            new HistorialTransaccionesFrame().setVisible(true);
            dispose();
        });

        agregarMenu(sidebar, btnCajero, btnStock, btnHistorial, btnReportes, btnGastos, btnConfig);
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(12));

        JButton btnSalir = crearBotonMenu("Salir", "/img/Salir.png", false);
        btnSalir.setForeground(ROJO);
        btnSalir.addActionListener(e -> System.exit(0));

        agregarMenu(sidebar, btnSalir);
        sidebar.add(Box.createVerticalStrut(18));

        return sidebar;
    }

    private void agregarMenu(JPanel panel, JButton... botones) {
        for (JButton boton : botones) {
            panel.add(boton);
            panel.add(Box.createVerticalStrut(7));
        }
    }

    private JPanel crearHeaderSidebar() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setBackground(SIDEBAR);
        header.setBorder(new EmptyBorder(16, 16, 16, 14));
        header.setMaximumSize(new Dimension(220, 78));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        marca.setOpaque(false);

        JLabel logo = new JLabel(redimensionarIcono("/img/carroBlanco.png", 22, 22));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(AZUL);
        logo.setPreferredSize(new Dimension(40, 40));
        logo.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblPos = new JLabel("Pos");
        lblPos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPos.setForeground(AZUL);

        JLabel lblDesc = new JLabel("Sistema de Caja");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(TEXTO_SUAVE);

        textos.add(lblPos);
        textos.add(lblDesc);

        marca.add(logo);
        marca.add(textos);

        header.add(marca, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearLinea() {
        JPanel linea = new JPanel();
        linea.setMaximumSize(new Dimension(220, 1));
        linea.setPreferredSize(new Dimension(220, 1));
        linea.setBackground(new Color(232, 235, 241));
        return linea;
    }

    private JButton crearBotonMenu(String texto, String iconPath, boolean seleccionado) {
        JButton boton = new JButton(texto);
        if (iconPath != null && !iconPath.isEmpty()) {
            boton.setIcon(redimensionarIcono(iconPath, 18, 18));
            boton.setIconTextGap(13);
        }
        boton.setMaximumSize(new Dimension(190, 40));
        boton.setPreferredSize(new Dimension(190, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(new EmptyBorder(0, 14, 0, 10));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFont(new Font("Segoe UI", seleccionado ? Font.BOLD : Font.PLAIN, 14));

        if (seleccionado) {
            boton.setBackground(AZUL_CLARO);
            boton.setForeground(AZUL);
            boton.setBorder(new RoundedBorder(AZUL_CLARO, 12));
        } else {
            boton.setBackground(SIDEBAR);
            boton.setForeground(new Color(62, 70, 82));
            boton.setBorderPainted(false);
        }

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!seleccionado) {
                    boton.setBackground(new Color(240, 243, 248));
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!seleccionado) {
                    boton.setBackground(SIDEBAR);
                }
            }
        });

        return boton;
    }

    private ImageIcon redimensionarIcono(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                Image img = iconOriginal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {}
        return null; 
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int arc;

        public RoundedBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 1;
            insets.right = 1;
            insets.top = 1;
            insets.bottom = 1;
            return insets;
        }
    }

    public static void main(String[] args) {
        System.out.println("xdd");
        
        SwingUtilities.invokeLater(() -> new Caja_GUI().setVisible(true));
    }
}