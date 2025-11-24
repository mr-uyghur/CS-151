/**
 * CompositeShape interface
 * 
 * This interface represents ANY drawable shape in the program.
 * Each shape knows how to draw itself using a Graphics2D context.
 * 
 * Every shape class (CarShape, SnowMan, MyHouseShape, etc.)
 * MUST implement this interface.
 */

import java.awt.Graphics2D;

public interface CompositeShape {
    
    /**
     * Draws the shape using the given graphics object.
     * 
     * @param g2 the Graphics2D context used to draw
     */
    void draw(Graphics2D g2);
}
