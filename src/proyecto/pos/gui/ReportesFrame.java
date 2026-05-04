package proyecto.pos.gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;

public class ReportesFrame extends JFrame {

    private static final Color AZUL = new Color(26, 83, 160);
    private static final Color AZUL_HOVER = new Color(18, 65, 128);
    private static final Color AZUL_CLARO = new Color(232, 241, 255);
    private static final Color FONDO = new Color(246, 248, 251);
    private static final Color SIDEBAR = new Color(250, 251, 253);
    private static final Color BORDE = new Color(225, 229, 236);
    private static final Color TEXTO = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);

    private static final Color MORADO = new Color(183, 80, 255);
    private static final Color CELESTE = new Color(0, 118, 255);
    private static final Color ROJO = new Color(235, 32, 49);
    private static final Color VERDE = new Color(0, 172, 65);
    private static final Color NARANJA = new Color(245, 158, 11);

    private final DecimalFormat formato = new DecimalFormat("#,##0.00");

    private String periodoActual = "Últimos 7 días";
    private String tabActual = "ingresos";

    private String[] dias = {"10 Nov", "11 Nov", "12 Nov", "13 Nov", "14 Nov", "15 Nov"};
    private double[] ingresos = {1200, 2100, 800, 1050, 3600, 4350};
    private double[] gastos = {600, 1200, 2100, 1500, 1600, 1000};
    private int[] metodosPago = {18, 21, 15};
    private int totalTransacciones = 54;

    private KpiCard cardTransacciones;
    private KpiCard cardVentas;
    private KpiCard cardGastos;
    private KpiCard cardUtilidad;

    private LineChartPanel graficoLinea;
    private BarChartPanel graficoBarras;
    private DonutChartPanel graficoDonut;

    private JButton tabIngresos;
    private JButton tabGastos;
    private JButton tabComparacion;
    private JButton btnFiltroPeriodo;

    public ReportesFrame() {
        configurarVentana();
        construirInterfaz();
    }

    private void configurarVentana() {
        setTitle("Reportes");
        setSize(1180, 740);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(crearSidebar(), BorderLayout.WEST);
        root.add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBackground(SIDEBAR);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDE));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        sidebar.add(crearHeaderSidebar());
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(34));

        JButton btnCajero = crearBotonMenu("Cajero", "/img/carrito.png", false);
        JButton btnStock = crearBotonMenu("Artículos y Stock", "/img/stock.png", false);
        JButton btnHistorial = crearBotonMenu("Historial de Trans.", "/img/Historial.png", false);
        JButton btnReportes = crearBotonMenu("Reportes", "/img/Reporte.png", true);
        JButton btnGastos = crearBotonMenu("Gastos", "/img/billetera.png", false);
        JButton btnConfig = crearBotonMenu("Configuración", "/img/configuracion.png", false);

        btnCajero.addActionListener(e -> {
            new Caja_GUI().setVisible(true);
            dispose();
        });

        btnStock.addActionListener(e -> {
            new ArticulosStockFrame().setVisible(true);
            dispose();
        });

        btnHistorial.addActionListener(e -> {
            new HistorialTransaccionesFrame().setVisible(true);
            dispose();
        });

        btnGastos.addActionListener(e -> mostrarPendiente("Módulo de gastos pendiente de conectar."));
        btnConfig.addActionListener(e -> mostrarPendiente("Módulo de configuración pendiente de conectar."));

        agregarMenu(sidebar, btnCajero, btnStock, btnHistorial, btnReportes, btnGastos, btnConfig);

        sidebar.add(Box.createVerticalGlue());
        sidebar.add(crearLinea());
        sidebar.add(Box.createVerticalStrut(12));

        JButton btnModo = crearBotonMenu("Mode Tampilan", "/img/ojo.png", false);
        JButton btnSalir = crearBotonMenu("Salir", "/img/Salir.png", false);
        btnSalir.setForeground(new Color(220, 53, 69));
        btnSalir.addActionListener(e -> System.exit(0));

        agregarMenu(sidebar, btnModo, btnSalir);
        sidebar.add(Box.createVerticalStrut(18));

        return sidebar;
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

        JLabel lblPos = new JLabel("Pos System");
        lblPos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPos.setForeground(AZUL);

        JLabel lblDesc = new JLabel("Sistema de Caja");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(TEXTO_SUAVE);

        textos.add(lblPos);
        textos.add(lblDesc);

        marca.add(logo);
        marca.add(textos);

        JButton btnColapsar = new JButton("≪");
        btnColapsar.setPreferredSize(new Dimension(34, 34));
        btnColapsar.setFocusPainted(false);
        btnColapsar.setBorderPainted(false);
        btnColapsar.setBackground(new Color(236, 239, 244));
        btnColapsar.setForeground(new Color(80, 86, 98));
        btnColapsar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        header.add(marca, BorderLayout.CENTER);
        header.add(btnColapsar, BorderLayout.EAST);

        return header;
    }

    private JButton crearBotonMenu(String texto, String iconPath, boolean seleccionado) {
        JButton boton = new JButton(texto);
        boton.setIcon(redimensionarIcono(iconPath, 18, 18));
        boton.setIconTextGap(13);
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
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!seleccionado) {
                    boton.setBackground(new Color(240, 243, 248));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!seleccionado) {
                    boton.setBackground(SIDEBAR);
                }
            }
        });

        return boton;
    }

    private void agregarMenu(JPanel panel, JButton... botones) {
        for (JButton boton : botones) {
            panel.add(boton);
            panel.add(Box.createVerticalStrut(7));
        }
    }

    private JPanel crearLinea() {
        JPanel linea = new JPanel();
        linea.setMaximumSize(new Dimension(220, 1));
        linea.setPreferredSize(new Dimension(220, 1));
        linea.setBackground(new Color(232, 235, 241));
        return linea;
    }

    private JPanel crearContenido() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(FONDO);
        contenedor.setBorder(new EmptyBorder(26, 28, 24, 28));

        contenedor.add(crearHeader(), BorderLayout.NORTH);
        contenedor.add(crearPanelPrincipal(), BorderLayout.CENTER);

        return contenedor;
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(FONDO);
        header.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Reportes");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(Color.BLACK);

        JLabel subtitulo = new JLabel("Análisis financiero y rendimiento del negocio");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitulo.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(4));
        titulos.add(subtitulo);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        derecha.setOpaque(false);
        derecha.add(crearBotonIcono("/img/ojo.png"));
        derecha.add(crearTarjetaHora());
        derecha.add(crearTarjetaUsuario());

        header.add(titulos, BorderLayout.WEST);
        header.add(derecha, BorderLayout.EAST);

        return header;
    }

    private JButton crearBotonIcono(String iconPath) {
        JButton boton = new JButton(redimensionarIcono(iconPath, 18, 18));
        boton.setPreferredSize(new Dimension(44, 40));
        boton.setBackground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorder(new RoundedBorder(BORDE, 12));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private JPanel crearTarjetaHora() {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 14, 6, 14)
        ));
        tarjeta.setPreferredSize(new Dimension(110, 40));

        JLabel lblTitulo = new JLabel("Hora");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitulo.setForeground(TEXTO);

        JLabel lblHora = new JLabel(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " WIB");
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblHora.setForeground(TEXTO_SUAVE);

        new Timer(1000, e -> lblHora.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()) + " WIB")).start();

        tarjeta.add(lblTitulo);
        tarjeta.add(lblHora);

        return tarjeta;
    }

    private JPanel crearTarjetaUsuario() {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 0));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 12),
                new EmptyBorder(6, 10, 6, 12)
        ));
        tarjeta.setPreferredSize(new Dimension(150, 40));

        JLabel avatar = new JLabel(redimensionarIcono("/img/perfilPedro.jpg", 28, 28));
        avatar.setPreferredSize(new Dimension(28, 28));

        JPanel textos = new JPanel();
        textos.setOpaque(false);
        textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

        JLabel nombre = new JLabel("uwu fernandez");
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 10));
        nombre.setForeground(TEXTO);

        JLabel rol = new JLabel("Cajero");
        rol.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        rol.setForeground(TEXTO_SUAVE);

        textos.add(nombre);
        textos.add(rol);

        tarjeta.add(avatar, BorderLayout.WEST);
        tarjeta.add(textos, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearPanelPrincipal() {
        RoundedPanel panel = new RoundedPanel(Color.WHITE, 14);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDE, 14),
                new EmptyBorder(14, 14, 14, 14)
        ));

        JPanel acciones = crearBarraAcciones();
        JPanel kpis = crearKPIs();
        JPanel tabs = crearTabs();

        graficoLinea = new LineChartPanel("Ingresos diarios", dias, ingresos, AZUL);

        JPanel graficosInferiores = crearGraficosInferiores();

        estirar(acciones, 42);
        estirar(kpis, 78);
        estirar(tabs, 38);
        estirar(graficoLinea, 205);
        estirar(graficosInferiores, 165);

        panel.add(acciones);
        panel.add(Box.createVerticalStrut(8));
        panel.add(kpis);
        panel.add(Box.createVerticalStrut(10));
        panel.add(tabs);
        panel.add(Box.createVerticalStrut(8));
        panel.add(graficoLinea);
        panel.add(Box.createVerticalStrut(8));
        panel.add(graficosInferiores);

        return panel;
    }

    private JPanel crearBarraAcciones() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        btnFiltroPeriodo = crearBotonSecundario(periodoActual, "/img/Historial.png", 150);
        JButton btnImprimir = crearBotonSecundario("Exportar reporte", "/img/imprimir.png", 170);

        btnFiltroPeriodo.addActionListener(e -> mostrarFiltroPeriodo());
        btnImprimir.addActionListener(e -> mostrarPendiente("Exportación de reporte pendiente de conectar."));

        panel.add(btnFiltroPeriodo, BorderLayout.WEST);
        panel.add(btnImprimir, BorderLayout.EAST);

        return panel;
    }

    private JPanel crearKPIs() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setOpaque(false);

        double totalIngresos = sumar(ingresos);
        double totalGastos = sumar(gastos);
        double utilidad = totalIngresos - totalGastos;

        cardTransacciones = new KpiCard(
                redimensionarIcono("/img/carrito.png", 22, 22),
                "Total transacciones",
                String.valueOf(totalTransacciones),
                MORADO,
                new Color(245, 228, 255)
        );

        cardVentas = new KpiCard(
                redimensionarIcono("/img/Reporte.png", 22, 22),
                "Ventas totales",
                soles(totalIngresos),
                CELESTE,
                new Color(225, 241, 255)
        );

        cardGastos = new KpiCard(
                redimensionarIcono("/img/billetera.png", 22, 22),
                "Total gastos",
                soles(totalGastos),
                ROJO,
                new Color(255, 230, 232)
        );

        cardUtilidad = new KpiCard(
                redimensionarIcono("/img/stock.png", 22, 22),
                "Utilidad bruta",
                soles(utilidad),
                utilidad >= 0 ? VERDE : ROJO,
                utilidad >= 0 ? new Color(222, 250, 234) : new Color(255, 230, 232)
        );

        panel.add(cardTransacciones);
        panel.add(cardVentas);
        panel.add(cardGastos);
        panel.add(cardUtilidad);

        return panel;
    }

    private JPanel crearTabs() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        tabIngresos = crearTab("Ingresos", true);
        tabGastos = crearTab("Gastos", false);
        tabComparacion = crearTab("Comparación", false);

        tabIngresos.addActionListener(e -> cambiarTab("ingresos"));
        tabGastos.addActionListener(e -> cambiarTab("gastos"));
        tabComparacion.addActionListener(e -> cambiarTab("comparacion"));

        panel.add(tabIngresos);
        panel.add(tabGastos);
        panel.add(tabComparacion);

        return panel;
    }

    private JButton crearTab(String texto, boolean activo) {
        JButton boton = new JButton(texto);
        boton.setPreferredSize(new Dimension(116, 36));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        aplicarEstadoTab(boton, activo);
        return boton;
    }

    private void cambiarTab(String tipo) {
        tabActual = tipo;

        aplicarEstadoTab(tabIngresos, tipo.equals("ingresos"));
        aplicarEstadoTab(tabGastos, tipo.equals("gastos"));
        aplicarEstadoTab(tabComparacion, tipo.equals("comparacion"));

        if (tipo.equals("ingresos")) {
            graficoLinea.setData("Ingresos diarios", dias, ingresos, AZUL);
        } else if (tipo.equals("gastos")) {
            graficoLinea.setData("Gastos diarios", dias, gastos, ROJO);
        } else {
            graficoLinea.setData("Utilidad diaria", dias, calcularUtilidadDiaria(), VERDE);
        }
    }

    private void aplicarEstadoTab(JButton boton, boolean activo) {
        if (activo) {
            boton.setBackground(AZUL);
            boton.setForeground(Color.WHITE);
            boton.setBorder(new RoundedBorder(AZUL, 10));
        } else {
            boton.setBackground(Color.WHITE);
            boton.setForeground(new Color(88, 96, 110));
            boton.setBorder(new RoundedBorder(BORDE, 10));
        }
    }

    private JPanel crearGraficosInferiores() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 14, 0));
        panel.setOpaque(false);

        graficoDonut = new DonutChartPanel(
                "Método de pago",
                new String[]{"Efectivo", "Tarjeta", "Yape / QR"},
                metodosPago,
                new Color[]{VERDE, NARANJA, new Color(20, 164, 225)}
        );

        graficoBarras = new BarChartPanel(
                "Ingresos vs Gastos",
                dias,
                ingresos,
                gastos
        );

        panel.add(graficoDonut);
        panel.add(graficoBarras);

        return panel;
    }

    private JButton crearBotonSecundario(String texto, String iconPath, int ancho) {
        JButton boton = new JButton(texto);
        boton.setIcon(redimensionarIcono(iconPath, 16, 16));
        boton.setIconTextGap(8);
        boton.setPreferredSize(new Dimension(ancho, 38));
        boton.setBackground(Color.WHITE);
        boton.setForeground(AZUL);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(new RoundedBorder(AZUL, 10));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(new Color(244, 248, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(Color.WHITE);
            }
        });

        return boton;
    }

    private void estirar(Component componente, int alto) {
        Dimension dimension = new Dimension(100, alto);
        componente.setPreferredSize(dimension);
        componente.setMinimumSize(dimension);
        componente.setMaximumSize(new Dimension(Integer.MAX_VALUE, alto));
    }

    private void mostrarFiltroPeriodo() {
        String[] opciones = {
            "Hoy",
            "Últimos 7 días",
            "Últimos 30 días",
            "Este mes"
        };

        String seleccion = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione el periodo del reporte:",
                "Filtrar periodo",
                JOptionPane.PLAIN_MESSAGE,
                null,
                opciones,
                periodoActual
        );

        if (seleccion != null) {
            aplicarPeriodoDemo(seleccion);
        }
    }

    private void aplicarPeriodoDemo(String periodo) {
        periodoActual = periodo;

        switch (periodo) {
            case "Hoy":
                dias = new String[]{"08:00", "10:00", "12:00", "14:00", "16:00", "18:00"};
                ingresos = new double[]{120, 250, 600, 430, 780, 950};
                gastos = new double[]{80, 90, 180, 150, 210, 260};
                metodosPago = new int[]{8, 5, 4};
                totalTransacciones = 17;
                break;

            case "Últimos 30 días":
                dias = new String[]{"Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Hoy"};
                ingresos = new double[]{6200, 7100, 8400, 7600, 9100, 10300};
                gastos = new double[]{3200, 3900, 4100, 4300, 4700, 5000};
                metodosPago = new int[]{70, 83, 61};
                totalTransacciones = 214;
                break;

            case "Este mes":
                dias = new String[]{"01-05", "06-10", "11-15", "16-20", "21-25", "26-30"};
                ingresos = new double[]{5300, 6900, 8100, 7400, 9500, 11200};
                gastos = new double[]{2900, 3400, 4600, 4200, 5200, 6100};
                metodosPago = new int[]{86, 92, 73};
                totalTransacciones = 251;
                break;

            default:
                dias = new String[]{"10 Nov", "11 Nov", "12 Nov", "13 Nov", "14 Nov", "15 Nov"};
                ingresos = new double[]{1200, 2100, 800, 1050, 3600, 4350};
                gastos = new double[]{600, 1200, 2100, 1500, 1600, 1000};
                metodosPago = new int[]{18, 21, 15};
                totalTransacciones = 54;
                break;
        }

        btnFiltroPeriodo.setText(periodo);
        actualizarKPIs();
        actualizarGraficos();
    }

    private void actualizarKPIs() {
        double totalIngresos = sumar(ingresos);
        double totalGastos = sumar(gastos);
        double utilidad = totalIngresos - totalGastos;

        cardTransacciones.setValor(String.valueOf(totalTransacciones));
        cardVentas.setValor(soles(totalIngresos));
        cardGastos.setValor(soles(totalGastos));
        cardUtilidad.setValor(soles(utilidad));
        cardUtilidad.setColorValor(utilidad >= 0 ? VERDE : ROJO);
    }

    private void actualizarGraficos() {
        graficoDonut.setData(
                new String[]{"Efectivo", "Tarjeta", "Yape / QR"},
                metodosPago
        );

        graficoBarras.setData(dias, ingresos, gastos);

        if (tabActual.equals("ingresos")) {
            graficoLinea.setData("Ingresos diarios", dias, ingresos, AZUL);
        } else if (tabActual.equals("gastos")) {
            graficoLinea.setData("Gastos diarios", dias, gastos, ROJO);
        } else {
            graficoLinea.setData("Utilidad diaria", dias, calcularUtilidadDiaria(), VERDE);
        }
    }

    private double sumar(double[] datos) {
        double total = 0;
        for (double dato : datos) {
            total += dato;
        }
        return total;
    }

    private double[] calcularUtilidadDiaria() {
        double[] utilidad = new double[ingresos.length];

        for (int i = 0; i < ingresos.length; i++) {
            utilidad[i] = ingresos[i] - gastos[i];
        }

        return utilidad;
    }

    private String soles(double monto) {
        return "S/ " + formato.format(monto);
    }

    private void mostrarPendiente(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Reportes", JOptionPane.INFORMATION_MESSAGE);
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

    public static void main(String[] args) {
        activarVisual();

        SwingUtilities.invokeLater(() -> new ReportesFrame().setVisible(true));
    }

    private static void activarVisual() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    private static class KpiCard extends RoundedPanel {

        private JLabel lblValor;

        public KpiCard(ImageIcon icono, String titulo, String valor, Color colorPrincipal, Color colorFondoIcono) {
            super(Color.WHITE, 12);
            setLayout(new BorderLayout(12, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDE, 12),
                    new EmptyBorder(12, 14, 12, 14)
            ));

            JPanel circulo = new CircleIconPanel(icono, colorFondoIcono);
            circulo.setPreferredSize(new Dimension(46, 46));

            JPanel textos = new JPanel();
            textos.setOpaque(false);
            textos.setLayout(new BoxLayout(textos, BoxLayout.Y_AXIS));

            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lblTitulo.setForeground(colorPrincipal);

            lblValor = new JLabel(valor);
            lblValor.setFont(new Font("Segoe UI", Font.BOLD, 17));
            lblValor.setForeground(colorPrincipal);

            textos.add(Box.createVerticalGlue());
            textos.add(lblTitulo);
            textos.add(Box.createVerticalStrut(3));
            textos.add(lblValor);
            textos.add(Box.createVerticalGlue());

            add(circulo, BorderLayout.WEST);
            add(textos, BorderLayout.CENTER);
        }

        public void setValor(String nuevoValor) {
            lblValor.setText(nuevoValor);
            repaint();
        }

        public void setColorValor(Color color) {
            lblValor.setForeground(color);
            repaint();
        }
    }

    private static class CircleIconPanel extends JPanel {

        private final ImageIcon icono;
        private final Color colorFondo;

        public CircleIconPanel(ImageIcon icono, Color colorFondo) {
            this.icono = icono;
            this.colorFondo = colorFondo;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = preparar(g);

            int size = Math.min(getWidth(), getHeight()) - 2;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            g2.setColor(colorFondo);
            g2.fillOval(x, y, size, size);

            if (icono != null) {
                int ix = (getWidth() - icono.getIconWidth()) / 2;
                int iy = (getHeight() - icono.getIconHeight()) / 2;
                icono.paintIcon(this, g2, ix, iy);
            }

            g2.dispose();
        }
    }

    private static class LineChartPanel extends RoundedPanel {

        private String titulo;
        private String[] labels;
        private double[] values;
        private Color lineColor;

        public LineChartPanel(String titulo, String[] labels, double[] values, Color lineColor) {
            super(Color.WHITE, 12);
            this.titulo = titulo;
            this.labels = labels;
            this.values = values;
            this.lineColor = lineColor;

            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDE, 12),
                    new EmptyBorder(12, 14, 12, 14)
            ));
        }

        public void setData(String titulo, String[] labels, double[] values, Color lineColor) {
            this.titulo = titulo;
            this.labels = labels;
            this.values = values;
            this.lineColor = lineColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = preparar(g);

            int left = 62;
            int right = 18;
            int top = 48;
            int bottom = 34;
            int w = getWidth() - left - right;
            int h = getHeight() - top - bottom;

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(titulo, 14, 25);

            double min = obtenerMinimo(values);
            double max = obtenerMaximo(values);

            if (min > 0) {
                min = 0;
            }

            if (max < 0) {
                max = 0;
            }

            double rango = max - min;

            if (rango == 0) {
                rango = 1;
            }

            double padding = rango * 0.15;
            min -= padding;
            max += padding;
            rango = max - min;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            for (int i = 0; i <= 5; i++) {
                int y = top + (h * i / 5);
                double valor = max - (rango * i / 5);

                g2.setColor(new Color(225, 229, 236));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(left, y, left + w, y);

                g2.setColor(new Color(145, 153, 166));
                g2.drawString(formatoCorto(valor), 10, y + 4);
            }

            int yCero = top + (int) ((max - 0) / rango * h);

            if (yCero >= top && yCero <= top + h) {
                g2.setColor(new Color(180, 187, 198));
                g2.setStroke(new BasicStroke(1.4f));
                g2.drawLine(left, yCero, left + w, yCero);
            }

            g2.setColor(new Color(225, 229, 236));
            g2.drawLine(left, top, left, top + h);
            g2.drawLine(left, top + h, left + w, top + h);

            int[] xs = new int[values.length];
            int[] ys = new int[values.length];

            for (int i = 0; i < values.length; i++) {
                xs[i] = left + (w * i / (values.length - 1));
                ys[i] = top + (int) ((max - values[i]) / rango * h);
            }

            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(2));

            for (int i = 0; i < xs.length - 1; i++) {
                g2.drawLine(xs[i], ys[i], xs[i + 1], ys[i + 1]);
            }

            for (int i = 0; i < xs.length; i++) {
                g2.setColor(Color.WHITE);
                g2.fillOval(xs[i] - 4, ys[i] - 4, 8, 8);

                g2.setColor(lineColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(xs[i] - 4, ys[i] - 4, 8, 8);
            }

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(145, 153, 166));

            for (int i = 0; i < labels.length; i++) {
                int textW = g2.getFontMetrics().stringWidth(labels[i]);
                g2.drawString(labels[i], xs[i] - textW / 2, top + h + 22);
            }

            int index = Math.min(2, values.length - 1);
            dibujarTooltip(g2, xs[index], ys[index], values[index]);

            g2.dispose();
        }

        private void dibujarTooltip(Graphics2D g2, int x, int y, double value) {
            String linea1 = "Dato seleccionado";
            String linea2 = "S/ " + new DecimalFormat("#,##0.00").format(value);

            int boxW = 132;
            int boxH = 48;
            int bx = Math.min(x + 10, getWidth() - boxW - 12);
            int by = Math.max(y - 52, 42);

            g2.setColor(new Color(12, 38, 78));
            g2.fillRoundRect(bx, by, boxW, boxH, 8, 8);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
            g2.drawString(linea1, bx + 10, by + 18);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g2.setColor(new Color(210, 226, 255));
            g2.drawString(linea2, bx + 10, by + 35);
        }
    }

    private static class DonutChartPanel extends RoundedPanel {

        private final String titulo;
        private String[] labels;
        private int[] values;
        private final Color[] colors;

        public DonutChartPanel(String titulo, String[] labels, int[] values, Color[] colors) {
            super(Color.WHITE, 12);
            this.titulo = titulo;
            this.labels = labels;
            this.values = values;
            this.colors = colors;

            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDE, 12),
                    new EmptyBorder(12, 14, 12, 14)
            ));
        }

        public void setData(String[] labels, int[] values) {
            this.labels = labels;
            this.values = values;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = preparar(g);

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(titulo, 14, 24);

            int total = 0;

            for (int value : values) {
                total += value;
            }

            if (total <= 0) {
                g2.dispose();
                return;
            }

            int size = Math.min(125, getHeight() - 62);
            int x = 28;
            int y = 52;
            int start = 90;

            for (int i = 0; i < values.length; i++) {
                int angle = (int) Math.round(values[i] * 360.0 / total);
                g2.setColor(colors[i]);
                g2.fillArc(x, y, size, size, start, -angle);
                start -= angle;
            }

            g2.setColor(Color.WHITE);
            int hole = size / 2;
            g2.fillOval(x + size / 4, y + size / 4, hole, hole);

            int legendX = x + size + 48;
            int legendY = y + 12;

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));

            for (int i = 0; i < labels.length; i++) {
                int itemY = legendY + i * 28;

                g2.setColor(colors[i]);
                g2.fillOval(legendX, itemY - 9, 10, 10);

                g2.setColor(TEXTO);
                g2.drawString(labels[i], legendX + 18, itemY);

                String porcentaje = String.format("%.1f%%", values[i] * 100.0 / total);
                String texto = values[i] + " (" + porcentaje + ")";

                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.drawString(texto, getWidth() - 105, itemY);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            }

            g2.dispose();
        }
    }

    private static class BarChartPanel extends RoundedPanel {

        private final String titulo;
        private String[] labels;
        private double[] ingresos;
        private double[] gastos;

        public BarChartPanel(String titulo, String[] labels, double[] ingresos, double[] gastos) {
            super(Color.WHITE, 12);
            this.titulo = titulo;
            this.labels = labels;
            this.ingresos = ingresos;
            this.gastos = gastos;

            setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDE, 12),
                    new EmptyBorder(12, 14, 12, 14)
            ));
        }

        public void setData(String[] labels, double[] ingresos, double[] gastos) {
            this.labels = labels;
            this.ingresos = ingresos;
            this.gastos = gastos;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = preparar(g);

            int left = 56;
            int right = 18;
            int top = 45;
            int bottom = 30;
            int w = getWidth() - left - right;
            int h = getHeight() - top - bottom;

            g2.setColor(TEXTO);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            g2.drawString(titulo, 14, 24);

            double max = Math.max(obtenerMaximo(ingresos), obtenerMaximo(gastos));
            max = redondearMaximo(max);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));

            for (int i = 0; i <= 4; i++) {
                int y = top + (h * i / 4);

                g2.setColor(new Color(225, 229, 236));
                g2.drawLine(left, y, left + w, y);

                g2.setColor(new Color(145, 153, 166));
                double valor = max - (max * i / 4);
                g2.drawString(formatoCorto(valor), 8, y + 4);
            }

            int grupos = labels.length;
            int grupoW = w / grupos;
            int barW = Math.max(12, grupoW / 5);

            for (int i = 0; i < grupos; i++) {
                int baseX = left + i * grupoW + grupoW / 2;
                int baseY = top + h;

                int ingresoH = (int) ((ingresos[i] / max) * h);
                int gastoH = (int) ((gastos[i] / max) * h);

                g2.setColor(VERDE);
                g2.fillRoundRect(baseX - barW - 3, baseY - ingresoH, barW, ingresoH, 5, 5);

                g2.setColor(ROJO);
                g2.fillRoundRect(baseX + 3, baseY - gastoH, barW, gastoH, 5, 5);

                g2.setColor(new Color(145, 153, 166));
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));

                String label = labels[i];
                int textW = g2.getFontMetrics().stringWidth(label);
                g2.drawString(label, baseX - textW / 2, baseY + 20);
            }

            dibujarLeyenda(g2, getWidth() - 175, 18);

            g2.dispose();
        }

        private void dibujarLeyenda(Graphics2D g2, int x, int y) {
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));

            g2.setColor(VERDE);
            g2.fillRoundRect(x, y, 12, 8, 4, 4);
            g2.setColor(TEXTO_SUAVE);
            g2.drawString("Ingresos", x + 17, y + 8);

            g2.setColor(ROJO);
            g2.fillRoundRect(x + 83, y, 12, 8, 4, 4);
            g2.setColor(TEXTO_SUAVE);
            g2.drawString("Gastos", x + 100, y + 8);
        }
    }

    private static class RoundedPanel extends JPanel {

        private final Color fondo;
        private final int arc;

        public RoundedPanel(Color fondo, int arc) {
            this.fondo = fondo;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = preparar(g);
            g2.setColor(fondo);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();

            super.paintComponent(g);
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
            Graphics2D g2 = preparar(g);
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

    private static Graphics2D preparar(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }

    private static double obtenerMaximo(double[] datos) {
        double max = datos.length > 0 ? datos[0] : 0;

        for (double dato : datos) {
            if (dato > max) {
                max = dato;
            }
        }

        return max;
    }

    private static double obtenerMinimo(double[] datos) {
        double min = datos.length > 0 ? datos[0] : 0;

        for (double dato : datos) {
            if (dato < min) {
                min = dato;
            }
        }

        return min;
    }

    private static double redondearMaximo(double max) {
        if (max <= 0) {
            return 1000;
        }

        if (max <= 1000) {
            return 1000;
        }

        if (max <= 2000) {
            return 2000;
        }

        if (max <= 3000) {
            return 3000;
        }

        if (max <= 4000) {
            return 4000;
        }

        if (max <= 5000) {
            return 5000;
        }

        return Math.ceil(max / 1000.0) * 1000;
    }

    private static String formatoCorto(double valor) {
        double abs = Math.abs(valor);
        String signo = valor < 0 ? "-" : "";

        if (abs >= 1000) {
            return signo + String.format("%.0fk", abs / 1000.0);
        }

        return String.valueOf((int) valor);
    }
}