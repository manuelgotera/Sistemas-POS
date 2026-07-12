package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import proyecto.pos.model.Cliente;

/**
 * HU-08 — Vinculación de Venta con Perfil de Cliente (CRM)
 * Como el gerente, necesito que cada transacción procesada en el módulo de
 * ventas se vincule al perfil del cliente en el CRM, con la finalidad de
 * evaluar las tendencias de consumo y autorizar descuentos masivos automáticos.
 *
 * Criterio 1: Actualización de perfil tras procesar una venta.
 * Criterio 2: Asignación automática de categoría por volumen de compra.
 * Criterio 3: Restricción de permisos para datos sensibles.
 * Criterio 4: Aplicación automática de descuentos según historial.
 */
public class PerfilClienteCRMFrame extends JFrame {

    // ─── PALETA ───────────────────────────────────────────────────────────────
    private static final Color AZUL        = new Color(26, 83, 160);
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
    private static final Color MORADO_BG   = new Color(243, 232, 255);
    private static final Color MORADO_TX   = new Color(107, 33, 168);

    // ─── COMPONENTES ──────────────────────────────────────────────────────────
    private JTable            tablaClientes;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField        txtBuscar;
    private JComboBox<String> cbRol;
    private JButton           btnVincular;
    private JLabel            lblFooter;

    // Panel de detalle
    private JLabel lblDetalleId;
    private JLabel lblDetalleTipo;
    private JLabel lblDetalleNombre;
    private JLabel lblDetalleCategoria;
    private JLabel lblDetalleDni;
    private JLabel lblDetalleTelefono;
    private JLabel lblDetalleEmail;
    private JLabel lblDetalleDireccion;
    private JLabel lblDetallePuntos;
    private JLabel lblDetalleGasto;
    private JLabel lblDetalleCompras;
    private JLabel lblDetalleUltima;

    private Connection      conexion;
    private ClienteDAOImpl  clienteDAO;
    private int             clienteSeleccionadoId = -1;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Columnas tabla
    private static final int COL_ID        = 0;
    private static final int COL_NOMBRE    = 1;
    private static final int COL_APELLIDO  = 2;
    private static final int COL_TIPO      = 3;
    private static final int COL_CATEGORIA = 4;
    private static final int COL_PUNTOS    = 5;

    public PerfilClienteCRMFrame() {
        FlatLightLaf.setup();
        conexion   = new DatabaseConnection().conectar();
        clienteDAO = new ClienteDAOImpl(conexion);
        configurarVentana();
        construirInterfaz();
        cargarClientes();
    }

    // Constructor con clienteId — viene desde ClientesFrame (botón "Ver Perfil CRM")
    public PerfilClienteCRMFrame(int clienteId) {
        FlatLightLaf.setup();
        conexion   = new DatabaseConnection().conectar();
        clienteDAO = new ClienteDAOImpl(conexion);
        configurarVentana();
        construirInterfaz();
        cargarClientes();
        // Preseleccionar el cliente automáticamente
        SwingUtilities.invokeLater(() -> preseleccionarCliente(clienteId));
    }

