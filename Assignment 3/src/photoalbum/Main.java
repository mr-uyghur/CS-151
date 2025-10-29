package photoalbum;

import javax.swing.SwingUtilities;
import photoalbum.controller.PhotoAlbumController;
import photoalbum.model.PhotoAlbumModel;
import photoalbum.view.PhotoAlbumView;

/**
 * Application entry point.
 * Creates MVC components and shows the window on the Swing EDT.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PhotoAlbumModel model = new PhotoAlbumModel();
            PhotoAlbumView view = new PhotoAlbumView();
            new PhotoAlbumController(model, view); // wire controller
            view.setVisible(true);
        });
    }
}
