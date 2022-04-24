import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Category extends topPanel{
    static ArrayList<bttCategory> bttCategoryList = new ArrayList<>();

    private panel selection;
    private button adminAccess;
    private bttCategory btt;
    private JScrollPane scroll;

    Category() {
        super(
                "Vending Machine",
                "<html><p style='text-align:center;'>To insert cash, please click insert cash button at the right of the conner<br/>Please Select a Category</p><html>"
        );
        // create a button for admin part
        adminAccess = new button("Admin", 10, 10, 100, 50);
        adminAccess.setVerticalAlignment(JLabel.CENTER);
        adminAccess.addActionListener(e -> {
            if (Admin.accessAdmin()) {
                Main.window.refresh(frame);
                Main.admin = new Admin();
            }
        });

        // create a panel that show all the bttCategoryList
        selection = new panel(1000, panel.autosize(Storage.categoryList.size()), new GridBagLayout(), Color.CYAN);

        // add a scrollbar to be able to scroll up and down
        scroll = new JScrollPane(selection, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(1000, 650));

        // adjust the location by columns and rows, columns must be 4 at max
        GridBagConstraints bttConstraints = new GridBagConstraints();
        bttConstraints.gridx = 0;
        bttConstraints.gridy = 0;
        bttConstraints.insets = new Insets(10, 15, 10, 15);

        // create the list of button with categoryName label in it
        for (Storage i : Storage.categoryList) {
            if (i.itemList.size() == 0) {  // category that have no item will not display
                continue;
            }
            btt = new bttCategory(i, 200, 100);
            bttCategoryList.add(btt);           // add the button into bttCategoryList
            selection.add(btt, bttConstraints); // adding the button into frame
            if (bttConstraints.gridx == 3) {
                bttConstraints.gridx = 0;
                bttConstraints.gridy ++;
            }
            else {
                bttConstraints.gridx ++;
            }
        }
        // add all the GUI component into the frame to display it
        frame.add(top, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.SOUTH);
        top.add(adminAccess);

        frame.updateUI(); // refresh
    }

    // create a class for the category buttons
    private class bttCategory extends button implements ActionListener {
        Storage category;

        bttCategory(Storage category, int width, int height) { // create button
            super(category.categoryName, width, height);
            // assign a category to the button
            this.category = category;            // this category will be used to pass as parameter
            this.addActionListener(this);     // to add an action to this button
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // any bttCategory button pressed will be executed the code
            if (e.getSource() == this) {
                Main.window.refresh(frame);
                Main.item = new Item(category);
            }
        }
    }
}
