package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;

import java.io.File;

import java.net.URL;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap; 
import java.util.Map;     

import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.PlatoDAOImpl;
import proyecto.pos.dao.interfaces.PlatoDAO;
import java.sql.*;
import proyecto.pos.dao.impl.ClienteDAOImpl;
import proyecto.pos.dao.interfaces.ClienteDAO;
import proyecto.pos.model.Cliente;
import proyecto.pos.model.Mesa;
import proyecto.pos.model.Plato;

public class Caja_GUI extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color ROJO = new Color(220, 53, 69);
    private static final Color VERDE = new Color(40, 167, 69);
    
    private Connection conexion;
    private ArrayList<Plato> platos_seleccionados = new ArrayList<>();

    private Map<String, ItemCarritoUI> mapaCarrito = new HashMap<>();

    private ArrayList<Plato> obtenerPlatosBD() {
        PlatoDAO plato_dao = new PlatoDAOImpl(conexion);
        return (ArrayList<Plato>) plato_dao.listar();
    }//
    
    private JPanel panelCarritoContenedor;
    private JPanel contenedorPrincipal;   
    private JTextField txtBuscar;
    private JTextField txtCliente, txtMesa; 
    private JButton btnPagar, btnVaciar;
    
    private CardLayout cardLayoutAtencion;
    private JPanel panelTarjetasAtencion;
    private String tipoAtencionActual = "MESA"; 
    private JTextField txtLlevarNombre;
    private JTextField txtDeliveryDir;
    
    private JLabel lblMontoSubtotal;
    private JLabel lblMontoDescuento;
    private JLabel lblMontoIGV;
    private JLabel lblTotalPagar;

    private double totalAcumulado = 0.0;

    // --- Variables Seguras de Descuento ---
    private double reglaDescuentoPorcentaje = 0.0; 
    private double reglaDescuentoFijo = 0.0;
    private JButton btnAplicarDescuento;

    private String filtroCategoriaActual = "Todos"; 
    private JPanel panelFiltros; 
    
    public Caja_GUI() {
        DatabaseConnection db = new DatabaseConnection();
        this.conexion = db.conectar();
        configurarVentana();
        initComponents();
        filtrarProductos(); 
    }

    private void configurarVentana() { 
        FlatLightLaf.setup();
        setTitle("Sistema de Caja - POS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(1280, 720); 
        setMinimumSize(new Dimension(1100, 620)); 
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        add(new MenuSidebar(this, "Cajero"), BorderLayout.WEST);

        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);
        cabeceraCompleta.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 40, 10, 40));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);

        JLabel lblPagina = new JLabel("Caja");
        lblPagina.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblEmpresa = new JLabel("La Buena Vida");
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmpresa.setForeground(Color.GRAY);

        panelTitulo.add(lblPagina);
        panelTitulo.add(lblEmpresa);
        topBar.add(panelTitulo, BorderLayout.WEST);

        JPanel panelPerfil = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelPerfil.setBackground(Color.WHITE);

        JLabel lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Timer timerReloj = new Timer(1000, e -> {
            String horaActual = sdf.format(new Date());
            lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + horaActual + "</font></html>");
        });
        timerReloj.start();
        lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + sdf.format(new Date()) + "</font></html>");

        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Cajero</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        JLabel lblAvatar = new JLabel();
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        lblAvatar.setBackground(Color.LIGHT_GRAY);
        lblAvatar.setOpaque(true);
        ImageIcon fotoPerfil = MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 40, 40);
        if (fotoPerfil != null) {
            lblAvatar.setIcon(fotoPerfil);
        }

        panelPerfil.add(lblHora);
        panelPerfil.add(lblUsuario);
        panelPerfil.add(lblAvatar);
        topBar.add(panelPerfil, BorderLayout.EAST);

        JPanel panelBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 10));
        panelBuscador.setBackground(Color.WHITE);

        txtBuscar = new JTextField();
        txtBuscar.setPreferredSize(new Dimension(350, 40)); 
        txtBuscar.putClientProperty("JTextField.placeholderText", "Buscar producto (F2)...");
        txtBuscar.putClientProperty("JComponent.roundRect", true); 
        
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });
        
        panelBuscador.add(txtBuscar);

        panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 40, 10));
        panelFiltros.setBackground(Color.WHITE);
        
        String[] categoriasMenu = {"Todos", "Platos principales", "Entradas", "Bebidas", "Postres"};
        ButtonGroup bgFiltros = new ButtonGroup();

        for (String cat : categoriasMenu) {
            JToggleButton btnFiltro = new JToggleButton(cat);
            btnFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnFiltro.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnFiltro.setFocusPainted(false);
            
            btnFiltro.setBackground(new Color(245, 247, 250));
            btnFiltro.setForeground(Color.DARK_GRAY);
            btnFiltro.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 15, 8, 15)
            ));

            if (cat.equals("Todos")) btnFiltro.setSelected(true);

            btnFiltro.addActionListener(e -> {
                filtroCategoriaActual = cat;
                filtrarProductos(); 
            });

            bgFiltros.add(btnFiltro);
            panelFiltros.add(btnFiltro);
        }

        cabeceraCompleta.add(topBar);
        cabeceraCompleta.add(panelBuscador);
        cabeceraCompleta.add(panelFiltros); 

        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        contenedorPrincipal = new JPanel();
        contenedorPrincipal.setLayout(new BoxLayout(contenedorPrincipal, BoxLayout.Y_AXIS));
        contenedorPrincipal.setBackground(Color.WHITE);
        contenedorPrincipal.setBorder(new EmptyBorder(0, 10, 20, 10));

        JScrollPane scroll = new JScrollPane(contenedorPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16); 
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        areaCentro.add(scroll, BorderLayout.CENTER);
        add(areaCentro, BorderLayout.CENTER);

        // --- 3. RESUMEN DE PAGO ---
        
        JPanel panelResumen = new JPanel(new BorderLayout());
        panelResumen.setPreferredSize(new Dimension(320, 0)); 
        panelResumen.setBackground(Color.WHITE);
        panelResumen.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(230, 230, 230)));

        JPanel superiorResumen = new JPanel(new BorderLayout());
        superiorResumen.setBackground(Color.WHITE);
        
        JPanel panelAtencionContenedor = new JPanel(new BorderLayout(0, 10));
        panelAtencionContenedor.setBackground(Color.WHITE);
        panelAtencionContenedor.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTipoAtencion = new JLabel("Tipo de atención:");
        lblTipoAtencion.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JPanel panelBotonesAtencion = new JPanel(new GridLayout(1, 3, 5, 0));
        panelBotonesAtencion.setBackground(Color.WHITE);
        JToggleButton btnMesa = new JToggleButton("Mesa", true); 
        JToggleButton btnLlevar = new JToggleButton("Llevar");
        JToggleButton btnDelivery = new JToggleButton("Delivery");
        
        ButtonGroup bgAtencion = new ButtonGroup();
        bgAtencion.add(btnMesa); bgAtencion.add(btnLlevar); bgAtencion.add(btnDelivery);
        
        panelBotonesAtencion.add(btnMesa);
        panelBotonesAtencion.add(btnLlevar);
        panelBotonesAtencion.add(btnDelivery);

        cardLayoutAtencion = new CardLayout();
        panelTarjetasAtencion = new JPanel(cardLayoutAtencion);
        panelTarjetasAtencion.setBackground(Color.WHITE);

        // 1. Tarjeta: MESA
        JPanel cardMesa = new JPanel(new GridLayout(4, 1, 0, 2));
        cardMesa.setBackground(Color.WHITE);
        cardMesa.add(new JLabel("DNI Cliente (Opcional):"));
        txtCliente = new JTextField();
        txtCliente.putClientProperty("JTextField.placeholderText", "DNI (8 dígitos)...");

        ((AbstractDocument) txtCliente.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if ((fb.getDocument().getLength() + string.length()) <= 8 && string.matches("\\d+")) {
                    super.insertString(fb, offset, string, attr);
                }
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if ((fb.getDocument().getLength() + text.length() - length) <= 8 && text.matches("\\d+")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
        cardMesa.add(txtCliente);
        
        cardMesa.add(new JLabel("Mesa:"));
        txtMesa = new JTextField();
        txtMesa.putClientProperty("JTextField.placeholderText", "Ej. 05");
        
        ((AbstractDocument) txtMesa.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text != null && text.matches("\\d+")) super.replace(fb, offset, length, text, attrs);
            }
        });
        cardMesa.add(txtMesa);

        // 2. Tarjeta: PARA LLEVAR
        JPanel cardLlevar = new JPanel(new GridLayout(4, 1, 0, 2));
        cardLlevar.setBackground(Color.WHITE);
        cardLlevar.add(new JLabel("DNI/Nombre Cliente:"));
        txtLlevarNombre = new JTextField();
        txtLlevarNombre.putClientProperty("JTextField.placeholderText", "Nombre de quien recoge...");
        cardLlevar.add(txtLlevarNombre);
        cardLlevar.add(new JLabel("Hora de recojo (Opcional):"));
        cardLlevar.add(new JTextField()); 

        // 3. Tarjeta: DELIVERY
        JPanel cardDelivery = new JPanel(new GridLayout(4, 1, 0, 2));
        cardDelivery.setBackground(Color.WHITE);
        cardDelivery.add(new JLabel("Teléfono / DNI:"));
        cardDelivery.add(new JTextField()); 
        cardDelivery.add(new JLabel("Dirección exacta:"));
        txtDeliveryDir = new JTextField();
        txtDeliveryDir.putClientProperty("JTextField.placeholderText", "Av/Calle...");
        cardDelivery.add(txtDeliveryDir);

        panelTarjetasAtencion.add(cardMesa, "MESA");
        panelTarjetasAtencion.add(cardLlevar, "LLEVAR");
        panelTarjetasAtencion.add(cardDelivery, "DELIVERY");

        btnMesa.addActionListener(e -> { cardLayoutAtencion.show(panelTarjetasAtencion, "MESA"); tipoAtencionActual = "MESA"; });
        btnLlevar.addActionListener(e -> { cardLayoutAtencion.show(panelTarjetasAtencion, "LLEVAR"); tipoAtencionActual = "LLEVAR"; });
        btnDelivery.addActionListener(e -> { cardLayoutAtencion.show(panelTarjetasAtencion, "DELIVERY"); tipoAtencionActual = "DELIVERY"; });

        panelAtencionContenedor.add(lblTipoAtencion, BorderLayout.NORTH);
        panelAtencionContenedor.add(panelBotonesAtencion, BorderLayout.CENTER);
        panelAtencionContenedor.add(panelTarjetasAtencion, BorderLayout.SOUTH);

        JLabel titleRes = new JLabel("Pedido actual");
        titleRes.setBorder(new EmptyBorder(10, 10, 5, 10));
        titleRes.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JPanel headerResumen = new JPanel(new BorderLayout());
        headerResumen.setBackground(Color.WHITE);
        headerResumen.add(panelAtencionContenedor, BorderLayout.NORTH);
        headerResumen.add(titleRes, BorderLayout.SOUTH);

        panelCarritoContenedor = new JPanel();
        panelCarritoContenedor.setLayout(new BoxLayout(panelCarritoContenedor, BoxLayout.Y_AXIS));
        panelCarritoContenedor.setBackground(new Color(248, 249, 251));
        
        JScrollPane scrollCart = new JScrollPane(panelCarritoContenedor);
        scrollCart.setBorder(new EmptyBorder(5, 10, 10, 10));
        scrollCart.getVerticalScrollBar().setUnitIncrement(16);

        superiorResumen.add(headerResumen, BorderLayout.NORTH);
        superiorResumen.add(scrollCart, BorderLayout.CENTER);

        // --- SECCIÓN DESGLOSE DE PAGO CON DESCUENTO ---
        JPanel inferiorResumen = new JPanel(new BorderLayout(0, 10)); 
        inferiorResumen.setBackground(Color.WHITE);
        inferiorResumen.setBorder(new EmptyBorder(10, 10, 15, 10));

        JPanel panelDesglose = new JPanel(new GridLayout(4, 2, 5, 5));
        panelDesglose.setBackground(Color.WHITE);
        panelDesglose.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDE)); 
        
        lblMontoSubtotal = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblMontoDescuento = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblMontoDescuento.setForeground(ROJO); // Descuento en rojito para que destaque
        lblMontoIGV = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        
        lblTotalPagar = new JLabel("S/ 0.00", SwingConstants.RIGHT);
        lblTotalPagar.setFont(new Font("Segoe UI", Font.BOLD, 19));
        lblTotalPagar.setForeground(new Color(26, 79, 156));

        // BOTÓN INTERACTIVO PARA DESCUENTO
        btnAplicarDescuento = new JButton("Descuento:");
        btnAplicarDescuento.setHorizontalAlignment(SwingConstants.LEFT);
        btnAplicarDescuento.setBorderPainted(false);
        btnAplicarDescuento.setContentAreaFilled(false);
        btnAplicarDescuento.setForeground(AZUL);
        btnAplicarDescuento.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAplicarDescuento.setMargin(new Insets(0,0,0,0));
        btnAplicarDescuento.setEnabled(false); // Se habilita solo si hay productos
        
        btnAplicarDescuento.addActionListener(e -> abrirDialogoDescuento());

        JLabel lblTxtTotal = new JLabel("TOTAL:");
        lblTxtTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));

        panelDesglose.add(new JLabel("Subtotal:")); panelDesglose.add(lblMontoSubtotal);
        panelDesglose.add(btnAplicarDescuento); panelDesglose.add(lblMontoDescuento);
        panelDesglose.add(new JLabel("IGV (18%):")); panelDesglose.add(lblMontoIGV);
        panelDesglose.add(lblTxtTotal); panelDesglose.add(lblTotalPagar);

        JPanel panelAcciones = new JPanel(new GridLayout(2, 1, 0, 5));
        panelAcciones.setBackground(Color.WHITE);
        
        btnPagar = new JButton("Cobrar (F9)");
        btnPagar.setEnabled(false); 
        btnPagar.setPreferredSize(new Dimension(0, 45)); 
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        
        btnVaciar = new JButton("Vaciar pedido");
        btnVaciar.setForeground(Color.GRAY);
        btnVaciar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnVaciar.addActionListener(e -> vaciarTodo());

        panelAcciones.add(btnPagar);
        panelAcciones.add(btnVaciar);

        inferiorResumen.add(panelDesglose, BorderLayout.CENTER);
        inferiorResumen.add(panelAcciones, BorderLayout.SOUTH);

        btnPagar.addActionListener(e -> {
            if (totalAcumulado > 0) {
                if (tipoAtencionActual.equals("MESA")) {
                    String dni = txtCliente.getText().trim();
                    String mesa = txtMesa.getText().trim();

                    if (mesa.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Debe asignar un número de mesa.", "Validación Requerida", JOptionPane.WARNING_MESSAGE);
                        txtMesa.requestFocus();
                        return;
                    }

                    Cliente cliente = null;
                    if (!dni.isEmpty()) {
                        if (dni.length() != 8) {
                            JOptionPane.showMessageDialog(this, "Si ingresa un DNI, debe tener exactamente 8 dígitos.", "Validación", JOptionPane.WARNING_MESSAGE);
                            txtCliente.requestFocus();
                            return;
                        }
                        
                        cliente = buscarClienteDNI(dni);
                        if(cliente == null){
                            JOptionPane.showMessageDialog(this, "El DNI ingresado no pertenece a ningún cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
                            txtCliente.requestFocus();
                            return; 
                        }
                    }
                    
                    double totalFinal = totalAcumulado;
                    if (reglaDescuentoPorcentaje > 0) totalFinal = totalAcumulado - (totalAcumulado * (reglaDescuentoPorcentaje / 100.0));
                    else if (reglaDescuentoFijo > 0) totalFinal = totalAcumulado - reglaDescuentoFijo;
                    if(totalFinal < 0) totalFinal = 0; 

                    Pago_GUI pago = new Pago_GUI(this, totalFinal, cliente, platos_seleccionados, new Mesa(Integer.parseInt(mesa),0,0,0), conexion);
                    pago.setVisible(true);
                } 
                else if (tipoAtencionActual.equals("LLEVAR")) {
                    JOptionPane.showMessageDialog(this, "Procesando pedido para LLEVAR...");
                }
                else if (tipoAtencionActual.equals("DELIVERY")) {
                    JOptionPane.showMessageDialog(this, "Procesando pedido para DELIVERY...");
                }
            }
        });

        panelResumen.add(superiorResumen, BorderLayout.CENTER);
        panelResumen.add(inferiorResumen, BorderLayout.SOUTH);
        add(panelResumen, BorderLayout.EAST);
    }

    // =========================================================================
    // --- LÓGICA DE INTERFAZ DEL DESCUENTO SEGURO ---
    // =========================================================================
    private void abrirDialogoDescuento() {
        JDialog dialog = new JDialog(this, "Aplicar Descuento", true);
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel panelCentral = new JPanel(new GridLayout(2, 2, 10, 10));
        panelCentral.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"Porcentaje (%)", "Monto Fijo (S/)"});
        JTextField txtValor = new JTextField();
        
        ((AbstractDocument) txtValor.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                String newStr = fb.getDocument().getText(0, fb.getDocument().getLength()) + string;
                if (newStr.matches("\\d*\\.?\\d*")) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String newStr = currentText.substring(0, offset) + text + currentText.substring(offset + length);
                if (newStr.matches("\\d*\\.?\\d*")) super.replace(fb, offset, length, text, attrs);
            }
        });

        if (reglaDescuentoPorcentaje > 0) {
            cmbTipo.setSelectedIndex(0);
            txtValor.setText(String.valueOf(reglaDescuentoPorcentaje));
        } else if (reglaDescuentoFijo > 0) {
            cmbTipo.setSelectedIndex(1);
            txtValor.setText(String.valueOf(reglaDescuentoFijo));
        }

        panelCentral.add(new JLabel("Tipo de descuento:"));
        panelCentral.add(cmbTipo);
        panelCentral.add(new JLabel("Valor:"));
        panelCentral.add(txtValor);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnQuitar = new JButton("Quitar");
        JButton btnAplicar = new JButton("Aplicar");
        
        btnAplicar.setBackground(AZUL);
        btnAplicar.setForeground(Color.WHITE);

        btnQuitar.addActionListener(e -> {
            reglaDescuentoPorcentaje = 0.0;
            reglaDescuentoFijo = 0.0;
            actualizarTotal();
            dialog.dispose();
        });

        btnAplicar.addActionListener(e -> {
            if (txtValor.getText().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Ingrese un valor.");
                return;
            }
            
            double valorIngresado = Double.parseDouble(txtValor.getText());
            
            if (cmbTipo.getSelectedIndex() == 0) { // Porcentaje
                if (valorIngresado <= 0 || valorIngresado > 100) {
                    JOptionPane.showMessageDialog(dialog, "El porcentaje debe estar entre 1 y 100.");
                    return;
                }
                reglaDescuentoPorcentaje = valorIngresado;
                reglaDescuentoFijo = 0.0;
            } else { // Monto Fijo
                if (valorIngresado <= 0 || valorIngresado > totalAcumulado) {
                    JOptionPane.showMessageDialog(dialog, "El descuento no puede ser mayor al total actual (S/ " + totalAcumulado + ").");
                    return;
                }
                reglaDescuentoFijo = valorIngresado;
                reglaDescuentoPorcentaje = 0.0;
            }
            
            actualizarTotal();
            dialog.dispose();
        });

        panelBotones.add(btnQuitar);
        panelBotones.add(btnAplicar);
        
        dialog.add(panelCentral, BorderLayout.CENTER);
        dialog.add(panelBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private Cliente buscarClienteDNI(String dni){
        ClienteDAO cliente_dao = new ClienteDAOImpl(conexion);
        return cliente_dao.obtenerPorDni(dni);
    }
    
    // --- FILTRADO CORREGIDO ---
    private void filtrarProductos() {
        String textoBuscado = txtBuscar.getText().toLowerCase().trim();
        contenedorPrincipal.removeAll();
        
        ArrayList<Plato> todosLosPlatos = obtenerPlatosBD();
        
        String[] categoriasIterar;
        if (filtroCategoriaActual.equals("Todos")) {
            categoriasIterar = new String[]{"Platos principales", "Entradas", "Bebidas", "Postres"};
        } else {
            categoriasIterar = new String[]{filtroCategoriaActual};
        }

        boolean encontroAlgunPlato = false;

        for (String catActual : categoriasIterar) {
            ArrayList<Plato> platosDeEstaCategoria = new ArrayList<>();
            
            for (Plato p : todosLosPlatos) {
                String nombreCatPlato = "";
                if (p.getCategoria() != null && p.getCategoria().getNombre() != null) {
                    nombreCatPlato = p.getCategoria().getNombre().toLowerCase();
                }

                boolean coincideTexto = p.getNombre().toLowerCase().contains(textoBuscado);
                
                // LÓGICA DE CATEGORÍAS CORREGIDA
                boolean coincideCategoria = false;
                if (catActual.equals("Todos")) {
                    coincideCategoria = true;
                } else if (catActual.equalsIgnoreCase("Platos principales")) {
                    coincideCategoria = nombreCatPlato.contains("principal") || nombreCatPlato.contains("fondo");
                } else {
                    coincideCategoria = nombreCatPlato.contains(catActual.toLowerCase());
                }

                if (coincideTexto && coincideCategoria) {
                    platosDeEstaCategoria.add(p);
                }
            }

            if (!platosDeEstaCategoria.isEmpty()) {
                agregarSeccion(contenedorPrincipal, catActual, platosDeEstaCategoria);
                encontroAlgunPlato = true;
            }
        }

        if (!encontroAlgunPlato) {
            JLabel lblVacio = new JLabel("No se encontraron productos para esta búsqueda o categoría.");
            lblVacio.setBorder(new EmptyBorder(20, 20, 20, 20));
            lblVacio.setForeground(Color.GRAY);
            lblVacio.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            contenedorPrincipal.add(lblVacio);
        }

        contenedorPrincipal.revalidate();
        contenedorPrincipal.repaint();
    }

    private void agregarSeccion(JPanel contenedor, String titulo, ArrayList<Plato> platos) {
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(15, 0, 10, 0)); 
        contenedor.add(lbl);

        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 15)); 
        grid.setBackground(Color.WHITE);
        System.out.println(titulo);
        
        for (Plato p : platos) {
            JPanel cardWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            cardWrapper.setBackground(Color.WHITE);
            cardWrapper.add(crearCard(p)); 
            grid.add(cardWrapper);
        }
        
        JPanel gridWrapper = new JPanel(new BorderLayout());
        gridWrapper.setBackground(Color.WHITE);
        gridWrapper.add(grid, BorderLayout.NORTH);
        contenedor.add(gridWrapper);
    }

    private JPanel crearCard(Plato plato) {
        String disponibilidad;
        JPanel card = new JPanel(new BorderLayout());
        Dimension fixedSize = new Dimension(150, 240);
        card.setPreferredSize(fixedSize);
        card.setMinimumSize(fixedSize);
        card.setMaximumSize(fixedSize);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        System.out.println(getClass().getResource("/img/PiscoSour.png"));
        JLabel lblImg = new JLabel("", SwingConstants.CENTER);

        lblImg.setPreferredSize(new Dimension(150, 120));
        lblImg.setOpaque(true);
        lblImg.setBackground(new Color(245, 245, 245));

        try {

            // plato.getImagen() debe tener la ruta completa
            // Ejemplo:
            // C:\Users\Usuario\Downloads\POS_imagenes\pisco.png

            String rutaImagen = plato.getImagen();

            System.out.println(rutaImagen);

            File archivo = new File(rutaImagen);

            if (archivo.exists()) {

                ImageIcon iconOriginal = new ImageIcon(rutaImagen);

                Image imagenEscalada =
                        iconOriginal.getImage().getScaledInstance(
                                140,
                                115,
                                Image.SCALE_SMOOTH
                        );
                lblImg.setIcon(new ImageIcon(imagenEscalada));
                    
                }
        } catch (Exception e) {

            e.printStackTrace();

        }

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(8, 10, 8, 10));
        info.setBackground(Color.WHITE);

        JLabel n = new JLabel(plato.getNombre());
        n.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        // --- FORMATO DE MONEDA CORREGIDO ---
        JLabel p = new JLabel("S/ " + String.format(java.util.Locale.US, "%.2f", plato.getPrecio()));
        p.setForeground(AZUL);
        p.setFont(new Font("Segoe UI", Font.BOLD, 13));

        if(plato.getDisponible() == 1){disponibilidad = "DISPONIBLE";}else{disponibilidad = "AGOTADO";}
        
        JLabel s = new JLabel(disponibilidad);
        s.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JButton btnAdd = new JButton("+");
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (disponibilidad.equalsIgnoreCase("DISPONIBLE")) {
            s.setForeground(VERDE);
            btnAdd.setBackground(AZUL);
            btnAdd.setForeground(Color.WHITE);
            btnAdd.setEnabled(true);
        } else {
            s.setForeground(ROJO);
            btnAdd.setBackground(Color.LIGHT_GRAY);
            btnAdd.setForeground(Color.DARK_GRAY);
            btnAdd.setEnabled(false); 
        }

        info.add(n);
        info.add(Box.createVerticalStrut(3));
        info.add(p);
        info.add(Box.createVerticalStrut(3));
        info.add(s);

        btnAdd.addActionListener(e -> agregarAlCarrito(plato));
        
        card.add(lblImg, BorderLayout.NORTH);
        card.add(info, BorderLayout.CENTER);
        card.add(btnAdd, BorderLayout.SOUTH);
        return card;
    }

    private void agregarAlCarrito(Plato plato) {
        if (mapaCarrito.containsKey(plato.getNombre())) {
            ItemCarritoUI item = mapaCarrito.get(plato.getNombre());
            item.incrementar(); 
            // item.incrementar() ya se encarga de agregar a platos_seleccionados y sumar totalAcumulado
        } else {
            ItemCarritoUI nuevoItem = new ItemCarritoUI(plato);
            mapaCarrito.put(plato.getNombre(), nuevoItem);
            panelCarritoContenedor.add(nuevoItem.getPanelPrincipal());
            
            platos_seleccionados.add(plato);
            totalAcumulado += plato.getPrecio();
        }
        
        actualizarTotal();
        panelCarritoContenedor.revalidate();
        panelCarritoContenedor.repaint();
    }
    
    private class ItemCarritoUI {
        private Plato plato;
        private int cantidad;
        private JPanel panelPrincipal;
        private JLabel lblCantidad;
        private JLabel lblSubtotalItem;

        public ItemCarritoUI(Plato plato) {
            this.plato = plato;
            this.cantidad = 1;
            construirPanel();
        }

        private void construirPanel() {
            panelPrincipal = new JPanel(new BorderLayout());
            panelPrincipal.setBackground(Color.WHITE);
            panelPrincipal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
            panelPrincipal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
                new EmptyBorder(5, 5, 5, 5)
            ));

            JLabel lblNombre = new JLabel("<html><b>" + plato.getNombre() + "</b></html>");
            lblNombre.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            panelPrincipal.add(lblNombre, BorderLayout.NORTH);

            JPanel panelInferior = new JPanel(new BorderLayout());
            panelInferior.setBackground(Color.WHITE);

            JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            panelControles.setBackground(Color.WHITE);
            
            JButton btnMenos = crearBotonCantidad("-");
            lblCantidad = new JLabel(String.valueOf(cantidad));
            lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblCantidad.setPreferredSize(new Dimension(20, 20));
            lblCantidad.setHorizontalAlignment(SwingConstants.CENTER);
            JButton btnMas = crearBotonCantidad("+");

            panelControles.add(btnMenos);
            panelControles.add(lblCantidad);
            panelControles.add(btnMas);

            JPanel panelDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            panelDerecha.setBackground(Color.WHITE);

            lblSubtotalItem = new JLabel(String.format(java.util.Locale.US, "S/ %.2f", plato.getPrecio() * cantidad));
            lblSubtotalItem.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblSubtotalItem.setForeground(AZUL);

            JButton btnRemove = new JButton("X");
            btnRemove.setForeground(ROJO);
            btnRemove.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnRemove.setContentAreaFilled(false);
            btnRemove.setBorderPainted(false);
            btnRemove.setMargin(new Insets(0, 0, 0, 0));
            btnRemove.setFont(new Font("Segoe UI", Font.BOLD, 14));

            panelDerecha.add(lblSubtotalItem);
            panelDerecha.add(btnRemove);

            panelInferior.add(panelControles, BorderLayout.WEST);
            panelInferior.add(panelDerecha, BorderLayout.EAST);
            panelPrincipal.add(panelInferior, BorderLayout.CENTER);

            btnMas.addActionListener(e -> incrementar());
            btnMenos.addActionListener(e -> decrementar());
            btnRemove.addActionListener(e -> {
                panelCarritoContenedor.remove(panelPrincipal);
                mapaCarrito.remove(plato.getNombre());
                platos_seleccionados.removeIf(p -> p.getNombre().equals(plato.getNombre()));
                totalAcumulado -= (plato.getPrecio() * cantidad);
                actualizarTotal();
                panelCarritoContenedor.revalidate();
                panelCarritoContenedor.repaint();
            });
        }

        public void incrementar() {
            cantidad++;
            platos_seleccionados.add(plato);
            totalAcumulado += plato.getPrecio();
            actualizarUI();
            actualizarTotal();
        }

        public void decrementar() {
            if (cantidad > 1) {
                cantidad--;
                platos_seleccionados.remove(plato); 
                totalAcumulado -= plato.getPrecio();
                actualizarUI();
                actualizarTotal();
            }
        }

        private void actualizarUI() {
            lblCantidad.setText(String.valueOf(cantidad));
            lblSubtotalItem.setText(String.format(java.util.Locale.US, "S/ %.2f", plato.getPrecio() * cantidad));
        }

        private JButton crearBotonCantidad(String texto) {
            JButton btn = new JButton(texto);
            btn.setPreferredSize(new Dimension(22, 22));
            btn.setMargin(new Insets(0, 0, 0, 0));
            btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setBackground(new Color(240, 240, 240));
            btn.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
            return btn;
        }

        public JPanel getPanelPrincipal() {
            return panelPrincipal;
        }
    }

    // =========================================================================
    // --- LÓGICA DE MATEMÁTICA Y ACTUALIZACIÓN ---
    // =========================================================================
    private void actualizarTotal() {
        if (totalAcumulado < 0.01) {
            totalAcumulado = 0.0;
            reglaDescuentoPorcentaje = 0.0; 
            reglaDescuentoFijo = 0.0;
        } 
        
        double montoDescuento = 0.0;
        if (reglaDescuentoPorcentaje > 0) {
            montoDescuento = totalAcumulado * (reglaDescuentoPorcentaje / 100.0);
        } else if (reglaDescuentoFijo > 0) {
            montoDescuento = reglaDescuentoFijo;
        }

        if (montoDescuento > totalAcumulado) {
            montoDescuento = totalAcumulado; 
        }

        double totalAPagar = totalAcumulado - montoDescuento;

        double igvTasa = 0.18;
        double baseImponible = totalAPagar / (1 + igvTasa);
        double igv = totalAPagar - baseImponible;
        
        lblMontoSubtotal.setText(String.format(java.util.Locale.US, "S/ %.2f", baseImponible));
        
        if (montoDescuento > 0) {
            lblMontoDescuento.setText(String.format(java.util.Locale.US, "- S/ %.2f", montoDescuento));
        } else {
            lblMontoDescuento.setText("S/ 0.00");
        }
        
        lblMontoIGV.setText(String.format(java.util.Locale.US, "S/ %.2f", igv));
        lblTotalPagar.setText(String.format(java.util.Locale.US, "S/ %.2f", totalAPagar));
        
        if (totalAcumulado > 0) {
            btnPagar.setEnabled(true);
            btnPagar.setBackground(AZUL);
            btnPagar.setForeground(Color.WHITE);
            btnVaciar.setForeground(ROJO);
            btnAplicarDescuento.setEnabled(true);
        } else {
            btnPagar.setEnabled(false);
            btnPagar.setBackground(Color.LIGHT_GRAY);
            btnPagar.setForeground(Color.DARK_GRAY);
            btnVaciar.setForeground(Color.GRAY);
            btnAplicarDescuento.setEnabled(false);
        }
    }

    public void vaciarTodo() {
        panelCarritoContenedor.removeAll();
        platos_seleccionados.clear(); 
        mapaCarrito.clear(); 
        totalAcumulado = 0.0;
        reglaDescuentoPorcentaje = 0.0; 
        reglaDescuentoFijo = 0.0;       
        
        actualizarTotal();
        
        if(txtCliente != null) txtCliente.setText("");
        if(txtMesa != null) txtMesa.setText("");
        if(txtLlevarNombre != null) txtLlevarNombre.setText("");
        if(txtDeliveryDir != null) txtDeliveryDir.setText("");
        
        panelCarritoContenedor.revalidate();
        panelCarritoContenedor.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Caja_GUI().setVisible(true));
    }
}