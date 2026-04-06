import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WeatherApp {

    public static void main(String[] args) {
        // Use the system's native look and feel (Windows / macOS / Linux)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Falls back to default Java Swing look — that's fine
        }

        // All Swing UI must be created on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(WeatherGUI::new);
    }
}
