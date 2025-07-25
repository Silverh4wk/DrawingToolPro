package Tools;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
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
     * ToDo(Hazim:) Add a drop down menu later maybe for file format choosing.... nice way of not remembering to do this dumb
     */

public class SaveImageTool {
    public static void saveImage(CanvasPanel panel) {
        try {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Image");

            // filters for supported formats
            FileFilter pngFilter = new FileNameExtensionFilter("PNG Images (*.png)", "png");
            FileFilter jpgFilter = new FileNameExtensionFilter("JPEG Images (*.jpg)", "jpg", "jpeg");

            // add the filters to file chooser
            fileChooser.addChoosableFileFilter(pngFilter);
            fileChooser.addChoosableFileFilter(jpgFilter);
            fileChooser.setFileFilter(pngFilter); // png as default

            int option = fileChooser.showSaveDialog(panel);
            if (option != JFileChooser.APPROVE_OPTION)
                return;

            File file = fileChooser.getSelectedFile();
            FileFilter selectedFilter = fileChooser.getFileFilter();

            // determine the format based on selected filter
            String format = "png";
            String primaryExtension = "png";

            if (selectedFilter == jpgFilter) {
                format = "jpeg";
                primaryExtension = "jpg";
            }

            String filePath = file.getAbsolutePath();
            String fileName = file.getName().toLowerCase();

            // to make sure the user cant add any other extensino end such as mp3
            boolean hasExtension = fileName.contains(".");

            if (hasExtension) {
                boolean validExtension = false;

                if (selectedFilter == pngFilter) {
                    validExtension = fileName.endsWith(".png");
                } else if (selectedFilter == jpgFilter) {
                    validExtension = fileName.endsWith(".jpg") || fileName.endsWith(".jpeg");
                }

                // reject the invalid extensions ... idk why but the jpg isnt working for
                // somereason
                // need to work more on this
                if (!validExtension) {
                    String allowed = (selectedFilter == pngFilter) ? ".png" : ".jpg or .jpeg";
                    JOptionPane.showMessageDialog(panel,
                            "Invalid file extension. Please use " + allowed,
                            "Invalid Extension",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else {
                filePath += "." + primaryExtension;
                file = new File(filePath);
            }

            // Check if file exists and confirm overwrite
            if (file.exists()) {
                int overwrite = JOptionPane.showConfirmDialog(
                        panel,
                        "File already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);

                if (overwrite != JOptionPane.YES_OPTION) {
                    return; // User cancelled overwrite
                }
            }

            // Save the image
            BufferedImage image = panel.getImage();

            // For JPEG: remove transparency by converting to RGB
            if (format.equalsIgnoreCase("jpeg")) {
                BufferedImage rgbImage = new BufferedImage(
                        image.getWidth(),
                        image.getHeight(),
                        BufferedImage.TYPE_INT_RGB);
                rgbImage.createGraphics().drawImage(image, 0, 0, null);
                image = rgbImage;
            }

            boolean success = ImageIO.write(image, format, file);

            if (!success) {
                throw new Exception("ImageIO failed to save the image.");
            }
            JOptionPane.showMessageDialog(panel, "Image saved to: " + file.getAbsolutePath());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel,
                    "Failed to save image: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}