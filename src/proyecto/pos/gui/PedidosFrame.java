    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto.pos.gui;

/**
 *
 * @author USER
 */

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
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.PedidoController;
import proyecto.pos.model.Pedido;
import proyecto.pos.model.PedidoDetalle;
import proyecto.pos.model.Plato;
import proyecto.pos.model.Repartidor;

public class PedidosFrame extends JFrame {

    private final Color AZUL = new Color(26, 83, 160);
    private final Color FONDO = new Color(246, 248, 251);

    private Connection conexion;
    private PedidoController pedidoController;

    private JTextField txtCliente;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextArea txtObservacion;
    private JComboBox<PlatoItem> cboProductos;
    private JSpinner spCantidad;
    private DefaultTableModel modeloDetalle;
    private JTable tablaDetalle;
    private JLabel lblTotal;

    private DefaultTableModel modeloPendientes;
    private JTable tablaPendientes;
    private JComboBox<Repartidor> cboRepartidorAsignar;

    private DefaultTableModel modeloAsignados;
    private JTable tablaAsignados;
    private JComboBox<Repartidor> cboRepartidorConsulta;
    private JComboBox<String> cboEstado;
    private JTextField txtEvidencia;
    private JTextArea txtMotivo;

    private DefaultTableModel modeloProductos;
    private JTable tablaProductos;

    private final List<PedidoDetalle> detalleActual = new ArrayList<>();

    public PedidosFrame() {
        conectarBD();
        configurarVentana();
        construirPantalla();
        cargarDatosIniciales();
    }

    private void conectarBD() {
        DatabaseConnection db = new DatabaseConnection();
        conexion = db.conectar();

        if (conexion != null) {
            pedidoController = new PedidoController(conexion);
        }
    }

