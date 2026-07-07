//package proyecto.pos.gui;
//
//import javafx.application.Platform;
//import javafx.embed.swing.JFXPanel;
//import javafx.scene.Scene;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import javax.swing.*;
//import java.awt.*;
//import java.io.File;
//
//public class DashboardFrame extends JFrame {
//
//    private JPanel panelPrincipal;
//    private JButton btnDashboard;
//    private JFXPanel jfxPanel; // Este es el contenedor para el gráfico
//
//    public DashboardFrame() {
//        inicializarComponentes();
//    }
//
//    private void inicializarComponentes() {
//        setTitle("Sistema POS");
//        setSize(1000, 700);
//        setLocationRelativeTo(null);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        panelPrincipal = new JPanel(new BorderLayout());
//        
//        btnDashboard = new JButton("Abrir Dashboard");
//        btnDashboard.addActionListener(evt -> abrirDashboard());
//
//        panelPrincipal.add(btnDashboard, BorderLayout.NORTH);
//        
//        // Inicializamos el panel de JavaFX
//        jfxPanel = new JFXPanel();
//        panelPrincipal.add(jfxPanel, BorderLayout.CENTER);
//
//        add(panelPrincipal);
//    }
//
//    private void abrirDashboard() {
//        File archivoDashboard = new File("C:\\Users\\User\\Downloads\\dashboard.html");
//        
//        if (!archivoDashboard.exists()) {
//            JOptionPane.showMessageDialog(this, "No se encontró dashboard.html", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        // JavaFX debe correr en su propio hilo
//        Platform.runLater(() -> {
//            WebView webView = new WebView();
//            WebEngine webEngine = webView.getEngine();
//            webEngine.load(archivoDashboard.toURI().toASCIIString());
//            
//            Scene scene = new Scene(webView);
//            jfxPanel.setScene(scene);
//        });
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> new DashboardFrame().setVisible(true));
//    }
//}