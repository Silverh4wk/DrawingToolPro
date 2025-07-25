package src;

// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// Tc2l Lab Exercise for CP6224
// Group Members:
// TODO |
//        //(done) fix the jittery mouse movement when drawing an object
//        //(done) add undo and redo functionality
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// File: src/CanvasPanel.java

import Helpers.Helpers.IntVector2D;
import Tools.CreationTool;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javax.swing.*;

public class CanvasPanel extends JPanel {
    private final BufferedImage canvasImage;
    private final Stack<List<DrawableItem>> undoStackComposition = new Stack<>(); // For composition canvas
    private final Stack<List<DrawableItem>> redoStackComposition = new Stack<>(); // For composition canvas
    private final Stack<BufferedImage> undoStackDrawing = new Stack<>(); // For drawing canvas
    private final Stack<BufferedImage> redoStackDrawing = new Stack<>(); // For drawing canvas
    private final ToolManager toolManager;
    private final CanvasType canvasType; // To differentiate between drawing and composition canvas
    private final Point2D.Double panOffset = new Point2D.Double(0, 0);


    private int brushSize = 10;
    private Color brushColor = Color.BLACK;
    private float rotationAngle = 0.0f;
    private float zoomFactor = 1.0f;

    private CreationType currentSelectedCreation; // For drawing library items on composition canvas
    private List<DrawableItem> drawableItems; // For composition canvas to hold multiple items
    private DrawableItem selectedItem; // The currently selected item on the composition canvas
    private Point lastMousePress; // For dragging selected items (moving or rotating)
    private double initialRotationAngle; // For gesture-based rotation
    private boolean isRotating = false; // Flag to indicate if currently rotating
    private boolean isPanning = false;
    private Point lastPanPoint = null;
    private double canvasRotationAngle = 0; // For rotating the entire canvas
    // Returns the current visible image of the canvas

    public BufferedImage getCanvasImage() {
        BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        paint(g2d); // Calls paintComponent
        g2d.dispose();
        return image;
    }

    // Sets the canvas image (used after merging)
    public void setCanvasImage(BufferedImage newImage) {
        Graphics2D g2d = canvasImage.createGraphics();
        g2d.setComposite(java.awt.AlphaComposite.Src);
        g2d.setColor(new java.awt.Color(0, 0, 0, 0)); // transparent background
        g2d.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g2d.drawImage(newImage, 0, 0, null);
        g2d.dispose();
        repaint();
    }

