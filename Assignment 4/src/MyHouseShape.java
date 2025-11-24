/**
 * MyHouseShape
 * 
 * This is your custom composite shape.
 * Must include at least 4 primitive shapes.
 * 
 * This house uses:
 *  - A rectangle (body)
 *  - Two roof lines
 *  - A door rectangle
 *  - A circular window
 */

import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class MyHouseShape implements CompositeShape {

    private double x;
    private double y;
    private double size;

    /**
     * Constructs the custom house shape.
     * 
     * @param x    top-left x
     * @param y    top-left y
     * @param size base width of the house
     */
    public MyHouseShape(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    @Override
    public void draw(Graphics2D g2) {

        // BODY
        double bodyWidth = size;
        double bodyHeight = size * 0.75;
        Rectangle2D.Double body =
            new Rectangle2D.Double(x, y, bodyWidth, bodyHeight);

        // ROOF
        Line2D.Double leftRoof =
            new Line2D.Double(x, y, x + bodyWidth / 2, y - bodyHeight / 2);
        Line2D.Double rightRoof =
            new Line2D.Double(x + bodyWidth / 2, y - bodyHeight / 2, x + bodyWidth, y);

        // DOOR
        double doorWidth = bodyWidth / 4;
        double doorHeight = bodyHeight / 2;
        Rectangle2D.Double door =
            new Rectangle2D.Double(
                x + bodyWidth / 2 - doorWidth / 2,
                y + bodyHeight - doorHeight,
                doorWidth,
                doorHeight
            );

        // ROUND WINDOW
        double r = bodyWidth / 8;
        Ellipse2D.Double window =
            new Ellipse2D.Double(
                x + bodyWidth * 0.7 - r,
                y + bodyHeight * 0.3 - r,
                2 * r,
                2 * r
            );

        // DRAW EVERYTHING
        g2.draw(body);
        g2.draw(leftRoof);
        g2.draw(rightRoof);
        g2.draw(door);
        g2.draw(window);
    }
}
