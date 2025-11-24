/**
 * ShapeIcon
 * 
 * This class allows a CompositeShape to be used as an Icon for buttons.
 * Swing buttons can display icons, so we wrap a shape in this class
 * and let Swing draw the shape at icon-size.
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

public class ShapeIcon implements Icon {

    private CompositeShape shape; // The shape this icon will display
    private int width;            // Icon width
    private int height;           // Icon height

    /**
     * Constructs a ShapeIcon.
     * 
     * @param shape  the CompositeShape to draw
     * @param width  the width of the icon
     * @param height the height of the icon
     */
    public ShapeIcon(CompositeShape shape, int width, int height) {
        this.shape = shape;
        this.width = width;
        this.height = height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    /**
     * Actually paints the shape inside the icon.
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2 = (Graphics2D) g.create();  // safe copy
        g2.translate(x, y);                        // shift to icon position
        // Ensure the icon's shape is drawn in black
        g2.setColor(Color.BLACK);
        shape.draw(g2);                            // draw shape
        g2.dispose();                              // cleanup
    }
}
