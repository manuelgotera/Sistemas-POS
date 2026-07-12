package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestHistorial {

    public static void main(String[] args) {
        
        FlatLightLaf.setup();

        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);
        
        UIManager.put("TableHeader.separatorColor", new java.awt.Color(225, 228, 233));

        /*
         * Lanzamiento de la interfaz
         */
        SwingUtilities.invokeLater(() -> {
            HistorialTransaccionesFrame frame = new HistorialTransaccionesFrame();
            frame.setVisible(true);
        });
    }
}