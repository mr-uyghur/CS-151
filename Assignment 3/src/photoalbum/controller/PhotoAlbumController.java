package photoalbum.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import photoalbum.model.AlbumIterator;
import photoalbum.model.AlbumIteratorImpl;
import photoalbum.model.Photo;
import photoalbum.model.PhotoAlbumModel;
import photoalbum.strategy.SortByDate;
import photoalbum.strategy.SortByName;
import photoalbum.strategy.SortBySize;
import photoalbum.view.PhotoAlbumView;


/**
 * CONTROLLER in MVC.
 * Wires UI events to model mutations and keeps the view in sync with model changes.
 */
public class PhotoAlbumController implements ChangeListener {
    private final PhotoAlbumModel model;
    private final PhotoAlbumView view;

    public PhotoAlbumController(PhotoAlbumModel model, PhotoAlbumView view) {
        this.model = model;
        this.view  = view;

        model.addChangeListener(this); // observe model changes
        wireActions();                 // connect UI events
        refreshView();                 // initial paint
    }

    /** Attach action listeners to all buttons and list selection. */
    private void wireActions() {
        // Add Photo: choose image file, prompt for display name, add to model
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

        // Navigate to previous using the iterator
        view.prevBtn.addActionListener(e -> {
            if (model.isEmpty()) return;
            AlbumIterator it = model.iterator();
            if (it.hasPrevious()) it.previous();
            model.setCurrentIndex(((AlbumIteratorImpl) it).getIndex());
        });

        // Navigate to next using the iterator
        view.nextBtn.addActionListener(e -> {
            if (model.isEmpty()) return;
            AlbumIterator it = model.iterator();
            if (it.hasNext()) it.next();
            model.setCurrentIndex(((AlbumIteratorImpl) it).getIndex());
        });

        // Sorting strategies (STRATEGY pattern)
        view.sortNameBtn.addActionListener(e -> { model.setStrategy(new SortByName());  syncIndexAfterSort(); });
        view.sortDateBtn.addActionListener(e -> { model.setStrategy(new SortByDate());  syncIndexAfterSort(); });
        view.sortSizeBtn.addActionListener(e -> { model.setStrategy(new SortBySize());  syncIndexAfterSort(); });

        // Keep model's current index aligned with user clicks in the list
        view.getPhotoListComponent().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int idx = view.getPhotoListComponent().getSelectedIndex();
                if (idx >= 0) model.setCurrentIndex(idx);
            }
        });
    }

    /** Ensure current index remains valid after a sort change. */
    private void syncIndexAfterSort() {
        if (model.size() == 0) return;
        model.setCurrentIndex(Math.max(0, Math.min(model.getCurrentIndex(), model.size() - 1)));
    }

    /** Called whenever the model changes; refreshes the entire view. */
    @Override public void stateChanged(ChangeEvent e) { refreshView(); }

    /** Refresh list, selection, image, and status bar based on model state. */
    private void refreshView() {
        List<String> names = new ArrayList<>();
        for (Photo p : model.getSortedPhotos()) names.add(p.getName());
        view.setPhotoNames(names);
        view.setSelectedIndex(model.getCurrentIndex());
        view.showCurrentPhoto(model.getCurrentPhoto());
        view.setStatus("Sort: " + model.getStrategy().name() + " | Photos: " + model.size());
    }

    /** Handles file selection + basic validation, then adds the photo to the model. */
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

        // Quick image sanity check
        ImageIcon test = new ImageIcon(f.getAbsolutePath());
        if (test.getIconWidth() <= 0) {
            JOptionPane.showMessageDialog(view, "File is not a readable image.", "Unsupported", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ask for a friendly display name (default: filename)
        String defaultName = f.getName();
        String name = (String) JOptionPane.showInputDialog(
                view, "Enter a display name:", "Photo Name",
                JOptionPane.PLAIN_MESSAGE, null, null, defaultName);
        if (name == null || name.isBlank()) name = defaultName;

        Photo p = new Photo(name.trim(), f.getAbsolutePath(), new Date());
        model.addPhoto(p);

        // Focus the newly added photo in the sorted list
        int idx = model.getSortedPhotos().indexOf(p);
        model.setCurrentIndex(idx >= 0 ? idx : 0);
    }
}
