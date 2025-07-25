package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
// Tc2l Lab Exercise for CP6224
// Group Members:
// TODO | 
//                     
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 

import Tools.Tool;
import java.awt.*;

public class ToolManager {
    private Tool currentTool;

    public void setTool(Tool tool) {
        this.currentTool = tool;
    }

    public Tool getCurrentTool() {
        return currentTool;
    }

    public void useTool(Graphics2D g, int x, int y, int size, Color color, int prevX, int prevY) {
        if (currentTool != null) {
            currentTool.applyTool(g, x, y, size, color, prevX, prevY);
        }
    }
} 