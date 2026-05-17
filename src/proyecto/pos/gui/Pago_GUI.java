package proyecto.pos.gui;

import java.sql.SQLException;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.controller.VentaController;
import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.dao.interfaces.VentaDAO;
import proyecto.pos.model.Cliente;
import proyecto.pos.model.Mesa;
import proyecto.pos.model.Plato;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;
import proyecto.pos.service.VentaService;
import java.util.Date;
import proyecto.pos.dao.impl.EmpleadoDAOImpl;
import proyecto.pos.dao.interfaces.EmpleadoDAO;
import proyecto.pos.model.ComprobantePago;
import proyecto.pos.model.Empleado;
import proyecto.pos.model.EstadoPago;
import proyecto.pos.model.MetodoPago;

public class Pago_GUI extends JDialog {

    private EmpleadoDAO empleado_dao;
    private double total;
    private String nombreCliente;
    private Caja_GUI cajaPadre; 
    private Cliente cliente;
    private Mesa mesa;
    private ArrayList<Plato> platos;
    private JTextField txtEfectivo, txtTarjeta;
    private JLabel lblRestante;
    private JButton btnGuardarImprimir, btnGuardarSolo;
    private VentaController venta_controller;
    
    public Pago_GUI(Caja_GUI parent, double total, Cliente cliente, ArrayList<Plato> platos, Mesa mesa, Connection conexion) {
        super(parent, "Pago", true); 
        this.venta_controller = new VentaController(conexion);
        this.cajaPadre = parent;
        this.platos = platos;
        this.cliente = cliente;
        this.mesa = mesa;
        this.total = total;
        
        if (this.cliente != null) {
            System.out.println(mesa.getNumero_mesa());
            this.nombreCliente = cliente.getNombre() + " " + cliente.getApellidos() + " ------- Mesa: " + String.valueOf(mesa.getNumero_mesa());
        } else {
            this.nombreCliente = "Público General ------- Mesa: " + String.valueOf(mesa.getNumero_mesa());
        }
 
        this.empleado_dao = new EmpleadoDAOImpl(conexion);

        VentaController venta_controller = new VentaController(conexion);
        
        configurarVentana();
        initComponents();
        actualizarCalculos(); 
    }
    
