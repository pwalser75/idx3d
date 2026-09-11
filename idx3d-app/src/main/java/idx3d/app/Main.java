package idx3d.app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point for the idx3d demo launcher. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // keep the default look and feel
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
