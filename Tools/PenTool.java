package Tools;

import java.awt.*;
import java.awt.geom.Point2D;

public class PenTool implements Tool {
    @Override
    public void applyTool(Graphics2D g, int x, int y, int size, Color color, int prevX, int prevY) {
        g.setColor(color);
        
        //enables antialiasing to remove jittery line shape or smooth it out 
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // interpolate between the previous and current mouse positions and draw along the path to fill the gaps when
        // u move mouse quickly
        double distance = Point2D.distance((double) prevX, (double) prevY, (double) x, (double) y);
        double dx = x - prevX;
        double dy = y - prevY;

        //A loop that steps along the line from prevX, prevY to x, y one pixel at a time.
        //making small dots to fill the gaps 
        for (double i = 0; i <= distance; i += 1) {
            int drawX = (int) (prevX + (dx * i / distance));
            int drawY = (int) (prevY + (dy * i / distance));
            g.fillOval(drawX, drawY, size, size);
        }
    }
}
