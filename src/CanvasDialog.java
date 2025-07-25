package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
// Tc2l Lab Exercise for CP6224
// Group Members:
// TODO | 
//                     
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 

import Helpers.Helpers.IntVector2D;
import java.awt.*;
import javax.swing.*;

public class CanvasDialog {

    private IntVector2D leftCanvasDimensions = new IntVector2D(800, 600);
    private IntVector2D rightCanvasDimensions = new IntVector2D(800, 600);
    private int dividerHeight = 10;
    private int gridRow = 0;
    private int gridColumn = 1; 
    private int gridHgap = 5;
    private int gridVgap = 5;

    public boolean showDialog(JFrame parent) {
        JTextField leftWidthField = new JTextField("800");
        JTextField leftHeightField = new JTextField("600");
        JTextField rightWidthField = new JTextField("800");
        JTextField rightHeightField = new JTextField("600");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(gridRow,gridColumn, gridHgap,gridVgap));
        
        // Left Canvas Section
        panel.add(new JLabel("Left Canvas (Composition):"));
        panel.add(new JLabel("Width:"));
        panel.add(leftWidthField);
        panel.add(new JLabel("Height:"));
        panel.add(leftHeightField);
        
        panel.add(Box.createVerticalStrut(dividerHeight)); // To seperate the sections 
        
        // Right Canvas Section
        panel.add(new JLabel("Right Canvas (Drawing):"));
        panel.add(new JLabel("Width:"));
        panel.add(rightWidthField);
        panel.add(new JLabel("Height:"));
        panel.add(rightHeightField);

        int result = JOptionPane.showConfirmDialog(parent, panel, "Canvas Dimensions", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                leftCanvasDimensions.setX(Integer.parseInt(leftWidthField.getText()));
                leftCanvasDimensions.setY(Integer.parseInt(leftHeightField.getText()));
                rightCanvasDimensions.setX(Integer.parseInt(rightWidthField.getText()));
                rightCanvasDimensions.setY(Integer.parseInt(rightHeightField.getText()));
                return true;
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid input. Please enter valid integers only.");
                return false;
            }
        }
        return false;
    }
     // Getters for the left canvas dimensions
    public IntVector2D getLeftCanvasDimensions() {
        return leftCanvasDimensions;
    }
    // Getter for the right canvas dimensions
    public IntVector2D getRightCanvasDimensions() {
        return rightCanvasDimensions;
    }
}
