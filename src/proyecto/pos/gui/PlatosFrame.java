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
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;

public class PlatosFrame extends JFrame {

    // ─── COLORES ─────────────────────────────────────────────────────────────
    private static final Color AZUL         = new Color(26, 83, 160);
    private static final Color AZUL_HOVER   = new Color(18, 65, 128);
    private static final Color AZUL_CLARO   = new Color(232, 241, 255);
    private static final Color FONDO        = new Color(246, 248, 251);
    private static final Color BORDE        = new Color(225, 229, 236);
    private static final Color TEXTO        = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE  = new Color(105, 113, 128);
    private static final Color VERDE        = new Color(40, 167, 69);
    private static final Color VERDE_BG     = new Color(225, 245, 238);
    private static final Color VERDE_TEXT   = new Color(15, 110, 86);
    private static final Color ROJO         = new Color(220, 53, 69);
    private static final Color NARANJA      = new Color(255, 145, 77);
    private static final Color NARANJA_BG   = new Color(255, 243, 230);
    private static final Color NARANJA_TEXT = new Color(150, 80, 0);
    private static final Color GRIS         = new Color(150, 157, 168);
    private static final Color AMARILLO     = new Color(255, 193, 7);

    // ─── COLUMNAS (sin Costo ni Margen) ──────────────────────────────────────
    private static final int COL_ID         = 0;
    private static final int COL_NOMBRE     = 1;
    private static final int COL_CATEGORIA  = 2;
    private static final int COL_PRECIO     = 3;
    private static final int COL_DISPONIBLE = 4;
    private static final int COL_ULTIMA_MOD = 5;
    private static final int COL_POPULAR    = 6;
    private static final int COL_ACCIONES   = 7;

    private static final String[] CATEGORIAS = {"Todos", "Entradas", "Segundos", "Postres", "Bebidas"};

    // ─── COMPONENTES ─────────────────────────────────────────────────────────
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JLabel lblFooter;
    private String categoriaActiva = "Todos";

    // Formulario lateral
    private JTextField        txtIdPlato;
    private JTextField        txtNombrePlato;
    private JComboBox<String> cboCategoriaForm;
    private JTextField        txtPrecio;
    private JTextArea         txtDescripcionPlato;   // ← NUEVO
    private JCheckBox         chkDisponible;
    private JLabel            lblEstadoBadge;
    private JButton           btnGuardar;

    private int nextId = 106;

    // Recetas en memoria: idPlato → lista de [ingrediente, cantidad, unidad]
    private final Map<Integer, List<String[]>> recetasPorPlato       = new HashMap<>();
    // Descripciones en memoria: idPlato → descripción                  ← NUEVO
    private final Map<Integer, String>         descripcionesPorPlato  = new HashMap<>();

