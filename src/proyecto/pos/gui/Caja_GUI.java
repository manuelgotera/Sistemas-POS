package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Caja_GUI extends JFrame {

    // --- VARIABLES GLOBALES (ESTADO DEL SISTEMA Y COMPONENTES) ---
    private DefaultListModel<String> modeloCarrito = new DefaultListModel<>();
    private JList<String> listaCarrito = new JList<>(modeloCarrito);

    private JButton btnCash, btnTarjeta, btnPagar, btnVaciar;
    private JLabel lblTotalPagar;
    private double totalAcumulado = 0.0;

    public Caja_GUI() {
        configurarVentana();
        initComponents();
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

        // --- 1. SIDEBAR (IZQUIERDA) ---
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(250, 250, 250));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel cabeceraSidebar = new JPanel(new BorderLayout());
        cabeceraSidebar.setBackground(new Color(250, 250, 250));
        cabeceraSidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        cabeceraSidebar.setMaximumSize(new Dimension(220, 75));

        JPanel panelLogoTextos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelLogoTextos.setBackground(new Color(250, 250, 250));

        JLabel lblIconoPOS = new JLabel(redimensionarIcono("/img/icono_carrito_blanco.png", 20, 20), SwingConstants.CENTER);
        lblIconoPOS.setPreferredSize(new Dimension(40, 40));
        lblIconoPOS.setBackground(new Color(26, 79, 156)); 
        lblIconoPOS.setOpaque(true);

        JPanel textosPOS = new JPanel();
        textosPOS.setLayout(new BoxLayout(textosPOS, BoxLayout.Y_AXIS));
        textosPOS.setBackground(new Color(250, 250, 250));
        JLabel lblPOS = new JLabel("Pos");
        lblPOS.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPOS.setForeground(new Color(26, 79, 156));
        JLabel lblDesc = new JLabel("Sistema de Caja");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblDesc.setForeground(Color.GRAY);

        textosPOS.add(lblPOS);
        textosPOS.add(lblDesc);
        panelLogoTextos.add(lblIconoPOS);
        panelLogoTextos.add(textosPOS);

        JButton btnColapsar = new JButton("≪");
        btnColapsar.setFocusPainted(false);
        btnColapsar.setBorderPainted(false);
        btnColapsar.setBackground(new Color(235, 235, 235));
        btnColapsar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnColapsar.setPreferredSize(new Dimension(35, 35));

        cabeceraSidebar.add(panelLogoTextos, BorderLayout.CENTER);
        cabeceraSidebar.add(btnColapsar, BorderLayout.EAST);

        JPanel divisorTop = new JPanel();
        divisorTop.setMaximumSize(new Dimension(220, 1));
        divisorTop.setBackground(new Color(230, 230, 230));

        sidebar.add(cabeceraSidebar);
        sidebar.add(divisorTop);
        actualizarNavegacionSidebar(sidebar); 
        add(sidebar, BorderLayout.WEST);

        // --- 2. ÁREA CENTRAL (BUSCADOR + GRID) ---
        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);

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

        JLabel lblHora = new JLabel("<html><b>Hora</b><br><font color='gray'>15:07:14 WIB</font></html>");
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));

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

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(350, 40)); 
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto (F2)...");
        txtBuscar.putClientProperty("JComponent.roundRect", true); 
        panelBuscador.add(txtBuscar);

        cabeceraCompleta.add(topBar);
        cabeceraCompleta.add(panelBuscador);
        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        // CONTENEDOR DE PRODUCTOS (GRID)
        JPanel contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Color.WHITE);
        
        // MARGEN LATERAL REDUCIDO (de 20 a 10) PARA QUE ENTREN LAS 4 TARJETAS
        contenedorPrincipal.setBorder(new EmptyBorder(0, 10, 20, 10));

        agregarSeccion(contenedorPrincipal, "Platos Principales", new String[][]{
            {"Ceviche", "12.00", "Stock: 120", "/img/Ceviche.png"},
            {"Lomo Saltado", "18.00", "Stock: 33", "/img/LomoSaltado.png"},
            {"Aji de Gallina", "15.00", "Stock: 13", "/img/AjiDeGallina.png"},
            {"Anticucho", "12.00", "Stock: 15", "/img/Anticucho.png"},
            // Agregué uno extra de prueba para que veas que baja a la siguiente línea
            {"Papa a la Huancaina", "10.00", "Stock: 40", "/img/Ceviche.png"}
        });

        agregarSeccion(contenedorPrincipal, "Bebidas", new String[][]{
            {"Pisco Sour", "14.00", "Stock: 81", "/img/PiscoSour.png"},
            {"Inka Kola", "8.00", "Stock: 32", "/img/InkaKola.png"},
            {"Chicha Morada", "8.00", "Stock: 20", "/img/ChichaMorada.png"},
            {"Limonada", "8.00", "Stock: 32", "/img/InkaKola.png"}
        });

        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        areaCentro.add(scroll, BorderLayout.CENTER);
        add(areaCentro, BorderLayout.CENTER);

        // --- 3. RESUMEN DE PAGO (DERECHA) ---
        JPanel panelResumen = new JPanel(new BorderLayout());
        
        panelResumen.setPreferredSize(new Dimension(240, 0));
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        JPanel superiorResumen = new JPanel(new BorderLayout());
        superiorResumen.setBackground(Color.WHITE);
        JLabel titleRes = new JLabel("Artículos seleccionados");
        
        titleRes.setBorder(new EmptyBorder(12, 10, 8, 10));
        titleRes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        listaCarrito.setBackground(new Color(248, 249, 251));
        JScrollPane scrollCart = new JScrollPane(listaCarrito);
        scrollCart.setBorder(new EmptyBorder(5, 10, 10, 10));

        superiorResumen.add(titleRes, BorderLayout.NORTH);
        superiorResumen.add(scrollCart, BorderLayout.CENTER);

        JPanel inferiorResumen = new JPanel(new GridLayout(0, 1, 0, 10)); 
        inferiorResumen.setBackground(Color.WHITE);
        inferiorResumen.setBorder(new EmptyBorder(10, 10, 15, 10));

        lblTotalPagar = new JLabel("Total: 0.00 so");
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTotalPagar.setForeground(new Color(26, 79, 156));

        JPanel pnlMetodos = new JPanel(new GridLayout(1, 2, 5, 0)); 
        pnlMetodos.setBackground(Color.WHITE);
        btnCash = new JButton("Cash");
        btnTarjeta = new JButton("Tarjeta");
        
        btnCash.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTarjeta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        btnCash.addActionListener(e -> seleccionarMetodo(btnCash, btnTarjeta));
        btnTarjeta.addActionListener(e -> seleccionarMetodo(btnTarjeta, btnCash));
        pnlMetodos.add(btnCash);
        pnlMetodos.add(btnTarjeta);

        btnPagar = new JButton("Pagar (F9)");
        btnPagar.setEnabled(false); 
        btnPagar.setPreferredSize(new Dimension(0, 40)); 
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 13)); 
        
        btnPagar.addActionListener(e -> {
            if (totalAcumulado > 0) {
                Pago_GUI pago = new Pago_GUI(this, totalAcumulado);
                pago.setVisible(true);
                vaciarTodo(); 
            }
        });

        btnVaciar = new JButton("Vaciar carrito");
        btnVaciar.setForeground(Color.GRAY);
        btnVaciar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVaciar.addActionListener(e -> vaciarTodo());

        inferiorResumen.add(new JLabel("Método de pago:"));
        inferiorResumen.add(pnlMetodos);
        inferiorResumen.add(lblTotalPagar);
        inferiorResumen.add(btnPagar);
        inferiorResumen.add(btnVaciar);

        panelResumen.add(superiorResumen, BorderLayout.CENTER);
        panelResumen.add(inferiorResumen, BorderLayout.SOUTH);
        add(panelResumen, BorderLayout.EAST);
    }

    // --- MÉTODOS DE APOYO Y LÓGICA ---

    private void actualizarNavegacionSidebar(JPanel sidebar) {
        sidebar.add(Box.createVerticalStrut(180));

        JButton btnCajero = crearBotonMenu("Cajero", true, "/img/icon_cart.png");
        JButton btnStock = crearBotonMenu("Artículos y Stock", false, "/img/icon_box.png");
        JButton btnHistorial = crearBotonMenu("Historial de Trans.", false, "/img/icon_history.png"); 
        JButton btnReportes = crearBotonMenu("Reportes", false, "/img/icon_chart.png");
        JButton btnGastos = crearBotonMenu("Gastos", false, "/img/icon_wallet.png");
        JButton btnConfig = crearBotonMenu("Configuración", false, "/img/icon_settings.png");

        btnStock.addActionListener(e -> {
            new ArticulosStockFrame().setVisible(true);
            this.dispose(); 
        });

        btnHistorial.addActionListener(e -> {
            new HistorialTransaccionesFrame().setVisible(true);
            this.dispose(); 
        });

        sidebar.add(btnCajero);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnStock);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnHistorial);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnReportes);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnGastos);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnConfig);

        sidebar.add(Box.createVerticalGlue());

        JPanel divisorBottom = new JPanel();
        divisorBottom.setMaximumSize(new Dimension(220, 1));
        divisorBottom.setBackground(new Color(230, 230, 230));
        sidebar.add(divisorBottom);
        sidebar.add(Box.createVerticalStrut(10));

        JButton btnModo = crearBotonMenu("Mode Tampilan", false, "/img/icon_bell.png");
        JButton btnSalir = crearBotonMenu("Salir", false, "/img/icon_logout.png");
        btnSalir.setForeground(new Color(220, 53, 69)); 

        btnSalir.addActionListener(e -> System.exit(0));

        sidebar.add(btnModo);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(btnSalir);
        sidebar.add(Box.createVerticalStrut(20));

        sidebar.revalidate();
        sidebar.repaint();
    }

    private void agregarSeccion(JPanel contenedor, String titulo, String[][] productos) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(15, 0, 10, 0)); 
        contenedor.add(lbl);

        // CAMBIO DE 3 A 4 COLUMNAS (Y se reduce un poco el espacio horizontal entre ellas)
        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 15)); 
        grid.setBackground(Color.WHITE);
        
        for (String[] p : productos) {
            JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            cardWrapper.setBackground(Color.WHITE);
            cardWrapper.add(crearCard(p[0], p[1], p[2], "", p[3]));
            
            grid.add(cardWrapper);
        }
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(grid, BorderLayout.NORTH);
        
        contenedor.add(gridWrapper);
    }

    private JPanel crearCard(String nombre, String precio, String stock, String cat, String imgPath) {
        JPanel card = new JPanel(new BorderLayout());
        
        Dimension fixedSize = new Dimension(165, 240);
        card.setPreferredSize(fixedSize);
        card.setMinimumSize(fixedSize);
        card.setMaximumSize(fixedSize);
        
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        JLabel lblImg = new JLabel("", SwingConstants.CENTER);
        lblImg.setPreferredSize(new Dimension(165, 120));
        lblImg.setOpaque(true);
        lblImg.setBackground(new Color(245, 245, 245)); 

        try {
            java.net.URL imgURL = getClass().getResource(imgPath);
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                Image imagenEscalada = iconOriginal.getImage().getScaledInstance(155, 115, Image.SCALE_SMOOTH);
                lblImg.setIcon(new ImageIcon(imagenEscalada));
            } else {
                lblImg.setText("No found");
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

        JButton btnAdd = new JButton("+");
        btnAdd.setBackground(new Color(26, 79, 156));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> {
            modeloCarrito.addElement(nombre + " - " + precio + " so");
            totalAcumulado += Double.parseDouble(precio);
            lblTotalPagar.setText("Total: " + String.format("%.2f", totalAcumulado) + " so");
            btnVaciar.setForeground(Color.RED);
        });

        card.add(lblImg, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }

    private void seleccionarMetodo(JButton sel, JButton desel) {
        sel.setBackground(new Color(26, 79, 156));
        sel.setForeground(Color.WHITE);
        desel.setBackground(Color.WHITE);
        desel.setForeground(Color.BLACK);

        if (!modeloCarrito.isEmpty()) {
            btnPagar.setEnabled(true);
            btnPagar.setBackground(new Color(26, 79, 156));
            btnPagar.setForeground(Color.WHITE);
        }
    }

    private void vaciarTodo() {
        modeloCarrito.clear();
        totalAcumulado = 0.0;
        lblTotalPagar.setText("Total: 0.00 so");
        btnVaciar.setForeground(Color.GRAY);
        btnPagar.setEnabled(false);
        btnPagar.setBackground(Color.LIGHT_GRAY);
        
        btnCash.setBackground(Color.WHITE);
        btnCash.setForeground(Color.BLACK);
        btnTarjeta.setBackground(Color.WHITE);
        btnTarjeta.setForeground(Color.BLACK);
    }

    private JButton crearBotonMenu(String texto, boolean seleccionado, String iconPath) {
        JButton btn = new JButton(texto);

        if (iconPath != null && !iconPath.isEmpty()) {
            btn.setIcon(redimensionarIcono(iconPath, 20, 20));
            btn.setIconTextGap(15);
        }

        btn.setMaximumSize(new Dimension(190, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT); 
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));

        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (seleccionado) {
            btn.setBackground(new Color(235, 240, 255));
            btn.setForeground(new Color(26, 79, 156));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 17)); 
            btn.putClientProperty("JButton.buttonType", "roundRect"); 
        } else {
            btn.setBackground(new Color(250, 250, 250));
            btn.setForeground(new Color(80, 80, 80));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 17)); 
            btn.setBorderPainted(false);
        }

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!seleccionado) btn.setBackground(new Color(242, 242, 242));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!seleccionado) btn.setBackground(new Color(250, 250, 250));
            }
        });

        return btn;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Caja_GUI().setVisible(true));
    }
}