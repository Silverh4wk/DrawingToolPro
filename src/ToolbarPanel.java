package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * // Tc2l Lab Exercise for CP6224

// Group Members:
// TODO |
//
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * // File: src/ToolbarPanel.java

import Helpers.*;
import Tools.*;
import javafx.scene.input.Clipboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.*;
import javax.swing.border.*;

public class ToolbarPanel extends JToolBar {
    private final ToolManager toolManager;
    private final CanvasPanel leftCanvasPanel;
    private final CanvasPanel rightCanvasPanel;
    private final Consumer<Color> colorChangeCallback;
    private final CreationPanel creationPanel;
    private JButton leftCanvasBtn;
    private JButton rightCanvasBtn;
    private CanvasPanel activeCanvas;
    public int iconSizeX = 24;
    public int iconSizeY = 24;
    public int brushSize = 0;
    private int lastMouseY = 0;
    private boolean isZooming = false;
    private JButton mergeButton;
    private int majorTickSpacing = 50; // Major tick spacing for scale slider
    private int verticalGap = 5; // Placeholder for flip vertical gap
    private int horizontalGap = 5; // Placeholder for flip horizontal gap
    private int dimensionWidth = 250; // Width of the toolbar panel
    private int colorR = 64;
    private int colorG = 64;
    private int colorB = 64;

