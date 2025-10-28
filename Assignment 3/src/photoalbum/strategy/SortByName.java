package photoalbum.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import photoalbum.model.Photo;

/** Sorts by photo name (case-insensitive). */
public class SortByName implements SortingStrategy {
    @Override public List<Photo> sort(List<Photo> photos) {
        List<Photo> copy = new ArrayList<>(photos);
        copy.sort(Comparator.comparing(Photo::getName, String.CASE_INSENSITIVE_ORDER));
        return copy;
    }
    @Override public String name() { return "Name"; }
}
