package proyecto.pos;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Pruebaaaaa extends JFrame {

    public Pruebaaaaa() {
        configurarVentana();
        initComponents();
        
        
    }

    private void configurarVentana() {
        FlatLightLaf.setup();
        setTitle("Sistema de Caja - POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 850); // Tamanio de la ventana
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    private void initComponents() {
        // Layout principal: se divide en 3 (Sidebar, Centro, Resumen)
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR (IZQUIERDA) ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(new Color(250, 250, 250));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Botones de ejemplo para el sidebar
        sidebar.add(Box.createVerticalStrut(50));
        sidebar.add(crearBotonMenu("Cajero", true));
        sidebar.add(crearBotonMenu("Productos", false));
        sidebar.add(crearBotonMenu("Reportes", false));
        sidebar.add(Box.createVerticalGlue()); // Empuja el botón salir abajo
        sidebar.add(crearBotonMenu("Salir", false));
        sidebar.add(Box.createVerticalStrut(20));

        add(sidebar, BorderLayout.WEST);

        // --- 2. ÁREA CENTRAL (BUSCADOR + GRID) ---
        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        // ==========================================
        // NUEVA CABECERA COMPLETA (TÍTULO + BUSCADOR)
        // ==========================================
        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);

        // --- PISO 1: TOP BAR (Título y Usuario) ---
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 40, 10, 40)); // Márgenes superiores y laterales

        // Lado Izquierdo: Título de la vista
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

        // Lado Derecho: Notificaciones, Hora y Perfil
        JPanel panelPerfil = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelPerfil.setBackground(Color.WHITE);

        // Texto de Hora
        JLabel lblHora = new JLabel("<html><b>Hora</b><br><font color='gray'>15:07:14 WIB</font></html>");
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHora.setBorder(new EmptyBorder(0, 10, 0, 10));

        // Texto de Usuario
        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Foto de perfil (Círculo gris provisional)
        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        try {
            java.net.URL urlFoto = getClass().getResource("/img/perfilPedro.jpg");
            if (urlFoto != null) {
                ImageIcon iconOriginal = new ImageIcon(urlFoto);
                // Escalamos tu foto a 40x40
                Image imgEscalada = iconOriginal.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                lblAvatar.setIcon(new ImageIcon(imgEscalada));
            }
        } catch (Exception e) {
            System.out.println("Error al cargar foto de perfil");
        }
        //panelPerfil.add(btnNotif);
        panelPerfil.add(lblHora);
        panelPerfil.add(lblUsuario);
        panelPerfil.add(lblAvatar);
        
        topBar.add(panelPerfil, BorderLayout.EAST);

        // --- PISO 2: BARRA DE BÚSQUEDA ---
        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 10));
        panelBuscador.setBackground(Color.WHITE);
        
        JTextField txtBuscar = new JTextField(65);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto (F2)...");
        try {
            java.net.URL lupaURL = getClass().getResource("/img/Lupa.png");
            if (lupaURL != null) {
                txtBuscar.putClientProperty("JTextField.leadingIcon", new ImageIcon(lupaURL));
            }
        } catch (Exception e) {}
        txtBuscar.putClientProperty("JComponent.roundRect", true); 
        txtBuscar.putClientProperty("JTextField.showClearButton", true); 
        txtBuscar.setPreferredSize(new Dimension(0, 45));
        
        panelBuscador.add(txtBuscar);

        // Ensamblamos el piso 1 y 2
        cabeceraCompleta.add(topBar);
        cabeceraCompleta.add(panelBuscador);
        
        // Colocamos todo al NORTE del área central
        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        // ==========================================
        // LA SOLUCIÓN: ESTRUCTURA DE CATEGORÍAS
        // ==========================================
        
        // 1. Creamos la "Caja Mayor" que apilará todo verticalmente
        JPanel contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Color.WHITE);

        // --- SECCIÓN COMIDA ---
        JLabel lblTituloComida = new JLabel("Platos Principales");
        lblTituloComida.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloComida.setBorder(new EmptyBorder(10, 20, 0, 0)); // Margen
        
        JPanel gridComidas = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        gridComidas.setBackground(Color.WHITE);
        gridComidas.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineamos a la izquierda
        
        gridComidas.add(crearCard("Ceviche", "12.00", "Stock: 120", "Comida", "/img/Ceviche.png"));
        gridComidas.add(crearCard("Lomo Saltado", "18.00", "Stock: 33", "Comida", "/img/LomoSaltado.png"));
        gridComidas.add(crearCard("Aji de Gallina", "18.00", "Stock: 33", "Comida", "/img/AjiDeGallina.png"));
        gridComidas.add(crearCard("Anticucho", "18.00", "Stock: 33", "Comida", "/img/Anticucho.png"));

        // --- SECCIÓN BEBIDAS ---
        JLabel lblTituloBebida = new JLabel("Bebidas");
        lblTituloBebida.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloBebida.setBorder(new EmptyBorder(20, 20, 0, 0)); // Margen superior para separarlo de la comida
        
        JPanel gridBebidas = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        gridBebidas.setBackground(Color.WHITE);
        gridBebidas.setAlignmentX(Component.LEFT_ALIGNMENT); // Alineamos a la izquierda
        
        gridBebidas.add(crearCard("Pisco Sour", "14.00", "Stock: 81", "Bebida", "/img/PiscoSour.png"));
        gridBebidas.add(crearCard("Chicha Morada", "14.00", "Stock: 81", "Bebida", "/img/ChichaMorada.png"));
        gridBebidas.add(crearCard("Inka Kola", "14.00", "Stock: 81", "Bebida", "/img/InkaKola.png"));
        gridBebidas.add(crearCard("Inka Kola", "14.00", "Stock: 81", "Bebida", "/img/InkaKola.png"));
        gridBebidas.add(crearCard("Inka Kola", "14.00", "Stock: 81", "Bebida", "/img/InkaKola.png"));
        gridBebidas.add(crearCard("Inka Kola", "14.00", "Stock: 81", "Bebida", "/img/InkaKola.png"));


        // 2. Metemos los títulos y los grids a la Caja Mayor en orden estricto
        contenedorPrincipal.add(lblTituloComida);
        contenedorPrincipal.add(gridComidas);
        contenedorPrincipal.add(lblTituloBebida);
        contenedorPrincipal.add(gridBebidas);

        // 3. Finalmente, envolvemos la Caja Mayor en el Scroll
        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        areaCentro.add(scroll, BorderLayout.CENTER);

        add(areaCentro, BorderLayout.CENTER);

        // --- 3. RESUMEN DE PAGO (DERECHA) ---
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setPreferredSize(new Dimension(320, 0));
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        JLabel lblTituloResumen = new JLabel("Resumen de Pago", SwingConstants.CENTER);
        lblTituloResumen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTituloResumen.setBorder(new EmptyBorder(20, 0, 20, 0));
        panelResumen.add(lblTituloResumen, BorderLayout.NORTH);

        // Panel inferior de totales
        JPanel panelTotales = new JPanel(new GridLayout(3, 1, 0, 10));
        panelTotales.setBackground(Color.WHITE);
        panelTotales.setBorder(new EmptyBorder(20, 25, 30, 25));

        JLabel lblTotal = new JLabel("Total: 0.00 so");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTotal.setForeground(new Color(26, 79, 156));

        JButton btnPagar = new JButton("Pagar (F9)");
        btnPagar.setBackground(new Color(26, 79, 156));
        btnPagar.setForeground(Color.WHITE);
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnPagar.setPreferredSize(new Dimension(0, 55));

        panelTotales.add(lblTotal);
        panelTotales.add(btnPagar);
        panelResumen.add(panelTotales, BorderLayout.SOUTH);

        add(panelResumen, BorderLayout.EAST);
    }

    private JButton crearBotonMenu(String texto, boolean seleccionado) {
        JButton btn = new JButton(texto);
        btn.setMaximumSize(new Dimension(180, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        if (seleccionado) {
            btn.setBackground(new Color(235, 240, 255));
            btn.setForeground(new Color(26, 79, 156));
        } else {
            btn.setBackground(new Color(250, 250, 250));
            btn.setForeground(Color.GRAY);
        }
        return btn;
    }

    private JPanel crearCard(String nombre, String precio, String stock, String categoria, String imgPath) {
        JPanel card = new JPanel();
        // 1. Tarjeta de 165 de ancho x 250 de alto 
        card.setPreferredSize(new Dimension(165, 250)); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(235, 235, 235), 1, true));
        card.setLayout(new BorderLayout());

        // --- CARGA Y ESCALADO DE IMAGEN ---
        JLabel lblImg = new JLabel();
        lblImg.setBackground(new Color(245, 245, 245));
        lblImg.setOpaque(true);
        // 2. La imagen ahora respeta el ancho de la tarjeta (165)
        lblImg.setPreferredSize(new Dimension(165, 115)); 
        lblImg.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            java.net.URL imgURL = getClass().getResource(imgPath);
            
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                // 3. Escalamos a 155x115 para que deje un pequeño borde interior
                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(155, 115, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(imagenEscalada));
            } else {
                lblImg.setText("Imagen no encontrada");
                lblImg.setForeground(Color.GRAY);
            }
        } catch (Exception e) {
            lblImg.setText("Error al cargar");
        }
        // ----------------------------------        
        
        // Textos apilados con BoxLayout
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBackground(Color.WHITE);
        info.setBorder(new EmptyBorder(8, 10, 8, 10)); // Márgenes reducidos

        JLabel n = new JLabel(nombre);
        n.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Letra más pequeña para textos largos
        JLabel p = new JLabel(precio + " so");
        p.setForeground(new Color(26, 79, 156));
        p.setFont(new Font("Segoe UI", Font.BOLD, 13));
        JLabel s = new JLabel(stock);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        s.setForeground(Color.GRAY);

        info.add(n);
        info.add(Box.createVerticalStrut(3));
        info.add(p);
        info.add(Box.createVerticalStrut(3));
        info.add(s);

        // Botón añadir (+)
        JButton btnAdd = new JButton("+");
        btnAdd.setBackground(new Color(26, 79, 156));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 16));
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.add(lblImg, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Pruebaaaaa().setVisible(true));
    }
}