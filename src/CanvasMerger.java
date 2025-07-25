package src;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class CanvasMerger {

    // Combines two images into one, layered based on leftOnTop
    public static BufferedImage mergeImages(BufferedImage imgA, BufferedImage imgB, boolean imgAOnTop) {
        int width = Math.max(imgA.getWidth(), imgB.getWidth());
        int height = Math.max(imgA.getHeight(), imgB.getHeight());

        BufferedImage merged = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = merged.createGraphics();

        if (imgAOnTop) {
            g2d.drawImage(imgB, 0, 0, null); // bottom layer
            g2d.drawImage(imgA, 0, 0, null); // top layer
        } else {
            g2d.drawImage(imgA, 0, 0, null);
            g2d.drawImage(imgB, 0, 0, null);
        }

        g2d.dispose();
        return merged;
    }
    // implementation of the insertImageLayer method
    // This method merges the left canvas's content onto the right canvas's drawing layer.
    // It assumes both CanvasPanel objects are already initialized and have their images set.
   public static void insertImageLayer(CanvasPanel leftCanvasPanel, CanvasPanel rightCanvasPanel) {
        BufferedImage leftCanvasContent = leftCanvasPanel.getImage(); // Get the current image from the left canvas.

        BufferedImage rightCanvasCurrentContent = rightCanvasPanel.getImage(); // Get the current image from the right canvas.

        BufferedImage mergedResult = mergeImages(leftCanvasContent, rightCanvasCurrentContent, true); // Merge the left canvas's content onto the right canvas's drawing layer, with left on top.

        rightCanvasPanel.setCanvasImage(mergedResult); // Set the merged image as the new content of the right canvas.

        if (rightCanvasPanel.getCanvasType() == CanvasPanel.CanvasType.COMPOSITION) {
            // Note: CanvasPanel's setDrawableItems with an empty list effectively clears them.
            rightCanvasPanel.setDrawableItems(new java.util.ArrayList<>());
            rightCanvasPanel.setSelectedItem(null); // Deselect any item on the right canvas
        }
        rightCanvasPanel.repaint(); // Ensure the right canvas updates its display with the new merged image
    }
}
