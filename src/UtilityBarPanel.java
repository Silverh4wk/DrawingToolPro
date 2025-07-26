package src;

import Helpers.Helpers;
import Helpers.Helpers.KeyBindingHelper;
import Tools.BrushSizeTool;
import Tools.SaveImageTool;

import java.awt.*;
import javax.swing.*;

public class UtilityBarPanel extends JPanel {
    public int iconSizeX = 24;
    public int iconSizeY = 24;

    public UtilityBarPanel(CanvasPanel canvasPanel) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        initTools(canvasPanel);
        setupKeyBindings(canvasPanel);
    }

    private void initTools(CanvasPanel canvasPanel) {
        add(BrushSizeTool.create(canvasPanel));
        add(createIconButton("icons/toolbar/undo.png", e -> canvasPanel.undo()));
        add(createIconButton("icons/toolbar/redo.png", e -> canvasPanel.redo()));
        add(createIconButton("icons/toolbar/save.png", e -> SaveImageTool.saveImage(canvasPanel)));

    }

    private JButton createIconButton(String iconPath, java.awt.event.ActionListener action) {
        JButton button = new JButton();
        ImageIcon icon = new ImageIcon(iconPath);
        icon = Helpers.iconSizeChanger(icon, iconSizeX, iconSizeY);
        button.setIcon(icon);
        button.addActionListener(action);
        return button;
    }

    private void setupKeyBindings(CanvasPanel canvasPanel) {
        KeyBindingHelper.bind(this, "control Z", "undo", e -> canvasPanel.undo());
        KeyBindingHelper.bind(this, "META Z", "undo", e -> canvasPanel.undo());
        KeyBindingHelper.bind(this, "control Y", "redo", e -> canvasPanel.redo());
    }

}