    private void configurarVentana() {
        setSize(450, 500); 
        setLocationRelativeTo(getOwner());
        setUndecorated(true); 
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel("<html>Pago <font size='4' color='gray'>| " + nombreCliente + "</font></html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCerrar.setForeground(Color.GRAY);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.addActionListener(e -> dispose());
        cabecera.add(lblTitulo, BorderLayout.WEST);
        cabecera.add(btnCerrar, BorderLayout.EAST);

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);

        JLabel lblInstruccion = new JLabel("Ingrese el monto a pagar por método:");
        lblInstruccion.setForeground(Color.GRAY);

        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(new Color(242, 246, 255));
        panelTotal.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel txtTotal = new JLabel("Total a pagar:");
        txtTotal.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel numTotal = new JLabel("S/ " + String.format(java.util.Locale.US, "%.2f", total));
        numTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        numTotal.setForeground(new Color(26, 79, 156));
        panelTotal.add(txtTotal, BorderLayout.WEST);
        panelTotal.add(numTotal, BorderLayout.EAST);

        JPanel panelEntradas = new JPanel(new GridLayout(2, 2, 10, 15));
        panelEntradas.setBackground(Color.WHITE);
        JLabel lblEfectivo = new JLabel("Monto en Efectivo:");
        lblEfectivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtEfectivo = new JTextField("");
        txtEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel lblTarjeta = new JLabel("Monto en Tarjeta:");
        lblTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTarjeta = new JTextField("");
        txtTarjeta.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        DocumentFilter decimalFilter = new DocumentFilter() {
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if ((fb.getDocument().getText(0, fb.getDocument().getLength()) + string).matches("\\d*\\.?\\d*")) super.insertString(fb, offset, string, attr);
            }
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                if ((currentText.substring(0, offset) + text + currentText.substring(offset + length)).matches("\\d*\\.?\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        };

        ((AbstractDocument) txtEfectivo.getDocument()).setDocumentFilter(decimalFilter);
        ((AbstractDocument) txtTarjeta.getDocument()).setDocumentFilter(decimalFilter);

        panelEntradas.add(lblEfectivo); panelEntradas.add(txtEfectivo);
        panelEntradas.add(lblTarjeta); panelEntradas.add(txtTarjeta);
        agregarEscuchaCalculo(txtEfectivo); agregarEscuchaCalculo(txtTarjeta);

        JPanel panelResultado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelResultado.setBackground(Color.WHITE);
        lblRestante = new JLabel("Falta: S/ " + String.format(java.util.Locale.US, "%.2f", total));
        lblRestante.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRestante.setForeground(new Color(220, 53, 69)); 
        panelResultado.add(lblRestante);

        cuerpo.add(Box.createVerticalStrut(20)); cuerpo.add(lblInstruccion);
        cuerpo.add(Box.createVerticalStrut(15)); cuerpo.add(panelTotal);
        cuerpo.add(Box.createVerticalStrut(25)); cuerpo.add(panelEntradas);
        cuerpo.add(Box.createVerticalStrut(20)); cuerpo.add(panelResultado);

        JPanel pie = new JPanel(new GridLayout(2, 1, 0, 10)); 
        pie.setBackground(Color.WHITE); pie.setPreferredSize(new Dimension(0, 90));

        btnGuardarImprimir = new JButton("Completar Pago e Imprimir Recibo");
        btnGuardarImprimir.setBackground(new Color(26, 79, 156));
        btnGuardarImprimir.setForeground(Color.WHITE);
        btnGuardarImprimir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnGuardarSolo = new JButton("Solo Guardar Venta");
        btnGuardarSolo.setBackground(Color.WHITE);
        btnGuardarSolo.setBorder(BorderFactory.createLineBorder(new Color(26, 79, 156)));
        btnGuardarSolo.setForeground(new Color(26, 79, 156));
        btnGuardarSolo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGuardarImprimir.addActionListener(e -> procesarYGuardarVenta("Imprimiendo recibo..."));
        btnGuardarSolo.addActionListener(e -> procesarYGuardarVenta("Venta guardada exitosamente en el sistema."));

        pie.add(btnGuardarImprimir); pie.add(btnGuardarSolo);

        panelPrincipal.add(cabecera, BorderLayout.NORTH);
        panelPrincipal.add(cuerpo, BorderLayout.CENTER);
        panelPrincipal.add(pie, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void procesarYGuardarVenta(String mensajeFinal) {
        try {
            if (this.cliente == null) {
                this.cliente = new Cliente();
                this.cliente.setId(1); 
            }

            Venta venta = new Venta();
            venta.setCliente(this.cliente);
            venta.setCaja_id(1); 
            venta.setDetalles(obtenerVentaDetalle());
            venta.setDescuento(0); 
            
            double igvCalculado = total - (total / 1.18);
            double subtotalCalculado = total - igvCalculado;
            
            venta.setSubtotal(subtotalCalculado);
            venta.setFecha(new Date());
            venta.setIgv(igvCalculado);
            venta.setTotal(total);
            venta.setComprobantes(obtenerComprobantesPago());
            venta.setMesa(mesa);
            venta.setEmpleado(obtenerEmpleado());
            venta.setEstadoPago(EstadoPago.PAGADO);

            venta_controller.registrarVenta(venta);
            descontar();
            finalizado(mensajeFinal);
            cajaPadre.setTotalAcumulado(0);
        } catch (Exception ex) {
            ex.printStackTrace(); // Imprime el error real en la consola
            JOptionPane.showMessageDialog(this, "Error en Base de Datos: " + ex.getMessage(), "Fallo al Guardar", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Empleado obtenerEmpleado(){
        Empleado empleado = empleado_dao.obtenerPorId(1); 
        if (empleado == null) {
            empleado = new Empleado();
            empleado.setId(1); 
        }
        return empleado;
    }
    
    private ArrayList<ComprobantePago> obtenerComprobantesPago() {
        ArrayList<ComprobantePago> comprobantes = new ArrayList<>();
        ComprobantePago comprobante = new ComprobantePago();
        comprobante.setEstado("PAGADO");
        comprobante.setFecha_emision(new Date());
        comprobante.setMetodo_pago(new MetodoPago(1,"BCP")); 
        
        int randomNum = (int)(Math.random() * 9000) + 1000;
        comprobante.setSerie_numero("BOLET-" + randomNum);
        comprobante.setTipo_comprobante("BOLETA");
        comprobantes.add(comprobante);
        return comprobantes;
    }
    
private ArrayList<VentaDetalle> obtenerVentaDetalle() {
        ArrayList<VentaDetalle> venta_detalle = new ArrayList<>();
        // Ahora agrupamos por el ID del plato (Integer) para que sea exacto y no por memoria
        Map<Integer, VentaDetalle> agrupacion = new HashMap<>();

        for (Plato p : platos) {
            if (agrupacion.containsKey(p.getPlatoId())) {
                // Si el plato ya está, le sumamos 1 a la cantidad y actualizamos su subtotal
                VentaDetalle det = agrupacion.get(p.getPlatoId());
                det.setCantidad(det.getCantidad() + 1);
                det.setSubtotal(det.getCantidad() * det.getPrecioUnitario());
            } else {
                // Si es la primera vez que entra, lo creamos con cantidad 1
                VentaDetalle det = new VentaDetalle(p, 1, p.getPrecio(), p.getPrecio(), "");
                agrupacion.put(p.getPlatoId(), det);
            }
        }
        
        venta_detalle.addAll(agrupacion.values());
        return venta_detalle;
    }
    
    private void agregarEscuchaCalculo(JTextField textField) {
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { actualizarCalculos(); }
            public void removeUpdate(DocumentEvent e) { actualizarCalculos(); }
            public void insertUpdate(DocumentEvent e) { actualizarCalculos(); }
        });
    }

    private void actualizarCalculos() {
        try {
            double pagoEfectivo = txtEfectivo.getText().isEmpty() ? 0 : Double.parseDouble(txtEfectivo.getText());
            double pagoTarjeta = txtTarjeta.getText().isEmpty() ? 0 : Double.parseDouble(txtTarjeta.getText());
            double sumatoria = pagoEfectivo + pagoTarjeta;
            double diferencia = total - sumatoria;

            if (diferencia > 0) {
                lblRestante.setText("Falta: S/ " + String.format(java.util.Locale.US, "%.2f", diferencia));
                lblRestante.setForeground(new Color(220, 53, 69)); 
                btnGuardarImprimir.setEnabled(false); btnGuardarSolo.setEnabled(false);
            } else {
                btnGuardarImprimir.setEnabled(true); btnGuardarSolo.setEnabled(true);
                if (diferencia == 0) {
                    lblRestante.setText("Pago exacto ✓");
                    lblRestante.setForeground(new Color(40, 167, 69)); 
                } else {
                    lblRestante.setText("Vuelto a entregar: S/ " + String.format(java.util.Locale.US, "%.2f", Math.abs(diferencia)));
                    lblRestante.setForeground(new Color(40, 167, 69)); 
                }
            }
        } catch (NumberFormatException e) {
            lblRestante.setText("Entrada inválida");
            lblRestante.setForeground(Color.RED);
            btnGuardarImprimir.setEnabled(false); btnGuardarSolo.setEnabled(false);
        }
    }
    
    private void descontar(){
        for(Plato p : platos){
            venta_controller.descontarStockPorVenta(p.getPlatoId(),1);
        }
    }
    private void finalizado(String mensaje) {
        JOptionPane.showMessageDialog(this, "¡Operación exitosa!\n" + mensaje);
        cajaPadre.vaciarTodo(); 
        this.dispose(); 
        SwingUtilities.invokeLater(() -> new HistorialTransaccionesFrame().setVisible(true));
    }
}