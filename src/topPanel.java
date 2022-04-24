import javax.swing.*;
import java.awt.*;

public class topPanel {
    panel frame, top;
    button insertBtt, ejectBtt;
    label title, description, cash;

    topPanel(String titleText, String dcp) {
        // create a panel as a frame for category page
        frame = new panel(1000, 800, new BorderLayout());
        Main.window.add(frame);

        // create a header
        top = new panel(1000, 115);
        top.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        // create a title and description
        title = new label(titleText, 200, 10, 600, 50);
        title.setFont(new Font("Calibri", Font.BOLD, 30));

        description = new label(dcp , 200, 60, 600, 50);
        description.setFont(new Font("Calibri", Font.BOLD, 16));

        // create a button for insert cash
        insertBtt = new button("Insert Cash", 750, 10, 110, 50);
        insertBtt.addActionListener(e -> Storage.insertCash(cash));

        // create a button for eject cash
        ejectBtt = new button("Eject Cash", 870, 10, 110, 50);
        ejectBtt.addActionListener(e -> Storage.ejectCash(cash));

        // to show cash in top panel
        cash = new label("Cash: RM"+ Storage.getCash(), 870, 60, 120, 30);
        cash.setHorizontalAlignment(JLabel.LEFT);
        cash.setFont(new Font("Calibri", Font.BOLD, 16));

        top.add(title);
        top.add(insertBtt);
        top.add(ejectBtt);
        top.add(description);
        top.add(cash);
    }
}
