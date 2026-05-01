package proyecto.pos.gui;

// Librerías para construir el formulario modal
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;

// Componentes Swing
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Dialog/modal para agregar o editar productos.
 *
 * Esta clase forma parte del FRONTEND.
 * Se encarga de mostrar el formulario y validar los datos antes de enviarlos
 * a la ventana principal.
 *
 * No guarda directamente en base de datos.
 * Devuelve los datos validados a ArticulosStockFrame.
 */
public class ProductoDialog extends JDialog {

    // Colores principales usados en el formulario
    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color BORDE = new Color(225, 228, 233);

    // Campos del formulario
    private JTextField txtNombre;
    private JTextField txtCodigo;
    private JTextField txtBarcode;
    private JTextField txtCosto;
    private JTextField txtPrecio;
    private JTextField txtStock;
    private JTextField txtStockMinimo;
    private JComboBox<String> cboCategoria;
    private JCheckBox chkActivo;

    // Indica si el usuario presionó "Aceptar" correctamente
    private boolean confirmado = false;

    // Objeto temporal donde se guardan los datos validados
    private ProductoFormData productoData;

    /**
     * Constructor usado cuando se va a agregar un producto nuevo.
     */
    public ProductoDialog(Window owner) {
        super(owner, "Agregar un nuevo producto", ModalityType.APPLICATION_MODAL);
        construirInterfaz();
    }

    /**
     * Constructor usado cuando se va a editar un producto existente.
     */
    public ProductoDialog(Window owner, ProductoFormData data) {
        super(owner, "Editar producto", ModalityType.APPLICATION_MODAL);
        construirInterfaz();
        cargarDatos(data);
    }

    /**
     * Construye la estructura del dialog:
     * header, formulario y botones.
     */
    private void construirInterfaz() {
        setSize(570, 610);
        setMinimumSize(new Dimension(570, 610));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        add(crearHeader(), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
        add(crearBotones(), BorderLayout.SOUTH);
    }

    /**
     * Crea el encabezado del modal.
     */
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(18, 22, 14, 22));

        JLabel titulo = new JLabel(getTitle());
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton cerrar = new JButton("X");
        cerrar.setFocusPainted(false);
        cerrar.setBorderPainted(false);
        cerrar.setContentAreaFilled(false);
        cerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        cerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cerrar.addActionListener(e -> dispose());

        header.add(titulo, BorderLayout.WEST);
        header.add(cerrar, BorderLayout.EAST);

