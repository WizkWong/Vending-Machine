import javax.swing.*;
import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class panel extends JPanel {

    panel(int x, int y, int width, int height, LayoutManager layout, Color color) {
        this.setBounds(x, y, width, height);
        this.setLayout(layout);
        this.setBackground(color);
    }

    panel(int x, int y, int width, int height) {
        this(x, y, width, height, null, null);
    }

    panel(int width, int height, LayoutManager layout, Color color) {
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(layout);
        this.setBackground(color);
    }

    panel(int width, int height, LayoutManager layout) {
        this(width, height, layout, null);
    }

    panel(int width, int height) {
        this(width, height, null, null);
    }

    static int autosize(int length) {
        DecimalFormat df = new DecimalFormat("#");
        df.setRoundingMode(RoundingMode.UP);
        return Integer.parseInt(df.format((length / 4f))) * 130;
    }
}