    // ── PRESELECCIONAR CLIENTE (viene de ClientesFrame) ──────────────────────
    public void preseleccionarCliente(int clienteId) {
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            Object val = modeloTabla.getValueAt(i, COL_ID);
            if (val != null && Integer.parseInt(val.toString()) == clienteId) {
                int filaVista = tablaClientes.convertRowIndexToView(i);
                tablaClientes.setRowSelectionInterval(filaVista, filaVista);
                tablaClientes.scrollRectToVisible(tablaClientes.getCellRect(filaVista, 0, true));
                onSeleccionarFila();
                break;
            }
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void configurarVentana() {
        setTitle("Vinculación de Venta con Perfil de Cliente (CRM) — HU-08");
        setSize(1300, 780);
        setMinimumSize(new Dimension(1120, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(new MenuSidebar(this, "Clientes"), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(new EmptyBorder(24, 28, 20, 28));
        centro.add(crearHeader(), BorderLayout.NORTH);
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

        JLabel titulo = new JLabel("Vinculación de Venta con Perfil de Cliente (CRM)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel sub = new JLabel("HU-08 · Actualización de perfil · Categoría por volumen · Permisos · Descuentos automáticos");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(sub);

        p.add(titulos, BorderLayout.WEST);
        return p;
    }

    // ── CUERPO ────────────────────────────────────────────────────────────────
    private JPanel crearCuerpo() {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setOpaque(false);
        p.add(crearPanelIzquierdo(), BorderLayout.CENTER);
        p.add(crearPanelDerecho(),   BorderLayout.EAST);
        return p;
    }

    // ── VENTANA FLOTANTE DE RESULTADO (Criterios 1-4 tras vincular) ─────────
    private void mostrarResultadoFlotante(String nombreCliente, List<String[]> filas) {
        JDialog dlg = new JDialog(this, "Resultado de la Vinculación CRM", true);
        dlg.setSize(600, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        // ── Cabecera con botón de cierre "✕" ──────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AZUL);
        header.setBorder(new EmptyBorder(16, 20, 16, 12));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel hTit = new JLabel("✔ Resultado de la Vinculación CRM");
        hTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hTit.setForeground(Color.WHITE);
        JLabel hSub = new JLabel("Criterios 1-4 · Cliente: " + nombreCliente);
        hSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hSub.setForeground(new Color(255, 255, 255, 210));
        titulos.add(hTit);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(hSub);

        JButton btnCerrar = new JButton("✕");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setToolTipText("Cerrar");
        btnCerrar.addActionListener(e -> dlg.dispose());

        header.add(titulos,   BorderLayout.WEST);
        header.add(btnCerrar, BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // ── Cuerpo: tabla Campo / Valor, amplia y legible ─────────────────
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 22, 12, 22));

        String[] cols = {"Campo", "Valor registrado"};
        DefaultTableModel modeloRes = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (String[] fila : filas) modeloRes.addRow(fila);

        JTable tablaRes = new JTable(modeloRes);
        tablaRes.setRowHeight(36);
        tablaRes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaRes.setShowVerticalLines(false);
        tablaRes.setShowHorizontalLines(true);
        tablaRes.setGridColor(new Color(235, 238, 244));
        tablaRes.setIntercellSpacing(new Dimension(0, 0));
        tablaRes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaRes.getTableHeader().setPreferredSize(new Dimension(0, 34));
        tablaRes.getColumnModel().getColumn(0).setPreferredWidth(230);
        tablaRes.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setForeground(TEXTO_SUAVE);
                l.setBorder(new EmptyBorder(0, 10, 0, 6));
                return l;
            }
        });
        tablaRes.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setFont(l.getFont().deriveFont(Font.BOLD));
                l.setForeground(TEXTO);
                l.setBorder(new EmptyBorder(0, 6, 0, 10));
                return l;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaRes);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.setPreferredSize(new Dimension(0, 340));
        body.add(scroll, BorderLayout.CENTER);
        root.add(body, BorderLayout.CENTER);

        // ── Pie: banner de éxito + botón Cerrar ───────────────────────────
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(0, 22, 20, 22));

        JPanel banner = new JPanel(new BorderLayout(8, 0));
        banner.setBackground(VERDE_BG);
        banner.setAlignmentX(LEFT_ALIGNMENT);
        banner.setMaximumSize(new Dimension(999, 60));
        banner.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(new Color(134, 239, 172), 8),
                new EmptyBorder(10, 12, 10, 12)));
        JLabel icoOk = new JLabel("✔");
        icoOk.setFont(new Font("Segoe UI", Font.BOLD, 16));
        icoOk.setForeground(VERDE_TEXT);
        JLabel msgOk = new JLabel("<html><b>Perfil sincronizado en el CRM</b> — El historial, la categoría y el descuento quedaron actualizados y visibles en su ficha.</html>");
        msgOk.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        msgOk.setForeground(VERDE_TEXT);
        banner.add(icoOk, BorderLayout.WEST);
        banner.add(msgOk, BorderLayout.CENTER);
        footer.add(banner);
        footer.add(Box.createVerticalStrut(14));

        JButton btnCerrarAbajo = new JButton("Cerrar y volver a la lista");
        btnCerrarAbajo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCerrarAbajo.setForeground(Color.WHITE);
        btnCerrarAbajo.setBackground(AZUL);
        btnCerrarAbajo.setBorder(new EmptyBorder(11, 14, 11, 14));
        btnCerrarAbajo.setFocusPainted(false);
        btnCerrarAbajo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarAbajo.setAlignmentX(LEFT_ALIGNMENT);
        btnCerrarAbajo.setMaximumSize(new Dimension(999, 44));
        btnCerrarAbajo.addActionListener(e -> dlg.dispose());
        footer.add(btnCerrarAbajo);

        root.add(footer, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    // ── PANEL IZQUIERDO ───────────────────────────────────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)));

        JPanel barraTop = new JPanel(new BorderLayout(10, 0));
        barraTop.setOpaque(false);
        barraTop.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel lblTit = new JLabel("Clientes registrados en el CRM");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(TEXTO);

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
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por ID, nombre o categoría...");
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
        String[] cols = {"ID", "Nombre", "Apellido", "Tipo", "Categoría CRM", "Puntos"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setRowHeight(38);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.setShowVerticalLines(false);
        tablaClientes.setGridColor(new Color(235, 238, 244));
        tablaClientes.setSelectionBackground(AZUL_CLARO);
        tablaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader th = tablaClientes.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));

        tablaClientes.getColumnModel().getColumn(COL_ID)       .setPreferredWidth(50);
        tablaClientes.getColumnModel().getColumn(COL_NOMBRE)   .setPreferredWidth(150);
        tablaClientes.getColumnModel().getColumn(COL_APELLIDO) .setPreferredWidth(150);
        tablaClientes.getColumnModel().getColumn(COL_TIPO)     .setPreferredWidth(80);
        tablaClientes.getColumnModel().getColumn(COL_CATEGORIA).setPreferredWidth(110);
        tablaClientes.getColumnModel().getColumn(COL_PUNTOS)   .setPreferredWidth(70);

        tablaClientes.getColumnModel().getColumn(COL_CATEGORIA).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setOpaque(true);
                String cat = val == null ? "" : val.toString();
                switch (cat.toUpperCase()) {
                    case "VIP"     -> { lbl.setBackground(MORADO_BG);   lbl.setForeground(MORADO_TX);  lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    case "PREMIUM" -> { lbl.setBackground(VERDE_BG);    lbl.setForeground(VERDE_TEXT);  lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    case "REGULAR" -> { lbl.setBackground(AMARILLO_BG); lbl.setForeground(AMARILLO_TX); lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                    default        -> { lbl.setBackground(sel ? AZUL_CLARO : Color.WHITE); lbl.setForeground(TEXTO); }
                }
                if (sel && !cat.isEmpty()) lbl.setBorder(BorderFactory.createLineBorder(AZUL, 1));
                else lbl.setBorder(null);
                return lbl;
            }
        });

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaClientes.getColumnModel().getColumn(COL_ID).setCellRenderer(centro);
        tablaClientes.getColumnModel().getColumn(COL_TIPO).setCellRenderer(centro);
        tablaClientes.getColumnModel().getColumn(COL_PUNTOS).setCellRenderer(centro);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaClientes.setRowSorter(sorter);

        tablaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onSeleccionarFila();
        });

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── PANEL DERECHO ─────────────────────────────────────────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(340, 0));

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
        card.setMaximumSize(new Dimension(340, 999));

        JLabel tit = new JLabel("Perfil CRM del cliente seleccionado");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        tit.setForeground(TEXTO);
        tit.setAlignmentX(LEFT_ALIGNMENT);
        card.add(tit);
        card.add(Box.createVerticalStrut(14));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(14));

        // ── Criterio 3: simulador de rol (controla el enmascarado) ───────────
        JPanel filaRol = new JPanel(new BorderLayout(6, 0));
        filaRol.setOpaque(false);
        filaRol.setAlignmentX(LEFT_ALIGNMENT);
        filaRol.setMaximumSize(new Dimension(999, 28));
        JLabel lblRol = new JLabel("Rol de sesión:");
        lblRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblRol.setForeground(TEXTO_SUAVE);
        cbRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "CAJERO", "MOZO", "CHEF"});
        cbRol.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        cbRol.addActionListener(e -> onSeleccionarFila());
        filaRol.add(lblRol, BorderLayout.WEST);
        filaRol.add(cbRol,  BorderLayout.CENTER);
        card.add(filaRol);
        card.add(Box.createVerticalStrut(14));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(14));

        lblDetalleId        = crearFilaDetalle(card, "ID:",        "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleTipo      = crearFilaDetalle(card, "Tipo:",      "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleNombre    = crearFilaDetalle(card, "Nombre:",    "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleDni       = crearFilaDetalle(card, "DNI:",       "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleTelefono  = crearFilaDetalle(card, "Teléfono:",  "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleEmail     = crearFilaDetalle(card, "Email:",     "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleDireccion = crearFilaDetalle(card, "Dirección:", "—");
        card.add(Box.createVerticalStrut(6));
        lblDetallePuntos    = crearFilaDetalle(card, "Puntos:",    "—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleCompras   = crearFilaDetalle(card, "N° compras:","—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleGasto     = crearFilaDetalle(card, "Gasto total:","—");
        card.add(Box.createVerticalStrut(6));
        lblDetalleUltima    = crearFilaDetalle(card, "Última compra:", "—");

        card.add(Box.createVerticalStrut(16));

        lblDetalleCategoria = new JLabel("Sin selección");
        lblDetalleCategoria.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblDetalleCategoria.setForeground(TEXTO_SUAVE);
        lblDetalleCategoria.setBackground(new Color(240, 242, 245));
        lblDetalleCategoria.setOpaque(true);
        lblDetalleCategoria.setHorizontalAlignment(SwingConstants.CENTER);
        lblDetalleCategoria.setBorder(new EmptyBorder(8, 12, 8, 12));
        lblDetalleCategoria.setAlignmentX(LEFT_ALIGNMENT);
        lblDetalleCategoria.setMaximumSize(new Dimension(999, 34));
        card.add(lblDetalleCategoria);

        card.add(Box.createVerticalStrut(22));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(18));

        btnVincular = new JButton("🔗 Vincular Venta y Actualizar Perfil");
        btnVincular.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnVincular.setForeground(Color.WHITE);
        btnVincular.setBackground(AZUL);
        btnVincular.setBorder(new EmptyBorder(12, 14, 12, 14));
        btnVincular.setFocusPainted(false);
        btnVincular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVincular.setEnabled(false);
        btnVincular.setMaximumSize(new Dimension(999, 46));
        btnVincular.setAlignmentX(LEFT_ALIGNMENT);
        btnVincular.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btnVincular.isEnabled()) btnVincular.setBackground(new Color(18, 65, 128)); }
            public void mouseExited (MouseEvent e) { if (btnVincular.isEnabled()) btnVincular.setBackground(AZUL); }
        });
        btnVincular.addActionListener(e -> ejecutarVinculacion());
        card.add(btnVincular);

        JLabel nota = new JLabel("<html><center>Recalcula categoría, descuento y<br>permisos según el historial de compras</center></html>");
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
        lEtiq.setPreferredSize(new Dimension(90, 18));

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
    // La "Categoría CRM" se calcula al vuelo (SUM de ventas PAGADAS por cliente).
    // No se guarda en ninguna columna: "tipo_cliente" (Natural/Empresa) queda intacto.
    private void cargarClientes() {
        modeloTabla.setRowCount(0);

        String sql = "SELECT c.cliente_id, c.nombre, c.apellido, c.tipo_cliente, "
                + "c.puntos_fidelidad, NVL(SUM(v.total), 0) AS gasto_total "
                + "FROM clientes c "
                + "LEFT JOIN ventas_cabecera v "
                + "  ON v.cliente_id = c.cliente_id AND UPPER(TRIM(v.estado_pago)) = 'PAGADO' "
                + "GROUP BY c.cliente_id, c.nombre, c.apellido, c.tipo_cliente, c.puntos_fidelidad "
                + "ORDER BY c.cliente_id";

        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                double gastoTotal   = rs.getDouble("gasto_total");
                String categoriaCRM = calcularCategoria(gastoTotal);
                modeloTabla.addRow(new Object[]{
                    rs.getInt("cliente_id"),
                    rs.getString("nombre"),
                    rs.getString("apellido"),
                    rs.getString("tipo_cliente") != null ? rs.getString("tipo_cliente") : "—",
                    categoriaCRM,
                    rs.getInt("puntos_fidelidad")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al cargar clientes: " + ex.getMessage(),
                "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        actualizarFooter();
    }

    // ── Reglas de negocio compartidas (mismo criterio que ClienteDAOImpl) ────
    private String calcularCategoria(double gastoTotal) {
        if (gastoTotal >= 300) return "VIP";
        if (gastoTotal >= 100) return "Premium";
        return "Regular";
    }

    private double calcularDescuento(String categoriaCRM) {
        return switch (categoriaCRM) {
            case "VIP"     -> 10.0;
            case "Premium" -> 5.0;
            default        -> 0.0;
        };
    }

    // ── SELECCIÓN DE FILA ─────────────────────────────────────────────────────
    private void onSeleccionarFila() {
        int fila = tablaClientes.getSelectedRow();
        if (fila < 0) {
            limpiarDetalle();
            return;
        }
        int filaModelo = tablaClientes.convertRowIndexToModel(fila);
        clienteSeleccionadoId = Integer.parseInt(modeloTabla.getValueAt(filaModelo, COL_ID).toString());

        Cliente c = clienteDAO.obtenerPorId(clienteSeleccionadoId);
        if (c == null) { limpiarDetalle(); return; }

        boolean accesoTotal = "ADMINISTRADOR".equals(cbRol.getSelectedItem());

        lblDetalleId.setText("#" + c.getId());
        lblDetalleTipo.setText(nvl(c.getTipoCliente())); // NATURAL/EMPRESA — dato real, no sensible
        lblDetalleNombre.setText(c.getNombre() + " " + (c.getApellidos() != null ? c.getApellidos() : ""));
        lblDetalleDni.setText(accesoTotal ? nvl(c.getDni()) : enmascarar(c.getDni(), 2, 0));
        lblDetalleTelefono.setText(accesoTotal ? nvl(c.getTelefono()) : enmascarar(c.getTelefono(), 3, 2));
        lblDetalleEmail.setText(accesoTotal ? nvl(c.getEmail()) : enmascararEmail(c.getEmail()));
        lblDetalleDireccion.setText(accesoTotal ? nvl(c.getDireccion()) : "🔒 Oculta (rol restringido)");
        lblDetallePuntos.setText(String.valueOf(c.getPuntosFideldiad()));

        // Historial de compras en vivo (para previsualizar antes de vincular)
        double[] resumen = obtenerResumenCompras(clienteSeleccionadoId);
        lblDetalleCompras.setText(String.valueOf((int) resumen[0]));
        lblDetalleGasto.setText("S/. " + String.format("%.2f", resumen[1]));
        lblDetalleUltima.setText(resumen[2] > 0 ? SDF.format(new java.util.Date((long) resumen[2])) : "Sin registros");

        // Categoría CRM calculada al vuelo (no se guarda en BD)
        String categoriaCRM = calcularCategoria(resumen[1]);
        double descuento    = calcularDescuento(categoriaCRM);
        switch (categoriaCRM) {
            case "VIP" -> {
                lblDetalleCategoria.setText("★ VIP — " + (int) descuento + "% descuento automático");
                lblDetalleCategoria.setBackground(MORADO_BG);
                lblDetalleCategoria.setForeground(MORADO_TX);
            }
            case "Premium" -> {
                lblDetalleCategoria.setText("✔ PREMIUM — " + (int) descuento + "% descuento automático");
                lblDetalleCategoria.setBackground(VERDE_BG);
                lblDetalleCategoria.setForeground(VERDE_TEXT);
            }
            default -> {
                lblDetalleCategoria.setText("○ REGULAR — sin descuento automático");
                lblDetalleCategoria.setBackground(AMARILLO_BG);
                lblDetalleCategoria.setForeground(AMARILLO_TX);
            }
        }
        btnVincular.setEnabled(true);
    }

    private void limpiarDetalle() {
        clienteSeleccionadoId = -1;
        lblDetalleId.setText("—");
        lblDetalleTipo.setText("—");
        lblDetalleNombre.setText("—");
        lblDetalleDni.setText("—");
        lblDetalleTelefono.setText("—");
        lblDetalleEmail.setText("—");
        lblDetalleDireccion.setText("—");
        lblDetallePuntos.setText("—");
        lblDetalleCompras.setText("—");
        lblDetalleGasto.setText("—");
        lblDetalleUltima.setText("—");
        lblDetalleCategoria.setText("Sin selección");
        lblDetalleCategoria.setBackground(new Color(240, 242, 245));
        lblDetalleCategoria.setForeground(TEXTO_SUAVE);
        btnVincular.setEnabled(false);
    }

    // ── ENMASCARADO (Criterio 3) ─────────────────────────────────────────────
    private String nvl(String v) { return (v == null || v.isBlank()) ? "—" : v; }

    private String enmascarar(String valor, int visibleInicio, int visibleFin) {
        if (valor == null || valor.isBlank()) return "—";
        if (valor.length() <= visibleInicio + visibleFin) return "*".repeat(valor.length());
        String inicio = valor.substring(0, visibleInicio);
        String fin    = visibleFin > 0 ? valor.substring(valor.length() - visibleFin) : "";
        return inicio + "*".repeat(valor.length() - visibleInicio - visibleFin) + fin;
    }

    private String enmascararEmail(String email) {
        if (email == null || !email.contains("@")) return "—";
        String[] partes = email.split("@", 2);
        String usuario = partes[0].length() > 1 ? partes[0].charAt(0) + "***" : "***";
        return usuario + "@" + partes[1];
    }

    // ── RESUMEN DE COMPRAS (consulta rápida para previsualizar) ─────────────
    private double[] obtenerResumenCompras(int clienteId) {
        String sql = "SELECT COUNT(*) AS num_compras, NVL(SUM(total),0) AS gasto_total, "
                + "MAX(fecha_hora) AS ultima_compra FROM ventas_cabecera "
                + "WHERE cliente_id = ? AND UPPER(TRIM(estado_pago)) = 'PAGADO'";
        double numCompras = 0, gastoTotal = 0, ultima = 0;
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, clienteId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    numCompras = rs.getInt("num_compras");
                    gastoTotal = rs.getDouble("gasto_total");
                    java.sql.Timestamp t = rs.getTimestamp("ultima_compra");
                    ultima = t != null ? t.getTime() : 0;
                }
            }
        } catch (SQLException ex) {
            // Si falla la previsualización no se interrumpe la selección del cliente,
            // pero se deja registro en consola para poder depurar la causa real.
            System.err.println("⚠ Error al obtener resumen de compras del cliente " + clienteId + ": " + ex.getMessage());
        }
        return new double[]{numCompras, gastoTotal, ultima};
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
        int visibles = tablaClientes.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " de " + total + " clientes");
    }

    // ── EJECUCIÓN VINCULACIÓN (HU-08) ────────────────────────────────────────
    private void ejecutarVinculacion() {
        if (clienteSeleccionadoId <= 0) return;

        String rolSeleccionado = (String) cbRol.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>¿Vincular la venta al perfil del cliente #" + clienteSeleccionadoId + "?</b><br><br>"
                + "<small>Se actualizará su historial, categoría y descuento autorizado "
                + "según el rol de sesión seleccionado (" + rolSeleccionado + ").</small></html>",
                "Confirmar Vinculación — HU-08",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        btnVincular.setEnabled(false);
        btnVincular.setText("⏳ Procesando...");

        int idCliente = clienteSeleccionadoId;

        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                return clienteDAO.vincularVentaConPerfil(idCliente, rolSeleccionado);
            }

            @Override
            protected void done() {
                try {
                    List<String> log = get();
                    boolean hayCommit = false;
                    boolean hayError  = false;

                    for (String linea : log) {
                        String[] partes = linea.split("\\|", 2);
                        if ("COMMIT".equals(partes[0])) hayCommit = true;
                        if ("ERROR".equals(partes[0]))  hayError  = true;
                    }

                    if (hayCommit) {
                        List<String[]> filas = new ArrayList<>();
                        String nombreCliente = "#" + idCliente;
                        for (String linea : log) {
                            if (linea.startsWith("AUDIT|")) {
                                String[] kv = linea.substring(6).split("=", 2);
                                if (kv.length == 2) {
                                    if ("CLIENTE".equals(kv[0])) nombreCliente = kv[1];
                                    String campo = switch (kv[0]) {
                                        case "CLIENTE"         -> "Cliente";
                                        case "TIPO_CLIENTE"    -> "Tipo de cliente (Natural/Empresa)";
                                        case "NUM_COMPRAS"     -> "N° de compras registradas";
                                        case "GASTO_TOTAL"     -> "Gasto total histórico (S/.)";
                                        case "ULTIMA_COMPRA"   -> "Última compra";
                                        case "CATEGORIA"       -> "Categoría CRM calculada";
                                        case "DESCUENTO"       -> "Descuento automático autorizado";
                                        case "ROL"             -> "Rol de sesión evaluado";
                                        case "ACCESO_SENSIBLE" -> "Acceso a datos sensibles";
                                        default                 -> kv[0];
                                    };
                                    String valor = kv[1];
                                    if ("DESCUENTO".equals(kv[0])) valor = valor + "%";
                                    filas.add(new String[]{campo, valor});
                                }
                            }
                        }
                        cargarClientes();
                        preseleccionarCliente(idCliente);
                        mostrarResultadoFlotante(nombreCliente, filas);
                    } else if (hayError) {
                        StringBuilder err = new StringBuilder();
                        for (String linea : log) {
                            if (linea.startsWith("ERROR|")) err.append(linea.substring(6)).append("\n");
                        }
                        mostrarResultado(false, err.length() > 0 ? err.toString()
                                : "No se pudo vincular el perfil del cliente.");
                    }

                } catch (Exception ex) {
                    mostrarResultado(false, "Error inesperado: " + ex.getMessage());
                } finally {
                    btnVincular.setText("🔗 Vincular Venta y Actualizar Perfil");
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

        JLabel msg = new JLabel("<html><b>" + (exito ? "Vinculación completada" : "Vinculación rechazada")
                + "</b><br><font style='font-size:11px;'>" + mensaje.replace("\n", "<br>") + "</font></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(exito ? VERDE_TEXT : ROJO);

        msgPanel.add(icono, BorderLayout.WEST);
        msgPanel.add(msg,   BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, msgPanel,
                exito ? "Vinculación exitosa" : "Vinculación rechazada",
                exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PerfilClienteCRMFrame().setVisible(true));
    }
}