        return header;
    }

    /**
     * Crea todos los campos del formulario.
     */
    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, BORDE),
                new EmptyBorder(18, 22, 18, 22)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel descripcion = new JLabel("Ingrese los detalles del nuevo producto");
        descripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 12, 0);
        panel.add(descripcion, gbc);

        // Campo de nombre
        txtNombre = crearCampo("Ejemplo: Producto A");
        agregarCampoCompleto(panel, gbc, 1, "Nombre producto *", txtNombre);

        // Código y barcode
        txtCodigo = crearCampo("Ejemplo: BRG-001");
        txtBarcode = crearCampo("Ejemplo: 00000000");
        agregarDosCampos(panel, gbc, 3, "Código del producto *", txtCodigo, "Barcode (opcional)", txtBarcode);

        // Botón visual para imagen.
        // Actualmente solo es parte visual, no abre selector de archivo.
        JButton btnImagen = new JButton("<html><center>Imagen<br>Click para subir</center></html>");
        btnImagen.setPreferredSize(new Dimension(130, 90));
        btnImagen.setFocusPainted(false);
        btnImagen.setBackground(Color.WHITE);
        btnImagen.setForeground(Color.GRAY);
        btnImagen.setCursor(new Cursor(Cursor.HAND_CURSOR));

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(8, 0, 5, 10);
        JLabel lblImagen = crearLabel("Imagen del producto (opcional)");
        panel.add(lblImagen, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 12, 10);
        panel.add(btnImagen, gbc);

        // Categoría
        cboCategoria = new JComboBox<>(new String[]{"Seleccionar categoría", "Comida", "Bebida"});
        agregarCampoCompleto(panel, gbc, 7, "Categoría *", cboCategoria);

        // Costo y precio de venta
        txtCosto = crearCampo("0.00");
        txtPrecio = crearCampo("0.00");
        agregarDosCampos(panel, gbc, 9, "Costo *", txtCosto, "Precio venta *", txtPrecio);

        // Stock inicial y stock mínimo
        txtStock = crearCampo("0");
        txtStockMinimo = crearCampo("0");
        agregarDosCampos(panel, gbc, 11, "Stock inicial *", txtStock, "Stock mínimo *", txtStockMinimo);

        // Estado del producto
        chkActivo = new JCheckBox("Status activo");
        chkActivo.setSelected(true);
        chkActivo.setBackground(Color.WHITE);
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 0, 0, 0);
        panel.add(chkActivo, gbc);

        return panel;
    }

    /**
     * Crea los botones inferiores: Cancelar y Aceptar.
     */
    private JPanel crearBotones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(14, 22, 18, 22));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(220, 40));
        btnCancelar.setFocusPainted(false);
        btnCancelar.setForeground(AZUL);
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnAceptar = new JButton("Aceptar");
        btnAceptar.setPreferredSize(new Dimension(220, 40));
        btnAceptar.setFocusPainted(false);
        btnAceptar.setForeground(Color.WHITE);
        btnAceptar.setBackground(AZUL);
        btnAceptar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Ejecuta las validaciones y guarda temporalmente los datos
        btnAceptar.addActionListener(e -> guardar());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);

        gbc.gridx = 0;
        panel.add(btnCancelar, gbc);

        gbc.gridx = 1;
        panel.add(btnAceptar, gbc);

        return panel;
    }

    /**
     * Crea un campo de texto con placeholder.
     * FlatLaf permite usar la propiedad "JTextField.placeholderText".
     */
    private JTextField crearCampo(String placeholder) {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(210, 36));
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        return campo;
    }

    /**
     * Crea un JLabel estándar del formulario.
     */
    private JLabel crearLabel(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return label;
    }

    /**
     * Agrega un campo que ocupa el ancho completo del formulario.
     */
    private void agregarCampoCompleto(JPanel panel, GridBagConstraints gbc, int fila, String label, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 0, 5, 0);
        panel.add(crearLabel(label), gbc);

        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        panel.add(campo, gbc);
    }

    /**
     * Agrega dos campos en la misma fila.
     */
    private void agregarDosCampos(JPanel panel, GridBagConstraints gbc, int fila,
                                  String label1, java.awt.Component campo1,
                                  String label2, java.awt.Component campo2) {
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.insets = new Insets(6, 0, 5, 10);
        panel.add(crearLabel(label1), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(6, 10, 5, 0);
        panel.add(crearLabel(label2), gbc);

        gbc.gridx = 0;
        gbc.gridy = fila + 1;
        gbc.insets = new Insets(0, 0, 8, 10);
        panel.add(campo1, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 8, 0);
        panel.add(campo2, gbc);
    }

    /**
     * Carga los datos existentes en el formulario cuando se edita un producto.
     */
    private void cargarDatos(ProductoFormData data) {
        txtCodigo.setText(data.codigo);
        txtNombre.setText(data.nombre);
        cboCategoria.setSelectedItem(data.categoria);
        txtCosto.setText(String.valueOf(data.costo));
        txtPrecio.setText(String.valueOf(data.precio));
        txtStock.setText(String.valueOf(data.stock));
        txtStockMinimo.setText(String.valueOf(data.stockMinimo));
        chkActivo.setSelected(data.activo);
    }

    /**
     * Valida los campos y, si todo está correcto, guarda los datos
     * en productoData.
     */
    private void guardar() {
        String nombre = txtNombre.getText().trim();
        String codigo = txtCodigo.getText().trim();
        String categoria = String.valueOf(cboCategoria.getSelectedItem());

        // Validaciones de campos obligatorios
        if (nombre.isEmpty()) {
            mostrarError("El nombre del producto es obligatorio.", txtNombre);
            return;
        }

        if (codigo.isEmpty()) {
            mostrarError("El código del producto es obligatorio.", txtCodigo);
            return;
        }

        if (categoria.equals("Seleccionar categoría")) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría.", "Validación", JOptionPane.WARNING_MESSAGE);
            cboCategoria.requestFocus();
            return;
        }

        double costo;
        double precio;
        int stock;
        int stockMinimo;

        // Validación de costo
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

        // Validación de precio
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

        // Regla de negocio básica:
        // el precio de venta no debería ser menor al costo
        if (precio < costo) {
            mostrarError("El precio de venta no puede ser menor que el costo.", txtPrecio);
            return;
        }

        // Validación de stock inicial
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

        // Validación de stock mínimo
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

        // Si pasó todas las validaciones, se crea el objeto temporal.
        productoData = new ProductoFormData(
                codigo,
                nombre,
                categoria,
                costo,
                precio,
                stock,
                stockMinimo,
                chkActivo.isSelected()
        );

        confirmado = true;
        dispose();
    }

    /**
     * Muestra mensaje de error y enfoca el campo correspondiente.
     */
    private void mostrarError(String mensaje, java.awt.Component campo) {
        JOptionPane.showMessageDialog(this, mensaje, "Validación", JOptionPane.WARNING_MESSAGE);
        campo.requestFocus();
    }

    /**
     * Indica si el formulario fue aceptado correctamente.
     */
    public boolean isConfirmado() {
        return confirmado;
    }

    /**
     * Devuelve los datos validados del formulario.
     */
    public ProductoFormData getProductoData() {
        return productoData;
    }

    /**
     * Clase interna usada para transportar los datos del formulario.
     *
     * Se usa para no depender todavía directamente del modelo de base de datos.
     * Luego puede reemplazarse por Insumo, Plato u otra clase del paquete model.
     */
    public static class ProductoFormData {
        public String codigo;
        public String nombre;
        public String categoria;
        public double costo;
        public double precio;
        public int stock;
        public int stockMinimo;
        public boolean activo;

        public ProductoFormData(String codigo, String nombre, String categoria, double costo,
                                double precio, int stock, int stockMinimo, boolean activo) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.categoria = categoria;
            this.costo = costo;
            this.precio = precio;
            this.stock = stock;
            this.stockMinimo = stockMinimo;
            this.activo = activo;
        }
    }
}