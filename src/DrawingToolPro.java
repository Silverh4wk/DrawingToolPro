package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

// Tc2l Lab Exercise for CP6224
// Group Members:
//Note(Hazim): TODO |
// //                 Apply OOPDs concepts (done with this for now)
//                    .also prepare a proper todo list for tasks to be completed
//                    (Hazim: )  (done: undo/redo)Undo button, Apply layering, also fix the read me file .
//                    (Reem: )   Animals, the canvas composition
//                    (Izzat: )    Loading of older projects into canvas,Custom images
//                    (Amirul:)   Canvas rotation, Flowers
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
// File: src/DrawingToolPro.java

import javax.swing.*;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.*;
import Helpers.Helpers.*; // Assuming IntVector2D is here

public class DrawingToolPro extends JFrame {
    private ToolManager toolManager;
    private CanvasPanel leftCanvasPanel; // Left for image composition
    private CanvasPanel rightCanvasPanel; // Right for freehand drawing
    private FileMenuBar fileMenuBar;
    private UtilityBarPanel utilityBarPanel;
    private CreationPanel creationPanel;
    private Color currentColor = Color.BLACK;
    private int creationPanelWidth = 150; // Default width for creation panel
    // (Note:Hazim) this panel will sit in the center
    // left side will be for the image stuff and right side will be for the free
    // hand drawing tool
    // private JSplitPane splitPane;
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public DrawingToolPro(IntVector2D leftCanvasDimensions, IntVector2D rightCanvasDimensions) {
        super("DrawingToolPro");

        setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        toolManager = new ToolManager();

        // Initialize both canvas panels with their respective dimensions
        leftCanvasPanel = new CanvasPanel(toolManager, leftCanvasDimensions, CanvasPanel.CanvasType.COMPOSITION);
        rightCanvasPanel = new CanvasPanel(toolManager, rightCanvasDimensions, CanvasPanel.CanvasType.DRAWING);

        // Instantiate CreationPanel FIRST, as it's needed by ToolbarPanel and
        // FileMenuBar
        creationPanel = new CreationPanel(leftCanvasPanel);

        // Pass both canvas panels and creationPanel to ToolbarPanel constructor
        ToolbarPanel toolbar = new ToolbarPanel(toolManager, leftCanvasPanel, rightCanvasPanel, this::onColorChange,
                creationPanel);

        // Pass both canvas panels and creationPanel to FileMenuBar
        fileMenuBar = new FileMenuBar(leftCanvasPanel, rightCanvasPanel, creationPanel);

        // Set up menu bar
        setJMenuBar(fileMenuBar);

        // Utility bar (Undo/Redo, Brush Size) for the drawing canvas
        utilityBarPanel = new UtilityBarPanel(rightCanvasPanel);
        rightCanvasPanel.setBrushColor(currentColor);

        // Configure utility wrapper with separator
        JPanel utilityWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        utilityWrapper.add(utilityBarPanel);

        // Create north panel with separator line
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(utilityWrapper, BorderLayout.CENTER);
        northPanel.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

        // Minimum sizes for split pane components
        Dimension creationPanelMinSize = new Dimension(creationPanelWidth, leftCanvasDimensions.getY());
        creationPanel.setMinimumSize(creationPanelMinSize);

        // Set minimum sizes for canvases
        leftCanvasPanel.setMinimumSize(new Dimension(leftCanvasDimensions.getX() / 3, leftCanvasDimensions.getY()));
        rightCanvasPanel.setMinimumSize(new Dimension(rightCanvasDimensions.getX() / 3, rightCanvasDimensions.getY()));

        JScrollPane leftScrollPane = new JScrollPane(leftCanvasPanel);
        JScrollPane rightScrollPane = new JScrollPane(rightCanvasPanel);

        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        rightScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        rightScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JSplitPane mainSplitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                creationPanel,
                new JSplitPane(
                        JSplitPane.HORIZONTAL_SPLIT,
                        leftScrollPane, 
                        rightScrollPane 
                ));

        mainSplitPane.setOneTouchExpandable(true);
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setResizeWeight(0.2);

        // Add components to frame
        add(northPanel, BorderLayout.NORTH);
        add(toolbar, BorderLayout.WEST);
        add(mainSplitPane, BorderLayout.CENTER);

        setVisible(true);
    }

    private void onColorChange(Color newColor) {
        currentColor = newColor;
        rightCanvasPanel.setBrushColor(newColor); // Only drawing canvas changes color
    }

    public static void main(String[] args) {
        int themeIndex = 2;

        try {
            switch (themeIndex) {
                case 1 -> UIManager.setLookAndFeel(new FlatLightLaf());
                case 2 -> UIManager.setLookAndFeel(new FlatDarkLaf());
                case 3 -> UIManager.setLookAndFeel(new FlatIntelliJLaf());
                case 4 -> UIManager.setLookAndFeel(new FlatDarculaLaf());
                default -> UIManager.setLookAndFeel(new FlatLightLaf());
            }
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            CanvasDialog dialog = new CanvasDialog();
            boolean success = dialog.showDialog(null);
            if (success) {
                IntVector2D leftDimensions = dialog.getLeftCanvasDimensions();
                IntVector2D rightDimensions = dialog.getRightCanvasDimensions();
                new DrawingToolPro(leftDimensions, rightDimensions);
            }
        });
    }
}