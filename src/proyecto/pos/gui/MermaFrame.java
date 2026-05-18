package proyecto.pos.gui;

import proyecto.pos.model.Merma;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Empleado;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.EmpleadoController;
import proyecto.pos.controller.InsumoController;
import proyecto.pos.controller.MermaController;
import proyecto.pos.model.PDFGenerator;

public class MermaFrame extends JFrame {

    // ── Paleta ───────────────────────────────────────────────────────────────
    static final Color AZUL       = new Color(26,  83, 160);
    static final Color AZUL_HOV   = new Color(18,  65, 128);
    static final Color AZUL_CLAR  = new Color(232, 241, 255);
    static final Color PAGE_BG    = new Color(246, 248, 251);
    static final Color CARD_BG    = Color.WHITE;
    static final Color BORDER_CLR = new Color(225, 229, 236);
    static final Color TEXT_DARK  = new Color(30,  37,  48);
    static final Color TEXT_MUTED = new Color(105, 113, 128);
    static final Color VERDE      = new Color(40,  167,  69);
    static final Color ROJO       = new Color(220,  53,  69);
    static final Color AMARILLO   = new Color(255, 193,   7);

    // ── Columnas (sin COL_ESTADO)
    static final int COL_ID      = 0;
    static final int COL_MERMA_ID = 1;
    static final int COL_CODINSU  = 2;
    static final int COL_INSUMO   = 3;
    static final int COL_CANT     = 4;
    static final int COL_UNIDAD   = 5;
    static final int COL_MOTIVO   = 6;
    static final int COL_EMPLEADO = 7;
    static final int COL_FECHA    = 8;
    static final int COL_COSTO    = 9;
    static final int COL_ACC      = 10;

    // ── Datos ────────────────────────────────────────────────────────────────
    private ArrayList<Merma>    mermas    = new ArrayList<>();
    private ArrayList<Insumo>   insumos   = new ArrayList<>();
    private ArrayList<Empleado> empleados = new ArrayList<>();
    //private int nextId = 60;

    // ── Componentes ──────────────────────────────────────────────────────────
    private DefaultTableModel                 modelo;
    private JTable                            tabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField                        txtBuscar;
    private JLabel                            lblFooter;
    private JLabel                            lblTotalKg, lblNumMermas, lblInsumoMas, lblCostoEst;

    private InsumoController insumo_controller;
    private MermaController merma_controller;
    private EmpleadoController empleado_controller;
    private Connection conexion;
    
    // ═════════════════════════════════════════════════════════════════════════
    public MermaFrame() {
        DatabaseConnection db = new DatabaseConnection();
        conexion = db.conectar();
        insumo_controller = new InsumoController(conexion);
        merma_controller = new MermaController(conexion);
        empleado_controller = new EmpleadoController(conexion);
        
        setTitle("Registro de Merma");
        setSize(1280, 760);
        setMinimumSize(new Dimension(1100, 640));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cargarDatosDemo();

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.add(new MenuSidebar(this, "Mermas"), BorderLayout.WEST);
        root.add(buildContenido(), BorderLayout.CENTER);
        setContentPane(root);

        actualizarStats();
    }
    
    
    private void cargarEmpleados(){
        empleados = (ArrayList<Empleado>) empleado_controller.listarEmpleados();
        
    }
    
    private void cargarInsumos(){
        insumos = (ArrayList<Insumo>) insumo_controller.listarInsumos();
    }
    
    private void cargarMermas(){
        mermas = (ArrayList<Merma>) merma_controller.listarMermas();
    }
    // ═════════════════════════════════════════════════════════════════════════
    //  DATOS DEMO
    // ═════════════════════════════════════════════════════════════════════════
    private void cargarDatosDemo() {
        cargarEmpleados();
        cargarInsumos();
        cargarMermas();
    }

