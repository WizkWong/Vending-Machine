import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class button extends JButton {

    button(String str, int x, int y, int width, int height) {
        this.setText(str);
        this.setBounds(x, y, width, height);
        this.setFocusable(false);
        this.setVerticalTextPosition(JLabel.CENTER);
        this.setHorizontalTextPosition(JLabel.CENTER);
    }

    button(String str, int width, int height) {
        this.setText(str);
        this.setPreferredSize(new Dimension(width, height));
        this.setFocusable(false);
        this.setVerticalTextPosition(JLabel.CENTER);
        this.setHorizontalTextPosition(JLabel.CENTER);
    }

    button(String str) {
        this(str, 0, 0);
    }
}
