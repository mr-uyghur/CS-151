package photoalbum.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;   // used in refreshView()
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener; // validate images with ImageIO
import photoalbum.model.AlbumIterator;
import photoalbum.model.AlbumIteratorImpl;
import photoalbum.model.Photo;
import photoalbum.model.PhotoAlbumModel;
import photoalbum.strategy.SortByDate;
import photoalbum.strategy.SortByName;
import photoalbum.strategy.SortBySize;
import photoalbum.view.PhotoAlbumView;

/**
 * CONTROLLER (MVC)
 * - Wires UI events to the model.
 * - Listens for model changes and refreshes the view.
 * 
 * IMPORTANT: We guard against Swing feedback loops by:
 *   1) Using a 'syncingView' flag while we programmatically update JList selection.
 *   2) Never calling model.setCurrentIndex(...) inside refreshView().
 */
public class PhotoAlbumController implements ChangeListener {
    private final PhotoAlbumModel model;
    private final PhotoAlbumView view;

    /** Guard flag: true while we are updating the view programmatically. */
    private boolean syncingView = false;

    public PhotoAlbumController(PhotoAlbumModel model, PhotoAlbumView view) {
        this.model = model;
        this.view  = view;

        model.addChangeListener(this); // observe model changes
        wireActions();                 // connect UI events
        refreshView();                 // initial paint
    }

    /** Attach action listeners to all buttons and list selection. */
    private void wireActions() {
        // Add Photo
        view.addBtn.addActionListener(e -> doAddPhoto());

        // Delete selected photo by its display name
        view.deleteBtn.addActionListener(e -> {
            String name = view.getSelectedName();
            if (name == null) {
                JOptionPane.showMessageDialog(view, "Select a photo to delete.", "Nothing selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            model.deletePhotoByName(name);
        });

        // Navigate: Previous
        view.prevBtn.addActionListener(e -> {
            if (model.isEmpty()) return;
            AlbumIterator it = model.iterator();
            if (it.hasPrevious()) it.previous();
            model.setCurrentIndex(((AlbumIteratorImpl) it).getIndex());
        });

        // Navigate: Next
        view.nextBtn.addActionListener(e -> {
            if (model.isEmpty()) return;
            AlbumIterator it = model.iterator();
            if (it.hasNext()) it.next();
            model.setCurrentIndex(((AlbumIteratorImpl) it).getIndex());
        });

        // Sorting (Strategy)
        view.sortNameBtn.addActionListener(e -> { model.setStrategy(new SortByName()); syncIndexAfterSort(); });
        view.sortDateBtn.addActionListener(e -> { model.setStrategy(new SortByDate()); syncIndexAfterSort(); });
        view.sortSizeBtn.addActionListener(e -> { model.setStrategy(new SortBySize()); syncIndexAfterSort(); });

        // Keep model's current index aligned with USER clicks in the list
        view.getPhotoListComponent().addListSelectionListener(e -> {
            // Ignore intermediate & programmatic changes
            if (e.getValueIsAdjusting()) return;
            if (syncingView) return;

            int idx = view.getPhotoListComponent().getSelectedIndex();
            if (idx >= 0 && idx != model.getCurrentIndex()) {
                model.setCurrentIndex(idx); // user-initiated change only
            }
        });
    }

    /** Ensure current index remains valid after a sort change. */
    private void syncIndexAfterSort() {
        if (model.size() == 0) return;
        model.setCurrentIndex(Math.max(0, Math.min(model.getCurrentIndex(), model.size() - 1)));
    }

    /** Model -> View update hook. */
    @Override public void stateChanged(ChangeEvent e) { refreshView(); }

    /**
     * Refreshes the entire VIEW from the MODEL.
     * NOTE: Does NOT write back to the model — avoids feedback loops.
     */
    private void refreshView() {
        // Build ordered list and display names
        List<Photo> ordered = model.getSortedPhotos();
        List<String> names  = new ArrayList<>(ordered.size());
        for (Photo p : ordered) names.add(p.getName());

        // Block selection listener while we touch the JList
        syncingView = true;
        try {
            view.setPhotoNames(names); // repopulate left list

            int idx = model.getCurrentIndex();
            if (!ordered.isEmpty()) {
                // Clamp locally for the view (do not mutate model here)
                if (idx < 0 || idx >= ordered.size()) idx = 0;
                view.setSelectedIndex(idx);
                view.showCurrentPhoto(ordered.get(idx));
            } else {
                view.setSelectedIndex(-1);
                view.showCurrentPhoto(null);
            }

            view.setStatus("Sort: " + model.getStrategy().name()
                         + " | Photos: " + ordered.size());
        } finally {
            syncingView = false; // re-enable user selection handling
        }
    }

    /** Handles file selection + robust validation, then adds the photo to the model. */
    private void doAddPhoto() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Choose an image file");
        int result = chooser.showOpenDialog(view);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File f = chooser.getSelectedFile();
        if (f == null || !f.exists()) {
            JOptionPane.showMessageDialog(view, "Selected file does not exist.", "Invalid Path", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate image file (ImageIO returns null for unsupported types)
        try {
            if (ImageIO.read(f) == null) {
                JOptionPane.showMessageDialog(view, "File is not a readable image (use .jpg/.png/.gif/.bmp).", "Unsupported", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Failed to read the image file.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ask user for a display name (default to filename)
        String defaultName = f.getName();
        String name = (String) JOptionPane.showInputDialog(
                view, "Enter a display name:", "Photo Name",
                JOptionPane.PLAIN_MESSAGE, null, null, defaultName);
        if (name == null || name.isBlank()) name = defaultName;

        // Add to model
        Photo p = new Photo(name.trim(), f.getAbsolutePath(), new Date());
        model.addPhoto(p);

        // Focus the newly added photo (compute its position in CURRENT sort)
        List<Photo> ordered = model.getSortedPhotos();
        int idx = ordered.indexOf(p);
        if (idx < 0) idx = 0; // fallback
        model.setCurrentIndex(idx); // user-intended selection
    }
}
