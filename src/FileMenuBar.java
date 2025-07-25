package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
// Tc2l Lab Exercise for CP6224
// Group Members:
// TODO | 
//                     
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 

import javax.swing.*;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.Component; // Needed for SwingUtilities.getWindowAncestor
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FileMenuBar extends JMenuBar {

    private CanvasPanel leftCanvasPanel; // Reference to the left (composition) canvas
    private CanvasPanel rightCanvasPanel; // Reference to the right (drawing) canvas
    private CreationPanel creationPanel;

    // constructor to accept both CanvasPanel instances
    public FileMenuBar(CanvasPanel leftCanvasPanel, CanvasPanel rightCanvasPanel, CreationPanel creationPanel) {
        this.leftCanvasPanel = leftCanvasPanel;
        this.rightCanvasPanel = rightCanvasPanel;
        this.creationPanel = creationPanel;

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu themeMenu = new JMenu("Themes");

        // File menu items
        JMenuItem saveDrawingCanvasItem = new JMenuItem("Save Drawing Canvas");
        saveDrawingCanvasItem.addActionListener(e -> saveCanvas(rightCanvasPanel, "drawing_artwork.png"));
        fileMenu.add(saveDrawingCanvasItem);

        JMenuItem saveCompositionCanvasItem = new JMenuItem("Save Composition Canvas");
        saveCompositionCanvasItem.addActionListener(e -> saveCanvas(leftCanvasPanel, "composition_artwork.png"));
        fileMenu.add(saveCompositionCanvasItem);

        // Add "Add to Library" for current drawing canvas
        JMenuItem addDrawingToLibraryItem = new JMenuItem("Add Drawing to Library");
        addDrawingToLibraryItem.addActionListener(e -> addCanvasToLibrary(rightCanvasPanel));
        fileMenu.add(addDrawingToLibraryItem);


        // Theme menu items
        JMenuItem lightItem = new JMenuItem("Flat Light");
        JMenuItem darkItem = new JMenuItem("Flat Dark");
        JMenuItem intelliJItem = new JMenuItem("Flat IntelliJ");
        JMenuItem darculaItem = new JMenuItem("Flat Darcula");

        lightItem.addActionListener(e -> setTheme(new FlatLightLaf(), this));
        darkItem.addActionListener(e -> setTheme(new FlatDarkLaf(), this));
        intelliJItem.addActionListener(e -> setTheme(new FlatIntelliJLaf(), this));
        darculaItem.addActionListener(e -> setTheme(new FlatDarculaLaf(), this));

        themeMenu.add(lightItem);
        themeMenu.add(darkItem);
        themeMenu.add(intelliJItem);
        themeMenu.add(darculaItem);

        menuBar.add(fileMenu);
        menuBar.add(themeMenu);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(menuBar);
    }

    private void saveCanvas(CanvasPanel canvas, String defaultFileName) {
        if (canvas == null) {
            JOptionPane.showMessageDialog(this, "Canvas is not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Canvas As");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }

            try {
                BufferedImage image = canvas.getImage();
                ImageIO.write(image, "png", fileToSave);
                JOptionPane.showMessageDialog(this, "Image saved successfully to " + fileToSave.getAbsolutePath(), "Save Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving image: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    private void addCanvasToLibrary(CanvasPanel canvas) {
        if (canvas == null) {
            JOptionPane.showMessageDialog(this, "Canvas is not initialized.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        BufferedImage sourceImage = canvas.getImage(); // Get the image from the canvas
        if (sourceImage == null) {
            JOptionPane.showMessageDialog(this, "No image to add to library.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // create a deep copy of the BufferedImage to ensure it's independent ---
        BufferedImage imageCopy = new BufferedImage(
            sourceImage.getColorModel(),
            sourceImage.copyData(null), // Copy the pixel data
            sourceImage.isAlphaPremultiplied(),
            null
        );

        String artworkName = JOptionPane.showInputDialog(this, "Enter a name for your Drawing:", "Drawing Name", JOptionPane.QUESTION_MESSAGE);
        if (artworkName != null && !artworkName.trim().isEmpty()) {
            if (creationPanel != null) {
                // Pass the deep copy to CustomCreation
                CustomCreation customCreation = new CustomCreation(artworkName, imageCopy);
                creationPanel.addCustomCreation(customCreation);
                JOptionPane.showMessageDialog(this, "Drawing added to library!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Creation library not available.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Drawing not added. Name cannot be empty.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void setTheme(LookAndFeel laf, Component component) {
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(SwingUtilities.getWindowAncestor(this));
        } catch (Exception ex) {
            System.err.println("Failed to apply theme: " + ex.getMessage());
        }
    }
}
