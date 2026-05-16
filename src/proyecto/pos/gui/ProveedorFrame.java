package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.ProveedorController;
import proyecto.pos.model.Proveedor;

public class ProveedorFrame extends JFrame {

    private ProveedorController proveedor_controller;
    private ArrayList<Proveedor> proveedores;
    private Connection conexion;
    // ───────────────── COLORES ─────────────────
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);

    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);

    private static final Color VERDE_BG    = new Color(220, 245, 230);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);

    private static final Color ROJO_BG     = new Color(255, 228, 230);
    private static final Color ROJO_TEXT   = new Color(170, 25, 40);

    // ───────────────── TABLA ─────────────────
    private JTable tabla;
    private DefaultTableModel modelo;
    private int filaEditando = -1;
    
    private JLabel lblTotal;
    private JLabel lblActivos;
    private JLabel lblInactivos;

    public ProveedorFrame() {

        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();
        this.proveedor_controller = new ProveedorController(conexion);
        this.proveedores = new ArrayList();
        setTitle("Proveedores Regionales");
        setSize(1280, 720);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void obtenerProveedores(){
        proveedores = (ArrayList<Proveedor>) proveedor_controller.listarProveedores();
    }
    
    private void initComponents() {

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        // SI QUIERES EL SIDEBAR
        // root.add(new MenuSidebar(this, "Proveedores"), BorderLayout.WEST);
        root.add(new MenuSidebar(this, "Proveedores"), BorderLayout.WEST);

        root.add(crearContenido(), BorderLayout.CENTER);
    }

    private void cargarProveedores(){
        modelo.setRowCount(0);
        obtenerProveedores();
        for (Proveedor p : proveedores) {

            modelo.addRow(new Object[]{
                p.getId(),
                p.getCodigo(),
                p.getNombre(),
                p.getRucDni(),
                p.getTelefono(),
                p.getEmail(),
                p.getDireccion(),
                p.getTipoInsumo(),
                p.getRegion(),
                p.getContacto(),
                p.getCumplimiento()
            });
        }

        //actualizarCards();
    }
    
    // ───────────────── CONTENIDO ─────────────────

    private JPanel crearContenido() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));

        panel.add(crearHeader(), BorderLayout.NORTH);
        panel.add(crearCentro(), BorderLayout.CENTER);

        return panel;
    }

    // ───────────────── HEADER ─────────────────

    private JPanel crearHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel textos = new JPanel();
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));
        textos.setBackground(FONDO);

        JLabel titulo = new JLabel("Proveedores Regionales");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(30, 30, 30));

        JLabel subtitulo = new JLabel(
                "Registrar y consultar proveedores que abastecen al restaurante");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(new Color(120, 120, 120));

        textos.add(titulo);
        textos.add(Box.createVerticalStrut(5));
        textos.add(subtitulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setBackground(FONDO);

        JPanel horaPanel = crearMiniPanel("Hora", "23:09:01");

        JPanel perfilPanel = new JPanel(new BorderLayout(8, 0));
        perfilPanel.setPreferredSize(new Dimension(150, 42));
        perfilPanel.setBackground(Color.WHITE);
        perfilPanel.setBorder(BorderFactory.createLineBorder(BORDE));

        JLabel avatar = new JLabel("MG");
        avatar.setOpaque(true);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(35, 35));
        avatar.setBackground(new Color(210, 220, 255));
        avatar.setForeground(AZUL);
        avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        JLabel info = new JLabel(
                "<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        perfilPanel.add(avatar, BorderLayout.WEST);
        perfilPanel.add(info, BorderLayout.CENTER);

        derecha.add(horaPanel);
        derecha.add(perfilPanel);

        header.add(textos, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);

        return header;
    }

    private JPanel crearMiniPanel(String titulo, String valor) {

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel lbl1 = new JLabel(titulo);
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JLabel lbl2 = new JLabel(valor);
        lbl2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lbl2.setForeground(Color.GRAY);

        panel.add(lbl1);
        panel.add(lbl2);

        return panel;
    }

    // ───────────────── CENTRO ─────────────────

    private JPanel crearCentro() {

        JPanel centro = new JPanel(new BorderLayout(0, 16));
        centro.setBackground(FONDO);

        centro.add(crearCards(), BorderLayout.NORTH);
        centro.add(crearPanelTabla(), BorderLayout.CENTER);

        return centro;
    }

    // ───────────────── CARDS ─────────────────

    private JPanel crearCards() {

        JPanel cards = new JPanel(new GridLayout(1, 3, 16, 0));
        cards.setBackground(FONDO);

        JPanel c1 = crearCard(
                "Total Proveedores",
                "0",
                new Color(235, 228, 255),
                new Color(90, 70, 210)
        );

        lblTotal = getValorCard(c1);

        JPanel c2 = crearCard(
                "Activos",
                "0",
                VERDE_BG,
                VERDE_TEXT
        );

        lblActivos = getValorCard(c2);

        JPanel c3 = crearCard(
                "Inactivos",
                "0",
                ROJO_BG,
                ROJO_TEXT
        );

        lblInactivos = getValorCard(c3);

        cards.add(c1);
        cards.add(c2);
        cards.add(c3);

        return cards;
    }

    private JPanel crearCard(
            String titulo,
            String valor,
            Color fondoIcono,
            Color colorTexto
    ) {

        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel icono = new JPanel();
        icono.setPreferredSize(new Dimension(54, 54));
        icono.setBackground(fondoIcono);

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setForeground(new Color(130, 130, 130));
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(colorTexto);

        textos.add(lblTitulo);
        textos.add(lblValor);

        card.add(icono, BorderLayout.WEST);
        card.add(textos, BorderLayout.CENTER);

        return card;
    }

    private JLabel getValorCard(JPanel card) {

        JPanel panelTextos = (JPanel) card.getComponent(1);

        return (JLabel) panelTextos.getComponent(1);
    }

    // ───────────────── PANEL TABLA ─────────────────

    private JPanel crearPanelTabla() {

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(16, 16, 16, 16)
        ));

        panel.add(crearBarraSuperior(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearBarraBotones(), BorderLayout.SOUTH);
        cargarProveedores();
        return panel;
    }


    // ───────────────── BARRA SUPERIOR ─────────────────

    private JPanel crearBarraSuperior() {

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setBackground(Color.WHITE);

        JComboBox<String> cbTipo = new JComboBox<>(new String[]{
            "Todos los tipos de insumo",
            "Carnes",
            "Verduras",
            "Bebidas",
            "Lácteos"
        });

        cbTipo.setPreferredSize(new Dimension(210, 36));

        izquierda.add(cbTipo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setBackground(Color.WHITE);

        JButton btnPDF = crearBoton("Exportar PDF", Color.WHITE, AZUL);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(260, 36));
        txtBuscar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(0, 10, 0, 10)
        ));

        derecha.add(btnPDF);
        derecha.add(txtBuscar);

        barra.add(izquierda, BorderLayout.WEST);
        barra.add(derecha, BorderLayout.EAST);

        return barra;
    }

    // ───────────────── TABLA ─────────────────

    private JScrollPane crearTabla() {

        modelo = new DefaultTableModel(
                new String[]{
                    "Id",
                    "Código",
                    "Nombre / Razón Social",
                    "RUC / DNI",
                    "Teléfono",
                    "Email",
                    "Dirección",
                    "Tipo de Insumo",
                    "Región",
                    "Contacto",
                    "Cumplimiento"
                },
                0
        ){
             @Override
            public boolean isCellEditable(int row, int column) {

                // SOLO la fila agregada es editable
                return row == filaEditando;
            }
        };

        tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(0).setMinWidth(0);
        tabla.getColumnModel().getColumn(0).setMaxWidth(0);
        tabla.getColumnModel().getColumn(0).setWidth(0);
        tabla.setRowHeight(42);

        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        tabla.setGridColor(new Color(240, 240, 240));

        tabla.setShowVerticalLines(true);

        tabla.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 12));

        tabla.getTableHeader().setBackground(
                new Color(248, 248, 248));

        tabla.getTableHeader().setForeground(
                new Color(80, 80, 80));

        tabla.getTableHeader().setReorderingAllowed(false);

        int[] anchos = {
            0,
            80,
            220,
            130,
            110,
            150,
            200,
            140,
            120,
            120,
            120
        };
        TableColumn colTipo =
        tabla.getColumnModel().getColumn(7);

        colTipo.setCellEditor(
                new DefaultCellEditor(
                        new JComboBox<>(new String[]{
                            "Carnes",
                            "Verduras",
                            "Bebidas",
                            "Lácteos",
                            "Condimentos",
                            "Mariscos"
                        })
                )
        );
        
        for (int i = 0; i < anchos.length; i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        // BADGE CUMPLIMIENTO
        TableColumn colCumplimiento =
                tabla.getColumnModel().getColumn(7);

        colCumplimiento.setCellRenderer(
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column
            ) {

                JLabel lbl = (JLabel)
                        super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column
                        );

                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));

                String val = value == null
                        ? ""
                        : value.toString();

                lbl.setOpaque(true);

                if (val.equalsIgnoreCase("ALTO")) {

                    lbl.setBackground(VERDE_BG);
                    lbl.setForeground(VERDE_TEXT);

                } else {

                    lbl.setBackground(ROJO_BG);
                    lbl.setForeground(ROJO_TEXT);
                }

                return lbl;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);

        scroll.setBorder(BorderFactory.createLineBorder(BORDE));

        scroll.getViewport().setBackground(Color.WHITE);

        return scroll;
    }

    // ───────────────── BOTONES CRUD ─────────────────

    private JPanel crearBarraBotones() {

        JPanel barra = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0));

        barra.setBackground(Color.WHITE);

        barra.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAgregar = crearBotonAccion(
                "＋ Agregar",
                new Color(25, 135, 84),
                Color.WHITE
        );

        btnAgregar.addActionListener(e -> {

            modelo.addRow(new Object[]{
                null,               
                "",                 
                "",                 
                "",                 
                "",                 
                "",
                "", 
                "Carnes",           
                "",                 
                "",                 
                "ALTO"              
            });

            filaEditando = modelo.getRowCount() - 1;

            tabla.setRowSelectionInterval(
                    filaEditando,
                    filaEditando
            );

            modelo.fireTableDataChanged();
        });

        JButton btnGuardar = crearBotonAccion(
                "💾 Guardar",
                new Color(13, 110, 253), // AZUL
                Color.WHITE
        );
        btnGuardar.addActionListener(e -> guardar());
        
        
        JButton btnEditar = crearBotonAccion(
                "✎ Editar",
                new Color(184, 134, 11), // AMARILLO OSCURO
                Color.WHITE
        );

        JButton btnEliminar = crearBotonAccion(
                "✕ Eliminar",
                new Color(220, 53, 69), // ROJO
                Color.WHITE
        );

        barra.add(btnEliminar);
        barra.add(btnEditar);
        barra.add(btnGuardar);
        barra.add(btnAgregar);

        return barra;
    }
    
    // ───────────────── BOTONES ─────────────────

    private JButton crearBoton(
            String texto,
            Color fondo,
            Color textoColor
    ) {

        JButton btn = new JButton(texto);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btn.setBackground(fondo);

        btn.setForeground(textoColor);

        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AZUL),
                new EmptyBorder(10, 22, 10, 22)
        ));

        btn.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                btn.setBackground(AZUL_CLARO);
            }

            @Override
            public void mouseExited(MouseEvent e) {

                btn.setBackground(fondo);
            }
        });

        return btn;
    }

    private JButton crearBotonAccion(
        String texto,
        Color fondo,
        Color colorTexto
    ) {

        JButton btn = new JButton(texto);

        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btn.setBackground(fondo);

        btn.setForeground(colorTexto);

        btn.setFocusPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBorder(new EmptyBorder(10, 18, 10, 18));

        btn.setOpaque(true);

        Color hover = fondo.darker();

        btn.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {

                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(MouseEvent e) {

                btn.setBackground(fondo);
            }
        });

        return btn;
    }
    
    // ───────────────── GUARDAR PROVEEDORES ─────────────────

    private void guardar() {

        for (int i = 0; i < modelo.getRowCount(); i++) {

            Object idObj = modelo.getValueAt(i, 0);

            // SOLO guardar filas nuevas
            if (idObj != null) {
                continue;
            }

            String codigo =
                    modelo.getValueAt(i, 1).toString().trim();

            String nombre =
                    modelo.getValueAt(i, 2).toString().trim();

            String ruc =
                    modelo.getValueAt(i, 3).toString().trim();

            String telefono =
                    modelo.getValueAt(i, 4).toString().trim();

            String email =
                    modelo.getValueAt(i, 5).toString().trim();

            String direccion =
                    modelo.getValueAt(i, 6).toString().trim();

            String tipoInsumo =
                    modelo.getValueAt(i, 7).toString().trim();

            String region =
                    modelo.getValueAt(i, 8).toString().trim();

            String contacto =
                    modelo.getValueAt(i, 9).toString().trim();

            String cumplimiento =
                    modelo.getValueAt(i, 10).toString().trim();

            // ───────── VALIDACIONES ─────────

            if (codigo.isEmpty()) {

                err("Código vacío", i);
                return;
            }

            if (nombre.isEmpty()) {

                err("Nombre / Razón Social vacío", i);
                return;
            }

            if (ruc.isEmpty()) {

                err("RUC / DNI vacío", i);
                return;
            }

            // DNI O RUC
            /*if (!(ruc.matches("\\d{8}") ||
                  ruc.matches("\\d{11}"))) {

                err("RUC o DNI inválido", i);
                return;
            }*/

            if (telefono.isEmpty()) {

                err("Teléfono vacío", i);
                return;
            }

            if (!telefono.matches("\\d{9}")) {

                err("Teléfono inválido", i);
                return;
            }

            if (email.isEmpty()) {

                err("Email vacío", i);
                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                err("Email inválido", i);
                return;
            }

            if (direccion.isEmpty()) {

                err("Dirección vacía", i);
                return;
            }

            if (region.isEmpty()) {

                err("Región vacía", i);
                return;
            }

            if (contacto.isEmpty()) {

                err("Contacto vacío", i);
                return;
            }

            if (!(cumplimiento.equalsIgnoreCase("ALTO")
                    || cumplimiento.equalsIgnoreCase("BAJO"))) {

                err("Cumplimiento inválido", i);
                return;
            }

            // ───────── VALIDAR DUPLICADOS ─────────

            if (proveedor_controller
                    .obtenerProveedorPorCodigo(codigo) != null) {

                err("El código ya existe", i);
                return;
            }

            if (proveedor_controller
                    .obtenerProveedorPorRUC(ruc) != null) {

                err("El RUC/DNI ya existe", i);
                return;
            }

            // ───────── GUARDAR ─────────
            Proveedor p = new Proveedor();
            p.setCodigo(codigo);
            p.setNombre(nombre);
            p.setRucDni(ruc);
            p.setTelefono(telefono);
            p.setEmail(email);
            p.setDireccion(direccion);
            p.setTipoInsumo(tipoInsumo);
            p.setRegion(region);
            p.setContacto(contacto);
            if(cumplimiento.equals("ALTO")){
                p.setCumplimiento(1);
            }else{p.setCumplimiento(0);}
            
            try {

                proveedor_controller.registrarProveedor(p);
            } catch (Exception e) {

                err("Error al guardar: " + e.getMessage(), i);

                return;
            }
        }

        filaEditando = -1;

        modelo.fireTableDataChanged();

        JOptionPane.showMessageDialog(
                this,
                "Proveedores guardados correctamente"
        );
        cargarProveedores();
    }
    
    private void err(String msg, int fila) {

        JOptionPane.showMessageDialog(
                this,
                msg + " — fila " + (fila + 1),
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
    }
    // ───────────────── MAIN ─────────────────

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ProveedorFrame().setVisible(true);
        });
    }
}
