package proyecto.pos.gui;

import proyecto.pos.model.Merma;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Empleado;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;


public class MermaFrame extends JFrame {

    // ── Paleta del sistema ────────────────────────────────────────────────────
    static final Color AZUL        = new Color(26,  83, 160);
    static final Color AZUL_HOV    = new Color(18,  65, 128);
    static final Color AZUL_CLAR   = new Color(232, 241, 255);
    static final Color PAGE_BG     = new Color(246, 248, 251);
    static final Color CARD_BG     = Color.WHITE;
    static final Color BORDER_CLR  = new Color(225, 229, 236);
    static final Color TEXT_DARK   = new Color(30,  37,  48);
    static final Color TEXT_MUTED  = new Color(105, 113, 128);
    static final Color VERDE       = new Color(40,  167,  69);
    static final Color ROJO        = new Color(220,  53,  69);
    static final Color NARANJA     = new Color(255, 145,  77);
    static final Color GRIS        = new Color(150, 157, 168);
    static final Color AMARILLO    = new Color(255, 193,   7);

    // ── Columnas tabla ────────────────────────────────────────────────────────
    static final int COL_ID      = 0;
    static final int COL_CODINSU = 1;
    static final int COL_INSUMO  = 2;
    static final int COL_CANT    = 3;
    static final int COL_UNIDAD  = 4;
    static final int COL_MOTIVO  = 5;
    static final int COL_EMPLEADO= 6;
    static final int COL_FECHA   = 7;
    static final int COL_COSTO   = 8;
    static final int COL_ESTADO  = 9;
    static final int COL_ACC     = 10;

    // ── Datos demo ────────────────────────────────────────────────────────────
    private final List<Merma>    mermas     = new ArrayList<>();
    private final List<Insumo>   insumos    = new ArrayList<>();
    private final List<Empleado> empleados  = new ArrayList<>();
    private int nextId = 59;

    // ── Componentes UI ────────────────────────────────────────────────────────
    private DefaultTableModel                 modelo;
    private JTable                            tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField                        txtBuscar;

    // Formulario
    private JTextField        txtIdMerma;
    private JComboBox<String> cboInsumo;
    private JComboBox<String> cboEmpleado;
    private JTextField        txtCantidad;
    private JComboBox<String> cboUnidad;
    private JTextArea         txtMotivo;
    private JTextField        txtFecha;

    // Info adicional
    private JLabel lblAlmacen, lblRegistradoPor, lblUltimaAct;

    // Stats
    private JLabel lblTotalMermaKg, lblNumMermas, lblInsumoMas, lblCostoEst;

    // Paginación
    private JLabel lblPagInfo;
    private int    paginaActual = 1;
    private static final int FILAS_POR_PAG = 5;

    // Merma seleccionada para editar
    private int filaSeleccionada = -1;

