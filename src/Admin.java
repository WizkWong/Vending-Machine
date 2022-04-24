import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class Admin {
    static ArrayList<bttCategory> bttCategoryList = new ArrayList<>();

    private panel frame, top, selection;
    private button backBtt, addBtt, changePss;
    private label title, description;
    private bttCategory btt;
    private JScrollPane scroll;


    Admin() {
        //create a panel as a frame for categoryName page
        frame = new panel(1000, 800, new BorderLayout());
        Main.window.add(frame);

        // create a header
        top = new panel(1000, 115);
        top.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        //create a back button
        backBtt = new button("Back", 10, 10, 100, 50);
        backBtt.setVerticalAlignment(JLabel.CENTER);
        backBtt.addActionListener(e -> {
            Main.window.refresh(frame);
            Main.category = new Category();
        });

        title = new label("Setting", 200, 10, 600, 50);
        title.setFont(new Font("Calibri", Font.BOLD, 30));

        // create a button for add a new category
        addBtt = new button("Add New Category", 680, 10, 140, 50);
        addBtt.addActionListener(e -> {
            addCategory();
            Main.window.refresh(frame);
            Main.admin = new Admin();
        });

        // create a button for change the password
        changePss = new button("Change Password", 830, 10, 140, 50);
        changePss.addActionListener(e -> changePassword());

        String dcp = "<html><p style='text-align:center;'>Please Select a Category to view the table</p><html>";
        description = new label(dcp , 200, 60, 600, 50);
        description.setFont(new Font("Calibri", Font.BOLD, 16));

        // create a panel that show all the categoryList
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
        top.add(backBtt);
        top.add(title);
        top.add(addBtt);
        top.add(changePss);
        top.add(description);

        frame.updateUI();  // refresh
    }
    // create a class for the category buttons
    private class bttCategory extends button implements ActionListener {
        Storage catogory;

        bttCategory(Storage category, int width, int height) { // create button
            super(category.categoryName, width, height);
            // assign a category to the button
            this.catogory = category;
            this.addActionListener(this); // add an action to the button
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == this) {
                Main.window.refresh(frame);
                Main.categoryStt = new CategoryStt(catogory); // go to specific category setting
            }
        }
    }

    static boolean accessAdmin() { // to prevent user to access unless password is correct
        // create a confirm dialog with the JPasswordFiled in it
        String password;
        JPasswordField pss = new JPasswordField(10);
        JLabel label = new JLabel("Admin Password:");
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 0));
        panel.add(label);
        panel.add(pss);
        int option;
        do {
            option = JOptionPane.showConfirmDialog(null, panel, "Password Entry", JOptionPane.OK_CANCEL_OPTION); // return int

            if (option == JOptionPane.YES_OPTION) {
                password = new String(pss.getPassword());  // set the input to variable

                if (Main.main.readAdmin().equals(password)) { // open the file, read the file and compare it, if true then access admin page
                    return true;
                } else {
                    // if false then show an error message
                    JOptionPane.showMessageDialog(null, "Wrong Password", "Error", JOptionPane.ERROR_MESSAGE);
                    pss.setText(null);
                }
            }
        } while(option == JOptionPane.YES_OPTION);
        return false;
    }

    void addCategory() {
        String newCategory;
        boolean pass;
        while (true) {
            pass = true;
            // create input dialog for receive the new category
            newCategory = JOptionPane.showInputDialog("Enter the name of the category");
            if (newCategory == null) {  // exit if input is null
                break;
            }

            if (newCategory.equals("")) {
                JOptionPane.showMessageDialog(null, "Category cannot be empty!!!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                for (Storage s : Storage.categoryList) { // check any same name to prevent duplication
                    if (newCategory.equalsIgnoreCase(s.categoryName)) {
                        JOptionPane.showMessageDialog(null, "This category name is already exist", "Error", JOptionPane.ERROR_MESSAGE);
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    // create new category to system and file
                    new Storage(newCategory, new ArrayList<>());
                    Main.main.createOrModifyFile("category.txt", newCategory + "\n", true);
                    Main.main.createOrModifyFile("category\\" + newCategory + ".txt", "", false);
                    break;
                }
            }
        }
    }

    void changePassword() {  // to change the password
        if (accessAdmin()) { // if true then allow to change
            // create a confirm dialog with the JPasswordFiled in it
            String newPassword;
            JPasswordField pss = new JPasswordField(10);
            label label1 = new label("New Password:", 100, 10);
            label1.setHorizontalAlignment(JLabel.LEFT);
            label label2 = new label("Minimum 4 password character", 200, 15);
            label2.setHorizontalAlignment(JLabel.LEFT);
            panel box = new panel(270, 50, new FlowLayout(FlowLayout.LEADING, 0, 5));
            box.add(label1);
            box.add(pss);
            box.add(label2);
            int option;
            do {
                option = JOptionPane.showConfirmDialog(null, box, "Change Password", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    newPassword = String.valueOf(pss.getPassword());
                    if (newPassword.length() >= 4) {
                        Main.main.createOrModifyFile("admin.txt", newPassword, false);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Password must at least 4 character", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } while (option == JOptionPane.YES_OPTION);
        }
    }
}
