/**
 * ShapeDisplayer
 * 
 * This is the main class that creates the Swing window.
 * It adds:
 *  - 3 shape buttons on top (Snowman, Car, House)
 *  - A large drawing panel below
 * 
 * Clicking a button changes the current shape.
 * Clicking the panel draws that shape.
 */

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

public class ShapeDisplayer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    /**
     * Builds the entire GUI.
     */
    private static void createAndShowGui() {

        // FRAME SETUP
        JFrame frame = new JFrame("Shape Displayer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // DRAWING PANEL
        ShapePanel drawingPanel = new ShapePanel();

        // SHAPE FACTORIES (these generate shapes at click position)
        ShapePanel.ShapeFactory snowFactory =
            (x, y) -> new SnowMan(x, y, 60);

        ShapePanel.ShapeFactory carFactory =
            (x, y) -> new CarShape(x - 30, y - 20, 60);

        ShapePanel.ShapeFactory houseFactory =
            (x, y) -> new MyHouseShape(x - 30, y - 30, 60);

        // Set initial shape
        drawingPanel.setCurrentFactory(snowFactory);

        // ICONS FOR BUTTONS (small shapes)
        // Center the snowman inside the 60x60 icon (x = 30, y centered)
        Icon snowIcon =
            new ShapeIcon(new SnowMan(30, 30, 40), 60, 60);

        Icon carIcon =
            new ShapeIcon(new CarShape(10, 20, 40), 60, 60);

        Icon houseIcon =
            new ShapeIcon(new MyHouseShape(10, 25, 40), 60, 60);

        // BUTTONS
        JToggleButton snowButton = new JToggleButton(snowIcon);
        JToggleButton carButton = new JToggleButton(carIcon);
        JToggleButton houseButton = new JToggleButton(houseIcon);

        // GROUP (only one selected at a time)
        ButtonGroup group = new ButtonGroup();
        group.add(snowButton);
        group.add(carButton);
        group.add(houseButton);

        // Default selection
        snowButton.setSelected(true);

        // BUTTON LISTENERS
        snowButton.addActionListener(e -> drawingPanel.setCurrentFactory(snowFactory));
        carButton.addActionListener(e -> drawingPanel.setCurrentFactory(carFactory));
        houseButton.addActionListener(e -> drawingPanel.setCurrentFactory(houseFactory));

        // BUTTON PANEL (left-aligned)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        buttonPanel.add(snowButton);
        buttonPanel.add(carButton);
        buttonPanel.add(houseButton);
        
        
        

        // LAYOUT
        frame.setLayout(new BorderLayout());
        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(drawingPanel, BorderLayout.CENTER);

        // SIZE + SHOW
        frame.setSize(550, 650);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
