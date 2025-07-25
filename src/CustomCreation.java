package src;

import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;


public class CustomCreation extends CreationType {
    private BufferedImage bufferedImage; // Store the BufferedImage

    // Constructor for CustomCreation that accepts a name and a BufferedImage
    public CustomCreation(String name, BufferedImage image) {
       
        super(name, new ImageIcon(image)); // Pass ImageIcon directly to super
        this.bufferedImage = image;
    }

    // Constructor for CustomCreation that accepts a name and an image path
    @Override
    public ImageIcon getImage() {
        if (bufferedImage != null) {
            return new ImageIcon(bufferedImage);
        }
        return null; // Return null 
    }

    //Getter for BufferedImage
    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }
}
