package Tools;

import src.CanvasPanel;

public class ClearCanvasTool {
    private final CanvasPanel canvas;

    public ClearCanvasTool(CanvasPanel canvas) {
        this.canvas = canvas;
    }

    public void onPress() {
        canvas.clearCanvas();
    }
}
