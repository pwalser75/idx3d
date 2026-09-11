// | -----------------------------------------------------------------
// | idx3d III is (c)1999/2000 by Peter Walser
// | Modernized resource loader (classpath-first) for Java 17.
// | -----------------------------------------------------------------

package idx3d;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Resolves demo assets (textures, materials, meshes) from the classpath first
 * (they live under {@code /assets/} inside the uber jar) and falls back to the
 * filesystem while running from a source checkout.
 *
 * <p>Because several legacy demos referenced assets with paths that were
 * relative to the wrong directory, a bare file name is also searched under the
 * well-known asset sub-folders.</p>
 */
public final class idx3d_Resources {

    private static final String ASSETS = "/assets/";
    private static final String[] FALLBACK_DIRS = { "textures/", "materials/", "meshes/" };

    private idx3d_Resources() {
    }

    /** Opens a resource, or returns {@code null} if it cannot be found. */
    public static InputStream open(String path) {
        if (path == null) {
            return null;
        }
        String p = path.replace('\\', '/');

        InputStream in = findClasspath(p);
        if (in != null) {
            return in;
        }
        for (String dir : FALLBACK_DIRS) {
            in = findClasspath(dir + basename(p));
            if (in != null) {
                return in;
            }
        }

        File f = new File(p);
        if (f.isFile()) {
            try {
                return new FileInputStream(f);
            } catch (IOException ignored) {
                // fall through
            }
        }
        f = new File("idx3d-demos/src/main/resources/assets/" + p);
        if (f.isFile()) {
            try {
                return new FileInputStream(f);
            } catch (IOException ignored) {
                // fall through
            }
        }
        System.err.println("idx3d: resource not found: " + path);
        return null;
    }

    private static InputStream findClasspath(String p) {
        String cp = p.startsWith("/") ? p : "/" + p;
        InputStream in = idx3d_Resources.class.getResourceAsStream(cp);
        if (in != null) {
            return in;
        }
        if (!cp.startsWith(ASSETS)) {
            in = idx3d_Resources.class.getResourceAsStream(ASSETS + cp.substring(1));
        }
        return in;
    }

    /** Loads an image as a {@link BufferedImage}, or returns {@code null} on failure. */
    public static BufferedImage image(String path) {
        try (InputStream in = open(path)) {
            if (in == null) {
                return null;
            }
            return ImageIO.read(in);
        } catch (IOException e) {
            System.err.println("idx3d: cannot load image " + path + ": " + e);
            return null;
        }
    }

    /** Loads a texture, falling back to a 1x1 blank texture. */
    public static idx3d_Texture texture(String path) {
        BufferedImage img = image(path);
        return img != null ? new idx3d_Texture(img) : new idx3d_Texture(1, 1);
    }

    /** Loads a {@code .material} file. */
    public static idx3d_Material material(String path) {
        idx3d_Material material = new idx3d_Material();
        try (InputStream in = open(path)) {
            if (in != null) {
                material.load(in, baseDir(path));
            }
        } catch (IOException e) {
            System.err.println("idx3d: cannot load material " + path + ": " + e);
        }
        return material;
    }

    /** Imports a {@code .3ds} mesh into the given scene. */
    public static void import3ds(idx3d_Scene scene, String path) {
        try (InputStream in = open(path)) {
            if (in != null) {
                new idx3d_3ds_Importer().importFromStream(in, scene);
            }
        } catch (Exception e) {
            System.err.println("idx3d: cannot import 3ds " + path + ": " + e);
        }
    }

    static String baseDir(String path) {
        String p = path.replace('\\', '/');
        int i = p.lastIndexOf('/');
        return i < 0 ? "" : p.substring(0, i + 1);
    }

    private static String basename(String p) {
        int i = p.lastIndexOf('/');
        return i < 0 ? p : p.substring(i + 1);
    }
}
