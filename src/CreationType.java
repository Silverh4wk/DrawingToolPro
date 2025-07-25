package src;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class CreationType {
    private String name;
    private ImageIcon image;
    
    // Constructor for CreationType with name and image path
    public CreationType(String name, String imagePath) {
        this.name = name;
        if (imagePath != null) {
            this.image = new ImageIcon(imagePath);
        }
    }
    // Constructor for CreationType with ImageIcon
    public CreationType(String name, ImageIcon imageIcon) {
        this.name = name;
        this.image = imageIcon;
    }
    //BufferedImage
    public CreationType(BufferedImage image, String mergedLayer) {
        this.image = new ImageIcon(image);
        this.name = mergedLayer;
    }

    public String getName() {
        return name;
    }

    public ImageIcon getImage() {
        return image;
    }

    protected void setImage(ImageIcon image) {
        this.image = image;
    }
}
