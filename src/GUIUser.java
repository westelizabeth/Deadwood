/**
Each user object is associated with a GUIUser.
 The GUI user holds information strictly having to do with the view.
 */
import javax.swing.*;
import javax.swing.ImageIcon;
import java.util.ArrayList;

public class GUIUser {
        //attributes
        JLabel thejLabel;
        ImageIcon guiImg;
        int xPosition;
        int yPosition;

        //constructor
        GUIUser(JLabel user, ImageIcon png, int x, int y) {
            thejLabel = user;
            guiImg = png;
             xPosition = x;
             yPosition = y;
        }

        public void setBounds(ArrayList<Integer> bounds){
            thejLabel.setBounds(bounds.get(0), bounds.get(1), guiImg.getIconWidth(), guiImg.getIconHeight());
        }
        public ArrayList<Integer> getBounds(){
            ArrayList<Integer> bounds = new ArrayList<>();
            bounds.add(thejLabel.getX());
            bounds.add(thejLabel.getY());
            bounds.add(guiImg.getIconWidth());
            bounds.add(guiImg.getIconHeight());
            return bounds;
        }
        public JLabel getJlabel() {
            return this.thejLabel;
        }
    }