    public void placeCurrentCreationAtCenter() {
        if (canvasType == CanvasType.COMPOSITION && currentSelectedCreation != null) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            DrawableItem newItem = new DrawableItem(currentSelectedCreation, centerX, centerY);
            drawableItems.add(newItem);
            selectedItem = newItem;
            currentSelectedCreation = null;

            repaint();
        }
    }

    // Enum to define canvas types
    public enum CanvasType {
        DRAWING,
        COMPOSITION,
    }

    public CanvasPanel(ToolManager toolManager, IntVector2D canvasDimenions, CanvasType type) {
        this.toolManager = toolManager;
        this.canvasType = type;
        setPreferredSize(new Dimension(canvasDimenions.getX(), canvasDimenions.getY()));
        canvasImage = new BufferedImage(canvasDimenions.getX(), canvasDimenions.getY(), BufferedImage.TYPE_INT_ARGB);
        clearCanvas();

        if (this.canvasType == CanvasType.COMPOSITION) {
            drawableItems = new ArrayList<>();
        }

        initializeMouseListeners();
    }

    public void resetRotation() {
        rotationAngle = 0.0f;
        panOffset.setLocation(0, 0);

        repaint();
    }

    public void setRotationAngle(float angle) {
        rotationAngle = angle;
        repaint();
    }

    public void zoom(float factor, Point pivot) {
        double oldZoom = zoomFactor;
        zoomFactor *= factor;

        zoomFactor = Math.max(0.1f, Math.min(10.0f, zoomFactor));

        double actualFactor = zoomFactor / oldZoom;

        panOffset.x = (int) (pivot.x - (pivot.x - panOffset.x) * actualFactor);
        panOffset.y = (int) (pivot.y - (pivot.y - panOffset.y) * actualFactor);

        repaint();
    }

    public void resetZoom() {
        zoomFactor = 1.0f;
        panOffset.setLocation(0, 0);
        repaint();
    }

    private void initializeMouseListeners() {
        MouseAdapter adapter = new MouseAdapter() {
            private Point prevPoint = null;
            private Point dragStart = null;

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    isPanning = true;
                    lastMousePress = e.getPoint();
                    return;
                }
                saveStateForUndo();
                prevPoint = transformPoint(e.getPoint());
                dragStart = e.getPoint();

                if (canvasType == CanvasType.COMPOSITION) {
                    handleCompositionMousePress(e);
                } else if (canvasType == CanvasType.DRAWING) {
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

                if (isPanning) {
                    Point current = e.getPoint();
                    panOffset.x += current.x - lastMousePress.x;
                    panOffset.y += current.y - lastMousePress.y;
                    lastMousePress = current;
                    repaint();
                    return;
                }

                Point current = transformPoint(e.getPoint());

                if (canvasType == CanvasType.COMPOSITION && selectedItem != null) {
                    handleCompositionDrag(e, current);
                } else if (canvasType == CanvasType.DRAWING) {
                    drawOnCanvas(prevPoint, current);
                    prevPoint = current;
                }
                repaint();
            }

            // when mouse released, reset all of those flags back to normal
            @Override
            public void mouseReleased(MouseEvent e) {
                isPanning = false;
                prevPoint = null;
                dragStart = null;
                isRotating = false;
                repaint();
            }

            private Point transformPoint(Point p) {
                // apply the inverse transformations to get image coordinates
                double x = (p.x - panOffset.x) / zoomFactor;
                double y = (p.y - panOffset.y) / zoomFactor;

                if (canvasType == CanvasType.COMPOSITION) {
                    // reverse canvas rotation
                    double centerX = getWidth() / 2.0;
                    double centerY = getHeight() / 2.0;

                    double tx = x - centerX;
                    double ty = y - centerY;

                    double rad = Math.toRadians(-canvasRotationAngle);
                    double rx = tx * Math.cos(rad) - ty * Math.sin(rad);
                    double ry = tx * Math.sin(rad) + ty * Math.cos(rad);

                    x = rx + centerX;
                    y = ry + centerY;
                }

                return new Point((int) x, (int) y);
            }

            private void handleCompositionMousePress(MouseEvent e) {
                if (e.isMetaDown() && selectedItem != null) {
                    startRotation(e);
                    return;
                }

                // Try to select existing item
                for (int i = drawableItems.size() - 1; i >= 0; i--) {
                    DrawableItem item = drawableItems.get(i);
                    if (item.getBounds().contains(e.getPoint())) {
                        selectAndBringToFront(item, i);
                        return;
                    }
                }

                // then place new item if creation tool is selected
                if (currentSelectedCreation != null) {
                    placeNewItem(e);
                } else {
                    selectedItem = null;
                }
            }

            private void startRotation(MouseEvent e) {
                isRotating = true;
                Point2D center = selectedItem.getCenter();
                initialRotationAngle = Math.atan2(
                        e.getY() - center.getY(),
                        e.getX() - center.getX());
            }

            private void selectAndBringToFront(DrawableItem item, int index) {
                selectedItem = item;
                if (index != drawableItems.size() - 1) {
                    drawableItems.remove(index);
                    drawableItems.add(item);
                }
            }

            private void placeNewItem(MouseEvent e) {
                DrawableItem newItem = new DrawableItem(
                        currentSelectedCreation,
                        e.getX(),
                        e.getY());
                drawableItems.add(newItem);
                selectedItem = newItem;
                currentSelectedCreation = null;
            }

            private void handleCompositionDrag(MouseEvent e, Point current) {
                if (isRotating) {
                    rotateItem(e);
                } else if (dragStart != null) {
                    moveItem(current);
                }
            }

            private void rotateItem(MouseEvent e) {
                Point2D center = selectedItem.getCenter();
                double currentAngle = Math.atan2(
                        e.getY() - center.getY(),
                        e.getX() - center.getX());
                double angleDelta = currentAngle - initialRotationAngle;
                selectedItem.setRotationAngle(selectedItem.getRotationAngle() + Math.toDegrees(angleDelta));
                initialRotationAngle = currentAngle;
            }

            // private void moveItem(Point current) {
            // int dx = current.x - dragStart.x;
            // int dy = current.y - dragStart.y;
            // selectedItem.setPosition(
            // selectedItem.getX() + dx,
            // selectedItem.getY() + dy);
            // dragStart = current;
            // }
            // private void moveItem(Point current) {
            // int dx = current.x - dragStart.x;
            // int dy = current.y - dragStart.y;
            // selectedItem.setPosition(
            // selectedItem.getX() + dx,
            // selectedItem.getY() + dy);
            // dragStart = current;
            // }

            private void moveItem(Point current) {
                int dx = current.x - dragStart.x;
                int dy = current.y - dragStart.y;
                selectedItem.setPosition(
                        selectedItem.getX() + dx,
                        selectedItem.getY() + dy);
                dragStart = current;
            }

            private void drawOnCanvas(Point start, Point end) {
                Graphics2D g = canvasImage.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Point imgStart = transformPoint(start);
                Point imgEnd = transformPoint(end);

                toolManager.useTool(g,
                        imgEnd.x, imgEnd.y,
                        (int) (brushSize / zoomFactor),
                        brushColor,
                        imgStart.x, imgStart.y);

                g.dispose();
            }
        };
        // idk why i didnt use the mouse wheel to begin with
        addMouseWheelListener(e -> {
            float zoomFactor = (e.getWheelRotation() < 0) ? 1.1f : 0.9f;
            Point center = new Point(getWidth() / 2, getHeight() / 2);
            zoom(zoomFactor, center);
        });
        

        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g2d.translate(panOffset.x, panOffset.y);
        g2d.scale(zoomFactor, zoomFactor);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        AffineTransform originalTransform = g2d.getTransform();

        g2d.rotate(Math.toRadians(rotationAngle), centerX, centerY);

        if (canvasType == CanvasType.COMPOSITION) {
            g2d.rotate(Math.toRadians(canvasRotationAngle), centerX, centerY);
        }

        g2d.drawImage(canvasImage, 0, 0, null);

        if (canvasType == CanvasType.COMPOSITION && drawableItems != null) {
            for (DrawableItem item : drawableItems) {
                item.draw(g2d);
            }
            if (selectedItem != null) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2 / zoomFactor));
                Rectangle bounds = selectedItem.getBounds();
                g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }

        g2d.setTransform(originalTransform);
        g2d.dispose();
    }

    public void clearCanvas() {
        // Clear the canvas image by filling it with an opaque white background
        Graphics2D g = canvasImage.createGraphics();
        g.setComposite(java.awt.AlphaComposite.Src);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasImage.getWidth(), canvasImage.getHeight());
        g.dispose();

        // Now, apply type-specific clearing
        if (canvasType == CanvasType.COMPOSITION) {
            if (drawableItems != null) {
                drawableItems.clear();

            }
            selectedItem = null; // Deselect any item
        }
        repaint(); // Request a redraw of the canvas after clearing

    }

    public BufferedImage getImage() {
        // For composition canvas, render all drawable items onto a new BufferedImage
        if (canvasType == CanvasType.COMPOSITION) {
            BufferedImage composedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = composedImage.createGraphics();

            // Set rendering hints for quality during saving
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2d.setColor(Color.WHITE); // Fill background with white
            g2d.fillRect(0, 0, getWidth(), getHeight());

            AffineTransform canvasRotation = new AffineTransform(); // 1. Apply the current canvas rotation
            g2d.transform(canvasRotation);

            if (drawableItems != null) {
                for (DrawableItem item : drawableItems) {
                    item.draw(g2d);
                }
            }
            g2d.dispose();
            return composedImage;
        }
        return canvasImage;
    }

    public void setBrushSize(int size) {
        this.brushSize = size;
    }

    public int getBrushSize() {
        return brushSize;
    }

    public void setBrushColor(Color color) {
        this.brushColor = color;
    }

    public Color getBrushColor() {
        return brushColor;
    }

    // Method to set the current creation type for the composition canvas
    public void setCurrentCreation(CreationType creationType) {
        if (this.canvasType == CanvasType.COMPOSITION) {
            this.currentSelectedCreation = creationType;
            toolManager.setTool(new CreationTool(this));
        }
    }

    public CreationType getCurrentCreation() {
        return currentSelectedCreation;
    }

    // get the list of drawable items
    public List<DrawableItem> getDrawableItems() {
        return drawableItems;
    }

    // Setter for drawable items
    public void setDrawableItems(List<DrawableItem> items) {
        if (this.canvasType == CanvasType.COMPOSITION) {
            this.drawableItems = items;
            repaint();
        }
    }

    public CanvasType getCanvasType() {
        return this.canvasType;
    }

    /**
     * Flips the currently selected DrawableItem on the composition canvas
     * horizontally.
     */
    public void flipSelectedItemX() {
        if (canvasType == CanvasType.COMPOSITION && selectedItem != null) {
            selectedItem.setFlippedX(!selectedItem.isFlippedX());
            repaint();
        }
    }

    /**
     * Flips the currently selected DrawableItem on the composition canvas
     * vertically.
     */
    public void flipSelectedItemY() {
        if (canvasType == CanvasType.COMPOSITION && selectedItem != null) {
            selectedItem.setFlippedY(!selectedItem.isFlippedY());
            repaint();
        }
    }

    // Rotates the currently selected DrawableItem on the composition canvas.
    public void rotateSelectedItem(double angle) {
        if (canvasType == CanvasType.COMPOSITION && selectedItem != null) {
            selectedItem.setRotationAngle(selectedItem.getRotationAngle() + angle);
            repaint();
        }
    }

    // Scales DrawableItem on the composition canvas.
    public void scaleSelectedItem(double scale) {
        if (canvasType == CanvasType.COMPOSITION && selectedItem != null) {
            selectedItem.setScale(scale);
            repaint();
        }
    }

    // Rotates the entire composition canvas.
    public void rotateCanvas(double angle) {
        if (canvasType == CanvasType.COMPOSITION) {
            // If an item is selected, rotate the item. Otherwise, rotate the entire canvas.
            if (selectedItem != null) {
                selectedItem.setRotationAngle(selectedItem.getRotationAngle() + angle);
            } else {
                this.canvasRotationAngle += angle;
                // Keep angle within 0-360 or -360 to 360
                this.canvasRotationAngle %= 360;
                if (this.canvasRotationAngle < 0) {
                    this.canvasRotationAngle += 360; // Normalize negative angles
                }
            }
            repaint();
        }
    }

    private void saveStateForUndo() {
        if (canvasType == CanvasType.COMPOSITION) {
            List<DrawableItem> currentItemsCopy = new ArrayList<>();
            for (DrawableItem item : drawableItems) {
                currentItemsCopy.add(item.deepCopy());
            }
            undoStackComposition.push(currentItemsCopy);
            redoStackComposition.clear();
        } else { // Drawing canvas
            BufferedImage snapshot = new BufferedImage(
                    canvasImage.getWidth(), canvasImage.getHeight(), canvasImage.getType());
            Graphics g = snapshot.getGraphics();
            g.drawImage(canvasImage, 0, 0, null);
            g.dispose();
            undoStackDrawing.push(snapshot);
            redoStackDrawing.clear();
        }
    }

    public void undo() {
        if (canvasType == CanvasType.COMPOSITION) {
            if (!undoStackComposition.isEmpty()) {
                List<DrawableItem> currentItemsCopy = new ArrayList<>();
                for (DrawableItem item : drawableItems) {
                    currentItemsCopy.add(item.deepCopy());
                }
                redoStackComposition.push(currentItemsCopy);

                List<DrawableItem> previousState = undoStackComposition.pop();
                this.drawableItems.clear();
                this.drawableItems.addAll(previousState);
                setSelectedItem((DrawableItem) null); // Deselect any item
                repaint();
            }
        } else { // Drawing canvas
            if (!undoStackDrawing.isEmpty()) {
                redoStackDrawing.push(copyCanvas());
                BufferedImage previous = undoStackDrawing.pop();
                drawImageOnCanvas(previous);
                repaint();
            }
        }
    }

    public void redo() {
        if (canvasType == CanvasType.COMPOSITION) {
            if (!redoStackComposition.isEmpty()) {
                List<DrawableItem> currentItemsCopy = new ArrayList<>();
                for (DrawableItem item : drawableItems) {
                    currentItemsCopy.add(item.deepCopy());
                }
                undoStackComposition.push(currentItemsCopy);

                List<DrawableItem> nextState = redoStackComposition.pop();
                this.drawableItems.clear();
                this.drawableItems.addAll(nextState);
                setSelectedItem((DrawableItem) null); // Deselect any item
                repaint();
            }
        } else { // Drawing canvas
            if (!redoStackDrawing.isEmpty()) {
                undoStackDrawing.push(copyCanvas());
                BufferedImage next = redoStackDrawing.pop();
                drawImageOnCanvas(next);
                repaint();
            }
        }
    }

    private BufferedImage copyCanvas() {
        BufferedImage copy = new BufferedImage(
                canvasImage.getWidth(), canvasImage.getHeight(), canvasImage.getType());
        Graphics g = copy.getGraphics();
        g.drawImage(canvasImage, 0, 0, null);
        g.dispose();
        return copy;
    }

    public void drawImageOnCanvas(BufferedImage img) {
        Graphics g = canvasImage.createGraphics(); // Use createGraphics for new drawing operations
        g.drawImage(img, 0, 0, null);
        g.dispose();
    }

    public void setSelectedItem(DrawableItem item) {
        this.selectedItem = item;
        firePropertyChange("selectedItem", null, item);
        repaint();
    }

    public DrawableItem getSelectedItem() {
        return selectedItem;
    }

    public double getCanvasRotationAngle() {
        return canvasRotationAngle;
    }
}
