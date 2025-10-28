package photoalbum.model;

/**
 * ITERATOR interface to traverse the album in the current sorted order.
 */
public interface AlbumIterator {
    boolean hasNext();
    boolean hasPrevious();
    Photo current();
    Photo next();
    Photo previous();
}
