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
            FileFilter bmpFilter = new FileNameExtensionFilter("BMP Images (*.bmp)", "bmp");

            // add the filters to file chooser
            fileChooser.addChoosableFileFilter(pngFilter);
            fileChooser.addChoosableFileFilter(jpgFilter);
            fileChooser.addChoosableFileFilter(bmpFilter);
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
            } else if (selectedFilter == bmpFilter) {
                format = "bmp";
                primaryExtension = "bmp";
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
                } else if (selectedFilter == bmpFilter) {
                    validExtension = fileName.endsWith(".bmp");
                }

                // reject the invalid extensions 
                if (!validExtension) {
                    String allowed;
                    if (selectedFilter == pngFilter) {
                        allowed = ".png";
                    } else if (selectedFilter == jpgFilter) {
                        allowed = ".jpg or .jpeg";
                    } else { 
                        allowed = ".bmp";
                    }
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

            // For JPEG: removing the transparency by converting to RGB
            if (format.equalsIgnoreCase("jpeg") || format.equalsIgnoreCase("bmp")) {
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