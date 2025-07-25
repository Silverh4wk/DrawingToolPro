package src;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

public class CreationType {
    private String name;
    private BufferedImage customImage;
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

    // BufferedImage
    public CreationType(BufferedImage image, String mergedLayer) {
        this.image = new ImageIcon(image);
        this.name = mergedLayer;
    }

    // for custom images
    public CreationType(String name, BufferedImage customImage) {
        this.name = name;
        this.customImage = customImage;
        this.image = new ImageIcon(customImage);

    }

    public BufferedImage getCustom() {
        return customImage;
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
