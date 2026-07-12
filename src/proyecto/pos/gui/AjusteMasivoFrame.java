package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.PlatoController;
import proyecto.pos.model.CategoriaMenu;
import proyecto.pos.model.Plato;
import proyecto.pos.service.PlatoService;

/**
 * HU-05 — Ajuste Masivo de Precios
 * Criterio 1: Inicio de transacción con bloqueo de registros en BD.
 * Criterio 2: Validación de integridad en cada ítem procesado.
 * Criterio 3: Commit exitoso al actualizar todos los registros.
 * Criterio 4: Rollback automático ante cualquier error de conexión.
 */
public class AjusteMasivoFrame extends JFrame {

    // ─── PALETA (misma que el resto del proyecto) ─────────────────────────────
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
    private static final Color AMARILLO_BG = new Color(255, 249, 231);
    private static final Color AMARILLO_TXT= new Color(133, 100, 4);

    // ─── COMPONENTES ──────────────────────────────────────────────────────────
    private JTable tablaPlatos;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JSpinner   spinPorcentaje;
    private JComboBox<String> cboCategoria;
    private JLabel     lblResumen;
    private JButton    btnEjecutar;
    private JButton    btnSelAll;
    private JLabel     lblFooter;

    private Connection      conexion;
    private PlatoController platoController;
    private PlatoService    platoService;
    private List<Plato>     todosLosPlatos = new ArrayList<>();

    // ─── COLUMNAS ─────────────────────────────────────────────────────────────
    private static final int COL_CHECK     = 0;
    private static final int COL_ID        = 1;
    private static final int COL_NOMBRE    = 2;
    private static final int COL_CATEGORIA = 3;
    private static final int COL_PRECIO    = 4;
    private static final int COL_PREVIEW   = 5;

