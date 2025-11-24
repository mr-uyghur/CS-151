/**
 * CarShape
 * 
 * A simple geometric car. This version is original work inspired by the 
 * textbook's CarShape example. If you use the exact textbook code,
 * include an acknowledgment comment.
 */

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class CarShape implements CompositeShape {

    private double x;      // top-left x position
    private double y;      // top-left y position
    private double width;  // width of the car body

    /**
     * Constructs the car.
     * 
     * @param x     left coordinate
     * @param y     top coordinate
     * @param width width of the car
     */
    public CarShape(double x, double y, double width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    /**
     * Draws the car using primitive shapes.
     */
    @Override
    public void draw(Graphics2D g2) {

        // SIZE CALCULATIONS
        double bodyHeight = width / 4;

        // CAR BODY
        Rectangle2D.Double body =
            new Rectangle2D.Double(x, y + width / 4, width, bodyHeight);

        // WHEELS
        double wheelRadius = width / 8;
        Ellipse2D.Double frontWheel =
            new Ellipse2D.Double(x + width / 6, y + width / 4 + bodyHeight, wheelRadius, wheelRadius);
        Ellipse2D.Double rearWheel =
            new Ellipse2D.Double(x + width * 2 / 3, y + width / 4 + bodyHeight, wheelRadius, wheelRadius);

        // ROOF
        Line2D.Double frontWindshield =
            new Line2D.Double(x + width / 4, y + width / 4, x + width / 3, y);
        Line2D.Double roofTop =
            new Line2D.Double(x + width / 3, y, x + (2 * width / 3), y);
        Line2D.Double rearWindshield =
            new Line2D.Double(x + (2 * width / 3), y, x + (3 * width / 4), y + width / 4);

        // DRAW ALL PARTS
        g2.draw(body);
        g2.draw(frontWheel);
        g2.draw(rearWheel);
        g2.draw(frontWindshield);
        g2.draw(roofTop);
        g2.draw(rearWindshield);
    }
}
