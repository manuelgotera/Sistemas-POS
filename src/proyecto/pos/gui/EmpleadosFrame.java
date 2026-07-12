package proyecto.pos.gui;

import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.EmpleadoController;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoEmpleado;
import proyecto.pos.model.Rol;

public class EmpleadosFrame extends JFrame {

    // ── Paleta ──────────────────────────────────────────────────────────────
    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color ROJO        = new Color(220, 53, 69);
    private static final Color VERDE_BG    = new Color(225, 245, 238);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);
    private static final Color ROJO_BG     = new Color(255, 235, 238);
    private static final Color ROJO_TEXT   = new Color(180, 30, 50);

    private static final int COL_ID        = 0;
    private static final int COL_NOMBRE    = 1;
    private static final int COL_APELLIDO  = 2;
    private static final int COL_DNI       = 3;
    private static final int COL_TELEFONO  = 4;
    private static final int COL_EMAIL     = 5;
    private static final int COL_ROL       = 6;
    private static final int COL_ESTADO    = 7;
    private static final int COL_FECHA     = 8;

    private JTable tabla;
    private DefaultTableModel modelo;
    private JButton btnAgregar, btnEliminar, btnGuardar, btnEditar;
    private int filaEditando = -1;

    private JLabel lblTotalEmp;
    private JLabel lblActivos;
    private JLabel lblInactivos;

    private EmpleadoController empleado_controller;
    private Connection conexion;
    
    public EmpleadosFrame() {
        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();
        this.empleado_controller = new EmpleadoController(conexion);
        setTitle("Gestión de Empleados");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        // ── ÚNICO CAMBIO: sidebar reutilizable en lugar del crearSidebar() propio
        root.add(new MenuSidebar(this, "Empleados"), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    // ─── CONTENIDO PRINCIPAL  (sin cambios) ─────────────────────────────────

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

        JLabel titulo = new JLabel("Gestión de Empleados");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(25, 25, 25));

        JLabel subtitulo = new JLabel("Administra el personal y sus roles en el sistema");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);

        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.CENTER);

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

        JLabel nombrePerfil = new JLabel(
                "<html><b>uwu fernandez</b><br><font color='gray'>Administrador</font></html>");
        nombrePerfil.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        perfilPanel.add(circulo, BorderLayout.WEST);
        perfilPanel.add(nombrePerfil, BorderLayout.CENTER);

        header.add(titulos, BorderLayout.WEST);
        header.add(perfilPanel, BorderLayout.EAST);
        return header;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(FONDO);
        panel.add(crearCards(), BorderLayout.NORTH);
        panel.add(crearPanelTabla(), BorderLayout.CENTER);
        return panel;
    }

    // ─── CARDS  (sin cambios) ────────────────────────────────────────────────

    private JPanel crearCards() {
        JPanel cards = new JPanel(new GridLayout(1, 3, 14, 0));
        cards.setBackground(FONDO);

        JPanel c1 = crearCard("Total Empleados", "0", new Color(238, 237, 254), new Color(100, 90, 210));
        lblTotalEmp = getValorCard(c1);
        cards.add(c1);

        JPanel c2 = crearCard("Activos", "0", VERDE_BG, VERDE_TEXT);
        lblActivos = getValorCard(c2);
        cards.add(c2);

        JPanel c3 = crearCard("Inactivos", "0", ROJO_BG, ROJO_TEXT);
        lblInactivos = getValorCard(c3);
        cards.add(c3);

        return cards;
    }

    private JPanel crearCard(String etiqueta, String valor, Color colorFondo, Color colorAccent) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(16, 18, 16, 18)
        ));

        JPanel icono = new JPanel();
        icono.setBackground(colorFondo);
        icono.setPreferredSize(new Dimension(46, 46));
        icono.setBorder(BorderFactory.createLineBorder(colorFondo, 10));

        JPanel texto = new JPanel(new GridBagLayout());
        texto.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 0;
        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEtq.setForeground(new Color(140, 140, 140));
        texto.add(lblEtq, gbc);

        gbc.gridy = 1;
        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblVal.setForeground(colorAccent);
        texto.add(lblVal, gbc);

        card.add(icono, BorderLayout.WEST);
        card.add(texto, BorderLayout.CENTER);
        return card;
    }

    private JLabel getValorCard(JPanel card) {
        return (JLabel) ((JPanel) card.getComponent(1)).getComponent(1);
    }

    // ─── PANEL TABLA  (sin cambios) ──────────────────────────────────────────

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(18, 18, 18, 18)
        ));
        panel.add(crearBarraSuperior(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearBarraBotones(), BorderLayout.SOUTH);
        cargarEmpleados();
        return panel;
    }

    private JPanel crearBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel lblTit = new JLabel("Lista de empleados");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(new Color(30, 30, 30));

        JLabel lblSub = new JLabel(
            "Haz clic en Agregar para añadir  ·  Clic sobre el Rol para modificarlo");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(TEXTO_SUAVE);

        JPanel izq = new JPanel(new BorderLayout(0, 3));
        izq.setBackground(Color.WHITE);
        izq.add(lblTit, BorderLayout.NORTH);
        izq.add(lblSub, BorderLayout.SOUTH);

        barra.add(izq, BorderLayout.WEST);
        return barra;
    }

    private JScrollPane crearTabla() {
        modelo = new DefaultTableModel(
                    new String[]{
                    "ID",
                    "Nombre",
                    "Apellido",
                    "DNI",
                    "Teléfono",
                    "Email",
                    "Rol",
                    "Estado",
                    "Fecha"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int r, int c) {
                // ninguna fila editable
                if (filaEditando == -1)
                    return false;

                // solo la fila seleccionada
                if (r != filaEditando)
                    return false;

                // ID nunca editable
                if (c == COL_ID)
                    return false;

                // Rol se cambia con modal
                if (c == COL_ROL)
                    return false;

                return true;
            }
        };

        tabla = new JTable(modelo);
        tabla.getColumnModel().getColumn(COL_ID).setMinWidth(0);
        tabla.getColumnModel().getColumn(COL_ID).setMaxWidth(0);
        tabla.getColumnModel().getColumn(COL_ID).setWidth(0);
        tabla.setRowHeight(44);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(242, 242, 242));
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(Color.BLACK);

        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(250, 250, 250));
        tabla.getTableHeader().setForeground(new Color(100, 100, 100));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.getTableHeader().setBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));

        int[] anchos = {
                0,    // ID oculta
                140,  // Nombre
                140,  // Apellido
                90,   // DNI
                110,  // Teléfono
                160,  // Email
                200,  // Rol
                100,  // Estado
                120   // Fecha
            };
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Estado: ComboBox editor + badge renderer
        TableColumn colEstado = tabla.getColumnModel().getColumn(7);
        colEstado.setCellEditor(new DefaultCellEditor(
                new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"})
        ));
        colEstado.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
                if (!sel) {
                    String v = val == null ? "" : val.toString();
                    lbl.setOpaque(true);
                    if ("ACTIVO".equals(v)) {
                        lbl.setBackground(VERDE_BG); lbl.setForeground(VERDE_TEXT);
                    } else {
                        lbl.setBackground(ROJO_BG); lbl.setForeground(ROJO_TEXT);
                    }
                }
                return lbl;
            }
        });

        // Rol: renderer visual estilo chip clicable
        TableColumn colRol = tabla.getColumnModel().getColumn(COL_ROL);
        colRol.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        t, val, sel, foc, row, col);
                String texto = (val == null ? "" : val.toString());
                lbl.setText(texto);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setOpaque(true);
                if (!sel) {
                    lbl.setBackground(AZUL_CLARO);
                    lbl.setForeground(AZUL);
                } else {
                    lbl.setBackground(new Color(200, 220, 255));
                    lbl.setForeground(AZUL);
                }
                lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
                return lbl;
            }
        });

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(2).setCellRenderer(centro);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centro);

        tabla.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());

                // SOLO permitir cambiar rol
                // si la fila está en edición
                if (fila == filaEditando && col == COL_ROL) {
                    abrirSelectorRol(fila);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());

                // cursor mano SOLO en fila editable
                tabla.setCursor(
                    (fila == filaEditando && col == COL_ROL)
                        ? new Cursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor()
                );
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ─── MINI VENTANA SELECTOR DE ROL  (sin cambios) ────────────────────────

    private void abrirSelectorRol(int filaVista) {
        int filaModelo = tabla.convertRowIndexToModel(filaVista);
        Object valActual = modelo.getValueAt(filaModelo, 6);
        String rolActual = (valActual == null) ? "Cajero" : valActual.toString();

        String[] roles = {"Administrador", "Cajero", "Mesero", "Cocinero"};

        JDialog dialog = new JDialog(this, "Seleccionar Rol", true);
        dialog.setUndecorated(true);
        dialog.setSize(290, 54 + roles.length * 52);
        dialog.setLocationRelativeTo(this);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        root.setBorder(BorderFactory.createLineBorder(BORDE, 1));
        dialog.setContentPane(root);

        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(Color.WHITE);
        cab.setBorder(new EmptyBorder(14, 18, 10, 16));

        JLabel lblTit = new JLabel("SELECCIONAR ROL");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTit.setForeground(new Color(55, 55, 55));

        JLabel btnX = new JLabel("  ✕");
        btnX.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnX.setForeground(TEXTO_SUAVE);
        btnX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnX.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
            public void mouseEntered(MouseEvent e) { btnX.setForeground(ROJO); }
            public void mouseExited (MouseEvent e) { btnX.setForeground(TEXTO_SUAVE); }
        });

        cab.add(lblTit, BorderLayout.WEST);
        cab.add(btnX, BorderLayout.EAST);

        JPanel sepLine = new JPanel();
        sepLine.setBackground(BORDE);
        sepLine.setPreferredSize(new Dimension(0, 1));

        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(Color.WHITE);
        lista.setBorder(new EmptyBorder(10, 12, 12, 12));

        for (String rol : roles) {
            boolean esActual = rol.equals(rolActual);

            JPanel item = new JPanel(new BorderLayout(8, 0));
            item.setBackground(esActual ? AZUL_CLARO : Color.WHITE);
            item.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(esActual ? new Color(180, 210, 255) : BORDE, 1),
                    new EmptyBorder(9, 14, 9, 12)
            ));
            item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel lblRol = new JLabel(rol);
            lblRol.setFont(new Font("Segoe UI", esActual ? Font.BOLD : Font.PLAIN, 13));
            lblRol.setForeground(esActual ? AZUL : new Color(45, 45, 45));
            item.add(lblRol, BorderLayout.WEST);

            if (esActual) {
                JLabel check = new JLabel("✔");
                check.setFont(new Font("Segoe UI", Font.BOLD, 12));
                check.setForeground(AZUL);
                item.add(check, BorderLayout.EAST);
            }

            item.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (!esActual) {
                        item.setBackground(new Color(245, 248, 252));
                        item.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(BORDE, 1),
                                new EmptyBorder(9, 14, 9, 12)
                        ));
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (!esActual) {
                        item.setBackground(Color.WHITE);
                        item.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(BORDE, 1),
                                new EmptyBorder(9, 14, 9, 12)
                        ));
                    }
                }
                public void mouseClicked(MouseEvent e) {
                    modelo.setValueAt(rol, filaModelo, 6);
                    dialog.dispose();
                }
            });

            lista.add(item);
            lista.add(Box.createVerticalStrut(6));
        }

        root.add(cab, BorderLayout.NORTH);
        root.add(sepLine, BorderLayout.CENTER);
        root.add(lista, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // ─── BARRA DE BOTONES  (sin cambios) ────────────────────────────────────

    private JPanel crearBarraBotones() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        barra.setBackground(Color.WHITE);
        barra.setBorder(new EmptyBorder(14, 0, 0, 0));

        btnEliminar = crearBoton("✕  Eliminar", ROJO,                    Color.WHITE);
        btnEditar   = crearBoton("✎  Editar",   new Color(245, 245, 245), new Color(50, 50, 50));
        btnGuardar  = crearBoton("✔  Guardar",  new Color(15, 110, 86),   Color.WHITE);
        btnAgregar  = crearBoton("＋  Agregar",  AZUL,                    Color.WHITE);

        barra.add(btnEliminar);
        barra.add(btnEditar);
        barra.add(btnGuardar);
        barra.add(btnAgregar);

        btnAgregar.addActionListener(e -> {
            String hoy = new SimpleDateFormat("dd/MM/yyyy")
                .format(new java.util.Date());
            modelo.addRow(new Object[]{
                null,
                "",
                "",
                "",
                "",
                "",
                "Cajero",
                "ACTIVO",
                hoy
            });

            // nueva fila editable
            filaEditando = modelo.getRowCount() - 1;

            // seleccionar la nueva fila
            tabla.setRowSelectionInterval(
                filaEditando,
                filaEditando
            );

            // refrescar tabla
            modelo.fireTableDataChanged();

            actualizarCards();
        });
        btnEliminar.addActionListener(e -> { eliminarFila(); actualizarCards(); });
        btnEditar.addActionListener(e -> {
            if (filaEditando == -1) {
                activarEdicion();
            } else {
                actualizarEmpleado();
                actualizarCards();
            }
        });
        btnGuardar.addActionListener(e -> guardar());

        return barra;
    }

    private JButton crearBoton(String texto, Color fondo, Color colorTexto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(fondo);
        btn.setForeground(colorTexto);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(9, 18, 9, 18));
        Color hover = fondo.darker();
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited (MouseEvent e) { btn.setBackground(fondo); }
        });
        return btn;
    }

    // ─── LÓGICA  (sin cambios) ───────────────────────────────────────────────

    private void eliminarFila() {
        int fila = tabla.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una fila primero.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this,
                "¿Eliminar al empleado seleccionado?", "Confirmar",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok == JOptionPane.YES_OPTION) modelo.removeRow(fila);
    }

    private void activarEdicion() {

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
    }
    
    private void guardar() {

        SimpleDateFormat formato =
            new SimpleDateFormat("dd/MM/yyyy");

        formato.setLenient(false);

        for (int i = 0; i < modelo.getRowCount(); i++) {

            Object idObj = modelo.getValueAt(i, COL_ID);

            // Fix: también omitir si idObj es string vacío o 0 (no es fila nueva)
            if (idObj != null && !idObj.toString().trim().isEmpty() && !idObj.toString().trim().equals("0")) {
                continue;
            }

            String nombre =
                modelo.getValueAt(i, COL_NOMBRE).toString().trim();

            String apellido =
                modelo.getValueAt(i, COL_APELLIDO).toString().trim();

            String dni =
                modelo.getValueAt(i, COL_DNI).toString().trim();

            String telefono =
                modelo.getValueAt(i, COL_TELEFONO).toString().trim();

            String email =
                modelo.getValueAt(i, COL_EMAIL).toString().trim();

            String rolStr =
                modelo.getValueAt(i, COL_ROL).toString().trim();
            
            String estadoStr =
                    modelo.getValueAt(i, COL_ESTADO).toString().trim();

            String fechaStr =
                modelo.getValueAt(i, COL_FECHA).toString().trim();

            if (nombre.isEmpty()) {
                err("Nombre vacío", i);
                return;
            }

            if (apellido.isEmpty()) {
                err("Apellido vacío", i);
                return;
            }

            if (!dni.matches("\\d{8}")) {
                err("DNI inválido", i);
                return;
            }

            if (!email.contains("@")) {
                err("Email inválido", i);
                return;
            }
            
            
            
            java.util.Date fecha;
                            System.out.println(
                    "[" + fechaStr + "]"
                );
                try {
                fecha = (java.util.Date) formato.parse(fechaStr);
            } catch (Exception e) {
                err("Fecha inválida", i);
                return;
            }

            Empleado existente =
                empleado_controller.obtenerPorDni(dni);

            if (existente != null) {
                err("Empleado ya registrado", i);
                return;
            }

            try {

                Rol rol = new Rol();

                rol.setNombre_rol(rolStr);

                switch (rolStr.toUpperCase()) {

                    case "ADMINISTRADOR":
                        rol.setId(1);
                        break;

                    case "CAJERO":
                        rol.setId(2);
                        break;

                    case "MESERO":
                        rol.setId(3);
                        break;

                    case "COCINERO":
                        rol.setId(4);
                        break;
                }

                empleado_controller.registrarEmpleado(
                        rol,
                        nombre,
                        apellido,
                        dni,
                        telefono,
                        email,
                        EstadoEmpleado.valueOf(estadoStr),
                        fecha
                );

            } catch (Exception e) {

                err("Error al guardar: " + e.getMessage(), i);

                return;
            }
        }

        cargarEmpleados();

        filaEditando = -1;

        modelo.fireTableDataChanged();

        JOptionPane.showMessageDialog(this,
                "Empleados guardados correctamente");
    }

    private void actualizarCards() {
        int total = modelo.getRowCount();
        int activos = 0, inactivos = 0;
        for (int i = 0; i < total; i++) {
            Object est = modelo.getValueAt(i, 7);
            if ("ACTIVO".equals(est)) activos++; else inactivos++;
        }
        if (lblTotalEmp  != null) lblTotalEmp.setText(String.valueOf(total));
        if (lblActivos   != null) lblActivos.setText(String.valueOf(activos));
        if (lblInactivos != null) lblInactivos.setText(String.valueOf(inactivos));
    }

    private void cargarEmpleados() {

        modelo.setRowCount(0);

        ArrayList<Empleado> lista =
            (ArrayList<Empleado>) empleado_controller.listarEmpleados();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Empleado e : lista) {

            modelo.addRow(new Object[]{
                e.getId(),
                e.getNombre(),
                e.getApellidos(),
                e.getDni(),
                e.getTelefono(),
                e.getEmail(),
                e.getRol().getNombre_rol(),
                e.getEstado().name(),
                sdf.format(e.getFecha_contratación())
            });
        }

        actualizarCards();
    }
    
    private void err(String msg, int fila) {

        JOptionPane.showMessageDialog(
                this,
                msg + " — fila " + (fila + 1),
                "Validación",
                JOptionPane.WARNING_MESSAGE
        );
    }
    
    private void actualizarEmpleado() {
        
        int fila = tabla.getSelectedRow();
        SimpleDateFormat formato =
            new SimpleDateFormat("dd/MM/yyyy");
        
        if (fila < 0) {

            JOptionPane.showMessageDialog(
                this,
                "Selecciona una fila."
            );

            return;
        }

        Object idObj = modelo.getValueAt(fila, COL_ID);

        if (idObj == null) {

            JOptionPane.showMessageDialog(
                this,
                "El empleado aún no existe en la BD."
            );

            return;
        }

        try {

            int id =
                Integer.parseInt(idObj.toString());
            System.out.println(id);
            String nombre =
                modelo.getValueAt(fila, COL_NOMBRE)
                      .toString()
                      .trim();

            String apellido =
                modelo.getValueAt(fila, COL_APELLIDO)
                      .toString()
                      .trim();

            String dni =
                modelo.getValueAt(fila, COL_DNI)
                      .toString()
                      .trim();

            String telefono =
                modelo.getValueAt(fila, COL_TELEFONO)
                      .toString()
                      .trim();

            String email =
                modelo.getValueAt(fila, COL_EMAIL)
                      .toString()
                      .trim();
            System.out.println("mierda de mierda xddxd");
            String rolStr =
                modelo.getValueAt(fila, COL_ROL)
                      .toString()
                      .trim();

            String estado =
                modelo.getValueAt(fila, COL_ESTADO)
                      .toString()
                      .trim();
            
            String fechaStr =
                modelo.getValueAt(fila, COL_FECHA).toString().trim();
            // VALIDACIONES
            if (nombre.isEmpty()) {
                err("Nombre vacío", fila);
                return;
            }

            if (apellido.isEmpty()) {
                err("Apellido vacío", fila);
                return;
            }

            if (!dni.matches("\\d{8}")) {
                err("DNI inválido", fila);
                return;
            }

            if (!email.contains("@")) {
                err("Email inválido", fila);
                return;
            }

            Rol rol = new Rol();

            rol.setNombre_rol(rolStr);
            System.out.println("mierda de mierda");
            switch (rolStr.toUpperCase()) {

                case "ADMINISTRADOR":
                    rol.setId(1);
                    break;

                case "CAJERO":
                    rol.setId(2);
                    break;

                case "MESERO":
                    rol.setId(3);
                    break;

                case "COCINERO":
                    rol.setId(4);
                    break;
            }
             java.util.Date fecha;
                            System.out.println(
                    "[" + fechaStr + "]"
                );
                try {
                fecha = (java.util.Date) formato.parse(fechaStr);
            } catch (Exception e) {
                err("Fecha inválida", fila);
                return;
            }

                
            empleado_controller.actualizarEmpleado(
                id,
                rol,
                EstadoEmpleado.valueOf(estado),
                nombre,
                apellido,
                dni,
                telefono,
                email,
                fecha
            );

            filaEditando = -1;

            modelo.fireTableDataChanged();

            JOptionPane.showMessageDialog(
                this,
                "Empleado actualizado correctamente"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                "Error al actualizar: " + e.getMessage()
            );
        }
    }
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(() -> new EmpleadosFrame().setVisible(true));
    }
}