    // ══════════════════════════════════════════════════════════════════════════
    public MermaFrame() {
        setTitle("Registro de Merma");
        setSize(1280, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cargarDatosDemo();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.add(new MenuSidebar(this, "Mermas"), BorderLayout.WEST);
        root.add(buildContenido(), BorderLayout.CENTER);
        setContentPane(root);

        actualizarStats();
        nuevoRegistro();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ══════════════════════════════════════════════════════════════════════════
    private void cargarDatosDemo() {
        // Insumos demo
        insumos.add(demoInsumo(12, "INS-012", "Carne de Res",    "Kg",  22.0));
        insumos.add(demoInsumo(8,  "INS-008", "Arroz Integral",  "Kg",  4.5));
        insumos.add(demoInsumo(11, "INS-011", "Frutas Variadas", "Kg",  6.0));
        insumos.add(demoInsumo(9,  "INS-009", "Uvas",            "Kg",  9.0));
        insumos.add(demoInsumo(13, "INS-013", "Pollo Entero",    "Kg",  10.5));

        // Empleados demo
        empleados.add(demoEmpleado(1, "Juan Pérez",  "Cocinero"));
        empleados.add(demoEmpleado(2, "Manuel Gotera", "Almacén"));

        // Mermas demo (las 5 del diseño)
        mermas.add(new Merma(59, insumos.get(0), empleados.get(0), 2.50, "Exceso de cocción",    fechaHoy(10, 30)));
        mermas.add(new Merma(58, insumos.get(1), empleados.get(1), 1.80, "Caducidad en almacén", fechaHoy(9,  15)));
        mermas.add(new Merma(57, insumos.get(2), empleados.get(1), 1.50, "Caducidad en almacén", fechaHoy(8,  45)));
        mermas.add(new Merma(56, insumos.get(3), empleados.get(1), 2.00, "Madurez excesiva",     fechaHoy(8,  30)));
        mermas.add(new Merma(55, insumos.get(4), empleados.get(0), 1.20, "Empaque dañado",       fechaHoy(7,  50)));
    }

    private Insumo demoInsumo(int id, String cod, String nombre, String unidad, double precio) {
        Insumo i = new Insumo();
        try {
            // Usa setters estándar — ajusta si tu Insumo tiene nombres diferentes
            i.getClass().getMethod("setInsumoId",  int.class)   .invoke(i, id);
            i.getClass().getMethod("setCodigo",     String.class).invoke(i, cod);
            i.getClass().getMethod("setNombre",     String.class).invoke(i, nombre);
            i.getClass().getMethod("setUnidad",     String.class).invoke(i, unidad);
            i.getClass().getMethod("setPrecioUnitario", double.class).invoke(i, precio);
        } catch (Exception ex) { /* fallback: usamos toString */ }
        return i;
    }

    private Empleado demoEmpleado(int id, String nombre, String cargo) {
        Empleado e = new Empleado();
        try {
            e.getClass().getMethod("setEmpleadoId", int.class)   .invoke(e, id);
            e.getClass().getMethod("setNombre",     String.class).invoke(e, nombre);
            e.getClass().getMethod("setCargo",      String.class).invoke(e, cargo);
        } catch (Exception ex) { /* fallback */ }
        return e;
    }

    private Date fechaHoy(int hora, int min) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hora);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    // Helpers para leer Insumo/Empleado sin depender de getters exactos
    private String nombreInsumo(Insumo i) {
        try { return (String) i.getClass().getMethod("getNombre").invoke(i); }
        catch (Exception e) { return i.toString(); }
    }
    private String codigoInsumo(Insumo i) {
        try { return (String) i.getClass().getMethod("getCodigo").invoke(i); }
        catch (Exception e) { return "?"; }
    }
    private String unidadInsumo(Insumo i) {
        try { return (String) i.getClass().getMethod("getUnidad").invoke(i); }
        catch (Exception e) { return "Kg"; }
    }
    private double precioInsumo(Insumo i) {
        try { return (double) i.getClass().getMethod("getPrecioUnitario").invoke(i); }
        catch (Exception e) { return 0; }
    }
    private String nombreEmpleado(Empleado e) {
        try { return (String) e.getClass().getMethod("getNombre").invoke(e); }
        catch (Exception ex) { return e.toString(); }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LAYOUT PRINCIPAL
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildContenido() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(PAGE_BG);
        c.setBorder(new EmptyBorder(22, 26, 18, 26));
        c.add(buildHeader(),  BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 14));
        centro.setOpaque(false);
        centro.add(buildStatsRow(),    BorderLayout.NORTH);
        centro.add(buildBodySplit(),   BorderLayout.CENTER);

        c.add(centro, BorderLayout.CENTER);
        return c;
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PAGE_BG);
        h.setBorder(new EmptyBorder(0, 0, 16, 0));

        // Izquierda: icono + títulos
        JPanel izq = new JPanel(new BorderLayout(12, 0));
        izq.setOpaque(false);

