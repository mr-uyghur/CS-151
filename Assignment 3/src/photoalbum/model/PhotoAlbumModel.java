package photoalbum.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import photoalbum.strategy.SortByDate;
import photoalbum.strategy.SortingStrategy;

/**
 * MODEL in MVC.
 * Owns the list of photos, the current index (in sorted order), and the active sorting strategy.
 * Notifies listeners on any change so views can refresh.
 */
public class PhotoAlbumModel {
    private final List<Photo> photos = new ArrayList<>();
    private SortingStrategy strategy = new SortByDate(); // default sort is by date (spec)
    private int currentIndex = -1; // -1 means "no selection / empty"

    private final List<ChangeListener> listeners = new ArrayList<>();

    // --- Listener management ---
    public void addChangeListener(ChangeListener l) { listeners.add(l); }
    public void removeChangeListener(ChangeListener l) { listeners.remove(l); }
    private void fireChange() {
        ChangeEvent evt = new ChangeEvent(this);
        for (ChangeListener l : List.copyOf(listeners)) l.stateChanged(evt);
    }

    // --- Collection operations ---
    public void addPhoto(Photo p) {
        if (p == null) return;
        photos.add(p);
        if (currentIndex < 0) currentIndex = 0; // first item becomes current
        fireChange();
    }

    /** Deletes by case-insensitive name; does nothing if not found. */
    public void deletePhotoByName(String name) {
        if (name == null || photos.isEmpty()) return;
        int idx = -1;
        for (int i = 0; i < photos.size(); i++) {
            if (name.equalsIgnoreCase(photos.get(i).getName())) { idx = i; break; }
        }
        if (idx >= 0) {
            photos.remove(idx);
            if (photos.isEmpty()) currentIndex = -1;
            else currentIndex = Math.min(currentIndex, photos.size() - 1);
            fireChange();
        }
    }

    public boolean isEmpty() { return photos.isEmpty(); }
    public int size()        { return photos.size(); }

    public List<Photo> getPhotos() { return Collections.unmodifiableList(photos); }

    /** Returns a fresh list sorted using the current strategy (STRATEGY pattern). */
    public List<Photo> getSortedPhotos() { return strategy.sort(photos); }

    public void setStrategy(SortingStrategy s) {
        if (s == null) return;
        this.strategy = s;
        fireChange(); // let view recompute the list display
    }

    public SortingStrategy getStrategy() { return strategy; }

    // --- Current selection support (index is in the *sorted* list) ---
    public int getCurrentIndex() { return currentIndex; }

    public void setCurrentIndex(int idx) {
        if (photos.isEmpty()) currentIndex = -1;
        else currentIndex = Math.max(0, Math.min(idx, photos.size() - 1));
        fireChange();
    }

    /** Returns the current photo using the current sorted order. */
    public Photo getCurrentPhoto() {
        if (photos.isEmpty() || currentIndex < 0) return null;
        List<Photo> ordered = getSortedPhotos();
        if (currentIndex >= 0 && currentIndex < ordered.size()) return ordered.get(currentIndex);
        return null;
    }

    /** Iterator over the current sorted order, starting at currentIndex. */
    public AlbumIterator iterator() {
        return new AlbumIteratorImpl(getSortedPhotos(), Math.max(0, currentIndex));
    }
}