    public PlatosFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        actualizarFooter();
    }

    private void configurarVentana() {
        setTitle("Gestión de Platos");
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        root.add(new MenuSidebar(this, "Platos"), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    // ─── CONTENIDO ───────────────────────────────────────────────────────────

    private JPanel crearContenido() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(26, 28, 24, 28));
        contenedor.add(crearHeader(), BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(18, 0));
        cuerpo.setBackground(FONDO);
        cuerpo.add(crearPanelFormulario(), BorderLayout.WEST);
        cuerpo.add(crearPanelTabla(),      BorderLayout.CENTER);
        contenedor.add(cuerpo, BorderLayout.CENTER);
        return contenedor;
    }

    // ─── HEADER ──────────────────────────────────────────────────────────────

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Gestión de Platos del Menú");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Administra precios, categorías y disponibilidad de platos");
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
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 14, 6, 14)));
        tarjeta.setPreferredSize(new Dimension(110, 40));

        JLabel lblTitulo = new JLabel("Hora");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(TEXTO);

        JLabel lblHora = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHora.setForeground(TEXTO_SUAVE);

        new Timer(1000, e -> lblHora.setText(
                new SimpleDateFormat("HH:mm:ss").format(new Date()))).start();

        tarjeta.add(lblTitulo);
        tarjeta.add(lblHora);
        return tarjeta;
    }

    private JPanel crearTarjetaUsuario() {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 10, 6, 12)));
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

    // ─── FORMULARIO (izquierda) ───────────────────────────────────────────────

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(20, 18, 20, 18)));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);

        JLabel t1 = new JLabel("Formulario de Gestión");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t1.setForeground(TEXTO);
        t1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel t2 = new JLabel("de Platos");
        t2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t2.setForeground(TEXTO);
        t2.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(t1);
        form.add(t2);
        form.add(Box.createVerticalStrut(20));

        // ID (solo lectura)
        form.add(crearLabel("ID Plato"));
        form.add(Box.createVerticalStrut(5));
        txtIdPlato = crearTextField("(nuevo)");
        txtIdPlato.setEnabled(false);
        txtIdPlato.setBackground(new Color(245, 246, 248));
        form.add(txtIdPlato);
        form.add(Box.createVerticalStrut(14));

        // Nombre
        form.add(crearLabel("Nombre del Plato"));
        form.add(Box.createVerticalStrut(5));
        txtNombrePlato = crearTextField("Escribe el nombre...");
        form.add(txtNombrePlato);
        form.add(Box.createVerticalStrut(14));

        // Categoría
        form.add(crearLabel("Categoría"));
        form.add(Box.createVerticalStrut(5));
        cboCategoriaForm = new JComboBox<>(new String[]{
            "Seleccionar Categoría", "Entradas", "Segundos", "Postres", "Bebidas"
        });
        cboCategoriaForm.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboCategoriaForm.setBackground(Color.WHITE);
        cboCategoriaForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        cboCategoriaForm.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboCategoriaForm.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(2, 8, 2, 8)));
        form.add(cboCategoriaForm);
        form.add(Box.createVerticalStrut(14));

        // Precio
        form.add(crearLabel("Precio (S/.)"));
        form.add(Box.createVerticalStrut(5));
        txtPrecio = crearTextField("0.00");
        form.add(txtPrecio);
        form.add(Box.createVerticalStrut(14));

        // ── Descripción del Plato ── NUEVO ────────────────────────────────────
        form.add(crearLabel("Descripción del Plato"));
        form.add(Box.createVerticalStrut(5));
        txtDescripcionPlato = new JTextArea(3, 1);
        txtDescripcionPlato.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescripcionPlato.setForeground(TEXTO);
        txtDescripcionPlato.setLineWrap(true);
        txtDescripcionPlato.setWrapStyleWord(true);
        txtDescripcionPlato.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(8, 10, 8, 10)));
        JScrollPane scrollDesc = new JScrollPane(txtDescripcionPlato);
        scrollDesc.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        scrollDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        scrollDesc.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(scrollDesc);
        form.add(Box.createVerticalStrut(14));
        // ─────────────────────────────────────────────────────────────────────

        // Disponible + badge
        JPanel panelDisp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelDisp.setBackground(Color.WHITE);
        panelDisp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panelDisp.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkDisponible = new JCheckBox("Disponible");
        chkDisponible.setSelected(true);
        chkDisponible.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkDisponible.setBackground(Color.WHITE);
        chkDisponible.setForeground(TEXTO);
        chkDisponible.addActionListener(e -> actualizarBadge());

        lblEstadoBadge = new JLabel("ACTIVO");
        lblEstadoBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstadoBadge.setOpaque(true);
        lblEstadoBadge.setBackground(VERDE_BG);
        lblEstadoBadge.setForeground(VERDE_TEXT);
        lblEstadoBadge.setBorder(new EmptyBorder(4, 10, 4, 10));

        panelDisp.add(chkDisponible);
        panelDisp.add(Box.createHorizontalStrut(8));
        panelDisp.add(lblEstadoBadge);
        form.add(panelDisp);
        form.add(Box.createVerticalStrut(22));

        // Botón Guardar
        btnGuardar = new JButton("  GUARDAR CAMBIOS");
        btnGuardar.setIcon(MenuSidebar.redimensionarIcono("/img/guardar.png", 16, 16));
        btnGuardar.setIconTextGap(8);
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(AZUL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.setBorder(new EmptyBorder(12, 14, 12, 14));
        btnGuardar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnGuardar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnGuardar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnGuardar.setBackground(AZUL_HOVER); }
            public void mouseExited(MouseEvent e)  { btnGuardar.setBackground(AZUL); }
        });
        btnGuardar.addActionListener(e -> guardarPlato());
        form.add(btnGuardar);

        // Botón Nuevo
        form.add(Box.createVerticalStrut(10));
        JButton btnNuevo = new JButton("+ Nuevo Plato");
        btnNuevo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnNuevo.setBackground(Color.WHITE);
        btnNuevo.setForeground(AZUL);
        btnNuevo.setFocusPainted(false);
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(8, 14, 8, 14)));
        btnNuevo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnNuevo.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnNuevo.addActionListener(e -> limpiarFormulario());
        form.add(btnNuevo);

        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(TEXTO_SUAVE);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JTextField crearTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setForeground(TEXTO);
        tf.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(8, 10, 8, 10)));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    // ─── TABLA (derecha) ─────────────────────────────────────────────────────

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)));
        panel.add(crearCabeceraTabla(), BorderLayout.NORTH);
        panel.add(crearTabla(),         BorderLayout.CENTER);
        panel.add(crearFooter(),        BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearCabeceraTabla() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 0, 14, 0));

        JPanel top = new JPanel(new BorderLayout(12, 0));
        top.setOpaque(false);

        JLabel lblTitulo = new JLabel("Lista de Platos del Menú");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitulo.setForeground(TEXTO);

        top.add(lblTitulo,      BorderLayout.WEST);
        top.add(crearBuscador(), BorderLayout.EAST);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(12, 0, 0, 0));
        for (String cat : CATEGORIAS) filtros.add(crearBotonCategoria(cat, filtros));

        wrapper.add(top,     BorderLayout.NORTH);
        wrapper.add(filtros, BorderLayout.CENTER);
        return wrapper;
    }

    private JButton crearBotonCategoria(String categoria, JPanel contenedor) {
        boolean sel = categoria.equals(categoriaActiva);
        JButton btn = new JButton("[ " + categoria + " ]");
        btn.setFont(new Font("Segoe UI", sel ? Font.BOLD : Font.PLAIN, 12));
        btn.setBackground(sel ? AZUL_CLARO : Color.WHITE);
        btn.setForeground(sel ? AZUL : TEXTO_SUAVE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(sel ? AZUL : BORDE, 20),
                new EmptyBorder(6, 14, 6, 14)));
        btn.addActionListener(e -> {
            categoriaActiva = categoria;
            aplicarFiltros();
            contenedor.removeAll();
            for (String cat : CATEGORIAS) contenedor.add(crearBotonCategoria(cat, contenedor));
            contenedor.revalidate();
            contenedor.repaint();
        });
        return btn;
    }

    private JPanel crearBuscador() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(260, 36));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 10, 0, 10)));

        JLabel icono = new JLabel(MenuSidebar.redimensionarIcono("/img/Lupa.png", 15, 15));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXTO);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar...");
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void removeUpdate(DocumentEvent e)  { aplicarFiltros(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltros(); }
        });

        panel.add(icono,      BorderLayout.WEST);
        panel.add(txtBuscar,  BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTabla() {
        String[] columnas = {
            "ID", "Nombre", "Categoría", "Precio (S/.)",
            "Disponibilidad", "Última Modif.", "Pop.", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(40);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setShowVerticalLines(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(new Color(235, 238, 244));
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(TEXTO);
        tabla.setFillsViewportHeight(true);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setForeground(new Color(45, 52, 65));
        th.setBackground(Color.WHITE);
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 40));
        th.setReorderingAllowed(false);

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        int[] anchos = {55, 210, 110, 100, 120, 110, 65, 110};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        tabla.getColumnModel().getColumn(COL_ID).setCellRenderer(new TextoCentradoRenderer());
        tabla.getColumnModel().getColumn(COL_PRECIO).setCellRenderer(new TextoCentradoRenderer());
        tabla.getColumnModel().getColumn(COL_DISPONIBLE).setCellRenderer(new DisponibleRenderer());
        tabla.getColumnModel().getColumn(COL_ULTIMA_MOD).setCellRenderer(new TextoCentradoRenderer());
        tabla.getColumnModel().getColumn(COL_POPULAR).setCellRenderer(new PopularRenderer());
        tabla.getColumnModel().getColumn(COL_ACCIONES).setCellRenderer(new AccionesRenderer());

        // ── LISTENER DE CLIC ──────────────────────────────────────────────────
        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fv = tabla.rowAtPoint(e.getPoint());
                int cv = tabla.columnAtPoint(e.getPoint());
                if (fv < 0 || cv < 0) return;

                int fm = tabla.convertRowIndexToModel(fv);

                // Doble clic en cualquier parte → ventana de receta
                if (e.getClickCount() == 2) {
                    abrirVentanaReceta(fm);
                    return;
                }

                // Clic simple sólo en columna Acciones
                if (tabla.convertColumnIndexToModel(cv) == COL_ACCIONES) {
                    int xRel  = e.getX() - tabla.getCellRect(fv, cv, true).x;
                    int ancho = tabla.getColumnModel().getColumn(cv).getWidth();
                    if (xRel < ancho / 2) cargarEnFormulario(fm);
                    else                   eliminarPlato(fm);
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

        JLabel ayuda = new JLabel("Doble clic → ver receta  ·  ✎ editar  ·  🗑 eliminar");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        ayuda.setForeground(GRIS);

        footer.add(lblFooter, BorderLayout.WEST);
        footer.add(ayuda,     BorderLayout.EAST);
        return footer;
    }

    // ─── DATOS DEMO ───────────────────────────────────────────────────────────

    private void cargarDatosDemo() {
        String hoy = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        agregarFila(101, "Arroz con Pollo",    "Segundos", 15.00f, true,  hoy, 2);
        agregarFila(102, "Ceviche", "Entradas", 18.00f, true,  hoy, 2);
        agregarFila(103, "Inka Kola",          "Bebidas",   5.00f, true,  hoy, 1);
        agregarFila(104, "Tallarín Saltado",   "Segundos", 14.50f, false, hoy, 2);
        agregarFila(105, "Tres Leches",        "Postres",  10.00f, true,  hoy, 3);

        // Recetas demo
        recetasPorPlato.put(101, new ArrayList<>(Arrays.asList(
            new String[]{"Pollo entero",    "300", "g"},
            new String[]{"Arroz",           "200", "g"},
            new String[]{"Ají amarillo",    "2",   "unid"},
            new String[]{"Caldo de pollo",  "250", "ml"}
        )));
        recetasPorPlato.put(102, new ArrayList<>(Arrays.asList(
            new String[]{"Pescado fresco",  "250", "g"},
            new String[]{"Limón",           "6",   "unid"},
            new String[]{"Cebolla roja",    "1",   "unid"},
            new String[]{"Ají limo",        "1",   "unid"},
            new String[]{"Cilantro",        "10",  "g"}
        )));
        recetasPorPlato.put(105, new ArrayList<>(Arrays.asList(
            new String[]{"Bizcocho",          "150", "g"},
            new String[]{"Leche evaporada",   "200", "ml"},
            new String[]{"Crema de leche",    "100", "ml"},
            new String[]{"Azúcar",            "50",  "g"}
        )));

        // ── Descripciones demo ── NUEVO ───────────────────────────────────────
        descripcionesPorPlato.put(101, "Arroz cocido con trozos de pollo guisado en salsa de ají amarillo y verduras.");
        descripcionesPorPlato.put(102, "Pescado fresco marinado en limón con cebolla, ají limo y cilantro.");
        descripcionesPorPlato.put(103, "Bebida gaseosa de sabor característico peruano.");
        descripcionesPorPlato.put(104, "Tallarines salteados con carne, tomate, cebolla y sillao.");
        descripcionesPorPlato.put(105, "Bizcocho bañado en tres tipos de leche con crema chantilly.");
        // ─────────────────────────────────────────────────────────────────────
    }

    private void agregarFila(int id, String nombre, String categoria,
                              float precio, boolean disponible, String fecha, int popularidad) {
        modeloTabla.addRow(new Object[]{
            id, nombre, categoria,
            String.format("S/ %.2f", precio),
            disponible ? "ACTIVO" : "INACTIVO",
            fecha, popularidad, ""
        });
    }

    // ─── VENTANA RECETA ───────────────────────────────────────────────────────

    private void abrirVentanaReceta(int filaModelo) {
        int    idPlato   = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
        String nombre    = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE));
        String categoria = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_CATEGORIA));
        String precio    = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_PRECIO));

        List<String[]> receta = recetasPorPlato.getOrDefault(idPlato, new ArrayList<>());

        JDialog dialog = new JDialog(this, "Receta: " + nombre, true);
        dialog.setSize(480, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        // ── Encabezado azul ──
        JPanel enc = new JPanel(new BorderLayout());
        enc.setBackground(AZUL);
        enc.setBorder(new EmptyBorder(16, 20, 16, 20));

        JPanel encTextos = new JPanel();
        encTextos.setOpaque(false);
        encTextos.setLayout(new BoxLayout(encTextos, BoxLayout.Y_AXIS));

        JLabel lblNombreRec = new JLabel(nombre);
        lblNombreRec.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblNombreRec.setForeground(Color.WHITE);

        JLabel lblSubRec = new JLabel(categoria + "  ·  " + precio);
        lblSubRec.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubRec.setForeground(new Color(180, 210, 255));

        encTextos.add(lblNombreRec);
        encTextos.add(Box.createVerticalStrut(3));
        encTextos.add(lblSubRec);

        JLabel btnX = new JLabel("✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btnX.setForeground(Color.WHITE);
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
        });

        enc.add(encTextos, BorderLayout.WEST);
        enc.add(btnX,      BorderLayout.EAST);

        // ── Tabla de ingredientes ──
        String[] colsRec = {"Ingrediente", "Cantidad", "Unidad"};
        DefaultTableModel modeloReceta = new DefaultTableModel(colsRec, 0) {
            public boolean isCellEditable(int r, int c) { return true; }
        };
        for (String[] ing : receta)
            modeloReceta.addRow(new Object[]{ing[0], ing[1], ing[2]});

        JTable tablaReceta = new JTable(modeloReceta);
        tablaReceta.setRowHeight(36);
        tablaReceta.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReceta.setShowVerticalLines(false);
        tablaReceta.setShowHorizontalLines(true);
        tablaReceta.setGridColor(new Color(235, 238, 244));
        tablaReceta.setSelectionBackground(AZUL_CLARO);
        tablaReceta.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaReceta.getTableHeader().setBackground(new Color(248, 249, 252));
        tablaReceta.getTableHeader().setForeground(TEXTO_SUAVE);
        tablaReceta.getColumnModel().getColumn(0).setPreferredWidth(190);
        tablaReceta.getColumnModel().getColumn(1).setPreferredWidth(80);
        tablaReceta.getColumnModel().getColumn(2).setPreferredWidth(80);

        JScrollPane scrollRec = new JScrollPane(tablaReceta);
        scrollRec.setBorder(new MenuSidebar.RoundedBorder(BORDE, 8));
        scrollRec.getViewport().setBackground(Color.WHITE);

        // ── Sección ingredientes ──
        JPanel secIngredientes = new JPanel(new BorderLayout());
        secIngredientes.setBackground(Color.WHITE);
        secIngredientes.setBorder(new EmptyBorder(18, 20, 10, 20));

        JLabel lblTitIng = new JLabel("Ingredientes de la receta");
        lblTitIng.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitIng.setForeground(TEXTO);
        lblTitIng.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel hint = new JLabel("Puedes editar los valores directamente en la tabla");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(GRIS);
        hint.setBorder(new EmptyBorder(8, 0, 0, 0));

        secIngredientes.add(lblTitIng, BorderLayout.NORTH);
        secIngredientes.add(scrollRec, BorderLayout.CENTER);
        secIngredientes.add(hint,      BorderLayout.SOUTH);

        // ── Fila agregar ingrediente ──
        JPanel panelAgregar = new JPanel(new BorderLayout(8, 0));
        panelAgregar.setBackground(new Color(248, 249, 252));
        panelAgregar.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel lblAgregar = new JLabel("Agregar: ");
        lblAgregar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAgregar.setForeground(TEXTO_SUAVE);

        JTextField txtIngNombre   = new JTextField();
        JTextField txtIngCantidad = new JTextField();
        JTextField txtIngUnidad   = new JTextField();

        estilizarCampoReceta(txtIngNombre,   "Ingrediente");
        estilizarCampoReceta(txtIngCantidad, "Cant.");
        estilizarCampoReceta(txtIngUnidad,   "Unidad");
        txtIngCantidad.setPreferredSize(new Dimension(60, 30));
        txtIngUnidad.setPreferredSize(new Dimension(65, 30));

        JButton btnAgrIng = new JButton("+");
        btnAgrIng.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgrIng.setBackground(AZUL);
        btnAgrIng.setForeground(Color.WHITE);
        btnAgrIng.setFocusPainted(false);
        btnAgrIng.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgrIng.setBorder(new EmptyBorder(5, 14, 5, 14));
        btnAgrIng.setPreferredSize(new Dimension(40, 30));
        btnAgrIng.addActionListener(e -> {
            String n = txtIngNombre.getText().trim();
            String c = txtIngCantidad.getText().trim();
            String u = txtIngUnidad.getText().trim();
            if (!n.isEmpty()) {
                modeloReceta.addRow(new Object[]{n, c, u});
                txtIngNombre.setText("");
                txtIngCantidad.setText("");
                txtIngUnidad.setText("");
                txtIngNombre.requestFocus();
            }
        });

        JPanel camposIng = new JPanel(new BorderLayout(6, 0));
        camposIng.setOpaque(false);
        camposIng.add(txtIngNombre, BorderLayout.CENTER);

        JPanel derechaIng = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derechaIng.setOpaque(false);
        derechaIng.add(txtIngCantidad);
        derechaIng.add(txtIngUnidad);
        derechaIng.add(btnAgrIng);
        camposIng.add(derechaIng, BorderLayout.EAST);

        panelAgregar.add(lblAgregar, BorderLayout.WEST);
        panelAgregar.add(camposIng,  BorderLayout.CENTER);

        // ── Botones pie ──
        JPanel botones = new JPanel(new BorderLayout(10, 0));
        botones.setBackground(Color.WHITE);
        botones.setBorder(new EmptyBorder(12, 20, 18, 20));

        JButton btnCerrar = crearBotonDialog("Cerrar", false);
        btnCerrar.addActionListener(e -> dialog.dispose());

        JButton btnGuardarRec = crearBotonDialog("Guardar Receta", true);
        btnGuardarRec.addActionListener(e -> {
            if (tablaReceta.isEditing())
                tablaReceta.getCellEditor().stopCellEditing();

            List<String[]> nuevaReceta = new ArrayList<>();
            for (int i = 0; i < modeloReceta.getRowCount(); i++) {
                nuevaReceta.add(new String[]{
                    String.valueOf(modeloReceta.getValueAt(i, 0)),
                    String.valueOf(modeloReceta.getValueAt(i, 1)),
                    String.valueOf(modeloReceta.getValueAt(i, 2))
                });
            }
            recetasPorPlato.put(idPlato, nuevaReceta);
            dialog.dispose();
            mostrarToast("✔  Receta de \"" + nombre + "\" guardada");
        });

        botones.add(btnCerrar,     BorderLayout.WEST);
        botones.add(btnGuardarRec, BorderLayout.EAST);

        // ── Ensamblar ──
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setBackground(Color.WHITE);
        centro.add(secIngredientes, BorderLayout.CENTER);
        centro.add(panelAgregar,    BorderLayout.SOUTH);

        root.add(enc,     BorderLayout.NORTH);
        root.add(centro,  BorderLayout.CENTER);
        root.add(botones, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.setVisible(true);
    }

    private void estilizarCampoReceta(JTextField tf, String placeholder) {
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 6),
                new EmptyBorder(5, 8, 5, 8)));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
    }

    private JButton crearBotonDialog(String texto, boolean primario) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (primario) {
            btn.setBackground(AZUL);
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(TEXTO);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    new MenuSidebar.RoundedBorder(BORDE, 8),
                    new EmptyBorder(10, 20, 10, 20)));
        }
        return btn;
    }

    // ─── LÓGICA FORMULARIO ────────────────────────────────────────────────────

    private void guardarPlato() {
        String nombre      = txtNombrePlato.getText().trim();
        String catSel      = String.valueOf(cboCategoriaForm.getSelectedItem());
        String precioStr   = txtPrecio.getText().replace("S/.", "").replace("S/", "").trim();
        String descripcion = txtDescripcionPlato.getText().trim();   // ← NUEVO

        if (nombre.isEmpty() || catSel.equals("Seleccionar Categoría")) {
            mostrarToast("⚠  Completa nombre y categoría");
            return;
        }

        float precio = 0f;
        try { precio = Float.parseFloat(precioStr); } catch (NumberFormatException ignored) {}

        boolean disponible = chkDisponible.isSelected();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String idStr = txtIdPlato.getText().trim();

        int idEdicion = -1;
        try { idEdicion = Integer.parseInt(idStr); } catch (NumberFormatException ignored) {}

        if (idEdicion > 0) {
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (Integer.parseInt(String.valueOf(modeloTabla.getValueAt(i, COL_ID))) == idEdicion) {
                    modeloTabla.setValueAt(nombre,                             i, COL_NOMBRE);
                    modeloTabla.setValueAt(catSel,                             i, COL_CATEGORIA);
                    modeloTabla.setValueAt(String.format("S/ %.2f", precio),   i, COL_PRECIO);
                    modeloTabla.setValueAt(disponible ? "ACTIVO" : "INACTIVO", i, COL_DISPONIBLE);
                    modeloTabla.setValueAt(fecha,                              i, COL_ULTIMA_MOD);
                    descripcionesPorPlato.put(idEdicion, descripcion);          // ← NUEVO
                    aplicarFiltros();
                    mostrarToast("✔  Plato actualizado");
                    limpiarFormulario();
                    return;
                }
            }
        }

        agregarFila(nextId, nombre, catSel, precio, disponible, fecha, 1);
        descripcionesPorPlato.put(nextId, descripcion);                         // ← NUEVO
        nextId++;
        aplicarFiltros();
        mostrarToast("✔  Plato agregado");
        limpiarFormulario();
    }

    private void cargarEnFormulario(int filaModelo) {
        txtIdPlato.setText(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
        txtNombrePlato.setText(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE)));
        cboCategoriaForm.setSelectedItem(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_CATEGORIA)));
        txtPrecio.setText(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_PRECIO))
                .replace("S/", "").trim());
        boolean activo = "ACTIVO".equalsIgnoreCase(
                String.valueOf(modeloTabla.getValueAt(filaModelo, COL_DISPONIBLE)));
        chkDisponible.setSelected(activo);
        actualizarBadge();

        // ── Cargar descripción ── NUEVO ───────────────────────────────────────
        int idCargado = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
        txtDescripcionPlato.setText(descripcionesPorPlato.getOrDefault(idCargado, ""));
        // ─────────────────────────────────────────────────────────────────────
    }

    private void limpiarFormulario() {
        txtIdPlato.setText("(nuevo)");
        txtNombrePlato.setText("");
        cboCategoriaForm.setSelectedIndex(0);
        txtPrecio.setText("");
        txtDescripcionPlato.setText("");   // ← NUEVO
        chkDisponible.setSelected(true);
        actualizarBadge();
    }

    private void eliminarPlato(int filaModelo) {
        String nombre = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE));
        int op = JOptionPane.showConfirmDialog(
                this, "¿Eliminar el plato \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
            modeloTabla.removeRow(filaModelo);
            recetasPorPlato.remove(id);
            descripcionesPorPlato.remove(id);   // ← NUEVO
            aplicarFiltros();
            mostrarToast("✔  Plato eliminado");
        }
    }

    private void actualizarBadge() {
        if (chkDisponible.isSelected()) {
            lblEstadoBadge.setText("ACTIVO");
            lblEstadoBadge.setBackground(VERDE_BG);
            lblEstadoBadge.setForeground(VERDE_TEXT);
        } else {
            lblEstadoBadge.setText("INACTIVO");
            lblEstadoBadge.setBackground(NARANJA_BG);
            lblEstadoBadge.setForeground(NARANJA_TEXT);
        }
    }

    // ─── FILTROS ─────────────────────────────────────────────────────────────

    private void aplicarFiltros() {
        if (sorter == null) return;
        String texto = txtBuscar != null ? txtBuscar.getText().trim() : "";
        List<RowFilter<DefaultTableModel, Object>> filtros = new ArrayList<>();

        if (!texto.isEmpty())
            filtros.add(RowFilter.regexFilter("(?i)" + Pattern.quote(texto),
                    COL_ID, COL_NOMBRE, COL_CATEGORIA));

        if (!"Todos".equals(categoriaActiva))
            filtros.add(RowFilter.regexFilter("^" + Pattern.quote(categoriaActiva) + "$",
                    COL_CATEGORIA));

        sorter.setRowFilter(filtros.isEmpty() ? null : RowFilter.andFilter(filtros));
        actualizarFooter();
    }

    private void actualizarFooter() {
        int v = tabla == null ? 0 : tabla.getRowCount();
        int t = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        if (lblFooter != null)
            lblFooter.setText("Mostrando " + v + " de " + t + " platos");
    }

    // ─── TOAST ───────────────────────────────────────────────────────────────

    private void mostrarToast(String mensaje) {
        JLabel toast = new JLabel(mensaje);
        toast.setOpaque(true);
        toast.setBackground(new Color(235, 244, 255));
        toast.setForeground(AZUL);
        toast.setFont(new Font("Segoe UI", Font.BOLD, 13));
        toast.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(new Color(174, 204, 252), 10),
                new EmptyBorder(10, 14, 10, 14)));

        JDialog td = new JDialog(this, false);
        td.setUndecorated(true);
        td.add(toast);
        td.pack();
        td.setLocation(getX() + getWidth() - td.getWidth() - 40, getY() + 70);
        td.setVisible(true);

        Timer t = new Timer(1800, e -> td.dispose());
        t.setRepeats(false);
        t.start();
    }

    // ─── RENDERERS ───────────────────────────────────────────────────────────

    private static class TextoCentradoRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(new EmptyBorder(0, 6, 0, 6));
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(TEXTO);
            lbl.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return lbl;
        }
    }

    private static class DisponibleRenderer extends JPanel implements TableCellRenderer {
        public DisponibleRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            boolean activo = "ACTIVO".equalsIgnoreCase(String.valueOf(value));
            Color c = activo ? VERDE : NARANJA;
            JPanel dot = new JPanel() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(c); g2.fillOval(0, 3, 10, 10); g2.dispose();
                }
            };
            dot.setOpaque(false);
            dot.setPreferredSize(new Dimension(14, 16));
            JLabel lbl = new JLabel(String.valueOf(value));
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
            lbl.setForeground(c);
            JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            wrap.setOpaque(false);
            wrap.add(dot); wrap.add(lbl);
            add(wrap, new GridBagConstraints());
            return this;
        }
    }

    private static class PopularRenderer extends JPanel implements TableCellRenderer {
        public PopularRenderer() { setLayout(new GridBagLayout()); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            int estrellas = 0;
            try { estrellas = Integer.parseInt(String.valueOf(value)); }
            catch (NumberFormatException ignored) {}
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 3; i++) sb.append(i < estrellas ? "★" : "☆");
            JLabel lbl = new JLabel(sb.toString());
            lbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            lbl.setForeground(AMARILLO);
            add(lbl, new GridBagConstraints());
            return this;
        }
    }

    private static class AccionesRenderer extends JPanel implements TableCellRenderer {
        public AccionesRenderer() { setLayout(new FlowLayout(FlowLayout.CENTER, 8, 7)); setOpaque(true); }
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            removeAll();
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);

            JLabel editar = new JLabel("✎");
            editar.setFont(new Font("Segoe UI", Font.BOLD, 15));
            editar.setForeground(AZUL);
            editar.setOpaque(true);
            editar.setBackground(AZUL_CLARO);
            editar.setBorder(new EmptyBorder(4, 7, 4, 7));

            JLabel eliminar = new JLabel("🗑");
            eliminar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
            eliminar.setForeground(ROJO);
            eliminar.setOpaque(true);
            eliminar.setBackground(new Color(255, 235, 238));
            eliminar.setBorder(new EmptyBorder(4, 7, 4, 7));

            add(editar);
            add(eliminar);
            return this;
        }
    }

    // ─── MAIN ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PlatosFrame().setVisible(true));
    }
}