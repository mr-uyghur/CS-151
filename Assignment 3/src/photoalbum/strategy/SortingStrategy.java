package photoalbum.strategy;

import java.util.List;
import photoalbum.model.Photo;
/**
 * STRATEGY interface for sorting a photo list.
 * Implementations should return a NEW sorted list, leaving the original intact.
 */
public interface SortingStrategy {
    List<Photo> sort(List<Photo> photos);
    String name(); // human-readable name for status bar / buttons
}
