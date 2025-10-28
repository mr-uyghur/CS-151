package photoalbum.strategy;

import java.util.ArrayList;
import java.util.List;
import photoalbum.model.Photo;

/** Sorts by file size (ascending). */
public class SortBySize implements SortingStrategy {
    @Override public List<Photo> sort(List<Photo> photos) {
        List<Photo> copy = new ArrayList<>(photos);
        copy.sort((a, b) -> Long.compare(a.getFileSize(), b.getFileSize()));
        return copy;
    }
    @Override public String name() { return "Size"; }
}
