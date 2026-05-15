package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;
import java.sql.Connection;
import java.util.ArrayList;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.InsumoController;
import proyecto.pos.controller.PlatoController;
import proyecto.pos.controller.RecetaController;
import proyecto.pos.model.Insumo;
import proyecto.pos.model.Receta;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class PlatosFrame extends JFrame {
    
    private Connection Conexion;
    private PlatoController plato_controller;
    private InsumoController insumo_controller;
    private RecetaController receta_controller;
    private ArrayList<Plato> platos;
    private ArrayList<CategoriaMenu> categorias;
    private ArrayList<Insumo> insumos;
    
    

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

    // ─── COLUMNAS ────────────────────────────────────────────────────────────
    private static final int COL_ID         = 0;
    private static final int COL_NOMBRE     = 1;
    private static final int COL_CATEGORIA  = 2;
    private static final int COL_PRECIO     = 3;
    private static final int COL_DISPONIBLE = 4;
    private static final int COL_ULTIMA_MOD = 5;
    private static final int COL_POPULAR    = 6;
    private static final int COL_ACCIONES   = 7;
    
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
    private JCheckBox         chkDisponible;
    private JLabel            lblEstadoBadge;
    private JButton           btnGuardar;

    // ── Imagen del plato ─────────────────────────────────────────────────────
    private JLabel   lblPreviewImagen;          // miniatura en el formulario
    private JButton  btnSeleccionarImagen;
    private String   rutaImagenSeleccionada = null;   // ruta en disco

    
    
    public PlatosFrame() {
        DatabaseConnection db = new DatabaseConnection();
        Connection conexion = db.conectar();
        plato_controller = new PlatoController(conexion);
        receta_controller = new RecetaController(conexion);
        insumo_controller = new InsumoController(conexion);
        obtenerPlatos();
        obtenerCategorias();
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        actualizarFooter();
    }
    
    private void obtenerPlatos(){
        platos = (ArrayList<Plato>) plato_controller.listarPlatos();
    }
    
    private void obtenerCategorias(){
        categorias = (ArrayList<CategoriaMenu>) plato_controller.listarCategorias();
    }
    
    private void obtenerInsumos(){
        insumos = (ArrayList<Insumo>) insumo_controller.listarInsumos();
    }
    
    private ArrayList<Receta> obtenerRecetas(int plato_id){
        return (ArrayList<Receta>) receta_controller.listarRecetaPorPlato(plato_id);
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
        cboCategoriaForm = new JComboBox<>();
        for(CategoriaMenu cm : categorias){
            cboCategoriaForm.addItem(cm.getNombre());
        }
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

        // ── Imagen del Plato ─────────────────────────────────────────────────
        form.add(crearLabel("Imagen del Plato"));
        form.add(Box.createVerticalStrut(6));

        // Zona de preview
        lblPreviewImagen = new JLabel("Sin imagen", SwingConstants.CENTER);
        lblPreviewImagen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblPreviewImagen.setForeground(TEXTO_SUAVE);
        lblPreviewImagen.setOpaque(true);
        lblPreviewImagen.setBackground(new Color(245, 246, 248));
        lblPreviewImagen.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(2, 2, 2, 2)));
        lblPreviewImagen.setHorizontalAlignment(SwingConstants.CENTER);
        lblPreviewImagen.setVerticalAlignment(SwingConstants.CENTER);
        lblPreviewImagen.setPreferredSize(new Dimension(200, 90));
        lblPreviewImagen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        lblPreviewImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblPreviewImagen);
        form.add(Box.createVerticalStrut(6));

        // Botón seleccionar imagen
        btnSeleccionarImagen = new JButton("📷  Seleccionar imagen");
        btnSeleccionarImagen.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnSeleccionarImagen.setBackground(Color.WHITE);
        btnSeleccionarImagen.setForeground(AZUL);
        btnSeleccionarImagen.setFocusPainted(false);
        btnSeleccionarImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSeleccionarImagen.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(7, 10, 7, 10)));
        btnSeleccionarImagen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        btnSeleccionarImagen.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSeleccionarImagen.addActionListener(e -> seleccionarImagen());
        form.add(btnSeleccionarImagen);
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

    // ── Abre el JFileChooser y carga la imagen en el preview ──────────────────
    private void seleccionarImagen() {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle(
                "Seleccionar imagen del plato"
        );

        chooser.setFileFilter(
                new FileNameExtensionFilter(
                        "Imágenes",
                        "jpg",
                        "jpeg",
                        "png",
                        "gif"
                )
        );

        int resultado =
                chooser.showOpenDialog(this);

        if (resultado ==
                JFileChooser.APPROVE_OPTION) {

            try {

                File archivoOriginal =
                        chooser.getSelectedFile();

                // =========================
                // CARPETA DESTINO
                // =========================
                File carpeta =
                        new File(System.getProperty("user.home") + "\\Downloads\\POS_imagenes");

                if (!carpeta.exists()) {

                    carpeta.mkdirs();
                }

                // =========================
                // NOMBRE ÚNICO
                // =========================
                String nombreArchivo =
                        archivoOriginal.getName();

                // =========================
                // ARCHIVO DESTINO
                // =========================
                File archivoDestino =
                        new File(
                                carpeta,
                                nombreArchivo
                        );

                // =========================
                // COPIAR IMAGEN
                // =========================
                Files.copy(
                        archivoOriginal.toPath(),
                        archivoDestino.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                );

                // =========================
                // GUARDAR RUTA
                // =========================
                rutaImagenSeleccionada =
                        archivoDestino.getPath();

                // =========================
                // PREVIEW
                // =========================
                mostrarPreviewImagen(
                        rutaImagenSeleccionada,
                        lblPreviewImagen
                );

            } catch (Exception ex) {

                ex.printStackTrace();

                JOptionPane.showMessageDialog(
                        this,
                        "Error al guardar imagen"
                );
            }
        }
    }

    /** Escala la imagen y la pone en un JLabel de preview. */
    private void mostrarPreviewImagen(String ruta, JLabel destino) {
        if (ruta == null || ruta.isBlank()) {
            destino.setIcon(null);
            destino.setText("Sin imagen");
            return;
        }
        try {
            BufferedImage img = ImageIO.read(new File(ruta));
            if (img != null) {
                int w = destino.getPreferredSize().width  - 8;
                int h = destino.getPreferredSize().height - 8;
                Image scaled = img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
                destino.setIcon(new ImageIcon(scaled));
                destino.setText("");
            }
        } catch (Exception ex) {
            destino.setIcon(null);
            destino.setText("Imagen no válida");
        }
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

        top.add(lblTitulo,       BorderLayout.WEST);
        top.add(crearBuscador(), BorderLayout.EAST);

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.setOpaque(false);
        filtros.setBorder(new EmptyBorder(12, 0, 0, 0));
        for (CategoriaMenu cat : categorias){
            String cat_nombre = cat.getNombre();
            filtros.add(crearBotonCategoria(cat_nombre, filtros));
        }

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
            for (CategoriaMenu cat : categorias){
                String cat_nombre = cat.getNombre();
                contenedor.add(crearBotonCategoria(cat_nombre, contenedor));
            }
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

        panel.add(icono,     BorderLayout.WEST);
        panel.add(txtBuscar, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTabla() {
        String[] columnas = {
            "ID","Nombre","Categoría","Precio (S/.)",
            "Disponibilidad","Última Modif.","Pop.","Acciones"
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

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int fv = tabla.rowAtPoint(e.getPoint());
                int cv = tabla.columnAtPoint(e.getPoint());
                if (fv < 0 || cv < 0) return;
                int fm = tabla.convertRowIndexToModel(fv);

                if (e.getClickCount() == 2) {
                    abrirVentanaReceta(fm);
                    return;
                }
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
        obtenerPlatos();
        
        for (Plato p : platos){
            boolean disponibilidad = false;
            if (p.getDisponible() == 1) {
                disponibilidad = true; 
            }
            agregarFila(p.getPlatoId(),p.getNombre(), p.getCategoria().getNombre(), p.getPrecio(), disponibilidad, hoy,0);
        }
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

    // ─── VENTANA RECETA (con descripción adentro) ─────────────────────────────

    private void abrirVentanaReceta(int filaModelo) {
        int    idPlato   = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
        String nombre    = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE));
        String categoria = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_CATEGORIA));
        String precio    = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_PRECIO));
        //String descActual = descripcionesPorPlato.getOrDefault(idPlato, "");

        ArrayList<Receta> recetas_plato = obtenerRecetas(idPlato);
        JDialog dialog = new JDialog(this, "Receta: " + nombre, true);
        dialog.setSize(500, 640);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        // ── Encabezado azul ──────────────────────────────────────────────────
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

        // ── Descripción del plato (NUEVA sección dentro del dialog) ──────────
        JPanel secDescripcion = new JPanel(new BorderLayout(0, 6));
        secDescripcion.setBackground(Color.WHITE);
        secDescripcion.setBorder(new EmptyBorder(16, 20, 0, 20));

        JLabel lblTitDesc = new JLabel("Descripción del plato");
        lblTitDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitDesc.setForeground(TEXTO);

        JTextArea txtDescDialog = new JTextArea("xd");
        txtDescDialog.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtDescDialog.setForeground(TEXTO);
        txtDescDialog.setLineWrap(true);
        txtDescDialog.setWrapStyleWord(true);
        txtDescDialog.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(8, 10, 8, 10)));

        JScrollPane scrollDescDlg = new JScrollPane(txtDescDialog);
        scrollDescDlg.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        scrollDescDlg.setPreferredSize(new Dimension(0, 70));

        secDescripcion.add(lblTitDesc,    BorderLayout.NORTH);
        secDescripcion.add(scrollDescDlg, BorderLayout.CENTER);

        // ── Tabla de ingredientes ─────────────────────────────────────────────
        // =========================
        // COLUMNAS
        // =========================
        String[] columnas = {
                "ID_INSUMO",
                "INSUMO",
                "CANTIDAD",
                "UNIDAD"
        };

        // =========================
        // MODELO
        // =========================
        DefaultTableModel modeloReceta =
                new DefaultTableModel(columnas, 0) {

            @Override
            public boolean isCellEditable(
                    int fila,
                    int columna
            ) {

                // ID NO editable
                return columna != 0;
            }
        };

        // =========================
        // TABLA
        // =========================
        JTable tablaReceta =
                new JTable(modeloReceta);

        // =========================
        // ESTILOS
        // =========================
        tablaReceta.setRowHeight(36);

        tablaReceta.setFont(
                new Font(
                        "Segoe UI",
                        Font.PLAIN,
                        12
                )
        );

        tablaReceta.setShowVerticalLines(false);

        tablaReceta.setShowHorizontalLines(true);

        tablaReceta.setGridColor(
                new Color(235, 238, 244)
        );

        tablaReceta.setSelectionBackground(
                new Color(220, 235, 255)
        );

        // =========================
        // HEADER
        // =========================
        tablaReceta.getTableHeader().setFont(
                new Font(
                        "Segoe UI",
                        Font.BOLD,
                        12
                )
        );

        tablaReceta.getTableHeader().setBackground(
                new Color(248, 249, 252)
        );

        tablaReceta.getTableHeader().setForeground(
                new Color(80, 80, 80)
        );

        // =========================
        // OCULTAR ID
        // =========================
        TableColumn columnaId =
                tablaReceta
                .getColumnModel()
                .getColumn(0);

        columnaId.setMinWidth(0);
        columnaId.setMaxWidth(0);
        columnaId.setPreferredWidth(0);
        columnaId.setWidth(0);

        // =========================
        // TAMAÑOS VISIBLES

        JScrollPane scrollRec = new JScrollPane(tablaReceta);
        scrollRec.setBorder(new MenuSidebar.RoundedBorder(BORDE, 8));
        scrollRec.getViewport().setBackground(Color.WHITE);

        // ── Sección ingredientes ──────────────────────────────────────────────
        JPanel secIngredientes = new JPanel(new BorderLayout());
        secIngredientes.setBackground(Color.WHITE);
        secIngredientes.setBorder(new EmptyBorder(14, 20, 10, 20));

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

        // ── Fila agregar ingrediente ──────────────────────────────────────────
        JPanel panelAgregar = new JPanel(new BorderLayout(8, 0));
        panelAgregar.setBackground(new Color(248, 249, 252));
        panelAgregar.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 8),
                new EmptyBorder(10, 14, 10, 14)));

        JLabel lblAgregar = new JLabel("Agregar: ");
        lblAgregar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAgregar.setForeground(TEXTO_SUAVE);

        for (Receta r : recetas_plato){
            modeloReceta.addRow(
                new Object[]{
                    r.getInsumo().getInsumoId(),
                    r.getInsumo().getNombre(),
                    r.getCantidad_requerida(),
                    r.getInsumo().getUnidadMedida()
                }
            );
        }
        
        JComboBox<String> cbIngredientes = new JComboBox<>();
        obtenerInsumos();
        for(Insumo i : insumos){
            cbIngredientes.addItem(i.getNombre());
        }
        JTextField txtIngCantidad = new JTextField();
        JTextField txtIngUnidad   = new JTextField();
        cbIngredientes.setPreferredSize(new Dimension(200, 35));

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
            int ingrediente_indice = cbIngredientes.getSelectedIndex();
            int id = insumos.get(ingrediente_indice).getInsumoId();
            String n = cbIngredientes.getItemAt(ingrediente_indice);
            String c = txtIngCantidad.getText().trim();
            String u = txtIngUnidad.getText().trim();
            System.out.println(id);
            if (!n.isEmpty()) {
                modeloReceta.addRow(new Object[]{id,n, c, u});
                txtIngCantidad.setText("");
                txtIngUnidad.setText("");
                
            }
        });

        JPanel camposIng = new JPanel(new BorderLayout(6, 0));
        camposIng.setOpaque(false);

        JPanel derechaIng = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        derechaIng.setOpaque(false);
        derechaIng.add(cbIngredientes);
        derechaIng.add(txtIngCantidad);
        derechaIng.add(txtIngUnidad);
        derechaIng.add(btnAgrIng);
        camposIng.add(derechaIng, BorderLayout.EAST);

        panelAgregar.add(lblAgregar, BorderLayout.WEST);
        panelAgregar.add(camposIng,  BorderLayout.CENTER);

        // ── Botones pie ───────────────────────────────────────────────────────
        JPanel botones = new JPanel(new BorderLayout(10, 0));
        botones.setBackground(Color.WHITE);
        botones.setBorder(new EmptyBorder(12, 20, 18, 20));

        JButton btnCerrar = crearBotonDialog("Cerrar", false);
        btnCerrar.addActionListener(e -> dialog.dispose());

        JButton btnGuardarRec = crearBotonDialog("Guardar Receta", true);
        btnGuardarRec.addActionListener(e -> {
            if (tablaReceta.isEditing())
                tablaReceta.getCellEditor().stopCellEditing();

            // Guardar ingredientes
            
            for (int i = 0; i < modeloReceta.getRowCount(); i++) {
                System.out.println(tablaReceta
                            .getValueAt(i, 0)
                            .toString());
                int id_insumo =
                    Integer.parseInt(
                            tablaReceta
                            .getValueAt(i, 0)
                            .toString()
                    );
                Receta nueva_receta = new Receta();
                Plato p = new Plato();
                Insumo in = new Insumo();
                in = insumo_controller.obtenerPorId(id_insumo);
                p.setPlatoId(idPlato);
                nueva_receta.setCantidad_requerida(Integer.parseInt(modeloReceta.getValueAt(i,2).toString()));
                nueva_receta.setPlato(p);
                nueva_receta.setInsumo(in);
                receta_controller.registrarReceta(nueva_receta);
                
            }
            // ── Guardar descripción editada en el dialog ── NUEVO ──────────────
            dialog.dispose();
            mostrarToast("✔  Receta de \"" + nombre + "\" guardada");
        });

        botones.add(btnCerrar,     BorderLayout.WEST);
        botones.add(btnGuardarRec, BorderLayout.EAST);

        // ── Ensamblar ─────────────────────────────────────────────────────────
        JPanel centro = new JPanel(new BorderLayout(0, 0));
        centro.setBackground(Color.WHITE);

        // Descripción arriba, luego ingredientes, luego fila agregar
        JPanel centroTop = new JPanel(new BorderLayout(0, 0));
        centroTop.setBackground(Color.WHITE);
        centroTop.add(secDescripcion,  BorderLayout.NORTH);
        centroTop.add(secIngredientes, BorderLayout.CENTER);

        centro.add(centroTop,    BorderLayout.CENTER);
        centro.add(panelAgregar, BorderLayout.SOUTH);

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
        String precioStr   = txtPrecio.getText()
                .replace("S/.", "")
                .replace("S/", "")
                .trim();

        if (nombre.isEmpty()
                || catSel.equals("Seleccionar Categoría")) {

            mostrarToast("⚠ Completa nombre y categoría");
            return;
        }
        float precio = 0f;
        try {
            precio = Float.parseFloat(precioStr);
        } catch (NumberFormatException e) {
            mostrarToast("⚠ Precio inválido");
            return;
        }
        if (precio <= 0) {
            mostrarToast("⚠ El precio debe ser mayor a 0");
            return;
        }

        boolean disponible = chkDisponible.isSelected();
        String fecha = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        String idStr =txtIdPlato.getText().trim();
        int idEdicion = -1;
        try {
            idEdicion = Integer.parseInt(idStr);
        } catch (NumberFormatException ignored) {}
        CategoriaMenu categoriaSeleccionada = null;
        for (CategoriaMenu c : categorias) {
            if (c.getNombre().equals(catSel)) {
                categoriaSeleccionada = c;
                break;
            }
        }
        if (categoriaSeleccionada == null) {
            mostrarToast("⚠ Categoría inválida");
            return;
        }
        // =========================
        // ACTUALIZAR
        // =========================
        if (idEdicion > 0) {
            try {
                Plato plato = new Plato();
                plato.setPlatoId(idEdicion);
                plato.setNombre(nombre);
                plato.setPrecio(precio);
                plato.setCategoria(categoriaSeleccionada);
                plato.setDisponible(
                        disponible ? 1 : 0
                );

                plato_controller.actualizarDisponibilidad(idEdicion, disponible ? 1: 0);
                plato_controller.actualizarCategoria(idEdicion, categoriaSeleccionada);
                plato_controller.actualizarNombre(idEdicion, nombre);
                plato_controller.actualizarPrecio(idEdicion, precio);
                // ACTUALIZAR TABLA
                for (int i = 0;
                     i < modeloTabla.getRowCount();
                     i++) {

                    int idTabla = Integer.parseInt(
                            String.valueOf(
                                    modeloTabla.getValueAt(
                                            i,
                                            COL_ID
                                    )
                            )
                    );

                    if (idTabla == idEdicion) {

                        modeloTabla.setValueAt(
                                nombre,
                                i,
                                COL_NOMBRE
                        );

                        modeloTabla.setValueAt(
                                catSel,
                                i,
                                COL_CATEGORIA
                        );

                        modeloTabla.setValueAt(
                                String.format(
                                        "S/ %.2f",
                                        precio
                                ),
                                i,
                                COL_PRECIO
                        );

                        modeloTabla.setValueAt(
                                disponible
                                        ? "ACTIVO"
                                        : "INACTIVO",
                                i,
                                COL_DISPONIBLE
                        );

                        modeloTabla.setValueAt(
                                fecha,
                                i,
                                COL_ULTIMA_MOD
                        );

                        break;
                    }
                }

                
                aplicarFiltros();

                mostrarToast("✔ Plato actualizado");

                limpiarFormulario();

                return;

            } catch (Exception e) {

                e.printStackTrace();

                mostrarToast(
                        "❌ Error al actualizar"
                );

                return;
            }
        }

        // =========================
        // INSERTAR NUEVO
        // =========================
        try {

            Plato plato = new Plato();

            plato.setNombre(nombre);
            plato.setPrecio(precio);
            plato.setCategoria(categoriaSeleccionada);
            plato.setDisponible(
                    disponible ? 1 : 0
            );

            plato_controller.registrarPlato(
                    nombre,
                    precio,
                    categoriaSeleccionada,
                    disponible ? 1 : 0,
                    rutaImagenSeleccionada
            );
            plato = plato_controller.obtenerPlatoPorNombre(nombre);
            agregarFila(
                    plato.getPlatoId(),
                    plato.getNombre(),
                    plato.getCategoria().getNombre(),
                    plato.getPrecio(),
                    plato.getDisponible()==1, 
                    "",
                    1
            );


            aplicarFiltros();

            mostrarToast("✔ Plato agregado");

            limpiarFormulario();

        } catch (Exception e) {

            e.printStackTrace();

            mostrarToast(
                    "❌ Error al registrar"
            );
        }
        cargarDatosDemo();
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

        // Cargar imagen guardada del plato
        int idCargado = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
        String ruta = plato_controller.obtenerPlatoPorId(idCargado).getImagen();
        rutaImagenSeleccionada = ruta;
        mostrarPreviewImagen(ruta, lblPreviewImagen);
    }

    private void limpiarFormulario() {
        txtIdPlato.setText("(nuevo)");
        txtNombrePlato.setText("");
        cboCategoriaForm.setSelectedIndex(0);
        txtPrecio.setText("");
        chkDisponible.setSelected(true);
        actualizarBadge();
        // Limpiar imagen
        rutaImagenSeleccionada = null;
        lblPreviewImagen.setIcon(null);
        lblPreviewImagen.setText("Sin imagen");
    }

    private void eliminarPlato(int filaModelo) {
        String nombre = String.valueOf(modeloTabla.getValueAt(filaModelo, COL_NOMBRE));
        int op = JOptionPane.showConfirmDialog(
                this, "¿Eliminar el plato \"" + nombre + "\"?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op == JOptionPane.YES_OPTION) {
            int id = Integer.parseInt(String.valueOf(modeloTabla.getValueAt(filaModelo, COL_ID)));
            modeloTabla.removeRow(filaModelo);
            //recetasPorPlato.remove(id);
            //descripcionesPorPlato.remove(id);
            //imagenesPorPlato.remove(id);
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