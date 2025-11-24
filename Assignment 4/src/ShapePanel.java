/**
 * ShapePanel
 * 
 * This is the drawing canvas where the user clicks to place shapes.
 * It stores a list of shapes and repaints them every frame.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class ShapePanel extends JPanel {

    /**
     * ShapeFactory
     * 
     * A functional interface that creates shapes at a given (x, y) position.
     * We use factories so clicking buttons can change which shape is created.
     */
    public interface ShapeFactory {
        CompositeShape create(int x, int y);
    }

    private List<CompositeShape> shapes = new ArrayList<>(); // all shapes placed on screen
    private ShapeFactory currentFactory;                      // the active shape creator

    /**
     * Creates the drawing panel and adds mouse listener for clicking.
     */
    public ShapePanel() {
        setBackground(Color.WHITE);

        // Listen for mouse presses
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                if (currentFactory != null) {
                    // Create new shape where user clicked
                    CompositeShape s = currentFactory.create(e.getX(), e.getY());
                    shapes.add(s);

                    repaint(); // redraw screen
                }
            }
        });
    }

    /**
     * Sets the currently selected shape.
     */
    public void setCurrentFactory(ShapeFactory factory) {
        this.currentFactory = factory;
    }

    /**
     * Draws all shapes stored in the 'shapes' list.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        for (CompositeShape s : shapes) {
            s.draw(g2);
        }
    }
}
