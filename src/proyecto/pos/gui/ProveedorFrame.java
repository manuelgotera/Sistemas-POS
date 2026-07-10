package proyecto.pos.gui;

import proyecto.pos.model.Proveedor;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class ProveedorFrame extends JFrame {

    // ── Paleta ────────────────────────────────────────────────────────────────
    static final Color AZUL       = new Color(26, 83, 160);
    static final Color AZUL_HOV   = new Color(18, 65, 128);
    static final Color AZUL_CLAR  = new Color(232, 241, 255);
    static final Color PAGE_BG    = new Color(246, 248, 251);
    static final Color CARD_BG    = Color.WHITE;
    static final Color BORDER_CLR = new Color(225, 229, 236);
    static final Color TEXT_DARK  = new Color(30,  37,  48);
    static final Color TEXT_MUTED = new Color(105, 113, 128);

    // Pills
    static final Color P_VERDE   = new Color(40,  167,  69);
    static final Color P_NARANJA = new Color(255, 145,  77);
    static final Color P_ROJO    = new Color(220,  53,  69);
    static final Color P_AZUL    = new Color(83,  137, 174);
    static final Color P_GRIS    = new Color(150, 157, 168);

    // Alerta banner
    static final Color AM_FONDO = new Color(255, 249, 219);
    static final Color AM_BORDE = new Color(245, 213,  93);
    static final Color AM_TEXTO = new Color(128,  89,   0);

    // ── Índices de columna ────────────────────────────────────────────────────
    static final int COL_CODIGO   = 0;
    static final int COL_NOMBRE   = 1;
    static final int COL_RUCDNI   = 2;
    static final int COL_TELEFONO = 3;
    static final int COL_DIRECC   = 4;
    static final int COL_TIPO     = 5;
    static final int COL_REGION   = 6;
    static final int COL_CUMPL    = 7;
    static final int COL_STATUS   = 8;
    static final int COL_ACC      = 9;

    // ── Datos en memoria ──────────────────────────────────────────────────────
    private final List<Proveedor> proveedores = new ArrayList<>();
    private int nextId = 1;

    // ── Componentes ───────────────────────────────────────────────────────────
    private DefaultTableModel                 modelo;
    private JTable                            tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField                        txtBuscar;
    private JComboBox<String>                 cboFiltroTipo;
    private JLabel                            lblAlerta;
    private JLabel                            lblFooter;
    private JLabel                            lblTotal, lblActivos, lblInactivos;
    private Connection conexion;

    // ══════════════════════════════════════════════════════════════════════════
    public ProveedorFrame() {
        setTitle("Proveedores Regionales");
        setSize(1280, 740);
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.add(new MenuSidebar(this, "Proveedores", conexion), BorderLayout.WEST);

        root.add(buildContenido(), BorderLayout.CENTER);

        setContentPane(root);

        cargarDemo();
        actualizarAlerta();
        actualizarFooter();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CONTENIDO CENTRAL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildContenido() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(PAGE_BG);
        c.setBorder(new EmptyBorder(26, 28, 24, 28));
        c.add(buildHeader(),         BorderLayout.NORTH);
        c.add(buildPanelPrincipal(), BorderLayout.CENTER);
        return c;
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PAGE_BG);
        h.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Proveedores Regionales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXT_DARK);

        JLabel sub = new JLabel("Registrar y consultar proveedores que abastecen al restaurante");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXT_MUTED);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(sub);

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        chips.setOpaque(false);
        chips.add(buildChipHora());
        chips.add(buildChipUsuario());

        h.add(titulos, BorderLayout.WEST);
        h.add(chips,   BorderLayout.EAST);
        return h;
    }

    private JPanel buildChipHora() {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBackground(CARD_BG);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 14, 6, 14)));
        chip.setPreferredSize(new Dimension(110, 40));

        JLabel lTit  = new JLabel("Hora");
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lTit.setForeground(TEXT_DARK);

        JLabel lHora = new JLabel(hora());
        lHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lHora.setForeground(TEXT_MUTED);
        new Timer(1000, e -> lHora.setText(hora())).start();

        chip.add(lTit); chip.add(lHora);
        return chip;
    }

    private JPanel buildChipUsuario() {
        JPanel chip = new JPanel(new BorderLayout(8, 0));
        chip.setBackground(CARD_BG);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 10, 6, 12)));
        chip.setPreferredSize(new Dimension(155, 40));

        ImageIcon fotoIcon = MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 28, 28);
        JLabel foto;
        if (fotoIcon != null) {
            foto = new JLabel(fotoIcon);
        } else {
            foto = new JLabel("MG");
            foto.setOpaque(true);
            foto.setBackground(AZUL);
            foto.setForeground(Color.WHITE);
            foto.setFont(new Font("Segoe UI", Font.BOLD, 11));
            foto.setHorizontalAlignment(SwingConstants.CENTER);
            foto.setPreferredSize(new Dimension(28, 28));
        }

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel nombre = new JLabel("Manuel Gotera");
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nombre.setForeground(TEXT_DARK);

        JLabel rol = new JLabel("Cajero");
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(TEXT_MUTED);

        textos.add(nombre); textos.add(rol);
        chip.add(foto,    BorderLayout.WEST);
        chip.add(textos,  BorderLayout.CENTER);
        return chip;
    }

    private String hora() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PANEL PRINCIPAL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildPanelPrincipal() {
        JPanel p = new JPanel(new BorderLayout(0, 16));
        p.setBackground(PAGE_BG);
        p.add(buildStatsRow(),  BorderLayout.NORTH);
        p.add(buildTableCard(), BorderLayout.CENTER);
        return p;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        lblTotal     = statCard(row, "Total Proveedores", "0",
                "/img/carrito.png",       new Color(218, 212, 255), new Color(80, 55, 180));
        lblActivos   = statCard(row, "Activos",           "0",
                "/img/empleados.png",     new Color(178, 235, 208), new Color(18, 115, 65));
        lblInactivos = statCard(row, "Inactivos",         "0",
                "/img/configuracion.png", new Color(248, 195, 195), new Color(155, 28, 28));

        return row;
    }

    private JLabel statCard(JPanel parent, String titulo, String valor,
                             String imgPath, Color iconBg, Color numColor) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setBackground(iconBg);
        iconBox.setPreferredSize(new Dimension(54, 54));

        ImageIcon ico = MenuSidebar.redimensionarIcono(imgPath, 26, 26);
        if (ico != null) {
            iconBox.add(new JLabel(ico), new GridBagConstraints());
        }
        card.add(iconBox, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setBackground(CARD_BG);

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTit.setForeground(TEXT_MUTED);

        JLabel lVal = new JLabel(valor);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lVal.setForeground(numColor);

        texts.add(lTit); texts.add(Box.createVerticalStrut(4)); texts.add(lVal);
        card.add(texts, BorderLayout.CENTER);
        parent.add(card);
        return lVal;
    }

    // ── Tarjeta con tabla ─────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 16, 14, 16)));
        card.add(buildCardHeader(),  BorderLayout.NORTH);
        card.add(buildTablaScroll(), BorderLayout.CENTER);
        card.add(buildFooterRow(),   BorderLayout.SOUTH);
        return card;
    }

    // ── Encabezado de card ────────────────────────────────────────────────────
    private JPanel buildCardHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD_BG);
        outer.setBorder(new EmptyBorder(0, 0, 14, 0));

        // ── Botones superiores ────────────────────────────────────────────────
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);

        JButton bExp = btnSecundario("Exportar PDF",       160);
        JButton bAgr = btnPrimario  ("+ Añadir Proveedor", 200);

        bExp.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "La exportación a PDF todavía no está conectada.",
                "Exportar PDF", JOptionPane.INFORMATION_MESSAGE));
        bAgr.addActionListener(e -> abrirAgregar());

        btns.add(bExp);
        btns.add(bAgr);

        // ── Alerta banner ─────────────────────────────────────────────────────
        lblAlerta = new JLabel();
        lblAlerta.setOpaque(true);
        lblAlerta.setBackground(AM_FONDO);
        lblAlerta.setForeground(AM_TEXTO);
        lblAlerta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAlerta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AM_BORDE, 1, true),
                new EmptyBorder(9, 12, 9, 12)));
        lblAlerta.setVisible(false);

        // ── Filtro por tipo ───────────────────────────────────────────────────
        cboFiltroTipo = new JComboBox<>(new String[]{
            "Todos los tipos de insumo", "Carnes y Aves", "Frutas y Verduras",
            "Bebidas", "Lácteos", "Insumos secos"
        });
        cboFiltroTipo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboFiltroTipo.setBackground(CARD_BG);
        cboFiltroTipo.setPreferredSize(new Dimension(210, 36));
        cboFiltroTipo.addActionListener(e -> aplicarFiltros());

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(14, 0, 0, 0));
        filtros.add(cboFiltroTipo,   BorderLayout.WEST);
        filtros.add(buildBuscador(), BorderLayout.EAST);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.setOpaque(false);
        centro.setBorder(new EmptyBorder(14, 0, 0, 0));
        centro.add(lblAlerta, BorderLayout.NORTH);
        centro.add(filtros,   BorderLayout.CENTER);

        outer.add(btns,   BorderLayout.NORTH);
        outer.add(centro, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildBuscador() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(CARD_BG);
        p.setPreferredSize(new Dimension(310, 36));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXT_DARK);
        txtBuscar.setBackground(CARD_BG);
        txtBuscar.putClientProperty("JTextField.placeholderText",
                "Buscar por nombre, RUC/DNI o región…");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        JLabel ico = new JLabel("🔍");
        ico.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        p.add(ico, BorderLayout.WEST);
        p.add(txtBuscar, BorderLayout.CENTER);
        return p;
    }

    // ── Tabla ─────────────────────────────────────────────────────────────────
    private JScrollPane buildTablaScroll() {
        modelo = new DefaultTableModel(
            new String[]{
                "Código", "Nombre / Razón Social", "RUC / DNI",
                "Teléfono", "Dirección", "Tipo de Insumo",
                "Región", "Cumplimiento", "Status", "Acciones"
            }, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
            public Class<?> getColumnClass(int c) {
                return c == COL_CUMPL ? Integer.class : String.class;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(235, 238, 244));
        tabla.setSelectionBackground(AZUL_CLAR);
        tabla.setSelectionForeground(TEXT_DARK);
        tabla.setFillsViewportHeight(true);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(new Color(45, 52, 65));
        th.setBackground(CARD_BG);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 40));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        th.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        int[] anchos = {80, 185, 120, 100, 170, 130, 110, 110, 90, 100};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.getColumnModel().getColumn(COL_CODIGO)  .setCellRenderer(new TxtR(SwingConstants.LEFT,   false));
        tabla.getColumnModel().getColumn(COL_NOMBRE)  .setCellRenderer(new TxtR(SwingConstants.LEFT,   true));
        tabla.getColumnModel().getColumn(COL_RUCDNI)  .setCellRenderer(new TxtR(SwingConstants.LEFT,   false));
        tabla.getColumnModel().getColumn(COL_TELEFONO).setCellRenderer(new TxtR(SwingConstants.LEFT,   false));
        tabla.getColumnModel().getColumn(COL_DIRECC)  .setCellRenderer(new TxtR(SwingConstants.LEFT,   false));
        tabla.getColumnModel().getColumn(COL_TIPO)    .setCellRenderer(new TxtR(SwingConstants.LEFT,   false));
        tabla.getColumnModel().getColumn(COL_REGION)  .setCellRenderer(new TxtR(SwingConstants.CENTER, false));
        tabla.getColumnModel().getColumn(COL_CUMPL)   .setCellRenderer(new CumplR());
        tabla.getColumnModel().getColumn(COL_STATUS)  .setCellRenderer(new StatusR());
        tabla.getColumnModel().getColumn(COL_ACC)     .setCellRenderer(new AccR());

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fv = tabla.rowAtPoint(e.getPoint());
                int cv = tabla.columnAtPoint(e.getPoint());
                if (fv < 0 || cv < 0) return;
                int fm = tabla.convertRowIndexToModel(fv);
                int cm = tabla.convertColumnIndexToModel(cv);
                if (cm == COL_ACC) {
                    Rectangle celda = tabla.getCellRect(fv, cv, true);
                    if (e.getX() - celda.x < celda.width / 2) abrirEditar(fm);
                    else                                        eliminar(fm);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        return scroll;
    }

    private JPanel buildFooterRow() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(80, 88, 100));
        p.add(lblFooter, BorderLayout.WEST);
        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarDemo() {
        add(new Proveedor(nextId++, cod(), "Avícola San José",
                "20512345678", "944100001", "Carr. Laredo km 3, Trujillo",
                "Carnes y Aves", "La Libertad", 98, true));
        add(new Proveedor(nextId++, cod(), "Carnicería San Pedro",
                "20512345679", "944100002", "Mercado Mayorista, Chiclayo",
                "Carnes y Aves", "Lambayeque", 95, true));
        add(new Proveedor(nextId++, cod(), "Granja Orgánica Sol",
                "10456789012", "944100003", "Comunidad Otuzco, La Libertad",
                "Frutas y Verduras", "La Libertad", 99, true));
        add(new Proveedor(nextId++, cod(), "Distribuidora Lindley",
                "20100116335", "944100004", "Av. Industrial 456, Lima",
                "Bebidas", "Lima", 97, true));
        add(new Proveedor(nextId++, cod(), "Viñedos El Sol",
                "20512399001", "944100005", "Zona Industrial, Ica",
                "Bebidas", "Lima", 96, true));
        add(new Proveedor(nextId++, cod(), "Lácteos Norteños",
                "20405678901", "944100006", "Cajamarca Centro",
                "Lácteos", "Cajamarca", 82, false));
        add(new Proveedor(nextId++, cod(), "Mayorista Norte",
                "20301234567", "944100007", "Av. España 1240, Trujillo",
                "Insumos secos", "La Libertad", 91, true));
    }

    private void add(Proveedor prov) {
        proveedores.add(prov);
        modelo.addRow(toRow(prov));
        actualizarStats();
    }

    private Object[] toRow(Proveedor p) {
        return new Object[]{
            p.getCodigo(), p.getNombre(), p.getRucDni(),
            p.getTelefono(), p.getDireccion(), p.getTipoInsumo(),
            p.getRegion(), p.getCumplimiento(),
            p.isActivo() ? "activo" : "inactivo",
            "acc"
        };
    }

    private String cod() { return "PRV-" + String.format("%03d", nextId); }

    // ══════════════════════════════════════════════════════════════════════════
    //  CRUD
    // ══════════════════════════════════════════════════════════════════════════
    private void abrirAgregar() {
        ProveedorDialog dlg = new ProveedorDialog(this, null);
        dlg.setVisible(true);
        if (!dlg.isConfirmado()) return;

        Proveedor nuevo = dlg.getProveedor();

        if (rucExiste(nuevo.getRucDni(), -1)) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un proveedor con el RUC/DNI: " + nuevo.getRucDni(),
                    "RUC/DNI duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nombreExiste(nuevo.getNombre(), -1)) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un proveedor con el nombre: " + nuevo.getNombre(),
                    "Nombre duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        nuevo.setId(nextId);
        nuevo.setCodigo(cod());
        nextId++;

        add(nuevo);
        aplicarFiltros();
        actualizarAlerta();

        JOptionPane.showMessageDialog(this,
                "El proveedor \"" + nuevo.getNombre() + "\" fue registrado correctamente.\n"
                + "Código asignado: " + nuevo.getCodigo(),
                "Registro exitoso ✓", JOptionPane.INFORMATION_MESSAGE);
        toast("Proveedor registrado con éxito ✓");
    }

    private void abrirEditar(int fm) {
        Proveedor original = filaAProveedor(fm);
        ProveedorDialog dlg = new ProveedorDialog(this, original);
        dlg.setVisible(true);
        if (!dlg.isConfirmado()) return;

        Proveedor editado = dlg.getProveedor();

        if (!editado.getRucDni().equals(original.getRucDni())
                && rucExiste(editado.getRucDni(), original.getId())) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un proveedor con ese RUC/DNI.",
                    "RUC/DNI duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!editado.getNombre().equalsIgnoreCase(original.getNombre())
                && nombreExiste(editado.getNombre(), original.getId())) {
            JOptionPane.showMessageDialog(this,
                    "Ya existe un proveedor con ese nombre.",
                    "Nombre duplicado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        editado.setId(original.getId());
        editado.setCodigo(original.getCodigo());

        int idx = indexById(original.getId());
        if (idx >= 0) proveedores.set(idx, editado);

        modelo.setValueAt(editado.getNombre(),                        fm, COL_NOMBRE);
        modelo.setValueAt(editado.getRucDni(),                        fm, COL_RUCDNI);
        modelo.setValueAt(editado.getTelefono(),                      fm, COL_TELEFONO);
        modelo.setValueAt(editado.getDireccion(),                     fm, COL_DIRECC);
        modelo.setValueAt(editado.getTipoInsumo(),                    fm, COL_TIPO);
        modelo.setValueAt(editado.getRegion(),                        fm, COL_REGION);
        modelo.setValueAt(editado.getCumplimiento(),                  fm, COL_CUMPL);
        modelo.setValueAt(editado.isActivo() ? "activo" : "inactivo", fm, COL_STATUS);

        actualizarStats();
        aplicarFiltros();
        actualizarAlerta();

        JOptionPane.showMessageDialog(this,
                "Los datos de \"" + editado.getNombre() + "\" fueron actualizados correctamente.",
                "Actualización exitosa ✓", JOptionPane.INFORMATION_MESSAGE);
        toast("Datos actualizados con éxito ✓");
    }

    private void eliminar(int fm) {
        String nombre = (String) modelo.getValueAt(fm, COL_NOMBRE);
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar al proveedor \"" + nombre + "\"?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

        String codigo = (String) modelo.getValueAt(fm, COL_CODIGO);
        proveedores.removeIf(p -> p.getCodigo().equals(codigo));
        modelo.removeRow(fm);

        actualizarStats();
        aplicarFiltros();
        actualizarAlerta();
        toast("Proveedor eliminado correctamente");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FILTROS
    // ══════════════════════════════════════════════════════════════════════════
    private void aplicarFiltros() {
        if (sorter == null) return;
        String texto = txtBuscar     == null ? "" : txtBuscar.getText().trim();
        String tipo  = cboFiltroTipo == null ? "" : (String) cboFiltroTipo.getSelectedItem();

        RowFilter<DefaultTableModel, Object> fT = texto.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + Pattern.quote(texto),
                  COL_NOMBRE, COL_RUCDNI, COL_REGION, COL_TIPO);

        RowFilter<DefaultTableModel, Object> fC = "Todos los tipos de insumo".equals(tipo) ? null
                : RowFilter.regexFilter("(?i)^" + Pattern.quote(tipo) + "$", COL_TIPO);

        if      (fT != null && fC != null)
            sorter.setRowFilter(RowFilter.andFilter(Arrays.asList(fT, fC)));
        else if (fT != null) sorter.setRowFilter(fT);
        else if (fC != null) sorter.setRowFilter(fC);
        else                 sorter.setRowFilter(null);

        actualizarFooter();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS
    // ══════════════════════════════════════════════════════════════════════════
    private void actualizarStats() {
        int total = modelo.getRowCount();
        long act  = IntStream.range(0, total)
                .filter(i -> "activo".equals(modelo.getValueAt(i, COL_STATUS))).count();
        lblTotal    .setText(String.valueOf(total));
        lblActivos  .setText(String.valueOf(act));
        lblInactivos.setText(String.valueOf(total - act));
    }

    private void actualizarAlerta() {
        if (modelo == null || lblAlerta == null) return;
        int inact = 0, bajos = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            if ("inactivo".equals(modelo.getValueAt(i, COL_STATUS))) inact++;
            Object c = modelo.getValueAt(i, COL_CUMPL);
            if (c instanceof Integer && (Integer) c < 85) bajos++;
        }
        if (inact > 0 || bajos > 0) {
            lblAlerta.setText("  ⚠  Alertas: " + inact + " inactivos, "
                    + bajos + " con cumplimiento bajo (<85%)");
            lblAlerta.setVisible(true);
        } else {
            lblAlerta.setVisible(false);
        }
    }

    private void actualizarFooter() {
        if (tabla == null || lblFooter == null) return;
        lblFooter.setText("Mostrando " + tabla.getRowCount()
                + " de " + modelo.getRowCount() + " datos");
    }

    private Proveedor filaAProveedor(int fm) {
        String  codigo = (String)  modelo.getValueAt(fm, COL_CODIGO);
        String  nombre = (String)  modelo.getValueAt(fm, COL_NOMBRE);
        String  rucDni = (String)  modelo.getValueAt(fm, COL_RUCDNI);
        String  tel    = (String)  modelo.getValueAt(fm, COL_TELEFONO);
        String  dir    = (String)  modelo.getValueAt(fm, COL_DIRECC);
        String  tipo   = (String)  modelo.getValueAt(fm, COL_TIPO);
        String  region = (String)  modelo.getValueAt(fm, COL_REGION);
        int     cumpl  = (Integer) modelo.getValueAt(fm, COL_CUMPL);
        boolean activo = "activo".equals(modelo.getValueAt(fm, COL_STATUS));
        return new Proveedor(idPorCodigo(codigo), codigo, nombre, rucDni,
                tel, dir, tipo, region, cumpl, activo);
    }

    private int indexById(int id) {
        for (int i = 0; i < proveedores.size(); i++)
            if (proveedores.get(i).getId() == id) return i;
        return -1;
    }

    private int idPorCodigo(String codigo) {
        for (Proveedor p : proveedores)
            if (p.getCodigo().equals(codigo)) return p.getId();
        return 0;
    }

    private boolean rucExiste(String rucDni, int excluirId) {
        for (Proveedor p : proveedores)
            if (p.getRucDni().equalsIgnoreCase(rucDni) && p.getId() != excluirId) return true;
        return false;
    }

    private boolean nombreExiste(String nombre, int excluirId) {
        for (Proveedor p : proveedores)
            if (p.getNombre().equalsIgnoreCase(nombre) && p.getId() != excluirId) return true;
        return false;
    }

    private void toast(String msg) {
        JLabel lbl = new JLabel(msg);
        lbl.setOpaque(true);
        lbl.setBackground(new Color(235, 244, 255));
        lbl.setForeground(AZUL);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(174, 204, 252), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        JDialog d = new JDialog(this, false);
        d.setUndecorated(true);
        d.add(lbl); d.pack();
        d.setLocation(getX() + getWidth() - d.getWidth() - 40, getY() + 80);
        d.setVisible(true);
        Timer t = new Timer(2000, e -> d.dispose());
        t.setRepeats(false); t.start();
    }

    // ── Botones ───────────────────────────────────────────────────────────────
    private JButton btnPrimario(String txt, int ancho) {
        JButton b = btnBase(txt, ancho);
        b.setBackground(AZUL);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(AZUL, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_HOV); }
            public void mouseExited (MouseEvent e) { b.setBackground(AZUL); }
        });
        return b;
    }

    private JButton btnSecundario(String txt, int ancho) {
        JButton b = btnBase(txt, ancho);
        b.setBackground(CARD_BG);
        b.setForeground(AZUL);
        b.setBorder(BorderFactory.createLineBorder(AZUL, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(232, 241, 255)); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD_BG); }
        });
        return b;
    }

    private JButton btnBase(String txt, int ancho) {
        JButton b = new JButton(txt);
        b.setPreferredSize(new Dimension(ancho, 40));
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setOpaque(true);
        b.setContentAreaFilled(true);  // ← clave para que el color se vea en Windows L&F
        b.setFocusPainted(false);
        b.setBorderPainted(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RENDERERS
    // ══════════════════════════════════════════════════════════════════════════
    static class TxtR extends DefaultTableCellRenderer {
        private final boolean bold;
        TxtR(int align, boolean bold) { this.bold = bold; setHorizontalAlignment(align); }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            setBorder(new EmptyBorder(0, 8, 0, 8));
            setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 12));
            setForeground(TEXT_DARK);
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    static class CumplR extends JPanel implements TableCellRenderer {
        CumplR() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            int val = (v instanceof Integer) ? (Integer) v : 0;
            Color col = val >= 95 ? P_VERDE : val >= 85 ? P_NARANJA : P_ROJO;
            add(new Pill(val + "%", col), new GridBagConstraints());
            return this;
        }
    }

    static class StatusR extends JPanel implements TableCellRenderer {
        StatusR() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            String s   = String.valueOf(v);
            Color  col = "activo".equalsIgnoreCase(s) ? P_AZUL : P_GRIS;
            add(new Pill(s, col), new GridBagConstraints());
            return this;
        }
    }

    static class AccR extends JPanel implements TableCellRenderer {
        AccR() { setLayout(new FlowLayout(FlowLayout.CENTER, 10, 7)); setOpaque(true); }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            JLabel ed = new JLabel("✎");
            ed.setFont(new Font("Segoe UI", Font.BOLD, 14));
            ed.setForeground(AZUL);
            JLabel rm = new JLabel("⌫");
            rm.setFont(new Font("Segoe UI", Font.BOLD, 14));
            rm.setForeground(P_ROJO);
            add(ed); add(rm);
            return this;
        }
    }

    static class Pill extends JLabel {
        private final Color bg;
        Pill(String texto, Color bg) {
            super(texto, SwingConstants.CENTER);
            this.bg = bg;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(4, 13, 4, 13));
            setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DIÁLOGO AGREGAR / EDITAR
    // ══════════════════════════════════════════════════════════════════════════
    public static class ProveedorDialog extends JDialog {

        private static final String[] TIPOS = {
            "Carnes y Aves", "Frutas y Verduras",
            "Bebidas", "Lácteos", "Insumos secos"
        };
        private static final String[] REGIONES = {
            "La Libertad", "Lambayeque", "Áncash",
            "Lima", "Cajamarca", "Piura", "Arequipa", "Ica"
        };

        private final JTextField        fNombre      = fc("Ej: Avícola San José");
        private final JTextField        fRucDni      = fn("Ej: 20512345678", 11);
        private final JTextField        fTelefono    = fn("Ej: 944123456", 9);
        private final JTextField        fDireccion   = fc("Ej: Av. España 1240, Trujillo");
        private final JComboBox<String> cbTipoInsumo = cc(TIPOS);
        private final JComboBox<String> cbRegion     = cc(REGIONES);
        private final JSpinner spCumplimiento =
                new JSpinner(new SpinnerNumberModel(90, 0, 100, 1));
        private final JComboBox<String> cbActivo = cc(new String[]{"activo","inactivo"});

        private final JLabel eNombre    = fe();
        private final JLabel eRucDni    = fe();
        private final JLabel eTelefono  = fe();
        private final JLabel eDireccion = fe();

        private boolean confirmado = false;
        private final Proveedor original;

        private static final Color D_AZUL   = new Color(26, 83, 160);
        private static final Color D_HOV    = new Color(18, 65, 128);
        private static final Color D_WHITE  = Color.WHITE;
        private static final Color D_BORDER = new Color(225, 229, 236);
        private static final Color D_TEXT   = new Color(30,  37,  48);
        private static final Color D_ROJO   = new Color(220,  53,  69);

        public ProveedorDialog(Window owner, Proveedor p) {
            super(owner,
                p == null ? "Añadir Proveedor Regional" : "Editar Proveedor",
                ModalityType.APPLICATION_MODAL);
            this.original = p;
            init();
            if (p != null) precargar(p);
        }

        private void init() {
            setSize(530, 545);
            setResizable(false);
            setLocationRelativeTo(getOwner());
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);

            JPanel root = new JPanel(new BorderLayout());
            root.setBackground(D_WHITE);
            setContentPane(root);

            JPanel header = new JPanel(new BorderLayout());
            header.setBackground(D_AZUL);
            header.setBorder(new EmptyBorder(14, 20, 14, 20));
            JPanel hTxts = new JPanel();
            hTxts.setLayout(new BoxLayout(hTxts, BoxLayout.Y_AXIS));
            hTxts.setBackground(D_AZUL);
            JLabel hTit = new JLabel(original == null
                    ? "Añadir Proveedor Regional" : "Editar Proveedor");
            hTit.setForeground(Color.WHITE);
            hTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
            JLabel hSub = new JLabel(original == null
                    ? "Complete los campos obligatorios (*)"
                    : "Modifique los datos y confirme los cambios");
            hSub.setForeground(new Color(255, 255, 255, 180));
            hSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            hTxts.add(hTit); hTxts.add(Box.createVerticalStrut(2)); hTxts.add(hSub);
            header.add(hTxts, BorderLayout.CENTER);
            root.add(header, BorderLayout.NORTH);

            JPanel body = new JPanel(new BorderLayout());
            body.setBackground(D_WHITE);
            body.setBorder(new EmptyBorder(18, 24, 8, 24));
            body.add(buildForm(),   BorderLayout.CENTER);
            body.add(buildFooter(), BorderLayout.SOUTH);
            root.add(body, BorderLayout.CENTER);
        }

        private JPanel buildForm() {
            spCumplimiento.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            ((JSpinner.DefaultEditor) spCumplimiento.getEditor())
                    .getTextField().setHorizontalAlignment(JTextField.CENTER);

            JPanel p = new JPanel(new GridBagLayout());
            p.setBackground(D_WHITE);

            fila(p, 0, 0, "Nombre / Razón Social *", fNombre,       eNombre);
            fila(p, 1, 0, "RUC o DNI *",             fRucDni,       eRucDni);
            fila(p, 0, 1, "Teléfono *",              fTelefono,     eTelefono);
            fila(p, 1, 1, "Región *",                cbRegion,      null);
            filaAncha(p, 2, "Dirección *",           fDireccion,    eDireccion);
            fila(p, 0, 3, "Tipo de Insumo *",        cbTipoInsumo,  null);
            fila(p, 1, 3, "Cumplimiento (%)",        spCumplimiento,null);
            fila(p, 0, 4, "Estado",                  cbActivo,      null);

            return p;
        }

        private void fila(JPanel p, int col, int row,
                          String lbl, JComponent comp, JLabel err) {
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 0.5; g.gridx = col;
            int lp = (col==1)?10:0, rp = (col==0)?10:0;
            g.gridy = row*3;   g.insets = new Insets(0,lp,2,rp);  p.add(lbf(lbl), g);
            g.gridy = row*3+1; g.insets = new Insets(0,lp,0,rp);
            comp.setPreferredSize(new Dimension(0,36)); p.add(comp, g);
            g.gridy = row*3+2; g.insets = new Insets(0,lp,6,rp);
            p.add(err != null ? err : new JLabel(" "), g);
        }

        private void filaAncha(JPanel p, int row, String lbl,
                               JComponent comp, JLabel err) {
            GridBagConstraints g = new GridBagConstraints();
            g.fill = GridBagConstraints.HORIZONTAL; g.weightx = 1.0;
            g.gridwidth = 2; g.gridx = 0;
            g.gridy = row*3;   g.insets = new Insets(0,0,2,0); p.add(lbf(lbl), g);
            g.gridy = row*3+1; g.insets = new Insets(0,0,0,0);
            comp.setPreferredSize(new Dimension(0,36)); p.add(comp, g);
            g.gridy = row*3+2; g.insets = new Insets(0,0,6,0);
            p.add(err != null ? err : new JLabel(" "), g);
        }

        private JPanel buildFooter() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
            p.setBackground(D_WHITE);
            p.setBorder(BorderFactory.createMatteBorder(1,0,0,0, D_BORDER));
            JButton bCan = bSec("Cancelar");
            JButton bOk  = bPri(original == null ? "Guardar" : "Actualizar");
            bCan.addActionListener(e -> dispose());
            bOk .addActionListener(e -> intentarGuardar());
            p.add(bCan); p.add(bOk);
            return p;
        }

        private void intentarGuardar() {
            resetErr();
            boolean ok = true;

            if (fNombre.getText().trim().isEmpty()) {
                marcarErr(fNombre, eNombre, "El nombre es obligatorio"); ok = false;
            }
            String ruc = fRucDni.getText().trim();
            if (ruc.isEmpty()) {
                marcarErr(fRucDni, eRucDni, "El RUC/DNI es obligatorio"); ok = false;
            } else if (!ruc.matches("\\d{8}") && !ruc.matches("\\d{11}")) {
                marcarErr(fRucDni, eRucDni, "8 dígitos (DNI) u 11 dígitos (RUC)"); ok = false;
            }
            if (fTelefono.getText().trim().isEmpty()) {
                marcarErr(fTelefono, eTelefono, "El teléfono es obligatorio"); ok = false;
            } else if (!fTelefono.getText().trim().matches("\\d{9}")) {
                marcarErr(fTelefono, eTelefono, "Debe tener 9 dígitos"); ok = false;
            }
            if (fDireccion.getText().trim().isEmpty()) {
                marcarErr(fDireccion, eDireccion, "La dirección es obligatoria"); ok = false;
            }
            if (!ok) return;
            confirmado = true;
            dispose();
        }

        private void resetErr() {
            eNombre   .setText(" "); fNombre   .setBorder(borde());
            eRucDni   .setText(" "); fRucDni   .setBorder(borde());
            eTelefono .setText(" "); fTelefono .setBorder(borde());
            eDireccion.setText(" "); fDireccion.setBorder(borde());
        }

        private void marcarErr(JTextField tf, JLabel err, String msg) {
            err.setText(msg);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(D_ROJO, 1, true),
                    new EmptyBorder(0, 8, 0, 8)));
        }

        private void precargar(Proveedor p) {
            fNombre     .setText(p.getNombre());
            fRucDni     .setText(p.getRucDni());
            fTelefono   .setText(p.getTelefono());
            fDireccion  .setText(p.getDireccion());
            cbTipoInsumo.setSelectedItem(p.getTipoInsumo());
            cbRegion    .setSelectedItem(p.getRegion());
            spCumplimiento.setValue(p.getCumplimiento());
            cbActivo    .setSelectedItem(p.isActivo() ? "activo" : "inactivo");
        }

        public boolean isConfirmado() { return confirmado; }

        public Proveedor getProveedor() {
            int    id     = original != null ? original.getId()     : 0;
            String codigo = original != null ? original.getCodigo() : "";
            return new Proveedor(
                id, codigo,
                fNombre    .getText().trim().toUpperCase(),
                fRucDni    .getText().trim(),
                fTelefono  .getText().trim(),
                fDireccion .getText().trim(),
                (String)  cbTipoInsumo.getSelectedItem(),
                (String)  cbRegion.getSelectedItem(),
                (Integer) spCumplimiento.getValue(),
                "activo".equals(cbActivo.getSelectedItem())
            );
        }

        private static JTextField fc(String ph) {
            JTextField tf = new JTextField();
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setForeground(D_TEXT);
            tf.setBorder(borde());
            tf.putClientProperty("JTextField.placeholderText", ph);
            return tf;
        }

        private static JTextField fn(String ph, int max) {
            JTextField tf = fc(ph);
            tf.setDocument(new NumDoc(max));
            return tf;
        }

        private static JComboBox<String> cc(String[] items) {
            JComboBox<String> c = new JComboBox<>(items);
            c.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            c.setBackground(D_WHITE);
            return c;
        }

        private static JLabel lbf(String texto) {
            JLabel l = new JLabel(texto);
            l.setFont(new Font("Segoe UI", Font.BOLD, 12));
            l.setForeground(D_TEXT);
            return l;
        }

        private static JLabel fe() {
            JLabel l = new JLabel(" ");
            l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            l.setForeground(D_ROJO);
            return l;
        }

        private static Border borde() {
            return BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(D_BORDER, 1, true),
                    new EmptyBorder(0, 8, 0, 8));
        }

        private static JButton bPri(String texto) {
            JButton b = new JButton(texto);
            b.setPreferredSize(new Dimension(120, 36));
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setOpaque(true);
            b.setContentAreaFilled(true);  // ← fix L&F Windows
            b.setBackground(D_AZUL);
            b.setForeground(Color.WHITE);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createLineBorder(D_AZUL, 1, true));
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(D_HOV); }
                public void mouseExited (MouseEvent e) { b.setBackground(D_AZUL); }
            });
            return b;
        }

        private static JButton bSec(String texto) {
            JButton b = new JButton(texto);
            b.setPreferredSize(new Dimension(110, 36));
            b.setFont(new Font("Segoe UI", Font.BOLD, 13));
            b.setOpaque(true);
            b.setContentAreaFilled(true);  // ← fix L&F Windows
            b.setBackground(D_WHITE);
            b.setForeground(D_AZUL);
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            b.setBorder(BorderFactory.createLineBorder(D_AZUL, 1, true));
            b.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { b.setBackground(new Color(232, 241, 255)); }
                public void mouseExited (MouseEvent e) { b.setBackground(D_WHITE); }
            });
            return b;
        }

        private static class NumDoc extends PlainDocument {
            private final int max;
            NumDoc(int max) { this.max = max; }
            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) return;
                String solo = str.replaceAll("[^0-9]", "");
                int esp = max - getLength();
                if (esp <= 0) return;
                if (solo.length() > esp) solo = solo.substring(0, esp);
                super.insertString(offs, solo, a);
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ProveedorFrame().setVisible(true);
        });
    }
}
