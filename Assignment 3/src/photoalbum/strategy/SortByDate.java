package photoalbum.strategy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import photoalbum.model.Photo;

/** Sorts by the date the photo was added (ascending). */
public class SortByDate implements SortingStrategy {
    @Override public List<Photo> sort(List<Photo> photos) {
        List<Photo> copy = new ArrayList<>(photos);
        copy.sort(Comparator.comparing(Photo::getDateAdded));
        return copy;
    }
    @Override public String name() { return "Date"; }
}
