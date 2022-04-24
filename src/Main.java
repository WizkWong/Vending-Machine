import java.awt.*;
import java.io.*;
import java.util.*;

public class Main {
    public static Main main;
    public static frame window;
    public static Category category;
    public static Item item;
    public static Admin admin;
    public static AdminCategory adminCategory;
    ArrayList<String> itemList;

    Main() {
        readCategory();
        window = new frame(1000, 800, new BorderLayout());
    }

    String readAdmin() {
        // read the admin password
        FileReader fr;
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
        }
        return readAdmin();
    }

    void readCategory() {
        // read the file to store all the data in the ArrayList\
        FileReader fr;
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
        }
    }

    void readItem(String categoryName) {
        // read all the item in a category\(categoryName).txt
        FileReader fr;
        BufferedReader br = null;
        itemList = new ArrayList<>();
        try {
            File file = new File("category\\" + categoryName + ".txt");
            if (!file.exists()) {
                System.out.printf("%s fIle is not exist, proceed to create %s file\n", categoryName, categoryName);
                createOrModifyFile("category\\" + categoryName + ".txt", "", false);
            }
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String item;
            while ((item = br.readLine()) != null) {
                if (item.length() > 0) {
                    itemList.add(item);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(br);
        }
        // create a storage class that will store in categoryList
        new Storage(categoryName, itemList);
    }

    void createOrModifyFile(String filename, String content, Boolean append) {
        // create or replace the file
        FileWriter fw;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(filename, append);
            bw = new BufferedWriter(fw);
            bw.write(content);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeFile(bw);
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
