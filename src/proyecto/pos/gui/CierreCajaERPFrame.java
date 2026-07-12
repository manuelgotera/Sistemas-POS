package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import proyecto.pos.config.DatabaseConnection;
import proyecto.pos.dao.impl.CajaDAOImpl;
import proyecto.pos.dao.interfaces.CajaDAO;
import proyecto.pos.model.AsientoContable;
import proyecto.pos.model.Caja;
import proyecto.pos.model.VentaResumenCaja;
import proyecto.pos.service.CajaService;
import proyecto.pos.service.ErpSincronizacionService;

/**
 * HU-07 — Cierre de Caja con Sincronización Contable (ERP)
 * Criterio 1: Sincronización automática al cierre del día.
 * Criterio 2: Alerta por fallos de conectividad.
 * Criterio 3: Registro automático en el asiento contable general.
 * Criterio 4: Reporte de discrepancias.
 *
 * Arquitectura: Frame → CajaService (cierre) + ErpSincronizacionService (asiento)
 * → DAOs. Mismo patrón usado en AjusteMasivoFrame y AnulacionVentaFrame.
 */
public class CierreCajaERPFrame extends JFrame {

    // ─── PALETA (misma que el resto del proyecto) ─────────────────────────────
    private static final Color AZUL        = new Color(26, 83, 160);
    private static final Color AZUL_HOVER  = new Color(18, 65, 128);
    private static final Color AZUL_CLARO  = new Color(232, 241, 255);
    private static final Color FONDO       = new Color(246, 248, 251);
    private static final Color BORDE       = new Color(225, 229, 236);
    private static final Color TEXTO       = new Color(30, 37, 48);
    private static final Color TEXTO_SUAVE = new Color(105, 113, 128);
    private static final Color VERDE       = new Color(40, 167, 69);
    private static final Color VERDE_BG    = new Color(225, 245, 238);
    private static final Color VERDE_TEXT  = new Color(15, 110, 86);
    private static final Color ROJO        = new Color(220, 53, 69);
    private static final Color ROJO_BG     = new Color(255, 235, 238);
    private static final Color AMARILLO_BG = new Color(255, 249, 219);
    private static final Color AMARILLO_TX = new Color(133, 100, 4);

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // ─── COMPONENTES ──────────────────────────────────────────────────────────
    private JTable            tablaVentas;
    private DefaultTableModel modeloTabla;
    private JLabel            lblFooter;

    private JLabel lblStatCajaId, lblStatApertura, lblStatTotalVentas, lblStatEstado;
    private JLabel lblMontoInicialInfo, lblTotalVentasInfo;
    private JSpinner  spinMontoFinal;
    private JCheckBox chkSimularFallo;
    private JLabel     lblResultado;
    private JLabel     lblEsperadoVal, lblRealVal, lblDiferenciaVal;
    private JButton    btnSincronizar;

    private Connection conexion;
    private CajaDAO                  cajaDAO;
    private CajaService              cajaService;
    private ErpSincronizacionService erpService;
    private Caja cajaAbierta;

    public CierreCajaERPFrame() {
        FlatLightLaf.setup();
        conexion = new DatabaseConnection().conectar();
        cajaDAO     = new CajaDAOImpl(conexion);
        cajaService = new CajaService(cajaDAO);
        erpService  = new ErpSincronizacionService(conexion);
        configurarVentana();
        construirInterfaz();
        cargarCajaAbierta();
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void configurarVentana() {
        setTitle("Cierre de Caja — Sincronización ERP");
        setSize(1280, 770);
        setMinimumSize(new Dimension(1100, 660));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // ──────────────────────────────────────────────────────────────────────────
    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(FONDO);
        setContentPane(root);

        root.add(new MenuSidebar(this, "CierreERP"), BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setBackground(FONDO);
        centro.setBorder(new EmptyBorder(24, 28, 20, 28));
        centro.add(crearHeader(),  BorderLayout.NORTH);
        centro.add(crearCuerpo(), BorderLayout.CENTER);

        root.add(centro, BorderLayout.CENTER);
    }

    // ── HEADER ────────────────────────────────────────────────────────────────
    private JPanel crearHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 18, 0));

