// File: src/CreationPanel.java
package src;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class CreationPanel extends JPanel {
    private final CanvasPanel canvasPanel;
    private final List<AnimalCreation> animalCreations;
    private final List<FlowerCreation> flowerCreations;
    private final List<CustomCreation> customCreations;

    private CreationType selectedCreationType;
    private JPanel contentPanel;
    private int gridRow = 0;
    private int gridColumn = 1; 
    private int gridHgap = 5;
    private int gridVgap = 5;

    public CreationPanel(CanvasPanel canvasPanel) {
        this.canvasPanel = canvasPanel;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Creations Library"));
        setBackground(Color.LIGHT_GRAY);

        animalCreations = new ArrayList<>();
        flowerCreations = new ArrayList<>();
        customCreations = new ArrayList<>();

        loadInitialCreations();

        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(gridRow, gridColumn, gridHgap, gridVgap)); 
        contentPanel.setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // Display animals by default
        filterCreations("Animal");
    }

    private void loadInitialCreations() {
        // Load Animal images 
        animalCreations.add(new AnimalCreation("Dog", "icons/animals/dog.png"));
        animalCreations.add(new AnimalCreation("Cat", "icons/animals/cat.png"));
        animalCreations.add(new AnimalCreation("Cow", "icons/animals/cow.png"));
        animalCreations.add(new AnimalCreation("Elephant", "icons/animals/elephant.png"));

        // Load Flower images
        flowerCreations.add(new FlowerCreation("Rose", "icons/flowers/rose.png"));
        flowerCreations.add(new FlowerCreation("Sunflower", "icons/flowers/sunflower.png"));
        flowerCreations.add(new FlowerCreation("Lily", "icons/flowers/lily.png"));
        flowerCreations.add(new FlowerCreation("Tulip", "icons/flowers/tulip.png"));
    }

    public void filterCreations(String category) {
        contentPanel.removeAll(); // Clear existing buttons

        List<? extends CreationType> currentCategoryList;
        switch (category) {
            case "Animal":
                currentCategoryList = animalCreations;
                break;
            case "Flower":
                currentCategoryList = flowerCreations;
                break;
            case "Custom":
                currentCategoryList = customCreations;
                break;
            default:
                currentCategoryList = animalCreations; // Default to animals
                break;
        }

        for (CreationType type : currentCategoryList) {
            JButton button = new JButton(type.getName(), type.getImage());
            button.setVerticalTextPosition(SwingConstants.BOTTOM);
            button.setHorizontalTextPosition(SwingConstants.CENTER);
            button.setPreferredSize(new Dimension(80, 80)); 
            button.addActionListener(e -> {
                selectedCreationType = type;
                canvasPanel.setCurrentCreation(type); // Set the creation type on canvasPanel
            });
            contentPanel.add(button);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }
 
    public void addCustomCreation(CustomCreation customCreation) {
        customCreations.add(customCreation);
        filterCreations("Custom"); // Automatically switch to "Custom" category
    }
}