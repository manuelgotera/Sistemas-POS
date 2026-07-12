package proyecto.pos.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class ProductoDialog extends JDialog {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_HOVER = new Color(18, 65, 128);
    private static final Color FONDO = new Color(255, 255, 255);
    private static final Color TEXTO = new Color(28, 35, 46);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color BORDE_FOCUS = new Color(159, 190, 238);
    private static final Color ROJO = new Color(220, 53, 69);

    private JTextField txtNombre;
    private JTextField txtCodigo;
    private JTextField txtBarcode;
    private JTextField txtCosto;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    private JTextField txtProveedor;
    private JTextField txtFechaVencimiento;
    private JTextField txtMerma;

    private JComboBox<String> cboCategoria;
    private JComboBox<String> cboUnidadMedida;
    private JComboBox<String> cboMotivoMerma;

    private ToggleSwitch swActivo;

    private boolean confirmado = false;
    private ProductoFormData productoData;

    public ProductoDialog(Window owner) {
        super(owner, "Agregar un nuevo producto", ModalityType.APPLICATION_MODAL);
        construirInterfaz(false);
    }

    public ProductoDialog(Window owner, ProductoFormData data) {
        super(owner, "Editar producto", ModalityType.APPLICATION_MODAL);
        construirInterfaz(true);
        cargarDatos(data);
    }

    private void construirInterfaz(boolean editar) {
        setUndecorated(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(610, 780);
        setMinimumSize(new Dimension(610, 720));
        setLocationRelativeTo(getOwner());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        root.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(205, 211, 220), 14),
                new EmptyBorder(0, 0, 0, 0)
        ));

        JScrollPane scrollFormulario = new JScrollPane(crearFormulario(editar));
        scrollFormulario.setBorder(null);
        scrollFormulario.getViewport().setBackground(Color.WHITE);
        scrollFormulario.getVerticalScrollBar().setUnitIncrement(16);

        root.add(crearHeader(), BorderLayout.NORTH);
        root.add(scrollFormulario, BorderLayout.CENTER);
        root.add(crearBotones(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                new EmptyBorder(17, 22, 15, 18)
        ));

        JLabel titulo = new JLabel(getTitle());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(Color.BLACK);

        JButton cerrar = new JButton("×");
        cerrar.setPreferredSize(new Dimension(32, 32));
        cerrar.setFocusPainted(false);
        cerrar.setBorderPainted(false);
        cerrar.setContentAreaFilled(false);
        cerrar.setFont(new Font("Segoe UI", Font.BOLD, 26));
        cerrar.setForeground(new Color(25, 31, 40));
        cerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cerrar.addActionListener(e -> dispose());

        header.add(titulo, BorderLayout.WEST);
        header.add(cerrar, BorderLayout.EAST);

        return header;
    }

    private JPanel crearFormulario(boolean editar) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 22, 8, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel descripcion = new JLabel(editar
                ? "Actualiza los detalles del producto seleccionado"
                : "Ingrese los detalles del nuevo producto");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descripcion.setForeground(new Color(55, 63, 75));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(descripcion, gbc);

        txtNombre = crearCampo("Ejemplo: Arroz, Pollo, Gaseosa");
        agregarCampoCompleto(panel, gbc, 1, "Nombre producto / insumo", true, txtNombre);

        txtCodigo = crearCampo("Ejemplo: INS-001");
        txtBarcode = crearCampo("Ejemplo: 00000000");
        agregarDosCampos(panel, gbc, 3,
                "Código del producto", true, txtCodigo,
                "Barcode", false, txtBarcode);

        JPanel imagenBox = crearImagenBox();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(crearLabel("Imagen del producto (opcional)", false), gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 12, 10);
        panel.add(imagenBox, gbc);

        cboCategoria = new JComboBox<>(new String[]{
                "Seleccionar tipo de stock",
                "Consumo crudo",
                "Preparado",
                "Bebida"
        });
        cboCategoria.setPreferredSize(new Dimension(210, 38));
        cboCategoria.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboCategoria.setBackground(Color.WHITE);
        cboCategoria.setBorder(new RoundedBorder(BORDE, 10));

        cboUnidadMedida = new JComboBox<>(new String[]{
                "Seleccionar unidad",
                "kg",
                "g",
                "L",
                "ml",
                "unidad",
                "paquete"
        });
        cboUnidadMedida.setPreferredSize(new Dimension(210, 38));
        cboUnidadMedida.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboUnidadMedida.setBackground(Color.WHITE);
        cboUnidadMedida.setBorder(new RoundedBorder(BORDE, 10));

        agregarDosCampos(panel, gbc, 7,
                "Tipo de stock", true, cboCategoria,
                "Unidad de medida", true, cboUnidadMedida);

        txtProveedor = crearCampo("Ejemplo: Proveedor San José");
        txtFechaVencimiento = crearCampo("yyyy-MM-dd");
        agregarDosCampos(panel, gbc, 9,
                "Proveedor", false, txtProveedor,
                "Fecha vencimiento", false, txtFechaVencimiento);

        txtCosto = crearCampo("0.00");
        txtPrecio = crearCampo("0.00");
        agregarDosCampos(panel, gbc, 11,
                "Costo", true, txtCosto,
                "Precio venta", true, txtPrecio);

        txtStock = crearCampo("0");
        txtStockMinimo = crearCampo("0");
        agregarDosCampos(panel, gbc, 13,
                "Stock actual", true, txtStock,
                "Stock mínimo", true, txtStockMinimo);

        txtMerma = crearCampo("0");

        cboMotivoMerma = new JComboBox<>(new String[]{
                "Sin merma",
                "Vencimiento",
                "Producto dañado",
                "Desperdicio",
                "Error de preparación",
                "Otro"
        });
        cboMotivoMerma.setPreferredSize(new Dimension(210, 38));
        cboMotivoMerma.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cboMotivoMerma.setBackground(Color.WHITE);
        cboMotivoMerma.setBorder(new RoundedBorder(BORDE, 10));

        agregarDosCampos(panel, gbc, 15,
                "Cantidad de merma", false, txtMerma,
                "Motivo de merma", false, cboMotivoMerma);

        JPanel estadoPanel = crearEstadoPanel();
        gbc.gridx = 0;
        gbc.gridy = 17;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        panel.add(estadoPanel, gbc);

        return panel;
    }

    private JPanel crearImagenBox() {
        JPanel box = new JPanel(new GridBagLayout());
        box.setPreferredSize(new Dimension(112, 90));
        box.setBackground(Color.WHITE);
        box.setBorder(new RoundedBorder(BORDE, 10));
        box.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JLabel icono = new JLabel(redimensionarIcono("/img/stock.png", 20, 20));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel texto = new JLabel("Click para subir");
        texto.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        texto.setForeground(new Color(160, 166, 176));
        texto.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(icono);
        contenido.add(Box.createVerticalStrut(8));
        contenido.add(texto);
        box.add(contenido);

        box.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        ProductoDialog.this,
                        "La carga de imagen aún es visual. Puedes conectarla luego con JFileChooser.",
                        "Imagen del producto",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        return box;
    }

    private JPanel crearEstadoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(2, 0, 0, 0));

        JLabel label = crearLabel("Status activo", false);
        swActivo = new ToggleSwitch();
        swActivo.setSelected(true);

        JPanel izquierda = new JPanel();
        izquierda.setOpaque(false);
        izquierda.setLayout(new BoxLayout(izquierda, BoxLayout.Y_AXIS));
        izquierda.add(label);
        izquierda.add(Box.createVerticalStrut(7));
        izquierda.add(swActivo);

        panel.add(izquierda, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 22, 18, 22));

        JButton btnCancelar = crearBoton("Cancelar", false);
        JButton btnAceptar = crearBoton("Aceptar", true);

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> guardar());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.gridx = 0;
        panel.add(btnCancelar, gbc);

        gbc.insets = new Insets(0, 8, 0, 0);
        gbc.gridx = 1;
        panel.add(btnAceptar, gbc);

        return panel;
    }

    private JButton crearBoton(String texto, boolean primario) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(220, 42));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        if (primario) {
            boton.setBackground(AZUL);
            boton.setForeground(Color.WHITE);
            boton.setBorder(new RoundedBorder(AZUL, 10));
            boton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    boton.setBackground(AZUL_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    boton.setBackground(AZUL);
                }
            });
        } else {
            boton.setBackground(Color.WHITE);
            boton.setForeground(AZUL);
            boton.setBorder(new RoundedBorder(AZUL, 10));
        }

        return boton;
    }

    private JTextField crearCampo(String placeholder) {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(210, 38));
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        campo.setForeground(TEXTO);
        campo.setBackground(Color.WHITE);
        campo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 10),
                new EmptyBorder(0, 10, 0, 10)
        ));
        campo.putClientProperty("JTextField.placeholderText", placeholder);

        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDE_FOCUS, 10),
                        new EmptyBorder(0, 10, 0, 10)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        new RoundedBorder(BORDE, 10),
                        new EmptyBorder(0, 10, 0, 10)
                ));
            }
        });

        return campo;
    }

    private JLabel crearLabel(String texto, boolean requerido) {
        String labelText = requerido
                ? "<html>" + texto + " <font color='#dc3545'>*</font></html>"
                : texto;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(40, 47, 58));
        return label;
    }

    private void agregarCampoCompleto(JPanel panel, GridBagConstraints gbc, int fila,
                                      String label, boolean requerido, Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 0, 6, 0);
        panel.add(crearLabel(label, requerido), gbc);

        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(campo, gbc);
    }

    private void agregarDosCampos(JPanel panel, GridBagConstraints gbc, int fila,
                                  String label1, boolean req1, Component campo1,
                                  String label2, boolean req2, Component campo2) {
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.insets = new Insets(6, 0, 6, 10);
        panel.add(crearLabel(label1, req1), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(6, 10, 6, 0);
        panel.add(crearLabel(label2, req2), gbc);

        gbc.gridx = 0;
        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 0, 8, 10);
        panel.add(campo1, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 8, 0);
        panel.add(campo2, gbc);
    }

    private void cargarDatos(ProductoFormData data) {
        txtCodigo.setText(data.codigo);
        txtNombre.setText(data.nombre);
        cboCategoria.setSelectedItem(data.categoria);
        txtCosto.setText(String.valueOf(data.costo));
        txtPrecio.setText(String.valueOf(data.precio));
        txtStock.setText(String.valueOf(data.stock));
        txtStockMinimo.setText(String.valueOf(data.stockMinimo));
        swActivo.setSelected(data.activo);

        cboUnidadMedida.setSelectedItem(data.unidadMedida == null || data.unidadMedida.isEmpty()
                ? "Seleccionar unidad"
                : data.unidadMedida);

        txtProveedor.setText(data.proveedor == null ? "" : data.proveedor);
        txtFechaVencimiento.setText(data.fechaVencimiento == null ? "" : data.fechaVencimiento);
        txtMerma.setText(String.valueOf(data.merma));

        cboMotivoMerma.setSelectedItem(data.motivoMerma == null || data.motivoMerma.isEmpty()
                ? "Sin merma"
                : data.motivoMerma);
    }

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String codigo = txtCodigo.getText().trim();
        String categoria = String.valueOf(cboCategoria.getSelectedItem());
        String unidadMedida = String.valueOf(cboUnidadMedida.getSelectedItem());
        String proveedor = txtProveedor.getText().trim();
        String fechaVencimiento = txtFechaVencimiento.getText().trim();
        String motivoMerma = String.valueOf(cboMotivoMerma.getSelectedItem());

        if (nombre.isEmpty()) {
            mostrarError("El nombre del producto es obligatorio.", txtNombre);
            return;
        }

        if (codigo.isEmpty()) {
            mostrarError("El código del producto es obligatorio.", txtCodigo);
            return;
        }

        if (categoria.equals("Seleccionar tipo de stock")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar el tipo de stock.", "Validación", JOptionPane.WARNING_MESSAGE);
            cboCategoria.requestFocus();
            return;
        }

        if (unidadMedida.equals("Seleccionar unidad")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar la unidad de medida.", "Validación", JOptionPane.WARNING_MESSAGE);
            cboUnidadMedida.requestFocus();
            return;
        }

        if (!fechaVencimiento.isEmpty() && !fechaValida(fechaVencimiento)) {
            mostrarError("La fecha de vencimiento debe tener el formato yyyy-MM-dd. Ejemplo: 2025-12-31", txtFechaVencimiento);
            return;
        }

        double costo;
        double precio;
        double merma;
        int stock;
        int stockMinimo;

        try {
            costo = Double.parseDouble(txtCosto.getText().trim());
            if (costo < 0) {
                mostrarError("El costo no puede ser negativo.", txtCosto);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("El costo debe ser un número válido. Ejemplo: 4.50", txtCosto);
            return;
        }

        try {
            precio = Double.parseDouble(txtPrecio.getText().trim());
            if (precio < 0) {
                mostrarError("El precio no puede ser negativo.", txtPrecio);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("El precio debe ser un número válido. Ejemplo: 8.00", txtPrecio);
            return;
        }

        if (precio < costo) {
            mostrarError("El precio de venta no puede ser menor que el costo.", txtPrecio);
            return;
        }

        try {
            stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 0) {
                mostrarError("El stock no puede ser negativo.", txtStock);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("El stock debe ser un número entero. Ejemplo: 20", txtStock);
            return;
        }

        try {
            stockMinimo = Integer.parseInt(txtStockMinimo.getText().trim());
            if (stockMinimo < 0) {
                mostrarError("El stock mínimo no puede ser negativo.", txtStockMinimo);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("El stock mínimo debe ser un número entero. Ejemplo: 10", txtStockMinimo);
            return;
        }

        try {
            merma = Double.parseDouble(txtMerma.getText().trim());
            if (merma < 0) {
                mostrarError("La merma no puede ser negativa.", txtMerma);
                return;
            }
        } catch (NumberFormatException e) {
            mostrarError("La merma debe ser un número válido. Ejemplo: 2.5", txtMerma);
            return;
        }

        if (merma > stock) {
            mostrarError("La merma no puede ser mayor que el stock actual.", txtMerma);
            return;
        }

        if (merma > 0 && motivoMerma.equals("Sin merma")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un motivo de merma.", "Validación", JOptionPane.WARNING_MESSAGE);
            cboMotivoMerma.requestFocus();
            return;
        }

        productoData = new ProductoFormData(
                codigo,
                nombre,
                categoria,
                costo,
                precio,
                stock,
                stockMinimo,
                swActivo.isSelected(),
                unidadMedida,
                proveedor,
                fechaVencimiento,
                merma,
                motivoMerma
        );

        confirmado = true;
        dispose();
    }

    private boolean fechaValida(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            sdf.parse(fecha);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private void mostrarError(String mensaje, Component campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public ProductoFormData getProductoData() {
        return productoData;
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

    private static class ToggleSwitch extends JToggleButton {
        public ToggleSwitch() {
            setPreferredSize(new Dimension(42, 22));
            setMinimumSize(new Dimension(42, 22));
            setMaximumSize(new Dimension(42, 22));
            setBorderPainted(false);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 3;
            int circle = h - padding * 2;

            g2.setColor(isSelected() ? AZUL : new Color(190, 196, 205));
            g2.fillRoundRect(0, 0, w, h, h, h);

            int x = isSelected() ? w - circle - padding : padding;
            g2.setColor(Color.WHITE);
            g2.fillOval(x, padding, circle, circle);
            g2.dispose();
        }
    }

    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int arc;

        public RoundedBorder(Color color, int arc) {
            this.color = color;
            this.arc = arc;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, arc, arc);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = 1;
            insets.right = 1;
            insets.top = 1;
            insets.bottom = 1;
            return insets;
        }
    }

    public static class ProductoFormData {
        public String codigo;
        public String nombre;
        public String categoria;
        public double costo;
        public double precio;
        public int stock;
        public int stockMinimo;
        public boolean activo;

        public String unidadMedida;
        public String proveedor;
        public String fechaVencimiento;
        public double merma;
        public String motivoMerma;

        public ProductoFormData(String codigo, String nombre, String categoria, double costo,
                                double precio, int stock, int stockMinimo, boolean activo) {
            this(
                    codigo,
                    nombre,
                    categoria,
                    costo,
                    precio,
                    stock,
                    stockMinimo,
                    activo,
                    "unidad",
                    "",
                    "",
                    0,
                    "Sin merma"
            );
        }

        public ProductoFormData(String codigo, String nombre, String categoria, double costo,
                                double precio, int stock, int stockMinimo, boolean activo,
                                String unidadMedida, String proveedor, String fechaVencimiento,
                                double merma, String motivoMerma) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.costo = costo;
            this.precio = precio;
            this.stock = stock;
            this.stockMinimo = stockMinimo;
            this.activo = activo;
            this.unidadMedida = unidadMedida;
            this.proveedor = proveedor;
            this.fechaVencimiento = fechaVencimiento;
            this.merma = merma;
            this.motivoMerma = motivoMerma;
        }
    }
}