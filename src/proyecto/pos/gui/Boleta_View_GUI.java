package proyecto.pos.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import proyecto.pos.model.Venta;
import proyecto.pos.model.VentaDetalle;
import proyecto.pos.dao.impl.VentaDAOImpl;
import proyecto.pos.model.PDFGenerator;
import proyecto.pos.model.NumeroALetras;

public class Boleta_View_GUI extends JDialog {

    private Connection conexion;
    private int ventaId;
    private JTextArea txtTicket;

    public Boleta_View_GUI(JFrame parent, int ventaId, Connection con) {
        super(parent, "Comprobante de Pago #" + ventaId, true);
        this.ventaId = ventaId;
        this.conexion = con;
        
        configurarVentana();
        initComponents();
        cargarDatosYMostrar();
    }

    private void configurarVentana() {
        setSize(380, 650);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());
    }

    private void initComponents() {
        txtTicket = new JTextArea();
        txtTicket.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTicket.setEditable(false);
        txtTicket.setBackground(new Color(253, 253, 240)); 
        txtTicket.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scroll = new JScrollPane(txtTicket);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        JPanel panelAcciones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panelAcciones.setBackground(Color.WHITE);
        panelAcciones.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnPdf = new JButton("Exportar a PDF 📄");
        btnPdf.setBackground(new Color(220, 53, 69)); 
        btnPdf.setForeground(Color.WHITE);
        btnPdf.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPdf.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnPdf.addActionListener(e -> exportarAPDF());
        btnCerrar.addActionListener(e -> dispose());

        panelAcciones.add(btnPdf);
        panelAcciones.add(btnCerrar);
        add(panelAcciones, BorderLayout.SOUTH);
    }

  private void cargarDatosYMostrar() {
    VentaDAOImpl dao = new VentaDAOImpl(conexion);
    Venta v = dao.obtenerPorId(ventaId);

    if (v != null) {
        StringBuilder sb = new StringBuilder();
        sb.append("      LA BUENA VIDA    \n");
        sb.append("      RUC: 20601234567            \n");
        sb.append("    Av. Larco 543 - Trujillo      \n");
        sb.append("==================================\n");
        sb.append(" BOLETA ELECTRÓNICA: B001-").append(String.format("%05d", v.getVentaId())).append("\n");
        
        // --- FECHA SEGURA (Si es null, no rompe el programa) ---
        String fechaTexto = "Sin Fecha";
        if (v.getFecha() != null) {
            try {
                fechaTexto = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(v.getFecha());
            } catch (Exception e) { fechaTexto = "Error Fecha"; }
        }
        sb.append(" FECHA: ").append(fechaTexto).append("\n");
        
        // --- PERSONAL Y CLIENTE SEGURO ---
        String cajero = (v.getEmpleado() != null) ? v.getEmpleado().getNombre() : "Cajero Principal";
        String cliente = (v.getCliente() != null) ? v.getCliente().getNombre() : "Público General";
        sb.append(" CAJERO: ").append(cajero).append("\n");
        sb.append(" CLIENTE: ").append(cliente).append("\n");
        
        // --- UBICACIÓN SEGURA ---
        String mesaTexto = "Para Llevar";
        if (v.getMesa() != null && v.getMesa().getMesaId() > 0) {
            mesaTexto = "Mesa " + v.getMesa().getMesaId();
        }
        sb.append(" UBICACIÓN: ").append(mesaTexto).append("\n");
        
        sb.append("==================================\n");
        sb.append(String.format(" %-18s %3s %7s\n", "PRODUCTO", "CANT", "SUBT."));
        sb.append("----------------------------------\n");

        // --- DETALLES SEGUROS ---
        if (v.getDetalles() != null) {
            for (VentaDetalle det : v.getDetalles()) {
                String nombre = (det.getPlato() != null) ? det.getPlato().getNombre() : "Producto";
                if (nombre.length() > 18) nombre = nombre.substring(0, 15) + "...";
                sb.append(String.format(" %-18s %4d %8.2f\n", nombre, det.getCantidad(), det.getSubtotal()));
            }
        }

        sb.append("==================================\n");
        sb.append(String.format(" SUBTOTAL:           S/ %10.2f\n", v.getSubtotal()));
        
        // PARCHE DEL % (Doble %% para String.format)
        sb.append(String.format(" IGV (18%%):          S/ %10.2f\n", v.getIgv()));
        
        if(v.getDescuento() > 0) {
            sb.append(String.format(" DESCUENTO:        - S/ %10.2f\n", v.getDescuento()));
        }
        sb.append("----------------------------------\n");
        sb.append(String.format(" TOTAL A PAGAR:      S/ %10.2f\n", v.getTotal()));
        sb.append("==================================\n");
        
        // CONVERTIDOR DE LETRAS
        try {
            sb.append(proyecto.pos.model.NumeroALetras.convertir(v.getTotal())).append("\n");
        } catch(Exception e) {
            sb.append("SON: SOLES\n");
        }
        
        sb.append("\n    ¡Gracias por su consumo!    \n");
        sb.append("  Vuelva pronto a La Buena Vida  \n");

        txtTicket.setText(sb.toString());
        
    } else {
        txtTicket.setText("\n\n [!] Error: No se pudo recuperar\n     la venta de la base de datos.");
    }
}

    private void exportarAPDF() {
        VentaDAOImpl dao = new VentaDAOImpl(conexion);
        Venta v = dao.obtenerPorId(ventaId);
        if (v != null) {
            PDFGenerator.crearBoleta(v);
            JOptionPane.showMessageDialog(this, "Boleta exportada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}