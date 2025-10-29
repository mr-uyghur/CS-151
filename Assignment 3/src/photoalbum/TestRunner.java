package photoalbum;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import photoalbum.model.Photo;
import photoalbum.model.PhotoAlbumModel;
import photoalbum.strategy.SortByName;
import photoalbum.strategy.SortBySize;

/**
 * Small console runner to exercise model operations without the GUI.
 * Creates temporary files, adds photos to the model, checks sorting and iterator, and deletes an entry.
 */
public class TestRunner {
    public static void main(String[] args) throws Exception {
        File outDir = new File("bin/test-data");
        outDir.mkdirs();

        File f1 = new File(outDir, "test1.jpg");
        try (FileOutputStream os = new FileOutputStream(f1)) { os.write(new byte[100]); }
        File f2 = new File(outDir, "test2.jpg");
        try (FileOutputStream os = new FileOutputStream(f2)) { os.write(new byte[200]); }

        PhotoAlbumModel model = new PhotoAlbumModel();
        Photo p1 = new Photo("Alpha", f1.getAbsolutePath(), new Date(1000));
        Photo p2 = new Photo("Beta",  f2.getAbsolutePath(), new Date(2000));

        model.addPhoto(p1);
        model.addPhoto(p2);

        System.out.println("Added photos -> size: " + model.size());

        List<Photo> byName = new SortByName().sort(model.getPhotos());
        System.out.println("First by name: " + byName.get(0).getName());

        model.setStrategy(new SortBySize());
        List<Photo> bySize = model.getSortedPhotos();
        System.out.println("First by size: " + bySize.get(0).getName());

        // Iterator behavior (uses currentIndex; default becomes 0 after first add)
        var it = model.iterator();
        System.out.println("Iterator current: " + (it.current() != null ? it.current().getName() : "<null>"));
        if (it.hasNext()) {
            it.next();
            System.out.println("Iterator after next: " + it.current().getName());
        }

        // Delete by name
        model.deletePhotoByName("Alpha");
        System.out.println("Size after deleting Alpha: " + model.size());

        System.out.println("TestRunner finished successfully.");
    }
}