    private void configurarVentana() {
        setTitle("Gestión de Pedidos y Delivery");
        setSize(1250, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirPantalla() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(new MenuSidebar(this, "Pedidos"), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titulo = new JLabel("Gestión de Pedidos y Delivery");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Registro, asignación, seguimiento de pedidos y control de productos");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(Color.DARK_GRAY);

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel textos = new JPanel(new BorderLayout());
        textos.setOpaque(false);
        textos.add(titulo, BorderLayout.NORTH);
        textos.add(subtitulo, BorderLayout.SOUTH);

        header.add(textos, BorderLayout.WEST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabs.addTab("OP-010 Registrar", panelRegistrar());
        tabs.addTab("OP-011 Asignar", panelAsignar());
        tabs.addTab("OP-012 / OP-013 Repartidor", panelRepartidor());
        tabs.addTab("OP-014 Productos", panelProductos());

        contenido.add(header, BorderLayout.NORTH);
        contenido.add(tabs, BorderLayout.CENTER);

        root.add(contenido, BorderLayout.CENTER);
    }

    private JPanel panelRegistrar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setOpaque(false);

        txtCliente = new JTextField();
        txtTelefono = new JTextField();
        txtDireccion = new JTextField();
        txtObservacion = new JTextArea(3, 20);
        txtObservacion.setLineWrap(true);
        txtObservacion.setWrapStyleWord(true);

        cboProductos = new JComboBox<>();
        spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 50, 1));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        agregarCampo(formulario, c, 0, "Cliente:", txtCliente);
        agregarCampo(formulario, c, 1, "Teléfono / DNI:", txtTelefono);
        agregarCampo(formulario, c, 2, "Dirección:", txtDireccion);
        agregarCampo(formulario, c, 3, "Producto:", cboProductos);
        agregarCampo(formulario, c, 4, "Cantidad:", spCantidad);

        c.gridx = 0;
        c.gridy = 5;
        c.weightx = 0;
        formulario.add(label("Observación:"), c);

        c.gridx = 1;
        c.gridy = 5;
        c.weightx = 1;
        formulario.add(new JScrollPane(txtObservacion), c);

        JButton btnAgregar = botonPrimario("Agregar producto");
        JButton btnQuitar = botonSecundario("Quitar producto");
        JButton btnRegistrar = botonPrimario("Registrar pedido");
        JButton btnLimpiar = botonSecundario("Limpiar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        botones.add(btnAgregar);
        botones.add(btnQuitar);
        botones.add(btnRegistrar);
        botones.add(btnLimpiar);

        modeloDetalle = new DefaultTableModel(
                new String[]{"ID", "Producto", "Cantidad", "Precio", "Subtotal"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaDetalle = crearTabla(modeloDetalle);

        lblTotal = new JLabel("Total: S/ 0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(AZUL);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(new JScrollPane(tablaDetalle), BorderLayout.CENTER);
        centro.add(lblTotal, BorderLayout.SOUTH);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(centro, BorderLayout.CENTER);

        btnAgregar.addActionListener(e -> agregarProducto());
        btnQuitar.addActionListener(e -> quitarProducto());
        btnRegistrar.addActionListener(e -> registrarPedido());
        btnLimpiar.addActionListener(e -> limpiarRegistro());

        return panel;
    }

    private JPanel panelAsignar() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloPendientes = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Dirección", "Total", "Fecha", "Estado"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaPendientes = crearTabla(modeloPendientes);
        cboRepartidorAsignar = new JComboBox<>();

        JButton btnAsignar = botonPrimario("Asignar repartidor");
        JButton btnRefrescar = botonSecundario("Refrescar");

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setOpaque(false);
        barra.add(label("Repartidor:"));
        cboRepartidorAsignar.setPreferredSize(new Dimension(280, 35));
        barra.add(cboRepartidorAsignar);
        barra.add(btnAsignar);
        barra.add(btnRefrescar);

        panel.add(barra, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);

        btnAsignar.addActionListener(e -> asignarRepartidor());
        btnRefrescar.addActionListener(e -> cargarPendientesYRepartidores());

        return panel;
    }

    private JPanel panelRepartidor() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        modeloAsignados = new DefaultTableModel(
                new String[]{"ID", "Cliente", "Dirección", "Estado", "Total", "Fecha"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaAsignados = crearTabla(modeloAsignados);
        cboRepartidorConsulta = new JComboBox<>();

        JButton btnBuscar = botonSecundario("Buscar pedidos");

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT));
        barra.setOpaque(false);
        barra.add(label("Repartidor:"));
        cboRepartidorConsulta.setPreferredSize(new Dimension(280, 35));
        barra.add(cboRepartidorConsulta);
        barra.add(btnBuscar);

        cboEstado = new JComboBox<>(new String[]{
            "EN_CAMINO", "ENTREGADO", "NO_ENTREGADO", "REPROGRAMADO"
        });

        txtEvidencia = new JTextField();
        txtMotivo = new JTextArea(3, 20);
        txtMotivo.setLineWrap(true);
        txtMotivo.setWrapStyleWord(true);

        JButton btnActualizar = botonPrimario("Actualizar estado");

        JPanel formEstado = new JPanel(new GridBagLayout());
        formEstado.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        agregarCampo(formEstado, c, 0, "Nuevo estado:", cboEstado);
        agregarCampo(formEstado, c, 1, "Evidencia:", txtEvidencia);

        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 0;
        formEstado.add(label("Motivo:"), c);

        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 1;
        formEstado.add(new JScrollPane(txtMotivo), c);

        c.gridx = 1;
        c.gridy = 3;
        formEstado.add(btnActualizar, c);

        JPanel norte = new JPanel(new BorderLayout());
        norte.setOpaque(false);
        norte.add(barra, BorderLayout.NORTH);
        norte.add(formEstado, BorderLayout.SOUTH);

        panel.add(norte, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaAsignados), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> cargarPedidosDelRepartidor());
        btnActualizar.addActionListener(e -> cambiarEstadoPedido());

        return panel;
    }

    private JPanel panelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel ayuda = new JLabel("OP-014: Control de productos disponibles y agotados.");
        ayuda.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        modeloProductos = new DefaultTableModel(
                new String[]{"ID", "Producto", "Precio", "Estado"}, 0
        ) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProductos = crearTabla(modeloProductos);

        JButton btnDisponible = botonPrimario("Marcar disponible");
        JButton btnAgotado = botonSecundario("Marcar agotado");
        JButton btnRefrescar = botonSecundario("Refrescar");

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botones.setOpaque(false);
        botones.add(btnDisponible);
        botones.add(btnAgotado);
        botones.add(btnRefrescar);

        JPanel arriba = new JPanel(new BorderLayout());
        arriba.setOpaque(false);
        arriba.add(ayuda, BorderLayout.WEST);
        arriba.add(botones, BorderLayout.EAST);

        panel.add(arriba, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaProductos), BorderLayout.CENTER);

        btnDisponible.addActionListener(e -> cambiarDisponibilidadProducto(1));
        btnAgotado.addActionListener(e -> cambiarDisponibilidadProducto(0));
        btnRefrescar.addActionListener(e -> cargarProductos());

