package photoalbum.model;

import java.util.List;

/**
 * Concrete iterator over a snapshot of an ordered list of photos.
 * Maintains an index and supports bidirectional navigation.
 */
public class AlbumIteratorImpl implements AlbumIterator {
    private final List<Photo> ordered;
    private int index;

    public AlbumIteratorImpl(List<Photo> ordered, int startIndex) {
        this.ordered = ordered;
        // Clamp start index into range
        this.index = Math.max(0, Math.min(startIndex, Math.max(0, ordered.size() - 1)));
    }

    @Override public boolean hasNext()     { return !ordered.isEmpty() && index < ordered.size() - 1; }
    @Override public boolean hasPrevious() { return !ordered.isEmpty() && index > 0; }

    @Override public Photo current() {
        if (ordered.isEmpty()) return null;
        return ordered.get(index);
    }

    @Override public Photo next() {
        if (hasNext()) index++;
        return current();
    }

    @Override public Photo previous() {
        if (hasPrevious()) index--;
        return current();
    }

    /** Expose current index so the controller can sync it back to the model. */
    public int getIndex() { return index; }
}
