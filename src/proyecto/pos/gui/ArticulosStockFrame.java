package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ArticulosStockFrame extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_HOVER = new Color(18, 65, 128);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO = new Color(246, 248, 251);
    private static final Color SIDEBAR = new Color(250, 251, 253);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color TEXTO = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color AMARILLO_FONDO = new Color(255, 249, 219);
    private static final Color AMARILLO_BORDE = new Color(245, 213, 93);
    private static final Color AMARILLO_TEXTO = new Color(128, 89, 0);

    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboCategoria;
    private JLabel lblAlerta;
    private JLabel lblFooter;

    public ArticulosStockFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        actualizarAlertaStock();
        actualizarFooter();
    }

    private void configurarVentana() {
        setTitle("Artículos y Stock");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDE));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(crearHeaderSidebar());
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(34));

        JButton btnCajero = crearBotonMenu("Cajero", "/img/carrito.png", false);
        JButton btnStock = crearBotonMenu("Artículos y Stock", "/img/stock.png", true);
        JButton btnHistorial = crearBotonMenu("Historial de Trans.", "/img/Historial.png", false);
        JButton btnReportes = crearBotonMenu("Reportes", "/img/Reporte.png", false);
        JButton btnGastos = crearBotonMenu("Gastos", "/img/billetera.png", false);
        JButton btnConfig = crearBotonMenu("Configuración", "/img/configuracion.png", false);

        btnCajero.addActionListener(e -> {
            new Caja_GUI().setVisible(true);
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

        // Se eliminó el botón de Modo Tampilan
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

        // Se eliminó el botón btnColapsar de aquí
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
        boton.setIcon(redimensionarIcono(iconPath, 18, 18));
        boton.setIconTextGap(13);
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

    private JPanel crearContenido() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(26, 28, 24, 28));
        contenedor.add(crearHeader(), BorderLayout.NORTH);
        contenedor.add(crearPanelPrincipal(), BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Lista de Productos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Gestionar datos de productos e inventario");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(subtitulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        // Se eliminó crearBotonIcono("/img/ojo.png") de aquí
        derecha.add(crearTarjetaHora());
        derecha.add(crearTarjetaUsuario());

        header.add(titulos, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);

        return header;
    }

    // El resto del código se mantiene igual...
    private JPanel crearTarjetaHora() {
        JPanel tarjeta = crearTarjetaHeader(110);
        JLabel lblTitulo = new JLabel("Hora");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(TEXTO);

        JLabel lblHora = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHora.setForeground(TEXTO_SUAVE);

        new Timer(1000, e -> {
            lblHora.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        }).start();

        tarjeta.add(lblTitulo);
        tarjeta.add(lblHora);
        return tarjeta;
    }

    private JPanel crearTarjetaHeader(int ancho) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 14, 6, 14)
        ));
        tarjeta.setPreferredSize(new Dimension(ancho, 40));
        return tarjeta;
    }

    private JPanel crearTarjetaUsuario() {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 10, 6, 12)
        ));
        tarjeta.setPreferredSize(new Dimension(150, 40));

        JLabel avatar = new JLabel(redimensionarIcono("/img/perfilPedro.jpg", 28, 28));
        avatar.setPreferredSize(new Dimension(28, 28));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel nombre = new JLabel("Manuel Gotera");
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nombre.setForeground(TEXTO);

        JLabel rol = new JLabel("Cajero");
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(TEXTO_SUAVE);

        textos.add(nombre);
        textos.add(rol);

        tarjeta.add(avatar, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)
        ));

        panel.add(crearBarraAcciones(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearBarraAcciones() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botones.setOpaque(false);

        JButton btnExportar = crearBotonSecundario("Exportar PDF", "/img/imprimir.png", 150);
        JButton btnAgregar = crearBotonPrimario("+  Añadir Producto", 170);

        btnExportar.addActionListener(e -> JOptionPane.showMessageDialog(
                this,
                "La exportación a PDF todavía no está conectada.",
                "Exportar PDF",
                JOptionPane.INFORMATION_MESSAGE
        ));

        btnAgregar.addActionListener(e -> abrirDialogoAgregar());

        botones.add(btnExportar);
        botones.add(btnAgregar);

        lblAlerta = new JLabel("1 producto tiene existencias por debajo del mínimo");
        lblAlerta.setIcon(redimensionarIcono("/img/stock.png", 17, 17));
        lblAlerta.setIconTextGap(8);
        lblAlerta.setOpaque(true);
        lblAlerta.setBackground(AMARILLO_FONDO);
        lblAlerta.setForeground(AMARILLO_TEXTO);
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlerta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(AMARILLO_BORDE, 10),
                new EmptyBorder(9, 12, 9, 12)
        ));

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(14, 0, 0, 0));

        cboCategoria = new JComboBox<>(new String[]{"Todas las categorías", "Comida", "Bebida"});
        cboCategoria.setPreferredSize(new Dimension(190, 36));
        cboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboCategoria.setBackground(Color.WHITE);
        cboCategoria.addActionListener(e -> aplicarFiltros());

        filtros.add(cboCategoria, BorderLayout.WEST);
        filtros.add(crearBuscador(), BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(14, 0, 0, 0));
        centro.add(lblAlerta, BorderLayout.NORTH);
        centro.add(filtros, BorderLayout.CENTER);

        wrapper.add(botones, BorderLayout.NORTH);
        wrapper.add(centro, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel crearBuscador() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(280, 36));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 10, 0, 10)
        ));

        JLabel icono = new JLabel(redimensionarIcono("/img/Lupa.png", 16, 16));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXTO);
        txtBuscar.putClientProperty("JTextField.placeholderText", "name or code product");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e) { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        panel.add(icono, BorderLayout.WEST);
        panel.add(txtBuscar, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane crearTabla() {
        String[] columnas = { "Código", "Nombre", "Categoría", "Costo", "Precio", "Stock", "Min. stock", "Status", "Acciones" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(38);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setShowHorizontalLines(true);
        tablaProductos.setGridColor(new Color(235, 238, 244));
        tablaProductos.setSelectionBackground(AZUL_CLARO);
        tablaProductos.setSelectionForeground(TEXTO);
        tablaProductos.setFillsViewportHeight(true);
        tablaProductos.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = tablaProductos.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(45, 52, 65));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaProductos.setRowSorter(sorter);

        ajustarColumnas();
        aplicarRenderers();
        agregarAccionesTabla();

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBorder(new RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private void ajustarColumnas() {
        int[] anchos = {85, 175, 110, 85, 85, 75, 95, 95, 115};
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void aplicarRenderers() {
        tablaProductos.getColumnModel().getColumn(0).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(1).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(2).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(3).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(4).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new StockRenderer());
        tablaProductos.getColumnModel().getColumn(6).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        tablaProductos.getColumnModel().getColumn(8).setCellRenderer(new AccionesRenderer());
    }

    private void agregarAccionesTabla() {
        tablaProductos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int filaVista = tablaProductos.rowAtPoint(e.getPoint());
                int columnaVista = tablaProductos.columnAtPoint(e.getPoint());
                if (filaVista < 0 || columnaVista < 0) return;
                if (tablaProductos.convertColumnIndexToModel(columnaVista) != 8) return;

                int filaModelo = tablaProductos.convertRowIndexToModel(filaVista);
                int xRelativo = e.getX() - tablaProductos.getCellRect(filaVista, columnaVista, true).x;
                int ancho = tablaProductos.getColumnModel().getColumn(columnaVista).getWidth();

                if (xRelativo < ancho / 2) {
                    abrirDialogoEditar(filaModelo);
                } else {
                    eliminarProducto(filaModelo);
                }
            }
        });
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(80, 88, 100));
        footer.add(lblFooter, BorderLayout.WEST);
        return footer;
    }

    private JButton crearBotonPrimario(String texto, int ancho) {
        JButton boton = crearBotonBase(texto, ancho);
        boton.setBackground(AZUL);
        boton.setForeground(Color.WHITE);
        boton.setBorder(new RoundedBorder(AZUL, 10));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { boton.setBackground(AZUL_HOVER); }
            public void mouseExited(MouseEvent e) { boton.setBackground(AZUL); }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto, String iconPath, int ancho) {
        JButton boton = crearBotonBase(texto, ancho);
        boton.setIcon(redimensionarIcono(iconPath, 16, 16));
        boton.setIconTextGap(8);
        boton.setBackground(Color.WHITE);
        boton.setForeground(AZUL);
        boton.setBorder(new RoundedBorder(AZUL, 10));
        return boton;
    }

    private JButton crearBotonBase(String texto, int ancho) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(ancho, 40));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void cargarDatosDemo() {
        agregarFila("BRG-001", "Inka kola", "Bebida", 3.00, 5.00, 120, 20, true);
        agregarFila("BRG-002", "Chicha morada", "Bebida", 4.00, 8.00, 81, 20, true);
        agregarFila("BRG-003", "Pisco sour", "Bebida", 4.00, 14.00, 32, 20, true);
        agregarFila("BRG-0B3", "Ceviche", "Comida", 4.00, 12.00, 32, 20, true);
        agregarFila("BRG-0A1", "Lomo saltado", "Comida", 4.00, 18.00, 3, 20, true);
        agregarFila("BRG-0S1", "Anticucho Corazon", "Comida", 4.00, 15.00, 120, 20, true);
        agregarFila("BRG-0S2", "Aji de Gallina", "Comida", 4.00, 15.00, 33, 20, true);
    }

    private void agregarFila(String codigo, String nombre, String categoria, double costo, double precio, int stock, int stockMin, boolean activo) {
        modeloTabla.addRow(new Object[]{ codigo, nombre, categoria, formatoMoneda(costo), formatoMoneda(precio), stock, stockMin, activo ? "activo" : "inactivo", "Editar   Eliminar" });
    }

    private void abrirDialogoAgregar() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog(owner);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            ProductoDialog.ProductoFormData data = dialog.getProductoData();
            if (codigoExiste(data.codigo)) {
                JOptionPane.showMessageDialog(this, "El código del producto ya existe.", "Código duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            agregarFila(data.codigo, data.nombre, data.categoria, data.costo, data.precio, data.stock, data.stockMinimo, data.activo);
            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Datos agregados con éxito");
        }
    }

    private void abrirDialogoEditar(int filaModelo) {
        ProductoDialog.ProductoFormData actual = obtenerDataDesdeFila(filaModelo);
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog(owner, actual);
        dialog.setVisible(true);
        if (dialog.isConfirmado()) {
            ProductoDialog.ProductoFormData data = dialog.getProductoData();
            if (!data.codigo.equalsIgnoreCase(actual.codigo) && codigoExiste(data.codigo)) {
                JOptionPane.showMessageDialog(this, "El código del producto ya existe.", "Código duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }
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

    private void eliminarProducto(int filaModelo) {
        String nombre = String.valueOf(modeloTabla.getValueAt(filaModelo, 1));
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea eliminar \"" + nombre + "\"?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (opcion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaModelo);
            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Producto eliminado correctamente");
        }
    }

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

    private boolean codigoExiste(String codigo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (String.valueOf(modeloTabla.getValueAt(i, 0)).equalsIgnoreCase(codigo)) return true;
        }
        return false;
    }

    private void aplicarFiltros() {
        if (sorter == null || txtBuscar == null || cboCategoria == null) return;
        String texto = txtBuscar.getText().trim();
        String categoria = String.valueOf(cboCategoria.getSelectedItem());
        RowFilter<DefaultTableModel, Object> filtroTexto = texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0, 1);
        RowFilter<DefaultTableModel, Object> filtroCategoria = categoria.equals("Todas las categorías") ? null : RowFilter.regexFilter("^" + Pattern.quote(categoria) + "$", 2);

        if (filtroTexto != null && filtroCategoria != null) sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(filtroTexto, filtroCategoria)));
        else if (filtroTexto != null) sorter.setRowFilter(filtroTexto);
        else if (filtroCategoria != null) sorter.setRowFilter(filtroCategoria);
        else sorter.setRowFilter(null);
        actualizarFooter();
    }

    private void actualizarAlertaStock() {
        int bajoMinimo = 0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int stock = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, 5)));
            int min = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, 6)));
            if (stock < min) bajoMinimo++;
        }
        if (bajoMinimo > 0) {
            lblAlerta.setVisible(true);
            lblAlerta.setText(bajoMinimo == 1 ? "1 producto tiene existencias por debajo del mínimo" : bajoMinimo + " productos tienen existencias por debajo del mínimo");
        } else lblAlerta.setVisible(false);
    }

    private void actualizarFooter() {
        int visibles = tablaProductos == null ? 0 : tablaProductos.getRowCount();
        int total = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " - " + total + " de " + total + " datos");
    }

    private void mostrarToast(String mensaje) {
        JLabel toast = new JLabel(mensaje);
        toast.setOpaque(true);
        toast.setBackground(new Color(235, 244, 255));
        toast.setForeground(AZUL);
        toast.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toast.setBorder(BorderFactory.createCompoundBorder(new RoundedBorder(new Color(174, 204, 252), 10), new EmptyBorder(10, 14, 10, 14)));
        JDialog dialog = new JDialog(this, false);
        dialog.setUndecorated(true);
        dialog.add(toast);
        dialog.pack();
        dialog.setLocation(getX() + getWidth() - dialog.getWidth() - 40, getY() + 70);
        dialog.setVisible(true);
        Timer timer = new Timer(1800, e -> dialog.dispose());
        timer.setRepeats(false);
        timer.start();
    }

    private String formatoMoneda(double valor) { return String.format("S/ %.2f", valor); }
    private double limpiarMoneda(String texto) { return Double.parseDouble(texto.replace("S/", "").trim()); }

    private ImageIcon redimensionarIcono(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource(path);
            if (imgURL != null) {
                ImageIcon iconOriginal = new ImageIcon(imgURL);
                Image img = iconOriginal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) { System.err.println("No se encontró el icono: " + path); }
        return null;
    }

    // --- Renderers y Clases Auxiliares ---
    private static class TextoRenderer extends DefaultTableCellRenderer {
        private final int alineacion;
        public TextoRenderer(int alineacion) { this.alineacion = alineacion; }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(alineacion);
            label.setBorder(new EmptyBorder(0, 8, 0, 8));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setForeground(TEXTO);
            label.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return label;
        }
    }

    private static class StockRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int stock = Integer.parseInt(String.valueOf(value));
            int stockMin = Integer.parseInt(String.valueOf(table.getValueAt(row, 6)));
            boolean bajo = stock < stockMin;
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(bajo ? ROJO : TEXTO);
            label.setFont(new Font("Segoe UI", bajo ? Font.BOLD : Font.PLAIN, 12));
            label.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return label;
        }
    }

    private static class StatusRenderer extends JPanel implements TableCellRenderer {
        public StatusRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            String estado = String.valueOf(value);
            Color color = estado.equalsIgnoreCase("activo") ? new Color(83, 137, 174) : new Color(150, 157, 168);
            add(new PillLabel(estado, color), new GridBagConstraints());
            return this;
        }
    }

    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        public AccionesRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 10, 7)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            JLabel editar = new JLabel("✎"); editar.setFont(new Font("Segoe UI", Font.BOLD, 14)); editar.setForeground(AZUL);
            JLabel eliminar = new JLabel("⌫"); eliminar.setFont(new Font("Segoe UI", Font.BOLD, 14)); eliminar.setForeground(ROJO);
            add(editar); add(eliminar);
            return this;
        }
    }

    private static class PillLabel extends JLabel {
        private final Color colorFondo;
        public PillLabel(String texto, Color colorFondo) {
            super(texto, SwingConstants.CENTER); this.colorFondo = colorFondo;
            setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 13, 4, 13)); setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose(); super.paintComponent(g);
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color; private final int arc;
        public RoundedBorder(Color color, int arc) { this.color = color; this.arc = arc; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color); g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g2.dispose();
        }
        public Insets getBorderInsets(Component c) { return new Insets(1, 1, 1, 1); }
    }
}