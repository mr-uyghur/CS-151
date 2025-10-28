package photoalbum.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import photoalbum.model.Photo;

/**
 * VIEW in MVC.
 * Swing UI with three regions: left list, center viewer, bottom control bar.
 * Exposes minimal API so the controller can refresh the UI.
 */
public class PhotoAlbumView extends JFrame {
    // Left list model and component (names by default)
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> photoList = new JList<>(listModel);

    // Center image area
    private final JLabel photoLabel = new JLabel("No photo", SwingConstants.CENTER);

    // Buttons (public so controller can add listeners)
    public final JButton addBtn      = new JButton("Add Photo");
    public final JButton deleteBtn   = new JButton("Delete Photo");
    public final JButton prevBtn     = new JButton("Previous");
    public final JButton nextBtn     = new JButton("Next");
    public final JButton sortNameBtn = new JButton("Sort By Name");
    public final JButton sortDateBtn = new JButton("Sort By Date");
    public final JButton sortSizeBtn = new JButton("Sort By Size");

    // Simple status bar at the top
    private final JLabel status = new JLabel("Ready");

    public PhotoAlbumView() {
        super("Photo Album Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        buildUI();
    }

    /** Builds the window layout: split pane + control bar + status bar. */
    private void buildUI() {
        // Left panel with list
        photoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(photoList);
        listScroll.setPreferredSize(new Dimension(240, 0));

        // Center panel with current image
        JPanel center = new JPanel(new BorderLayout());
        center.add(photoLabel, BorderLayout.CENTER);

        // Split the window left/right
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, center);
        split.setResizeWeight(0.25);

        // Bottom control bar
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(addBtn);
        controls.add(deleteBtn);
        controls.add(prevBtn);
        controls.add(nextBtn);
        controls.add(sortNameBtn);
        controls.add(sortDateBtn);
        controls.add(sortSizeBtn);

        // Compose content
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(status, BorderLayout.NORTH);
        getContentPane().add(split, BorderLayout.CENTER);
        getContentPane().add(controls, BorderLayout.SOUTH);
    }

    // ---- Methods the controller calls to update the UI ----

    /** Replace the left list with the given names in current sorted order. */
    public void setPhotoNames(List<String> names) {
        listModel.clear();
        for (String n : names) listModel.addElement(n);
    }

    /** Highlight a row in the list; clears selection if idx is invalid. */
    public void setSelectedIndex(int idx) {
        if (idx >= 0 && idx < listModel.getSize()) {
            photoList.setSelectedIndex(idx);
            photoList.ensureIndexIsVisible(idx);
        } else {
            photoList.clearSelection();
        }
    }

    /** Returns the currently selected list item text (used by Delete). */
    public String getSelectedName() { return photoList.getSelectedValue(); }

    /** Display the current photo image and its name below the image. */
    public void showCurrentPhoto(Photo p) {
        if (p == null) {
            photoLabel.setText("No photo");
            photoLabel.setIcon(null);
            return;
        }
        photoLabel.setText(p.getName());
        // fit to a reasonable area in the window
        photoLabel.setIcon(p.getDisplayIcon(1000, 520));
        photoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        photoLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
    }

    /** Update status text (e.g., current sort + count). */
    public void setStatus(String text) { status.setText(text); }

    /** Expose the list widget so the controller can listen to selections. */
    public JList<String> getPhotoListComponent() { return photoList; }
}
