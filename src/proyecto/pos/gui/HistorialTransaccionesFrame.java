package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.model.Venta;

public class HistorialTransaccionesFrame extends JFrame {

    // --- PALETA DE COLORES DISEÑO INTERFAZ ---
    private static final Color AZUL         = new Color(26, 83, 160);
    private static final Color AZUL_CLARO   = new Color(232, 241, 255);
    private static final Color FONDO        = new Color(246, 248, 251);
    private static final Color BORDE        = new Color(225, 229, 236);
    private static final Color TEXTO        = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE  = new Color(105, 113, 128);
    private static final Color VERDE        = new Color(40, 167, 69);
    private static final Color ROJO         = new Color(220, 53, 69);

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JLabel lblFooter;

    public HistorialTransaccionesFrame() {
        FlatLightLaf.setup();
        configurarVentana();
        construirInterfaz();
        cargarDatosDesdeBD();
    }

    private void configurarVentana() {
        setTitle("Historial de Transacciones y Ventas");
        setSize(1200, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        // Sidebar lateral
        root.add(new MenuSidebar(this, "Historial"), BorderLayout.WEST);

        // Contenedor principal
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(26, 28, 24, 28));

        contenedor.add(crearHeader(), BorderLayout.NORTH);
        contenedor.add(crearPanelTabla(), BorderLayout.CENTER);

        root.add(contenedor, BorderLayout.CENTER);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Historial de Ventas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Visualiza, busca y genera boletas de los pedidos realizados");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(subtitulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        derecha.setOpaque(false);
        
        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JLabel lblAvatar = new JLabel(MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 36, 36));

        derecha.add(lblUsuario);
        derecha.add(lblAvatar);

        header.add(titulos, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);
        return header;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)));

        JPanel barraTop = new JPanel(new BorderLayout());
        barraTop.setOpaque(false);
        barraTop.setBorder(new EmptyBorder(0, 0, 14, 0));

        JLabel lblListado = new JLabel("Registro de Comprobantes");
        lblListado.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblListado.setForeground(TEXTO);

        barraTop.add(lblListado, BorderLayout.WEST);
        barraTop.add(crearBuscador(), BorderLayout.EAST);

        panel.add(barraTop, BorderLayout.NORTH);
        panel.add(crearTablaScroll(), BorderLayout.CENTER);
        panel.add(crearFooter(), BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearBuscador() {
        JPanel panel = new JPanel(new BorderLayout(8, 0));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 36));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 10, 0, 10)));

        JLabel icono = new JLabel("🔍");
        txtBuscar = new JTextField();
        txtBuscar.setBorder(null);
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar por TRX, Cajero o Estado...");
        
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { aplicarFiltro(); }
            public void removeUpdate(DocumentEvent e)  { aplicarFiltro(); }
            public void changedUpdate(DocumentEvent e) { aplicarFiltro(); }
        });

        panel.add(icono, BorderLayout.WEST);
        panel.add(txtBuscar, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearTablaScroll() {
        String[] columnas = {
            "Código TRX", "Fecha / Hora", "Cajero", "Mesa", "Subtotal", "IGV", "Monto Total", "Acción"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { 
                return c == 7; // Solo la columna "Acción" es clickable
            }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(45);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 238, 244));
        tabla.setSelectionBackground(AZUL_CLARO);

        JTableHeader th = tabla.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 40));

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        // Renderizadores
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        
        for (int i = 0; i < 7; i++) {
            if (i != 2) tabla.getColumnModel().getColumn(i).setCellRenderer(centro);
        }

        // Parche: Botón para ver boleta
        tabla.getColumnModel().getColumn(7).setCellRenderer(new BoletaButtonRenderer());
        tabla.getColumnModel().getColumn(7).setCellEditor(new BoletaButtonEditor());

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 0, 0, 0));

        lblFooter = new JLabel("Mostrando 0 comprobantes");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(TEXTO_SUAVE);

        footer.add(lblFooter, BorderLayout.WEST);
        return footer;
    }

    public void cargarDatosDesdeBD() {
        modeloTabla.setRowCount(0);
        Connection con = new DatabaseConnection().conectar();
        VentaDAOImpl dao = new VentaDAOImpl(con);
        List<Venta> lista = dao.listar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Venta v : lista) {
            Object[] fila = {
                "TRX-" + String.format("%04d", v.getVentaId()),
                sdf.format(v.getFecha()),
                v.getEmpleado().getNombre(),
                v.getMesa().getEstado_mesa() == 0 ? "Llevar" : "Mesa " + v.getMesa().getEstado_mesa(),
                String.format(java.util.Locale.US, "S/ %.2f", v.getSubtotal()),
                String.format(java.util.Locale.US, "S/ %.2f", v.getIgv()),
                String.format(java.util.Locale.US, "S/ %.2f", v.getTotal()),
                "VER BOLETA"
            };
            modeloTabla.addRow(fila);
        }
        actualizarFooter();
    }

    private void aplicarFiltro() {
        if (sorter == null) return;
        String texto = txtBuscar.getText().trim();
        sorter.setRowFilter(texto.isEmpty() ? null : RowFilter.regexFilter("(?i)" + Pattern.quote(texto)));
        actualizarFooter();
    }

    private void actualizarFooter() {
        lblFooter.setText("Mostrando " + tabla.getRowCount() + " de " + modeloTabla.getRowCount() + " registros");
    }

    // --- CLASES INTERNAS PARA EL BOTÓN ---

    private class BoletaButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public BoletaButtonRenderer() {
            setText("Ver Boleta");
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setForeground(AZUL);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(AZUL));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

private class BoletaButtonEditor extends DefaultCellEditor {
        private JButton btn;
        private int rowSel;

        public BoletaButtonEditor() {
            super(new JCheckBox());
            btn = new JButton("Ver Boleta");
            btn.addActionListener(e -> {
                // 1. Detenemos la edición de la celda inmediatamente para liberar la tabla
                fireEditingStopped();
                
                // 2. Obtenemos los datos de la fila
                String val = tabla.getValueAt(rowSel, 0).toString();
                int id = Integer.parseInt(val.replace("TRX-", ""));
                
               // System.out.println("DEBUG: Intentando abrir boleta para TRX ID: " + id); // Para que verifiques en el output

                // 3. Usamos invokeLater para que el diálogo se abra después de que Swing se refresque
                SwingUtilities.invokeLater(() -> {
                    Connection con = new DatabaseConnection().conectar();
                    if (con != null) {
                        Boleta_View_GUI boleta = new Boleta_View_GUI(HistorialTransaccionesFrame.this, id, con);
                        boleta.setVisible(true); // ¡Aquí debería aparecer!
                    } else {
                        System.err.println("Error: No hay conexión a la base de datos.");
                    }
                });
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            // Sincronizamos la fila correcta incluso si la tabla está filtrada
            this.rowSel = table.convertRowIndexToModel(row); 
            return btn;
        }
        
        // Esto es importante para que el botón no se quede presionado visualmente
        @Override
        public Object getCellEditorValue() {
            return "Ver Boleta";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HistorialTransaccionesFrame().setVisible(true));
    }
}