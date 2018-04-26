import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BTree implements Serializable {
    final static int NODESIZE = 4096;


    public static class Node implements Serializable{
        private int keyCount;
        private int childCount;
        private Business[] businesses;
        private Node[] children;
        private boolean isLeaf;
        private long id;


        public Node(boolean l, long i){
            businesses = new Business[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            keyCount = 0;
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

        public int getKeyCount() {
            return keyCount;
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



        public void insertBusiness(Business b){
            businesses[keyCount] = b;
            keyCount++;
        }

        public void removeChild(int i){
            children[i] = null;
            childCount--;
        }

        public void removeBusiness(int i){
            businesses[i] = null;
            keyCount--;
        }

        public int middleKey(){
            return getBusinesses().length / 2;
        }

        public boolean isFull(){
            return keyCount == ((2*minDegree)-1);
        }


    }

    Node root;
    final static int minDegree = 16;
    private int height;
    //private Business searchResult = null;
    private int total;
   // ArrayList<Business> allBusinesses = new ArrayList<>();


    //private ArrayList<Business> businesses;

    public BTree() throws IOException{
        total = 0;
        Node temp = new Node(true, total);
        temp.keyCount =0;
        temp.childCount=0;
        root = temp;
        WriteNode(temp);

        //also need to write the root
        //businesses = new ArrayList<>();
    }



    public  Node getRoot() {
        return this.root;
    }



    /*public ArrayList<Business> traverse(Node n){
        int i;
        for (i = 0; i < n.keyCount; i++){
            if (!n.isLeaf){
                traverse(n.getChildren()[i]);
            }
            Business b = n.businesses[i];
            allBusinesses.add(b);
            //businesses.add(b);
        }
        //last child
        if (!n.isLeaf){
            traverse(n.getChildren()[i]);
        }
        return allBusinesses;
    }*/

    /*public ArrayList<Business> traverseFromLoadedRoot(Node n) throws IOException{
        for (int i = 0; i < n.keyCount; i++){
            Node nextToLook=null;
            if (n.childCount != 0){
                nextToLook = n.getChildren()[i];
            }

            if (nextToLook != null) {
                if (!n.isLeaf && n.id != nextToLook.id) {
                    traverseFromLoadedRoot(this.ReadNode(nextToLook.id));
                }
                Business b = n.businesses[i];
                allBusinesses.add(b);
            }
        }

        return allBusinesses;
    }
*/

    public void insert(Business b) throws Exception{
        Node r = root;
       if (root.keyCount == 2 * minDegree -1) {
           total++;
           Node s = new Node (false, total);
           root = s;
           s.keyCount = 0;
           s.children[0] = r;
           s.childCount++;
           split(s,r);
           insertNonFull(s,b);
       } else {
           insertNonFull(r, b);
       }
    }

    public void insertNonFull(Node x, Business b) throws IOException{
        int i = x.getKeyCount() - 1;

        if (x.isLeaf) {
            while (i > -1 && b.hashCode() < x.businesses[i].hashCode()) {
                x.businesses[i + 1] = x.businesses[i];
                i--;
            }

            i++;
            x.businesses[i] = b;
            x.keyCount++;
            WriteNode(x);
        } else {
            while (i > -1 && b.hashCode() < x.businesses[i].hashCode()) { //find appropriate spot
                i--;
            }

            i++;
            Node temp = x.children[i];

            if (x.getChildren()[i].isFull()) {
                split(x, x.getChildren()[i]);
                if (b.hashCode() > x.businesses[i].hashCode()) {
                    i++;
                }
            }
            insertNonFull(temp, b);
        }
    }

    public void split(Node x, Node y) throws IOException {
        total++;
        Node z = new Node(y.isLeaf,total);
        z.isLeaf= y.isLeaf;
        for (int i = 0; i < minDegree - 1; i++) { //move second half of y's keyCount to to first half of z's keyCount
            z.businesses[i] = y.businesses[i + minDegree];
            z.keyCount++; //just added a key, increment numKeys
            y.businesses[i + minDegree] = null; ///this line might give some weird errors - for just keyCount it was y.keyCount[i + K] = 0
            y.keyCount--;
        }

        if (!y.isLeaf) {
            //move second half of y's pointers to be first half of z's pointers
            for (int i = 0; i < minDegree; i++) {
                z.children[i] = y.children[i + minDegree];
                z.childCount++;
                y.children[i + minDegree] = null;
                y.childCount--;
            }
        }

        // Z node can never point at 0
        int index = x.keyCount - 1;
        while (index > -1 && y.businesses[minDegree - 1].hashCode() < x.businesses[index].hashCode()) {
            x.businesses[index + 1] = x.businesses[index];
            index--;
        }

        index++;
        x.businesses[index] = y.businesses[minDegree - 1];
        x.keyCount++;
        y.businesses[minDegree - 1] = null; /// might give an error also -- used to be y.keyCount[K - 1] = 0
        y.keyCount--;


        int index2 = x.childCount - 1;
        while (index2 > index) {
            x.children[index2 + 1] = x.children[index2];
            index2--;
        }

        index2++;
        x.children[index2] = z;
        x.childCount++;


        WriteNode(x);
        WriteNode(y);
        WriteNode(z);
    }





    public Business search(Node n, Business b) throws IOException {
        int i = 0;

        while (i < n.getKeyCount() && b.hashCode() > n.getBusinesses()[i].hashCode()) {
            i += 1;
        }
        if (i <= n.getKeyCount() && b.hashCode() == n.getBusinesses()[i].hashCode()) {
            return n.businesses[i];
        } else if (n.isLeaf()){
            return null;
        } else {
            if (b.hashCode() < n.getBusinesses()[i].hashCode()) {
                if (n.children[i].id != 0){
                    return search(this.ReadNode(n.getChildren()[i].id), b);
                } else {
                    return null;
                }
            } else {
                return search(this.ReadNode(n.getChildren()[i+1].id), b);

            }
        }
    }

    /*public void search(Node n, String k){
        int i;
        for (i = 0; i < n.keyCount; i++){
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
    }*/

    public void showRoot() {
        for (Business b : root.getBusinesses()) {
            if (b != null) {
                System.out.println(b.getBusiness_id());
            }
        }
    }


    void writeAllNodes(Node n) throws IOException{
        int i;
        for (i = 0; i < n.keyCount; i++){
            if (!n.isLeaf){
                writeAllNodes(n.getChildren()[i]);
            }
            System.out.println("writing node with id: " + n.id + " ");
            WriteNode(n);
        }
        if (!n.isLeaf){
            writeAllNodes(n.getChildren()[i]);
        }
    }

    public void  WriteNode(Node n) throws IOException{
        RandomAccessFile file = new RandomAccessFile("Nodes", "rw");
        file.seek(n.id * NODESIZE);
        FileChannel fc = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(NODESIZE);

        int leaf;
        if (this.root.isLeaf){
            leaf=1;
        } else {
            leaf =0;
        }

        bb.putInt(leaf);

        if (n.getId() > 9000){
            System.out.println();
        }
        bb.putLong(n.getId());

        bb.putInt(n.getKeyCount());
        for (int i = 0; i<n.getKeyCount(); i++){

            Business current = n.businesses[i];

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
        bb.putInt(n.childCount);
        for (int j = 0; j<n.childCount; j++){
            if (n.children[j].getId() > 9000){
                System.out.println();
            }
            bb.putLong(n.children[j].getId());
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
        if (NODESIZE* id < 0) {
            System.out.println("fuck");
        }

        file.seek(NODESIZE * id);
        FileChannel fc = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(NODESIZE);
        fc.read(bb);
        bb.flip();

        int leaf = bb.getInt();
        if (leaf == 1) {
            temp.isLeaf = true;
        } else {
            temp.isLeaf = false;
        }

        temp.id = bb.getLong();
        temp.keyCount = bb.getInt();
        for (int i = 0; i < temp.keyCount; i++) {
//        for (int i = 0; i < temp.keyCount -1; i++) {
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
            if(current != null) {
                if (i < 32) {
                    temp.businesses[i] = current;
                }
            }
        }

        temp.childCount = bb.getInt();
        for (int i = 0; i < temp.childCount; i++) {
            // This here doesnt actually get the full node, just the location of the node in the file
            Long idp = bb.getLong();
            if (i < 32) {
                temp.children[i] = new Node(false, idp);
            }
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
            ByteBuffer bb = ByteBuffer.allocate(NODESIZE);
            fc.read(bb);
            bb.flip();

            BTree bt = new BTree();
            bt.root = r;

            int leaf = bb.getInt();
            if (leaf == 1) {
                r.isLeaf = true;
            } else {
                r.isLeaf = false;
            }

            r.id = bb.getLong();
            r.keyCount = bb.getInt();
            for (int i = 0; i < r.keyCount; i++) {

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
        ByteBuffer bb = ByteBuffer.allocate(NODESIZE);

        int leaf;
        if (this.root.isLeaf){
            leaf=1;
        } else {
            leaf =0;
        }

        bb.putInt(leaf);
        bb.putLong(this.root.getId());

        bb.putInt(this.root.getKeyCount());
        for (int i = 0; i<this.root.getKeyCount(); i++){

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



    public static void main(String[] args) throws IOException, Exception {
       Scanner kb = new Scanner(System.in);


       /*DatabaseParser dp = new DatabaseParser();
        List<Business> businesses = dp.businessesParser();
        BTree bt = new BTree();
        int x = 0;
        for (Business b : businesses){
            x++;
            System.out.println(x);
            bt.insert(b);

        }

        System.out.println(bt.total);
        bt.writeRoot();
        //bt.writeAllNodes(bt.root);
        Node no = bt.ReadNode((long)1200);*/


        // right now the traverseFromRoot method and readNode method always get the root for some super weird reason


        BTree bt2 = BTree.loadRoot();
        /*Node n = bt2.ReadNode(bt2.root.children[0].id);
        Node n2 = bt2.ReadNode(bt2.root.children[1].id);*/


        //bt.traverse(getRoot());
        System.out.println();
        String b;
        System.out.println("Enter a business id: ");

        b = kb.nextLine();
        Business test = new Business(b, null,null,null,null,null,0,0,null);

        Business bu = bt2.search(bt2.root,test);
        System.out.println(bu.getName());
        System.out.println();
//
//        while (true) {
//            if (temp != null) {
//                System.out.println(temp.toString());
//                System.out.println("Enter a business id: ");
//                b = kb.nextLine();
//                test = new Business(b, null,null,null,null,null,0,0,null);
//                bt.search(bt.getRoot(), test.getBusiness_id());
//                temp = bt.getSearchResult();
//            } else {
//                System.out.println("not found");
//                break;
//            }
//        }

    }
}