        return panel;
    }

    private void cargarDatosIniciales() {
        if (pedidoController == null) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar a la base de datos.");
            return;
        }

        try {
            cargarProductosCombo();
            cargarPendientesYRepartidores();
            cargarProductos();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar datos. Revisa que las tablas PEDIDOS, PEDIDO_DETALLE, REPARTIDORES y PEDIDO_HISTORIAL existan.\n\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProductosCombo() {
        DefaultComboBoxModel<PlatoItem> modelo = new DefaultComboBoxModel<>();

        List<Plato> platos = pedidoController.listarProductosParaGestion();

        for (Plato p : platos) {
            modelo.addElement(new PlatoItem(p));
        }

        cboProductos.setModel(modelo);
    }

    private void cargarPendientesYRepartidores() {
        cargarRepartidores();
        cargarPendientes();
        cargarPedidosDelRepartidor();
    }

    private void cargarRepartidores() {
        List<Repartidor> repartidores = pedidoController.listarRepartidores();

        DefaultComboBoxModel<Repartidor> modelo1 = new DefaultComboBoxModel<>();
        DefaultComboBoxModel<Repartidor> modelo2 = new DefaultComboBoxModel<>();

        for (Repartidor r : repartidores) {
            modelo1.addElement(r);
            modelo2.addElement(r);
        }

        cboRepartidorAsignar.setModel(modelo1);
        cboRepartidorConsulta.setModel(modelo2);
    }

    private void cargarPendientes() {
        modeloPendientes.setRowCount(0);

        List<Pedido> pedidos = pedidoController.listarPedidosPendientes();

        for (Pedido p : pedidos) {
            modeloPendientes.addRow(new Object[]{
                p.getPedidoId(),
                p.getClienteNombre(),
                p.getDireccionEntrega(),
                formatoMoneda(p.getTotal()),
                formatoFecha(p.getFechaPedido()),
                p.getEstado()
            });
        }
    }

    private void cargarPedidosDelRepartidor() {
        modeloAsignados.setRowCount(0);

        Repartidor repartidor = (Repartidor) cboRepartidorConsulta.getSelectedItem();

        if (repartidor == null) {
            return;
        }

        List<Pedido> pedidos = pedidoController.listarPedidosPorRepartidor(repartidor.getRepartidorId());

        for (Pedido p : pedidos) {
            modeloAsignados.addRow(new Object[]{
                p.getPedidoId(),
                p.getClienteNombre(),
                p.getDireccionEntrega(),
                p.getEstado(),
                formatoMoneda(p.getTotal()),
                formatoFecha(p.getFechaPedido())
            });
        }
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);

        List<Plato> platos = pedidoController.listarProductosParaGestion();

        for (Plato p : platos) {
            modeloProductos.addRow(new Object[]{
                p.getPlatoId(),
                p.getNombre(),
                formatoMoneda(p.getPrecio()),
                p.getDisponible() == 1 ? "Disponible" : "Agotado"
            });
        }

        cargarProductosCombo();
    }

    private void agregarProducto() {
        PlatoItem item = (PlatoItem) cboProductos.getSelectedItem();

        if (item == null) {
            JOptionPane.showMessageDialog(this, "No hay productos cargados.");
            return;
        }

        Plato plato = item.getPlato();

        if (plato.getDisponible() != 1) {
            JOptionPane.showMessageDialog(this,
                    "Este producto está marcado como agotado y no puede venderse.",
                    "Producto no disponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad = (Integer) spCantidad.getValue();

        PedidoDetalle detalle = new PedidoDetalle(plato, cantidad);
        detalleActual.add(detalle);

        modeloDetalle.addRow(new Object[]{
            plato.getPlatoId(),
            plato.getNombre(),
            cantidad,
            formatoMoneda(detalle.getPrecioUnitario()),
            formatoMoneda(detalle.getSubtotal())
        });

        actualizarTotal();
    }

    private void quitarProducto() {
        int fila = tablaDetalle.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del detalle.");
            return;
        }

        int filaModelo = tablaDetalle.convertRowIndexToModel(fila);

        detalleActual.remove(filaModelo);
        modeloDetalle.removeRow(filaModelo);

        actualizarTotal();
    }

    private void registrarPedido() {
        try {
            Pedido pedido = new Pedido();
            pedido.setClienteNombre(txtCliente.getText().trim());
            pedido.setTelefonoCliente(txtTelefono.getText().trim());
            pedido.setDireccionEntrega(txtDireccion.getText().trim());
            pedido.setObservacion(txtObservacion.getText().trim());
            pedido.setDetalles(new ArrayList<>(detalleActual));

            int idPedido = pedidoController.registrarPedido(pedido);

            JOptionPane.showMessageDialog(this,
                    "Pedido registrado correctamente.\nEstado: PENDIENTE\nCódigo de pedido: " + idPedido,
                    "Registro correcto",
                    JOptionPane.INFORMATION_MESSAGE);

            limpiarRegistro();
            cargarPendientesYRepartidores();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limpiarRegistro() {
        txtCliente.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtObservacion.setText("");
        spCantidad.setValue(1);
        detalleActual.clear();
        modeloDetalle.setRowCount(0);
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;

        for (PedidoDetalle d : detalleActual) {
            total += d.getSubtotal();
        }

        lblTotal.setText("Total: " + formatoMoneda(total));
    }

    private void asignarRepartidor() {
        int fila = tablaPendientes.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido pendiente.");
            return;
        }

        Repartidor repartidor = (Repartidor) cboRepartidorAsignar.getSelectedItem();

        if (repartidor == null) {
            JOptionPane.showMessageDialog(this, "No hay repartidores disponibles.");
            return;
        }

        int filaModelo = tablaPendientes.convertRowIndexToModel(fila);
        int pedidoId = Integer.parseInt(String.valueOf(modeloPendientes.getValueAt(filaModelo, 0)));

        try {
            pedidoController.asignarRepartidor(pedidoId, repartidor.getRepartidorId());

            JOptionPane.showMessageDialog(this,
                    "Pedido asignado correctamente a: " + repartidor.getNombre());

            cargarPendientesYRepartidores();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cambiarEstadoPedido() {
        int fila = tablaAsignados.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un pedido asignado.");
            return;
        }

        int filaModelo = tablaAsignados.convertRowIndexToModel(fila);
        int pedidoId = Integer.parseInt(String.valueOf(modeloAsignados.getValueAt(filaModelo, 0)));

        String nuevoEstado = String.valueOf(cboEstado.getSelectedItem());
        String evidencia = txtEvidencia.getText().trim();
        String motivo = txtMotivo.getText().trim();

        try {
            pedidoController.cambiarEstadoPedido(pedidoId, nuevoEstado, evidencia, motivo);

            JOptionPane.showMessageDialog(this,
                    "Estado actualizado correctamente a: " + nuevoEstado);

            txtEvidencia.setText("");
            txtMotivo.setText("");

            cargarPendientesYRepartidores();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validación",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cambiarDisponibilidadProducto(int disponible) {
        int fila = tablaProductos.getSelectedRow();

        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto.");
            return;
        }

        int filaModelo = tablaProductos.convertRowIndexToModel(fila);
        int platoId = Integer.parseInt(String.valueOf(modeloProductos.getValueAt(filaModelo, 0)));

        try {
            pedidoController.actualizarDisponibilidadProducto(platoId, disponible);

            if (disponible == 1) {
                JOptionPane.showMessageDialog(this, "Producto marcado como Disponible.");
            } else {
                JOptionPane.showMessageDialog(this, "Producto marcado como Agotado.");
            }

            cargarProductos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints c, int fila, String texto, Component campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        panel.add(label(texto), c);

        c.gridx = 1;
        c.gridy = fila;
        c.weightx = 1;
        campo.setPreferredSize(new Dimension(250, 32));
        panel.add(campo, c);
    }

    private JLabel label(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JTable crearTabla(DefaultTableModel modelo) {
        JTable tabla = new JTable(modelo);
        tabla.setRowHeight(32);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.setSelectionBackground(new Color(232, 241, 255));
        tabla.setSelectionForeground(Color.BLACK);

        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);

        return tabla;
    }

    private JButton botonPrimario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(AZUL);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
    }

    private JButton botonSecundario(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(Color.WHITE);
        btn.setForeground(AZUL);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
    }

    private String formatoMoneda(double valor) {
        return String.format(java.util.Locale.US, "S/ %.2f", valor);
    }

    private String formatoFecha(Date fecha) {
        if (fecha == null) {
            return "";
        }

        return new SimpleDateFormat("dd/MM/yyyy HH:mm").format(fecha);
    }

    private static class PlatoItem {

        private final Plato plato;

        public PlatoItem(Plato plato) {
            this.plato = plato;
        }

        public Plato getPlato() {
            return plato;
        }

        @Override
        public String toString() {
            String estado = plato.getDisponible() == 1 ? "Disponible" : "Agotado";
            return plato.getNombre() + " - S/ "
                    + String.format(java.util.Locale.US, "%.2f", plato.getPrecio())
                    + " (" + estado + ")";
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PedidosFrame().setVisible(true));
    }
}