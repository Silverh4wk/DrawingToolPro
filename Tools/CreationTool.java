package Tools;

import java.awt.*;
import src.CanvasPanel;
import src.CreationType;

public class CreationTool implements Tool {
    private final CanvasPanel canvasPanel; // Needed to get the current selected CreationType

    public CreationTool(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
    }

     // The CreationTool's applyTool is typically used when placing the item on the canvas.
    @Override
    public void applyTool(Graphics2D g, int x, int y, int size, Color color, int prevX, int prevY) {
    
        // Get the currently selected CreationType from the CanvasPanel
        CreationType selectedType = canvasPanel.getCurrentCreation(); 
        
    }
}
