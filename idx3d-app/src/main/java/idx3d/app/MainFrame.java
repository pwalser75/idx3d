package idx3d.app;

import idx3d.demos.DemoCatalog;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** Main application window: dark header, demo sidebar (left) and viewport (right). */
final class MainFrame extends JFrame {

    private final DefaultListModel<DemoCatalog.DemoInfo> model = new DefaultListModel<>();
    private final DemoCell cellRenderer = new DemoCell();
    private final DemoViewport viewport = new DemoViewport();

    private final JList<DemoCatalog.DemoInfo> list;
    private int hoverIndex = -1;

    MainFrame() {
        super("idx3d III  -  Software 3D Engine");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setSize(1280, 800);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Theme.BG);
        setLayout(new BorderLayout());

        for (DemoCatalog.DemoInfo demo : DemoCatalog.all()) {
            model.addElement(demo);
        }

        list = new JList<>(model);
        list.setCellRenderer(cellRenderer);
        list.setFixedCellHeight(74);
        list.setBackground(Theme.SURFACE);
        list.setSelectionBackground(Theme.SELECTED);
        list.setSelectionForeground(Theme.TEXT);
        list.setBorder(null);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFocusable(true);
        installHover();

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedIndex() >= 0) {
                viewport.show(model.get(list.getSelectedIndex()));
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSplit(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        list.setSelectedIndex(0);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG);
        header.setBorder(new EmptyBorder(20, 24, 16, 24));

        JLabel brand = new JLabel(
                "<html><span style='color:#22D3EE'>idx3d</span> <span style='color:#E6EDF3'>III</span></html>");
        brand.setFont(Theme.sans(Font.BOLD, 26));

        JLabel subtitle = new JLabel("Software 3D engine  \u00b7  pure Java  \u00b7  " 
                + DemoCatalog.all().size() + " demos");
        subtitle.setFont(Theme.sans(Font.PLAIN, 11.5f));
        subtitle.setForeground(Theme.TEXT_DIM);

        JPanel brandBox = new JPanel();
        brandBox.setOpaque(false);
        brandBox.setLayout(new BoxLayout(brandBox, BoxLayout.Y_AXIS));
        brand.setAlignmentX(0f);
        subtitle.setAlignmentX(0f);
        brandBox.add(brand);
        brandBox.add(Box.createVerticalStrut(4));
        brandBox.add(subtitle);

        header.add(brandBox, BorderLayout.WEST);
        header.add(hairline(Theme.BORDER), BorderLayout.SOUTH);
        return header;
    }

    private JComponent buildSplit() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildSidebar(), viewport);
        split.setBorder(null);
        split.setDividerSize(1);
        split.setBackground(Theme.BORDER);
        split.setResizeWeight(0);
        split.setDividerLocation(320);
        return split;
    }

    private JComponent buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Theme.SURFACE);
        sidebar.setPreferredSize(new Dimension(320, 0));

        JPanel sectionHeader = new JPanel(new BorderLayout());
        sectionHeader.setOpaque(false);
        sectionHeader.setBorder(new EmptyBorder(16, 22, 12, 22));

        JLabel section = new JLabel("DEMOS");
        section.setFont(Theme.mono(Font.BOLD, 11));
        section.setForeground(Theme.TEXT_FAINT);

        JLabel count = new JLabel(String.format("%02d", DemoCatalog.all().size()));
        count.setFont(Theme.mono(Font.PLAIN, 11));
        count.setForeground(Theme.TEXT_FAINT);
        count.setHorizontalAlignment(SwingConstants.RIGHT);

        sectionHeader.add(section, BorderLayout.WEST);
        sectionHeader.add(count, BorderLayout.EAST);

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.setBackground(Theme.SURFACE);
        scroll.getViewport().setBackground(Theme.SURFACE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        DarkScrollBarUI.apply(scroll.getVerticalScrollBar());

        sidebar.add(sectionHeader, BorderLayout.NORTH);
        sidebar.add(scroll, BorderLayout.CENTER);
        return sidebar;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG);
        bar.setBorder(new EmptyBorder(10, 24, 12, 24));

        JLabel hints = new JLabel(
                "Drag rotate  \u00b7  Shift/right-drag pan  \u00b7  Arrows camera  \u00b7  + / - zoom  \u00b7  A antialias  \u00b7  S pause  \u00b7  I inspector");
        hints.setFont(Theme.sans(Font.PLAIN, 11));
        hints.setForeground(Theme.TEXT_FAINT);

        JLabel credit = new JLabel("(c) 1999/2000 Peter Walser");
        credit.setFont(Theme.sans(Font.PLAIN, 11));
        credit.setForeground(Theme.TEXT_FAINT);
        credit.setHorizontalAlignment(SwingConstants.RIGHT);

        bar.add(hints, BorderLayout.WEST);
        bar.add(credit, BorderLayout.EAST);
        return bar;
    }

    private JComponent hairline(java.awt.Color color) {
        JPanel line = new JPanel();
        line.setBackground(color);
        line.setPreferredSize(new Dimension(1, 1));
        return line;
    }

    private void installHover() {
        list.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0 && !list.getCellBounds(index, index).contains(e.getPoint())) {
                    index = -1;
                }
                if (index != hoverIndex) {
                    hoverIndex = index;
                    cellRenderer.setHoverIndex(index);
                    list.repaint();
                }
            }
        });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoverIndex = -1;
                cellRenderer.setHoverIndex(-1);
                list.repaint();
            }
        });
    }
}
