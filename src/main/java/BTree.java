import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class BTree implements Serializable {
    public static class Node implements Serializable{
        private Integer keys;
        private Integer childCount;
        private Business[] businesses;
        private Node[] children;
        private boolean isLeaf;
        private long id;




        public Node(boolean l, long i){
            businesses = new Business[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            keys = 0;
            childCount = 0;
            isLeaf = l;
            id = i;
        }

        public Long getId(){
            return this.id;
        }

        private void insertChildren(Node n) {
            children[childCount] = n;
            childCount++;
        }

        public Business[] getBusinesses() {
            return businesses;
        }

        public int getKeys() {
            return keys;
        }

        public boolean isLeaf() {
            return isLeaf;
        }

        public int getChildCount() {
            return childCount;
        }

        public Node[] getChildren() {
            return children;
        }

        private void sortBusinesses(){
            Arrays.sort(businesses,0,keys);
        }

        public void insertBusiness(Business b){
            businesses[keys] = b;
            keys++;
            sortBusinesses();
        }

        public void removeChild(int i){
            children[i] = null;
            childCount--;
        }

        public void removeBusiness(int i){
            businesses[i] = null;
            keys--;
        }

        public int middleKey(){
            return getBusinesses().length / 2;
        }

        public boolean isFull(){
            return keys == ((2*minDegree)-1);
        }


    }

    Node root;
    private static int minDegree;
    private int height;
    private Business searchResult = null;
    private int total;
    ArrayList<Business> allBusinesses = new ArrayList<>();

    public Business getSearchResult() {
        return searchResult;
    }

    //private ArrayList<Business> businesses;

    public BTree() {
        minDegree = 16;
        total = 0;
        Node temp = new Node(true, total);
        temp.keys=0;
        temp.childCount=0;
        root = temp;

        //also need to write the root
        //businesses = new ArrayList<>();
    }



    public  Node getRoot() {
        return this.root;
    }



    public ArrayList<Business> traverse(Node n){
        int i;
        for (i = 0; i < n.keys; i++){
            if (!n.isLeaf){
                traverse(n.getChildren()[i]);
            }
            Business b = n.businesses[i];
            allBusinesses.add(b);
            //businesses.add(b);
        }
        if (!n.isLeaf){
            traverse(n.getChildren()[i]);
        }
        return allBusinesses;
    }

    public void insert(Business b){
        if (root == null){
            root = new Node(true,total);
            root.insertBusiness(b);
            height++;
        } else {
            if (root.isFull()){
                System.out.println("inserted when root is full");
                Node x = new Node(false,total);
                x.insertChildren(root);
                root = x;
                split(x, 0);
                insertNonFull(x,b);
                height++;
            } else{
                System.out.println("inserted");
                insertNonFull(root, b);
            }
        }
    }

    public void insertNonFull(Node x, Business b){
        int i = x.getKeys() - 1;

        if (x.isLeaf()){
            x.insertBusiness(b);
        } else {
            while (i > 0 && b.compareTo(x.getBusinesses()[i - 1]) < 0) {
                i -= 1;
            }
            i += 1;

            if (x.getChildren()[i].isFull()) {
                split(x, i);
                if (b.compareTo(x.getBusinesses()[i]) > 0) {
                    i += 1;
                }
            }
            insertNonFull(x.getChildren()[i], b);
        }
    }

    public void split(Node parent, int i) {
        total++;
        Node left = parent.getChildren()[i];
        Node right = new Node(left.isLeaf(),total);
        int median = left.middleKey();

        parent.insertBusiness(left.getBusinesses()[median]);

        for (int j = 1; j <= median; j++) {
            right.insertBusiness(left.getBusinesses()[j + median]);
        }

        for (int j = left.getKeys() - 1; j >= median; j--) {
            left.removeBusiness(j);
        }

        if (!left.isLeaf()) {
            for (int j = 1; j <= median + 1; j++) {
                right.insertChildren(left.getChildren()[j + median]);
            }

            for (int j = left.getChildCount() - 1; j >= median + 1; j--) {
                left.removeChild(j);
            }
        }

        parent.insertChildren(right);
    }





    public void search(Node n, Business b) throws IOException {
        int i = 0;

        while (i < n.getKeys() && b.compareTo(n.getBusinesses()[i]) > 0) {
            i += 1;
        }
        if (i < n.getKeys() && b.compareTo(n.getBusinesses()[i]) == 0) {
            searchResult = n.businesses[i];
        } else if (!n.isLeaf()){
            search(n.getChildren()[i], b);
        }
    }

    public void search(Node n, String k){
        int i;
        for (i = 0; i < n.keys; i++){
            System.out.println(n.id);
            if (!n.isLeaf){
                search(n.getChildren()[i],k);
            }
            if (k.compareTo(n.businesses[i].getBusiness_id()) == 0){
                searchResult = n.businesses[i];
            }

            //businesses.add(b);
        }
        if (!n.isLeaf){
            search(n.getChildren()[i],k);
        }
    }

    public void showRoot() {
        for (Business b : root.getBusinesses()) {
            if (b != null) {
                System.out.println(b.getBusiness_id());
            }
        }
    }


    void writeAllNodes(Node n) throws IOException{
        int i;
        for (i = 0; i < n.keys; i++){
            if (!n.isLeaf){
                writeAllNodes(n.getChildren()[i]);
            }
            System.out.println("writing node with id: " + n.id);
            WriteNode(n);
        }
        if (!n.isLeaf){
            writeAllNodes(n.getChildren()[i]);
        }
    }

    public void  WriteNode(Node n) throws IOException{
        RandomAccessFile file = new RandomAccessFile("Nodes", "rw");
        file.seek(n.id * 4096);
        FileChannel fc = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(4096);

        bb.putInt(this.height);
        bb.putInt(this.total);

        int leaf;
        if (this.root.isLeaf){
            leaf=1;
        } else {
            leaf =0;
        }

        bb.putInt(leaf);
        bb.putLong(this.root.getId());

        bb.putInt(this.root.getKeys());
        for (int i=0; i<this.root.getKeys(); i++){

            Business current = this.root.businesses[i];

            byte [] name = current.getName().getBytes();
            bb.putInt(name.length);
            bb.put(name);

            byte [] id = current.getBusiness_id().getBytes();
            bb.putInt(id.length);
            bb.put(id);

            byte [] city = current.getCity().getBytes();
            bb.putInt(city.length);
            bb.put(city);

            bb.putDouble(current.getLatitude());
            bb.putDouble(current.getLongitude());
        }
        // put the id's of the child nodes so that we can use that id as a multiplier throughout the file
        bb.putInt(this.root.childCount);
        for (int j = 0; j<this.root.childCount; j++){
            bb.putLong(this.root.children[j].id);
        }
        bb.flip();
        fc.write(bb);
        bb.clear();
        fc.close();
        file.close();
    }

    public Node ReadNode(Long id) throws  IOException{
        Node temp = new Node (false, 0); // make random node
        RandomAccessFile file = new RandomAccessFile("Nodes", "rw");
        file.seek(4096 * id);
        FileChannel fc = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(4096);
        fc.read(bb);
        bb.flip();

        int leaf = bb.getInt();
        if (leaf == 1) {
            temp.isLeaf = true;
        } else {
            temp.isLeaf = false;
        }

        temp.id = bb.getLong();
        temp.keys = bb.getInt();
        for (int i = 0; i < temp.keys; i++) {

            //read name
            int nameLen = bb.getInt();
            byte[] nameBuf = new byte[nameLen];
            bb.get(nameBuf);
            String name = new String(nameBuf);

            //read id
            int idLen = bb.getInt();
            byte[] idBuf = new byte[idLen];
            bb.get(idBuf);
            String idn = new String(idBuf);

            //get the city
            int cityLen = bb.getInt();
            byte[] cityBuf = new byte[cityLen];
            bb.get(cityBuf);
            String city = new String(cityBuf);

            Double lattitude = bb.getDouble();
            Double longitude = bb.getDouble();

            Business current = new Business(idn, name, null, city, null, null, lattitude, longitude, null);
            temp.businesses[i] = current;
        }

        temp.childCount = bb.getInt();
        for (int i = 0; i < temp.childCount; i++) {
            // This here doesnt actually get the full node, just the location of the node in the file
            Long idp = bb.getLong();
            temp.children[i] = new Node(false, idp);

        }


        bb.clear();
        fc.close();
        file.close();
        return temp;
    }

    static BTree loadRoot() {
        try {
            Node r = new Node(false, 0);
            RandomAccessFile rf = new RandomAccessFile("Root", "rw");
            rf.seek(0);
            FileChannel fc = rf.getChannel();
            ByteBuffer bb = ByteBuffer.allocate(4096);
            fc.read(bb);
            bb.flip();

            BTree bt = new BTree();
            bt.root = r;
            bt.height = bb.getInt();
            bt.total = bb.getInt();

            int leaf = bb.getInt();
            if (leaf == 1) {
                r.isLeaf = true;
            } else {
                r.isLeaf = false;
            }

            r.id = bb.getLong();
            r.keys = bb.getInt();
            for (int i = 0; i < r.keys; i++) {

                //read name
                int nameLen = bb.getInt();
                byte[] nameBuf = new byte[nameLen];
                bb.get(nameBuf);
                String name = new String(nameBuf);

                //read id
                int idLen = bb.getInt();
                byte[] idBuf = new byte[idLen];
                bb.get(idBuf);
                String id = new String(idBuf);

                //get the city
                int cityLen = bb.getInt();
                byte[] cityBuf = new byte[cityLen];
                bb.get(cityBuf);
                String city = new String(cityBuf);

                Double lattitude = bb.getDouble();
                Double longitude = bb.getDouble();

                Business current = new Business(id, name, null, city, null, null, lattitude, longitude, null);
                r.businesses[i] = current;
            }

            r.childCount = bb.getInt();
            for (int i = 0; i < r.childCount; i++) {
                // This here doesnt actually get the full node, just the location of the node in the file
                Long id = bb.getLong();
                r.children[i] = new Node(false, id);

            }


            bb.clear();
            fc.close();
            rf.close();
            bt.root = r;
            return bt;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeRoot() throws IOException{
        RandomAccessFile rf = new RandomAccessFile("Root", "rw");
        rf.seek(0);
        FileChannel fc = rf.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(4096);

        bb.putInt(this.height);
        bb.putInt(this.total);

        int leaf;
        if (this.root.isLeaf){
            leaf=1;
        } else {
            leaf =0;
        }

        bb.putInt(leaf);
        bb.putLong(this.root.getId());

        bb.putInt(this.root.getKeys());
        for (int i=0; i<this.root.getKeys(); i++){

            Business current = this.root.businesses[i];

            byte [] name = current.getName().getBytes();
            bb.putInt(name.length);
            bb.put(name);

            byte [] id = current.getBusiness_id().getBytes();
            bb.putInt(id.length);
            bb.put(id);

            byte [] city = current.getCity().getBytes();
            bb.putInt(city.length);
            bb.put(city);

            bb.putDouble(current.getLatitude());
            bb.putDouble(current.getLongitude());
        }

        // put the id's of the child nodes so that we can use that id as a multiplier throughout the file
        bb.putInt(this.root.childCount);
        for (int j = 0; j<this.root.childCount; j++){
            bb.putLong(this.root.children[j].id);
        }

        bb.flip();
        fc.write(bb);
        bb.clear();
        fc.close();
        rf.close();
    }



    public static void main(String[] args) throws IOException {
        Scanner kb = new Scanner(System.in);
        DatabaseParser dp = new DatabaseParser();
        List<Business> businesses = dp.businessesParser();
        BTree bt = new BTree();
        int x = 0;
        for (Business b : businesses){
            x++;
            System.out.println(x);

            if (x == 31){
                System.out.println("");
            }
            bt.insert(b);

        }

        System.out.println(bt.total);
        bt.writeRoot();
        bt.writeAllNodes(bt.root);

        BTree bt2 = BTree.loadRoot();



        //bt.traverse(getRoot());
        System.out.println();
        ArrayList <Business> allBusinesses = bt.traverse(bt.root);
        String b;
        System.out.println("Enter a business id: ");
        b = kb.nextLine();
        Business test = new Business(b, null,null,null,null,null,0,0,null);
        for (Business bu : allBusinesses){
            if (bu.getBusiness_id().equals(test.getBusiness_id())){
                System.out.println(bu.toString());
            }
        }
        bt.search(bt.getRoot(),test);
        Business temp = bt.getSearchResult();


        while (true) {
            if (temp != null) {
                System.out.println(temp.toString());
                System.out.println("Enter a business id: ");
                b = kb.nextLine();
                test = new Business(b, null,null,null,null,null,0,0,null);
                bt.search(bt.getRoot(), test.getBusiness_id());
                temp = bt.getSearchResult();
            } else {
                System.out.println("not found");
                break;
            }
        }

    }
}