        JPanel titulos = new JPanel();
        titulos.setOpaque(false);
        titulos.setLayout(new BoxLayout(titulos, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Cierre de Caja — Módulo Contable (ERP)");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(TEXTO);

        JLabel sub = new JLabel("Sincronización automática, validación de conectividad y registro contable");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        sub.setForeground(TEXTO_SUAVE);

        titulos.add(titulo);
        titulos.add(Box.createVerticalStrut(3));
        titulos.add(sub);

        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        der.setOpaque(false);
        JLabel lblU = new JLabel("<html><b>uwu fernandez</b><br><font color='gray'>Gerente</font></html>");
        lblU.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        der.add(lblU);

        p.add(titulos, BorderLayout.WEST);
        p.add(der,     BorderLayout.EAST);
        return p;
    }

    // ── CUERPO ────────────────────────────────────────────────────────────────
    private JPanel crearCuerpo() {
        JPanel p = new JPanel(new BorderLayout(14, 0));
        p.setOpaque(false);
        p.add(crearPanelIzquierdo(), BorderLayout.CENTER);
        p.add(crearPanelDerecho(),   BorderLayout.EAST);
        return p;
    }

    // ── PANEL IZQUIERDO ───────────────────────────────────────────────────────
    private JPanel crearPanelIzquierdo() {
        JPanel contenedor = new JPanel(new BorderLayout(0, 14));
        contenedor.setOpaque(false);
        contenedor.add(crearFilaStats(), BorderLayout.NORTH);
        contenedor.add(crearCardTabla(), BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearFilaStats() {
        JPanel fila = new JPanel(new GridLayout(1, 4, 12, 0));
        fila.setOpaque(false);

        lblStatCajaId      = crearStatCard(fila, "ID Caja",       "—", AZUL);
        lblStatApertura     = crearStatCard(fila, "Apertura",      "—", TEXTO);
        lblStatTotalVentas  = crearStatCard(fila, "Total Ventas",  "S/. 0.00", VERDE_TEXT);
        lblStatEstado       = crearStatCard(fila, "Estado Caja",   "—", TEXTO_SUAVE);

        return fila;
    }

    private JLabel crearStatCard(JPanel contenedor, String titulo, String valorInicial, Color colorValor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 12),
                new EmptyBorder(12, 14, 12, 14)));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(TEXTO_SUAVE);
        lblTit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblVal = new JLabel(valorInicial);
        lblVal.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lblVal.setForeground(colorValor);
        lblVal.setAlignmentX(LEFT_ALIGNMENT);
        lblVal.setBorder(new EmptyBorder(4, 0, 0, 0));

        card.add(lblTit);
        card.add(lblVal);
        contenedor.add(card);
        return lblVal;
    }

