package proyecto.pos.gui;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import javax.swing.*;
import javax.swing.border.*;

public class MenuSidebar extends JPanel {

    private static final Color AZUL       = new Color(26, 83, 160);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color SIDEBAR    = new Color(250, 251, 253);
    private static final Color BORDE      = new Color(225, 229, 236);
    private static final Color TEXTO_SUAVE= new Color(105, 113, 128);
    private static final Color ROJO       = new Color(220, 53, 69);
    private static final Color VERDE      = new Color(40, 167, 69);
    private static final Color NARANJA    = new Color(255, 159, 64);

    private JFrame parentFrame;
    private String ventanaActiva;
    private Connection conexion;
    
    // Frames para Sub-Recetas y Producción
    private SubRecetaFrame subRecetaFrame;
    private ProduccionFrame produccionFrame;

    public MenuSidebar(JFrame parentFrame, String ventanaActiva, Connection conexion) {
        this.parentFrame    = parentFrame;
        this.ventanaActiva  = ventanaActiva;
        this.conexion       = conexion;
        initComponents();
    }

    private void initComponents() {
        setPreferredSize(new Dimension(220, 0));
        setBackground(SIDEBAR);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDE));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(crearHeaderSidebar());
        add(crearLinea());
        add(Box.createVerticalStrut(34));

        // Botones del menú original
        JButton btnCajero    = crearBotonMenu("Cajero",             "/img/carrito.png",       ventanaActiva.equals("Cajero"));
        JButton btnStock     = crearBotonMenu("Artículos y Stock",  "/img/stock.png",         ventanaActiva.equals("Stock"));
        JButton btnHistorial = crearBotonMenu("Historial de Trans.","/img/Historial.png",     ventanaActiva.equals("Historial"));
        JButton btnReportes  = crearBotonMenu("Reportes",           "/img/Reporte.png",       ventanaActiva.equals("Reportes"));
        JButton btnGastos    = crearBotonMenu("Gastos",             "/img/billetera.png",     ventanaActiva.equals("Gastos"));
        JButton btnClientes  = crearBotonMenu("Clientes",           "/img/clientes.png",      ventanaActiva.equals("Clientes"));
        JButton btnEmpleados = crearBotonMenu("Empleados",          "/img/empleados.png",     ventanaActiva.equals("Empleados"));
        JButton btnConfig    = crearBotonMenu("Configuración",      "/img/configuracion.png", ventanaActiva.equals("Configuracion"));

        // NUEVOS BOTONES - SUB-RECETAS Y PRODUCCIÓN
        JButton btnSubRecetas = crearBotonMenu("📋 Sub-Recetas",    null,                     ventanaActiva.equals("SubRecetas"));
        JButton btnProduccion = crearBotonMenu("⚙️ Producción",      null,                     ventanaActiva.equals("Produccion"));

        // Acciones botones originales
        btnCajero.addActionListener(e -> {
            try {
                navegar(new Caja_GUI());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnStock.addActionListener(e -> {
            try {
                navegar(new ArticulosStockFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnHistorial.addActionListener(e -> {
            try {
                navegar(new HistorialTransaccionesFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnReportes.addActionListener(e -> {
            try {
                navegar(new ReportesFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnGastos.addActionListener(e -> JOptionPane.showMessageDialog(parentFrame, "Módulo de gastos pendiente de conectar."));
        
        btnClientes.addActionListener(e -> {
            try {
                navegar(new ClientesFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnEmpleados.addActionListener(e -> {
            try {
                navegar(new EmpleadosFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });
        
        btnConfig.addActionListener(e -> {
            try {
                navegar(new ConfiguracionFrame());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parentFrame, "Error: " + ex.getMessage());
            }
        });

        // ACCIONES NUEVOS BOTONES
        btnSubRecetas.addActionListener(e -> abrirSubRecetas());
        btnProduccion.addActionListener(e -> abrirProduccion());

        // Agregar botones al panel
        agregarMenu(this, btnCajero, btnStock, btnHistorial, btnReportes,
                    btnGastos, btnClientes, btnEmpleados, btnConfig);
        
        // Separador antes de nuevas opciones
        add(Box.createVerticalStrut(14));
        add(crearLinea());
        add(Box.createVerticalStrut(14));
        
        JLabel lblProduccion = new JLabel("PRODUCCIÓN");
        lblProduccion.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProduccion.setForeground(TEXTO_SUAVE);
        lblProduccion.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblProduccion);
        add(Box.createVerticalStrut(7));
        
        // Agregar nuevos botones
        agregarMenu(this, btnSubRecetas, btnProduccion);

        add(Box.createVerticalGlue());
        add(crearLinea());
        add(Box.createVerticalStrut(12));

        JButton btnSalir = crearBotonMenu("Salir", "/img/Salir.png", false);
        btnSalir.setForeground(ROJO);
        btnSalir.addActionListener(e -> System.exit(0));

        agregarMenu(this, btnSalir);
        add(Box.createVerticalStrut(18));
    }

    /**
     * Abre SubRecetaFrame (crea una nueva instancia o muestra la existente)
     */
    private void abrirSubRecetas() {
        try {
            if (conexion == null) {
                JOptionPane.showMessageDialog(parentFrame, "❌ Error: No hay conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (subRecetaFrame == null || !subRecetaFrame.isVisible()) {
                subRecetaFrame = new SubRecetaFrame(conexion);
            } else {
                subRecetaFrame.setVisible(true);
                subRecetaFrame.toFront();
                subRecetaFrame.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "❌ Error al abrir Sub-Recetas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    /**
     * Abre ProduccionFrame (crea una nueva instancia o muestra la existente)
     */
    private void abrirProduccion() {
        try {
            if (conexion == null) {
                JOptionPane.showMessageDialog(parentFrame, "❌ Error: No hay conexión a la base de datos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (produccionFrame == null || !produccionFrame.isVisible()) {
                produccionFrame = new ProduccionFrame(conexion);
            } else {
                produccionFrame.setVisible(true);
                produccionFrame.toFront();
                produccionFrame.requestFocus();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(parentFrame, "❌ Error al abrir Producción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void navegar(JFrame nuevaVentana) {
        nuevaVentana.setVisible(true);
        parentFrame.dispose();
    }

    private JPanel crearHeaderSidebar() {
        JPanel header = new JPanel(new BorderLayout(8, 0));
        header.setBackground(SIDEBAR);
        header.setBorder(new EmptyBorder(16, 16, 16, 14));
        header.setMaximumSize(new Dimension(220, 78));

        JPanel marca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        marca.setOpaque(false);

        JLabel logo = new JLabel(redimensionarIcono("/img/carroBlanco.png", 22, 22));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setOpaque(true);
        logo.setBackground(AZUL);
        logo.setPreferredSize(new Dimension(40, 40));
        logo.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel lblPos = new JLabel("Pos");
        lblPos.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblPos.setForeground(AZUL);

        JLabel lblDesc = new JLabel("Sistema de Caja");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(TEXTO_SUAVE);

        textos.add(lblPos);
        textos.add(lblDesc);

        marca.add(logo);
        marca.add(textos);
        header.add(marca, BorderLayout.CENTER);

        return header;
    }

    private JPanel crearLinea() {
        JPanel linea = new JPanel();
        linea.setMaximumSize(new Dimension(220, 1));
        linea.setPreferredSize(new Dimension(220, 1));
        linea.setBackground(new Color(232, 235, 241));
        return linea;
    }

    private JButton crearBotonMenu(String texto, String iconPath, boolean seleccionado) {
        JButton boton = new JButton(texto);
        if (iconPath != null && !iconPath.isEmpty()) {
            boton.setIcon(redimensionarIcono(iconPath, 18, 18));
            boton.setIconTextGap(13);
        }
        boton.setMaximumSize(new Dimension(190, 40));
        boton.setPreferredSize(new Dimension(190, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(new EmptyBorder(0, 14, 0, 10));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFont(new Font("Segoe UI", seleccionado ? Font.BOLD : Font.PLAIN, 14));

        if (seleccionado) {
            boton.setBackground(AZUL_CLARO);
            boton.setForeground(AZUL);
            boton.setBorder(new RoundedBorder(AZUL_CLARO, 12));
        } else {
            boton.setBackground(SIDEBAR);
            boton.setForeground(new Color(62, 70, 82));
            boton.setBorderPainted(false);
        }

        boton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (!seleccionado) boton.setBackground(new Color(240, 243, 248)); }
            public void mouseExited (MouseEvent e) { if (!seleccionado) boton.setBackground(SIDEBAR); }
        });

        return boton;
    }

    private void agregarMenu(JPanel panel, JButton... botones) {
        for (JButton boton : botones) {
            panel.add(boton);
            panel.add(Box.createVerticalStrut(7));
        }
    }

    public static ImageIcon redimensionarIcono(String path, int width, int height) {
        try {
            java.net.URL imgURL = MenuSidebar.class.getResource(path);
            if (imgURL != null) {
                Image img = new ImageIcon(imgURL).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + path);
        }
        return null;
    }

    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int   arc;
        
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
}
