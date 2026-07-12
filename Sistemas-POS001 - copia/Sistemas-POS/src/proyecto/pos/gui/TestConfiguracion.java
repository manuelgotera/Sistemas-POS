package proyecto.pos.gui;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class TestConfiguracion {

    public static void main(String[] args) {

        FlatLightLaf.setup();

        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);

        SwingUtilities.invokeLater(() -> {
            new ConfiguracionFrame().setVisible(true);
        });
    }
}