    private void setPersonaFields(Empleado emp, int id, String nombre,
                                   String apellidos, String dni, String tel, String email) {
        try {
            Class<?> p = emp.getClass().getSuperclass();
            sf(p, emp, "id", id); sf(p, emp, "nombre", nombre);
            sf(p, emp, "apellidos", apellidos); sf(p, emp, "dni", dni);
            sf(p, emp, "telefono", tel); sf(p, emp, "email", email);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void sf(Class<?> c, Object o, String f, Object v) throws Exception {
        java.lang.reflect.Field field = c.getDeclaredField(f);
        field.setAccessible(true); field.set(o, v);
    }

    private String nombreInsumo(Insumo i)  { return i.getNombre()       != null ? i.getNombre()       : "—"; }
    private String codigoInsumo(Insumo i)  { return "INS-" + String.format("%03d", i.getInsumoId()); }
    private String unidadInsumo(Insumo i)  { return i.getUnidadMedida() != null ? i.getUnidadMedida() : "Kg"; }
    private float  costoInsumo (Insumo i)  { return i.getCosto(); }

    private String nombreEmpleado(Empleado e) {
        try {
            Class<?> p = e.getClass().getSuperclass();
            java.lang.reflect.Field fn = p.getDeclaredField("nombre");
            java.lang.reflect.Field fa = p.getDeclaredField("apellidos");
            fn.setAccessible(true); fa.setAccessible(true);
            return fn.get(e) + " " + fa.get(e);
        } catch (Exception ex) { return "Empleado"; }
    }

    private Date fecha(int h, int m) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h); c.set(Calendar.MINUTE, m); c.set(Calendar.SECOND, 0);
        return c.getTime();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LAYOUT PRINCIPAL
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildContenido() {
        JPanel c = new JPanel(new BorderLayout());
        c.setBackground(PAGE_BG);
        c.setBorder(new EmptyBorder(26, 28, 24, 28));
        c.add(buildHeader(), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setOpaque(false);
        centro.add(buildStatsRow(),  BorderLayout.NORTH);
        centro.add(buildTableCard(), BorderLayout.CENTER);
        c.add(centro, BorderLayout.CENTER);
        return c;
    }

    // ── Header ───────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout());
        h.setBackground(PAGE_BG);
        h.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));
        JLabel titulo = new JLabel("Registro de Merma");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24)); titulo.setForeground(TEXT_DARK);
        JLabel sub = new JLabel("Registra y controla las mermas o desperdicios de insumos");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13)); sub.setForeground(TEXT_MUTED);
        titulos.add(titulo); titulos.add(Box.createVerticalStrut(4)); titulos.add(sub);

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

        JLabel lHora  = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        lHora.setFont(new Font("Segoe UI", Font.BOLD, 11)); lHora.setForeground(TEXT_DARK);
        JLabel lFecha = new JLabel(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        lFecha.setFont(new Font("Segoe UI", Font.PLAIN, 10)); lFecha.setForeground(TEXT_MUTED);

        new javax.swing.Timer(1000, e ->
                lHora.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()))).start();

        chip.add(lHora); chip.add(lFecha);
        return chip;
    }

    private JPanel buildChipUsuario() {
        JPanel chip = new JPanel(new BorderLayout(8, 0));
        chip.setBackground(CARD_BG);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(6, 10, 6, 12)));
        chip.setPreferredSize(new Dimension(150, 40));

        ImageIcon foto = MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 28, 28);
        JLabel avatar  = foto != null ? new JLabel(foto) : fallbackAvatar("MG");

        JPanel txt = new JPanel(); txt.setOpaque(false);
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        JLabel lN = new JLabel("Manuel Gotera");
        lN.setFont(new Font("Segoe UI", Font.BOLD, 10)); lN.setForeground(TEXT_DARK);
        JLabel lR = new JLabel("Almacén");
        lR.setFont(new Font("Segoe UI", Font.PLAIN, 10)); lR.setForeground(TEXT_MUTED);
        txt.add(lN); txt.add(lR);

        chip.add(avatar, BorderLayout.WEST);
        chip.add(txt,    BorderLayout.CENTER);
        return chip;
    }

    private JLabel fallbackAvatar(String ini) {
        JLabel l = new JLabel(ini, SwingConstants.CENTER);
        l.setOpaque(true); l.setBackground(AZUL); l.setForeground(Color.WHITE);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setPreferredSize(new Dimension(28, 28));
        return l;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  STATS
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        JPanel c2 = statCard("Número de Mermas",       new Color(232, 248, 238), VERDE);
        JPanel c3 = statCard("Insumo con Más Merma",   new Color(255, 240, 230), ROJO);
        JPanel c4 = statCard("Costo Est. Merma (Mes)", new Color(255, 249, 219), new Color(160, 100, 0));

        lblNumMermas = getValLabel(c2);
        lblInsumoMas = getValLabel(c3); lblInsumoMas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblCostoEst  = getValLabel(c4);

         row.add(c2); row.add(c3); row.add(c4);
        return row;
    }

    private JPanel statCard(String titulo, Color bgIcon, Color colorVal) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                new EmptyBorder(16, 18, 16, 18)));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setBackground(bgIcon);
        iconBox.setPreferredSize(new Dimension(50, 50));

        JPanel texts = new JPanel(); texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setBackground(CARD_BG);
        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.PLAIN, 11)); lTit.setForeground(TEXT_MUTED);
        JLabel lVal = new JLabel("—");
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 26)); lVal.setForeground(colorVal);
        texts.add(lTit); texts.add(Box.createVerticalStrut(4)); texts.add(lVal);

        card.add(iconBox, BorderLayout.WEST);
        card.add(texts,   BorderLayout.CENTER);
        return card;
    }

    private JLabel getValLabel(JPanel card) {
        return (JLabel) ((JPanel) card.getComponent(1)).getComponent(2);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CARD TABLA
    // ═════════════════════════════════════════════════════════════════════════
    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(16, 16, 14, 16)));
        card.add(buildCardHeader(), BorderLayout.NORTH);
        card.add(buildTabla(),      BorderLayout.CENTER);
        card.add(buildFooter(),     BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildCardHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(CARD_BG);
        outer.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btns.setOpaque(false);

        JButton bExp = btnSecundario("🖨  Exportar PDF", 160);
        JButton bAgr = btnPrimario("＋  Nueva Merma",   170, AZUL, AZUL_HOV);

        bExp.addActionListener(e -> {
            try {
                PDFGenerator.generarReporteMermas(tabla);

                JOptionPane.showMessageDialog(this,
                        "PDF generado correctamente.",
                        "Exportar PDF",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al exportar: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        bAgr.addActionListener(e -> abrirDialogo(null));

        btns.add(bExp); btns.add(bAgr);

        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(12, 0, 0, 0));
        filtros.add(buildBuscador(), BorderLayout.EAST);

        outer.add(btns,    BorderLayout.NORTH);
        outer.add(filtros, BorderLayout.CENTER);
        return outer;
    }

    private JPanel buildBuscador() {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setBackground(CARD_BG);
        p.setPreferredSize(new Dimension(300, 36));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 10, 0, 10)));

        JLabel ico = new JLabel("🔍");
        ico.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXT_DARK);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar mermas...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { filtrar(); }
            public void removeUpdate(DocumentEvent e)  { filtrar(); }
            public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        p.add(ico,       BorderLayout.WEST);
        p.add(txtBuscar, BorderLayout.CENTER);
        return p;
    }

    // ── Tabla ────────────────────────────────────────────────────────────────
    private JScrollPane buildTabla() {
        // Sin columna "Estado"
        modelo = new DefaultTableModel(
            new String[]{"id","ID Merma","Cód. Insumo","Insumo","Cantidad","Unidad",
                         "Motivo","Empleado","Fecha Registro","Costo Est.","Acciones"}, 0
        ) { public boolean isCellEditable(int r, int c) { return false; } };

        for (Merma m : mermas)
            modelo.addRow(toRow(m));

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

        // 10 columnas (sin Estado)
        int[] anchos = {0, 95, 95, 130, 70, 60, 160, 115, 130, 85, 80};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        
        // ========================
        // OCULTAR COLUMNA ID
        // ========================
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        tabla.getColumnModel().getColumn(COL_ACC).setCellRenderer(new AccRenderer());

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(COL_CANT)  .setCellRenderer(center);
        tabla.getColumnModel().getColumn(COL_UNIDAD).setCellRenderer(center);
        tabla.getColumnModel().getColumn(COL_COSTO) .setCellRenderer(center);

        // Clic: mitad izquierda = editar, mitad derecha = eliminar
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fv = tabla.rowAtPoint(e.getPoint());
                int cv = tabla.columnAtPoint(e.getPoint());
                if (fv < 0) return;
                int fm = tabla.convertRowIndexToModel(fv);
                int cm = tabla.convertColumnIndexToModel(cv);
                if (cm == COL_ACC) {
                    Rectangle r  = tabla.getCellRect(fv, cv, true);
                    int xRel     = e.getX() - r.x;
                    if (xRel > r.width / 2) confirmarEliminar(fm);
                    else                    abrirDialogo(fm);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR, 1, true));
        scroll.getViewport().setBackground(CARD_BG);
        return scroll;
    }

    private JPanel buildFooter() {
        JPanel foot = new JPanel(new BorderLayout());
        foot.setOpaque(false);
        foot.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(80, 88, 100));
        foot.add(lblFooter, BorderLayout.WEST);
        actualizarFooter();
        return foot;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  DIÁLOGO AGREGAR / EDITAR
    // ═════════════════════════════════════════════════════════════════════════
   // ═════════════════════════════════════════════════════════════════════════
