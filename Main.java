import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

class Storage {
    static ArrayList<Storage> categoryList = new ArrayList<>();
    String categoryName;
    ArrayList<ItemObj> itemList;
    private static float cash = 0;

    Storage(String categoryName, ArrayList<String> itemList, ArrayList<Float> priceList, ArrayList<Integer> stockList) {
        this.categoryName = categoryName;
        this.itemList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            // store all the item in object
            this.itemList.add(new ItemObj(itemList.get(i), priceList.get(i), stockList.get(i)));
        }
        categoryList.add(this);
    }

    static class ItemObj {
        String name;
        float price;
        int stock;

        ItemObj(String name, float price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        public String toString() {
            return this.name + ", " + this.price + ", " + this.stock;
        }
    }

    public String toString() {
        return this.categoryName + "\n" + this.itemList + "\n";
    }

    static void insertCash(label cashTxt) { // when user want to insert the cash into the system
        String temp;
        float tempCash;
        while (true) {
            try {
                temp = JOptionPane.showInputDialog("Insert Cash:"); // return String value
                if (temp == null) {
                    break;
                }
                if ((tempCash = Float.parseFloat(temp)) < 0) {
                    JOptionPane.showMessageDialog(null, "Cash cannot accept the negative value", "Value Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    cash += tempCash;  // add the cash into the system
                    cashTxt.setText("Cash: RM" + cash);
                    break;
                }

            } catch (Exception er) { // if input is not number will show an error message
                JOptionPane.showMessageDialog(null, "Only number are allow", "Error", JOptionPane.ERROR_MESSAGE);
                er.printStackTrace();
            }
        }
    }

    static void ejectCash(label cash_txt) { // when user want to eject the cash
        int result = JOptionPane.showConfirmDialog(null, "Eject Cash: RM" + cash, "Confirm Eject?", JOptionPane.YES_NO_OPTION);
        if (result == 0) {
            cash = 0;    // reset cash to 0
            cash_txt.setText("Cash: RM" + cash);
        }
    }

    static void sellItem(float itemPrice, int qty, ItemObj item, panel frame, Storage category) {
        if (itemPrice <= Storage.cash) { // if user does have enough cash in the system will display an error message
            cash -= itemPrice;
            item.stock -= qty;
            itemToFile(category);
            Main.window.refresh(frame);
            Main.category = new Category(); // back to category page
        } else {
            JOptionPane.showMessageDialog(null, "Not Enough Cash", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void itemToFile(Storage category) {  // save the item from system to file
        String content = "";
        for (Storage.ItemObj item : category.itemList) {
            content += item.name + ";" +  item.price + ";" + item.stock + "\n";
        }
        // to replace the content of the item file from category folder
        Main.main.createOrModifyFile("category\\" + category.categoryName + ".txt", content, false);
    }

    static String getCash() {
        return String.valueOf(cash);
    }
}

class topPanel {
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
        cash = new label("Cash: RM"+ Storage.getCash(), 870, 60, 100, 30);
        cash.setFont(new Font("Calibri", Font.BOLD, 16));

        top.add(title);
        top.add(insertBtt);
        top.add(ejectBtt);
        top.add(description);
        top.add(cash);
    }
}

class Category extends topPanel{
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

class Item extends topPanel{
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
            btt.setIconTextGap(-15);
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

class Admin {
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
    private class bttCategory extends button implements ActionListener{
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
                    if (newCategory.toLowerCase().equals(s.categoryName.toLowerCase())) {
                        JOptionPane.showMessageDialog(null, "This category name is already exist", "Error", JOptionPane.ERROR_MESSAGE);
                        pass = false;
                        break;
                    }
                }
                if (pass) {
                    // create new category to system and file
                    new Storage(newCategory, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
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

class CategoryStt implements  ActionListener {
    private panel frame, tablePanel, toolPanel;
    private button backBtt, deleteItemBtt, deleteCategoryBtt, addBtt, editBtt, imgBtt;
    private label title, text;
    private JTable table;
    private JTextField searchBar;
    private DefaultTableModel tableModel;
    private ListSelectionModel tableSelection;
    Storage category;
    boolean edit = false;
    String searchText;
    int rows;

    CategoryStt(Storage category) {
        this.category = category;
        // create a frame for this Category Setting page
        frame = new panel(1000, 800, null, Color.CYAN);
        Main.window.add(frame);

        // create a panel for table
        tablePanel = new panel(20, 120, 950, 650, null, Color.WHITE);

        toolPanel = new panel(0, 0, 987, 70, null, Color.WHITE);
        toolPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));

        // create a 5 different function of button
        backBtt = new button("Back", 10, 10, 100, 50);
        backBtt.addActionListener(this);

        deleteCategoryBtt = new button("Delete Category", 400, 10, 130, 50);
        deleteCategoryBtt.addActionListener(this);

        deleteItemBtt = new button("Delete Item", 720, 10, 100, 50);
        deleteItemBtt.setEnabled(false);
        deleteItemBtt.addActionListener(this);

        addBtt = new button("Add", 820, 10, 60, 50);
        addBtt.addActionListener(this);

        editBtt = new button("Edit", 880, 10, 80, 50);
        editBtt.addActionListener(this);

        // add title
        title = new label(this.category.categoryName + " Category", 240, 10, 150, 50, Color.BLACK, 3);
        title.setFont(new Font("Calibri", Font.BOLD, 16));
        title.setOpaque(true);
        title.setBackground(Color.WHITE);

        text = new label("Search:", 20, 90, 50, 20);

        searchBar = new JTextField();
        searchBar.setBounds(70, 90, 250, 20);
        searchBar.addKeyListener(new KeyListener() {

            /* note that new KeyListener is an abstract class, means that force to override the method and
               cannot be removed, or else does not work */

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                searchEngine();
            }
        });

        // create table
        table = new JTable();
        table.setRowHeight(20);
        table.setFont(new Font("Default", Font.PLAIN, 16));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(0, 0, 950, 650);
        tableModel = (DefaultTableModel) table.getModel(); // to enable to modify data feature()
        // adding column field
        tableModel.addColumn("Item Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Stocks");

        // to change the cell font when is editing
        JTextField textField = new JTextField();
        textField.setFont(new Font("Default", Font.PLAIN, 16));
        DefaultCellEditor dce = new DefaultCellEditor(textField);
        // to apply the text field to the column
        table.getColumnModel().getColumn(0).setCellEditor(dce);
        table.getColumnModel().getColumn(1).setCellEditor(dce);
        table.getColumnModel().getColumn(2).setCellEditor(dce);

        addData();

        tableSelection = table.getSelectionModel(); // to enable the selection feature
//        table.putClientProperty("terminateEditOnFocusLost", true);
        table.addMouseListener(new MouseListener() {

            /* note that new MouseListener is an abstract class, means that force to override the method and
               cannot be removed, or else does not work */

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // table can or cannot be edited
                if (table.isEditing() && !edit) { // check the edit button is on, if off then cannot be edited
                    table.getCellEditor().stopCellEditing();
                }
                deleteItemBtt.setEnabled(tableSelection.getSelectedIndices().length != 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        // add all the GUI component into the frame to display it
        frame.add(toolPanel);
        toolPanel.add(backBtt);
        toolPanel.add(deleteCategoryBtt);
        toolPanel.add(deleteItemBtt);
        toolPanel.add(addBtt);
        toolPanel.add(editBtt);
        toolPanel.add(title);
        frame.add(text);
        frame.add(searchBar);
        tablePanel.add(scroll);
        frame.add(tablePanel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == backBtt) {  // back button being pressed will execute the block of code
            if (edit) {
                int result = JOptionPane.showConfirmDialog(null, "Save the changes to your system?", "Save", JOptionPane.YES_NO_CANCEL_OPTION);
                if (result == 0) {
                    if (!saveItem()) {  // if save is complete then proceed, or else stop
                        return;
                    }
                } else if (result == JOptionPane.CANCEL_OPTION || result == JOptionPane.CLOSED_OPTION){
                    // if user press cancel or closed option will remain
                    return;
                }
            }
            Main.window.refresh(frame);
            Main.admin = new Admin();
        }

        if (e.getSource() == addBtt) {  // add button being pressed will execute the block of code
            addItem();
        }

        if (e.getSource() == editBtt) {  // edit button being pressed will execute the block of code
            if (edit) {
                if (saveItem()) {  // if save is complete then proceed
                    edit = false;
                    editBtt.setText("Edit");
                } else {
                    return;
                }

            } else {
                edit = true;
                editBtt.setText("Save");
            }
        }

        if (e.getSource() == deleteCategoryBtt) {  // delete category button being pressed will execute the block of code
            deleteCategory();
        }

        if (e.getSource() == deleteItemBtt) {  // delete item button being pressed will execute the block of code
            deleteItem();
        }
    }

    void addData() { // add all the items from specific category into the table
        for (Storage.ItemObj item : category.itemList) {
            tableModel.addRow(new Object[]{item.name, item.price, item.stock});
        }
    }

    void addItem() {
        // create an input dialog to receive all sort of item field
        boolean pass;

        label nametxt = new label("Item Name:", 70, 10);
        nametxt.setHorizontalAlignment(JLabel.LEFT);
        JTextField nameField = new JTextField(15);

        label pricetxt = new label("Price:", 70, 10);
        pricetxt.setHorizontalAlignment(JLabel.LEFT);
        JTextField priceField = new JTextField(15);

        label stocktxt = new label("Stocks:", 70, 12);
        stocktxt.setHorizontalAlignment(JLabel.LEFT);
        JTextField stockField = new JTextField(15);

        panel box = new panel(250, 75, new FlowLayout(FlowLayout.LEADING, 0, 5));
        box.add(nametxt);
        box.add(nameField);
        box.add(pricetxt);
        box.add(priceField);
        box.add(stocktxt);
        box.add(stockField);

        while (true) {
            pass = true;
            // start to create an input dialog
            int option = JOptionPane.showConfirmDialog(null, box, "Add New Item", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText();
                    float price = Float.parseFloat(priceField.getText());
                    int stock = Integer.parseInt(stockField.getText());
                    if (price < 0 || stock < 0) {
                        JOptionPane.showMessageDialog(null, "Price and Stock cannot accept negative value", "Value Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        for (Storage.ItemObj item : category.itemList) { // check any same name to prevent duplication
                            if (name.toLowerCase().equals(item.name.toLowerCase())) {
                                JOptionPane.showMessageDialog(null, "This Item Name is already exist", "Error", JOptionPane.ERROR_MESSAGE);
                                pass = false;
                                break;
                            }
                        }
                        if (pass) {
                            // create a new item that will add into category itemList
                            category.itemList.add(new Storage.ItemObj(name, price, stock));
                            tableModel.addRow(new Object[]{name, price, stock});
                            String content = name + ";" + price + ";" + stock + "\n";
                            Main.main.createOrModifyFile("category\\" + category.categoryName + ".txt", content, true);
                            break;
                        }
                    }
                } catch (NumberFormatException er) { // any input field with false datatype will prompt an error
                    JOptionPane.showMessageDialog(null, "Price and Stock cannot have alphabet", "Error", JOptionPane.ERROR_MESSAGE);
                    er.printStackTrace();
                } catch (Exception er) {
                    JOptionPane.showMessageDialog(null, "Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
                    er.printStackTrace();
                }
            } else {
                break;
            }
        }
    }

    void deleteCategory() {
        String allCategory = "";
        String name = category.categoryName;
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete", "Warning", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            File file = new File("category\\" + name + ".txt");
            if (file.delete()) {  // delete the file, if failed then false
                Storage.categoryList.remove(category);  // remove the category
                // add all the category from system then replace the file
                for (Storage i : Storage.categoryList) {
                    allCategory += i.categoryName + "\n";
                }
                Main.main.createOrModifyFile("category.txt", allCategory, false);
                JOptionPane.showMessageDialog(null, "Successfully delete " + name, "Complete", JOptionPane.PLAIN_MESSAGE);
                Main.window.refresh(frame); // refresh the frame
                Main.admin = new Admin();
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete " + name, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    void deleteItem() {
        int result = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete the selected row", "warning", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            int[] selected = tableSelection.getSelectedIndices(); // return all the selected rows
            for (int i = selected.length - 1; i >= 0; i--) {  // reverse loop to remove item from category and table
                category.itemList.remove(selected[i]);
                tableModel.removeRow(selected[i]);
            }
            Storage.itemToFile(category);
        }
    }

    boolean saveItem() { // save the item that is edited
        float floatValue = 0;
        int intValue = 0;
        if (table.isEditing()) {
            JOptionPane.showMessageDialog(null, "Please press ENTER or ESC key to the field you just edited", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        // first check the column 1(price) and 2(stocks) have any empty string or any data is not number
        for (int column = 1; column < tableModel.getColumnCount(); column++) {
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                if (column == 1 || column == 2) {
                    String data = String.valueOf(table.getValueAt(row, column));

                    if (data.equals("")) {  // if empty string will change to 0
                        tableModel.setValueAt(0, row, column);
                    } else {
                        try {
                            // to check the data is a number or not
                            if (column == 1) {
                                floatValue = Float.parseFloat(data);
                            } else {
                                intValue = Integer.parseInt(data);
                            }
                            if (floatValue < 0 || intValue < 0) {
                                JOptionPane.showMessageDialog(null, "Price and Stock cannot accept negative value", "Value Error", JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                        } catch (Exception er) {  // if is string will prompt an error message then stop to save item
                            JOptionPane.showMessageDialog(null, "Price and Stocks column only allow number!!!\nRemember to press enter after enter the value", "Error", JOptionPane.ERROR_MESSAGE);
                            er.printStackTrace();
                            return false; // save is fail
                        }
                    }
                }
            }
        }
        // after the code above will start to save the data
        Vector<Vector> tableData = tableModel.getDataVector();  // get the data from table
        int n = 0;
        // replace all the item from itemList
        for (Vector row : tableData) {
            category.itemList.get(n).name = (String) row.get(0);
            category.itemList.get(n).price = Float.parseFloat(String.valueOf(row.get(1)));
            category.itemList.get(n).stock = Integer.parseInt(String.valueOf(row.get(2)));
            n++;
        }
        Storage.itemToFile(category);
        return true;  // save is complete
    }

    void searchEngine() {
        searchText = searchBar.getText();  // get the searchbar text
        rows = table.getRowCount();
        for (int n = rows - 1; n >= 0; n--) {  // remove all the table data
            tableModel.removeRow(n);
        }
        if (searchText.equals("")) {  // if search bar is empty string will add all data into table
            addData();
        } else {  // check any item contain search bar string will add into table
            for (Storage.ItemObj item : category.itemList) {
                if (item.name.toLowerCase().startsWith(searchText.toLowerCase())) {
                    tableModel.addRow(new Object[]{item.name, item.price, item.stock});
                }
            }
        }
    }
}


public class Main {
    public static Main main;
    public static frame window;
    public static Category category;
    public static Item item;
    public static Admin admin;
    public static CategoryStt categoryStt;
    ArrayList<String> itemList;
    ArrayList<Float> priceList;
    ArrayList<Integer> stockList;

    Main() {
        readCategory();
        window = new frame(1000, 800, new BorderLayout());
    }

    String readAdmin() {
        // read the admin password
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File file = new File("admin.txt");
            if (!file.exists()) {
                System.out.println("Admin file is not exist, proceed to create new admin file");
                createOrModifyFile("admin.txt", "123456", false);
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String password = null;
            while ((password = br.readLine()) != null) {
                if (password.length() >= 4) { // Ignore the length of string less than 3
                    break;
                }
            }
            if (password == null) {
                // if the file is empty proceed to replace file and return password without interrupt the system
                createOrModifyFile("admin.txt", "123456", false);
                return "123456";
            }
            return password;

        } catch (IOException e) {
            e.printStackTrace();
            createOrModifyFile("admin.txt", "123456", false);
        } finally {
            closeFile(br);
            closeFile(fr);
        }
        return readAdmin();
    }

    void readCategory() {
        // read the file to store all the data in the ArrayList\
        FileReader fr = null;
        BufferedReader br = null;
        try {
            File file = new File("category.txt");
            if (!file.exists()) {
                System.out.println("FIle is not exist, proceed to create a new file");
                createOrModifyFile("category.txt", """
                    Chip
                    Bar
                    Drink
                    """, false);
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String categoryName;
            while ((categoryName = br.readLine()) != null) {
                if (categoryName.length() > 0){
                    readItem(categoryName);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(br);
            closeFile(fr);
        }
    }

    void readItem(String categoryName) {
        // read all the item in a category\(categoryName).txt
        FileReader fr = null;
        BufferedReader br = null;
        itemList = new ArrayList<>();
        priceList = new ArrayList<>();
        stockList = new ArrayList<>();
        try {
            File file = new File("category\\" + categoryName + ".txt");
            if (!file.exists()) {
                System.out.printf("%s fIle is not exist, proceed to create %s file\n", categoryName, categoryName);
                createOrModifyFile("category\\" + categoryName + ".txt", "", false);
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String item;
            String[] array;
            while ((item = br.readLine()) != null) {
                if (item.length() > 0) {
                    array = item.split(";");
                    itemList.add(array[0]);
                    priceList.add(Float.parseFloat(array[1]));
                    stockList.add(Integer.parseInt(array[2]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(br);
            closeFile(fr);
        }
        // create a storage class that will store in categoryList
        new Storage(categoryName, itemList, priceList, stockList);
    }

    void createOrModifyFile(String filename, String content, Boolean append) {
        // create or replace the file
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(filename, append);
            bw = new BufferedWriter(fw);
            bw.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(bw);
            closeFile(fw);
        }
    }

    void closeFile(Closeable file) { // close the file
        if (file != null) {
            try {
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        main = new Main();
        category = new Category();
    }
}
