package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.stream.IntStream;

public class ClientesFrame extends JFrame {

    // ── Paleta ───────────────────────────────────────────────────────────────
    private static final Color AZUL       = new Color(26, 35, 126);
    private static final Color AZUL_BTN   = new Color(33, 150, 243);
    private static final Color VERDE_BTN  = new Color(27, 120, 53);
    private static final Color GRIS_BTN   = new Color(80, 80, 80);
    private static final Color ROJO_BTN   = new Color(198, 40, 40);
    private static final Color PAGE_BG    = new Color(248, 249, 250);
    private static final Color CARD_BG    = Color.WHITE;
    private static final Color BORDER_CLR = new Color(220, 220, 220);
    private static final Color TEXT_MUTED = new Color(120, 120, 120);
    private static final Color ACTIVE_BG  = new Color(230, 235, 255);

    // ── Índices de columna ───────────────────────────────────────────────────
    private static final int COL_TIPO      = 0;
    private static final int COL_NOMBRE    = 1;
    private static final int COL_APELLIDO  = 2;
    private static final int COL_DNI       = 3;
    private static final int COL_TELEFONO  = 4;
    private static final int COL_EMAIL     = 5;
    private static final int COL_PUNTOS    = 6;
    private static final int COL_DIRECCION = 7;
    private static final int COL_FECHA     = 8;

    // ── Estado ───────────────────────────────────────────────────────────────
    private DefaultTableModel modelo;
    private JTable            tabla;
    private int               filaEditando = -1;

    private JLabel lblPageTitle;
    private JLabel lblPageDesc;
    private JLabel lblTotal, lblEmpresa, lblNatural;

