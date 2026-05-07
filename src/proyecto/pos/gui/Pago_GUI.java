package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class Pago_GUI extends JDialog {

    private double total;
    private String nombreCliente;
    private Caja_GUI cajaPadre; 
    
    private JComboBox<String> cbComprobante;
    private JTextField txtDocumento;
    private JLabel lblDocumento;

    private JTextField txtEfectivo, txtTarjeta, txtYape;
    private JLabel lblRestante;
    private JButton btnGuardarImprimir, btnGuardarSolo;

    public Pago_GUI(Caja_GUI parent, double total, String cliente) {
        super(parent, "Pago", true); 
        this.cajaPadre = parent;
        this.total = total;
        this.nombreCliente = cliente;
        configurarVentana();
        initComponents();
        actualizarCalculos(); 
    }

    private void configurarVentana() {
        setSize(480, 620); // Tamaño aumentado para los nuevos campos
        setLocationRelativeTo(getOwner());
        setUndecorated(true); 
        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
    }

    private void initComponents() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(new EmptyBorder(20, 25, 20, 25));

        // --- CABECERA ---
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

        // --- CUERPO ---
        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setBackground(Color.WHITE);

        // 1. SECCIÓN COMPROBANTE
        JPanel panelComprobante = new JPanel(new GridLayout(2, 2, 10, 5));
        panelComprobante.setBackground(Color.WHITE);
        panelComprobante.setMaximumSize(new Dimension(500, 60));

        JLabel lblTipo = new JLabel("Tipo de Comprobante:");
        lblTipo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cbComprobante = new JComboBox<>(new String[]{"Boleta Simple", "Boleta con DNI", "Factura"});
        cbComprobante.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        lblDocumento = new JLabel("Documento:");
        lblDocumento.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtDocumento = new JTextField("");
        txtDocumento.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDocumento.setEnabled(false);

        panelComprobante.add(lblTipo);
        panelComprobante.add(cbComprobante);
        panelComprobante.add(lblDocumento);
        panelComprobante.add(txtDocumento);

        // Lógica de Comprobante
        cbComprobante.addActionListener(e -> configurarFiltroDocumento());

        // 2. TOTAL A PAGAR
        JPanel panelTotal = new JPanel(new BorderLayout());
        panelTotal.setBackground(new Color(242, 246, 255));
        panelTotal.setMaximumSize(new Dimension(500, 60));
        panelTotal.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel txtTotal = new JLabel("Total a pagar:");
        txtTotal.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        JLabel numTotal = new JLabel("S/ " + String.format("%.2f", total));
        numTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        numTotal.setForeground(new Color(26, 79, 156));
        
        panelTotal.add(txtTotal, BorderLayout.WEST);
        panelTotal.add(numTotal, BorderLayout.EAST);

        // 3. ENTRADAS DE PAGO DIVIDIDO
        JLabel lblInstruccion = new JLabel("Ingrese el monto a pagar por método:");
        lblInstruccion.setForeground(Color.GRAY);
        lblInstruccion.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel panelEntradas = new JPanel(new GridLayout(3, 2, 10, 15));
        panelEntradas.setBackground(Color.WHITE);
        panelEntradas.setMaximumSize(new Dimension(500, 120));

        JLabel lblEfectivo = new JLabel("Monto en Efectivo:");
        lblEfectivo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtEfectivo = new JTextField("");
        txtEfectivo.putClientProperty("JTextField.placeholderText", "0.00");
        txtEfectivo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        
        JLabel lblTarjeta = new JLabel("Monto en Tarjeta:");
        lblTarjeta.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtTarjeta = new JTextField("");
        txtTarjeta.putClientProperty("JTextField.placeholderText", "0.00");
        txtTarjeta.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JLabel lblYape = new JLabel("Monto en Yape/Plin:");
        lblYape.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtYape = new JTextField("");
        txtYape.putClientProperty("JTextField.placeholderText", "0.00");
        txtYape.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        // VALIDACIÓN: SOLO NÚMEROS Y DECIMALES
        DocumentFilter decimalFilter = new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (newStr.matches("\\d*\\.?\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newStr = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newStr.matches("\\d*\\.?\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        };

        ((AbstractDocument) txtEfectivo.getDocument()).setDocumentFilter(decimalFilter);
        ((AbstractDocument) txtTarjeta.getDocument()).setDocumentFilter(decimalFilter);
        ((AbstractDocument) txtYape.getDocument()).setDocumentFilter(decimalFilter);

        panelEntradas.add(lblEfectivo);
        panelEntradas.add(txtEfectivo);
        panelEntradas.add(lblTarjeta);
        panelEntradas.add(txtTarjeta);
        panelEntradas.add(lblYape);
        panelEntradas.add(txtYape);
        
        agregarEscuchaCalculo(txtEfectivo);
        agregarEscuchaCalculo(txtTarjeta);
        agregarEscuchaCalculo(txtYape);

        // 4. BANNER DE RESULTADO
        JPanel panelResultado = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelResultado.setBackground(Color.WHITE);
        lblRestante = new JLabel("Falta: S/ " + String.format("%.2f", total));
        lblRestante.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRestante.setForeground(new Color(220, 53, 69)); 
        panelResultado.add(lblRestante);

        // ENSAMBLAR CUERPO
        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(panelComprobante);
        cuerpo.add(Box.createVerticalStrut(15));
        cuerpo.add(panelTotal);
        cuerpo.add(Box.createVerticalStrut(15));
        cuerpo.add(lblInstruccion);
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(panelEntradas);
        cuerpo.add(Box.createVerticalStrut(20));
        cuerpo.add(panelResultado);

        // --- PIE (BOTONES DE ACCIÓN) ---
        JPanel pie = new JPanel(new GridLayout(2, 1, 0, 10)); 
        pie.setBackground(Color.WHITE);
        pie.setPreferredSize(new Dimension(0, 90));

        btnGuardarImprimir = new JButton("Completar Pago e Imprimir Recibo");
        btnGuardarImprimir.setBackground(new Color(26, 79, 156));
        btnGuardarImprimir.setForeground(Color.WHITE);
        btnGuardarImprimir.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        btnGuardarSolo = new JButton("Solo Guardar Venta");
        btnGuardarSolo.setBackground(Color.WHITE);
        btnGuardarSolo.setBorder(BorderFactory.createLineBorder(new Color(26, 79, 156)));
        btnGuardarSolo.setForeground(new Color(26, 79, 156));
        btnGuardarSolo.setFont(new Font("Segoe UI", Font.BOLD, 14));

        btnGuardarImprimir.addActionListener(e -> procesarVenta("Imprimiendo recibo..."));
        btnGuardarSolo.addActionListener(e -> procesarVenta("Venta guardada."));

        pie.add(btnGuardarImprimir);
        pie.add(btnGuardarSolo);

        panelPrincipal.add(cabecera, BorderLayout.NORTH);
        panelPrincipal.add(cuerpo, BorderLayout.CENTER);
        panelPrincipal.add(pie, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    private void configurarFiltroDocumento() {
        int index = cbComprobante.getSelectedIndex();
        txtDocumento.setText("");

        if (index == 0) { // Boleta Simple
            lblDocumento.setText("Documento:");
            txtDocumento.setEnabled(false);
            ((AbstractDocument) txtDocumento.getDocument()).setDocumentFilter(null);
        } else {
            txtDocumento.setEnabled(true);
            int maxLimit = (index == 1) ? 8 : 11; // 1 = DNI, 2 = Factura/RUC
            lblDocumento.setText((index == 1) ? "DNI (8 dígitos):" : "RUC (11 dígitos):");

            ((AbstractDocument) txtDocumento.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    if (text == null) return;
                    if ((fb.getDocument().getLength() + text.length() - length) <= maxLimit && text.matches("\\d+")) {
                        super.replace(fb, offset, length, text, attrs);
                    }
                }
            });
        }
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
            double pagoYape = txtYape.getText().isEmpty() ? 0 : Double.parseDouble(txtYape.getText());
            
            double sumatoria = pagoEfectivo + pagoTarjeta + pagoYape;
            double diferencia = total - sumatoria;

            if (diferencia > 0) {
                lblRestante.setText("Falta: S/ " + String.format("%.2f", diferencia));
                lblRestante.setForeground(new Color(220, 53, 69)); 
                btnGuardarImprimir.setEnabled(false);
                btnGuardarSolo.setEnabled(false);
            } else {
                btnGuardarImprimir.setEnabled(true);
                btnGuardarSolo.setEnabled(true);
                
                if (Math.abs(diferencia) < 0.01) { // Tolerancia por precisión de decimales
                    lblRestante.setText("Pago exacto ✓");
                    lblRestante.setForeground(new Color(40, 167, 69)); 
                } else {
                    lblRestante.setText("Vuelto a entregar: S/ " + String.format("%.2f", Math.abs(diferencia)));
                    lblRestante.setForeground(new Color(40, 167, 69)); 
                }
            }
        } catch (NumberFormatException e) {
            lblRestante.setText("Entrada inválida");
            lblRestante.setForeground(Color.RED);
            btnGuardarImprimir.setEnabled(false);
            btnGuardarSolo.setEnabled(false);
        }
    }

    private void procesarVenta(String mensaje) {
        int tipoComprobante = cbComprobante.getSelectedIndex();
        String doc = txtDocumento.getText().trim();

        // Validaciones Finales antes de proceder
        if (tipoComprobante == 1 && doc.length() != 8) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un DNI válido de 8 dígitos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (tipoComprobante == 2 && doc.length() != 11) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un RUC válido de 11 dígitos.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Aquí iría el envío al Backend...

        JOptionPane.showMessageDialog(this, "¡Venta realizada con éxito!\n" + mensaje);
        cajaPadre.vaciarTodo(); 
        this.dispose(); 
    }
}