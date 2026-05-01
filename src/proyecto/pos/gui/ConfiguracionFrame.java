package proyecto.pos.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ConfiguracionFrame extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO = new Color(246, 248, 251);
    private static final Color BORDE = new Color(225, 228, 233);

    public ConfiguracionFrame() {
        setTitle("Configuración");
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        construirUI();
    }

    private void construirUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    // SIDEBAR
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDE));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.setBorder(new EmptyBorder(20, 15, 20, 15));

        JLabel logo = new JLabel("▣ POS");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(AZUL);

        JLabel sub = new JLabel("Sistema de Caja");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(Color.GRAY);

        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.add(logo, BorderLayout.NORTH);
        logoPanel.add(sub, BorderLayout.CENTER);

        top.add(logoPanel, BorderLayout.CENTER);
        sidebar.add(top, BorderLayout.NORTH);

        JPanel menu = new JPanel(new GridLayout(10, 1));
        menu.setBackground(Color.WHITE);

        String[] opciones = {
            "Cajero",
            "Artículos y Stock",
            "Historial de Transacciones",
            "Reportes",
            "Gastos",
            "Configuración"
        };

        for (String op : opciones) {
            JLabel item = new JLabel(op);
            item.setBorder(new EmptyBorder(10, 15, 10, 15));

            if (op.equals("Configuración")) {
                item.setOpaque(true);
                item.setBackground(AZUL_CLARO);
                item.setForeground(AZUL);
            }

            menu.add(item);
        }

        sidebar.add(menu, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(2, 1));
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(new EmptyBorder(10, 15, 20, 15));

        bottom.add(new JLabel("Modo claro"));
        JLabel salir = new JLabel("Salir");
        salir.setForeground(Color.RED);
        bottom.add(salir);

        sidebar.add(bottom, BorderLayout.SOUTH);

        return sidebar;
    }

    // CONTENIDO
    private JPanel crearContenido() {
        JPanel cont = new JPanel(new BorderLayout());
        cont.setBackground(FONDO);
        cont.setBorder(new EmptyBorder(20, 20, 20, 20));

        cont.add(crearHeader(), BorderLayout.NORTH);
        cont.add(crearPanelPrincipal(), BorderLayout.CENTER);

        return cont;
    }

    // HEADER
    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);

        JLabel titulo = new JLabel("Configuración de la tienda");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(FONDO);

        JLabel hora = new JLabel("<html><b>Hora</b><br>15:07:14</html>");
        hora.setOpaque(true);
        hora.setBackground(Color.WHITE);
        hora.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel usuario = new JLabel("<html><b> uwu fernandez</b><br>Cajero</html>");
        usuario.setOpaque(true);
        usuario.setBackground(Color.WHITE);
        usuario.setBorder(new EmptyBorder(8, 12, 8, 12));

        userPanel.add(hora);
        userPanel.add(usuario);

        header.add(titulo, BorderLayout.WEST);
        header.add(userPanel, BorderLayout.EAST);

        return header;
    }

    // PANEL
    private JPanel crearPanelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                new EmptyBorder(20, 20, 20, 20)
        ));

        panel.add(crearTabs(), BorderLayout.CENTER);

        return panel;
    }

    // TABS
    private JTabbedPane crearTabs() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Perfil de Tienda", crearPerfil());
        tabs.add("Impuestos y Recibo", crearImpuestos());
        tabs.add("Usuarios", new JPanel());
        tabs.add("Datos", new JPanel());

        return tabs;
    }

    // PERFIL (NO TOCADO)
    private JPanel crearPerfil() {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JLabel titulo = new JLabel("Información de la tienda");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        panel.add(new JLabel("Nombre de la tienda *"), gbc);

        gbc.gridy++;
        panel.add(new JTextField(" "), gbc);

        gbc.gridy++;
        panel.add(new JLabel("Dirección completa *"), gbc);

        gbc.gridy++;
        JTextArea direccion = new JTextArea(3, 20);
        direccion.setBorder(BorderFactory.createLineBorder(new Color(200,200,200)));
        panel.add(direccion, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel("Teléfono *"), gbc);

        gbc.gridx = 1;
        panel.add(new JLabel("WhatsApp *"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JTextField("+51"), gbc);

        gbc.gridx = 1;
        panel.add(new JTextField("+51"), gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        panel.add(new JLabel("RUC (opcional)"), gbc);

        gbc.gridy++;
        panel.add(new JTextField(" "), gbc);

        gbc.gridy++;
        panel.add(new JLabel("Logo (opcional)"), gbc);

        gbc.gridy++;
        JButton btnSubir = new JButton("Subir imagen (JPG, PNG, SVG)");
        panel.add(btnSubir, gbc);

        gbc.gridy++;
        JButton guardar = new JButton("Guardar cambios");
        panel.add(guardar, gbc);

        contenedor.add(panel, BorderLayout.NORTH);

        return contenedor;
    }

    // 🔥 IMPUESTOS (COMO TU IMAGEN)
    private JPanel crearImpuestos() {

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int y = 0;

        // IMPUESTOS
        JLabel t1 = new JLabel("Configuración de Impuestos");
        t1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = y++; gbc.gridwidth = 2;
        panel.add(t1, gbc);

        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("IGV 18%"), gbc);

        gbc.gridx = 1;
        panel.add(new JCheckBox("Activado"), gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel("Descuento máximo (%)"), gbc);

        gbc.gridy = ++y;
        panel.add(new JTextField(" "), gbc);

        y++;
        JLabel nota = new JLabel("Límite máximo de descuento permitido");
        nota.setForeground(Color.GRAY);
        panel.add(nota, gbc);

        y++;
        gbc.gridy = y;
        panel.add(new JLabel("Redondeo de precios"), gbc);

        gbc.gridy = ++y;
        panel.add(new JComboBox<>(new String[]{
            "Sin redondeo", "Redondear arriba", "Redondear abajo"
        }), gbc);

        y++;
        panel.add(new JButton("Guardar configuración"), gbc);

        // NUMERACIÓN
        y++;
        gbc.gridy = y++;
        JLabel t2 = new JLabel("Numeración de comprobantes");
        t2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(t2, gbc);

        gbc.gridy = y;
        panel.add(new JLabel("Formato de número"), gbc);

        gbc.gridy = ++y;
        panel.add(new JTextField("Ejemplo: DD-MM-YYYY"), gbc);

        y++;
        JLabel ayuda = new JLabel("(DD=Día, MM=Mes, YYYY=Año)");
        ayuda.setForeground(Color.GRAY);
        panel.add(ayuda, gbc);

        y++;
        gbc.gridy = y;
        panel.add(new JLabel("Reinicio de numeración"), gbc);

        gbc.gridy = ++y;
        panel.add(new JComboBox<>(new String[]{
            "Mensual", "Anual", "Nunca"
        }), gbc);

        y++;
        panel.add(new JButton("Guardar numeración"), gbc);

        // IMPRESIÓN
        y++;
        gbc.gridy = y++;
        JLabel t3 = new JLabel("Configuración de impresión");
        t3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panel.add(t3, gbc);

        gbc.gridy = y;
        panel.add(new JLabel("Tamaño de papel"), gbc);

        gbc.gridy = ++y;
        panel.add(new JComboBox<>(new String[]{
            "80 mm", "58 mm"
        }), gbc);

        y++;
        gbc.gridy = y;
        panel.add(new JLabel("Margen (mm)"), gbc);

        gbc.gridy = ++y;
        panel.add(new JTextField("Ejemplo: 5"), gbc);

        y++;
        gbc.gridy = y;
        panel.add(new JLabel("Impresión automática"), gbc);

        gbc.gridy = ++y;
        panel.add(new JCheckBox("Imprimir automáticamente después de pagar"), gbc);

        y++;
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        botones.setBackground(Color.WHITE);
        botones.add(new JButton("Guardar impresión"));
        botones.add(new JButton("Probar impresión"));

        panel.add(botones, gbc);

        contenedor.add(panel, BorderLayout.NORTH);

        return contenedor;
    }
}