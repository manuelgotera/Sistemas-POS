package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.time.*;
import java.time.temporal.*;
import java.util.Date;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class ArticulosStockFrame extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_HOVER = new Color(18, 65, 128);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO = new Color(246, 248, 251);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color TEXTO = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color VERDE = new Color(40, 167, 69);
    private static final Color NARANJA = new Color(255, 145, 77);
    private static final Color GRIS = new Color(150, 157, 168);
    private static final Color AMARILLO_FONDO = new Color(255, 249, 219);
    private static final Color AMARILLO_BORDE = new Color(245, 213, 93);
    private static final Color AMARILLO_TEXTO = new Color(128, 89, 0);

    private static final int COL_CODIGO = 0;
    private static final int COL_NOMBRE = 1;
    private static final int COL_CATEGORIA = 2;
    private static final int COL_UNIDAD = 3;
    private static final int COL_PROVEEDOR = 4;
    private static final int COL_COSTO = 5;
    private static final int COL_PRECIO = 6;
    private static final int COL_STOCK = 7;
    private static final int COL_STOCK_MIN = 8;
    private static final int COL_MERMA = 9;
    private static final int COL_VENCIMIENTO = 10;
    private static final int COL_ALERTA = 11;
    private static final int COL_STATUS = 12;
    private static final int COL_ACCIONES = 13;

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
        recalcularAlertas();
        actualizarAlertaStock();
        actualizarFooter();
    }

    private void configurarVentana() {
        setTitle("Artículos y Stock");
        setSize(1280, 740);
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        root.add(new MenuSidebar(this, "Stock"), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
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

        JLabel titulo = new JLabel("Lista de Productos e Insumos");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Gestionar stock, proveedores, vencimientos y mermas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(subtitulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(crearTarjetaHora());
        derecha.add(crearTarjetaUsuario());

        header.add(titulos, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);

        return header;
    }

    private JPanel crearTarjetaHora() {
        JPanel tarjeta = crearTarjetaHeader(110);
        JLabel lblTitulo = new JLabel("Hora");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(TEXTO);

        JLabel lblHora = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHora.setForeground(TEXTO_SUAVE);

        new Timer(1000, e -> lblHora.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()))).start();

        tarjeta.add(lblTitulo);
        tarjeta.add(lblHora);
        return tarjeta;
    }

    private JPanel crearTarjetaHeader(int ancho) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 14, 6, 14)
        ));
        tarjeta.setPreferredSize(new Dimension(ancho, 40));
        return tarjeta;
    }

    private JPanel crearTarjetaUsuario() {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 10, 6, 12)
        ));
        tarjeta.setPreferredSize(new Dimension(150, 40));

        JLabel avatar = new JLabel(MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 28, 28));
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
                new MenuSidebar.RoundedBorder(BORDE, 14),
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

        lblAlerta = new JLabel("Sin alertas de stock");
        lblAlerta.setIcon(MenuSidebar.redimensionarIcono("/img/stock.png", 17, 17));
        lblAlerta.setIconTextGap(8);
        lblAlerta.setOpaque(true);
        lblAlerta.setBackground(AMARILLO_FONDO);
        lblAlerta.setForeground(AMARILLO_TEXTO);
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlerta.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(AMARILLO_BORDE, 10),
                new EmptyBorder(9, 12, 9, 12)
        ));

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(14, 0, 0, 0));

        cboCategoria = new JComboBox<>(new String[]{
                "Todas las categorías",
                "Consumo crudo",
                "Preparado",
                "Bebida"
        });
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
        panel.setPreferredSize(new Dimension(300, 36));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 10, 0, 10)
        ));

        JLabel icono = new JLabel(MenuSidebar.redimensionarIcono("/img/Lupa.png", 16, 16));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXTO);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por código, nombre o proveedor");
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
        String[] columnas = {
                "Código", "Nombre", "Tipo", "Unidad", "Proveedor",
                "Costo", "Precio", "Stock", "Min.", "Merma",
                "Vencimiento", "Alerta", "Status", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setRowHeight(40);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setShowHorizontalLines(true);
        tablaProductos.setGridColor(new Color(235, 238, 244));
        tablaProductos.setSelectionBackground(AZUL_CLARO);
        tablaProductos.setSelectionForeground(TEXTO);
        tablaProductos.setFillsViewportHeight(true);
        tablaProductos.setIntercellSpacing(new Dimension(0, 0));
        tablaProductos.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    private void ajustarColumnas() {
        int[] anchos = {85, 165, 125, 80, 150, 80, 80, 70, 70, 75, 110, 125, 95, 100};
        for (int i = 0; i < anchos.length; i++) {
            tablaProductos.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private void aplicarRenderers() {
        tablaProductos.getColumnModel().getColumn(COL_CODIGO).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(COL_NOMBRE).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(COL_CATEGORIA).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(COL_UNIDAD).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(COL_PROVEEDOR).setCellRenderer(new TextoRenderer(SwingConstants.LEFT));
        tablaProductos.getColumnModel().getColumn(COL_COSTO).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(COL_PRECIO).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(COL_STOCK).setCellRenderer(new StockRenderer());
        tablaProductos.getColumnModel().getColumn(COL_STOCK_MIN).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(COL_MERMA).setCellRenderer(new MermaRenderer());
        tablaProductos.getColumnModel().getColumn(COL_VENCIMIENTO).setCellRenderer(new TextoRenderer(SwingConstants.CENTER));
        tablaProductos.getColumnModel().getColumn(COL_ALERTA).setCellRenderer(new AlertaRenderer());
        tablaProductos.getColumnModel().getColumn(COL_STATUS).setCellRenderer(new StatusRenderer());
        tablaProductos.getColumnModel().getColumn(COL_ACCIONES).setCellRenderer(new AccionesRenderer());
    }

    private void agregarAccionesTabla() {
        tablaProductos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int filaVista = tablaProductos.rowAtPoint(e.getPoint());
                int columnaVista = tablaProductos.columnAtPoint(e.getPoint());
                if (filaVista < 0 || columnaVista < 0) return;
                if (tablaProductos.convertColumnIndexToModel(columnaVista) != COL_ACCIONES) return;

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
        boton.setBorder(new MenuSidebar.RoundedBorder(AZUL, 10));
        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { boton.setBackground(AZUL_HOVER); }
            public void mouseExited(MouseEvent e) { boton.setBackground(AZUL); }
        });
        return boton;
    }

    private JButton crearBotonSecundario(String texto, String iconPath, int ancho) {
        JButton boton = crearBotonBase(texto, ancho);
        boton.setIcon(MenuSidebar.redimensionarIcono(iconPath, 16, 16));
        boton.setIconTextGap(8);
        boton.setBackground(Color.WHITE);
        boton.setForeground(AZUL);
        boton.setBorder(new MenuSidebar.RoundedBorder(AZUL, 10));
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
        agregarFila("INS-001", "Pollo", "Consumo crudo", "kg", "Avícola San José", 8.50, 0, 12, 10, 0, fechaEnDias(12), true);
        agregarFila("INS-002", "Arroz", "Consumo crudo", "kg", "Mayorista Norte", 3.40, 0, 4, 15, 0, fechaEnDias(60), true);
        agregarFila("INS-003", "Chicha morada", "Preparado", "L", "Producción interna", 2.20, 8.00, 18, 8, 1.5, fechaEnDias(3), true);
        agregarFila("INS-004", "Inka Kola", "Bebida", "unidad", "Distribuidora Lindley", 3.00, 5.00, 120, 20, 0, fechaEnDias(120), true);
        agregarFila("INS-005", "Pescado", "Consumo crudo", "kg", "Mercado Modelo", 13.00, 0, 8, 6, 0, fechaEnDias(1), true);
        agregarFila("INS-006", "Salsa criolla", "Preparado", "kg", "Producción interna", 4.00, 0, 2, 5, 0.5, fechaEnDias(-1), true);
        agregarFila("INS-007", "Gaseosa personal", "Bebida", "unidad", "Distribuidora Centro", 2.50, 4.50, 30, 20, 0, "", true);
    }

    private String fechaEnDias(int dias) {
        return LocalDate.now().plusDays(dias).toString();
    }

    private void agregarFila(String codigo, String nombre, String categoria, String unidad,
                             String proveedor, double costo, double precio, int stock,
                             int stockMin, double merma, String fechaVencimiento, boolean activo) {

        String alerta = calcularAlerta(stock, stockMin, merma, fechaVencimiento);

        modeloTabla.addRow(new Object[]{
                codigo, nombre, categoria, unidad, proveedor,
                formatoMoneda(costo), formatoMoneda(precio), stock, stockMin, merma,
                fechaVencimiento == null || fechaVencimiento.trim().isEmpty() ? "Sin fecha" : fechaVencimiento,
                alerta, activo ? "activo" : "inactivo", "Editar   Eliminar"
        });
    }

    private void abrirDialogoAgregar() {
        // Asume que ProductoDialog está implementado en tu proyecto
        Window owner = SwingUtilities.getWindowAncestor(this);
        ProductoDialog dialog = new ProductoDialog(owner);
        dialog.setVisible(true);

        if (dialog.isConfirmado()) {
            ProductoDialog.ProductoFormData data = dialog.getProductoData();

            if (codigoExiste(data.codigo)) {
                JOptionPane.showMessageDialog(this, "El código del producto ya existe.", "Código duplicado", JOptionPane.WARNING_MESSAGE);
                return;
            }

            agregarFila(data.codigo, data.nombre, data.categoria, data.unidadMedida, data.proveedor,
                    data.costo, data.precio, data.stock, data.stockMinimo, data.merma, data.fechaVencimiento, data.activo);

            recalcularAlertas();
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

            modeloTabla.setValueAt(data.codigo, filaModelo, COL_CODIGO);
            modeloTabla.setValueAt(data.nombre, filaModelo, COL_NOMBRE);
            modeloTabla.setValueAt(data.categoria, filaModelo, COL_CATEGORIA);
            modeloTabla.setValueAt(data.unidadMedida, filaModelo, COL_UNIDAD);
            modeloTabla.setValueAt(data.proveedor, filaModelo, COL_PROVEEDOR);
            modeloTabla.setValueAt(formatoMoneda(data.costo), filaModelo, COL_COSTO);
            modeloTabla.setValueAt(formatoMoneda(data.precio), filaModelo, COL_PRECIO);
            modeloTabla.setValueAt(data.stock, filaModelo, COL_STOCK);
            modeloTabla.setValueAt(data.stockMinimo, filaModelo, COL_STOCK_MIN);
            modeloTabla.setValueAt(data.merma, filaModelo, COL_MERMA);
            modeloTabla.setValueAt(data.fechaVencimiento == null || data.fechaVencimiento.trim().isEmpty()
                    ? "Sin fecha" : data.fechaVencimiento, filaModelo, COL_VENCIMIENTO);
            modeloTabla.setValueAt(data.activo ? "activo" : "inactivo", filaModelo, COL_STATUS);

            recalcularAlertas();
            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Datos actualizados con éxito");
        }
    }

    private void eliminarProducto(int filaModelo) {
        String nombre = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE));
        int opcion = JOptionPane.showConfirmDialog(
                this, "¿Desea eliminar \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
        );

        if (opcion == JOptionPane.YES_OPTION) {
            modeloTabla.removeRow(filaModelo);
            recalcularAlertas();
            actualizarAlertaStock();
            aplicarFiltros();
            mostrarToast("Producto eliminado correctamente");
        }
    }

    private ProductoDialog.ProductoFormData obtenerDataDesdeFila(int fila) {
        String codigo = String.valueOf(modeloTabla.getValueAt(fila, COL_CODIGO));
        String nombre = String.valueOf(modeloTabla.getValueAt(fila, COL_NOMBRE));
        String categoria = String.valueOf(modeloTabla.getValueAt(fila, COL_CATEGORIA));
        String unidad = String.valueOf(modeloTabla.getValueAt(fila, COL_UNIDAD));
        String proveedor = String.valueOf(modeloTabla.getValueAt(fila, COL_PROVEEDOR));
        double costo = limpiarMoneda(String.valueOf(modeloTabla.getValueAt(fila, COL_COSTO)));
        double precio = limpiarMoneda(String.valueOf(modeloTabla.getValueAt(fila, COL_PRECIO)));
        int stock = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(fila, COL_STOCK)));
        int stockMin = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(fila, COL_STOCK_MIN)));
        double merma = Double.parseDouble(String.valueOf(modeloTabla.getValueAt(fila, COL_MERMA)));
        String fecha = String.valueOf(modeloTabla.getValueAt(fila, COL_VENCIMIENTO));
        boolean activo = String.valueOf(modeloTabla.getValueAt(fila, COL_STATUS)).equalsIgnoreCase("activo");

        if (fecha.equalsIgnoreCase("Sin fecha")) fecha = "";
        String motivoMerma = merma > 0 ? "Otro" : "Sin merma";

        return new ProductoDialog.ProductoFormData(
                codigo, nombre, categoria, costo, precio, stock, stockMin, activo,
                unidad, proveedor, fecha, merma, motivoMerma
        );
    }

    private boolean codigoExiste(String codigo) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if (String.valueOf(modeloTabla.getValueAt(i, COL_CODIGO)).equalsIgnoreCase(codigo)) {
                return true;
            }
        }
        return false;
    }

    private void aplicarFiltros() {
        if (sorter == null || txtBuscar == null || cboCategoria == null) return;

        String texto = txtBuscar.getText().trim();
        String categoria = String.valueOf(cboCategoria.getSelectedItem());

        RowFilter<DefaultTableModel, Object> filtroTexto = texto.isEmpty()
                ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto), COL_CODIGO, COL_NOMBRE, COL_PROVEEDOR);

        RowFilter<DefaultTableModel, Object> filtroCategoria = categoria.equals("Todas las categorías")
                ? null : RowFilter.regexFilter("^" + Pattern.quote(categoria) + "$", COL_CATEGORIA);

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

    private void recalcularAlertas() {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            int stock = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, COL_STOCK)));
            int stockMin = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, COL_STOCK_MIN)));
            double merma = Double.parseDouble(String.valueOf(modeloTabla.getValueAt(i, COL_MERMA)));
            String fecha = String.valueOf(modeloTabla.getValueAt(i, COL_VENCIMIENTO));

            modeloTabla.setValueAt(calcularAlerta(stock, stockMin, merma, fecha), i, COL_ALERTA);
        }
    }

    private String calcularAlerta(int stock, int stockMin, double merma, String fechaVencimiento) {
        if (fechaVencimiento != null && !fechaVencimiento.trim().isEmpty() && !fechaVencimiento.equalsIgnoreCase("Sin fecha")) {
            try {
                LocalDate fecha = LocalDate.parse(fechaVencimiento);
                long dias = ChronoUnit.DAYS.between(LocalDate.now(), fecha);

                if (dias < 0) return "Vencido";
                if (dias <= 7) return "Vence pronto";
            } catch (Exception e) {
                return "Fecha inválida";
            }
        }
        if (stock <= 0) return "Agotado";
        if (stock < stockMin) return "Stock bajo";
        if (merma > 0) return "Con merma";
        return "OK";
    }

    private void actualizarAlertaStock() {
        int stockBajo = 0, vencidos = 0, porVencer = 0, conMerma = 0, agotados = 0;

        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            String alerta = String.valueOf(modeloTabla.getValueAt(i, COL_ALERTA));
            if (alerta.equalsIgnoreCase("Stock bajo")) stockBajo++;
            else if (alerta.equalsIgnoreCase("Vencido")) vencidos++;
            else if (alerta.equalsIgnoreCase("Vence pronto")) porVencer++;
            else if (alerta.equalsIgnoreCase("Con merma")) conMerma++;
            else if (alerta.equalsIgnoreCase("Agotado")) agotados++;
        }

        int totalAlertas = stockBajo + vencidos + porVencer + conMerma + agotados;

        if (totalAlertas > 0) {
            lblAlerta.setVisible(true);
            lblAlerta.setText("Alertas: " + stockBajo + " stock bajo, " + agotados + " agotados, "
                    + porVencer + " por vencer, " + vencidos + " vencidos, " + conMerma + " con merma");
        } else {
            lblAlerta.setVisible(false);
        }
    }

    private void actualizarFooter() {
        int visibles = tablaProductos == null ? 0 : tablaProductos.getRowCount();
        int total = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " de " + total + " datos");
    }

    private void mostrarToast(String mensaje) {
        JLabel toast = new JLabel(mensaje);
        toast.setOpaque(true);
        toast.setBackground(new Color(235, 244, 255));
        toast.setForeground(AZUL);
        toast.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toast.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(new Color(174, 204, 252), 10),
                new EmptyBorder(10, 14, 10, 14)
        ));

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
            int stockMin = Integer.parseInt(String.valueOf(table.getValueAt(row, COL_STOCK_MIN)));
            boolean agotado = stock <= 0, bajo = stock < stockMin;
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(agotado || bajo ? ROJO : TEXTO);
            label.setFont(new Font("Segoe UI", agotado || bajo ? Font.BOLD : Font.PLAIN, 12));
            label.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return label;
        }
    }

    private static class MermaRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            double merma = Double.parseDouble(String.valueOf(value));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setForeground(merma > 0 ? NARANJA : TEXTO);
            label.setFont(new Font("Segoe UI", merma > 0 ? Font.BOLD : Font.PLAIN, 12));
            label.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return label;
        }
    }

    private static class AlertaRenderer extends JPanel implements TableCellRenderer {
        public AlertaRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            String alerta = String.valueOf(value);
            Color color;
            if (alerta.equalsIgnoreCase("OK")) color = VERDE;
            else if (alerta.equalsIgnoreCase("Stock bajo") || alerta.equalsIgnoreCase("Vence pronto") || alerta.equalsIgnoreCase("Con merma")) color = NARANJA;
            else if (alerta.equalsIgnoreCase("Vencido") || alerta.equalsIgnoreCase("Agotado") || alerta.equalsIgnoreCase("Fecha inválida")) color = ROJO;
            else color = GRIS;
            add(new PillLabel(alerta, color), new GridBagConstraints());
            return this;
        }
    }

    private static class StatusRenderer extends JPanel implements TableCellRenderer {
        public StatusRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            String estado = String.valueOf(value);
            Color color = estado.equalsIgnoreCase("activo") ? new Color(83, 137, 174) : GRIS;
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
            super(texto, SwingConstants.CENTER);
            this.colorFondo = colorFondo;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 13, 4, 13));
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(colorFondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}