    public AjusteMasivoFrame() {
        FlatLightLaf.setup();
        conexion = new DatabaseConnection().conectar();
        platoController = new PlatoController(conexion);
        platoService    = new PlatoService(conexion);
        configurarVentana();
        construirInterfaz();
        cargarPlatos();
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void configurarVentana() {
        setTitle("Ajuste Masivo de Precios");
        setSize(1250, 760);
        setMinimumSize(new Dimension(1050, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(new MenuSidebar(this, "AjusteMasivo"), BorderLayout.WEST);

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

        JLabel titulo = new JLabel("Ajuste Masivo de Precios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel sub = new JLabel("Transacción con bloqueo, validación de integridad, commit y rollback automático");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(sub);

        // Usuario info
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
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setOpaque(false);
        p.add(crearPanelIzquierdo(), BorderLayout.CENTER);
        p.add(crearPanelDerecho(),   BorderLayout.EAST);
        return p;
    }

    // ── PANEL IZQUIERDO: tabla de platos ──────────────────────────────────────
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

        JLabel lblTit = new JLabel("Catálogo de Platos");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(TEXTO);

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controles.setOpaque(false);

        // Filtro categoría
        String[] cats = {"Todas las categorías", "Platos principales", "Entradas", "Bebidas", "Postres"};
        cboCategoria = new JComboBox<>(cats);
        cboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboCategoria.setPreferredSize(new Dimension(170, 32));
        cboCategoria.addActionListener(e -> filtrarTabla());

        // Buscador
        JPanel panelBuscar = new JPanel(new BorderLayout(6, 0));
        panelBuscar.setBackground(Color.WHITE);
        panelBuscar.setPreferredSize(new Dimension(230, 32));
        panelBuscar.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 8, 0, 8)));
        JLabel icoSearch = new JLabel("🔍");
        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar plato...");
        txtBuscar.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filtrarTabla(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ filtrarTabla(); }
        });
        panelBuscar.add(icoSearch, BorderLayout.WEST);
        panelBuscar.add(txtBuscar, BorderLayout.CENTER);

        // Seleccionar todo
        btnSelAll = new JButton("☑ Seleccionar todo");
        btnSelAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnSelAll.setForeground(AZUL);
        btnSelAll.setBackground(AZUL_CLARO);
        btnSelAll.setBorder(new EmptyBorder(6, 12, 6, 12));
        btnSelAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSelAll.setFocusPainted(false);
        btnSelAll.addActionListener(e -> toggleSeleccionTodo());

        controles.add(cboCategoria);
        controles.add(panelBuscar);
        controles.add(btnSelAll);

        barraTop.add(lblTit,    BorderLayout.WEST);
        barraTop.add(controles, BorderLayout.EAST);

        card.add(barraTop, BorderLayout.NORTH);
        card.add(crearTablaScroll(), BorderLayout.CENTER);

        // Footer
        lblFooter = new JLabel("Cargando...");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(TEXTO_SUAVE);
        lblFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(lblFooter, BorderLayout.SOUTH);

        return card;
    }

    private JScrollPane crearTablaScroll() {
        String[] cols = {"", "ID", "Nombre del Plato", "Categoría", "Precio Actual", "Precio con Ajuste"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public Class<?> getColumnClass(int c) {
                return c == COL_CHECK ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int r, int c) { return c == COL_CHECK; }
        };

        tablaPlatos = new JTable(modeloTabla);
        tablaPlatos.setRowHeight(38);
        tablaPlatos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaPlatos.setShowVerticalLines(false);
        tablaPlatos.setGridColor(new Color(235, 238, 244));
        tablaPlatos.setSelectionBackground(AZUL_CLARO);

        JTableHeader th = tablaPlatos.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 38));

        // Anchos de columna
        tablaPlatos.getColumnModel().getColumn(COL_CHECK).setPreferredWidth(36);
        tablaPlatos.getColumnModel().getColumn(COL_ID).setPreferredWidth(50);
        tablaPlatos.getColumnModel().getColumn(COL_NOMBRE).setPreferredWidth(200);
        tablaPlatos.getColumnModel().getColumn(COL_CATEGORIA).setPreferredWidth(130);
        tablaPlatos.getColumnModel().getColumn(COL_PRECIO).setPreferredWidth(110);
        tablaPlatos.getColumnModel().getColumn(COL_PREVIEW).setPreferredWidth(120);

        sorter = new TableRowSorter<>(modeloTabla);
        tablaPlatos.setRowSorter(sorter);

        // Renderer para "Precio con Ajuste" en verde/rojo
        tablaPlatos.getColumnModel().getColumn(COL_PREVIEW).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String txt = val == null ? "" : val.toString();
                if (txt.startsWith("—") || txt.isEmpty()) {
                    lbl.setForeground(TEXTO_SUAVE);
                } else {
                    lbl.setForeground(VERDE);
                    lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
                }
                return lbl;
            }
        });

        // Renderer precio actual centrado
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaPlatos.getColumnModel().getColumn(COL_ID).setCellRenderer(centro);
        tablaPlatos.getColumnModel().getColumn(COL_PRECIO).setCellRenderer(centro);

        // Actualizar preview al cambiar check
        modeloTabla.addTableModelListener(e -> {
            if (e.getColumn() == COL_CHECK) {
                actualizarPreviewPrecios();
                actualizarResumen();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaPlatos);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // ── PANEL DERECHO: configuración ──────────────────────────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(310, 0));

        panel.add(crearCardConfiguracion());
        panel.add(Box.createVerticalGlue()); // llena el espacio restante para que no quede vacío abajo

        return panel;
    }

    private JPanel crearCardConfiguracion() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 18, 18, 18)));
        card.setMaximumSize(new Dimension(310, 999));

        JLabel tit = new JLabel("Parámetros del ajuste");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(TEXTO);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("— Ajuste masivo con transacción");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXTO_SUAVE);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(tit);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(16));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(20));

        // Porcentaje
        JLabel lblPct = new JLabel("Porcentaje de ajuste (%)");
        lblPct.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPct.setForeground(TEXTO_SUAVE);
        lblPct.setAlignmentX(LEFT_ALIGNMENT);

        SpinnerNumberModel modelo = new SpinnerNumberModel(5.0, -99.0, 1000.0, 0.5);
        spinPorcentaje = new JSpinner(modelo);
        spinPorcentaje.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinPorcentaje.setMaximumSize(new Dimension(999, 36));
        spinPorcentaje.setAlignmentX(LEFT_ALIGNMENT);
        spinPorcentaje.addChangeListener(e -> actualizarPreviewPrecios());

        card.add(lblPct);
        card.add(Box.createVerticalStrut(6));
        card.add(spinPorcentaje);
        card.add(Box.createVerticalStrut(22));

        // Resumen
        lblResumen = new JLabel("<html><div style='text-align:center;color:#1a53a0;'>"
                + "<b>0 platos</b> seleccionados</div></html>");
        lblResumen.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblResumen.setHorizontalAlignment(SwingConstants.CENTER);
        lblResumen.setAlignmentX(LEFT_ALIGNMENT);
        lblResumen.setMaximumSize(new Dimension(999, 36));
        lblResumen.setBorder(new EmptyBorder(8, 0, 8, 0));
        card.add(lblResumen);

        card.add(Box.createVerticalStrut(24));

        // Botón ejecutar
        btnEjecutar = new JButton("⚡ Ejecutar Ajuste Masivo");
        btnEjecutar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnEjecutar.setForeground(Color.WHITE);
        btnEjecutar.setBackground(AZUL);
        btnEjecutar.setBorder(new EmptyBorder(10, 16, 10, 16));
        btnEjecutar.setFocusPainted(false);
        btnEjecutar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEjecutar.setMaximumSize(new Dimension(999, 44));
        btnEjecutar.setAlignmentX(LEFT_ALIGNMENT);
        btnEjecutar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnEjecutar.setBackground(AZUL_HOVER); }
            public void mouseExited (MouseEvent e) { btnEjecutar.setBackground(AZUL); }
        });
        btnEjecutar.addActionListener(e -> ejecutarAjusteMasivo());
        card.add(btnEjecutar);

        return card;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(999, 1));
        sep.setForeground(BORDE);
        return sep;
    }

    // ── CARGA DE DATOS ────────────────────────────────────────────────────────
    private void cargarPlatos() {
        todosLosPlatos = platoController.listarPlatos();
        modeloTabla.setRowCount(0);
        for (Plato p : todosLosPlatos) {
            modeloTabla.addRow(new Object[]{
                false,
                p.getPlatoId(),
                p.getNombre(),
                p.getCategoria() != null ? p.getCategoria().getNombre() : "—",
                String.format("S/. %.2f", p.getPrecio()),
                "—"
            });
        }
        actualizarFooter();
    }

    // ── FILTROS ───────────────────────────────────────────────────────────────
    private void filtrarTabla() {
        String texto = txtBuscar.getText().trim().toLowerCase();
        String cat   = cboCategoria.getSelectedItem().toString();

        sorter.setRowFilter(RowFilter.andFilter(java.util.Arrays.asList(
            texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(texto), COL_NOMBRE),
            cat.equals("Todas las categorías") ? null : RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(cat), COL_CATEGORIA)
        ).stream().filter(f -> f != null).collect(java.util.stream.Collectors.toList())));

        actualizarFooter();
    }

    private void toggleSeleccionTodo() {
        boolean alguno = false;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Boolean) modeloTabla.getValueAt(i, COL_CHECK)) { alguno = true; break; }
        }
        boolean nuevo = !alguno;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            modeloTabla.setValueAt(nuevo, i, COL_CHECK);
        }
    }

    // ── PREVIEW DE PRECIOS ────────────────────────────────────────────────────
    private void actualizarPreviewPrecios() {
        double pct = ((Number) spinPorcentaje.getValue()).doubleValue();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            boolean seleccionado = (Boolean) modeloTabla.getValueAt(i, COL_CHECK);
            if (seleccionado) {
                String precioStr = modeloTabla.getValueAt(i, COL_PRECIO).toString()
                        .replace("S/. ", "").replace(",", ".");
                try {
                    double actual = Double.parseDouble(precioStr);
                    double nuevo  = actual * (1 + pct / 100.0);
                    modeloTabla.setValueAt(String.format("S/. %.2f", nuevo), i, COL_PREVIEW);
                } catch (NumberFormatException ignored) {}
            } else {
                modeloTabla.setValueAt("—", i, COL_PREVIEW);
            }
        }
    }

    private void actualizarResumen() {
        int cnt = 0;
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Boolean) modeloTabla.getValueAt(i, COL_CHECK)) cnt++;
        }
        double pct = ((Number) spinPorcentaje.getValue()).doubleValue();
        String signo = pct >= 0 ? "+" : "";
        lblResumen.setText("<html><div style='color:#1a53a0;'>"
                + "<b>" + cnt + " plato" + (cnt != 1 ? "s" : "") + "</b> seleccionados"
                + "<br><font style='font-size:10px;color:#696971;'>Ajuste: " + signo + String.format("%.1f", pct) + "%</font>"
                + "</div></html>");
    }

    private void actualizarFooter() {
        int total    = modeloTabla.getRowCount();
        int visibles = tablaPlatos.getRowCount();
        lblFooter.setText("Mostrando " + visibles + " de " + total + " platos");
    }

    // ── EJECUCIÓN DEL AJUSTE MASIVO (HU-05) ──────────────────────────────────
    private void ejecutarAjusteMasivo() {
        // Recoger IDs seleccionados
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < modeloTabla.getRowCount(); i++) {
            if ((Boolean) modeloTabla.getValueAt(i, COL_CHECK)) {
                ids.add(Integer.parseInt(modeloTabla.getValueAt(i, COL_ID).toString()));
            }
        }

        if (ids.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione al menos un plato para aplicar el ajuste.",
                    "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        float porcentaje = ((Number) spinPorcentaje.getValue()).floatValue();

        // Confirmación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "<html>¿Ejecutar ajuste masivo de <b>" + String.format("%.1f", porcentaje)
                + "%</b> en <b>" + ids.size() + " plato(s)</b>?<br><br>"
                + "<small>Se iniciará una transacción con bloqueo de registros en BD.<br>"
                + "Cualquier error provocará rollback automático.</small></html>",
                "Confirmar Ajuste Masivo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        btnEjecutar.setEnabled(false);
        btnEjecutar.setText("⏳ Procesando transacción...");

        // Ejecutar en hilo aparte para no bloquear la UI
        SwingWorker<List<String>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<String> doInBackground() {
                try {
                    return platoService.ajusteMasivoPrecios(ids, porcentaje);
                } catch (IllegalArgumentException ex) {
                    List<String> err = new ArrayList<>();
                    err.add("ERROR|Parámetro inválido: " + ex.getMessage());
                    return err;
                }
            }

            @Override
            protected void done() {
                try {
                    List<String> log = get();
                    boolean hayCommit   = false;
                    boolean hayRollback = false;

                    for (String linea : log) {
                        String tipo = linea.split("\\|", 2)[0];
                        if (tipo.equals("COMMIT"))   hayCommit = true;
                        if (tipo.equals("ROLLBACK")) hayRollback = true;
                    }

                    // Recargar tabla con nuevos precios
                    cargarPlatos();

                    if (hayCommit) {
                        mostrarResultado(true, "Commit exitoso. Todos los precios fueron actualizados correctamente.");
                    } else if (hayRollback) {
                        mostrarResultado(false, "Rollback ejecutado. Los precios originales fueron restaurados.");
                    }

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AjusteMasivoFrame.this,
                            "Error inesperado: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnEjecutar.setEnabled(true);
                    btnEjecutar.setText("⚡ Ejecutar Ajuste Masivo");
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

        JLabel msg = new JLabel("<html><b>" + (exito ? "Ajuste completado" : "Ajuste revertido") + "</b><br>"
                + "<font style='font-size:11px;'>" + mensaje + "</font></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(exito ? VERDE_TEXT : ROJO);

        msgPanel.add(icono, BorderLayout.WEST);
        msgPanel.add(msg,   BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, msgPanel,
                exito ? "— Commit exitoso" : "— Rollback ejecutado",
                exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AjusteMasivoFrame().setVisible(true));
    }
}