    // ══════════════════════════════════════════════════════════════════════════
    public ClientesFrame() {
        setTitle("Sistema POS");
        setSize(1250, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel root = new JPanel(new BorderLayout());
        // ── ÚNICO CAMBIO: sidebar reutilizable en lugar del buildSidebar() propio
        root.add(new MenuSidebar(this, "Clientes"), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  ÁREA PRINCIPAL  (sin cambios)
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildMainArea() {
        JPanel w = new JPanel(new BorderLayout());
        w.setBackground(PAGE_BG);
        w.add(buildTopbar(), BorderLayout.NORTH);
        w.add(buildClientesPage(), BorderLayout.CENTER);
        return w;
    }

    private JPanel buildTopbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(CARD_BG);
        bar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        bar.setPreferredSize(new Dimension(0, 72));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBackground(CARD_BG);
        left.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 0));

        lblPageTitle = new JLabel("Gestión de Clientes");
        lblPageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblPageTitle.setForeground(new Color(20, 20, 20));

        lblPageDesc = new JLabel("Administra tus clientes y sus tipos");
        lblPageDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPageDesc.setForeground(TEXT_MUTED);

        left.add(lblPageTitle);
        left.add(Box.createVerticalStrut(2));
        left.add(lblPageDesc);
        bar.add(left, BorderLayout.CENTER);

        // Chip usuario
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 14));
        right.setBackground(CARD_BG);
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        chip.setBackground(new Color(26, 35, 126));
        chip.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 10));
        JLabel av = new JLabel("UF");
        av.setOpaque(true); av.setBackground(new Color(57, 73, 171));
        av.setForeground(Color.WHITE);
        av.setFont(new Font("Segoe UI", Font.BOLD, 12));
        av.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        JPanel ct = new JPanel(); ct.setLayout(new BoxLayout(ct, BoxLayout.Y_AXIS));
        ct.setBackground(new Color(26, 35, 126));
        JLabel cn = new JLabel("uwu fernandez");
        cn.setForeground(Color.WHITE); cn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JLabel cr = new JLabel("Administrador");
        cr.setForeground(new Color(255, 255, 255, 160)); cr.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        ct.add(cn); ct.add(cr);
        chip.add(av); chip.add(ct);
        right.add(chip);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  PÁGINA CLIENTES  (sin cambios)
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildClientesPage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setBackground(PAGE_BG);
        page.setBorder(BorderFactory.createEmptyBorder(22, 26, 22, 26));
        page.add(buildStatsRow(), BorderLayout.NORTH);
        page.add(buildTableCard(), BorderLayout.CENTER);
        return page;
    }

    private JPanel buildStatsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 16, 0));
        row.setOpaque(false);
        row.setPreferredSize(new Dimension(0, 100));

        lblTotal   = statCard(row, "Total Clientes",     "0", new Color(205, 200, 245), new Color(80, 55, 180));
        lblEmpresa = statCard(row, "Empresas",           "0", new Color(170, 230, 200), new Color(18, 115, 65));
        lblNatural = statCard(row, "Personas Naturales", "0", new Color(245, 185, 185), new Color(155, 28, 28));

        return row;
    }

    private JLabel statCard(JPanel parent, String titulo, String valor, Color iconBg, Color numColor) {
        JPanel card = new JPanel(new BorderLayout(14, 0));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        JPanel box = new JPanel();
        box.setBackground(iconBg);
        box.setPreferredSize(new Dimension(54, 54));
        card.add(box, BorderLayout.WEST);

        JPanel texts = new JPanel();
        texts.setLayout(new BoxLayout(texts, BoxLayout.Y_AXIS));
        texts.setBackground(CARD_BG);
        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lTit.setForeground(TEXT_MUTED);
        JLabel lVal = new JLabel(valor);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lVal.setForeground(numColor);
        texts.add(lTit);
        texts.add(Box.createVerticalStrut(4));
        texts.add(lVal);
        card.add(texts, BorderLayout.CENTER);

        parent.add(card);
        return lVal;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createLineBorder(BORDER_CLR));

        JPanel ch = new JPanel();
        ch.setLayout(new BoxLayout(ch, BoxLayout.Y_AXIS));
        ch.setBackground(CARD_BG);
        ch.setBorder(BorderFactory.createEmptyBorder(14, 18, 10, 18));
        JLabel cht = new JLabel("Lista de clientes");
        cht.setFont(new Font("Segoe UI", Font.BOLD, 15));
        cht.setForeground(new Color(30, 30, 30));
        JLabel chd = new JLabel("Haz clic en Agregar para añadir  ·  Clic sobre el Tipo para modificarlo");
        chd.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chd.setForeground(TEXT_MUTED);
        ch.add(cht); ch.add(Box.createVerticalStrut(3)); ch.add(chd);
        card.add(ch, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
            new String[]{"Tipo","Nombre","Apellido","DNI","Teléfono","Email","Puntos","Dirección","Fecha"}, 0
        ) {
            public boolean isCellEditable(int r, int c) {
                if (r != filaEditando) return false;
                if (c == COL_TIPO)     return false;
                if (c == COL_APELLIDO && "EMPRESA".equals(safeGet(r, COL_TIPO))) return false;
                return true;
            }
        };

        tabla = new JTable(modelo);
        tabla.setRowHeight(30);
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 235, 235));
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setBackground(CARD_BG);
        tabla.setSelectionBackground(new Color(232, 236, 255));
        tabla.setSelectionForeground(new Color(20, 20, 20));

        tabla.getTableHeader().setBackground(CARD_BG);
        tabla.getTableHeader().setForeground(new Color(90, 90, 90));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_CLR));
        tabla.getTableHeader().setReorderingAllowed(false);

        int[] widths = {85, 130, 120, 95, 95, 155, 65, 130, 100};
        for (int i = 0; i < widths.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        tabla.getColumnModel().getColumn(COL_TIPO).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                String txt = v == null ? "NATURAL" : v.toString();
                boolean esEmp = "EMPRESA".equals(txt);
                JLabel l = new JLabel((r == filaEditando ? "▸ " : "") + txt);
                l.setOpaque(true);
                l.setHorizontalAlignment(CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setBackground(sel ? new Color(232, 236, 255) : CARD_BG);
                l.setForeground(esEmp ? new Color(18, 115, 65) : AZUL);
                return l;
            }
        });

        tabla.getColumnModel().getColumn(COL_APELLIDO).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                boolean esEmp = "EMPRESA".equals(safeGet(r, COL_TIPO));
                JLabel l = new JLabel(esEmp ? "—" : (v == null ? "" : v.toString()));
                l.setOpaque(true);
                l.setHorizontalAlignment(CENTER);
                l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                l.setBackground(sel ? new Color(232, 236, 255) : (esEmp ? new Color(245, 245, 245) : CARD_BG));
                l.setForeground(esEmp ? new Color(180, 180, 180) : new Color(20, 20, 20));
                return l;
            }
        });

        tabla.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int col = tabla.columnAtPoint(e.getPoint());
                int row = tabla.rowAtPoint(e.getPoint());
                if (row == filaEditando && col == COL_TIPO) abrirModalTipo(row);
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));
        scroll.getViewport().setBackground(CARD_BG);
        card.add(scroll, BorderLayout.CENTER);
        card.add(buildBotones(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton bE  = btn("□  Eliminar", ROJO_BTN);
        JButton bEd = btn("□  Editar",   GRIS_BTN);
        JButton bG  = btn("□  Guardar",  VERDE_BTN);
        JButton bA  = btn("□  Agregar",  AZUL_BTN);

        bE .addActionListener(e -> eliminarFila());
        bEd.addActionListener(e -> activarEdicion());
        bG .addActionListener(e -> guardar());
        bA .addActionListener(e -> agregarFila());

        p.add(bE); p.add(bEd); p.add(bG); p.add(bA);
        return p;
    }

    private JButton btn(String txt, Color bg) {
        JButton b = new JButton(txt);
        b.setBackground(bg); b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false); b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        Color dk = bg.darker();
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(dk); }
            public void mouseExited (MouseEvent e) { b.setBackground(bg); }
        });
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MODAL TIPO  (sin cambios)
    // ══════════════════════════════════════════════════════════════════════════
    private void abrirModalTipo(int fila) {
        String actual = safeGet(fila, COL_TIPO);

        JDialog dlg = new JDialog(this, "Seleccionar Tipo de Cliente", true);
        dlg.setSize(440, 310);
        dlg.setLocationRelativeTo(this);
        dlg.setResizable(false);

        JPanel root = new JPanel(new BorderLayout());

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 14));
        header.setBackground(AZUL);
        JLabel ht = new JLabel("Seleccionar Tipo de Cliente");
        ht.setForeground(Color.WHITE);
        ht.setFont(new Font("Segoe UI", Font.BOLD, 15));
        header.add(ht);
        root.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(CARD_BG);
        body.setBorder(BorderFactory.createEmptyBorder(18, 18, 10, 18));

        ButtonGroup bg = new ButtonGroup();

        JPanel optNat = tipoOption("NATURAL", "Persona Natural",
            "DNI (8 dígitos)  ·  Nombre + Apellido requeridos", "NATURAL".equals(actual), bg);
        JPanel optEmp = tipoOption("EMPRESA", "Empresa",
            "RUC (11 dígitos)  ·  Razón Social  ·  Sin Apellido", "EMPRESA".equals(actual), bg);

        body.add(optNat);
        body.add(Box.createVerticalStrut(12));
        body.add(optEmp);
        root.add(body, BorderLayout.CENTER);

        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        foot.setBackground(PAGE_BG);
        foot.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR));

        JButton bCancel = btn("Cancelar",  GRIS_BTN);
        JButton bOk     = btn("Confirmar", AZUL);

        bCancel.addActionListener(e -> dlg.dispose());
        bOk.addActionListener(e -> {
            String sel = bg.getSelection() != null ? bg.getSelection().getActionCommand() : actual;
            modelo.setValueAt(sel, fila, COL_TIPO);
            if ("EMPRESA".equals(sel)) {
                tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Razón Social");
                tabla.getColumnModel().getColumn(COL_APELLIDO).setHeaderValue("Apellido");
                tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("RUC");
                modelo.setValueAt("", fila, COL_APELLIDO);
            } else {
                tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Nombre");
                tabla.getColumnModel().getColumn(COL_APELLIDO).setHeaderValue("Apellido");
                tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("DNI");
            }
            tabla.getTableHeader().repaint();
            tabla.repaint();
            actualizarStats();
            dlg.dispose();
        });

        foot.add(bCancel); foot.add(bOk);
        root.add(foot, BorderLayout.SOUTH);

        dlg.setContentPane(root);
        dlg.setVisible(true);
    }

    private JPanel tipoOption(String cmd, String titulo, String desc,
                               boolean selected, ButtonGroup group) {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(selected ? AZUL : new Color(200, 200, 200), selected ? 2 : 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JRadioButton rb = new JRadioButton();
        rb.setActionCommand(cmd);
        rb.setSelected(selected);
        rb.setBackground(CARD_BG);
        rb.setPreferredSize(new Dimension(22, 22));
        group.add(rb);

        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
        txt.setBackground(CARD_BG);

        JLabel lTit = new JLabel(titulo);
        lTit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTit.setForeground(new Color(20, 20, 20));

        JLabel lDesc = new JLabel(desc);
        lDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lDesc.setForeground(TEXT_MUTED);

        txt.add(lTit);
        txt.add(Box.createVerticalStrut(3));
        txt.add(lDesc);

        p.add(rb,  BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);

        MouseAdapter ma = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { rb.setSelected(true); }
            public void mouseEntered(MouseEvent e) { p.setBackground(ACTIVE_BG); txt.setBackground(ACTIVE_BG); }
            public void mouseExited (MouseEvent e) { p.setBackground(CARD_BG);   txt.setBackground(CARD_BG);   }
        };
        p.addMouseListener(ma);
        txt.addMouseListener(ma);
        lTit.addMouseListener(ma);
        lDesc.addMouseListener(ma);

        return p;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  LÓGICA DE TABLA  (sin cambios)
    // ══════════════════════════════════════════════════════════════════════════
    private void agregarFila() {
        String hoy = new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());
        modelo.addRow(new Object[]{"NATURAL","","","","","","0","",hoy});
        filaEditando = modelo.getRowCount() - 1;
        tabla.setRowSelectionInterval(filaEditando, filaEditando);
        tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Nombre");
        tabla.getColumnModel().getColumn(COL_APELLIDO).setHeaderValue("Apellido");
        tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("DNI");
        tabla.getTableHeader().repaint();
        modelo.fireTableDataChanged();
        actualizarStats();
    }

    private void eliminarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this,"Selecciona una fila."); return; }
        modelo.removeRow(fila);
        filaEditando = -1;
        tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Nombre");
        tabla.getColumnModel().getColumn(COL_APELLIDO).setHeaderValue("Apellido");
        tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("DNI");
        tabla.getTableHeader().repaint();
        modelo.fireTableDataChanged();
        actualizarStats();
    }

    private void activarEdicion() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) { JOptionPane.showMessageDialog(this,"Selecciona una fila."); return; }
        filaEditando = fila;
        String tipo = safeGet(fila, COL_TIPO);
        if ("EMPRESA".equals(tipo)) {
            tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Razón Social");
            tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("RUC");
        } else {
            tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Nombre");
            tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("DNI");
        }
        tabla.getTableHeader().repaint();
        modelo.fireTableDataChanged();
    }

    private void guardar() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
        fmt.setLenient(false);

        for (int i = 0; i < modelo.getRowCount(); i++) {
            String tipo     = safeGet(i, COL_TIPO);
            String nombre   = safeGet(i, COL_NOMBRE).trim().toUpperCase();
            String apellido = safeGet(i, COL_APELLIDO).trim().toUpperCase();
            String dniRuc   = safeGet(i, COL_DNI).trim();
            String tel      = safeGet(i, COL_TELEFONO).trim();
            String email    = safeGet(i, COL_EMAIL).trim();
            String puntos   = safeGet(i, COL_PUNTOS).trim();
            String fecha    = safeGet(i, COL_FECHA).trim();
            boolean esEmpresa = "EMPRESA".equals(tipo);

            modelo.setValueAt(nombre, i, COL_NOMBRE);
            if (!esEmpresa) modelo.setValueAt(apellido, i, COL_APELLIDO);

            if (nombre.isEmpty()) { err(esEmpresa ? "Razón Social vacía" : "Nombre vacío", i); return; }
            if (!esEmpresa && apellido.isEmpty()) { err("Apellido vacío", i); return; }

            int dniLen = esEmpresa ? 11 : 8;
            if (!dniRuc.matches("\\d{" + dniLen + "}")) {
                err(esEmpresa ? "RUC debe tener 11 dígitos" : "DNI debe tener 8 dígitos", i); return;
            }
            if (tel.isEmpty())        { err("Teléfono vacío",            i); return; }
            if (!email.contains("@")) { err("Email inválido",            i); return; }
            try { Integer.parseInt(puntos); }
            catch (Exception ex)      { err("Puntos debe ser un número", i); return; }
            try { fmt.parse(fecha); }
            catch (Exception ex)      { err("Fecha inválida (dd/MM/yyyy)", i); return; }
        }

        filaEditando = -1;
        tabla.getColumnModel().getColumn(COL_NOMBRE).setHeaderValue("Nombre");
        tabla.getColumnModel().getColumn(COL_APELLIDO).setHeaderValue("Apellido");
        tabla.getColumnModel().getColumn(COL_DNI).setHeaderValue("DNI");
        tabla.getTableHeader().repaint();
        modelo.fireTableDataChanged();
        JOptionPane.showMessageDialog(this, "Datos guardados correctamente ✓",
                "Guardado", JOptionPane.INFORMATION_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UTILIDADES  (sin cambios)
    // ══════════════════════════════════════════════════════════════════════════
    private String safeGet(int r, int c) {
        Object v = modelo.getValueAt(r, c);
        return v == null ? "" : v.toString().trim();
    }

    private void err(String msg, int row) {
        JOptionPane.showMessageDialog(this, msg + " — fila " + (row + 1),
                "Validación", JOptionPane.WARNING_MESSAGE);
    }

    private void actualizarStats() {
        int total = modelo.getRowCount();
        long emp  = IntStream.range(0, total).filter(i -> "EMPRESA".equals(safeGet(i, COL_TIPO))).count();
        long nat  = total - emp;
        lblTotal  .setText(String.valueOf(total));
        lblEmpresa.setText(String.valueOf(emp));
        lblNatural.setText(String.valueOf(nat));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MAIN
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new ClientesFrame().setVisible(true);
        });
    }
}