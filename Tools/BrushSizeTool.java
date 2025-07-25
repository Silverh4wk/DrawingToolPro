package Tools;

import javax.swing.*;
import src.CanvasPanel;

public class BrushSizeTool {

    public static JPanel create(CanvasPanel canvasPanel) {

       
        int minSliderValue = 0;
        int maxSliderValue = 100;
        int majorTickSpacing = 10;
        int minorTickSpacing = 1;
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel sizeLabel = new JLabel("Brush Size: " + canvasPanel.getBrushSize());

        JSlider brushSizeSlider = new JSlider(JSlider.HORIZONTAL, minSliderValue, maxSliderValue,
                canvasPanel.getBrushSize());
        brushSizeSlider.setMajorTickSpacing(majorTickSpacing); // Major ticks every 10 units
        brushSizeSlider.setMinorTickSpacing(minorTickSpacing); // Minor ticks every 1 unit
        brushSizeSlider.setPaintTicks(true);
        brushSizeSlider.setPaintLabels(true);

        brushSizeSlider.addChangeListener(e -> {
            int size = brushSizeSlider.getValue();
            canvasPanel.setBrushSize(size);
            sizeLabel.setText("Brush Size: " + size);
        });


        panel.add(sizeLabel);
        panel.add(brushSizeSlider);

        return panel;
    }
}
