package src;
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * // Tc2l Lab Exercise for CP6224

// Group Members:
// TODO |
//
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * // File: src/ToolbarPanel.java

import Helpers.*;
import Tools.*;

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
    private boolean floating = false;
    private Point dragStart;
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
        setupDragHandling();
        setOrientation(JToolBar.VERTICAL);
        this.toolManager = toolManager;
        this.leftCanvasPanel = leftCanvasPanel;
        this.rightCanvasPanel = rightCanvasPanel;
        this.colorChangeCallback = colorChangeCallback;
        this.creationPanel = creationPanel;

        addPropertyChangeListener("floating", evt -> {
            boolean nowFloating = (Boolean) evt.getNewValue();
            setFloating(nowFloating);
            if (nowFloating) {
                setFloatingUI();
                Window w = SwingUtilities.getWindowAncestor(ToolbarPanel.this);
                if (w != null)
                    w.pack();
            } else {
                setDockedUI();
            }
            adjustLayoutForOrientation();
        });
        addPropertyChangeListener("orientation", evt -> adjustLayoutForOrientation());

        setupDockingZones();
        setupDragHandling();
        setupKeyBindings();

        setDockedUI();
        initializeToolbar();

    }

    public boolean isFloating() {
        return floating;
    }

    public void setFloating(boolean fl) {
        this.floating = fl;
    }

    private void initializeToolbar() {
        removeAll();
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
        // titleLabel.setForeground(Color.WHITE);
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
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 5));
        // panel.setBackground(new Color(colorR, colorG, colorB));

        // Pen Tool
        addToolButton(panel, "icons/toolbar/pen.png", new PenTool(), "", "Pen tool");

        // Eraser Tool
        addToolButton(panel, "icons/toolbar/eraser.png", new EraserTool(Color.WHITE), "", "Eraser tool");

        // Clear Canvas Button
        JButton clearBtn = createIconButton("icons/toolbar/clear.png", "", "Clear canvas");
        clearBtn.addActionListener(e -> {
            if (activeCanvas != null) { // Add a null check for safety
                new ClearCanvasTool(activeCanvas).onPress(); // Clear ONLY the activeCanvas
            } else {
                System.out.println("No active canvas selected to clear.");
            }
        });
        panel.add(clearBtn);

        // Color Selector
        JButton colorBtn = createIconButton("icons/toolbar/colorChooser.png", "", "Color selector");
        colorBtn.addActionListener(e -> ColorSelectorTool.onPress(rightCanvasPanel, colorChangeCallback));
        panel.add(colorBtn);
        // zoom
        JButton zoomResetBtn = createIconButton("icons/toolbar/zoom_reset.png", "", "reset the canvas");
        zoomResetBtn.addActionListener(e -> rightCanvasPanel.resetZoom());
        zoomResetBtn.addActionListener(e -> leftCanvasPanel.resetZoom());
        rightCanvasPanel.repaint(); // fixed the build problem for mac lol
        panel.add(zoomResetBtn);

        // Merge Button
        mergeButton = createIconButton("icons/toolbar/merge.png", "", "merge");
        mergeButton.addActionListener(e -> {
            CanvasMerger.insertImageLayer(leftCanvasPanel, rightCanvasPanel);
        });
        panel.add(mergeButton);

        // custom images
        JButton importImageBtn = createIconButton("icons/toolbar/import.png", "", "import image");
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
                        Point position = activeCanvas.getCenterPositionForImage(image);
                        activeCanvas.placeImportedImage(image, position);
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
        JButton pasteImageBtn = createIconButton("icons/toolbar/paste.png", "", "paste image");
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
                    Point position = activeCanvas.getCenterPositionForImage(buf);
                    activeCanvas.placeImportedImage(buf, position);

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
                Point position = activeCanvas.getCenterPositionForImage(buf);
                activeCanvas.placeImportedImage(buf, position);

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
        // panel.setBackground(new Color(colorR, colorG, colorB));

        leftCanvasBtn = createIconButton("icons/toolbar/art.png", "Composition", "Left canvas");
        rightCanvasBtn = createIconButton("icons/toolbar/canvas.png", "Drawing", "Right canvas");

        leftCanvasBtn.addActionListener(e -> setActiveCanvas(leftCanvasPanel));
        rightCanvasBtn.addActionListener(e -> setActiveCanvas(rightCanvasPanel));

        panel.add(leftCanvasBtn);
        panel.add(rightCanvasBtn);

        setActiveCanvas(rightCanvasPanel); // Set initial active canvas

        return panel;
    }

    private JPanel createLibraryCategoriesPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, horizontalGap, verticalGap));
        // panel.setBackground(new Color(colorR, colorG, colorB));

        String[][] categories = {
                { "Animal", "icons/toolbar/animal.png" },
                { "Flower", "icons/toolbar/flower.png" },
                { "Custom", "icons/toolbar/custom.png" }
        };

        for (String[] category : categories) {
            JButton btn = createIconButton(category[1], category[0], "");
            btn.addActionListener(e -> creationPanel.filterCreations(category[0]));
            panel.add(btn);
        }

        return panel;
    }

    private JPanel createImageToolsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        // panel.setBackground(new Color(colorR, colorG, colorB));

        // Rotation Slider
        JLabel rotationLabel = new JLabel("Rotation: 0°");
        // rotationLabel.setForeground(Color.WHITE);
        JSlider rotationSlider = new JSlider(0, 360, 0);
        rotationSlider.setName("transform_rotation");
        // rotationSlider.setBackground(new Color(colorR, colorG, colorB));
        rotationSlider.setEnabled(true);

        // rotationSlider.setForeground(Color.WHITE);
        configureSlider(rotationSlider, 90);

        // Scale Slider
        JLabel scaleLabel = new JLabel("Scale: 100%");
        // scaleLabel.setForeground(Color.WHITE);
        JSlider scaleSlider = new JSlider(50, 200, 100); // 50% to 200%
        scaleSlider.setName("transform_scale");
        // scaleSlider.setBackground(new Color(colorR, colorG, colorB));
        scaleSlider.setEnabled(true);

        // scaleSlider.setForeground(Color.WHITE);
        configureSlider(scaleSlider, majorTickSpacing);

        // Flip Panel
        JPanel flipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, horizontalGap, verticalGap));
        // flipPanel.setBackground(new Color(colorR, colorG, colorB));

        JButton flipHBtn = createIconButton("icons/toolbar/flip_h.png", "Flip H", "Flip Horizontally");
        JButton flipVBtn = createIconButton("icons/toolbar/flip_v.png", "Flip V", "Flip Horizontally");

        flipHBtn.setName("transform_flipH");
        flipHBtn.setEnabled(true);
        flipVBtn.setName("transform_flipV");
        flipVBtn.setEnabled(true);

        // Update the event listeners for sliders and buttons
        // (Note:hazim) added a rotation for the right canvas;;
        // as an artist, i need it to rotate using bindings
        rotationSlider.addChangeListener(e -> {
            if (!rotationSlider.getValueIsAdjusting()) {
                int value = rotationSlider.getValue();
                rotationLabel.setText("Rotation: " + value + "°");

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
    private JButton createIconButton(String iconPath, String text, String tooltip) {
        ImageIcon icon = new ImageIcon(iconPath);
        icon = Helpers.iconSizeChanger(icon, iconSizeX, iconSizeY);

        JButton button = new JButton(text);
        button.setIcon(icon);
        button.setToolTipText(tooltip);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFocusPainted(false);

        return button;
    }

    // * Adds a tool button to the specified panel with an icon and action listener
    private void addToolButton(JPanel panel, String iconPath, Tool tool, String text, String tip) {
        JButton button = createIconButton(iconPath, text, tip);
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
        leftCanvasBtn.setBorder(canvas == leftCanvasPanel
                ? BorderFactory.createLineBorder(Color.BLUE, 2)
                : BorderFactory.createEmptyBorder(2, 2, 2, 2));
        rightCanvasBtn.setBorder(canvas == rightCanvasPanel
                ? BorderFactory.createLineBorder(Color.BLUE, 2)
                : BorderFactory.createEmptyBorder(2, 2, 2, 2));

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
        leftCanvasPanel.resetTransformation();
        rightCanvasPanel.resetTransformation();

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

    private void setupDragHandling() {
        addMouseListener(new MouseAdapter() {
            private Point dragStart;

            @Override
            public void mousePressed(MouseEvent e) {
                if (isFloating()) {
                    dragStart = e.getPoint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                repaint(); // Redraw docking hints
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isFloating() && dragStart != null) {
                    Window window = SwingUtilities.getWindowAncestor(ToolbarPanel.this);
                    if (window != null) {
                        Point windowLoc = window.getLocation();
                        Point dragPoint = e.getLocationOnScreen();
                        window.setLocation(
                                dragPoint.x - dragStart.x,
                                dragPoint.y - dragStart.y);
                    }
                }
            }
        });
    }

    private void setFloatingUI() {
        // setBackground(new Color(240, 240, 240));
        setBorder(BorderFactory.createCompoundBorder());
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
    }

    private void setDockedUI() {
        // setBackground(new Color(colorR, colorG, colorB));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setCursor(Cursor.getDefaultCursor());
    }

    private void adjustLayoutForOrientation() {
        if (isFloating()) {
            if (getOrientation() == JToolBar.HORIZONTAL) {
                setPreferredSize(new Dimension(500, 180));
            } else {
                setPreferredSize(new Dimension(250, Integer.MAX_VALUE));
            }
        } else {
            if (getOrientation() == JToolBar.HORIZONTAL) {
                setPreferredSize(null);
            } else {
                setPreferredSize(new Dimension(dimensionWidth, getHeight()));
            }
        }

        removeAll();
        initializeToolbar();
        revalidate();
        repaint();
    }

    private void setupDockingZones() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (isFloating()) {
                    Insets insets = getInsets();
                    Point mousePos = e.getPoint();
                    int zoneSize = 40;

                    Rectangle topLeft = new Rectangle(insets.left, insets.top, zoneSize, zoneSize);
                    Rectangle topRight = new Rectangle(getWidth() - insets.right - zoneSize, insets.top, zoneSize,
                            zoneSize);

                    Window window = SwingUtilities.getWindowAncestor(ToolbarPanel.this);

                    if (window instanceof JFrame) {
                        JFrame frame = (JFrame) window;

                        // chekc which zone was clicked
                        if (topLeft.contains(mousePos)) {
                            dockToolbar(frame, BorderLayout.WEST);
                        } else if (topRight.contains(mousePos)) {
                            dockToolbar(frame, BorderLayout.EAST);
                        }
                    }
                }
            }
        });
    }

    private void dockToolbar(JFrame frame, String position) {
        setFloating(false);

        Container parent = getParent();
        if (parent != null) {
            parent.remove(this);
        }

        frame.add(this, position);
        // if (BorderLayout.SOUTH.equals(position)) {
        // setOrientation(JToolBar.HORIZONTAL);
        // } else {
        setOrientation(JToolBar.VERTICAL);
        // }

        frame.pack();
        frame.revalidate();
        repaint();
    }

    private void setupKeyBindings() {
        bindPasteShortcut();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(dimensionWidth, super.getPreferredSize().height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isFloating()) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // position indicators
            g2d.setColor(new Color(70, 130, 180));
            g2d.setStroke(new BasicStroke(2));
            int size = 24;
            int offset = 12;
            Insets insets = getInsets();

            // Adjust positions for insets
            drawDockingIcon(g2d,
                    insets.left + offset,
                    insets.top + offset,
                    size, "◀", "Dock Left");

            drawDockingIcon(g2d,
                    getWidth() - insets.right - offset - size,
                    insets.top + offset,
                    size, "▶", "Dock Right");
            g2d.dispose();

        }
    }

    private void drawDockingIcon(Graphics2D g2d, int x, int y, int size, String symbol, String text) {
        g2d.setColor(new Color(230, 230, 250));
        g2d.fillRoundRect(x, y, size, size, 5, 5);

        g2d.setColor(new Color(70, 130, 180));
        g2d.drawRoundRect(x, y, size, size, 5, 5);

        // symbol
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        int sx = x + (size - fm.stringWidth(symbol)) / 2;
        int sy = y + ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(symbol, sx, sy);

        // text label
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fm = g2d.getFontMetrics();
        int tx = x + (size - fm.stringWidth(text)) / 2;
        int ty = y + size + fm.getHeight() + 2;
        g2d.drawString(text, tx, ty);
    }
}