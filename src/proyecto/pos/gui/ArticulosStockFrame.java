package proyecto.pos.gui;

// Librerías para construir la interfaz gráfica con Swing
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
// Utilidades para filtros de búsqueda
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * Ventana principal del módulo "Artículos y Stock".
 *
 * Esta clase pertenece al FRONTEND del sistema.
 * Su responsabilidad es mostrar la lista de productos, permitir búsquedas,
 * filtrar por categoría, agregar, editar y eliminar productos desde la interfaz.
 *
 * Actualmente trabaja con datos de prueba.
 * Más adelante se puede reemplazar cargarDatosDemo() por llamadas al DAO.
 */
public class ArticulosStockFrame extends JFrame {

    // Colores principales de la interfaz, basados en el diseño de Figma
    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO = new Color(246, 248, 251);
    private static final Color BORDE = new Color(225, 228, 233);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color AMARILLO_FONDO = new Color(255, 249, 219);
    private static final Color AMARILLO_BORDE = new Color(255, 220, 100);

    // Tabla principal donde se muestran los productos
    private JTable tablaProductos;

    // Modelo de datos de la tabla.
    // Aquí se agregan, editan y eliminan filas visualmente.
    private DefaultTableModel modeloTabla;

    // Permite aplicar filtros a la tabla sin modificar los datos originales.
    private TableRowSorter<DefaultTableModel> sorter;

    // Componentes de búsqueda y filtro
    private JTextField txtBuscar;
    private JComboBox<String> cboCategoria;

    // Etiquetas dinámicas
    private JLabel lblAlerta;
    private JLabel lblFooter;

