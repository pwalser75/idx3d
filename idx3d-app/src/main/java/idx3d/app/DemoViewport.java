package idx3d.app;

import idx3d.demos.DemoCatalog;
import idx3d.idx3d_DemoPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * Right-hand pane: a header describing the selected demo and the host area in
 * which the demo panel runs.
 */
final class DemoViewport extends JPanel {

    private final JPanel host = new JPanel(new BorderLayout());
    private final JLabel title = new JLabel();
    private final JLabel description = new JLabel();
    private final JLabel badge = new JLabel();

    private idx3d_DemoPanel current;

    DemoViewport() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(18, 18, 18, 18));

        add(buildInfo(), BorderLayout.NORTH);

        host.setBackground(Color.BLACK);
        host.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        add(host, BorderLayout.CENTER);
    }

    private JPanel buildInfo() {
        JPanel info = new JPanel(new BorderLayout(16, 0));
        info.setOpaque(false);
        info.setBorder(new EmptyBorder(0, 4, 16, 4));

        title.setFont(Theme.sans(Font.BOLD, 22));
        title.setForeground(Theme.TEXT);
        description.setFont(Theme.sans(Font.PLAIN, 12.5f));
        description.setForeground(Theme.TEXT_DIM);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(description);

        badge.setFont(Theme.mono(Font.BOLD, 11));
        badge.setForeground(Theme.ACCENT);
        badge.setBackground(Theme.SURFACE_2);
        badge.setOpaque(true);
        badge.setBorder(new EmptyBorder(5, 10, 5, 10));
        badge.setVerticalAlignment(JLabel.TOP);

        info.add(text, BorderLayout.CENTER);
        info.add(badge, BorderLayout.EAST);
        return info;
    }

    void show(DemoCatalog.DemoInfo demo) {
        stopCurrent();
        host.removeAll();

        current = demo.create();
        host.add(current, BorderLayout.CENTER);
        title.setText(demo.title());
        description.setText(demo.description());
        badge.setText(demo.id().toUpperCase());

        host.revalidate();
        host.repaint();
        SwingUtilities.invokeLater(() -> {
            current.requestFocusInWindow();
            current.start();
        });
    }

    void stopCurrent() {
        if (current != null) {
            current.stop();
            current = null;
        }
    }
}
