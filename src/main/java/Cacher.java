import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.*;

public class Cacher {

    HashMap<String,Business> map = new HashMap<>();

    static BTree.Node test = new BTree.Node(true,0);

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
/*
    public static void main(String[] args) {
        cacheData();
        BTree.Node test = readData();
        System.out.println(test.isLeaf());
    }



   void write(BTree.Node n) throws IOException{
        try{
            RandomAccessFile file = new RandomAccessFile("Nodes", "rw");
            file.seek(n.nodeID*nodeSize);
            FileChannel fc = file.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(nodeSize);

            bb.putInt(n.leaf);
            bb.putLong(n.nodeID);

            bb.putInt(n.currentNumberOfKeys);
            for (int i = 0; i<n.currentNumberOfKeys; i++){

                YelpData current = n.keys[i];

                //write the name of the current yelp object
                byte [] name = current.name.getBytes();
                bb.putInt(name.length);
                bb.put(name);

                //write the id of current yelpObject
                byte [] id = current.id.getBytes();
                bb.putInt(id.length);
                bb.put(id);

                //write city
                byte [] city = current.city.getBytes();
                bb.putInt(city.length);
                bb.put(city);

                //add lat and long
                bb.putDouble(current.lattitude);
                bb.putDouble(current.longitude);


                String [] categories = new String [current.categories.size()];
                current.categories.toArray(categories);
                bb.putInt(current.numCategories);

                // add categories for each yelpdata item
                for (int j = 0; j<current.numCategories; j++){
                    byte[] catBytes = current.categories.get(j).getBytes();
                    bb.putInt(catBytes.length);
                    bb.put(catBytes);
                }

            }

            bb.putInt(n.currentNumberOfChildren);
            for (int i = 0; i<n.currentNumberOfChildren; i++){
                bb.putLong(n.children[i]);
            }


            bb.flip();
            fc.write(bb);
            bb.clear();
            fc.close();
            file.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
}
