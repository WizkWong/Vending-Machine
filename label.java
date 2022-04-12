import javax.swing.*;
import java.awt.*;

public class label extends JLabel{

    label(String str, int x, int y, int width, int height, Color color, int thickness) {
        this.setText(str);
        this.setBounds(x, y, width, height);
        this.setVerticalAlignment(JLabel.CENTER);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createLineBorder(color, thickness));
    }

    label(String str, int x, int y, int width, int height) {
        this(str, x, y, width, height, null, 0);
    }

    // create Label with all the needed setting
    label(String str, int width, int height, Color color, int thickness) {
        this.setText(str);
        this.setPreferredSize(new Dimension(width, height));
        this.setVerticalAlignment(JLabel.CENTER);
        this.setHorizontalAlignment(JLabel.CENTER);
        this.setBorder(BorderFactory.createLineBorder(color, thickness));
    }

    label(String str, int width, int height) {
        this(str, width, height, null, 0);
    }

    label() {
        this( null, 0, 0, null, 0);
    }
}
