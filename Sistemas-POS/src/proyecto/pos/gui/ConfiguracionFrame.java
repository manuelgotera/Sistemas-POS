package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class ConfiguracionFrame extends JFrame {

    // Componentes Perfil
    private JTextField txtNombreTienda, txtTelefono, txtWhatsapp, txtRuc;
    private JTextArea txtDireccion;
    
    // Componentes Impuestos/Recibo
    private JCheckBox chkIgv;
    private JTextField txtDescMax, txtFormatoNum, txtMargen;
    private JComboBox<String> cbRedondeo;

    public ConfiguracionFrame() {
        configurarVentana();
        initComponents();
    }

    private void configurarVentana() {
        FlatLightLaf.setup();
        setTitle("Sistema de Caja - Configuración");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setSize(1180, 720); 
        setMinimumSize(new Dimension(1000, 620)); 
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // --- 1. SIDEBAR (AHORA USANDO LA CLASE MenuSidebar) ---
        // Nota: Según tu clase MenuSidebar, la validación activa espera "Configuracion" (sin tilde)
        add(new MenuSidebar(this, "Configuracion"), BorderLayout.WEST);

        // --- 2. ÁREA CENTRAL ---
        JPanel areaCentro = new JPanel(new BorderLayout());
        areaCentro.setBackground(Color.WHITE);

        // CABECERA CENTRAL (Con el reloj)
        JPanel cabeceraCompleta = new JPanel();
        cabeceraCompleta.setLayout(new BoxLayout(cabeceraCompleta, BoxLayout.Y_AXIS));
        cabeceraCompleta.setBackground(Color.WHITE);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(new EmptyBorder(15, 40, 15, 40));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setLayout(new BoxLayout(panelTitulo, BoxLayout.Y_AXIS));
        panelTitulo.setBackground(Color.WHITE);

        JLabel lblPagina = new JLabel("Configuración");
        lblPagina.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblEmpresa = new JLabel("Ajustes del Sistema");
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblEmpresa.setForeground(Color.GRAY);

        panelTitulo.add(lblPagina);
        panelTitulo.add(lblEmpresa);
        topBar.add(panelTitulo, BorderLayout.WEST);

        JPanel panelPerfil = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        panelPerfil.setBackground(Color.WHITE);

        // Reloj en tiempo real
        JLabel lblHora = new JLabel();
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Timer timerReloj = new Timer(1000, e -> {
            lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + sdf.format(new Date()) + "</font></html>");
        });
        timerReloj.start();
        lblHora.setText("<html><b>Hora</b><br><font color='gray'>" + sdf.format(new Date()) + "</font></html>");

        JLabel lblUsuario = new JLabel("<html><b>Manuel Gotera</b><br><font color='gray'>Administrador</font></html>");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        // Se usa MenuSidebar para redimensionar el avatar
        JLabel lblAvatar = new JLabel(MenuSidebar.redimensionarIcono("/img/perfilPedro.jpg", 40, 40));
        lblAvatar.setPreferredSize(new Dimension(40, 40));
        lblAvatar.setBackground(Color.LIGHT_GRAY);
        lblAvatar.setOpaque(true);

        panelPerfil.add(lblHora);
        panelPerfil.add(lblUsuario);
        panelPerfil.add(lblAvatar);
        topBar.add(panelPerfil, BorderLayout.EAST);

        cabeceraCompleta.add(topBar);
        
        JPanel divisorTopHeader = new JPanel();
        divisorTopHeader.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
        divisorTopHeader.setBackground(new Color(230, 230, 230));
        cabeceraCompleta.add(divisorTopHeader);

        areaCentro.add(cabeceraCompleta, BorderLayout.NORTH);

        // CONTENIDO PRINCIPAL DE CONFIGURACIÓN (TABS)
        JPanel panelContenido = new JPanel(new BorderLayout());
        panelContenido.setBackground(new Color(248, 249, 251));
        panelContenido.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        panelContenido.add(crearTabs(), BorderLayout.CENTER);
        
        areaCentro.add(panelContenido, BorderLayout.CENTER);
        add(areaCentro, BorderLayout.CENTER);
    }

    // --- MÉTODOS DE CONTENIDO DE CONFIGURACIÓN ---

    private JTabbedPane crearTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabs.setBackground(Color.WHITE);
        
        tabs.addTab("Perfil de Tienda", crearPerfil());
        tabs.addTab("Impuestos y Recibo", crearImpuestos());
        tabs.addTab("Usuarios", crearSeccionUsuarios());
        tabs.addTab("Datos", crearSeccionDatos());
        return tabs;
    }

    private JPanel crearPerfil() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel titulo = new JLabel("Información de la tienda");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(26, 79, 156));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;
        gbc.gridy++; panel.add(new JLabel("Nombre de la tienda *"), gbc);
        gbc.gridy++; 
        txtNombreTienda = new JTextField(""); 
        txtNombreTienda.setPreferredSize(new Dimension(0, 35));
        panel.add(txtNombreTienda, gbc);

        gbc.gridy++; panel.add(new JLabel("Dirección completa *"), gbc);
        gbc.gridy++; 
        txtDireccion = new JTextArea(4, 20);
        txtDireccion.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        panel.add(new JScrollPane(txtDireccion), gbc);

        gbc.gridy++; gbc.gridx = 0; panel.add(new JLabel("Teléfono *"), gbc);
        gbc.gridx = 1; panel.add(new JLabel("WhatsApp *"), gbc);

        gbc.gridy++; gbc.gridx = 0; 
        txtTelefono = new JTextField("+51");
        txtTelefono.setPreferredSize(new Dimension(0, 35));
        configurarSoloNumeros(txtTelefono);
        panel.add(txtTelefono, gbc);

        gbc.gridx = 1; 
        txtWhatsapp = new JTextField("+51");
        txtWhatsapp.setPreferredSize(new Dimension(0, 35));
        configurarSoloNumeros(txtWhatsapp);
        panel.add(txtWhatsapp, gbc);

        gbc.gridy++; gbc.gridx = 0; gbc.gridwidth = 2; panel.add(new JLabel("RUC (opcional)"), gbc);
        gbc.gridy++; 
        txtRuc = new JTextField("");
        txtRuc.setPreferredSize(new Dimension(0, 35));
        configurarSoloNumeros(txtRuc);
        panel.add(txtRuc, gbc);

        gbc.gridy++; panel.add(new JLabel("Logo (opcional)"), gbc);
        gbc.gridy++; 
        JButton btnSubir = new JButton("Subir imagen (JPG, PNG, SVG)");
        btnSubir.setPreferredSize(new Dimension(0, 35));
        btnSubir.addActionListener(e -> seleccionarImagen());
        panel.add(btnSubir, gbc);

        gbc.gridy++; 
        JButton guardar = new JButton("Guardar cambios");
        guardar.setBackground(new Color(26, 79, 156));
        guardar.setForeground(Color.WHITE);
        guardar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        guardar.setPreferredSize(new Dimension(0, 40));
        panel.add(guardar, gbc);

        contenedor.add(panel, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearImpuestos() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        JLabel t1 = new JLabel("Configuración de Impuestos");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t1.setForeground(new Color(26, 79, 156));
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(t1, gbc);

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("IGV 18%"), gbc);
        gbc.gridx = 1; 
        chkIgv = new JCheckBox("Activado"); 
        chkIgv.setBackground(Color.WHITE);
        panel.add(chkIgv, gbc);
        
        y++; gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel("Descuento máximo (%)"), gbc);
        gbc.gridy = ++y; 
        txtDescMax = new JTextField("0");
        txtDescMax.setPreferredSize(new Dimension(0, 35));
        configurarSoloNumeros(txtDescMax);
        panel.add(txtDescMax, gbc);

        y++; JLabel nota = new JLabel("Límite máximo de descuento permitido");
        nota.setForeground(Color.GRAY); panel.add(nota, gbc);

        y++; gbc.gridy = y; panel.add(new JLabel("Redondeo de precios"), gbc);
        gbc.gridy = ++y; 
        cbRedondeo = new JComboBox<>(new String[]{"Sin redondeo", "Redondear arriba", "Redondear abajo"});
        cbRedondeo.setPreferredSize(new Dimension(0, 35));
        panel.add(cbRedondeo, gbc);

        y++; gbc.gridy = y++; 
        JLabel t2 = new JLabel("Numeración de comprobantes");
        t2.setFont(new Font("Segoe UI", Font.BOLD, 18)); 
        t2.setForeground(new Color(26, 79, 156));
        t2.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(t2, gbc);

        gbc.gridy = y; panel.add(new JLabel("Formato de número"), gbc);
        gbc.gridy = ++y; 
        txtFormatoNum = new JTextField("DD-MM-YYYY"); 
        txtFormatoNum.setPreferredSize(new Dimension(0, 35));
        panel.add(txtFormatoNum, gbc);

        y++; gbc.gridy = y; panel.add(new JLabel("Margen (mm)"), gbc);
        gbc.gridy = ++y; 
        txtMargen = new JTextField("5");
        txtMargen.setPreferredSize(new Dimension(0, 35));
        configurarSoloNumeros(txtMargen);
        panel.add(txtMargen, gbc);

        contenedor.add(panel, BorderLayout.NORTH);
        return contenedor;
    }

    private JPanel crearSeccionUsuarios() {
        JPanel main = new JPanel(new BorderLayout(20, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(30, 30, 30, 30)
        ));

        JPanel form = new JPanel(new GridLayout(8, 1, 5, 10));
        form.setBackground(Color.WHITE);
        form.setPreferredSize(new Dimension(300, 0));
        
        JLabel lblFormTitulo = new JLabel("Registrar Nuevo");
        lblFormTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitulo.setForeground(new Color(26, 79, 156));
        form.add(lblFormTitulo);
        
        form.add(new JLabel("Nombre del Usuario"));
        JTextField txtNuevoUsr = new JTextField();
        form.add(txtNuevoUsr);
        
        form.add(new JLabel("Rol"));
        JComboBox<String> cbRol = new JComboBox<>(new String[]{"Administrador", "Cajero", "Supervisor"});
        form.add(cbRol);
        
        form.add(new JLabel("Contraseña"));
        JPasswordField txtPass = new JPasswordField();
        form.add(txtPass);
        
        JButton btnAdd = new JButton("Añadir Usuario");
        btnAdd.setBackground(new Color(26, 79, 156));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        form.add(btnAdd);

        String[] columnas = {"ID", "Usuario", "Rol", "Estado"};
        Object[][] datos = {
            {"1", "Manuel Gotera", "Administrador", "Conectado"},
            {"2", "Ana Lopez", "Cajero", "Ausente"},
            {"3", "Carlos_99", "Supervisor", "Desconectado"}
        };
        
        DefaultTableModel model = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable tabla = new JTable(model);
        tabla.setRowHeight(35);
        tabla.setSelectionBackground(new Color(232, 241, 255));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(225, 228, 233)));

        main.add(form, BorderLayout.WEST);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private JPanel crearSeccionDatos() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);
        contenedor.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;
        JLabel t1 = new JLabel("Mantenimiento y Respaldo de Datos");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t1.setForeground(new Color(26, 79, 156));
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(t1, gbc);

        gbc.gridwidth = 1; gbc.gridy = y++; gbc.gridx = 0;
        panel.add(new JLabel("Base de Datos Actual"), gbc);
        gbc.gridx = 1; panel.add(new JLabel("pos_db_principal.db (12.5 MB)"), gbc);

        gbc.gridy = y++; gbc.gridx = 0; gbc.gridwidth = 2;
        JButton btnBackup = new JButton("Crear copia de seguridad (Backup)");
        btnBackup.setBackground(new Color(40, 167, 69));
        btnBackup.setForeground(Color.WHITE);
        btnBackup.setPreferredSize(new Dimension(0, 40));
        btnBackup.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnBackup.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Iniciando proceso de backup...\nPor favor espere.", "Sistema", JOptionPane.INFORMATION_MESSAGE);
            JOptionPane.showMessageDialog(this, "Backup creado exitosamente en:\n/backups/DB_BACKUP_" + System.currentTimeMillis() + ".db", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
        panel.add(btnBackup, gbc);

        gbc.gridy = y++;
        JButton btnExportar = new JButton("Exportar historial a Excel (.xlsx)");
        btnExportar.setPreferredSize(new Dimension(0, 40));
        btnExportar.addActionListener(e -> {
            JFileChooser save = new JFileChooser();
            save.setSelectedFile(new File("reporte_ventas.xlsx"));
            int result = save.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                JOptionPane.showMessageDialog(this, "Archivo excel generado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(btnExportar, gbc);

        gbc.gridy = y++;
        JLabel t2 = new JLabel("Zona Peligrosa");
        t2.setForeground(Color.RED);
        t2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        t2.setBorder(new EmptyBorder(20, 0, 0, 0));
        panel.add(t2, gbc);

        gbc.gridy = y++;
        JButton btnReset = new JButton("Restablecer sistema de fábrica");
        btnReset.setForeground(Color.RED);
        btnReset.setPreferredSize(new Dimension(0, 40));
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.addActionListener(e -> {
            int confirmar = JOptionPane.showConfirmDialog(this, 
                "¿ESTÁ SEGURO? Esta acción borrará todos los productos y ventas.\nEsta acción no se puede deshacer.", 
                "ADVERTENCIA CRÍTICA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirmar == JOptionPane.YES_OPTION) {
                String pass = JOptionPane.showInputDialog(this, "Ingrese contraseña de administrador para confirmar:");
                if ("admin".equals(pass)) {
                    JOptionPane.showMessageDialog(this, "Sistema restablecido. El programa se cerrará.");
                    System.exit(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(btnReset, gbc);

        contenedor.add(panel, BorderLayout.NORTH);
        return contenedor;
    }

    private void configurarSoloNumeros(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string.matches("[0-9+]*")) super.insertString(fb, offset, string, attr);
            }
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text.matches("[0-9+]*")) super.replace(fb, offset, length, text, attrs);
            }
        });
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "png", "svg"));
        int seleccion = fileChooser.showOpenDialog(this);
        if (seleccion == JFileChooser.APPROVE_OPTION) {
            File fichero = fileChooser.getSelectedFile();
            JOptionPane.showMessageDialog(this, "Logo cargado: " + fichero.getName());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConfiguracionFrame().setVisible(true));
    }
}