package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

public class HistorialTransaccionesFrame extends JFrame {

    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);
    private static final Color VERDE_BG    = new Color(225, 245, 238);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboPeriodo;
    private JLabel lblTotalTrx, lblVentaTotales, lblBruto, lblFooter, lblHora;
    private javax.swing.Timer relojTimer;

    public HistorialTransaccionesFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        iniciarReloj();
    }

    private void configurarVentana() {
        setTitle("Historial de Transacciones");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (relojTimer != null && relojTimer.isRunning()) relojTimer.stop();
                dispose();
                System.exit(0);
            }
        });
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        root.add(new MenuSidebar(this, "Historial"), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearContenido() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(24, 26, 24, 26));
        contenedor.add(crearHeader(), BorderLayout.NORTH);
        contenedor.add(crearPanelPrincipal(), BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel(new BorderLayout());
        titulos.setBackground(FONDO);

        JLabel titulo = new JLabel("Historial de transacciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitulo = new JLabel("Gestionar y revisar todas las transacciones realizadas");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);

        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.CENTER);

        JPanel derecho = new JPanel(new GridBagLayout());
        derecho.setBackground(FONDO);

        lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHora.setOpaque(true);
        lblHora.setBackground(Color.WHITE);
        lblHora.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JPanel perfilPanel = new JPanel(new BorderLayout(6, 0));
        perfilPanel.setBackground(Color.WHITE);
        perfilPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(4, 8, 4, 12)
        ));

        JLabel circulo = new JLabel("UF");
        circulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        circulo.setOpaque(true);
        circulo.setBackground(AZUL);
        circulo.setForeground(Color.WHITE);
        circulo.setPreferredSize(new Dimension(30, 30));
        circulo.setHorizontalAlignment(SwingConstants.CENTER);
        circulo.setBorder(BorderFactory.createLineBorder(AZUL, 4));

        JLabel nombrePerfil = new JLabel("<html><b>uwu fernandez</b><br><font color='gray'>Cajero</font></html>");
        nombrePerfil.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        perfilPanel.add(circulo, BorderLayout.WEST);
        perfilPanel.add(nombrePerfil, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.gridx = 0; derecho.add(lblHora, gbc);
        gbc.gridx = 1; derecho.add(perfilPanel, gbc);

        header.add(titulos, BorderLayout.WEST);
        header.add(derecho, BorderLayout.EAST);
        return header;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(18, 18, 18, 18)
        ));
        panel.add(crearStatsYFiltros(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearStatsYFiltros() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel stats = new JPanel(new GridBagLayout());
        stats.setBackground(Color.WHITE);
        stats.setBorder(new EmptyBorder(0, 0, 16, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0; gbc.insets = new Insets(0, 0, 0, 14);
        JPanel cardTrx = crearStatCard("Total Transacciones", "3", new Color(238, 237, 254), new Color(100, 90, 210));
        lblTotalTrx = obtenerLabelValor(cardTrx);
        stats.add(cardTrx, gbc);

        gbc.gridx = 1;
        JPanel cardVenta = crearStatCard("Ventas Totales", "S/. 31.00", new Color(230, 241, 251), new Color(26, 83, 160));
        lblVentaTotales = obtenerLabelValor(cardVenta);
        stats.add(cardVenta, gbc);

        gbc.gridx = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JPanel cardBruto = crearStatCard("Bruto", "S/. 14.00", new Color(234, 243, 222), new Color(50, 140, 80));
        lblBruto = obtenerLabelValor(cardBruto);
        stats.add(cardBruto, gbc);

        wrapper.add(stats, BorderLayout.NORTH);

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(new EmptyBorder(0, 0, 14, 0));

        cboPeriodo = new JComboBox<>(new String[]{"  Filtrar periodo", "Hoy", "Esta semana", "Este mes"});
        cboPeriodo.setPreferredSize(new Dimension(190, 36));
        cboPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboPeriodo.addActionListener(e -> aplicarFiltros());

        JPanel buscarWrap = new JPanel(new BorderLayout(4, 0));
        buscarWrap.setBackground(Color.WHITE);
        buscarWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(2, 8, 2, 8)
        ));
        buscarWrap.setPreferredSize(new Dimension(320, 36));

        JLabel lupaLabel = new JLabel("🔍");
        lupaLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createEmptyBorder());
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por nombre o codigo");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { aplicarFiltros(); }
        });

        buscarWrap.add(lupaLabel, BorderLayout.WEST);
        buscarWrap.add(txtBuscar, BorderLayout.CENTER);

        filtros.add(cboPeriodo, BorderLayout.WEST);
        filtros.add(buscarWrap, BorderLayout.EAST);
        wrapper.add(filtros, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel crearStatCard(String etiqueta, String valor, Color colorFondo, Color colorBorde) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel iconLabel = new JLabel();
        iconLabel.setOpaque(true);
        iconLabel.setBackground(colorFondo);
        iconLabel.setPreferredSize(new Dimension(46, 46));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createLineBorder(colorFondo, 10));

        JPanel texto = new JPanel(new GridBagLayout());
        texto.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEtiqueta.setForeground(new Color(140, 140, 140));
        texto.add(lblEtiqueta, gbc);

        gbc.gridy = 1;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(new Color(25, 25, 25));
        texto.add(lblValor, gbc);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(texto, BorderLayout.CENTER);
        return card;
    }

    private JLabel obtenerLabelValor(JPanel card) {
        JPanel texto = (JPanel) card.getComponent(1);
        return (JLabel) texto.getComponent(1);
    }

    private JScrollPane crearTabla() {
        String[] columnas = { "No. Transaccion", "Fecha / hora", "Cajero", "Total item", "Monto total", "Metodo de pago", "Estado", "Acciones" };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(44);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(250, 250, 250));
        tabla.getTableHeader().setForeground(new Color(130, 130, 130));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(242, 242, 242));
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(145);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(120);

        tabla.getColumnModel().getColumn(6).setCellRenderer(new EstadoRenderer());
        tabla.getColumnModel().getColumn(7).setCellRenderer(new AccionesRenderer());

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centro);
        tabla.getColumnModel().getColumn(4).setCellRenderer(centro);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());
                if (fila < 0 || col < 0 || col != 7) return;

                int filaModelo = tabla.convertRowIndexToModel(fila);
                if (filaModelo < 0 || filaModelo >= modeloTabla.getRowCount()) return;

                int xRel  = e.getX() - tabla.getCellRect(fila, col, true).x;
                int ancho = tabla.getColumnModel().getColumn(col).getWidth();
                if (xRel < ancho / 2) {
                    abrirDetalleTransaccion(filaModelo);
                } else {
                    imprimirTransaccion(filaModelo);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));
        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(130, 130, 130));
        footer.add(lblFooter, BorderLayout.WEST);
        return footer;
    }

    private void cargarDatosDemo() {
        agregarFila("TRX-2025-001", "22/12/2025 12:00", "Uwu fernandez", 2, "S/. 5.00",  "Tarjeta", "Completada");
        agregarFila("TRX-2025-002", "22/12/2025 14:00", "Uwu fernandez", 1, "S/. 8.00",  "Tarjeta", "Completada");
        agregarFila("TRX-2025-003", "22/12/2025 14:55", "Uwu fernandez", 2, "S/. 18.00", "Efectivo", "Completada");
        actualizarFooter();
    }

    private void agregarFila(String num, String fecha, String cajero, int items, String monto, String metodo, String estado) {
        if (num    == null || num.trim().isEmpty())    num    = "S/N";
        if (fecha  == null || fecha.trim().isEmpty())  fecha  = "-";
        if (cajero == null || cajero.trim().isEmpty()) cajero = "Desconocido";
        if (monto  == null || monto.trim().isEmpty())  monto  = "S/. 0.00";
        if (metodo == null || metodo.trim().isEmpty()) metodo = "-";
        if (estado == null || estado.trim().isEmpty()) estado = "Pendiente";
        modeloTabla.addRow(new Object[]{num, fecha, cajero, items, monto, metodo, estado, ""});
    }

    private void abrirDetalleTransaccion(int filaModelo) {
        String numTrx = obtenerCeldaSegura(filaModelo, 0, "SIN-NUMERO");
        String fecha  = obtenerCeldaSegura(filaModelo, 1, "Sin fecha");
        String cajero = obtenerCeldaSegura(filaModelo, 2, "Desconocido");
        String estado = obtenerCeldaSegura(filaModelo, 6, "Pendiente");

        JDialog dialog = new JDialog(this, "Detalles de la transaccion", true);
        dialog.setSize(390, 580);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Color.WHITE);
        encabezado.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Detalles de la transaccion");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel btnCerrarX = new JLabel("✕");
        btnCerrarX.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCerrarX.setForeground(new Color(100, 100, 100));
        btnCerrarX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarX.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
        });

        encabezado.add(lblTitulo, BorderLayout.WEST);
        encabezado.add(btnCerrarX, BorderLayout.EAST);

        JPanel cuerpo = new JPanel(new GridBagLayout());
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(new EmptyBorder(0, 20, 16, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        int fila = 0;

        JPanel bloqueInfo = new JPanel(new GridBagLayout());
        bloqueInfo.setBackground(AZUL_CLARO);
        bloqueInfo.setBorder(new EmptyBorder(14, 14, 14, 14));

        GridBagConstraints gi = new GridBagConstraints();
        gi.fill = GridBagConstraints.HORIZONTAL;
        gi.weightx = 1;
        gi.insets = new Insets(2, 0, 2, 0);

        gi.gridx = 0; gi.gridy = 0; gi.anchor = GridBagConstraints.WEST;
        addLabel(bloqueInfo, "No. Transaccion", new Font("Segoe UI", Font.PLAIN, 10), new Color(120, 120, 120), gi);

        gi.gridx = 1;
        addLabel(bloqueInfo, "Fecha / hora", new Font("Segoe UI", Font.PLAIN, 10), new Color(120, 120, 120), gi);

        gi.gridx = 0; gi.gridy = 1;
        addLabel(bloqueInfo, numTrx, new Font("Segoe UI", Font.BOLD, 13), new Color(25, 25, 25), gi);

        gi.gridx = 1;
        addLabel(bloqueInfo, fecha, new Font("Segoe UI", Font.BOLD, 13), new Color(25, 25, 25), gi);

        gi.gridx = 0; gi.gridy = 2; gi.gridwidth = 2;
        gi.insets = new Insets(8, 0, 8, 0);
        bloqueInfo.add(new JSeparator(), gi);

        gi.insets = new Insets(2, 0, 2, 0);
        gi.gridwidth = 1;

        gi.gridx = 0; gi.gridy = 3;
        addLabel(bloqueInfo, "Cajero", new Font("Segoe UI", Font.PLAIN, 10), new Color(120, 120, 120), gi);

        gi.gridx = 1;
        addLabel(bloqueInfo, "Estado", new Font("Segoe UI", Font.PLAIN, 10), new Color(120, 120, 120), gi);

        gi.gridx = 0; gi.gridy = 4;
        addLabel(bloqueInfo, cajero, new Font("Segoe UI", Font.BOLD, 13), new Color(25, 25, 25), gi);

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

        g.gridy = fila++;
        g.insets = new Insets(14, 0, 8, 0);
        JLabel lblProductos = new JLabel("Productos de la transaccion");
        lblProductos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cuerpo.add(lblProductos, g);
        g.insets = new Insets(0, 0, 0, 0);

        String[][] productos = obtenerProductosPorTransaccion(numTrx);
        for (String[] prod : productos) {
            g.gridy = fila++;
            cuerpo.add(crearFilaProducto(prod[0], prod[1], prod[2]), g);
        }

        g.gridy = fila++;
        g.insets = new Insets(10, 0, 10, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        double[] montos = calcularMontos(productos);
        String subtotalStr = String.format("S/. %.2f", montos[0]);
        String igvStr      = String.format("S/. %.2f", montos[1]);
        String totalStr    = String.format("S/. %.2f", montos[2]);

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("Sub total", subtotalStr, false), g);

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("IGV 18%", igvStr, false), g);

        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++;
        cuerpo.add(crearFilaMontoPagado("Monto total", totalStr), g);

        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++;
        g.insets = new Insets(6, 0, 4, 0);
        JLabel lblPago = new JLabel("Detalle de pago");
        lblPago.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cuerpo.add(lblPago, g);
        g.insets = new Insets(3, 0, 3, 0);

        double efectivo = Math.ceil(montos[2] / 10.0) * 10;
        double vuelto   = efectivo - montos[2];

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("Monto recibido", String.format("S/. %.2f", efectivo), false), g);

        g.gridy = fila++;
        JPanel filaVuelto = crearFilaResumen("Vuelto", String.format("S/. %.2f", vuelto), false);
        ((JLabel) filaVuelto.getComponent(0)).setForeground(AZUL);
        ((JLabel) filaVuelto.getComponent(1)).setForeground(AZUL);
        cuerpo.add(filaVuelto, g);

        JPanel botones = new JPanel(new GridBagLayout());
        botones.setBackground(Color.WHITE);
        botones.setBorder(new EmptyBorder(14, 20, 18, 20));

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.HORIZONTAL;
        gb.weightx = 1;
        gb.insets = new Insets(0, 0, 0, 10);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setForeground(new Color(60, 60, 60));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCerrar.addActionListener(e -> dialog.dispose());
        gb.gridx = 0;
        botones.add(btnCerrar, gb);

        gb.insets = new Insets(0, 0, 0, 0);
        JButton btnReimprimir = new JButton("  Reimprimir");
        btnReimprimir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReimprimir.setBackground(AZUL);
        btnReimprimir.setForeground(Color.WHITE);
        btnReimprimir.setFocusPainted(false);
        btnReimprimir.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(scrollCuerpo, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        dialog.setContentPane(contenido);
        dialog.setVisible(true);
    }

    private void addLabel(JPanel panel, String texto, Font font, Color color, GridBagConstraints gbc) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(font);
        lbl.setForeground(color);
        panel.add(lbl, gbc);
    }

    private double[] calcularMontos(String[][] productos) {
        double subtotal = 0;
        for (String[] p : productos) {
            try {
                String raw = p[2].replace("S/.", "").replace("s/", "").replace(",", ".").trim();
                subtotal += Double.parseDouble(raw);
            } catch (NumberFormatException ignored) { }
        }
        double igv   = subtotal * 0.18;
        double total = subtotal + igv;
        return new double[]{subtotal, igv, total};
    }

    private String[][] obtenerProductosPorTransaccion(String numTrx) {
        if (numTrx == null) return new String[][]{{"Sin productos", "-", "S/. 0.00"}};
        switch (numTrx) {
            case "TRX-2025-001":
                return new String[][]{
                    {"Lomo saltado",  "S/. 3.00 x 1", "S/. 3.00"},
                    {"Inka Kola",     "S/. 1.00 x 2", "S/. 2.00"}
                };
            case "TRX-2025-002":
                return new String[][]{
                    {"Chicha morada", "S/. 4.00 x 2", "S/. 8.00"}
                };
            case "TRX-2025-003":
                return new String[][]{
                    {"Pisco sour",    "S/. 12.00 x 1", "S/. 12.00"},
                    {"Ceviche",       "S/. 6.00 x 1",  "S/. 6.00"}
                };
            default:
                return new String[][]{{"Sin detalle disponible", "-", "S/. 0.00"}};
        }
    }

    private String obtenerCeldaSegura(int fila, int columna, String valorPorDefecto) {
        try {
            Object val = modeloTabla.getValueAt(fila, columna);
            if (val == null || val.toString().trim().isEmpty()) return valorPorDefecto;
            return val.toString().trim();
        } catch (Exception ex) {
            return valorPorDefecto;
        }
    }

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

        izq.add(lblNombre, BorderLayout.NORTH);
        izq.add(lblDetalle, BorderLayout.SOUTH);

        JLabel lblTotal = new JLabel(total);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(AZUL);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(izq, BorderLayout.WEST);
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

    private void imprimirTransaccion(int fila) {
        if (fila < 0 || fila >= modeloTabla.getRowCount()) {
            JOptionPane.showMessageDialog(this, "No se pudo identificar la transacción.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String num = obtenerCeldaSegura(fila, 0, "SIN-NUMERO");
        JOptionPane.showMessageDialog(this, "Enviando a impresora: " + num, "Imprimir", JOptionPane.INFORMATION_MESSAGE);
    }

    private void aplicarFiltros() {
        if (txtBuscar == null || sorter == null) return;
        String texto = txtBuscar.getText().trim();
        RowFilter<DefaultTableModel, Object> filtro = null;
        if (!texto.isEmpty()) {
            try {
                filtro = RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0, 1, 2, 5);
            } catch (Exception ex) {
                filtro = null;
            }
        }
        sorter.setRowFilter(filtro);
        actualizarFooter();
    }

    private void actualizarFooter() {
        if (tabla == null || modeloTabla == null || lblFooter == null) return;
        int vis   = tabla.getRowCount();
        int total = modeloTabla.getRowCount();
        lblFooter.setText("Mostrando 1 - " + vis + " de " + total + " datos");
    }

    private void iniciarReloj() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        relojTimer = new javax.swing.Timer(1000, e -> lblHora.setText("<html><b>Hora</b>&nbsp;&nbsp;" + sdf.format(new Date()) + "</html>"));
        relojTimer.start();
        lblHora.setText("<html><b>Hora</b>&nbsp;&nbsp;" + sdf.format(new Date()) + "</html>");
    }

    private static class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if (!isSelected) {
                label.setBackground(new Color(225, 245, 238));
                label.setForeground(new Color(15, 110, 86));
            }
            return label;
        }
    }

    private static class AccionesRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(isSelected ? new Color(232, 241, 255) : Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 4, 0, 4);

            JLabel btnVer = new JLabel("👁");
            btnVer.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            btnVer.setOpaque(true);
            btnVer.setBackground(new Color(232, 241, 255));
            btnVer.setBorder(new EmptyBorder(5, 9, 5, 9));
            btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel btnImp = new JLabel("🖨");
            btnImp.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            btnImp.setOpaque(true);
            btnImp.setBackground(new Color(232, 241, 255));
            btnImp.setBorder(new EmptyBorder(5, 9, 5, 9));
            btnImp.setCursor(new Cursor(Cursor.HAND_CURSOR));

            gbc.gridx = 0; panel.add(btnVer, gbc);
            gbc.gridx = 1; panel.add(btnImp, gbc);
            return panel;
        }
        
    }
}