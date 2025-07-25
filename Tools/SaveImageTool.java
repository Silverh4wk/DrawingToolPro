package Tools;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import src.CanvasPanel;

/*
     * Saving Image function:
     * Variables:
     * JFileChooser fileChooser : provides a simple mechanism for the user to choose
     * a file.
     * File File : An abstract representation of file and directory pathnames.
     * String fileName : A string that stores the filename.
     * String format : to determine the format of saving based (png or jpg)
     * 
     * A function to allow saving the images wherever u choose to and based on the
     * format that u choose
     * altho u have to specify the file name and the format before saving
     * (filename.jpg or filename.png) which is icky, might need
     * to add a drop down menu later so
     * ToDo(Hazim:) Add a drop down menu later maybe for file format choosing
     */
    
public class SaveImageTool {
    public static void saveImage(CanvasPanel panel) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");

            int option = fileChooser.showSaveDialog(panel);

            if (option == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName().toLowerCase();

                String format;
                if (fileName.endsWith(".png")) {
                    format = "png";
                } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
                    format = "jpg";
                } else {
                    format = "png";
                    file = new File(file.getAbsolutePath() + ".png");
                }

                ImageIO.write(panel.getImage(), format, file);
                JOptionPane.showMessageDialog(panel, "Image saved to: " + file.getAbsolutePath());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "Failed to save: " + ex.getMessage());
        }
    }
}
