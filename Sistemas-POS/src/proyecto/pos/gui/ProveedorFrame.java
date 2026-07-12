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
    private int columnaEditando = -1;
    
    
    public ProveedorFrame() {

        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();
        this.proveedor_controller = new ProveedorController(conexion);
        this.proveedores = new ArrayList<>();
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
        root.add(new MenuSidebar(this, "Proveedores"), BorderLayout.WEST);

        root.add(crearContenido(), BorderLayout.CENTER);
    }

    private void cargarProveedores(){
        String cumplimiento;
        modelo.setRowCount(0);
        obtenerProveedores();
        for (Proveedor p : proveedores) {
            if(p.getCumplimiento() == 1){
                cumplimiento = "ACTIVO";
            }
            else{
                cumplimiento = "INACTIVO";
            }
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
                cumplimiento
            });
        }

        actualizarCards(); // LÍNEA DESCOMENTADA Y ACTIVA
    }

    // ───────────────── MÉTODO NUEVO PARA LAS TARJETAS ─────────────────
    private void actualizarCards() {
        int total = 0;
        int activos = 0;
        int inactivos = 0;

        // Recorremos la lista de proveedores que ya obtuvimos de la BD
        if (proveedores != null) {
            total = proveedores.size();
            for (Proveedor p : proveedores) {
                if (p.getCumplimiento() == 1) {
                    activos++;
                } else {
                    inactivos++;
                }
            }
        }

        // Actualizamos los JLabels de las tarjetas
        if (lblTotal != null) lblTotal.setText(String.valueOf(total));
        if (lblActivos != null) lblActivos.setText(String.valueOf(activos));
        if (lblInactivos != null) lblInactivos.setText(String.valueOf(inactivos));
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

                // FILAS NUEVAS
                if (modelo.getValueAt(row, 0) == null) {
                    return true;
                }

                // EDITAR TODA LA FILA
                return row == filaEditando;
            }
        };

        tabla = new JTable(modelo);
        tabla.setCellSelectionEnabled(true);
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

        // Cambia esto alrededor de la línea 400
        colTipo.setCellEditor(
            new DefaultCellEditor(
                new JComboBox<>(new String[]{
                    "Carnes y Aves", 
                    "Vegetales y Frutas",
                    "Bebidas", 
                    "Lácteos",
                    "Pescados y Mariscos",
                    "Abarrotes Secos",
                    "Otros"
                })
            )
        );

        TableColumn colCumplimiento = tabla.getColumnModel().getColumn(10);
        colCumplimiento.setCellEditor
                (new DefaultCellEditor(
                        new JComboBox<>(new String[]{
                            "ACTIVO",
                            "INACTIVO"
                        }
                )
        ));
        
        for (int i = 0; i < anchos.length; i++) {

            tabla.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(anchos[i]);
        }

        // BADGE CUMPLIMIENTO

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

                if (val.equalsIgnoreCase("ACTIVO")) {

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
                "", 
                "Carnes",           
                "",                 
                "",                 
                "ACTIVO"              
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
        btnEditar.addActionListener(e -> activarEdicionFila());
        
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

        // TERMINAR EDICIÓN ACTIVA EN LA TABLA
        if (tabla.isEditing()) {
            tabla.getCellEditor().stopCellEditing();
        }

        // ───────── ACTUALIZAR FILA EXISTENTE ─────────
        if (filaEditando != -1 && modelo.getValueAt(filaEditando, 0) != null) {
            actualizarProveedor();
            filaEditando = -1;
            modelo.fireTableDataChanged();
            cargarProveedores(); 
            return;
        }

        // ───────── GUARDAR NUEVOS ─────────
        for (int i = 0; i < modelo.getRowCount(); i++) {

            Object idObj = modelo.getValueAt(i, 0);

            // SOLO FILAS NUEVAS
            if (idObj != null) {
                continue;
            }

            String codigo       = safeGet(i, 1);
            String nombre       = safeGet(i, 2);
            String ruc          = safeGet(i, 3);
            String telefono     = safeGet(i, 4);
            String email        = safeGet(i, 5);
            String direccion    = safeGet(i, 6);
            String tipoInsumo   = safeGet(i, 7);
            String region       = safeGet(i, 8);
            String contacto     = safeGet(i, 9);
            String cumplimiento = safeGet(i, 10);

            // ───────── VALIDACIONES MEJORADAS ─────────
            if (codigo.isEmpty()) { err("Código vacío", i); return; }
            if (nombre.isEmpty()) { err("Nombre / Razón Social vacío", i); return; }
            if (ruc.isEmpty()) { err("RUC / DNI vacío", i); return; }
            
            // Validar longitud de RUC/DNI
            if (!(ruc.matches("\\d{8}") || ruc.matches("\\d{11}"))) {
                err("El RUC o DNI debe tener 8 u 11 dígitos", i);
                return;
            }
            
            // Validar prefijo de RUC peruano
            if (ruc.length() == 11 && !(ruc.startsWith("10") || ruc.startsWith("20"))) {
                err("El RUC debe empezar con 10 o 20", i);
                return;
            }

            if (telefono.isEmpty()) { err("Teléfono vacío", i); return; }
            if (!telefono.matches("\\d{9}")) { err("Teléfono debe tener 9 dígitos", i); return; }
            
            // Validar formato de email más estricto
            if (email.isEmpty()) { err("Email vacío", i); return; }
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                err("Email inválido (Falta el .com, .pe, etc.)", i);
                return;
            }

            if (direccion.isEmpty()) { err("Dirección vacía", i); return; }
            if (tipoInsumo.isEmpty()) { err("Tipo de insumo vacío", i); return; }
            if (region.isEmpty()) { err("Región vacía", i); return; }
            if (contacto.isEmpty()) { err("Contacto vacío", i); return; }
            if (!(cumplimiento.equalsIgnoreCase("ACTIVO") || cumplimiento.equalsIgnoreCase("INACTIVO"))) {
                err("Cumplimiento inválido", i);
                return;
            }

            // ───────── VALIDAR DUPLICADOS ─────────
            if (proveedor_controller.obtenerProveedorPorCodigo(codigo) != null) {
                err("El código ya existe en la base de datos", i);
                return;
            }

            if (proveedor_controller.obtenerProveedorPorRUC(ruc) != null) {
                err("El RUC/DNI ya está registrado", i);
                return;
            }

            // ───────── REGISTRAR ─────────
            try {
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
                
                if(cumplimiento.equalsIgnoreCase("ACTIVO")){
                    p.setCumplimiento(1);
                } else {
                    p.setCumplimiento(0);
                }

                proveedor_controller.registrarProveedor(p);

            } catch (Exception e) {
                // 🔥 AQUÍ ESTÁ LA MAGIA: Extraemos el error real de Oracle (ORA-XXXX)
                String causaReal = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
                
                JOptionPane.showMessageDialog(this, 
                    "Error de Base de Datos en fila " + (i + 1) + ":\n" + causaReal,
                    "Error SQL", 
                    JOptionPane.ERROR_MESSAGE);
                
                e.printStackTrace(); // Esto lo imprimirá en rojo en la consola de NetBeans
                return;
            }
        }

        filaEditando = -1;
        modelo.fireTableDataChanged();
        
        JOptionPane.showMessageDialog(this, "Proveedores guardados correctamente");
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
    
    private void activarEdicionFila() {

        int fila = tabla.getSelectedRow();

        if (fila < 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una fila."
            );

            return;
        }

        filaEditando = fila;

        modelo.fireTableDataChanged();

        tabla.setRowSelectionInterval(fila, fila);
    }
    
    // ───────────────── EDITAR CELDA ─────────────────

   
    
    // ───────────────── MAIN ─────────────────

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            new ProveedorFrame().setVisible(true);
        });
    }
    
    // ───────────────── ACTUALIZAR CELDA ─────────────────

    private void actualizarProveedor() {

        try {

            int fila = filaEditando;

            int id = Integer.parseInt(
                    modelo.getValueAt(fila, 0).toString()
            );

            String codigo       = safeGet(fila, 1);
            String nombre       = safeGet(fila, 2);
            String ruc          = safeGet(fila, 3);
            String telefono     = safeGet(fila, 4);
            String email        = safeGet(fila, 5);
            String direccion    = safeGet(fila, 6);
            String tipoInsumo   = safeGet(fila, 7);
            String region       = safeGet(fila, 8);
            String contacto     = safeGet(fila, 9);
            String cumplimiento = safeGet(fila, 10);

            // ───────── VALIDACIONES ─────────

            if (codigo.isEmpty()) {
                err("Código vacío", fila);
                return;
            }

            if (nombre.isEmpty()) {
                err("Nombre vacío", fila);
                return;
            }

            if (!(ruc.matches("\\d{8}")
                    || ruc.matches("\\d{11}"))) {

                err("RUC/DNI inválido", fila);
                return;
            }

            if (!telefono.matches("\\d{9}")) {

                err("Teléfono inválido", fila);
                return;
            }

            if (!email.matches(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

                err("Email inválido", fila);
                return;
            }

            if (!(cumplimiento.equalsIgnoreCase("ACTIVO")
                    || cumplimiento.equalsIgnoreCase("INACTIVO"))) {

                err("Cumplimiento inválido", fila);
                return;
            }

            // ───────── ACTUALIZAR ─────────

            Proveedor p = new Proveedor();
            p.setId(id);
            p.setCodigo(codigo);
            p.setNombre(nombre);
            p.setRucDni(ruc);
            p.setTelefono(telefono);
            p.setEmail(email);
            p.setDireccion(direccion);
            p.setTipoInsumo(tipoInsumo);
            p.setRegion(region);
            p.setContacto(contacto);
            if(cumplimiento.equalsIgnoreCase("ACTIVO")){
                p.setCumplimiento(1);
            }else{
                p.setCumplimiento(0);
            }
            
            
            proveedor_controller.actualizarProveedor(p);

            JOptionPane.showMessageDialog(
                    this,
                    "Proveedor actualizado correctamente"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error al actualizar: "
                            + e.getMessage()
            );
        }
    }
    
        private String safeGet(int fila, int columna) {

        Object val = modelo.getValueAt(fila, columna);

        return val == null
                ? ""
                : val.toString().trim();
    }
}