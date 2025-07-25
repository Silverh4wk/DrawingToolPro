package src;

public class FlowerCreation extends CreationType { 
    // FlowerCreation extends CreationType, which is assumed to be a class
    public FlowerCreation(String name, String imagePath) {
        super(name, imagePath); // Call the constructor of the superclass (CreationType)
    }

    // Similar to AnimalCreation, getName() and getImage() are inherited
    // from CreationType and do not need to be explicitly overridden here
    // unless specific logic is required for flowers.
}