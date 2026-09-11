package idx3d.app;

import idx3d.demos.DemoCatalog;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

/** Dark list cell used for the demo chooser. */
final class DemoCell extends JPanel implements ListCellRenderer<DemoCatalog.DemoInfo> {

    private final JLabel index = new JLabel();
    private final JLabel title = new JLabel();
    private final JLabel description = new JLabel();

    private boolean selected;
    private int hoverIndex = -1;
    private int cellIndex = -1;

    DemoCell() {
        setOpaque(false);
        setLayout(new BorderLayout(14, 0));
        setBorder(new EmptyBorder(11, 18, 11, 14));

        index.setFont(Theme.mono(Font.BOLD, 13));
        index.setForeground(Theme.TEXT_FAINT);
        index.setPreferredSize(new Dimension(26, 20));
        index.setVerticalAlignment(JLabel.TOP);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        title.setFont(Theme.sans(Font.BOLD, 15));
        title.setForeground(Theme.TEXT);
        description.setFont(Theme.sans(Font.PLAIN, 11.5f));
        description.setForeground(Theme.TEXT_DIM);

        text.add(title);
        text.add(Box.createVerticalStrut(3));
        text.add(description);

        add(index, BorderLayout.WEST);
        add(text, BorderLayout.CENTER);
    }

    void setHoverIndex(int hoverIndex) {
        this.hoverIndex = hoverIndex;
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends DemoCatalog.DemoInfo> list,
            DemoCatalog.DemoInfo value, int index, boolean isSelected, boolean cellHasFocus) {
        this.selected = isSelected;
        this.cellIndex = index;
        this.index.setText(String.format("%02d", index + 1));
        this.index.setForeground(isSelected ? Theme.ACCENT : Theme.TEXT_FAINT);
        this.title.setText(value.title());
        this.description.setText(value.description());
        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (selected) {
            g2.setColor(Theme.SELECTED);
        } else if (cellIndex == hoverIndex) {
            g2.setColor(Theme.HOVER);
        } else {
            g2.setColor(Theme.SURFACE);
        }
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setColor(Theme.BORDER);
        g2.drawLine(18, getHeight() - 1, getWidth(), getHeight() - 1);

        if (selected) {
            g2.setColor(Theme.ACCENT);
            g2.fillRoundRect(0, 12, 3, getHeight() - 24, 3, 3);
        }
        g2.dispose();
    }
}
