
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
// Tc2l Lab Exercise for CP6224
// Group Members:
// TODO | 
//                     
// * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 

package Helpers;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Helpers {

public static class KeyBindingHelper {
    public static void bind(JComponent component, String keystrokeStr, String actionName, ActionListener action) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(keystrokeStr), actionName);
        actionMap.put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(e);
            }
        });
    }
}
    public static class IntVector2D {
        public int x;
        public int y;

        public IntVector2D(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public void setX(int value) {
            this.x = value;
        }

        public void setY(int value) {
            this.y = value;
        }

        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }

    public class Template<T> {
        public T value;

        public Template(T value) {
            this.value = value;
        }
    }

    public static <T> void swap(Template<T> a, Template<T> b) {
        T temp = a.value;
        a.value = b.value;
        b.value = temp;
    }

    public static ImageIcon iconSizeChanger(ImageIcon iconToBeChanged, int Width, int Height) {
        Image newimg = iconToBeChanged.getImage().getScaledInstance(Width, Height, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }


}
