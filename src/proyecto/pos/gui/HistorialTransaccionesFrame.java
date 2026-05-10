package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class HistorialTransaccionesFrame extends JFrame {

    // ─── COLORES ────────────────────────────
    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_HOVER  = new Color(18, 65, 128);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);
    private static final Color TEXTO       = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color VERDE_BG    = new Color(225, 245, 238);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);
    private static final Color VERDE       = new Color(40, 167, 69);
    private static final Color ROJO        = new Color(220, 53, 69);
    private static final Color NARANJA     = new Color(255, 145, 77);
    private static final Color GRIS        = new Color(150, 157, 168);

    // ─── COLUMNAS ────────────────────────────────────────────────────────────
    private static final int COL_NUM        = 0;
    private static final int COL_FECHA      = 1;
    private static final int COL_CAJERO     = 2;
    private static final int COL_ITEMS      = 3;
    private static final int COL_MONTO      = 4;
    private static final int COL_METODO     = 5;
    private static final int COL_ESTADO     = 6;
    private static final int COL_COMPROBANTE= 7;
    private static final int COL_ACCIONES   = 8;

    // ─── REGISTRO ESTÁTICO DE VENTAS ─────────────────────────────────────────
    private static final Map<String, VentaData> ventasRegistradas = new LinkedHashMap<>();
    private static int contadorTransacciones = 1;
    private static int contadorBoletas  = 1;
    private static int contadorFacturas = 1;

    public static String registrarVentaDesdeCaja(String cajero, String metodoPago,
            String tipoComprobante, List<String> productos, double total) {

        String numeroTransaccion = String.format("TRX-RUN-%03d", contadorTransacciones++);
        String numeroComprobante = "Factura".equalsIgnoreCase(tipoComprobante)
                ? String.format("F001-%06d", contadorFacturas++)
                : String.format("B001-%06d", contadorBoletas++);

        ventasRegistradas.put(numeroTransaccion, new VentaData(
                numeroTransaccion,
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()),
                cajero, metodoPago, tipoComprobante, numeroComprobante,
                new ArrayList<>(productos), total, "Completada"
        ));
        return numeroTransaccion;
    }

    // ─── MODELO DE VENTA ─────────────────────────────────────────────────────
    private static class VentaData {
        String numeroTransaccion, fechaHora, cajero, metodoPago;
        String tipoComprobante, numeroComprobante, estado;
        List<String> productos;
        double total;

        VentaData(String num, String fecha, String cajero, String metodo,
                  String tipo, String numComp, List<String> productos, double total, String estado) {
            this.numeroTransaccion = num; this.fechaHora = fecha;
            this.cajero = cajero;         this.metodoPago = metodo;
            this.tipoComprobante = tipo;  this.numeroComprobante = numComp;
            this.productos = productos;   this.total = total;
            this.estado = estado;
        }

        String comprobanteCompleto() { return tipoComprobante + " " + numeroComprobante; }
    }

    // ─── COMPONENTES UI ──────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboPeriodo;
    private JLabel lblFooter;

    // Stats
    private JLabel lblTotalTrx;
    private JLabel lblVentaTotales;
    private JLabel lblBruto;

    public HistorialTransaccionesFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        actualizarStats();
    }

    private void configurarVentana() {
        setTitle("Historial de Transacciones");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        // ── USA MenuSidebar igual que ArticulosStockFrame ──
        root.add(new MenuSidebar(this, "Historial"), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    // ─── CONTENIDO ───────────────────────────────────────────────────────────

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

        JLabel titulo = new JLabel("Historial de Transacciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Consulta y gestiona todas las ventas registradas");
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
        JPanel tarjeta = crearTarjetaHeaderBase(110);
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

    private JPanel crearTarjetaHeaderBase(int ancho) {
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

        JLabel nombre = new JLabel("Uwu Fernandez");
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
        panel.add(crearTabla(),         BorderLayout.CENTER);
        panel.add(crearFooter(),        BorderLayout.SOUTH);
        return panel;
    }

    // ─── BARRA DE ACCIONES + STATS + FILTROS ─────────────────────────────────

    private JPanel crearBarraAcciones() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 14, 0));

        // ── Stats Cards ──
        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel cardTrx   = crearStatCard("Total Transacciones", "0",   new Color(238, 237, 254), new Color(100, 90, 210));
        JPanel cardVenta = crearStatCard("Ventas Totales",       "S/ 0.00", new Color(230, 241, 251), AZUL);
        JPanel cardBruto = crearStatCard("Bruto Estimado",       "S/ 0.00", new Color(234, 243, 222), new Color(50, 140, 80));

        lblTotalTrx    = obtenerLabelValor(cardTrx);
        lblVentaTotales = obtenerLabelValor(cardVenta);
        lblBruto        = obtenerLabelValor(cardBruto);

        stats.add(cardTrx);
        stats.add(cardVenta);
        stats.add(cardBruto);

        // ── Filtros ──
        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);

        cboPeriodo = new JComboBox<>(new String[]{"Todos los periodos", "Hoy", "Esta semana", "Este mes"});
        cboPeriodo.setPreferredSize(new Dimension(190, 36));
        cboPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboPeriodo.setBackground(Color.WHITE);
        cboPeriodo.addActionListener(e -> aplicarFiltros());

        filtros.add(cboPeriodo,    BorderLayout.WEST);
        filtros.add(crearBuscador(), BorderLayout.EAST);

        wrapper.add(stats,   BorderLayout.NORTH);
        wrapper.add(filtros, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel crearStatCard(String etiqueta, String valor, Color colorFondo, Color colorTexto) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JPanel icono = new JPanel();
        icono.setBackground(colorFondo);
        icono.setPreferredSize(new Dimension(46, 46));
        icono.setBorder(new MenuSidebar.RoundedBorder(colorFondo, 10));

        JPanel texto = new JPanel(new GridBagLayout());
        texto.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEtiqueta.setForeground(new Color(140, 140, 140));
        texto.add(lblEtiqueta, gbc);

        gbc.gridy = 1;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(colorTexto);
        texto.add(lblValor, gbc);

        card.add(icono, BorderLayout.WEST);
        card.add(texto, BorderLayout.CENTER);
        return card;
    }

    private JLabel obtenerLabelValor(JPanel card) {
        JPanel texto = (JPanel) card.getComponent(1);
        return (JLabel) texto.getComponent(1);
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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por N°, cajero, comprobante...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        panel.add(icono, BorderLayout.WEST);
        panel.add(txtBuscar, BorderLayout.CENTER);
        return panel;
    }

    // ─── TABLA ───────────────────────────────────────────────────────────────

    private JScrollPane crearTabla() {
        String[] columnas = {
            "N° Transacción", "Fecha / Hora", "Cajero",
            "Items", "Monto Total", "Método de Pago",
            "Estado", "Comprobante", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(44);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(235, 238, 244));
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(45, 52, 65));
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
        header.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        // Anchos de columna
        int[] anchos = {130, 145, 130, 60, 100, 120, 110, 150, 110};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Renderers
        tabla.getColumnModel().getColumn(COL_ESTADO).setCellRenderer(new EstadoRenderer());
        tabla.getColumnModel().getColumn(COL_ACCIONES).setCellRenderer(new AccionesRenderer());

        DefaultTableCellRenderer centroR = new DefaultTableCellRenderer();
        centroR.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(COL_ITEMS).setCellRenderer(centroR);
        tabla.getColumnModel().getColumn(COL_MONTO).setCellRenderer(centroR);
        tabla.getColumnModel().getColumn(COL_METODO).setCellRenderer(centroR);
        tabla.getColumnModel().getColumn(COL_COMPROBANTE).setCellRenderer(centroR);

        // Clic en acciones
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int filaVista = tabla.rowAtPoint(e.getPoint());
                int colVista  = tabla.columnAtPoint(e.getPoint());
                if (filaVista < 0 || colVista < 0) return;
                if (tabla.convertColumnIndexToModel(colVista) != COL_ACCIONES) return;

                int filaModelo = tabla.convertRowIndexToModel(filaVista);
                int xRel  = e.getX() - tabla.getCellRect(filaVista, colVista, true).x;
                int ancho = tabla.getColumnModel().getColumn(colVista).getWidth();

                if (xRel < ancho / 2) {
                    abrirDetalleTransaccion(filaModelo);
                } else {
                    imprimirTransaccion(filaModelo);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
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

    // ─── DATOS ───────────────────────────────────────────────────────────────

    private void cargarDatosDemo() {
        // Datos demo fijos
        agregarFila("TRX-2025-001", "22/12/2025 12:00", "Uwu Fernandez", 2, "S/ 5.00",  "Tarjeta",  "Completada", "Boleta B001-000001");
        agregarFila("TRX-2025-002", "22/12/2025 14:00", "Uwu Fernandez", 1, "S/ 8.00",  "Tarjeta",  "Completada", "Boleta B001-000002");
        agregarFila("TRX-2025-003", "22/12/2025 14:55", "Uwu Fernandez", 2, "S/ 18.00", "Efectivo", "Completada", "Boleta B001-000003");

        // Cargar ventas registradas dinámicamente desde caja
        for (VentaData venta : ventasRegistradas.values()) {
            agregarFila(
                venta.numeroTransaccion, venta.fechaHora, venta.cajero,
                venta.productos.size(),
                String.format("S/ %.2f", venta.total),
                venta.metodoPago, venta.estado, venta.comprobanteCompleto()
            );
        }
        actualizarFooter();
    }

    private void agregarFila(String num, String fecha, String cajero,
                              int items, String monto, String metodo,
                              String estado, String comprobante) {
        modeloTabla.addRow(new Object[]{num, fecha, cajero, items, monto, metodo, estado, comprobante, ""});
    }

    // ─── DETALLE DE TRANSACCIÓN ───────────────────────────────────────────────

    private void abrirDetalleTransaccion(int filaModelo) {
        String numTrx       = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NUM));
        String fecha        = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_FECHA));
        String cajero       = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_CAJERO));
        String estado       = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ESTADO));
        String monto        = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_MONTO));
        String metodo       = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_METODO));
        String comprobante  = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_COMPROBANTE));
        VentaData venta     = ventasRegistradas.get(numTrx);

        JDialog dialog = new JDialog(this, "Detalle de Transacción", true);
        dialog.setSize(370, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        // ── Encabezado ──
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Color.WHITE);
        encabezado.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Detalle de la transacción");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel btnX = new JLabel("✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnX.setForeground(new Color(100, 100, 100));
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
        });
        encabezado.add(lblTitulo, BorderLayout.WEST);
        encabezado.add(btnX,      BorderLayout.EAST);

        // ── Cuerpo ──
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new GridBagLayout());
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(new EmptyBorder(0, 20, 16, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.gridx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        int fila = 0;

        // Bloque azul de info
        JPanel bloqueInfo = new JPanel(new GridBagLayout());
        bloqueInfo.setBackground(AZUL_CLARO);
        bloqueInfo.setBorder(new EmptyBorder(14, 14, 14, 14));

        GridBagConstraints gi = new GridBagConstraints();
        gi.fill = GridBagConstraints.HORIZONTAL;
        gi.weightx = 1; gi.insets = new Insets(2, 0, 2, 0);

        agregarParBloque(bloqueInfo, gi, 0, "N° Transacción", numTrx, "Fecha / Hora", fecha);
        gi.gridx = 0; gi.gridy = 2; gi.gridwidth = 2; gi.insets = new Insets(8,0,8,0);
        bloqueInfo.add(new JSeparator(), gi);
        gi.gridwidth = 1; gi.insets = new Insets(2,0,2,0);

        // Cajero + Estado badge
        gi.gridx = 0; gi.gridy = 3;
        JLabel lCajLbl = new JLabel("Cajero");
        lCajLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lCajLbl.setForeground(new Color(120,120,120));
        bloqueInfo.add(lCajLbl, gi);

        gi.gridx = 1;
        JLabel lEstLbl = new JLabel("Estado");
        lEstLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lEstLbl.setForeground(new Color(120,120,120));
        bloqueInfo.add(lEstLbl, gi);

        gi.gridx = 0; gi.gridy = 4;
        JLabel lCajVal = new JLabel(cajero);
        lCajVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bloqueInfo.add(lCajVal, gi);

        gi.gridx = 1;
        JLabel lEstBadge = new JLabel(estado);
        lEstBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lEstBadge.setOpaque(true);
        lEstBadge.setBackground(VERDE_BG);
        lEstBadge.setForeground(VERDE_TEXT);
        lEstBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        bloqueInfo.add(lEstBadge, gi);

        g.gridy = fila++;
        cuerpo.add(bloqueInfo, g);

        // Sección productos
        g.gridy = fila++;
        g.insets = new Insets(14, 0, 8, 0);
        JLabel lblProductos = new JLabel("Productos de la transacción");
        lblProductos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cuerpo.add(lblProductos, g);
        g.insets = new Insets(0, 0, 0, 0);

        if (venta != null) {
            for (String producto : venta.productos) {
                String[] partes  = producto.split(" - ", 2);
                String nombre    = partes.length > 0 ? partes[0] : producto;
                String detalle   = partes.length > 1 ? partes[1] : "";
                g.gridy = fila++;
                cuerpo.add(crearFilaProducto(nombre, detalle, detalle), g);
            }
        } else {
            // Productos demo según transacción
            String[][] demoProd = obtenerProductosDemo(numTrx);
            for (String[] prod : demoProd) {
                g.gridy = fila++;
                cuerpo.add(crearFilaProducto(prod[0], prod[1], prod[2]), g);
            }
        }

        // Separador
        g.gridy = fila++;
        g.insets = new Insets(10, 0, 10, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        // Resumen
        g.gridy = fila++; cuerpo.add(crearFilaResumen("Sub total", monto, false), g);
        g.gridy = fila++; cuerpo.add(crearFilaResumen("Comprobante", comprobante, false), g);

        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++; cuerpo.add(crearFilaMontoPagado("Monto pagado", monto), g);

        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++;
        g.insets = new Insets(6, 0, 4, 0);
        JLabel lblPago = new JLabel("Pago");
        lblPago.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cuerpo.add(lblPago, g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++; cuerpo.add(crearFilaResumen("Método", metodo, false), g);

        g.gridy = fila++;
        JPanel filaRef = crearFilaResumen("Referencia", numTrx, false);
        ((JLabel) filaRef.getComponent(0)).setForeground(AZUL);
        ((JLabel) filaRef.getComponent(1)).setForeground(AZUL);
        cuerpo.add(filaRef, g);

        // ── Botones ──
        JPanel botones = new JPanel(new GridBagLayout());
        botones.setBackground(Color.WHITE);
        botones.setBorder(new EmptyBorder(14, 20, 18, 20));

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.HORIZONTAL;
        gb.weightx = 1;
        gb.insets = new Insets(0, 0, 0, 10);

        JButton btnCerrar = crearBotonBase("Cerrar", 0);
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setForeground(new Color(60, 60, 60));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCerrar.addActionListener(e -> dialog.dispose());

        gb.gridx = 0;
        botones.add(btnCerrar, gb);

        gb.insets = new Insets(0, 0, 0, 0);
        JButton btnReimprimir = crearBotonBase("  Reimprimir", 0);
        btnReimprimir.setBackground(AZUL);
        btnReimprimir.setForeground(Color.WHITE);
        btnReimprimir.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnReimprimir.addActionListener(e -> {
            dialog.dispose();
            imprimirTransaccion(filaModelo);
        });

        gb.gridx = 1;
        botones.add(btnReimprimir, gb);

        JScrollPane scrollCuerpo = new JScrollPane(cuerpo);
        scrollCuerpo.setBorder(BorderFactory.createEmptyBorder());
        scrollCuerpo.getViewport().setBackground(Color.WHITE);

        contenido.add(encabezado,   BorderLayout.NORTH);
        contenido.add(scrollCuerpo, BorderLayout.CENTER);
        contenido.add(botones,      BorderLayout.SOUTH);

        dialog.setContentPane(contenido);
        dialog.setVisible(true);
    }

    /** Agrega un par de columnas etiqueta/valor al bloque de info azul */
    private void agregarParBloque(JPanel panel, GridBagConstraints gi,
                                   int gridy, String lbl1, String val1, String lbl2, String val2) {
        gi.gridx = 0; gi.gridy = gridy;
        JLabel l1 = new JLabel(lbl1);
        l1.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l1.setForeground(new Color(120, 120, 120));
        panel.add(l1, gi);

        gi.gridx = 1;
        JLabel l2 = new JLabel(lbl2);
        l2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        l2.setForeground(new Color(120, 120, 120));
        panel.add(l2, gi);

        gi.gridx = 0; gi.gridy = gridy + 1;
        JLabel v1 = new JLabel(val1);
        v1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(v1, gi);

        gi.gridx = 1;
        JLabel v2 = new JLabel(val2);
        v2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(v2, gi);
    }

    private String[][] obtenerProductosDemo(String numTrx) {
        if ("TRX-2025-001".equals(numTrx))
            return new String[][]{{"Lomo saltado", "S/ 18.00 x 1", "S/ 18.00"}, {"Inka Kola", "S/ 8.00 x 2", "S/ 16.00"}};
        if ("TRX-2025-002".equals(numTrx))
            return new String[][]{{"Chicha morada", "S/ 4.00 x 2", "S/ 8.00"}};
        return new String[][]{{"Pisco sour", "S/ 14.00 x 1", "S/ 14.00"}, {"Ceviche", "S/ 4.00 x 1", "S/ 4.00"}};
    }

    // ─── HELPERS DE UI ───────────────────────────────────────────────────────

    private JPanel crearFilaProducto(String nombre, String detalle, String total) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel izq = new JPanel(new BorderLayout());
        izq.setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblDetalle = new JLabel(detalle);
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetalle.setForeground(new Color(130, 130, 130));

        izq.add(lblNombre,  BorderLayout.NORTH);
        izq.add(lblDetalle, BorderLayout.SOUTH);

        JLabel lblTotal = new JLabel(total);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(AZUL);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(izq,      BorderLayout.WEST);
        fila.add(lblTotal, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearFilaResumen(String etiqueta, String valor, boolean negrita) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, 12));
        lblEtq.setForeground(new Color(100, 100, 100));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, 12));
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(lblEtq, BorderLayout.WEST);
        fila.add(lblVal, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearFilaMontoPagado(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEtq.setForeground(new Color(80, 80, 80));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(AZUL);
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(lblEtq, BorderLayout.WEST);
        fila.add(lblVal, BorderLayout.EAST);
        return fila;
    }

    private JButton crearBotonBase(String texto, int ancho) {
        JButton boton = new JButton(texto);
        if (ancho > 0) boton.setPreferredSize(new Dimension(ancho, 40));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    // ─── LÓGICA ──────────────────────────────────────────────────────────────

    private void imprimirTransaccion(int fila) {
        mostrarToast("Enviando a impresora: " + modeloTabla.getValueAt(fila, COL_NUM));
    }

    private void aplicarFiltros() {
        if (sorter == null || txtBuscar == null) return;
        String texto = txtBuscar.getText().trim();
        RowFilter<DefaultTableModel, Object> filtro = texto.isEmpty()
                ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(texto),
                        COL_NUM, COL_FECHA, COL_CAJERO, COL_METODO, COL_COMPROBANTE);
        sorter.setRowFilter(filtro);
        actualizarFooter();
    }

    private void actualizarStats() {
        int totalFilas = modeloTabla.getRowCount();
        double sumaTotal = 0;

        for (int i = 0; i < totalFilas; i++) {
            String montoStr = String.valueOf(modeloTabla.getValueAt(i, COL_MONTO))
                    .replace("S/", "").replace("s/", "").trim();
            try { sumaTotal += Double.parseDouble(montoStr); }
            catch (NumberFormatException ignored) {}
        }

        // Bruto estimado como el 60% del total (demo)
        double bruto = sumaTotal * 0.6;

        if (lblTotalTrx    != null) lblTotalTrx.setText(String.valueOf(totalFilas));
        if (lblVentaTotales != null) lblVentaTotales.setText(String.format("S/ %.2f", sumaTotal));
        if (lblBruto        != null) lblBruto.setText(String.format("S/ %.2f", bruto));
    }

    private void actualizarFooter() {
        int visibles = tabla == null ? 0 : tabla.getRowCount();
        int total    = modeloTabla == null ? 0 : modeloTabla.getRowCount();
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

        JDialog toastDialog = new JDialog(this, false);
        toastDialog.setUndecorated(true);
        toastDialog.add(toast);
        toastDialog.pack();
        toastDialog.setLocation(getX() + getWidth() - toastDialog.getWidth() - 40, getY() + 70);
        toastDialog.setVisible(true);

        Timer t = new Timer(1800, e -> toastDialog.dispose());
        t.setRepeats(false);
        t.start();
    }

    // ─── RENDERERS ───────────────────────────────────────────────────────────

    private static class EstadoRenderer extends JPanel implements TableCellRenderer {
        public EstadoRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            String estado = String.valueOf(value);
            Color color = estado.equalsIgnoreCase("Completada") ? VERDE
                        : estado.equalsIgnoreCase("Pendiente")  ? NARANJA
                        : ROJO;
            add(new PillLabel(estado, color), new GridBagConstraints());
            return this;
        }
    }

    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        public AccionesRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 10, 7)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            JLabel btnVer = new JLabel("👁");
            btnVer.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnVer.setOpaque(true);
            btnVer.setBackground(AZUL_CLARO);
            btnVer.setBorder(new EmptyBorder(4, 8, 4, 8));

            JLabel btnImp = new JLabel("🖨");
            btnImp.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnImp.setOpaque(true);
            btnImp.setBackground(AZUL_CLARO);
            btnImp.setBorder(new EmptyBorder(4, 8, 4, 8));

            add(btnVer);
            add(btnImp);
            return this;
        }
    }

    /** Pill reutilizable idéntico al de ArticulosStockFrame */
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