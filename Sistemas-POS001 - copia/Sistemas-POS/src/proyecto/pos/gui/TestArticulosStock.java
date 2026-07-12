    package proyecto.pos.gui;

import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestArticulosStock {

    public static void main(String[] args) {
        configurarVisual();

        SwingUtilities.invokeLater(() -> {
            ArticulosStockFrame frame = new ArticulosStockFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static void configurarVisual() {
        activarSuavizadoTexto();
        aplicarFlatLaf();
        aplicarFuenteGeneral();
        aplicarPropiedadesVisuales();
    }

    private static void activarSuavizadoTexto() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
    }

    private static void aplicarFlatLaf() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("No se pudo aplicar LookAndFeel.");
            }
        }
    }

    private static void aplicarFuenteGeneral() {
        Font fuente = new Font("Segoe UI", Font.PLAIN, 13);

        UIManager.put("defaultFont", fuente);
        UIManager.put("Label.font", fuente);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("TextField.font", fuente);
        UIManager.put("ComboBox.font", fuente);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 12));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 12));
        UIManager.put("OptionPane.messageFont", fuente);
        UIManager.put("OptionPane.buttonFont", new Font("Segoe UI", Font.BOLD, 12));
    }

    private static void aplicarPropiedadesVisuales() {
        UIManager.put("Button.arc", 10);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("ComboBox.arc", 10);

        UIManager.put("Button.innerFocusWidth", 0);
        UIManager.put("Component.focusWidth", 1);
        UIManager.put("Table.showHorizontalLines", true);
        UIManager.put("Table.showVerticalLines", false);
    }
}