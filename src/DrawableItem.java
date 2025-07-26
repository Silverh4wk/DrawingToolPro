package src;

import java.awt.*;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

/**
 * Represents an item that can be drawn on a canvas with properties such as
 * position,
 * rotation, scaling, and flipping. This class is used to manage drawable items
 * in a
 * graphics application.
 */
public class DrawableItem {
    private CreationType creationType;
    private int x;
    private int y;
    private double rotationAngle; // in degrees
    private double scaleX;
    private double scaleY;
    private boolean flippedX;
    private boolean flippedY;
    private int imageWidth;
    private int imageHeight;
    private static final int MARGIN = 2;
    private double scale = 1.0;

    public DrawableItem(CreationType creationType, int x, int y) {
        this.creationType = creationType;
        this.x = x;
        this.y = y;
        this.rotationAngle = 0;
        this.scaleX = 1.0;
        this.scaleY = 1.0;
        this.flippedX = false;
        this.flippedY = false;
    }

    // Deep copy constructor
    public DrawableItem(DrawableItem other) {
        this.creationType = other.creationType;

        this.x = other.x;
        this.y = other.y;
        this.rotationAngle = other.rotationAngle;
        this.scaleX = other.scaleX;
        this.scaleY = other.scaleY;
        this.flippedX = other.flippedX;
        this.flippedY = other.flippedY;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public DrawableItem deepCopy() {
        return new DrawableItem(this);
    }

    public CreationType getCreationType() {
        return creationType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public void setRotationAngle(double rotationAngle) {
        this.rotationAngle = rotationAngle % 360;
        if (this.rotationAngle < 0) {
            this.rotationAngle += 360;
        }
    }

    public double getScaleX() {
        return scaleX;
    }

    public void setScaleX(double scaleX) {
        this.scaleX = scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }

    public void setScaleY(double scaleY) {
        this.scaleY = scaleY;
    }

    public boolean isFlippedX() {
        return flippedX;
    }

    public void setFlippedX(boolean flippedX) {
        this.flippedX = flippedX;
    }

    public boolean isFlippedY() {
        return flippedY;
    }

    public void setFlippedY(boolean flippedY) {
        this.flippedY = flippedY;
    }

    public Point getCenter() {
        return new Point(x, y);
    }

    public double getScale() {
        return scale;
    }

    // Draws the item on the provided Graphics context.
    public void draw(Graphics g) {
        ImageIcon imageIcon = creationType.getImage();
        if (imageIcon == null || imageIcon.getImage() == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        Image image = imageIcon.getImage();

        imageWidth = image.getWidth(null);
        imageHeight = image.getHeight(null);

        double scaleXActual = scaleX * (flippedX ? -1 : 1);
        double scaleYActual = scaleY * (flippedY ? -1 : 1);

        AffineTransform transform = new AffineTransform();

        // Translate to the center point (x, y)
        transform.translate(x, y);

        // Apply rotation
        transform.rotate(Math.toRadians(rotationAngle));

        // Apply flipping and scaling
        transform.scale(scaleXActual, scaleYActual);

        // Move the image so it is centered
        transform.translate(-imageWidth / 2.0, -imageHeight / 2.0);

        g2d.drawImage(image, transform, null);
    }

    // Returns the bounding rectangle of the drawable item.
    public Rectangle getBounds() {
        ImageIcon imageIcon = creationType.getImage();
        if (imageIcon == null || imageIcon.getImage() == null) {
            return new Rectangle(x, y, 0, 0);
        }

        Image imageToDraw = imageIcon.getImage();

        int width = this.imageWidth;
        int height = this.imageHeight;
        // Calculate scaled dimensions
        int scaledWidth = (int) (width * Math.abs(scaleX));
        int scaledHeight = (int) (height * Math.abs(scaleY));

        scaledWidth += 2 * MARGIN;
        scaledHeight += 2 * MARGIN;

        // Return centered bounding box
        return new Rectangle(
                x - scaledWidth / 2,
                y - scaledHeight / 2,
                scaledWidth,
                scaledHeight);
    }

    // Sets both X and Y scale factors to the same value.
    public void setScale(double scale) {
        scale = Math.max(0.1, Math.min(4.0, scale));
        this.scaleX = scale;
        this.scaleY = scale;
    }
}
