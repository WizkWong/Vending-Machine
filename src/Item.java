import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Item extends topPanel{
    static ArrayList<bttItem> bttItemList = new ArrayList<>();
    String txt;
    Storage category;

    private panel selection;
    private button backbtt;
    private bttItem btt;
    private JScrollPane scroll;

    Item(Storage category) {
        super(
                category.categoryName,
                "<html><p style='text-align:center;'>To insert cash, please click insert cash button at the right of the conner<br/>Please Select an item</p><html>"
        );
        this.category = category;

        //create a back button
        backbtt = new button("Back", 10, 10, 100, 50);
        backbtt.setVerticalAlignment(JLabel.CENTER);
        backbtt.addActionListener(e -> {
            Main.window.refresh(frame);
            Main.category = new Category();
        });

        // create a panel that show all the bttItemList
        selection = new panel(1000, panel.autosize(category.itemList.size()), new GridBagLayout(), Color.CYAN);

        // add a scrollbar to be able to scroll up and down
        scroll = new JScrollPane(selection, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(1000, 650));

        // adjust the location by columns and rows, columns must be 4 at max
        GridBagConstraints bttConstraints = new GridBagConstraints();
        bttConstraints.gridx = 0;
        bttConstraints.gridy = 0;
        bttConstraints.insets = new Insets(10, 15, 10, 15);

        // create the list of button with item label in it
        for (Storage.ItemObj i : category.itemList) {
            // note: JButton cannot have new line using String "\n"
            txt = "<html><p style='text-align:center;'>" + i.name +
                    "<br/>RM" + i.price + "</p></html>";
            btt = new bttItem(txt, i, 200, 100);
            bttItemList.add(btt);                // add the button into bttItemList
            selection.add(btt, bttConstraints);  // add the button into frame
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
        top.add(backbtt);

        frame.updateUI();  // refresh
    }

    // create a class for the item buttons
    private class bttItem extends button implements ActionListener {
        Storage.ItemObj item;
        String txt;

        bttItem(String txt, Storage.ItemObj item, int width, int height) { // create button
            super(txt, width, height);
            // assign an item to the button
            this.item = item;
            this.txt = item.name + "\nPrice: RM" + item.price + "\nEnter the quantity:  Only accept 1-10";
            if (this.item.stock == 0) {
                this.setEnabled(false);
            }
            this.addActionListener(this);     // add an action to this button
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String temp;
            int qty;
            float total;
            int result;
            String initial = "1";
            // any bttItem button pressed will be executed the code
            if (e.getSource() == this) {
                do {
                    result = 1;
                    try {
                        // show an input dialog that user can enter it
                        temp = JOptionPane.showInputDialog(txt, initial); // return String value

                        if (temp == null) { // break when user cancel it
                            break;
                        }
                        qty = Integer.parseInt(temp);

                        if (1 <= qty && qty <= 10) {
                            if (qty > item.stock) { // if the input item more than stock wil show a message dialog
                                JOptionPane.showMessageDialog(null, "Sorry, the stock have only " + item.stock, "Stock is not enough", JOptionPane.PLAIN_MESSAGE);
                                initial = String.valueOf(item.stock);
                                continue;
                            }
                            DecimalFormat df = new DecimalFormat("#.00");  // set the decimal place to 2
                            total = Float.parseFloat(df.format(item.price * qty)); // calculate the total price and show a dialog for user to confirm
                            result = JOptionPane.showConfirmDialog(null, "Total price: RM" + total + ", confirm to purchase?", "Confirm?", JOptionPane.YES_NO_CANCEL_OPTION);
                            if (result == JOptionPane.YES_OPTION) {
                                Storage.sellItem(total, qty, item, frame, category);
                            }
                        } else {
                            // if qty or input is not 1 to 10 will show an error dialog
                            JOptionPane.showMessageDialog(null, "Only number 1 to 10 only!!!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception er) {
                        // if the input is not a string will show an error dialog
                        JOptionPane.showMessageDialog(null, "Only number 1 to 10 only!!!", "Error", JOptionPane.ERROR_MESSAGE);
                        er.printStackTrace();
                    }
                } while (result == 1); // JOption.NO_OPTION is 1
            }
        }
    }
}
