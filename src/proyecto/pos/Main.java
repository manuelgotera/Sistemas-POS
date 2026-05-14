package proyecto.pos;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import proyecto.pos.gui.Caja_GUI;

public class Main {
    public static void main(String[] args) {
        // Configura el tema visual
        FlatLightLaf.setup();

        // Llama a la interfaz directamente
        SwingUtilities.invokeLater(() -> {
            Caja_GUI caja = new Caja_GUI();
            caja.setVisible(true);
        });
    }
}