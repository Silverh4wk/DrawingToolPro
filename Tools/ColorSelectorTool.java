package Tools;

import java.awt.Color;
import java.util.function.Consumer;
import javax.swing.JColorChooser;
import src.CanvasPanel;

public class ColorSelectorTool {
    // This class provides a tool for selecting colors in the canvas panel.
    public static void onPress(CanvasPanel canvasPanel, Consumer<Color> colorChangeCallback) {
        Color selectedColor = JColorChooser.showDialog(null, "Choose Color", canvasPanel.getBrushColor());
        if (selectedColor != null) {
            colorChangeCallback.accept(selectedColor); 
        }
    }
}
