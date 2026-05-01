package proyecto.pos.gui;

// Librería FlatLaf para darle un estilo moderno a Swing
import com.formdev.flatlaf.FlatLightLaf;

// Utilidades de Swing
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Clase de prueba para ejecutar únicamente el módulo Artículos y Stock.
 *
 * Esta clase sirve para trabajar el frontend de manera aislada,
 * sin modificar todavía el main oficial del proyecto.
 */
public class TestArticulosStock {

    public static void main(String[] args) {

        /*
         * Activa FlatLaf.
         * Esto cambia el aspecto visual de Swing para que se vea más moderno.
         */
        FlatLightLaf.setup();

        /*
         * Configuraciones visuales adicionales.
         * FlatLaf permite redondear bordes de botones, campos y componentes.
         */
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 14);
        UIManager.put("TextComponent.arc", 14);

        /*
         * SwingUtilities.invokeLater asegura que la interfaz se cree
         * en el hilo correcto de Swing.
         */
        SwingUtilities.invokeLater(() -> {
            new ArticulosStockFrame().setVisible(true);
        });
    }
}