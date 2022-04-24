import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AdminCategory implements ActionListener {
    private panel frame, tablePanel, toolPanel;
    private button backBtt, deleteItemBtt, deleteCategoryBtt, addBtt, editBtt;
    private label title, text;
    private JTable table;
    private JTextField searchBar;
    private DefaultTableModel tableModel;
    private ListSelectionModel tableSelection;
    Storage category;
    boolean edit = false;
    String searchText;
    int rows;

    AdminCategory(Storage category) {
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
                            if (name.equalsIgnoreCase(item.name)) {
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
        if (table.isEditing()) {
            JOptionPane.showMessageDialog(null, "Please press ENTER or ESC key to the field you just edited", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            var tableData = tableModel.getDataVector();  // get the data from table
            int n = 0;
            // replace all the item from itemList
            for (var row : tableData) {
                category.itemList.get(n).name = (String) row.get(0);
                category.itemList.get(n).price = Float.parseFloat(String.valueOf(row.get(1)));
                category.itemList.get(n).stock = Integer.parseInt(String.valueOf(row.get(2)));
                n++;
            }
        } catch (Exception er) {  // if error occur will prompt an error message then stop to save item
            JOptionPane.showMessageDialog(null, "Price and Stocks column only allow number!!!\nRemember to press enter after enter the value", "Error", JOptionPane.ERROR_MESSAGE);
            er.printStackTrace();
            return false; // save is fail
        }
        Storage.itemToFile(category);
        return true;  // save is complete
    }

    void searchEngine() {
        searchText = searchBar.getText();  // get the searchbar text
        int rows = table.getRowCount();
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
