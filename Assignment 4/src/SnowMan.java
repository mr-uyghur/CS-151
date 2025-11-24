/**
 * SnowMan
 * 
 * A snowman made of three stacked circles.
 */

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

public class SnowMan implements CompositeShape {

    private double x;     // reference x (center of circles)
    private double y;     // reference y (bottom circle's top)
    private double size;  // overall height of the snowman

    /**
     * Constructs a snowman.
     * 
     * @param x    center x position
     * @param y    baseline y position
     * @param size overall snowman height
     */
    public SnowMan(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Draws the snowman using 3 circles.
     */
    @Override
    public void draw(Graphics2D g2) {
        double r = size / 3.0; // radius of each ball

        Ellipse2D.Double bottom =
            new Ellipse2D.Double(x - r, y, 2 * r, 2 * r);

        Ellipse2D.Double middle =
            new Ellipse2D.Double(x - r, y - (1.4 * r), 2 * r, 2 * r);

        Ellipse2D.Double head =
            new Ellipse2D.Double(x - r, y - (2.8 * r), 2 * r, 2 * r);

        g2.draw(bottom);
        g2.draw(middle);
        g2.draw(head);
    }
}
