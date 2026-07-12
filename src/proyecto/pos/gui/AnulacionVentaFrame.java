package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import static java.awt.Component.LEFT_ALIGNMENT;
import java.awt.event.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.model.EstadoPago;
import proyecto.pos.model.Venta;

/**
 * HU-06 — Anulación de Venta Liquidada
 * Criterio 1: Validación de estado de factura no anulada.
 * Criterio 2: Iniciar transacción de reversión de inventario.
 * Criterio 3: Actualización de estado de factura a "Anulada".
 * Criterio 4: Confirmación de registro de auditoría en el sistema.
 */
public class AnulacionVentaFrame extends JFrame {

    // ─── PALETA ───────────────────────────────────────────────────────────────
    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_HOVER  = new Color(18, 65, 128);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);
    private static final Color TEXTO       = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color VERDE       = new Color(40, 167, 69);
    private static final Color VERDE_BG    = new Color(225, 245, 238);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);
    private static final Color ROJO        = new Color(220, 53, 69);
    private static final Color ROJO_BG     = new Color(255, 235, 238);
    private static final Color AMARILLO_BG = new Color(255, 249, 219);
    private static final Color AMARILLO_TX = new Color(133, 100, 4);

    // ─── COMPONENTES ──────────────────────────────────────────────────────────
    private JTable          tablaVentas;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField      txtBuscar;
    private JTextArea       areaLog;
    private JButton         btnAnular;
    private JLabel          lblFooter;
    private JLabel          lblEstadoVenta;
    private JPanel          panelAuditoria;

    // Panel de detalle
    private JLabel lblDetalleId;
    private JLabel lblDetalleFecha;
    private JLabel lblDetalleCliente;
    private JLabel lblDetalleTotal;
    private JLabel lblDetalleEstado;
    private JLabel lblDetalleEmpleado;

    private Connection    conexion;
    private VentaDAOImpl  ventaDAO;
    private int           ventaSeleccionadaId = -1;
    private String        estadoSeleccionado  = "";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Columnas tabla
    private static final int COL_ID       = 0;
    private static final int COL_FECHA    = 1;
    private static final int COL_CLIENTE  = 2;
    private static final int COL_EMPLEADO = 3;
    private static final int COL_TOTAL    = 4;
    private static final int COL_ESTADO   = 5;

    public AnulacionVentaFrame() {
        FlatLightLaf.setup();
        conexion = new DatabaseConnection().conectar();
        ventaDAO = new VentaDAOImpl(conexion);
        configurarVentana();
        construirInterfaz();
        cargarVentas();
    }

    // Constructor con ventaId — viene desde HistorialTransaccionesFrame
    public AnulacionVentaFrame(int ventaId) {
        FlatLightLaf.setup();
        conexion = new DatabaseConnection().conectar();
        ventaDAO = new VentaDAOImpl(conexion);
        configurarVentana();
        construirInterfaz();
        cargarVentas();
        // Preseleccionar la venta automáticamente
        SwingUtilities.invokeLater(() -> preseleccionarVenta(ventaId));
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void configurarVentana() {
        setTitle("Anulación de Venta Liquidada");
        setSize(1280, 770);
        setMinimumSize(new Dimension(1100, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(new MenuSidebar(this, "Historial"), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(new EmptyBorder(24, 28, 20, 28));
        centro.add(crearHeader(),  BorderLayout.NORTH);
        centro.add(crearCuerpo(), BorderLayout.CENTER);

        root.add(centro, BorderLayout.CENTER);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Anulación de Venta Liquidada");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel sub = new JLabel("Validación de estado · Reversión de inventario · Auditoría");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(sub);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        der.setOpaque(false);
        JLabel lblU = new JLabel("<html><b>uwu fernandez</b><br><font color='gray'>Gerente</font></html>");
        lblU.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        der.add(lblU);

        p.add(titulos, BorderLayout.WEST);
        p.add(der,     BorderLayout.EAST);
        return p;
    }

    // ── CUERPO ────────────────────────────────────────────────────────────────
    private JPanel crearCuerpo() {
        JPanel p = new JPanel(new BorderLayout(14, 8));
        p.setOpaque(false);

        JPanel centro = new JPanel(new BorderLayout(14, 0));
        centro.setOpaque(false);
        centro.add(crearPanelIzquierdo(), BorderLayout.CENTER);
        centro.add(crearPanelDerecho(),   BorderLayout.EAST);

        p.add(centro,              BorderLayout.CENTER);
        p.add(crearPanelAuditoria(), BorderLayout.SOUTH);
        return p;
    }

    // ── PANEL AUDITORÍA (Criterio 4) ─────────────────────────────────────────
    private JPanel crearPanelAuditoria() {
        panelAuditoria = new JPanel(new BorderLayout());
        panelAuditoria.setBackground(Color.WHITE);
        panelAuditoria.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(14, 16, 14, 16)));
        panelAuditoria.setVisible(false);

        // Cabecera
        JPanel cab = new JPanel(new BorderLayout());
        cab.setOpaque(false);
        JLabel tit = new JLabel("Criterio 4 — Registro de auditoría confirmado en el sistema");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(TEXTO);
        JLabel badge = new JLabel("Confirmado");
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setForeground(new Color(76, 29, 149));
        badge.setBackground(new Color(245, 243, 255));
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(3, 10, 3, 10));
        cab.add(tit,   BorderLayout.WEST);
        cab.add(badge, BorderLayout.EAST);
        cab.setBorder(new EmptyBorder(0, 0, 10, 0));
        panelAuditoria.add(cab, BorderLayout.NORTH);

        // Tabla de datos
        String[] cols = {"Campo", "Valor registrado"};
        DefaultTableModel modeloAud = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaAud = new JTable(modeloAud);
        tablaAud.setRowHeight(28);
        tablaAud.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaAud.setShowVerticalLines(false);
        tablaAud.setGridColor(new Color(235, 238, 244));
        tablaAud.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tablaAud.getColumnModel().getColumn(0).setPreferredWidth(160);
        tablaAud.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(TEXTO_SUAVE);
                return l;
            }
        });

        // Guardar referencia al modelo para llenarlo luego
        panelAuditoria.putClientProperty("modelo", modeloAud);

        JScrollPane scroll = new JScrollPane(tablaAud);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 8));
        scroll.setPreferredSize(new Dimension(0, 160));
        panelAuditoria.add(scroll, BorderLayout.CENTER);

        // Banner éxito
        JPanel banner = new JPanel(new BorderLayout(8, 0));
        banner.setBackground(VERDE_BG);
        banner.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(new Color(134, 239, 172), 8),
                new EmptyBorder(8, 12, 8, 12)));
        JLabel icoOk = new JLabel("✔");
        icoOk.setFont(new Font("Segoe UI", Font.BOLD, 16));
        icoOk.setForeground(VERDE_TEXT);
        JLabel msgOk = new JLabel("<html><b>Auditoría confirmada en Historial de Ventas</b> — El registro de esta anulación queda registrado y visible en el Historial de Ventas del sistema.</html>");
        msgOk.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        msgOk.setForeground(VERDE_TEXT);
        banner.add(icoOk, BorderLayout.WEST);
        banner.add(msgOk, BorderLayout.CENTER);
        banner.setBorder(new EmptyBorder(8, 0, 0, 0));
        panelAuditoria.add(banner, BorderLayout.SOUTH);

        return panelAuditoria;
    }

    // ── PANEL IZQUIERDO ───────────────────────────────────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)));

        // Barra superior
        JPanel barraTop = new JPanel(new BorderLayout(10, 0));
        barraTop.setOpaque(false);
        barraTop.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblTit = new JLabel("Ventas registradas en el sistema");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(TEXTO);

        // Buscador
        JPanel panelBuscar = new JPanel(new BorderLayout(6, 0));
        panelBuscar.setBackground(Color.WHITE);
        panelBuscar.setPreferredSize(new Dimension(260, 32));
        panelBuscar.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 8, 0, 8)));
        JLabel icoSearch = new JLabel("🔍");
        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por ID, cliente o estado...");
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ filtrarTabla(); }
        });
        panelBuscar.add(icoSearch, BorderLayout.WEST);
        panelBuscar.add(txtBuscar, BorderLayout.CENTER);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setOpaque(false);
        controles.add(panelBuscar);

        barraTop.add(lblTit,    BorderLayout.WEST);
        barraTop.add(controles, BorderLayout.EAST);

        card.add(barraTop, BorderLayout.NORTH);
        card.add(crearTablaScroll(), BorderLayout.CENTER);

        lblFooter = new JLabel("Cargando...");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(TEXTO_SUAVE);
        lblFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(lblFooter, BorderLayout.SOUTH);

        return card;
    }

    private JScrollPane crearTablaScroll() {
        String[] cols = {"ID Venta", "Fecha", "Cliente", "Empleado", "Total (S/.)", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setRowHeight(38);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.setShowVerticalLines(false);
        tablaVentas.setGridColor(new Color(235, 238, 244));
        tablaVentas.setSelectionBackground(AZUL_CLARO);
        tablaVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tablaVentas.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));

        tablaVentas.getColumnModel().getColumn(COL_ID)      .setPreferredWidth(70);
        tablaVentas.getColumnModel().getColumn(COL_FECHA)   .setPreferredWidth(130);
        tablaVentas.getColumnModel().getColumn(COL_CLIENTE) .setPreferredWidth(160);
        tablaVentas.getColumnModel().getColumn(COL_EMPLEADO).setPreferredWidth(150);
        tablaVentas.getColumnModel().getColumn(COL_TOTAL)   .setPreferredWidth(100);
        tablaVentas.getColumnModel().getColumn(COL_ESTADO)  .setPreferredWidth(100);

        // Renderer de estado con colores
        tablaVentas.getColumnModel().getColumn(COL_ESTADO).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                String estado = val == null ? "" : val.toString();
                switch (estado) {
                    case "PAGADO"   -> { lbl.setBackground(VERDE_BG);    lbl.setForeground(VERDE_TEXT); lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    case "ANULADO"  -> { lbl.setBackground(ROJO_BG);     lbl.setForeground(ROJO);       lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    case "PENDIENTE"-> { lbl.setBackground(AMARILLO_BG); lbl.setForeground(AMARILLO_TX);lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    default         -> { lbl.setBackground(sel ? AZUL_CLARO : Color.WHITE); lbl.setForeground(TEXTO); }
                }
                if (sel && !estado.isEmpty()) lbl.setBorder(BorderFactory.createLineBorder(AZUL, 1));
                else lbl.setBorder(null);
                return lbl;
            }
        });

        // Renderer total alineado a la derecha
        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaVentas.getColumnModel().getColumn(COL_TOTAL).setCellRenderer(derecha);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaVentas.getColumnModel().getColumn(COL_ID).setCellRenderer(centro);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaVentas.setRowSorter(sorter);

        // Al seleccionar fila
        tablaVentas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSeleccionarFila();
        });

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── PANEL DERECHO ─────────────────────────────────────────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(310, 0));

        // Componente de log interno (sin panel visible), usado por el proceso de anulación
        areaLog = new JTextArea();

        panel.add(crearCardDetalle());

        return panel;
    }

    private JPanel crearCardDetalle() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(20, 20, 20, 20)));
        card.setMaximumSize(new Dimension(310, 999));

        JLabel tit = new JLabel("Detalle de venta seleccionada");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(TEXTO);
        tit.setAlignmentX(LEFT_ALIGNMENT);
        card.add(tit);
        card.add(Box.createVerticalStrut(14));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(14));

        lblDetalleId       = crearFilaDetalle(card, "ID Venta:",  "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleFecha    = crearFilaDetalle(card, "Fecha:",      "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleCliente  = crearFilaDetalle(card, "Cliente:",    "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleEmpleado = crearFilaDetalle(card, "Empleado:",   "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleTotal    = crearFilaDetalle(card, "Total:",      "—");

        card.add(Box.createVerticalStrut(16));

        // Badge de estado
        lblEstadoVenta = new JLabel("Sin selección");
        lblEstadoVenta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstadoVenta.setForeground(TEXTO_SUAVE);
        lblEstadoVenta.setBackground(new Color(240, 242, 245));
        lblEstadoVenta.setOpaque(true);
        lblEstadoVenta.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstadoVenta.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblEstadoVenta.setAlignmentX(LEFT_ALIGNMENT);
        lblEstadoVenta.setMaximumSize(new Dimension(999, 34));
        card.add(lblEstadoVenta);

        card.add(Box.createVerticalStrut(22));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(18));

        // Botón anular
        btnAnular = new JButton("⛔ Anular Venta Seleccionada");
        btnAnular.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAnular.setForeground(Color.WHITE);
        btnAnular.setBackground(ROJO);
        btnAnular.setBorder(new EmptyBorder(12, 14, 12, 14));
        btnAnular.setFocusPainted(false);
        btnAnular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAnular.setEnabled(false);
        btnAnular.setMaximumSize(new Dimension(999, 46));
        btnAnular.setAlignmentX(LEFT_ALIGNMENT);
        btnAnular.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btnAnular.isEnabled()) btnAnular.setBackground(new Color(185, 28, 28)); }
            public void mouseExited (MouseEvent e) { if (btnAnular.isEnabled()) btnAnular.setBackground(ROJO); }
        });
        btnAnular.addActionListener(e -> ejecutarAnulacion());
        card.add(btnAnular);

        JLabel nota = new JLabel("<html><center>Solo pueden anularse ventas<br>con estado <b>PAGADO</b></center></html>");
        nota.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        nota.setForeground(TEXTO_SUAVE);
        nota.setHorizontalAlignment(SwingConstants.CENTER);
        nota.setAlignmentX(LEFT_ALIGNMENT);
        nota.setBorder(new EmptyBorder(10, 0, 0, 0));
        nota.setMaximumSize(new Dimension(999, 40));
        card.add(nota);

        return card;
    }

    private JLabel crearFilaDetalle(JPanel card, String etiqueta, String valorInicial) {
        JPanel fila = new JPanel(new BorderLayout(6, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(999, 22));
        fila.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel lEtiq = new JLabel(etiqueta);
        lEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lEtiq.setForeground(TEXTO_SUAVE);
        lEtiq.setPreferredSize(new Dimension(72, 18));

        JLabel lVal = new JLabel(valorInicial);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lVal.setForeground(TEXTO);

        fila.add(lEtiq, BorderLayout.WEST);
        fila.add(lVal,  BorderLayout.CENTER);
        card.add(fila);
        return lVal;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(999, 1));
        sep.setForeground(BORDE);
        return sep;
    }

    // ── CARGA DE DATOS ────────────────────────────────────────────────────────
    private void cargarVentas() {
        modeloTabla.setRowCount(0);
        try {
            List<Venta> ventas = ventaDAO.listar();
            for (Venta v : ventas) {
                String fecha    = v.getFecha() != null ? SDF.format(v.getFecha()) : "—";
                String cliente  = v.getCliente()  != null ? v.getCliente().getNombre()  : "—";
                String empleado = v.getEmpleado() != null ? v.getEmpleado().getNombre() : "—";
                String estado   = v.getEstadoPago() != null ? v.getEstadoPago().name() : "—";
                modeloTabla.addRow(new Object[]{
                    v.getVentaId(),
                    fecha,
                    cliente,
                    empleado,
                    String.format("%.2f", v.getTotal()),
                    estado
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar ventas: " + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        actualizarFooter();
    }

    // ── SELECCIÓN DE FILA ─────────────────────────────────────────────────────
    private void onSeleccionarFila() {
        int fila = tablaVentas.getSelectedRow();
        if (fila < 0) {
            limpiarDetalle();
            return;
        }
        int filaModelo = tablaVentas.convertRowIndexToModel(fila);

        ventaSeleccionadaId = Integer.parseInt(modeloTabla.getValueAt(filaModelo, COL_ID).toString());
        estadoSeleccionado  = modeloTabla.getValueAt(filaModelo, COL_ESTADO).toString();

        lblDetalleId      .setText("#" + ventaSeleccionadaId);
        lblDetalleFecha   .setText(modeloTabla.getValueAt(filaModelo, COL_FECHA).toString());
        lblDetalleCliente .setText(modeloTabla.getValueAt(filaModelo, COL_CLIENTE).toString());
        lblDetalleEmpleado.setText(modeloTabla.getValueAt(filaModelo, COL_EMPLEADO).toString());
        lblDetalleTotal   .setText("S/. " + modeloTabla.getValueAt(filaModelo, COL_TOTAL));

        // Estado visual
        switch (estadoSeleccionado) {
            case "PAGADO" -> {
                lblEstadoVenta.setText("✔ PAGADO — Apta para anulación");
                lblEstadoVenta.setBackground(VERDE_BG);
                lblEstadoVenta.setForeground(VERDE_TEXT);
                btnAnular.setEnabled(true);
            }
            case "ANULADO" -> {
                lblEstadoVenta.setText("✖ ANULADA — Ya fue anulada anteriormente");
                lblEstadoVenta.setBackground(ROJO_BG);
                lblEstadoVenta.setForeground(ROJO);
                btnAnular.setEnabled(false);
            }
            default -> {
                lblEstadoVenta.setText("⚠ " + estadoSeleccionado + " — No anulable");
                lblEstadoVenta.setBackground(AMARILLO_BG);
                lblEstadoVenta.setForeground(AMARILLO_TX);
                btnAnular.setEnabled(false);
            }
        }
        areaLog.setText("Venta #" + ventaSeleccionadaId + " seleccionada.\nEstado: " + estadoSeleccionado + "\n");
    }

    private void limpiarDetalle() {
        ventaSeleccionadaId = -1;
        estadoSeleccionado  = "";
        lblDetalleId.setText("—");
        lblDetalleFecha.setText("—");
        lblDetalleCliente.setText("—");
        lblDetalleEmpleado.setText("—");
        lblDetalleTotal.setText("—");
        lblEstadoVenta.setText("Sin selección");
        lblEstadoVenta.setBackground(new Color(240, 242, 245));
        lblEstadoVenta.setForeground(TEXTO_SUAVE);
        btnAnular.setEnabled(false);
    }

    // ── FILTRO ────────────────────────────────────────────────────────────────
    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" +
                    java.util.regex.Pattern.quote(texto)));
        }
        actualizarFooter();
    }

    private void actualizarFooter() {
        int total    = modeloTabla.getRowCount();
        int visibles = tablaVentas.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " de " + total + " ventas");
    }

    // ── EJECUCIÓN ANULACIÓN (HU-06) ──────────────────────────────────────────
    private void ejecutarAnulacion() {
        if (ventaSeleccionadaId <= 0) return;

        // Confirmación con resumen
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>¿Confirmar anulación de Venta #" + ventaSeleccionadaId + "?</b><br><br>"
                + "<small>Se revertirá el inventario y se registrará la auditoría correspondiente.<br><br>"
                + "<font color='red'>Esta acción no se puede deshacer.</font></small></html>",
                "Confirmar Anulación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        btnAnular.setEnabled(false);
        btnAnular.setText("⏳ Procesando...");
        areaLog.setText("──────────────────────────────────────\n");
        areaLog.append("ANULACIÓN  — Venta #" + ventaSeleccionadaId + "\n");
        areaLog.append("──────────────────────────────────────\n");

        int idVenta = ventaSeleccionadaId;

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                // empleadoId = 1 (gerente por defecto; en producción usar sesión activa)
                return ventaDAO.anularVenta(idVenta, 1);
            }

            @Override
            protected void done() {
                try {
                    List<String> log = get();
                    StringBuilder sb = new StringBuilder();
                    boolean hayCommit   = false;
                    boolean hayRollback = false;
                    boolean hayError    = false;

                    for (String linea : log) {
                        String[] partes = linea.split("\\|", 2);
                        String tipo = partes[0];
                        String msg  = partes.length > 1 ? partes[1] : linea;
                        switch (tipo) {
                            case "COMMIT"   -> { sb.append("✔ ").append(msg).append("\n"); hayCommit = true; }
                            case "ROLLBACK" -> { sb.append("↩ ").append(msg).append("\n"); hayRollback = true; }
                            case "ERROR"    -> { sb.append("✖ ").append(msg).append("\n"); hayError = true; }
                            case "OK"       -> sb.append("✓ ").append(msg).append("\n");
                            default         -> sb.append("  ").append(msg).append("\n");
                        }
                    }
                    sb.append("──────────────────────────────────────\n");
                    areaLog.append(sb.toString());

                    if (hayCommit) {
                        // Llenar panel de auditoría (Criterio 4)
                        DefaultTableModel mAud = (DefaultTableModel) panelAuditoria.getClientProperty("modelo");
                        if (mAud != null) {
                            mAud.setRowCount(0);
                            for (String linea : log) {
                                if (linea.startsWith("AUDIT|")) {
                                    String[] kv = linea.substring(6).split("=", 2);
                                    if (kv.length == 2) {
                                        String campo = switch (kv[0]) {
                                            case "ID_EVENTO"       -> "ID Evento";
                                            case "VENTA_ID"        -> "Factura anulada";
                                            case "FECHA"           -> "Fecha/hora anulación";
                                            case "EMPLEADO_ID"     -> "Empleado responsable";
                                            case "MONTO"           -> "Monto reversado (S/.)";
                                            case "INSUMOS"         -> "Insumos reintegrados";
                                            case "ESTADO_ANTERIOR" -> "Estado anterior → nuevo";
                                            default                -> kv[0];
                                        };
                                        String valor = kv[1];
                                        if ("VENTA_ID".equals(kv[0]))        valor = "Venta #" + valor;
                                        if ("MONTO".equals(kv[0]))           valor = "-S/. " + valor;
                                        if ("INSUMOS".equals(kv[0]))         valor = valor + " insumo(s) al inventario";
                                        if ("ESTADO_ANTERIOR".equals(kv[0])) valor = valor + " → ANULADO";
                                        mAud.addRow(new Object[]{campo, valor});
                                    }
                                }
                            }
                        }
                        panelAuditoria.setVisible(true);
                        cargarVentas(); // recargar tabla
                        mostrarResultado(true,
                                "Venta #" + idVenta + " anulada correctamente.\n"
                                + "Inventario revertido y auditoría registrada.");
                    } else if (hayError) {
                        mostrarResultado(false,
                                "No se pudo anular la venta.\nRevise el log para más detalles.");
                    }

                } catch (Exception ex) {
                    areaLog.append("✖ Error inesperado: " + ex.getMessage() + "\n");
                } finally {
                    btnAnular.setText("⛔ Anular Venta Seleccionada");
                    // El botón se reactiva solo si la venta ya está PAGADA (la tabla se recargó)
                }
            }
        };
        worker.execute();
    }

    private void mostrarResultado(boolean exito, String mensaje) {
        JPanel msgPanel = new JPanel(new BorderLayout(10, 0));
        msgPanel.setBackground(exito ? VERDE_BG : ROJO_BG);
        msgPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel icono = new JLabel(exito ? "✅" : "❌");
        icono.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel msg = new JLabel("<html><b>" + (exito ? "Anulación completada" : "Anulación rechazada")
                + "</b><br><font style='font-size:11px;'>" + mensaje.replace("\n", "<br>") + "</font></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(exito ? VERDE_TEXT : ROJO);

        msgPanel.add(icono, BorderLayout.WEST);
        msgPanel.add(msg,   BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, msgPanel,
                exito ? "Anulación exitosa" : "Anulación rechazada",
                exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    // ── PRESELECCIONAR VENTA (viene de HistorialTransaccionesFrame) ─────────
    public void preseleccionarVenta(int ventaId) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object val = modeloTabla.getValueAt(i, COL_ID);
            if (val != null && Integer.parseInt(val.toString()) == ventaId) {
                // Seleccionar la fila en la tabla
                int filaVista = tablaVentas.convertRowIndexToView(i);
                tablaVentas.setRowSelectionInterval(filaVista, filaVista);
                tablaVentas.scrollRectToVisible(tablaVentas.getCellRect(filaVista, 0, true));
                onSeleccionarFila(); // activar el panel de detalle
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AnulacionVentaFrame().setVisible(true));
    }
}