    public ToolbarPanel(ToolManager toolManager, CanvasPanel leftCanvasPanel, CanvasPanel rightCanvasPanel,
            Consumer<Color> colorChangeCallback, CreationPanel creationPanel) {
        super(JToolBar.VERTICAL);
        setFloatable(true); // for draggin it around
        setRollover(true);
        this.toolManager = toolManager;
        this.leftCanvasPanel = leftCanvasPanel;
        this.rightCanvasPanel = rightCanvasPanel;
        this.colorChangeCallback = colorChangeCallback;
        this.creationPanel = creationPanel;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(dimensionWidth, getHeight()));
        setBackground(new Color(colorR, colorG, colorB)); // Dark background
        initializeToolbar();
        setupKeyBindings();
    }

    private void initializeToolbar() {
        // Drawing Tools Section
        addSection("Drawing Tools", createDrawingToolsPanel());

        // Active Canvas Section
        addSection("Active Canvas", createCanvasSelectionPanel());

        // Library Categories Section
        addSection("Library Categories", createLibraryCategoriesPanel());

        // Image Tools Section
        addSection("Image Tools", createImageToolsPanel());
    }

    private void addSection(String title, JPanel content) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(titleLabel);

        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        // content.setBackground(new Color(colorR, colorG, colorB));
        add(content);
        addSeparator(new Dimension(0, 10));
        // add(Box.createVerticalStrut(10)); // Spacing between sections
        // add(new JSeparator());
        // add(Box.createVerticalStrut(10));
    }

    private JPanel createDrawingToolsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, horizontalGap, verticalGap));
        panel.setBackground(new Color(colorR, colorG, colorB));

        // Pen Tool
        addToolButton(panel, "icons/toolbar/pen.png", new PenTool(), "");

        // Eraser Tool
        addToolButton(panel, "icons/toolbar/eraser.png", new EraserTool(Color.WHITE), "");

        // Clear Canvas Button
        JButton clearBtn = createIconButton("icons/toolbar/clear.png", "");
        clearBtn.addActionListener(e -> {
            if (activeCanvas != null) { // Add a null check for safety
                new ClearCanvasTool(activeCanvas).onPress(); // Clear ONLY the activeCanvas
            } else {
                System.out.println("No active canvas selected to clear.");
            }
        });
        panel.add(clearBtn);

        // Color Selector
        JButton colorBtn = createIconButton("icons/toolbar/colorChooser.png", "");
        colorBtn.addActionListener(e -> ColorSelectorTool.onPress(rightCanvasPanel, colorChangeCallback));
        panel.add(colorBtn);
        // zoom
        JButton zoomResetBtn = createIconButton("icons/toolbar/zoom_reset.png", "");
        zoomResetBtn.addActionListener(e -> rightCanvasPanel.resetZoom());
        zoomResetBtn.addActionListener(e -> leftCanvasPanel.resetZoom());
        rightCanvasPanel.repaint(); // fixed the build problem for mac lol
        panel.add(zoomResetBtn);

        // Merge Button
        mergeButton = createIconButton("icons/toolbar/merge.png", "");
        mergeButton.addActionListener(e -> {
            CanvasMerger.insertImageLayer(leftCanvasPanel, rightCanvasPanel);
        });
        panel.add(mergeButton);

        // custom images
        JButton importImageBtn = createIconButton("icons/toolbar/import.png", "");
        importImageBtn.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select an Image File");
            fileChooser.addChoosableFileFilter(new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif", "bmp"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedImage image = ImageIO.read(fileChooser.getSelectedFile());
                    if (image != null) {
                        leftCanvasPanel.placeImportedImage(image);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error loading image file", "Import Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        panel.add(importImageBtn);

        // pasting from clipboard
        JButton pasteImageBtn = createIconButton("icons/toolbar/paste.png", "");
        pasteImageBtn.addActionListener(e -> {
            // get the system clipboard
            java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);

            // check if it has an image
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                try {
                    Image raw = (Image) contents.getTransferData(DataFlavor.imageFlavor);
                    // convert to BufferedImage
                    BufferedImage buf;
                    if (raw instanceof BufferedImage) {
                        buf = (BufferedImage) raw;
                    } else {
                        // draw into a BufferedImage
                        buf = new BufferedImage(
                                raw.getWidth(null),
                                raw.getHeight(null),
                                BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2 = buf.createGraphics();
                        g2.drawImage(raw, 0, 0, null);
                        g2.dispose();
                    }

                    leftCanvasPanel.placeImportedImage(buf);

                } catch (UnsupportedFlavorException | IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Clipboard does not contain a valid image",
                            "Paste Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No image found in clipboard",
                        "Paste Error",
                        JOptionPane.WARNING_MESSAGE);
            }
        });
        panel.add(pasteImageBtn);
        return panel;

    }

    // pasting from clipboard
    private void pasteFromClipboard() {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);

        if (contents != null && contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            try {
                Image raw = (Image) contents.getTransferData(DataFlavor.imageFlavor);

                BufferedImage buf;
                if (raw instanceof BufferedImage) {
                    buf = (BufferedImage) raw;
                } else {
                    buf = new BufferedImage(
                            raw.getWidth(null),
                            raw.getHeight(null),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = buf.createGraphics();
                    g2.drawImage(raw, 0, 0, null);
                    g2.dispose();
                }

                leftCanvasPanel.placeImportedImage(buf);

            } catch (UnsupportedFlavorException | IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Clipboard does not contain a valid image",
                        "Paste Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "No image found in clipboard",
                    "Paste Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void bindPasteShortcut() {
        int menuMask = Toolkit.getDefaultToolkit()
                .getMenuShortcutKeyMaskEx();

        KeyStroke pasteKS = KeyStroke.getKeyStroke(KeyEvent.VK_V, menuMask);

        InputMap im = leftCanvasPanel
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = leftCanvasPanel.getActionMap();

        im.put(pasteKS, "pasteImage");

        am.put("pasteImage", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteFromClipboard();
            }
        });
    }

    private JPanel createCanvasSelectionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, horizontalGap, verticalGap));
        panel.setBackground(new Color(colorR, colorG, colorB));

        leftCanvasBtn = createIconButton("icons/toolbar/art.png", "Composition");
        rightCanvasBtn = createIconButton("icons/toolbar/canvas.png", "Drawing");

        leftCanvasBtn.addActionListener(e -> setActiveCanvas(leftCanvasPanel));
        rightCanvasBtn.addActionListener(e -> setActiveCanvas(rightCanvasPanel));

        panel.add(leftCanvasBtn);
        panel.add(rightCanvasBtn);

        setActiveCanvas(rightCanvasPanel); // Set initial active canvas

        return panel;
    }

    private JPanel createLibraryCategoriesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, horizontalGap, verticalGap));
        panel.setBackground(new Color(colorR, colorG, colorB));

        String[][] categories = {
                { "Animal", "icons/toolbar/animal.png" },
                { "Flower", "icons/toolbar/flower.png" },
                { "Custom", "icons/toolbar/custom.png" }
        };

        for (String[] category : categories) {
            JButton btn = createIconButton(category[1], category[0]);
            btn.addActionListener(e -> creationPanel.filterCreations(category[0]));
            panel.add(btn);
        }

        return panel;
    }

    private JPanel createImageToolsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(colorR, colorG, colorB));

        // Rotation Slider
        JLabel rotationLabel = new JLabel("Rotation: 0Â°");
        rotationLabel.setForeground(Color.WHITE);
        JSlider rotationSlider = new JSlider(0, 360, 0);
        rotationSlider.setName("transform_rotation");
        rotationSlider.setBackground(new Color(colorR, colorG, colorB));
        rotationSlider.setForeground(Color.WHITE);
        configureSlider(rotationSlider, 90);

        // Scale Slider
        JLabel scaleLabel = new JLabel("Scale: 100%");
        scaleLabel.setForeground(Color.WHITE);
        JSlider scaleSlider = new JSlider(50, 200, 100); // 50% to 200%
        scaleSlider.setName("transform_scale");
        scaleSlider.setBackground(new Color(colorR, colorG, colorB));
        scaleSlider.setForeground(Color.WHITE);
        configureSlider(scaleSlider, majorTickSpacing);

        // Flip Panel
        JPanel flipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, horizontalGap, verticalGap));
        flipPanel.setBackground(new Color(colorR, colorG, colorB));

        JButton flipHBtn = createIconButton("icons/toolbar/flip_h.png", "Flip H");
        JButton flipVBtn = createIconButton("icons/toolbar/flip_v.png", "Flip V");

        flipHBtn.setName("transform_flipH");
        flipVBtn.setName("transform_flipV");

        // Update the event listeners for sliders and buttons
        // (Note:hazim) added a rotation for the right canvas;;
        // as an artist, i need it to rotate using bindings
        rotationSlider.addChangeListener(e -> {
            if (!rotationSlider.getValueIsAdjusting()) {
                int value = rotationSlider.getValue();
                rotationLabel.setText("Rotation: " + value + "Â°");

                if (activeCanvas == leftCanvasPanel) {
                    if (activeCanvas.getSelectedItem() != null) {
                        activeCanvas.getSelectedItem().setRotationAngle(value);
                    } else {
                        activeCanvas.setRotationAngle(value);
                    }
                } else {
                    rightCanvasPanel.setRotationAngle(value);
                }

                activeCanvas.repaint();
            }
        });

        scaleSlider.addChangeListener(e -> {
            if (activeCanvas == leftCanvasPanel && !scaleSlider.getValueIsAdjusting()) {
                double scale = scaleSlider.getValue() / 100.0;
                scaleLabel.setText("Scale: " + scaleSlider.getValue() + "%");
                if (leftCanvasPanel.getSelectedItem() != null) {
                    leftCanvasPanel.scaleSelectedItem(scale);
                }
            }
        });

        flipHBtn.addActionListener(e -> {
            if (activeCanvas == leftCanvasPanel) {
                leftCanvasPanel.flipSelectedItemX();
            }
        });

        flipVBtn.addActionListener(e -> {
            if (activeCanvas == leftCanvasPanel) {
                leftCanvasPanel.flipSelectedItemY();
            }
        });

        // Add components to panel
        panel.add(rotationLabel);
        panel.add(rotationSlider);
        panel.add(Box.createVerticalStrut(10));
        panel.add(scaleLabel);
        panel.add(scaleSlider);
        panel.add(Box.createVerticalStrut(10));

        // Add flip buttons to flip panel
        flipPanel.add(flipHBtn);
        flipPanel.add(flipVBtn);
        panel.add(flipPanel);

        return panel;
    }

    // * Configures the slider with ticks and labels
    private void configureSlider(JSlider slider, int majorTick) {
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing(majorTick);
        slider.setMinorTickSpacing(majorTick / 2);
    }

    // * Creates an icon button with the specified icon path and text
    private JButton createIconButton(String iconPath, String text) {
        ImageIcon icon = new ImageIcon(iconPath);
        icon = Helpers.iconSizeChanger(icon, iconSizeX, iconSizeY);

        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFocusPainted(false);

        return button;
    }

    // * Adds a tool button to the specified panel with an icon and action listener
    private void addToolButton(JPanel panel, String iconPath, Tool tool, String text) {
        JButton button = createIconButton(iconPath, text);
        button.addActionListener(e -> {
            if (activeCanvas == rightCanvasPanel) {
                toolManager.setTool(tool);
            }
        });
        panel.add(button);
    }

    // * Sets the active canvas
    private void setActiveCanvas(CanvasPanel canvas) {
        this.activeCanvas = canvas;
        leftCanvasBtn.setBackground(canvas == leftCanvasPanel ? new Color(80, 80, 80) : new Color(64, 64, 64));
        rightCanvasBtn.setBackground(canvas == rightCanvasPanel ? new Color(80, 80, 80) : new Color(64, 64, 64));

        // Enable/disable transformation controls based on active canvas
        boolean isCompositionCanvas = (canvas == leftCanvasPanel);
        // Iterate through all components in the ToolbarPanel to find relevant controls
        for (Component comp : getComponentsInHierarchy(this)) { // Helper method to get all components
            if (comp instanceof JSlider) {
                JSlider slider = (JSlider) comp;
                if (slider.getName() != null &&
                        (slider.getName().startsWith("transform_"))) {
                    slider.setEnabled(true);
                }
            } else if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if (button.getName() != null &&
                        (button.getName().startsWith("transform_"))) {
                    button.setEnabled(isCompositionCanvas);
                }
            }
        }
    }

    // Helper method to get all components in a container hierarchy
    private List<Component> getComponentsInHierarchy(Container container) {
        List<Component> components = new ArrayList<>();
        for (Component comp : container.getComponents()) {
            components.add(comp);
            if (comp instanceof Container) {
                components.addAll(getComponentsInHierarchy((Container) comp));
            }
        }
        return components;
    }

    private void setupKeyBindings() {
        bindPasteShortcut();
    }

}