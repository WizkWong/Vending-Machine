import javax.swing.*;
import java.awt.*;

public class frame extends JFrame{

    frame(int width, int height, LayoutManager layout) {
        this.setTitle("Vending Machine App");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(width, height);
        this.setVisible(true);
        this.setLayout(layout);
        this.getContentPane().setBackground(Color.white);
    }

    frame(int width, int height) {
        this(width, height, null);
    }

    public void refresh(panel p) {
        this.remove(p);
        SwingUtilities.updateComponentTreeUI(this);
    }
}