        JLabel iconLbl = new JLabel(MenuSidebar.redimensionarIcono("/img/billetera.png", 32, 32));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(new Color(255, 235, 220));
        iconLbl.setPreferredSize(new Dimension(48, 48));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        iconLbl.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Registro de Merma");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titulo.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Registra y controla las mermas o desperdicios de insumos");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(TEXT_MUTED);
        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(sub);

        izq.add(iconLbl, BorderLayout.WEST);
        izq.add(titulos, BorderLayout.CENTER);

        // Derecha: chips
        JPanel chips = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        chips.setOpaque(false);
        chips.add(buildChipHora());
        chips.add(buildChipUsuario());

        h.add(izq,   BorderLayout.WEST);
        h.add(chips, BorderLayout.EAST);
        return h;
    }

    private JPanel buildChipHora() {
        JPanel chip = new JPanel();
        chip.setLayout(new BoxLayout(chip, BoxLayout.Y_AXIS));
        chip.setBackground(CARD_BG);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(5, 12, 5, 12)));
        chip.setPreferredSize(new Dimension(120, 44));

        JLabel lHora = new JLabel(new SimpleDateFormat("hh:mm a").format(new Date()));
        lHora.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lHora.setForeground(TEXT_DARK);

        JLabel lFecha = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        lFecha.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lFecha.setForeground(TEXT_MUTED);


        chip.add(lHora); chip.add(lFecha);
        return chip;
    }

    private JPanel buildChipUsuario() {
        JPanel chip = new JPanel(new BorderLayout(8, 0));
        chip.setBackground(CARD_BG);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 10, 6, 12)));
        chip.setPreferredSize(new Dimension(160, 44));

        ImageIcon foto = MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 28, 28);
        JLabel avatarLbl = foto != null ? new JLabel(foto) : new JLabel("MG");
        if (foto == null) {
            avatarLbl.setOpaque(true);
            avatarLbl.setBackground(AZUL);
            avatarLbl.setForeground(Color.WHITE);
            avatarLbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            avatarLbl.setHorizontalAlignment(SwingConstants.CENTER);
            avatarLbl.setPreferredSize(new Dimension(28, 28));
        }

        JPanel txt = new JPanel();
        txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lNom = new JLabel("Manuel Gotera");
        lNom.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lNom.setForeground(TEXT_DARK);
        JLabel lRol = new JLabel("Almacén");
        lRol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lRol.setForeground(TEXT_MUTED);
        txt.add(lNom); txt.add(lRol);

        chip.add(avatarLbl, BorderLayout.WEST);
        chip.add(txt,       BorderLayout.CENTER);
        return chip;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  STATS CARDS
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 14, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 90));

        // Card 1 — Total Merma Kg
        JPanel c1 = statCard("Total Merma (Mes)", new Color(232, 241, 255), AZUL);
        lblTotalMermaKg = getStatValLabel(c1);
        row.add(c1);

        // Card 2 — Número de Mermas
        JPanel c2 = statCard("Número de Mermas", new Color(232, 248, 238), VERDE);
        lblNumMermas = getStatValLabel(c2);
        row.add(c2);

        // Card 3 — Insumo con Más Merma
        JPanel c3 = statCard("Insumo con Más Merma", new Color(255, 240, 230), ROJO);
        lblInsumoMas = getStatValLabel(c3);
        lblInsumoMas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        row.add(c3);

        // Card 4 — Costo Est.
        JPanel c4 = statCard("Costo Est. Merma (Mes)", new Color(255, 249, 219), new Color(180, 120, 0));
        lblCostoEst = getStatValLabel(c4);
        row.add(c4);

        return row;
    }

    private JPanel statCard(String titulo, Color bgIcon, Color colorVal) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel iconBox = new JPanel();
        iconBox.setBackground(bgIcon);
        iconBox.setPreferredSize(new Dimension(44, 44));

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setBackground(CARD_BG);

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lTit.setForeground(TEXT_MUTED);

        JLabel lVal = new JLabel("—");
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lVal.setForeground(colorVal);
        lVal.putClientProperty("statVal", Boolean.TRUE); // para localizarlo

        texts.add(lTit);
        texts.add(Box.createVerticalStrut(3));
        texts.add(lVal);

        card.add(iconBox, BorderLayout.WEST);
        card.add(texts,   BorderLayout.CENTER);
        return card;
    }

    private JLabel getStatValLabel(JPanel card) {
        JPanel texts = (JPanel) card.getComponent(1);
        return (JLabel) texts.getComponent(2);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BODY: FORMULARIO + TABLA
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildBodySplit() {
        JPanel body = new JPanel(new BorderLayout(0, 14));
        body.setOpaque(false);
        body.add(buildFormCard(),  BorderLayout.NORTH);
        body.add(buildTableCard(), BorderLayout.CENTER);
        return body;
    }

    // ── FORMULARIO ────────────────────────────────────────────────────────────
    private JPanel buildFormCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 18, 16, 18)));

        // Título sección
        JLabel secTit = new JLabel("  \uD83D\uDCCB  Datos de la Merma");
        secTit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        secTit.setForeground(TEXT_DARK);
        secTit.setBorder(new EmptyBorder(0, 0, 12, 0));

        JPanel campos = new JPanel(new GridBagLayout());
        campos.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(4, 6, 4, 6);

        // ── Columna izquierda ─────────────────────────────────────────────────
        // ID Merma
        g.gridx=0; g.gridy=0; g.weightx=0.3;
        campos.add(lbl("ID Merma"), g);

        g.gridx=0; g.gridy=1;
        txtIdMerma = campo("");
        txtIdMerma.setEditable(false);
        txtIdMerma.setBackground(new Color(245, 246, 248));
        txtIdMerma.setForeground(TEXT_MUTED);
        campos.add(txtIdMerma, g);

        // Insumo
        g.gridx=0; g.gridy=2;
        campos.add(lbl("Insumo *"), g);
        g.gridx=0; g.gridy=3;
        cboInsumo = new JComboBox<>();
        for (Insumo i : insumos) cboInsumo.addItem(nombreInsumo(i));
        estilizarCombo(cboInsumo);
        campos.add(cboInsumo, g);

        // Empleado
        g.gridx=0; g.gridy=4;
        campos.add(lbl("Empleado *"), g);
        g.gridx=0; g.gridy=5;
        cboEmpleado = new JComboBox<>();
        cboEmpleado.addItem("xdd");
        for (Empleado e : empleados) cboEmpleado.addItem(nombreEmpleado(e) + " (" + cargo(e) + ")");
        estilizarCombo(cboEmpleado);
        campos.add(cboEmpleado, g);

        // Cantidad + Unidad
        g.gridx=0; g.gridy=6;
        campos.add(lbl("Cantidad *"), g);

        g.gridx=0; g.gridy=7;
        JPanel cantPanel = new JPanel(new BorderLayout(6, 0));
        cantPanel.setOpaque(false);
        txtCantidad = campo("0.00");
        cboUnidad = new JComboBox<>(new String[]{"Kg", "Lt", "Unid", "g"});
        estilizarCombo(cboUnidad);
        cboUnidad.setPreferredSize(new Dimension(70, 34));
        cantPanel.add(txtCantidad, BorderLayout.CENTER);
        cantPanel.add(cboUnidad,   BorderLayout.EAST);
        campos.add(cantPanel, g);

        // ── Columna centro (Motivo + Fecha) ──────────────────────────────────
        g.gridx=1; g.gridy=0; g.weightx=0.4;
        campos.add(lbl("Motivo *"), g);

        g.gridx=1; g.gridy=1; g.gridheight=5;
        txtMotivo = new JTextArea(4, 20);
        txtMotivo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMotivo.setForeground(TEXT_DARK);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);
        txtMotivo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 8, 6, 8)));
        JScrollPane scrollMotivo = new JScrollPane(txtMotivo);
        scrollMotivo.setBorder(BorderFactory.createEmptyBorder());
        campos.add(scrollMotivo, g);
        g.gridheight=1;

        g.gridx=1; g.gridy=6;
        campos.add(lbl("Fecha de Registro *"), g);

        g.gridx=1; g.gridy=7;
        JPanel fechaPanel = new JPanel(new BorderLayout(6, 0));
        fechaPanel.setOpaque(false);
        txtFecha = campo(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date()));
        txtFecha.setEditable(false);
        txtFecha.setBackground(new Color(245, 246, 248));
        JLabel calIco = new JLabel("📅");
        calIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        calIco.setBorder(new EmptyBorder(0, 4, 0, 0));
        fechaPanel.add(txtFecha, BorderLayout.CENTER);
        fechaPanel.add(calIco,   BorderLayout.EAST);
        campos.add(fechaPanel, g);

        // ── Columna derecha: Info adicional ───────────────────────────────────
        g.gridx=2; g.gridy=0; g.gridheight=8; g.weightx=0.3;
        campos.add(buildInfoAdicional(), g);
        g.gridheight=1;

        // ── Botones de acción ────────────────────────────────────────────────
        JPanel botones = new JPanel(new BorderLayout());
        botones.setOpaque(false);
        botones.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel izqBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izqBtns.setOpaque(false);

        JButton btnGuardar  = btnColor("  \uD83D\uDCBE  Guardar",  VERDE,              Color.WHITE);
        JButton btnEditar   = btnColor("  ✎  Editar",             AZUL,               Color.WHITE);
        JButton btnEliminar = btnColor("  \uD83D\uDDD1  Eliminar", ROJO,               Color.WHITE);
        JButton btnLimpiar  = btnColor("  ✖  Limpiar",            new Color(108,117,125), Color.WHITE);

        btnGuardar .addActionListener(e -> guardar());
        btnEditar  .addActionListener(e -> editar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar .addActionListener(e -> nuevoRegistro());

        izqBtns.add(btnGuardar);
        izqBtns.add(btnEditar);
        izqBtns.add(btnEliminar);
        izqBtns.add(btnLimpiar);

        JButton btnCancelar = new JButton("✕  Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCancelar.setForeground(TEXT_MUTED);
        btnCancelar.setBackground(CARD_BG);
        btnCancelar.setOpaque(true);
        btnCancelar.setContentAreaFilled(true);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        btnCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> nuevoRegistro());

        JPanel derBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        derBtns.setOpaque(false);
        derBtns.add(btnCancelar);

        botones.add(izqBtns,  BorderLayout.WEST);
        botones.add(derBtns,  BorderLayout.EAST);

        JPanel formBody = new JPanel(new BorderLayout());
        formBody.setOpaque(false);
        formBody.add(campos,  BorderLayout.CENTER);
        formBody.add(botones, BorderLayout.SOUTH);

        card.add(secTit,   BorderLayout.NORTH);
        card.add(formBody, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildInfoAdicional() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AZUL_CLAR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(174, 204, 252), 1, true),
                new EmptyBorder(14, 16, 14, 16)));

        JPanel inner = new JPanel(new GridBagLayout());
        inner.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1; g.gridx = 0;
        g.insets = new Insets(3, 0, 3, 0);

        JLabel tit = new JLabel("ℹ  Información Adicional");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tit.setForeground(AZUL);
        g.gridy = 0; inner.add(tit, g);

        g.gridy = 1; inner.add(Box.createVerticalStrut(6), g);

        g.gridy = 2; inner.add(infoFila("Almacén:",          "Almacén Central"), g);
        g.gridy = 3; inner.add(infoFila("Registrado por:",   "Manuel Gotera"),     g);

        lblAlmacen      = getInfoVal(infoFila("Almacén:",        "Almacén Central"));
        lblRegistradoPor= getInfoVal(infoFila("Registrado por:", "Manuel Gotera"));
        lblUltimaAct    = getInfoVal(infoFila("Última actualización:",
                new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date())));

        g.gridy = 2; inner.add(buildInfoFilaRef("Almacén:",               "Almacén Central"), g);
        g.gridy = 3; inner.add(buildInfoFilaRef("Registrado por:",        "Manuel Gotera"),     g);
        g.gridy = 4; inner.add(buildInfoFilaRef("Última actualización:",
                new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date())),             g);

        panel.add(inner, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildInfoFilaRef(String etq, String val) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 4, 0));
        fila.setOpaque(false);
        JLabel lEtq = new JLabel(etq);
        lEtq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lEtq.setForeground(TEXT_MUTED);
        JLabel lVal = new JLabel(val);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lVal.setForeground(TEXT_DARK);
        fila.add(lEtq); fila.add(lVal);
        return fila;
    }

    private JPanel infoFila(String etq, String val) { return buildInfoFilaRef(etq, val); }
    private JLabel getInfoVal(JPanel p) { return (JLabel) p.getComponent(1); }

    // ── TABLA ─────────────────────────────────────────────────────────────────
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(14, 16, 12, 16)));

        card.add(buildTableHeader(), BorderLayout.NORTH);
        card.add(buildTabla(),       BorderLayout.CENTER);
        card.add(buildTableFooter(), BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildTableHeader() {
        JPanel h = new JPanel(new BorderLayout(12, 0));
        h.setOpaque(false);
        h.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel tit = new JLabel("  ☰  Listado de Mermas");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tit.setForeground(TEXT_DARK);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        derecha.setOpaque(false);

        // Buscador
        JPanel buscPanel = new JPanel(new BorderLayout(6, 0));
        buscPanel.setBackground(CARD_BG);
        buscPanel.setPreferredSize(new Dimension(220, 32));
        buscPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 8, 0, 8)));
        JLabel lupaIco = new JLabel("🔍");
        lupaIco.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar mermas...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(); }
            public void removeUpdate(DocumentEvent e)  { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });
        buscPanel.add(lupaIco,   BorderLayout.WEST);
        buscPanel.add(txtBuscar, BorderLayout.CENTER);

        // Botón filtros
        JButton btnFiltros = btnSecundario("▼  Filtros", 100);

        // Botón refresh
        JButton btnRefresh = btnSecundario("↺", 36);
        btnRefresh.addActionListener(e -> {
            txtBuscar.setText("");
            filtrar();
        });

        // Botón exportar
        JButton btnExport = btnSecundario("⬇", 36);
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Exportación pendiente de conectar.", "Exportar", JOptionPane.INFORMATION_MESSAGE));

        derecha.add(buscPanel);
        derecha.add(btnFiltros);
        derecha.add(btnRefresh);
        derecha.add(btnExport);

        h.add(tit,     BorderLayout.WEST);
        h.add(derecha, BorderLayout.EAST);
        return h;
    }

    private JScrollPane buildTabla() {
        modelo = new DefaultTableModel(
            new String[]{
                "ID Merma", "Código Insumo", "Insumo", "Cantidad", "Unidad",
                "Motivo", "Empleado", "Fecha Registro", "Costo Est.", "Estado", "Acciones"
            }, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Merma m : mermas) modelo.addRow(mermaToRow(m, estadoDemo(m.getMermaId())));

        tabla = new JTable(modelo);
        tabla.setRowHeight(38);
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
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        th.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modelo);
        tabla.setRowSorter(sorter);

        int[] anchos = {90, 100, 120, 70, 60, 140, 110, 130, 80, 90, 100};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Renderers
        tabla.getColumnModel().getColumn(COL_ESTADO).setCellRenderer(new EstadoRenderer());
        tabla.getColumnModel().getColumn(COL_ACC)   .setCellRenderer(new AccRenderer());

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int c : new int[]{COL_CANT, COL_UNIDAD, COL_COSTO}) {
            tabla.getColumnModel().getColumn(c).setCellRenderer(center);
        }

        // Clic en fila
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fv = tabla.rowAtPoint(e.getPoint());
                int cv = tabla.columnAtPoint(e.getPoint());
                if (fv < 0) return;
                int fm = tabla.convertRowIndexToModel(fv);
                if (tabla.convertColumnIndexToModel(cv) == COL_ACC) {
                    Rectangle r = tabla.getCellRect(fv, cv, true);
                    int xRel = e.getX() - r.x;
                    int tercio = r.width / 3;
                    if      (xRel < tercio)     cargarEnForm(fm);
                    else if (xRel < tercio * 2) cargarEnForm(fm);
                    else                        eliminarFila(fm);
                } else {
                    cargarEnForm(fm);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        return scroll;
    }

    private JPanel buildTableFooter() {
        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblPagInfo = new JLabel("Mostrando 1 a 5 de " + mermas.size() + " registros");
        lblPagInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPagInfo.setForeground(TEXT_MUTED);

        JPanel pagBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        pagBtns.setOpaque(false);

        JButton btnAnterior = btnPag("Anterior");
        JButton btnSig      = btnPag("Siguiente");

        JButton btn1  = btnPagNum("1",  true);
        JButton btn2  = btnPagNum("2",  false);
        JButton btn3  = btnPagNum("3",  false);
        JButton btnDot= btnPagNum("...",false);
        JButton btn12 = btnPagNum("12", false);

        btnAnterior.addActionListener(e -> { if (paginaActual > 1)  { paginaActual--; actualizarPag(); } });
        btnSig     .addActionListener(e -> {                           paginaActual++; actualizarPag(); });

        pagBtns.add(btnAnterior);
        pagBtns.add(btn1); pagBtns.add(btn2); pagBtns.add(btn3);
        pagBtns.add(btnDot); pagBtns.add(btn12);
        pagBtns.add(btnSig);

        foot.add(lblPagInfo, BorderLayout.WEST);
        foot.add(pagBtns,    BorderLayout.EAST);
        return foot;
    }

    private JButton btnPag(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        b.setPreferredSize(new Dimension(75, 28));
        b.setBackground(CARD_BG);
        b.setForeground(TEXT_DARK);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private JButton btnPagNum(String num, boolean activo) {
        JButton b = new JButton(num);
        b.setFont(new Font("Segoe UI", activo ? Font.BOLD : Font.PLAIN, 11));
        b.setPreferredSize(new Dimension(32, 28));
        b.setBackground(activo ? AZUL : CARD_BG);
        b.setForeground(activo ? Color.WHITE : TEXT_DARK);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setBorder(BorderFactory.createLineBorder(activo ? AZUL : BORDER_CLR, 1, true));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LÓGICA CRUD
    // ══════════════════════════════════════════════════════════════════════════
    private void guardar() {
        if (!validar()) return;

        Insumo   ins = insumos.get(cboInsumo.getSelectedIndex());
        Empleado emp = empleados.get(cboEmpleado.getSelectedIndex());
        double   cant= Double.parseDouble(txtCantidad.getText().replace(",", "."));
        String   mot = txtMotivo.getText().trim();

        if (filaSeleccionada >= 0) {
            // Editar existente
            Merma m = mermas.get(filaSeleccionada);
            m.setInsumo(ins);
            m.setEmpleado(emp);
            m.setCantidad(cant);
            m.setMotivo(mot);
            modelo.setValueAt(codigoInsumo(ins),                         filaSeleccionada, COL_CODINSU);
            modelo.setValueAt(nombreInsumo(ins),                         filaSeleccionada, COL_INSUMO);
            modelo.setValueAt(String.format("%.2f", cant),               filaSeleccionada, COL_CANT);
            modelo.setValueAt((String) cboUnidad.getSelectedItem(),      filaSeleccionada, COL_UNIDAD);
            modelo.setValueAt(mot,                                        filaSeleccionada, COL_MOTIVO);
            modelo.setValueAt(nombreEmpleado(emp),                       filaSeleccionada, COL_EMPLEADO);
            modelo.setValueAt(String.format("S/ %.2f", cant * precioInsumo(ins)), filaSeleccionada, COL_COSTO);
            toast("Merma actualizada correctamente ✓");
        } else {
            // Nuevo
            Merma nueva = new Merma(nextId, ins, emp, cant, mot, new Date());
            mermas.add(0, nueva);
            modelo.insertRow(0, mermaToRow(nueva, "Activo"));
            nextId++;
            toast("Merma registrada correctamente ✓");
        }

        actualizarStats();
        nuevoRegistro();
    }

    private void editar() {
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una merma de la tabla para editar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Ya está cargada en el form — solo recordatorio
        toast("Modifica los campos y presiona Guardar");
    }

    private void eliminar() {
        if (filaSeleccionada < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecciona una merma de la tabla para eliminar.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        eliminarFila(filaSeleccionada);
    }

    private void eliminarFila(int fm) {
        String id = String.valueOf(modelo.getValueAt(fm, COL_ID));
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar la merma " + id + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        mermas.removeIf(m -> String.valueOf("MERM-" + String.format("%04d", m.getMermaId())).equals(id));
        modelo.removeRow(fm);
        actualizarStats();
        nuevoRegistro();
        toast("Merma eliminada correctamente");
    }

    private void cargarEnForm(int fm) {
        filaSeleccionada = fm;
        txtIdMerma .setText(String.valueOf(modelo.getValueAt(fm, COL_ID)));
        txtCantidad.setText(String.valueOf(modelo.getValueAt(fm, COL_CANT)));
        txtMotivo  .setText(String.valueOf(modelo.getValueAt(fm, COL_MOTIVO)));
        txtFecha   .setText(String.valueOf(modelo.getValueAt(fm, COL_FECHA)));

        String nomIns = String.valueOf(modelo.getValueAt(fm, COL_INSUMO));
        for (int i = 0; i < cboInsumo.getItemCount(); i++)
            if (cboInsumo.getItemAt(i).equals(nomIns)) { cboInsumo.setSelectedIndex(i); break; }

        String nomEmp = String.valueOf(modelo.getValueAt(fm, COL_EMPLEADO));
        for (int i = 0; i < cboEmpleado.getItemCount(); i++)
            if (cboEmpleado.getItemAt(i).startsWith(nomEmp)) { cboEmpleado.setSelectedIndex(i); break; }
    }

    private void nuevoRegistro() {
        filaSeleccionada = -1;
        txtIdMerma .setText("MERM-" + String.format("%04d", nextId));
        txtCantidad.setText("");
        txtMotivo  .setText("");
        txtFecha   .setText(new SimpleDateFormat("dd/MM/yyyy hh:mm a").format(new Date()));
        cboInsumo  .setSelectedIndex(0);
        cboEmpleado.setSelectedIndex(0);
        cboUnidad  .setSelectedIndex(0);
    }

    private boolean validar() {
        if (txtCantidad.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa la cantidad.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        try { Double.parseDouble(txtCantidad.getText().replace(",", ".")); }
        catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La cantidad debe ser un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtMotivo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingresa el motivo.", "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // ── Stats ─────────────────────────────────────────────────────────────────
    private void actualizarStats() {
        if (modelo == null) return;
        double totalKg   = 0;
        double totalCosto = 0;
        Map<String, Double> porInsumo = new LinkedHashMap<>();

        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                double cant = Double.parseDouble(String.valueOf(modelo.getValueAt(i, COL_CANT)));
                totalKg += cant;
                String costoStr = String.valueOf(modelo.getValueAt(i, COL_COSTO))
                        .replace("S/", "").replace("s/", "").trim();
                totalCosto += Double.parseDouble(costoStr);
                String ins = String.valueOf(modelo.getValueAt(i, COL_INSUMO));
                porInsumo.merge(ins, cant, Double::sum);
            } catch (Exception ignored) {}
        }

        String masInsumo = porInsumo.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> e.getKey() + "\n" + String.format("%.1f Kg", e.getValue()))
                .orElse("—");

        if (lblTotalMermaKg  != null) lblTotalMermaKg .setText(String.format("%.0f Kg",   totalKg));
        if (lblNumMermas     != null) lblNumMermas    .setText(String.valueOf(modelo.getRowCount()));
        if (lblInsumoMas     != null) lblInsumoMas    .setText(masInsumo.split("\n")[0]);
        if (lblCostoEst      != null) lblCostoEst     .setText(String.format("S/ %.2f",    totalCosto));
    }

    private void filtrar() {
        if (sorter == null || txtBuscar == null) return;
        String txt = txtBuscar.getText().trim();
        sorter.setRowFilter(txt.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + txt,
                  COL_ID, COL_INSUMO, COL_MOTIVO, COL_EMPLEADO));
        actualizarPag();
    }

    private void actualizarPag() {
        int vis   = tabla == null ? 0 : tabla.getRowCount();
        int total = modelo == null ? 0 : modelo.getRowCount();
        int desde = Math.min((paginaActual - 1) * FILAS_POR_PAG + 1, vis);
        int hasta = Math.min(paginaActual * FILAS_POR_PAG, vis);
        if (lblPagInfo != null)
            lblPagInfo.setText("Mostrando " + desde + " a " + hasta + " de " + total + " registros");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  HELPERS ROW / ESTADO
    // ══════════════════════════════════════════════════════════════════════════
    private Object[] mermaToRow(Merma m, String estado) {
        double costo = m.getCantidad() * precioInsumo(m.getInsumo());
        return new Object[]{
            "MERM-" + String.format("%04d", m.getMermaId()),
            codigoInsumo(m.getInsumo()),
            nombreInsumo(m.getInsumo()),
            String.format("%.2f", m.getCantidad()),
            unidadInsumo(m.getInsumo()),
            m.getMotivo(),
            nombreEmpleado(m.getEmpleado()),
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(m.getFecha_registro()),
            String.format("S/ %.2f", costo),
            estado,
            ""
        };
    }

    private String estadoDemo(int id) {
        if (id == 56) return "Eliminado";
        if (id == 55) return "Anulado";
        return "Activo";
    }

    private String cargo(Empleado e) {
        try { return (String) e.getClass().getMethod("getCargo").invoke(e); }
        catch (Exception ex) { return ""; }
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
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  BUILDERS DE COMPONENTES
    // ══════════════════════════════════════════════════════════════════════════
    private JLabel lbl(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(TEXT_DARK);
        return l;
    }

    private JTextField campo(String texto) {
        JTextField tf = new JTextField(texto);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(TEXT_DARK);
        tf.setPreferredSize(new Dimension(0, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 8, 0, 8)));
        return tf;
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cb.setBackground(CARD_BG);
        cb.setPreferredSize(new Dimension(0, 34));
    }

    private JButton btnColor(String txt, Color bg, Color fg) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        b.setPreferredSize(new Dimension(120, 36));
        b.setBackground(bg);
        b.setForeground(fg);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(bg.darker(), 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(bg.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    private JButton btnSecundario(String txt, int ancho) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(ancho, 32));
        b.setBackground(CARD_BG);
        b.setForeground(TEXT_DARK);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_CLAR); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD_BG); }
        });
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  RENDERERS
    // ══════════════════════════════════════════════════════════════════════════
    static class EstadoRenderer extends JPanel implements TableCellRenderer {
        EstadoRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            String s = String.valueOf(v);
            Color col = "Activo"  .equals(s) ? VERDE
                      : "Eliminado".equals(s) ? ROJO
                      : "Anulado" .equals(s) ? AMARILLO
                      : GRIS;
            add(new Pill(s, col), new GridBagConstraints());
            return this;
        }
    }

    static class AccRenderer extends JPanel implements TableCellRenderer {
        AccRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 4, 6)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);

            JLabel ver = iconBtn("👁",  new Color(232, 241, 255));
            JLabel edi = iconBtn("✎",  new Color(232, 241, 255));
            JLabel eli = iconBtn("🗑", new Color(255, 230, 230));

            add(ver); add(edi); add(eli);
            return this;
        }

        private JLabel iconBtn(String ico, Color bg) {
            JLabel l = new JLabel(ico, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            l.setOpaque(true);
            l.setBackground(bg);
            l.setPreferredSize(new Dimension(26, 24));
            l.setBorder(new EmptyBorder(2, 4, 2, 4));
            return l;
        }
    }

    static class Pill extends JLabel {
        private final Color bg;
        Pill(String txt, Color bg) {
            super(txt, SwingConstants.CENTER);
            this.bg = bg;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(3, 12, 3, 12));
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
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new MermaFrame().setVisible(true);
        });
    }
}