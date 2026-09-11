package idx3d.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Central dark "tech" theme: palette, typography and small helpers shared by the
 * launcher UI.
 */
public final class Theme {

    // -- palette -----------------------------------------------------------

    public static final Color BG = new Color(0x0D1117);
    public static final Color SURFACE = new Color(0x161B22);
    public static final Color SURFACE_2 = new Color(0x1C2430);
    public static final Color BORDER = new Color(0x21262D);
    public static final Color ACCENT = new Color(0x22D3EE);
    public static final Color ACCENT_DIM = new Color(0x0E7490);
    public static final Color TEXT = new Color(0xE6EDF3);
    public static final Color TEXT_DIM = new Color(0x8B949E);
    public static final Color TEXT_FAINT = new Color(0x6E7681);
    public static final Color SELECTED = new Color(0x0F2A33);
    public static final Color HOVER = new Color(0x1B222C);

    // -- typography --------------------------------------------------------

    public static final Font FONT_SANS = pick("Inter", "Segoe UI", "SF Pro Display",
            "Helvetica Neue", "Cantarell", "SansSerif");
    public static final Font FONT_MONO = pick("JetBrains Mono", "Fira Code", "Cascadia Code",
            "Consolas", "Menlo", "DejaVu Sans Mono", "Monospaced");

    private Theme() {
    }

    public static Font sans(int style, float size) {
        return FONT_SANS.deriveFont(style, size);
    }

    public static Font mono(int style, float size) {
        return FONT_MONO.deriveFont(style, size);
    }

    private static Font pick(String... names) {
        try {
            Set<String> available = new HashSet<>(Arrays.asList(
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
            for (String name : names) {
                if (available.contains(name)) {
                    return new Font(name, Font.PLAIN, 14);
                }
            }
        } catch (Throwable ignored) {
            // headless or restricted environment
        }
        return new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    }
}
