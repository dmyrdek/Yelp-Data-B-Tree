import java.io.*;
import java.sql.Timestamp;
import java.util.*;

public class Cacher {
    static BTree.Node test = new BTree.Node(true);

    public static void cacheData(){
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Test.ser"));
            oos.writeObject(test);
        } catch (IOException e) {
            System.out.println("Write failed!");
            e.printStackTrace();
        }
    }

    public static BTree.Node readData(){
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Test.ser"));
            BTree.Node read = (BTree.Node) ois.readObject();
            return read;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Read failed!");
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        cacheData();
        BTree.Node test = readData();
        System.out.println(test.isLeaf());
    }
}
