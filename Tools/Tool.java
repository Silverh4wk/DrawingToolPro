package Tools;
import java.awt.*;

public interface Tool {
    void applyTool(Graphics2D g, int x, int y, int size, Color color,int prevX, int prevY);
}
