// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | Global animation time-scale used for smooth slow motion.
// | -----------------------------------------------------------------

package idx3d;

/**
 * Holds the scale factor applied to per-frame animation deltas. While a demo's
 * {@code runtime()} is executing, {@link idx3d_DemoPanel} sets this to a value
 * below 1 (default 0.1) so transforms advance smoothly but slowly instead of
 * skipping frames. Outside animation it is 1, so scene setup, rendering and
 * user interaction are unaffected.
 *
 * <p>All access happens on the Swing event dispatch thread, so a simple static
 * field is sufficient.</p>
 */
public final class idx3d_Animation {

    private static float scale = 1f;

    private idx3d_Animation() {
    }

    public static void setScale(float scale) {
        idx3d_Animation.scale = scale;
    }

    public static float getScale() {
        return scale;
    }

    /** Scales a per-frame animation delta by the current factor. */
    public static float scale(float delta) {
        return delta * scale;
    }
}