    private JPanel crearCardTabla() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 16, 14, 16)));

        JLabel lblTit = new JLabel("Ventas del día — detalle por transacción");
        lblTit.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTit.setForeground(TEXTO);
        lblTit.setBorder(new EmptyBorder(0, 0, 12, 0));
        card.add(lblTit, BorderLayout.NORTH);

        String[] cols = {"ID Venta", "Fecha/Hora", "Cliente", "Total (S/.)"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setRowHeight(36);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.setShowVerticalLines(false);
        tablaVentas.setGridColor(new Color(235, 238, 244));
        tablaVentas.setSelectionBackground(AZUL_CLARO);

        JTableHeader th = tablaVentas.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(th.getPreferredSize().width, 36));

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tablaVentas.getColumnModel().getColumn(0).setPreferredWidth(70);
        tablaVentas.getColumnModel().getColumn(0).setCellRenderer(centro);

        DefaultTableCellRenderer derecha = new DefaultTableCellRenderer();
        derecha.setHorizontalAlignment(SwingConstants.RIGHT);
        tablaVentas.getColumnModel().getColumn(3).setCellRenderer(derecha);

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBorder(new MenuSidebar.RoundedBorder(BORDE, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        card.add(scroll, BorderLayout.CENTER);

        lblFooter = new JLabel("Cargando...");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(TEXTO_SUAVE);
        lblFooter.setBorder(new EmptyBorder(8, 0, 0, 0));
        card.add(lblFooter, BorderLayout.SOUTH);

        return card;
    }

    // ── PANEL DERECHO ─────────────────────────────────────────────────────────
    private JPanel crearPanelDerecho() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(320, 0));

        panel.add(crearCardSincronizacion());
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel crearCardSincronizacion() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                new MenuSidebar.RoundedBorder(BORDE, 14),
                new EmptyBorder(16, 18, 18, 18)));
        card.setMaximumSize(new Dimension(320, 999));

        JLabel tit = new JLabel("Cierre y sincronización ERP");
        tit.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tit.setForeground(TEXTO);
        tit.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Cierre automático con validación contable");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXTO_SUAVE);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        card.add(tit);
        card.add(Box.createVerticalStrut(2));
        card.add(sub);
        card.add(Box.createVerticalStrut(16));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(14));

        lblMontoInicialInfo = crearFilaDetalle(card, "Monto inicial:", "S/. 0.00");
        lblTotalVentasInfo  = crearFilaDetalle(card, "Ventas pagadas:", "S/. 0.00");

        card.add(Box.createVerticalStrut(16));

        // Monto final (arqueo)
        JLabel lblArqueo = new JLabel("Monto final de arqueo (S/.)");
        lblArqueo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblArqueo.setForeground(TEXTO_SUAVE);
        lblArqueo.setAlignmentX(LEFT_ALIGNMENT);

        SpinnerNumberModel modeloSpinner = new SpinnerNumberModel(0.0, 0.0, 999999.0, 1.0);
        spinMontoFinal = new JSpinner(modeloSpinner);
        spinMontoFinal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        spinMontoFinal.setMaximumSize(new Dimension(999, 36));
        spinMontoFinal.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lblArqueo);
        card.add(Box.createVerticalStrut(6));
        card.add(spinMontoFinal);
        card.add(Box.createVerticalStrut(10));

        // Simulación de fallo (para pruebas/demo del Criterio 2)
        chkSimularFallo = new JCheckBox("Simular fallo de conexión (modo prueba)");
        chkSimularFallo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        chkSimularFallo.setForeground(TEXTO_SUAVE);
        chkSimularFallo.setOpaque(false);
        chkSimularFallo.setAlignmentX(LEFT_ALIGNMENT);
        chkSimularFallo.setFocusPainted(false);
        card.add(chkSimularFallo);

        card.add(Box.createVerticalStrut(16));
        card.add(crearSeparador());
        card.add(Box.createVerticalStrut(14));

        // Reporte de discrepancias (Criterio 4) — se llena tras sincronizar
        JLabel lblRepTit = new JLabel("Reporte de discrepancias");
        lblRepTit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRepTit.setForeground(TEXTO);
        lblRepTit.setAlignmentX(LEFT_ALIGNMENT);
        card.add(lblRepTit);
        card.add(Box.createVerticalStrut(8));

        lblEsperadoVal   = crearFilaDetalle(card, "Esperado:",    "—");
        lblRealVal       = crearFilaDetalle(card, "Real (arqueo):", "—");
        lblDiferenciaVal = crearFilaDetalle(card, "Diferencia:",  "—");

        card.add(Box.createVerticalStrut(14));

        lblResultado = new JLabel("Caja aún no sincronizada");
        lblResultado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblResultado.setForeground(TEXTO_SUAVE);
        lblResultado.setBackground(new Color(240, 242, 245));
        lblResultado.setOpaque(true);
        lblResultado.setBorder(new EmptyBorder(6, 12, 6, 12));
        lblResultado.setAlignmentX(LEFT_ALIGNMENT);
        lblResultado.setMaximumSize(new Dimension(999, 999));
        card.add(lblResultado);

        card.add(Box.createVerticalStrut(20));

        btnSincronizar = new JButton("🔄 Cerrar Caja y Sincronizar con ERP");
        btnSincronizar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSincronizar.setForeground(Color.WHITE);
        btnSincronizar.setBackground(AZUL);
        btnSincronizar.setBorder(new EmptyBorder(10, 14, 10, 14));
        btnSincronizar.setFocusPainted(false);
        btnSincronizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSincronizar.setMaximumSize(new Dimension(999, 46));
        btnSincronizar.setAlignmentX(LEFT_ALIGNMENT);
        btnSincronizar.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { if (btnSincronizar.isEnabled()) btnSincronizar.setBackground(AZUL_HOVER); }
            public void mouseExited (MouseEvent e) { if (btnSincronizar.isEnabled()) btnSincronizar.setBackground(AZUL); }
        });
        btnSincronizar.addActionListener(e -> ejecutarCierreYSincronizacion());
        card.add(btnSincronizar);

        return card;
    }

    private JLabel crearFilaDetalle(JPanel card, String etiqueta, String valorInicial) {
        JPanel fila = new JPanel(new BorderLayout(6, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(LEFT_ALIGNMENT);
        fila.setMaximumSize(new Dimension(999, 22));
        fila.setBorder(new EmptyBorder(2, 0, 2, 0));

        JLabel lEtiq = new JLabel(etiqueta);
        lEtiq.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lEtiq.setForeground(TEXTO_SUAVE);
        lEtiq.setPreferredSize(new Dimension(100, 18));

        JLabel lVal = new JLabel(valorInicial);
        lVal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lVal.setForeground(TEXTO);
        lVal.setHorizontalAlignment(SwingConstants.RIGHT);

        fila.add(lEtiq, BorderLayout.WEST);
        fila.add(lVal,  BorderLayout.CENTER);
        card.add(fila);
        return lVal;
    }

    private JSeparator crearSeparador() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(999, 1));
        sep.setForeground(BORDE);
        return sep;
    }

    // ── CARGA DE DATOS ────────────────────────────────────────────────────────
    private void cargarCajaAbierta() {
        try {
            cajaAbierta = cajaDAO.obtenerCajaAbierta();
        } catch (Exception ex) {
            cajaAbierta = null;
        }

        if (cajaAbierta == null) {
            lblStatCajaId.setText("—");
            lblStatApertura.setText("—");
            lblStatTotalVentas.setText("S/. 0.00");
            lblStatEstado.setText("SIN CAJA ABIERTA");
            lblStatEstado.setForeground(ROJO);
            lblMontoInicialInfo.setText("S/. 0.00");
            lblTotalVentasInfo.setText("S/. 0.00");
            btnSincronizar.setEnabled(false);
            spinMontoFinal.setEnabled(false);
            lblFooter.setText("No hay una caja abierta actualmente.");
            modeloTabla.setRowCount(0);
            return;
        }

        double totalVentas = erpService.obtenerTotalVentasPagadas(cajaAbierta.getCajaId());

        lblStatCajaId.setText("#" + cajaAbierta.getCajaId());
        lblStatApertura.setText(cajaAbierta.getFecha_apertura() != null ? SDF.format(cajaAbierta.getFecha_apertura()) : "—");
        lblStatTotalVentas.setText(String.format("S/. %.2f", totalVentas));
        lblStatEstado.setText(cajaAbierta.getEstado());
        lblStatEstado.setForeground(VERDE_TEXT);

        lblMontoInicialInfo.setText(String.format("S/. %.2f", cajaAbierta.getMonto_inicial()));
        lblTotalVentasInfo.setText(String.format("S/. %.2f", totalVentas));

        btnSincronizar.setEnabled(true);
        spinMontoFinal.setEnabled(true);

        cargarVentasDeCaja(cajaAbierta.getCajaId());
    }

    private void cargarVentasDeCaja(int cajaId) {
        modeloTabla.setRowCount(0);
        List<VentaResumenCaja> ventas = erpService.listarVentasDeCaja(cajaId);
        for (VentaResumenCaja v : ventas) {
            modeloTabla.addRow(new Object[]{
                v.getVentaId(),
                v.getFechaHora() != null ? SDF.format(v.getFechaHora()) : "—",
                v.getClienteNombre(),
                String.format("%.2f", v.getTotal())
            });
        }
        lblFooter.setText("Mostrando " + ventas.size() + " venta(s) pagada(s) de la Caja #" + cajaId);
    }

    // ── EJECUCIÓN (HU-07) ────────────────────────────────────────────────────
    private void ejecutarCierreYSincronizacion() {
        if (cajaAbierta == null) return;

        double montoFinal = ((Number) spinMontoFinal.getValue()).doubleValue();
        boolean simularFallo = chkSimularFallo.isSelected();

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><b>¿Cerrar la Caja #" + cajaAbierta.getCajaId() + " y sincronizar con el ERP?</b><br><br>"
                + "<small>Monto final de arqueo: <b>S/. " + String.format("%.2f", montoFinal) + "</b><br><br>"
                + "<font color='red'>El cierre de caja no se puede deshacer.</font></small></html>",
                "Confirmar Cierre de Caja",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        btnSincronizar.setEnabled(false);
        btnSincronizar.setText("⏳ Sincronizando...");

        int cajaId = cajaAbierta.getCajaId();
        double montoInicial = cajaAbierta.getMonto_inicial();

        SwingWorker<AsientoContable, Void> worker = new SwingWorker<>() {
            @Override
            protected AsientoContable doInBackground() throws Exception {
                // 1) Cierre real de caja (cambia estado a CERRADA en caja_diaria)
                cajaService.cerrarCaja(montoFinal);
                // 2) Sincronización contable con el ERP (simulado)
                return erpService.sincronizarCierre(cajaId, montoInicial, montoFinal, simularFallo);
            }

            @Override
            protected void done() {
                try {
                    AsientoContable asiento = get();
                    mostrarReporteDiscrepancias(asiento);

                    boolean conDiferencia = "SINCRONIZADO_CON_DIFERENCIA".equals(asiento.getEstadoSincronizacion());
                    mostrarResultado(true, conDiferencia,
                            asiento.getMensaje() + "\nCaja #" + cajaId + " cerrada correctamente.");

                } catch (Exception ex) {
                    Throwable causa = ex.getCause() != null ? ex.getCause() : ex;

                    if (causa instanceof ErpSincronizacionService.ErpConexionException) {
                        // Criterio 2: la caja YA se cerró localmente; solo falló el envío al ERP.
                        lblResultado.setText("⚠ Caja cerrada, pero sin sincronizar con el ERP");
                        lblResultado.setBackground(AMARILLO_BG);
                        lblResultado.setForeground(AMARILLO_TX);
                        mostrarResultado(false, false, causa.getMessage()
                                + "\nLa caja se cerró localmente. Reintente la sincronización más tarde.");
                    } else {
                        mostrarResultado(false, false, "Error inesperado: " + causa.getMessage());
                    }
                } finally {
                    btnSincronizar.setText("🔄 Cerrar Caja y Sincronizar con ERP");
                    cargarCajaAbierta(); // refresca stats/tabla (ya no habrá caja abierta si todo salió bien)
                }
            }
        };
        worker.execute();
    }

    private void mostrarReporteDiscrepancias(AsientoContable asiento) {
        lblEsperadoVal.setText(String.format("S/. %.2f", asiento.getMontoEsperado()));
        lblRealVal.setText(String.format("S/. %.2f", asiento.getMontoFinal()));

        double dif = asiento.getDiferencia();
        String signo = dif >= 0 ? "+" : "";
        lblDiferenciaVal.setText(signo + String.format("S/. %.2f", dif));
        lblDiferenciaVal.setForeground(Math.abs(dif) <= 0.01 ? VERDE_TEXT : ROJO);

        if ("SINCRONIZADO".equals(asiento.getEstadoSincronizacion())) {
            lblResultado.setText("✔ Sincronizado — Caja cuadrada");
            lblResultado.setBackground(VERDE_BG);
            lblResultado.setForeground(VERDE_TEXT);
        } else {
            lblResultado.setText("⚠ Sincronizado con discrepancia");
            lblResultado.setBackground(AMARILLO_BG);
            lblResultado.setForeground(AMARILLO_TX);
        }
    }

    private void mostrarResultado(boolean exito, boolean conAdvertencia, String mensaje) {
        Color bg = exito ? (conAdvertencia ? AMARILLO_BG : VERDE_BG) : ROJO_BG;
        Color fg = exito ? (conAdvertencia ? AMARILLO_TX : VERDE_TEXT) : ROJO;
        String icono = exito ? (conAdvertencia ? "⚠️" : "✅") : "❌";
        String tituloDialogo = exito
                ? (conAdvertencia ? "Sincronizado con discrepancia" : "Sincronización exitosa")
                : "Falla de sincronización";

        JPanel msgPanel = new JPanel(new BorderLayout(10, 0));
        msgPanel.setBackground(bg);
        msgPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel iconoLbl = new JLabel(icono);
        iconoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 22));

        JLabel msg = new JLabel("<html><b>" + tituloDialogo + "</b><br>"
                + "<font style='font-size:11px;'>" + mensaje.replace("\n", "<br>") + "</font></html>");
        msg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        msg.setForeground(fg);

        msgPanel.add(iconoLbl, BorderLayout.WEST);
        msgPanel.add(msg,      BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, msgPanel, tituloDialogo,
                exito ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CierreCajaERPFrame().setVisible(true));
    }
}