package idx3d.demos;

import idx3d.idx3d_DemoPanel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Registry of every ported demo. The launcher lists these in order and creates
 * a fresh {@link idx3d_DemoPanel} for the selected entry.
 */
public final class DemoCatalog {

    /** Immutable description of one demo. */
    public static final class DemoInfo {
        private final String id;
        private final String title;
        private final String description;
        private final Supplier<idx3d_DemoPanel> factory;

        DemoInfo(String id, String title, String description, Supplier<idx3d_DemoPanel> factory) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.factory = factory;
        }

        public String id() {
            return id;
        }

        public String title() {
            return title;
        }

        public String description() {
            return description;
        }

        public idx3d_DemoPanel create() {
            return factory.get();
        }
    }

    private static final List<DemoInfo> DEMOS;
    private static final Map<String, DemoInfo> BY_ID;

    static {
        List<DemoInfo> demos = new ArrayList<>();
        demos.add(new DemoInfo("demo1", "Torus & Wineglass",
                "Press 1 for wireframe, 2 for flatshading, 3 for phong shading, 4 for textured phong shading. Subdivide mesh by pressing 'm'",
                Demo01::new));
        demos.add(new DemoInfo("demo2", "Terrain",
                "Animated heightfield terrain.",
                Demo02::new));
        demos.add(new DemoInfo("stones", "Stones",
                "Stones with different textures",
                Demo03::new));
        demos.add(new DemoInfo("demo4", "Torus Knot",
                "Torus knot, seven material modes (press 1-7 to switch)",
                Demo04::new));
        demos.add(new DemoInfo("demo6", "Lens Flare",
                "Chrome mesh with a lens flare.",
                Demo06::new));
        demos.add(new DemoInfo("demo7", "Venus",
                "Cave and Venus, stone and flare.",
                Demo07::new));
        demos.add(new DemoInfo("demo8", "Mech",
                "Chrome-plated mech, auto-rotating.",
                Demo08::new));
        demos.add(new DemoInfo("demo10", "3D Links",
                "Interactive 3D link picking.",
                Demo10::new));
        demos.add(new DemoInfo("demo11", "Demon",
                "Glass demon under a sky dome.",
                Demo11::new));
        demos.add(new DemoInfo("demo12", "Torus Knot II",
                "Reflective torus knot.",
                Demo12::new));
        DEMOS = Collections.unmodifiableList(demos);

        Map<String, DemoInfo> byId = new LinkedHashMap<>();
        for (DemoInfo info : DEMOS) {
            byId.put(info.id(), info);
        }
        BY_ID = Collections.unmodifiableMap(byId);
    }

    private DemoCatalog() {
    }

    public static List<DemoInfo> all() {
        return DEMOS;
    }

    public static DemoInfo byId(String id) {
        return BY_ID.get(id);
    }

    public static DemoInfo first() {
        return DEMOS.get(0);
    }
}
