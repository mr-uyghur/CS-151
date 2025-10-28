package photoalbum.model;

import java.awt.Image;
import java.io.File;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 * Domain object representing a single photo with basic metadata.
 * Also provides helpers to generate display and thumbnail icons.
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

    /** Returns a thumbnail icon (scaled); null if path is invalid. */
    public ImageIcon getThumbnail(int maxSide) {
        if (cachedThumb == null) {
            ImageIcon base = new ImageIcon(filePath);
            if (base.getIconWidth() <= 0) return null; // invalid path
            Image scaled = base.getImage().getScaledInstance(maxSide, -1, Image.SCALE_SMOOTH);
            cachedThumb = new ImageIcon(scaled);
        }
        return cachedThumb;
    }

    /** Returns a display icon scaled to fit within maxW × maxH (never upscales). */
    public ImageIcon getDisplayIcon(int maxW, int maxH) {
        if (cachedFull == null) {
            ImageIcon base = new ImageIcon(filePath);
            if (base.getIconWidth() <= 0) return null;
            double sx = maxW / (double) base.getIconWidth();
            double sy = maxH / (double) base.getIconHeight();
            double s = Math.min(1.0, Math.min(sx, sy)); // do not scale up
            Image scaled = base.getImage().getScaledInstance(
                (int)(base.getIconWidth() * s),
                (int)(base.getIconHeight() * s),
                Image.SCALE_SMOOTH);
            cachedFull = new ImageIcon(scaled);
        }
        return cachedFull;
    }
}