//  DIÁLOGO AGREGAR / EDITAR
// ═════════════════════════════════════════════════════════════════════════
private void abrirDialogo(Integer fm) {
    boolean esNuevo = (fm == null);

    JDialog dlg = new JDialog(this,
            esNuevo ? "Nueva Merma" : "Editar Merma", true);
    dlg.setSize(560, 500);
    dlg.setResizable(false);
    dlg.setLocationRelativeTo(this);

    // ── Combos ───────────────────────────────────────────────────────────
    JComboBox<String> cboIns = new JComboBox<>();
    JComboBox<String> cboEmp = new JComboBox<>();
    JTextField        txtCant = campoTexto("");
    JComboBox<String> cboUni  = new JComboBox<>(new String[]{"KG", "LITRO", "UNIDAD", "PAQUETE", "DOCENA"});
    estilizarCombo(cboUni);
    rebuilInsumoCombo(cboIns, cboUni);
    rebuilEmpleadoCombo(cboEmp);
    estilizarCombo(cboIns);
    estilizarCombo(cboEmp);

    JTextField txtCostoPreview = campoTexto("S/ 0.00");
    txtCostoPreview.setEditable(false);
    txtCostoPreview.setForeground(AZUL);
    txtCostoPreview.setFont(new Font("Segoe UI", Font.BOLD, 13));
    txtCostoPreview.setBackground(AZUL_CLAR);

    JTextArea txtMot = new JTextArea(4, 25);
    txtMot.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    txtMot.setLineWrap(true);
    txtMot.setWrapStyleWord(true);
    txtMot.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR, 1, true),
            new EmptyBorder(8, 10, 8, 10)));

    // ── Cálculo costo en tiempo real ─────────────────────────────────────
    Runnable actualizarCosto = () -> {
        try {
            int idx = cboIns.getSelectedIndex();
            // idx 0 = placeholder, último = "➕ Nuevo insumo..."
            if (idx <= 0 || idx >= insumos.size() + 1) {
                txtCostoPreview.setText("S/ 0.00");
                txtCostoPreview.setForeground(TEXT_MUTED);
                return;
            }
            double cant  = Double.parseDouble(txtCant.getText().replace(",", ".").trim());
            double costo = cant * costoInsumo(insumos.get(idx - 1));
            txtCostoPreview.setText(String.format("S/ %.2f", costo));
            txtCostoPreview.setForeground(AZUL);
        } catch (Exception ex) {
            txtCostoPreview.setText("S/ 0.00");
            txtCostoPreview.setForeground(TEXT_MUTED);
        }
    };

    txtCant.getDocument().addDocumentListener(new DocumentListener() {
        public void insertUpdate(DocumentEvent e)  { actualizarCosto.run(); }
        public void removeUpdate(DocumentEvent e)  { actualizarCosto.run(); }
        public void changedUpdate(DocumentEvent e) { actualizarCosto.run(); }
    });

    // Interceptar selección "➕ Nuevo insumo..." / "➕ Nuevo empleado..."
    cboIns.addActionListener(e -> {
        int idx = cboIns.getSelectedIndex();
        int ultimoIns = cboIns.getItemCount() - 1;
        if (idx == ultimoIns) { // "➕ Nuevo insumo..."
            Insumo nuevo = dialogNuevoInsumo(dlg);
            if (nuevo != null) {
                insumos.add(nuevo);
                rebuilInsumoCombo(cboIns, cboUni);
                cboIns.setSelectedIndex(insumos.size()); // selecciona el recién creado
            } else {
                cboIns.setSelectedIndex(0);
            }
        }
        actualizarCosto.run();
    });

    cboEmp.addActionListener(e -> {
        int idx = cboEmp.getSelectedIndex();
        int ultimoEmp = cboEmp.getItemCount() - 1;
        if (idx == ultimoEmp) { // "➕ Nuevo empleado..."
            Empleado nuevo = dialogNuevoEmpleado(dlg);
            if (nuevo != null) {
                empleados.add(nuevo);
                rebuilEmpleadoCombo(cboEmp);
                cboEmp.setSelectedIndex(empleados.size()); // selecciona el recién creado
            } else {
                cboEmp.setSelectedIndex(0);
            }
        }
    });

    // ── Pre-cargar si es edición ──────────────────────────────────────────
    if (!esNuevo) {
        String nomIns = String.valueOf(modelo.getValueAt(fm, COL_INSUMO));
        for (int i = 1; i < cboIns.getItemCount() - 1; i++)
            if (cboIns.getItemAt(i).equals(nomIns)) { cboIns.setSelectedIndex(i); break; }

        String nomEmp = String.valueOf(modelo.getValueAt(fm, COL_EMPLEADO));
        for (int i = 1; i < cboEmp.getItemCount() - 1; i++)
            if (cboEmp.getItemAt(i).trim().equals(nomEmp.trim())) { cboEmp.setSelectedIndex(i); break; }
        cboEmp.setSelectedItem(modelo.getValueAt(fm, COL_EMPLEADO));
        cboIns.setSelectedItem(modelo.getValueAt(fm, COL_INSUMO));
        txtCant.setText(String.valueOf(modelo.getValueAt(fm, COL_CANT)));
        txtMot.setText(String.valueOf(modelo.getValueAt(fm, COL_MOTIVO)));
        String uni = String.valueOf(modelo.getValueAt(fm, COL_UNIDAD));
        for (int i = 0; i < cboUni.getItemCount(); i++)
            if (cboUni.getItemAt(i).equals(uni)) { cboUni.setSelectedIndex(i); break; }
        actualizarCosto.run();
    }

    // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CARD_BG);
        dlg.setContentPane(root);

        // ── Header azul ──────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(AZUL);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JPanel hLeft = new JPanel();
        hLeft.setLayout(new BoxLayout(hLeft, BoxLayout.Y_AXIS));
        hLeft.setBackground(AZUL);

        JLabel hTit = new JLabel(esNuevo ? "Registrar Nueva Merma" : "Editar Merma");
        hTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        hTit.setForeground(Color.WHITE);

        JLabel hSub = new JLabel(esNuevo
                ? "Complete todos los campos obligatorios (*)"
                : "Modifique los datos y confirme los cambios");
        hSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        hSub.setForeground(new Color(255, 255, 255, 170));

        hLeft.add(hTit);
        hLeft.add(Box.createVerticalStrut(3));
        hLeft.add(hSub);
        header.add(hLeft, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // ── Body ─────────────────────────────────────────────────────────────
        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(CARD_BG);
        body.setBorder(new EmptyBorder(20, 24, 8, 24));
        GridBagConstraints g = new GridBagConstraints();
        g.fill    = GridBagConstraints.HORIZONTAL;
        g.insets  = new Insets(5, 6, 5, 6);
        g.weightx = 0.5;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        body.add(seccionLabel("Datos del Insumo"), g);
        g.gridwidth = 1;

        g.gridx = 0; g.gridy = 1; body.add(lbl("Insumo *"),   g);
        g.gridx = 1;               body.add(lbl("Empleado *"), g);
        g.gridx = 0; g.gridy = 2;
        cboIns.setPreferredSize(new Dimension(0, 36)); body.add(cboIns, g);
        g.gridx = 1;
        cboEmp.setPreferredSize(new Dimension(0, 36)); body.add(cboEmp, g);

        g.gridx = 0; g.gridy = 3; body.add(lbl("Cantidad *"),     g);
        g.gridx = 1;               body.add(lbl("Costo Estimado"), g);

        g.gridx = 0; g.gridy = 4;
        JPanel pCant = new JPanel(new BorderLayout(6, 0));
        pCant.setBackground(CARD_BG);
        txtCant.setPreferredSize(new Dimension(0, 36));
        cboUni.setPreferredSize(new Dimension(72, 36));
        pCant.add(txtCant, BorderLayout.CENTER);
        pCant.add(cboUni,  BorderLayout.EAST);
        body.add(pCant, g);

        g.gridx = 1;
        txtCostoPreview.setPreferredSize(new Dimension(0, 36));
        body.add(txtCostoPreview, g);

        g.gridx = 0; g.gridy = 5; g.gridwidth = 2;
        g.insets = new Insets(14, 6, 4, 6);
        body.add(seccionLabel("Motivo de la Merma"), g);

        g.gridy = 6;
        g.insets = new Insets(5, 6, 5, 6);
        JScrollPane scrollMot = new JScrollPane(txtMot);
        scrollMot.setBorder(null);
        scrollMot.setPreferredSize(new Dimension(0, 100));
        body.add(scrollMot, g);
        g.gridwidth = 1;

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(null);
        bodyScroll.getViewport().setBackground(CARD_BG);
        root.add(bodyScroll, BorderLayout.CENTER);

        // ── Footer del diálogo ───────────────────────────────────────────────
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(PAGE_BG);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
                new EmptyBorder(12, 20, 12, 20)));

        int ultimaFila = tabla.getRowCount() - 1;
        int ultimo_id  = Integer.parseInt(tabla.getValueAt(ultimaFila, 0).toString());
        JLabel lblIdPreview = new JLabel(esNuevo
                ? "ID asignado: MERM-" + String.format("%04d", ultimo_id)
                : "Editando: " + modelo.getValueAt(fm, COL_MERMA_ID));
        lblIdPreview.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblIdPreview.setForeground(TEXT_MUTED);

        JPanel btnsFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnsFooter.setOpaque(false);

        JButton btnCan = btnDialogSec("Cancelar");
        JButton btnOk  = btnDialogPri(esNuevo ? "Guardar" : "Actualizar");

        btnCan.addActionListener(e -> dlg.dispose());

        btnOk.addActionListener(e -> {
            boolean ok = true;

            // Validar cantidad
            double cant = 0;
            try {
                cant = Double.parseDouble(txtCant.getText().replace(",", ".").trim());
                if (cant <= 0) throw new NumberFormatException();
                txtCant.setBackground(CARD_BG);
            } catch (NumberFormatException ex) {
                txtCant.setBackground(new Color(255, 235, 235));
                ok = false;
            }

            // Validar motivo
            if (txtMot.getText().trim().isEmpty()) {
                txtMot.setBackground(new Color(255, 235, 235));
                ok = false;
            } else {
                txtMot.setBackground(CARD_BG);
            }

            if (!ok) {
                JOptionPane.showMessageDialog(dlg,
                        "Por favor completa todos los campos marcados.",
                        "Campos requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String empleado_identificador = (String) cboEmp.getSelectedItem();
            Empleado empleado = encontrarEmpleado(empleado_identificador);
            String insumo_identificador = (String) cboIns.getSelectedItem();
            Insumo insumo = encontrarInsumo(insumo_identificador);
            String   mot   = txtMot.getText().trim();
            String   uni   = (String) cboUni.getSelectedItem();
            double   costo = cant * insumo.getCosto();

            if (!esNuevo) {
                modelo.setValueAt(ultimo_id, fm, COL_ID);
                modelo.setValueAt("MERM-"+insumo.getInsumoId(),               fm, COL_CODINSU);
                modelo.setValueAt(insumo.getNombre().toUpperCase(),               fm, COL_INSUMO);
                modelo.setValueAt(String.format("%.2f", cant),     fm, COL_CANT);
                modelo.setValueAt(uni,                              fm, COL_UNIDAD);
                modelo.setValueAt(mot,                              fm, COL_MOTIVO);
                modelo.setValueAt(empleado.getNombre().toUpperCase()+ " " + 
                        empleado.getApellidos().toUpperCase(),             fm, COL_EMPLEADO);
                modelo.setValueAt(String.format("S/ %.2f", costo), fm, COL_COSTO);
                toast("Merma actualizada correctamente ✓");
            } else {
                Merma m = new Merma();
                m.setCantidad(cant);
                m.setEmpleado(empleado);
                m.setInsumo(insumo);
                m.setFecha_registro(new Date());
                m.setMotivo(mot);
                merma_controller.registrarMerma(insumo, empleado,cant, mot, new Date());
                insumo_controller.disminuirStock(m.getInsumo().getInsumoId(), (float) cant);
                mermas.add(m);
                modelo.addRow(toRow(m));
                toast("Merma registrada correctamente ✓");
            }
            actualizarStats();
            actualizarFooter();
            dlg.dispose();
        });

        btnsFooter.add(btnCan);
        btnsFooter.add(btnOk);
        footer.add(lblIdPreview, BorderLayout.WEST);
        footer.add(btnsFooter,   BorderLayout.EAST);
        root.add(footer, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

// ── Reconstruye el combo de insumos con placeholder y opción "➕ Nuevo" ──
private void rebuilInsumoCombo(JComboBox<String> cbo,JComboBox<String> cbo2) {
    cbo.removeAllItems();
    cbo.addItem("— Seleccionar insumo —");
    for (Insumo i : insumos) cbo.addItem(nombreInsumo(i).toUpperCase());
    cbo.addItem("➕  Registrar nuevo insumo...");
    cbo.setSelectedIndex(0);
    cbo.addActionListener(e -> {

            String insumo_cbo = (String) cbo.getSelectedItem();
            Insumo insumo = encontrarInsumo(insumo_cbo);
            if (insumo != null) {
                System.out.println(insumo.getUnidadMedida());
                cbo2.setSelectedItem(
                        insumo.getUnidadMedida().toUpperCase());
                cbo2.setEnabled(false);
            
        }
    });
}

// ── Reconstruye el combo de empleados con placeholder y opción "➕ Nuevo" ─
private void rebuilEmpleadoCombo(JComboBox<String> cbo) {
    cbo.removeAllItems();
    cbo.addItem("— Seleccionar empleado —");
    for (Empleado e : empleados) cbo.addItem(nombreEmpleado(e).toUpperCase());
    cbo.addItem("➕  Registrar nuevo empleado...");
    cbo.setSelectedIndex(0);
}

// ── Sub-diálogo: Nuevo Insumo ─────────────────────────────────────────────
private Insumo dialogNuevoInsumo(JDialog parent) {
    JDialog dlg = new JDialog(parent, "Registrar Nuevo Insumo", true);
    dlg.setSize(400, 340);
    dlg.setResizable(false);
    dlg.setLocationRelativeTo(parent);

    JPanel root = new JPanel(new BorderLayout());
    root.setBackground(CARD_BG);
    dlg.setContentPane(root);

    // Header
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(AZUL);
    header.setBorder(new EmptyBorder(14, 20, 14, 20));
    JLabel hTit = new JLabel("Nuevo Insumo");
    hTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
    hTit.setForeground(Color.WHITE);
    header.add(hTit);
    root.add(header, BorderLayout.NORTH);

    // Body
    JPanel body = new JPanel(new GridBagLayout());
    body.setBackground(CARD_BG);
    body.setBorder(new EmptyBorder(18, 24, 8, 24));
    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(5, 4, 5, 4);
    gc.weightx = 1.0;
    gc.gridwidth = 2;

    JTextField txtNombre  = campoTexto("");
    JTextField txtUnidad  = campoTexto("Kg");
    JTextField txtCosto   = campoTexto("0.00");
    JTextField txtStock   = campoTexto("0");

    gc.gridx = 0; gc.gridy = 0; body.add(lbl("Nombre del insumo *"), gc);
    gc.gridy = 1; body.add(txtNombre, gc);

    gc.gridwidth = 1;
    gc.gridx = 0; gc.gridy = 2; body.add(lbl("Unidad *"), gc);
    gc.gridx = 1;               body.add(lbl("Costo unitario (S/) *"), gc);
    gc.gridx = 0; gc.gridy = 3; body.add(txtUnidad, gc);
    gc.gridx = 1;               body.add(txtCosto, gc);

    gc.gridwidth = 2;
    gc.gridx = 0; gc.gridy = 4; body.add(lbl("Stock inicial"), gc);
    gc.gridy = 5; body.add(txtStock, gc);

    root.add(body, BorderLayout.CENTER);

    // Footer
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    footer.setBackground(PAGE_BG);
    footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

    JButton btnCan = btnDialogSec("Cancelar");
    JButton btnOk  = btnDialogPri("Agregar");

    final Insumo[] resultado = {null};

    btnCan.addActionListener(e -> dlg.dispose());
    btnOk.addActionListener(e -> {
        String nombre = txtNombre.getText().trim();
        String unidad = txtUnidad.getText().trim();
        if (nombre.isEmpty() || unidad.isEmpty()) {
            JOptionPane.showMessageDialog(dlg, "Nombre y unidad son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        float costoVal = 0f;
        float stockVal = 0f;
        try { costoVal = Float.parseFloat(txtCosto.getText().replace(",", ".").trim()); }
        catch (NumberFormatException ignored) {}
        try { stockVal = Float.parseFloat(txtStock.getText().replace(",", ".").trim()); }
        catch (NumberFormatException ignored) {}

        // Genera un ID incremental simple
        int nuevoId = 100 + insumos.size();
        resultado[0] = new Insumo(nuevoId, nombre, unidad, 0.0, null, costoVal, stockVal);
        dlg.dispose();
    });

    footer.add(btnCan);
    footer.add(btnOk);
    root.add(footer, BorderLayout.SOUTH);

    dlg.setVisible(true);
    return resultado[0]; // null si canceló
}

// ── Sub-diálogo: Nuevo Empleado ───────────────────────────────────────────
private Empleado dialogNuevoEmpleado(JDialog parent) {
    JDialog dlg = new JDialog(parent, "Registrar Nuevo Empleado", true);
    dlg.setSize(400, 380);
    dlg.setResizable(false);
    dlg.setLocationRelativeTo(parent);

    JPanel root = new JPanel(new BorderLayout());
    root.setBackground(CARD_BG);
    dlg.setContentPane(root);

    // Header
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(AZUL);
    header.setBorder(new EmptyBorder(14, 20, 14, 20));
    JLabel hTit = new JLabel("Nuevo Empleado");
    hTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
    hTit.setForeground(Color.WHITE);
    header.add(hTit);
    root.add(header, BorderLayout.NORTH);

    // Body
    JPanel body = new JPanel(new GridBagLayout());
    body.setBackground(CARD_BG);
    body.setBorder(new EmptyBorder(18, 24, 8, 24));
    GridBagConstraints gc = new GridBagConstraints();
    gc.fill = GridBagConstraints.HORIZONTAL;
    gc.insets = new Insets(5, 4, 5, 4);
    gc.weightx = 1.0;

    JTextField txtNombre    = campoTexto("");
    JTextField txtApellidos = campoTexto("");
    JTextField txtDni       = campoTexto("");
    JTextField txtTel       = campoTexto("");
    JTextField txtEmail     = campoTexto("");

    gc.gridwidth = 1;
    gc.gridx = 0; gc.gridy = 0; body.add(lbl("Nombre *"),    gc);
    gc.gridx = 1;               body.add(lbl("Apellidos *"),  gc);
    gc.gridx = 0; gc.gridy = 1; body.add(txtNombre,          gc);
    gc.gridx = 1;               body.add(txtApellidos,        gc);

    gc.gridx = 0; gc.gridy = 2; body.add(lbl("DNI *"),       gc);
    gc.gridx = 1;               body.add(lbl("Teléfono"),     gc);
    gc.gridx = 0; gc.gridy = 3; body.add(txtDni,             gc);
    gc.gridx = 1;               body.add(txtTel,              gc);

    gc.gridwidth = 2;
    gc.gridx = 0; gc.gridy = 4; body.add(lbl("Email"),       gc);
    gc.gridy = 5;               body.add(txtEmail,            gc);

    root.add(body, BorderLayout.CENTER);

    // Footer
    JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
    footer.setBackground(PAGE_BG);
    footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

    JButton btnCan = btnDialogSec("Cancelar");
    JButton btnOk  = btnDialogPri("Agregar");

    final Empleado[] resultado = {null};

    btnCan.addActionListener(e -> dlg.dispose());
    btnOk.addActionListener(e -> {
        String nombre    = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String dni       = txtDni.getText().trim();
        if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(dlg, "Nombre, apellidos y DNI son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int nuevoId = 100 + empleados.size();
        Empleado emp = new Empleado();
        setPersonaFields(emp, nuevoId, nombre, apellidos, dni,
                txtTel.getText().trim(), txtEmail.getText().trim());
        resultado[0] = emp;
        dlg.dispose();
    });

    footer.add(btnCan);
    footer.add(btnOk);
    root.add(footer, BorderLayout.SOUTH);

    dlg.setVisible(true);
    return resultado[0];
}

    private JLabel seccionLabel(String texto) {
        JLabel l = new JLabel(texto.toUpperCase());
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));
        l.setForeground(AZUL);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 218, 250)),
                new EmptyBorder(0, 0, 4, 0)));
        return l;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ELIMINAR
    // ═════════════════════════════════════════════════════════════════════════
    private void confirmarEliminar(int fm) {
        String id = String.valueOf(modelo.getValueAt(fm, COL_MERMA_ID));
        if (JOptionPane.showConfirmDialog(this,
                "¿Eliminar la merma " + id + "?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
        modelo.removeRow(fm);
        actualizarStats(); actualizarFooter();
        toast("Merma eliminada correctamente");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  STATS + FILTRO + FOOTER
    // ═════════════════════════════════════════════════════════════════════════
    private void actualizarStats() {
        if (modelo == null) return;
        double totalKg = 0, totalCosto = 0;
        Map<String, Double> porInsumo = new LinkedHashMap<>();
        for (int i = 0; i < modelo.getRowCount(); i++) {
            try {
                double cant = Double.parseDouble(String.valueOf(modelo.getValueAt(i, COL_CANT)));
                totalKg += cant;
                String cs = String.valueOf(modelo.getValueAt(i, COL_COSTO))
                        .replace("S/", "").replace("s/", "").trim();
                totalCosto += Double.parseDouble(cs);
                porInsumo.merge(String.valueOf(modelo.getValueAt(i, COL_INSUMO)), cant, Double::sum);
            } catch (Exception ignored) {}
        }
        String masInsumo = porInsumo.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse("—");

        if (lblTotalKg   != null) lblTotalKg  .setText(String.format("%d ", modelo.getRowCount()));
        if (lblNumMermas != null) lblNumMermas.setText(String.valueOf(modelo.getRowCount()));
        if (lblInsumoMas != null) lblInsumoMas.setText(masInsumo);
        if (lblCostoEst  != null) lblCostoEst .setText(String.format("S/ %.2f", totalCosto));
    }

    private void filtrar() {
        if (sorter == null || txtBuscar == null) return;
        String txt = txtBuscar.getText().trim();
        sorter.setRowFilter(txt.isEmpty() ? null
                : RowFilter.regexFilter("(?i)" + txt,
                  COL_MERMA_ID, COL_INSUMO, COL_MOTIVO, COL_EMPLEADO));
        actualizarFooter();
    }

    private void actualizarFooter() {
        if (tabla == null || lblFooter == null) return;
        lblFooter.setText("Mostrando " + tabla.getRowCount()
                + " de " + modelo.getRowCount() + " datos");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  ROW HELPER — sin estado
    // ═════════════════════════════════════════════════════════════════════════
    private Object[] toRow(Merma m) {
        Empleado empleado = empleado_controller.obtenerPorId(m.getEmpleado().getId());
        Insumo insumo = insumo_controller.obtenerPorId(m.getInsumo().getInsumoId());
        System.out.println(m.toString());
        System.out.println(empleado);
        System.out.println(insumo);
        float costo = (float) (m.getCantidad() * insumo.getCosto());
        
        return new Object[]{    
            m.getMermaId(),
            "MERM-" + String.format("%04d", m.getMermaId()),
            "INS-" + String.format("%04d", insumo.getInsumoId()),
            insumo.getNombre().toUpperCase(),
            String.format("%.2f", m.getCantidad()),
            insumo.getUnidadMedida(),
            m.getMotivo(),
            empleado.getNombre().toUpperCase() + " "+ empleado.getApellidos().toUpperCase(),
            new SimpleDateFormat("dd/MM/yyyy HH:mm").format(m.getFecha_registro()),
            String.format("S/ %.2f", costo),
            ""
        };
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TOAST
    // ═════════════════════════════════════════════════════════════════════════
    private void toast(String msg) {
        JLabel lbl = new JLabel(msg);
        lbl.setOpaque(true); lbl.setBackground(new Color(235, 244, 255));
        lbl.setForeground(AZUL); lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(174, 204, 252), 1, true),
                new EmptyBorder(10, 14, 10, 14)));
        JDialog d = new JDialog(this, false);
        d.setUndecorated(true); d.add(lbl); d.pack();
        d.setLocation(getX() + getWidth() - d.getWidth() - 40, getY() + 80);
        d.setVisible(true);
        javax.swing.Timer t = new javax.swing.Timer(2000, ev -> d.dispose());
        t.setRepeats(false); t.start();
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  BUILDERS COMPONENTES
    // ═════════════════════════════════════════════════════════════════════════
    private JLabel lbl(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12)); l.setForeground(TEXT_DARK);
        return l;
    }

    private JTextField campoTexto(String texto) {
        JTextField tf = new JTextField(texto);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12)); tf.setForeground(TEXT_DARK);
        tf.setPreferredSize(new Dimension(0, 34));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR, 1, true),
                new EmptyBorder(0, 8, 0, 8)));
        return tf;
    }

    private void estilizarCombo(JComboBox<?> cb) {
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 12)); cb.setBackground(CARD_BG);
    }

    private JButton btnPrimario(String txt, int ancho, Color bg, Color hov) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(ancho, 40));
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(bg.darker(), 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(hov); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg);  }
        });
        return b;
    }

    private JButton btnSecundario(String txt, int ancho) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(ancho, 40));
        b.setBackground(CARD_BG); b.setForeground(AZUL);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(AZUL, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_CLAR); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD_BG);   }
        });
        return b;
    }

    private JButton btnDialogPri(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(120, 36));
        b.setBackground(AZUL); b.setForeground(Color.WHITE);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(AZUL, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_HOV); }
            public void mouseExited (MouseEvent e) { b.setBackground(AZUL);     }
        });
        return b;
    }

    private JButton btnDialogSec(String txt) {
        JButton b = new JButton(txt);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setPreferredSize(new Dimension(110, 36));
        b.setBackground(CARD_BG); b.setForeground(AZUL);
        b.setOpaque(true); b.setContentAreaFilled(true); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createLineBorder(AZUL, 1, true));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(AZUL_CLAR); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD_BG);   }
        });
        return b;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  RENDERERS
    // ═════════════════════════════════════════════════════════════════════════

    // AccRenderer: solo lápiz (editar) y ✕ (eliminar)
    static class AccRenderer extends JPanel implements TableCellRenderer {
        AccRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8)); setOpaque(true); }

        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            removeAll();
            setBackground(sel ? t.getSelectionBackground() : Color.WHITE);
            add(iconBtn("✎", new Color(232, 241, 255), new Color(26,  83, 160)));
            add(iconBtn("✕", new Color(255, 230, 230), new Color(180, 40,  40)));
            return this;
        }

        private JLabel iconBtn(String ico, Color bg, Color fg) {
            JLabel l = new JLabel(ico, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.BOLD, 13));
            l.setOpaque(true);
            l.setBackground(bg);
            l.setForeground(fg);
            l.setPreferredSize(new Dimension(28, 24));
            l.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bg.darker(), 1, true),
                    new EmptyBorder(2, 5, 2, 5)));
            l.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return l;
        }
    }

    static class Pill extends JLabel {
        private final Color bg;
        Pill(String txt, Color bg) {
            super(txt, SwingConstants.CENTER); this.bg = bg;
            setForeground(Color.WHITE); setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBorder(new EmptyBorder(3, 12, 3, 12)); setOpaque(false);
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            g2.dispose(); super.paintComponent(g);
        }
    }
    
    private Empleado encontrarEmpleado(String identificador){
        for(Empleado emp : empleados){
            String str_emp = emp.getNombre().toUpperCase() + " " + emp.getApellidos().toUpperCase();
            if(str_emp.equals(identificador.toUpperCase())){
                return emp;
            }
        }
        return null;
    }
    
    private Insumo encontrarInsumo(String identificador){
        for(Insumo ins : insumos){
            String str_ins = ins.getNombre().toUpperCase();
            if(str_ins.equals(identificador.toUpperCase())){
                return ins;
            }
        }
        return null;
    }
    
    // ═════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ═════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new MermaFrame().setVisible(true);
        });
    }
}