    /**
     * Constructor principal de la ventana.
     * Configura la ventana, construye la interfaz y carga datos iniciales.
     */
    public ArticulosStockFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        actualizarAlertaStock();
    }

    /**
     * Configuración general de la ventana.
     */
    private void configurarVentana() {
        setTitle("Artículos y Stock");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /**
     * Construye la estructura base de la ventana:
     * sidebar izquierda + contenido principal.
     */
    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    /**
     * Crea el menú lateral izquierdo.
     * Este menú simula la navegación general del POS.
     */
    // ─── NUEVO SIDEBAR UNIFICADO ────────────────────────────────────────────────────────

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(new Color(250, 250, 250));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        // Cabecera del Sidebar (Logo y texto)
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

        // Agregamos los botones de navegación
        actualizarNavegacionSidebar(sidebar); 

        return sidebar;
    }

    private void actualizarNavegacionSidebar(JPanel sidebar) {
        sidebar.add(Box.createVerticalStrut(180)); // Espaciado superior

        // ¡ATENCIÓN AQUÍ! "Artículos y Stock" está en TRUE, los demás en FALSE
        JButton btnCajero = crearBotonMenu("Cajero", false, "/img/icon_cart.png");
        JButton btnStock = crearBotonMenu("Artículos y Stock", true, "/img/icon_box.png");
        JButton btnHistorial = crearBotonMenu("Historial de Trans.", false, "/img/icon_history.png"); 
        JButton btnReportes = crearBotonMenu("Reportes", false, "/img/icon_chart.png");
        JButton btnGastos = crearBotonMenu("Gastos", false, "/img/icon_wallet.png");
        JButton btnConfig = crearBotonMenu("Configuración", false, "/img/icon_settings.png");

        // ACCIONES DE NAVEGACIÓN
        btnCajero.addActionListener(e -> {
            new Caja_GUI().setVisible(true);
            this.dispose(); 
        });

        btnHistorial.addActionListener(e -> {
            new HistorialTransaccionesFrame().setVisible(true);
            this.dispose(); 
        });

        // Nota: btnStock no lleva ActionListener porque ya estamos en esta ventana.

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

        sidebar.add(Box.createVerticalGlue()); // Empuja lo demás hacia abajo

        // SECCIÓN INFERIOR
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
            btn.setFont(new Font("Segoe UI", Font.BOLD, 17)); // Tu tamaño de fuente
            btn.putClientProperty("JButton.buttonType", "roundRect"); 
        } else {
            btn.setBackground(new Color(250, 250, 250));
            btn.setForeground(new Color(80, 80, 80));
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 17)); // Tu tamaño de fuente
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
        } catch (Exception e) {
            System.err.println("No se encontró el icono: " + path);
        }
        return null; 
    }

    /**
     * Crea el contenido principal de la pantalla.
     */
    private JPanel crearContenido() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(24, 26, 24, 26));

        contenedor.add(crearHeader(), BorderLayout.NORTH);
        contenedor.add(crearPanelPrincipal(), BorderLayout.CENTER);

        return contenedor;
    }

    /**
     * Crea el encabezado superior:
     * título, subtítulo, hora y usuario.
     */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel(new BorderLayout());
        titulos.setBackground(FONDO);

        JLabel titulo = new JLabel("Lista de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitulo = new JLabel("Gestionar datos de productos e inventario");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);

        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.CENTER);

        JPanel usuario = new JPanel(new GridBagLayout());
        usuario.setBackground(FONDO);

        JLabel hora = new JLabel("<html><b>Hora</b><br>15:07:14 WIB</html>");
        hora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hora.setOpaque(true);
        hora.setBackground(Color.WHITE);
        hora.setBorder(new EmptyBorder(8, 14, 8, 14));

        JLabel perfil = new JLabel("<html><b>uwu fernandez</b><br>Cajero</html>");
        perfil.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        perfil.setOpaque(true);
        perfil.setBackground(Color.WHITE);
        perfil.setBorder(new EmptyBorder(8, 14, 8, 14));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        gbc.gridx = 0;
        usuario.add(hora, gbc);

        gbc.gridx = 1;
        usuario.add(perfil, gbc);

        header.add(titulos, BorderLayout.WEST);
        header.add(usuario, BorderLayout.EAST);

        return header;
    }

    /**
     * Crea el panel principal blanco que contiene:
     * botones, alerta, filtros, tabla y footer.
     */
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(18, 18, 18, 18)
        ));

        panel.add(crearBarraAcciones(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearFooter(), BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Crea la zona superior del panel:
     * botones de acción, alerta de stock, filtro y búsqueda.
     */
    private JPanel crearBarraAcciones() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        // Botones superiores: Exportar y Añadir
        JPanel botones = new JPanel(new GridBagLayout());
        botones.setBackground(Color.WHITE);

        JButton btnExportar = crearBotonSecundario("Exportar PDF");
        JButton btnAgregar = crearBotonPrimario("+ Añadir Producto");

        // Abre el formulario modal para agregar producto
        btnAgregar.addActionListener(e -> abrirDialogoAgregar());

        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.insets = new Insets(0, 5, 0, 5);

        gbcBtn.gridx = 0;
        botones.add(btnExportar, gbcBtn);

        gbcBtn.gridx = 1;
        botones.add(btnAgregar, gbcBtn);

        wrapper.add(botones, BorderLayout.EAST);

        // Alerta de bajo stock
        lblAlerta = new JLabel("  1 producto tiene existencias por debajo del mínimo");
        lblAlerta.setOpaque(true);
        lblAlerta.setBackground(AMARILLO_FONDO);
        lblAlerta.setForeground(new Color(130, 90, 0));
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblAlerta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AMARILLO_BORDE),
                new EmptyBorder(10, 12, 10, 12)
        ));

        // Filtros: categoría y búsqueda textual
        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(new EmptyBorder(14, 0, 14, 0));

        cboCategoria = new JComboBox<>(new String[]{"Todas las categorías", "Comida", "Bebida"});
        cboCategoria.setPreferredSize(new Dimension(190, 36));

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(270, 36));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre o código");

        // Cada vez que el usuario escribe, se aplica filtro en tiempo real
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                aplicarFiltros();
            }
        });

        // Cada vez que cambia la categoría, se aplica filtro
        cboCategoria.addActionListener(e -> aplicarFiltros());

        filtros.add(cboCategoria, BorderLayout.WEST);
        filtros.add(txtBuscar, BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(Color.WHITE);
        centro.setBorder(new EmptyBorder(16, 0, 0, 0));
        centro.add(lblAlerta, BorderLayout.NORTH);
        centro.add(filtros, BorderLayout.CENTER);

        wrapper.add(centro, BorderLayout.SOUTH);

        return wrapper;
    }

    /**
     * Crea y configura la tabla de productos.
     */
    private JScrollPane crearTabla() {
        String[] columnas = {
            "Código", "Nombre", "Categoría", "Costo", "Precio",
            "Stock", "Min. stock", "Status", "Acciones"
        };

        // Modelo no editable directamente.
        // Las modificaciones se hacen con los botones editar/eliminar.
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(42);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaProductos.getTableHeader().setReorderingAllowed(false);
        tablaProductos.setSelectionBackground(AZUL_CLARO);
        tablaProductos.setSelectionForeground(Color.BLACK);
        tablaProductos.setGridColor(new Color(235, 235, 235));

        // Ordenador/filtro de la tabla
        sorter = new TableRowSorter<>(modeloTabla);
        tablaProductos.setRowSorter(sorter);

        // Tamaño aproximado de columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(180);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(110);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(4).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaProductos.getColumnModel().getColumn(6).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(7).setPreferredWidth(90);
        tablaProductos.getColumnModel().getColumn(8).setPreferredWidth(120);

        // Renderizadores personalizados para status, acciones y stock bajo
        tablaProductos.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        tablaProductos.getColumnModel().getColumn(8).setCellRenderer(new AccionesRenderer());
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new StockRenderer());

        /*
         * Manejo de clicks en la columna "Acciones".
         * Si el click cae en la mitad izquierda: Editar.
         * Si cae en la mitad derecha: Eliminar.
         */
        tablaProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaProductos.rowAtPoint(e.getPoint());
                int columna = tablaProductos.columnAtPoint(e.getPoint());

                if (fila >= 0 && columna == 8) {
                    int filaModelo = tablaProductos.convertRowIndexToModel(fila);
                    int xRelativo = e.getX() - tablaProductos.getCellRect(fila, columna, true).x;
                    int anchoColumna = tablaProductos.getColumnModel().getColumn(columna).getWidth();

                    if (xRelativo < anchoColumna / 2) {
                        abrirDialogoEditar(filaModelo);
                    } else {
                        eliminarProducto(filaModelo);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    /**
     * Footer inferior de la tabla.
     * Muestra cuántos registros están visibles.
     */
    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));

        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(90, 90, 90));

        footer.add(lblFooter, BorderLayout.WEST);

        return footer;
    }

    /**
     * Botón principal azul.
     */
    private JButton crearBotonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(AZUL);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(165, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Botón secundario blanco con texto azul.
     */
    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(Color.WHITE);
        boton.setForeground(AZUL);
        boton.setFocusPainted(false);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setPreferredSize(new Dimension(150, 38));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Carga datos temporales de prueba.
     *
     * IMPORTANTE:
     * Esta función luego puede reemplazarse por una llamada al backend:
     * productoDAO.listar()
     */
    private void cargarDatosDemo() {
        agregarFila("BRG-001", "Inka kola", "Bebida", 3.00, 5.00, 120, 20, true);
        agregarFila("BRG-002", "Chicha morada", "Bebida", 4.00, 8.00, 81, 20, true);
        agregarFila("BRG-003", "Pisco sour", "Bebida", 4.00, 14.00, 32, 20, true);
        agregarFila("BRG-0B3", "Ceviche", "Comida", 4.00, 12.00, 32, 20, true);
        agregarFila("BRG-0A1", "Lomo saltado", "Comida", 4.00, 18.00, 3, 20, true);
        agregarFila("BRG-0S1", "Anticucho Corazon", "Comida", 4.00, 15.00, 120, 20, true);
        agregarFila("BRG-0S2", "Aji de Gallina", "Comida", 4.00, 15.00, 33, 20, true);

        actualizarFooter();
    }

    /**
     * Agrega una fila al modelo de tabla.
     *
     * Este método sirve tanto para datos demo como para insertar visualmente
     * productos creados desde el formulario.
     */
    private void agregarFila(String codigo, String nombre, String categoria, double costo, double precio,
                             int stock, int stockMin, boolean activo) {
        modeloTabla.addRow(new Object[]{
            codigo,
            nombre,
            categoria,
            formatoMoneda(costo),
            formatoMoneda(precio),
            stock,
            stockMin,
            activo ? "activo" : "inactivo",
            "Editar   Eliminar"
        });
    }

    /**
     * Abre el formulario para agregar un producto.
     */
    private void abrirDialogoAgregar() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog(owner);
        dialog.setVisible(true);

        // Si el usuario presionó Aceptar y pasó las validaciones
        if (dialog.isConfirmado()) {
            ProductoDialog.ProductoFormData data = dialog.getProductoData();

            // Validación visual para evitar códigos duplicados en la tabla
            if (codigoExiste(data.codigo)) {
                JOptionPane.showMessageDialog(
                        this,
                        "El código del producto ya existe. Ingrese otro código.",
                        "Código duplicado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            /*
             * Aquí actualmente se agrega solo a la tabla.
             * Luego se puede reemplazar o complementar con:
             * productoDAO.insertar(data)
             */
            agregarFila(data.codigo, data.nombre, data.categoria, data.costo, data.precio,
                    data.stock, data.stockMinimo, data.activo);

            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Datos agregados con éxito");
        }
    }

    /**
     * Abre el formulario para editar el producto seleccionado.
     */
    private void abrirDialogoEditar(int filaModelo) {
        ProductoDialog.ProductoFormData actual = obtenerDataDesdeFila(filaModelo);

        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog(owner, actual);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            ProductoDialog.ProductoFormData data = dialog.getProductoData();

            // Evita duplicar código si el usuario lo cambia al editar
            if (!data.codigo.equalsIgnoreCase(actual.codigo) && codigoExiste(data.codigo)) {
                JOptionPane.showMessageDialog(
                        this,
                        "El código del producto ya existe. Ingrese otro código.",
                        "Código duplicado",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            /*
             * Se actualiza visualmente la fila.
             * Más adelante aquí se puede llamar:
             * productoDAO.actualizar(data)
             */
            modeloTabla.setValueAt(data.codigo, filaModelo, 0);
            modeloTabla.setValueAt(data.nombre, filaModelo, 1);
            modeloTabla.setValueAt(data.categoria, filaModelo, 2);
            modeloTabla.setValueAt(formatoMoneda(data.costo), filaModelo, 3);
            modeloTabla.setValueAt(formatoMoneda(data.precio), filaModelo, 4);
            modeloTabla.setValueAt(data.stock, filaModelo, 5);
            modeloTabla.setValueAt(data.stockMinimo, filaModelo, 6);
            modeloTabla.setValueAt(data.activo ? "activo" : "inactivo", filaModelo, 7);

            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Datos actualizados con éxito");
        }
    }

    /**
     * Elimina un producto de la tabla luego de confirmar.
     */
    private void eliminarProducto(int filaModelo) {
        String nombre = String.valueOf(modeloTabla.getValueAt(filaModelo, 1));

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el producto \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            /*
             * Actualmente elimina solo de la tabla.
             * Luego puede reemplazarse por:
             * productoDAO.eliminar(codigo)
             */
            modeloTabla.removeRow(filaModelo);

            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Producto eliminado correctamente");
        }
    }

    /**
     * Convierte una fila de la tabla en un objeto ProductoFormData.
     * Esto permite reutilizar el mismo dialog para editar.
     */
    private ProductoDialog.ProductoFormData obtenerDataDesdeFila(int fila) {
        String codigo = String.valueOf(modeloTabla.getValueAt(fila, 0));
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, 1));
        String categoria = String.valueOf(modeloTabla.getValueAt(fila, 2));
        double costo = limpiarMoneda(String.valueOf(modeloTabla.getValueAt(fila, 3)));
        double precio = limpiarMoneda(String.valueOf(modeloTabla.getValueAt(fila, 4)));
        int stock = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(fila, 5)));
        int stockMin = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(fila, 6)));
        boolean activo = String.valueOf(modeloTabla.getValueAt(fila, 7)).equalsIgnoreCase("activo");

        return new ProductoDialog.ProductoFormData(codigo, nombre, categoria, costo, precio, stock, stockMin, activo);
    }

    /**
     * Verifica si un código ya existe dentro de la tabla.
     */
    private boolean codigoExiste(String codigo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String codigoTabla = String.valueOf(modeloTabla.getValueAt(i, 0));
            if (codigoTabla.equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Aplica los filtros de búsqueda por texto y categoría.
     */
    private void aplicarFiltros() {
        String texto = txtBuscar.getText().trim();
        String categoria = String.valueOf(cboCategoria.getSelectedItem());

        RowFilter<DefaultTableModel, Object> filtroTexto = null;
        RowFilter<DefaultTableModel, Object> filtroCategoria = null;

        // Filtro por código o nombre
        if (!texto.isEmpty()) {
            String regex = "(?i)" + Pattern.quote(texto);
            filtroTexto = RowFilter.regexFilter(regex, 0, 1);
        }

        // Filtro por categoría
        if (!categoria.equals("Todas las categorías")) {
            filtroCategoria = RowFilter.regexFilter("^" + Pattern.quote(categoria) + "$", 2);
        }

        // Combina los filtros según corresponda
        if (filtroTexto != null && filtroCategoria != null) {
            sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(filtroTexto, filtroCategoria)));
        } else if (filtroTexto != null) {
            sorter.setRowFilter(filtroTexto);
        } else if (filtroCategoria != null) {
            sorter.setRowFilter(filtroCategoria);
        } else {
            sorter.setRowFilter(null);
        }

        actualizarFooter();
    }

    /**
     * Revisa cuántos productos tienen stock menor al mínimo.
     * Si hay productos bajos, muestra la alerta amarilla.
     */
    private void actualizarAlertaStock() {
        int bajoMinimo = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int stock = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, 5)));
            int min = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, 6)));

            if (stock < min) {
                bajoMinimo++;
            }
        }

        if (bajoMinimo > 0) {
            lblAlerta.setVisible(true);
            lblAlerta.setText("  " + bajoMinimo + " producto(s) tiene(n) existencias por debajo del mínimo");
        } else {
            lblAlerta.setVisible(false);
        }
    }

    /**
     * Actualiza el texto inferior de la tabla.
     */
    private void actualizarFooter() {
        int visibles = tablaProductos == null ? 0 : tablaProductos.getRowCount();
        int total = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " - " + total + " de " + total + " datos");
    }

    /**
     * Muestra una notificación flotante temporal tipo "toast".
     */
    private void mostrarToast(String mensaje) {
        final JLabel toast = new JLabel("  " + mensaje + "  ");
        toast.setOpaque(true);
        toast.setBackground(new Color(235, 244, 255));
        toast.setForeground(AZUL);
        toast.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toast.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(170, 205, 255)),
                new EmptyBorder(10, 14, 10, 14)
        ));

        JDialogToast dialog = new JDialogToast(this, toast);
        dialog.setVisible(true);

        Timer timer = new Timer(1800, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Formatea valores monetarios para mostrarlos en la tabla.
     */
    private String formatoMoneda(double valor) {
        return String.format("S/ %.2f", valor);
    }

    /**
     * Quita el símbolo de moneda para volver a convertir a double.
     */
    private double limpiarMoneda(String texto) {
        return Double.parseDouble(texto.replace("S/", "").trim());
    }

    /**
     * Renderizador para la columna "Status".
     * Da estilo visual al texto activo/inactivo.
     */
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if (!isSelected) {
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(83, 137, 174));
            }

            return label;
        }
    }

    /**
     * Renderizador para la columna "Acciones".
     * Muestra visualmente las opciones Editar y Eliminar.
     */
    private static class AccionesRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setText("Editar   Eliminar");
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if (!isSelected) {
                label.setForeground(AZUL);
                label.setBackground(Color.WHITE);
            }

            return label;
        }
    }

    /**
     * Renderizador para la columna "Stock".
     * Si el stock está debajo del mínimo, se muestra en rojo.
     */
    private static class StockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);

            int stock = Integer.parseInt(String.valueOf(value));
            int stockMin = Integer.parseInt(String.valueOf(table.getValueAt(row, 6)));

            if (!isSelected) {
                if (stock < stockMin) {
                    label.setForeground(ROJO);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                } else {
                    label.setForeground(Color.BLACK);
                    label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                }
                label.setBackground(Color.WHITE);
            }

            return label;
        }
    }

    /**
     * Dialog pequeño usado como notificación flotante.
     */
    private static class JDialogToast extends javax.swing.JDialog {
        public JDialogToast(JFrame owner, JLabel label) {
            super(owner, false);
            setUndecorated(true);
            add(label);
            pack();

            int x = owner.getX() + owner.getWidth() - getWidth() - 45;
            int y = owner.getY() + 65;
            setLocation(x, y);
        }
    }
    
}