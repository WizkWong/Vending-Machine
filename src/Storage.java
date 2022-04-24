import javax.swing.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Storage {
    static ArrayList<Storage> categoryList = new ArrayList<>();
    String categoryName;
    ArrayList<ItemObj> itemList;
    private static float cash = 0;

    Storage(String categoryName, ArrayList<String> itemList) {
        this.categoryName = categoryName;
        this.itemList = new ArrayList<>();
        String[] array;
        for (String item : itemList) {
            // store all the item in object
            array = item.split(";");
            this.itemList.add(new ItemObj(array[0], Float.parseFloat(array[1]), Integer.parseInt(array[2])));
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
                    DecimalFormat df = new DecimalFormat("#.00"); // set the decimal place to 2
                    df.setRoundingMode(RoundingMode.DOWN);
                    cash = Float.parseFloat(df.format(cash + tempCash)); // add the cash into the system
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
            DecimalFormat df = new DecimalFormat("#.00"); // set the decimal place to 2
            cash = Float.parseFloat(df.format(cash - itemPrice));
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
