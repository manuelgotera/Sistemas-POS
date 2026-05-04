package proyecto.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import java.awt.Image;


public class HistorialTransaccionesFrame extends JFrame {

    private static final Color AZUL       = new Color(26, 83, 160);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO      = new Color(246, 248, 251);
    private static final Color BORDE      = new Color(225, 228, 233);
    private static final Color VERDE_BG   = new Color(225, 245, 238);
    private static final Color VERDE_TEXT = new Color(15, 110, 86);

    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtBuscar;
    private JComboBox<String> cboPeriodo;
    private JLabel lblTotalTrx;
    private JLabel lblVentaTotales;
    private JLabel lblBruto;
    private JLabel lblFooter;
    private JLabel lblHora;

    public HistorialTransaccionesFrame() {
        configurarVentana();
        construirInterfaz();
        cargarDatosDemo();
        iniciarReloj();
    }

    private void configurarVentana() {
        setTitle("Historial de Transacciones");
        setSize(1180, 720);
        setMinimumSize(new Dimension(1000, 620));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);
        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }


    // ─── NUEVO SIDEBAR UNIFICADO ────────────────────────────────────────────────────────

        private JPanel crearSidebar() {
            JPanel sidebar = new JPanel();
            sidebar.setPreferredSize(new Dimension(220, 0));
            sidebar.setBackground(new Color(250, 250, 250));
            sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));
            sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

            // Cabecera del Sidebar (Logo y texto)
            JPanel cabeceraSidebar = new JPanel(new BorderLayout());
            cabeceraSidebar.setBackground(new Color(250, 250, 250));
            cabeceraSidebar.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            cabeceraSidebar.setMaximumSize(new Dimension(220, 75));

            JPanel panelLogoTextos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            panelLogoTextos.setBackground(new Color(250, 250, 250));

            JLabel lblIconoPOS = new JLabel(redimensionarIcono("/img/icono_carrito_blanco.png", 20, 20), SwingConstants.CENTER);
            lblIconoPOS.setPreferredSize(new Dimension(40, 40));
            lblIconoPOS.setBackground(new Color(26, 79, 156)); 
            lblIconoPOS.setOpaque(true);

            JPanel textosPOS = new JPanel();
            textosPOS.setLayout(new BoxLayout(textosPOS, BoxLayout.Y_AXIS));
            textosPOS.setBackground(new Color(250, 250, 250));
            JLabel lblPOS = new JLabel("Pos");
            lblPOS.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblPOS.setForeground(new Color(26, 79, 156));
            JLabel lblDesc = new JLabel("Sistema de Caja");
            lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblDesc.setForeground(Color.GRAY);

            textosPOS.add(lblPOS);
            textosPOS.add(lblDesc);

            panelLogoTextos.add(lblIconoPOS);
            panelLogoTextos.add(textosPOS);

            JButton btnColapsar = new JButton("≪");
            btnColapsar.setFocusPainted(false);
            btnColapsar.setBorderPainted(false);
            btnColapsar.setBackground(new Color(235, 235, 235));
            btnColapsar.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnColapsar.setPreferredSize(new Dimension(35, 35));

            cabeceraSidebar.add(panelLogoTextos, BorderLayout.CENTER);
            cabeceraSidebar.add(btnColapsar, BorderLayout.EAST);

            JPanel divisorTop = new JPanel();
            divisorTop.setMaximumSize(new Dimension(220, 1));
            divisorTop.setBackground(new Color(230, 230, 230));

            sidebar.add(cabeceraSidebar);
            sidebar.add(divisorTop);

            // Agregamos los botones de navegación
            actualizarNavegacionSidebar(sidebar); 

            return sidebar;
        }

        private void actualizarNavegacionSidebar(JPanel sidebar) {
            sidebar.add(Box.createVerticalStrut(180)); // Tu espaciado superior

            // ¡ATENCIÓN AQUÍ! "Historial de Trans." está en TRUE, los demás en FALSE
            JButton btnCajero = crearBotonMenu("Cajero", false, "/img/icon_cart.png");
            JButton btnStock = crearBotonMenu("Artículos y Stock", false, "/img/icon_box.png");
            JButton btnHistorial = crearBotonMenu("Historial de Trans.", true, "/img/icon_history.png"); 
            JButton btnReportes = crearBotonMenu("Reportes", false, "/img/icon_chart.png");
            JButton btnGastos = crearBotonMenu("Gastos", false, "/img/icon_wallet.png");
            JButton btnConfig = crearBotonMenu("Configuración", false, "/img/icon_settings.png");

            // ACCIONES DE NAVEGACIÓN
            btnCajero.addActionListener(e -> {
                new Caja_GUI().setVisible(true);
                this.dispose(); 
            });

            btnStock.addActionListener(e -> {
                new ArticulosStockFrame().setVisible(true);
                this.dispose(); 
            });

            // Nota: btnHistorial no lleva ActionListener porque ya estamos en esta ventana.

            sidebar.add(btnCajero);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnStock);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnHistorial);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnReportes);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnGastos);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnConfig);

            sidebar.add(Box.createVerticalGlue()); // Empuja lo demás hacia abajo

            // SECCIÓN INFERIOR
            JPanel divisorBottom = new JPanel();
            divisorBottom.setMaximumSize(new Dimension(220, 1));
            divisorBottom.setBackground(new Color(230, 230, 230));
            sidebar.add(divisorBottom);
            sidebar.add(Box.createVerticalStrut(10));

            JButton btnModo = crearBotonMenu("Mode Tampilan", false, "/img/icon_bell.png");
            JButton btnSalir = crearBotonMenu("Salir", false, "/img/icon_logout.png");
            btnSalir.setForeground(new Color(220, 53, 69)); 

            btnSalir.addActionListener(e -> System.exit(0));

            sidebar.add(btnModo);
            sidebar.add(Box.createVerticalStrut(5));
            sidebar.add(btnSalir);
            sidebar.add(Box.createVerticalStrut(20));

            sidebar.revalidate();
            sidebar.repaint();
        }

        private JButton crearBotonMenu(String texto, boolean seleccionado, String iconPath) {
            JButton btn = new JButton(texto);

            if (iconPath != null && !iconPath.isEmpty()) {
                btn.setIcon(redimensionarIcono(iconPath, 20, 20));
                btn.setIconTextGap(15);
            }

            btn.setMaximumSize(new Dimension(190, 45));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setHorizontalAlignment(SwingConstants.LEFT); 
            btn.setBorder(new EmptyBorder(0, 15, 0, 0));

            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

            if (seleccionado) {
                btn.setBackground(new Color(235, 240, 255));
                btn.setForeground(new Color(26, 79, 156));
                btn.setFont(new Font("Segoe UI", Font.BOLD, 17)); // Tu nuevo tamaño de fuente
                btn.putClientProperty("JButton.buttonType", "roundRect"); 
            } else {
                btn.setBackground(new Color(250, 250, 250));
                btn.setForeground(new Color(80, 80, 80));
                btn.setFont(new Font("Segoe UI", Font.PLAIN, 17)); // Tu nuevo tamaño de fuente
                btn.setBorderPainted(false);
            }

            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (!seleccionado) btn.setBackground(new Color(242, 242, 242));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (!seleccionado) btn.setBackground(new Color(250, 250, 250));
                }
            });

            return btn;
        }

        private ImageIcon redimensionarIcono(String path, int width, int height) {
            try {
                java.net.URL imgURL = getClass().getResource(path);
                if (imgURL != null) {
                    ImageIcon iconOriginal = new ImageIcon(imgURL);
                    Image img = iconOriginal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                    return new ImageIcon(img);
                }
            } catch (Exception e) {
                System.err.println("No se encontró el icono: " + path);
            }
            return null; 
        }

    // CONTENIDO 
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

        JLabel titulo = new JLabel("Historial de transacciones");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JLabel subtitulo = new JLabel("Gestionar datos de productos e inventario");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.GRAY);

        titulos.add(titulo, BorderLayout.NORTH);
        titulos.add(subtitulo, BorderLayout.CENTER);

        JPanel derecho = new JPanel(new GridBagLayout());
        derecho.setBackground(FONDO);
        derecho.setBorder(new EmptyBorder(0, 0, 0, 0));

        JLabel campana = new JLabel("");
        campana.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        campana.setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHora.setOpaque(true);
        lblHora.setBackground(Color.WHITE);
        lblHora.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(6, 12, 6, 12)
        ));

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

        JLabel nombrePerfil = new JLabel("<html><b>uwu fernandez</b><br><font color='gray'>Cajero</font></html>");
        nombrePerfil.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        perfilPanel.add(circulo, BorderLayout.WEST);
        perfilPanel.add(nombrePerfil, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 6, 0, 6);
        gbc.gridx = 0; derecho.add(campana, gbc);
        gbc.gridx = 1; derecho.add(lblHora, gbc);
        gbc.gridx = 2; derecho.add(perfilPanel, gbc);

        header.add(titulos, BorderLayout.WEST);
        header.add(derecho, BorderLayout.EAST);
        return header;
    }

    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(18, 18, 18, 18)
        ));
        panel.add(crearStatsYFiltros(), BorderLayout.NORTH);
        panel.add(crearTabla(), BorderLayout.CENTER);
        panel.add(crearFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearStatsYFiltros() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JPanel stats = new JPanel(new GridBagLayout());
        stats.setBackground(Color.WHITE);
        stats.setBorder(new EmptyBorder(0, 0, 16, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0; gbc.insets = new Insets(0, 0, 0, 14);
        JPanel cardTrx = crearStatCard("Total Transacciones", "3",
                new Color(238, 237, 254), "", new Color(100, 90, 210));
        lblTotalTrx = obtenerLabelValor(cardTrx);
        stats.add(cardTrx, gbc);

        gbc.gridx = 1;
        JPanel cardVenta = crearStatCard("Venta Totales", "Rp 31.000",
                new Color(230, 241, 251), "", new Color(26, 83, 160));
        lblVentaTotales = obtenerLabelValor(cardVenta);
        stats.add(cardVenta, gbc);

        gbc.gridx = 2; gbc.insets = new Insets(0, 0, 0, 0);
        JPanel cardBruto = crearStatCard("Bruto", "Rp 14.000",
                new Color(234, 243, 222), "", new Color(50, 140, 80));
        lblBruto = obtenerLabelValor(cardBruto);
        stats.add(cardBruto, gbc);

        wrapper.add(stats, BorderLayout.NORTH);

        // Filtros
        JPanel filtros = new JPanel(new BorderLayout(12, 0));
        filtros.setBackground(Color.WHITE);
        filtros.setBorder(new EmptyBorder(0, 0, 14, 0));

        cboPeriodo = new JComboBox<>(new String[]{
            "  Filtrar periodo", "Hoy", "Esta semana", "Este mes"
        });
        cboPeriodo.setPreferredSize(new Dimension(190, 36));
        cboPeriodo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboPeriodo.addActionListener(e -> aplicarFiltros());

        // Buscador con ícono texto
        JPanel buscarWrap = new JPanel(new BorderLayout(4, 0));
        buscarWrap.setBackground(Color.WHITE);
        buscarWrap.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(2, 8, 2, 8)
        ));
        buscarWrap.setPreferredSize(new Dimension(320, 36));

        JLabel lupaLabel = new JLabel("🔍");
        lupaLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));

        txtBuscar = new JTextField();
        txtBuscar.setBorder(BorderFactory.createEmptyBorder());
        txtBuscar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtBuscar.putClientProperty("JTextField.placeholderText",
                "Buscar por nombre o codigo del producto");
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                aplicarFiltros();
            }
        });

        buscarWrap.add(lupaLabel, BorderLayout.WEST);
        buscarWrap.add(txtBuscar, BorderLayout.CENTER);

        filtros.add(cboPeriodo, BorderLayout.WEST);
        filtros.add(buscarWrap, BorderLayout.EAST);
        wrapper.add(filtros, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel crearStatCard(String etiqueta, String valor,
                                  Color colorFondo, String emoji, Color colorEmoji) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(14, 16, 14, 16)
        ));

        JLabel iconLabel = new JLabel(emoji);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        iconLabel.setOpaque(true);
        iconLabel.setBackground(colorFondo);
        iconLabel.setPreferredSize(new Dimension(46, 46));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconLabel.setBorder(BorderFactory.createLineBorder(colorFondo, 10));

        JPanel texto = new JPanel(new GridBagLayout());
        texto.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblEtiqueta = new JLabel(etiqueta);
        lblEtiqueta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblEtiqueta.setForeground(new Color(140, 140, 140));
        texto.add(lblEtiqueta, gbc);

        gbc.gridy = 1;
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValor.setForeground(new Color(25, 25, 25));
        texto.add(lblValor, gbc);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(texto, BorderLayout.CENTER);
        return card;
    }

    private JLabel obtenerLabelValor(JPanel card) {
        JPanel texto = (JPanel) card.getComponent(1);
        return (JLabel) texto.getComponent(1);
    }

    private JScrollPane crearTabla() {
        String[] columnas = {
            "No. Transaccion", "Fecha / hora", "Cajero",
            "Total item", "Monto total", "Metodo de pago", "Estado", "Acciones"
        };

        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(44);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setBackground(new Color(250, 250, 250));
        tabla.getTableHeader().setForeground(new Color(130, 130, 130));
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionBackground(AZUL_CLARO);
        tabla.setSelectionForeground(Color.BLACK);
        tabla.setGridColor(new Color(242, 242, 242));
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        sorter = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(sorter);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(145);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(130);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabla.getColumnModel().getColumn(4).setPreferredWidth(90);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(110);
        tabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        tabla.getColumnModel().getColumn(7).setPreferredWidth(120);

        tabla.getColumnModel().getColumn(6).setCellRenderer(new EstadoRenderer());
        tabla.getColumnModel().getColumn(7).setCellRenderer(new AccionesRenderer());

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabla.getColumnModel().getColumn(3).setCellRenderer(centro);
        tabla.getColumnModel().getColumn(4).setCellRenderer(centro);

        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tabla.rowAtPoint(e.getPoint());
                int col  = tabla.columnAtPoint(e.getPoint());
                if (fila >= 0 && col == 7) {
                    int filaModelo = tabla.convertRowIndexToModel(fila);
                    int xRel  = e.getX() - tabla.getCellRect(fila, col, true).x;
                    int ancho = tabla.getColumnModel().getColumn(col).getWidth();
                    if (xRel < ancho / 2) {
                        abrirDetalleTransaccion(filaModelo);
                    } else {
                        imprimirTransaccion(filaModelo);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(BORDE));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JPanel crearFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(new EmptyBorder(12, 0, 0, 0));
        lblFooter = new JLabel("Mostrando 0 datos");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(130, 130, 130));
        footer.add(lblFooter, BorderLayout.WEST);
        return footer;
    }

    // ─── DATOS DEMO ──────────────────────────────────────────────────────────────

    private void cargarDatosDemo() {
        agregarFila("TRX-2025-001", "22/12/2025 12:00", "Uwu fernandez", 20, "s/ 5.00",  "Tarjeta", "Completada");
        agregarFila("TRX-2025-002", "22/12/2025 14:00", "Uwu fernandez", 20, "s/ 8.00",  "Tarjeta", "Completada");
        agregarFila("TRX-2025-003", "22/12/2025 14:55", "Uwu fernandez", 20, "s/ 18.00", "Cash",    "Completada");
        actualizarFooter();
    }

    private void agregarFila(String num, String fecha, String cajero,
                              int items, String monto, String metodo, String estado) {
        modeloTabla.addRow(new Object[]{num, fecha, cajero, items, monto, metodo, estado, ""});
    }

    private void abrirDetalleTransaccion(int filaModelo) {
        String numTrx = String.valueOf(modeloTabla.getValueAt(filaModelo, 0));
        String fecha  = String.valueOf(modeloTabla.getValueAt(filaModelo, 1));
        String cajero = String.valueOf(modeloTabla.getValueAt(filaModelo, 2));
        String estado = String.valueOf(modeloTabla.getValueAt(filaModelo, 6));

        JDialog dialog = new JDialog(this, "Detalles de la transaccion", true);
        dialog.setSize(370, 560);
        dialog.setLocationRelativeTo(this);
        dialog.setUndecorated(true);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(Color.WHITE);
        contenido.setBorder(BorderFactory.createLineBorder(BORDE, 1));

        // ── Encabezado del modal ──
        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(Color.WHITE);
        encabezado.setBorder(new EmptyBorder(18, 20, 14, 20));

        JLabel lblTitulo = new JLabel("Detalles de la transaccion");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 15));

        JLabel btnCerrarX = new JLabel("✕");
        btnCerrarX.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btnCerrarX.setForeground(new Color(100, 100, 100));
        btnCerrarX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarX.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { dialog.dispose(); }
        });

        encabezado.add(lblTitulo, BorderLayout.WEST);
        encabezado.add(btnCerrarX, BorderLayout.EAST);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new GridBagLayout());
        cuerpo.setBackground(Color.WHITE);
        cuerpo.setBorder(new EmptyBorder(0, 20, 16, 20));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 1;
        g.gridx = 0;
        g.insets = new Insets(0, 0, 0, 0);
        int fila = 0;

        // Bloque azul claro con datos principales
        JPanel bloqueInfo = new JPanel(new GridBagLayout());
        bloqueInfo.setBackground(AZUL_CLARO);
        bloqueInfo.setBorder(new EmptyBorder(14, 14, 14, 14));

        GridBagConstraints gi = new GridBagConstraints();
        gi.fill = GridBagConstraints.HORIZONTAL;
        gi.weightx = 1;
        gi.insets = new Insets(2, 0, 2, 0);

        // Fila 1: No. Transaccion | Fecha/hora
        gi.gridx = 0; gi.gridy = 0; gi.anchor = GridBagConstraints.WEST;
        JLabel lNumLbl = new JLabel("No. Transaccion");
        lNumLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lNumLbl.setForeground(new Color(120, 120, 120));
        bloqueInfo.add(lNumLbl, gi);

        gi.gridx = 1;
        JLabel lFechaLbl = new JLabel("fecha / hora");
        lFechaLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lFechaLbl.setForeground(new Color(120, 120, 120));
        bloqueInfo.add(lFechaLbl, gi);

        gi.gridx = 0; gi.gridy = 1;
        JLabel lNumVal = new JLabel(numTrx);
        lNumVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bloqueInfo.add(lNumVal, gi);

        gi.gridx = 1;
        JLabel lFechaVal = new JLabel(fecha);
        lFechaVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bloqueInfo.add(lFechaVal, gi);

        // Separador interno
        gi.gridx = 0; gi.gridy = 2; gi.gridwidth = 2;
        gi.insets = new Insets(8, 0, 8, 0);
        bloqueInfo.add(new JSeparator(), gi);

        gi.insets = new Insets(2, 0, 2, 0);
        gi.gridwidth = 1;

        // Fila 2: Cajero | Estado
        gi.gridx = 0; gi.gridy = 3;
        JLabel lCajLbl = new JLabel("Cajero");
        lCajLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lCajLbl.setForeground(new Color(120, 120, 120));
        bloqueInfo.add(lCajLbl, gi);

        gi.gridx = 1;
        JLabel lEstLbl = new JLabel("Estado");
        lEstLbl.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lEstLbl.setForeground(new Color(120, 120, 120));
        bloqueInfo.add(lEstLbl, gi);

        gi.gridx = 0; gi.gridy = 4;
        JLabel lCajVal = new JLabel(cajero);
        lCajVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        bloqueInfo.add(lCajVal, gi);

        gi.gridx = 1;
        // Badge de estado verde
        JLabel lEstBadge = new JLabel(estado);
        lEstBadge.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lEstBadge.setOpaque(true);
        lEstBadge.setBackground(VERDE_BG);
        lEstBadge.setForeground(VERDE_TEXT);
        lEstBadge.setBorder(new EmptyBorder(4, 12, 4, 12));
        bloqueInfo.add(lEstBadge, gi);

        g.gridy = fila++;
        cuerpo.add(bloqueInfo, g);

        // ── Título productos ──
        g.gridy = fila++;
        g.insets = new Insets(14, 0, 8, 0);
        JLabel lblProductos = new JLabel("Productos de la transaccion");
        lblProductos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cuerpo.add(lblProductos, g);
        g.insets = new Insets(0, 0, 0, 0);

        // ── Lista de productos (demo según transacción) ──
        String[][] productos;
        if (numTrx.equals("TRX-2025-001")) {
            productos = new String[][]{
                {"Lomo saltado", "s/ 18.000 X 1", "s/ 18.000"},
                {"Inka Kola",    "s/ 8.000 X 2",  "s/ 16.000"}
            };
        } else if (numTrx.equals("TRX-2025-002")) {
            productos = new String[][]{
                {"Chicha morada", "s/ 4.000 X 2", "s/ 8.000"}
            };
        } else {
            productos = new String[][]{
                {"Pisco sour",   "s/ 14.000 X 1", "s/ 14.000"},
                {"Ceviche",      "s/ 4.000 X 1",  "s/ 4.000"}
            };
        }

        for (String[] prod : productos) {
            g.gridy = fila++;
            cuerpo.add(crearFilaProducto(prod[0], prod[1], prod[2]), g);
        }

        g.gridy = fila++;
        g.insets = new Insets(10, 0, 10, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("Sub total", "s/ 34.000", false), g);

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("IVA 11%", "s/ 3.740", false), g);

        // ── Separador ──
        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        // ── Monto pagado (destacado) ──
        g.gridy = fila++;
        cuerpo.add(crearFilaMontoPagado("Monto pagado", "Rp 37.740"), g);

        // ── Separador ──
        g.gridy = fila++;
        g.insets = new Insets(8, 0, 8, 0);
        cuerpo.add(new JSeparator(), g);
        g.insets = new Insets(3, 0, 3, 0);

        // ── Sección Pembayaran ──
        g.gridy = fila++;
        g.insets = new Insets(6, 0, 4, 0);
        JLabel lblPem = new JLabel("Pembayaran");
        lblPem.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cuerpo.add(lblPem, g);
        g.insets = new Insets(3, 0, 3, 0);

        g.gridy = fila++;
        cuerpo.add(crearFilaResumen("Monto", "s/ 50.000", false), g);

        g.gridy = fila++;
        JPanel filaVuelto = crearFilaResumen("vuelta (opcional)", "s/ 12.260", false);
        // Colorear la etiqueta vuelta en azul
        ((JLabel) filaVuelto.getComponent(0)).setForeground(AZUL);
        ((JLabel) filaVuelto.getComponent(1)).setForeground(AZUL);
        cuerpo.add(filaVuelto, g);

        // ── Botones ──
        JPanel botones = new JPanel(new GridBagLayout());
        botones.setBackground(Color.WHITE);
        botones.setBorder(new EmptyBorder(14, 20, 18, 20));

        GridBagConstraints gb = new GridBagConstraints();
        gb.fill = GridBagConstraints.HORIZONTAL;
        gb.weightx = 1;
        gb.insets = new Insets(0, 0, 0, 10);

        JButton btnCerrar = new JButton("cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnCerrar.setBackground(Color.WHITE);
        btnCerrar.setForeground(new Color(60, 60, 60));
        btnCerrar.setFocusPainted(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btnCerrar.addActionListener(e -> dialog.dispose());

        gb.gridx = 0;
        botones.add(btnCerrar, gb);

        gb.insets = new Insets(0, 0, 0, 0);
        JButton btnReimprimir = new JButton("  Reimprimir");
        btnReimprimir.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnReimprimir.setBackground(AZUL);
        btnReimprimir.setForeground(Color.WHITE);
        btnReimprimir.setFocusPainted(false);
        btnReimprimir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReimprimir.setBorder(new EmptyBorder(8, 16, 8, 16));
        btnReimprimir.addActionListener(e -> {
            dialog.dispose();
            imprimirTransaccion(filaModelo);
        });

        gb.gridx = 1;
        botones.add(btnReimprimir, gb);

        // Ensamblar modal
        JScrollPane scrollCuerpo = new JScrollPane(cuerpo);
        scrollCuerpo.setBorder(BorderFactory.createEmptyBorder());
        scrollCuerpo.getViewport().setBackground(Color.WHITE);

        contenido.add(encabezado, BorderLayout.NORTH);
        contenido.add(scrollCuerpo, BorderLayout.CENTER);
        contenido.add(botones, BorderLayout.SOUTH);

        dialog.setContentPane(contenido);
        dialog.setVisible(true);
    }

    private JPanel crearFilaProducto(String nombre, String detalle, String total) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel izq = new JPanel(new BorderLayout());
        izq.setBackground(Color.WHITE);

        JLabel lblNombre = new JLabel(nombre);
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JLabel lblDetalle = new JLabel(detalle);
        lblDetalle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDetalle.setForeground(new Color(130, 130, 130));

        izq.add(lblNombre, BorderLayout.NORTH);
        izq.add(lblDetalle, BorderLayout.SOUTH);

        JLabel lblTotal = new JLabel(total);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(AZUL);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(izq, BorderLayout.WEST);
        fila.add(lblTotal, BorderLayout.EAST);
        return fila;
    }

    /** Fila simple etiqueta - valor alineados. */
    private JPanel crearFilaResumen(String etiqueta, String valor, boolean negrita) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(3, 0, 3, 0));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, 12));
        lblEtq.setForeground(new Color(100, 100, 100));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", negrita ? Font.BOLD : Font.PLAIN, 12));
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(lblEtq, BorderLayout.WEST);
        fila.add(lblVal, BorderLayout.EAST);
        return fila;
    }

    /** Fila del monto total pagado con texto grande en azul. */
    private JPanel crearFilaMontoPagado(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setBackground(Color.WHITE);
        fila.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel lblEtq = new JLabel(etiqueta);
        lblEtq.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblEtq.setForeground(new Color(80, 80, 80));

        JLabel lblVal = new JLabel(valor);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblVal.setForeground(AZUL);
        lblVal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(lblEtq, BorderLayout.WEST);
        fila.add(lblVal, BorderLayout.EAST);
        return fila;
    }

    private void imprimirTransaccion(int fila) {
        javax.swing.JOptionPane.showMessageDialog(this,
            "Enviando a impresora: " + modeloTabla.getValueAt(fila, 0),
            "Imprimir", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText().trim();
        RowFilter<DefaultTableModel, Object> filtro = null;
        if (!texto.isEmpty()) {
            filtro = RowFilter.regexFilter("(?i)" + Pattern.quote(texto), 0, 1, 2, 5);
        }
        sorter.setRowFilter(filtro);
        actualizarFooter();
    }

    private void actualizarFooter() {
        int vis   = tabla == null ? 0 : tabla.getRowCount();
        int total = modeloTabla == null ? 0 : modeloTabla.getRowCount();
        lblFooter.setText("Mostrando 1 - " + vis + " de " + total + " datos");
    }

    private void iniciarReloj() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Timer timer = new Timer(1000, e ->
            lblHora.setText("<html><b>Hora</b><br>" + sdf.format(new Date()) + " WIB</html>")
        );
        timer.start();
        lblHora.setText("<html><b>Hora</b><br>" + sdf.format(new Date()) + " WIB</html>");
    }

    private static class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            if (!isSelected) {
                label.setBackground(new Color(225, 245, 238));
                label.setForeground(new Color(15, 110, 86));
            }
            return label;
        }
    }

    private static class AccionesRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(isSelected
                    ? new Color(232, 241, 255) : Color.WHITE);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 4, 0, 4);

            // Botón  ver
            JLabel btnVer = new JLabel("👁");
            btnVer.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            btnVer.setOpaque(true);
            btnVer.setBackground(new Color(232, 241, 255));
            btnVer.setBorder(new EmptyBorder(5, 9, 5, 9));
            btnVer.setCursor(new Cursor(Cursor.HAND_CURSOR));

            // Botón imprimir
            JLabel btnImp = new JLabel("🖨");
            btnImp.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
            btnImp.setOpaque(true);
            btnImp.setBackground(new Color(232, 241, 255));
            btnImp.setBorder(new EmptyBorder(5, 9, 5, 9));
            btnImp.setCursor(new Cursor(Cursor.HAND_CURSOR));

            gbc.gridx = 0; panel.add(btnVer, gbc);
            gbc.gridx = 1; panel.add(btnImp, gbc);
            return panel;
        }
    }
}