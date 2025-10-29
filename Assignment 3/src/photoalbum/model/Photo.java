package photoalbum.model;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import javax.imageio.ImageIO;            // UPDATED: load via ImageIO
import javax.swing.ImageIcon;

/**
 * Domain object representing a single photo with basic metadata.
 * Provides helpers to generate display and thumbnail icons using ImageIO.
 */
public class Photo {
    private String name;
    private String filePath;
    private Date dateAdded;
    private long fileSize;

    // Cached icons so we don't rescale every repaint
    private transient ImageIcon cachedThumb;
    private transient ImageIcon cachedFull;

    public Photo(String name, String filePath, Date dateAdded) {
        this.name = name;
        this.filePath = filePath;
        this.dateAdded = (dateAdded != null) ? dateAdded : new Date();

        File f = new File(filePath);
        this.fileSize = f.exists() ? f.length() : 0L;
    }

    // --- getters/setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFilePath() { return filePath; }
    public Date getDateAdded() { return dateAdded; }
    public long getFileSize() { return fileSize; }

    /** How the item appears in a JList when using the default renderer. */
    @Override public String toString() {
        return name + " (" + fileSize + " bytes)";
    }

    /**
     * Returns a thumbnail icon (scaled); null if path is invalid or unsupported.
     * Uses ImageIO for better format handling (PNG/JPG/GIF/BMP).
     */
    public ImageIcon getThumbnail(int maxSide) {
        if (cachedThumb != null) return cachedThumb;
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (img == null) return null; // unreadable format
            int w = img.getWidth();
            int h = img.getHeight();
            double s = (w >= h) ? (maxSide / (double) w) : (maxSide / (double) h);
            s = Math.min(1.0, s); // don't upscale small images
            int newW = Math.max(1, (int)Math.round(w * s));
            int newH = Math.max(1, (int)Math.round(h * s));
            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            cachedThumb = new ImageIcon(scaled);
            return cachedThumb;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns a display icon scaled to fit within maxW × maxH (never upscales).
     * Uses ImageIO.read for robust image loading.
     */
    public ImageIcon getDisplayIcon(int maxW, int maxH) {
        if (cachedFull != null) return cachedFull;
        try {
            BufferedImage img = ImageIO.read(new File(filePath));
            if (img == null) return null; // unsupported or unreadable
            int w = img.getWidth();
            int h = img.getHeight();
            double sx = maxW / (double) w;
            double sy = maxH / (double) h;
            double s  = Math.min(1.0, Math.min(sx, sy)); // do not upscale
            int newW = Math.max(1, (int)Math.round(w * s));
            int newH = Math.max(1, (int)Math.round(h * s));
            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            cachedFull = new ImageIcon(scaled);
            return cachedFull;
        } catch (Exception ex) {
            return null;
        }
    }
}
