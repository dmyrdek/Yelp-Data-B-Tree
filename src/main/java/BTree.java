import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BTree implements Serializable {
    public static class Node implements Serializable{
        private Integer keys;
        private Integer childCount;
        private Business[] businesses;
        private Node[] children;
        private boolean isLeaf;



        public Node(boolean l){
            businesses = new Business[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            keys = 0;
            childCount = 0;
            isLeaf = l;
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

    private static Node root;
    private static int minDegree;
    private int height;
    private Business searchResult = null;

    public Business getSearchResult() {
        return searchResult;
    }

    //private ArrayList<Business> businesses;

    public BTree() {
        minDegree = 16;
        root = null;
        //businesses = new ArrayList<>();
    }

    public static Node getRoot() {
        return root;
    }



    public void traverse(Node n){
        int i;
        for (i = 0; i < n.keys; i++){
            if (!n.isLeaf){
                traverse(n.children[i]);
            }
            Business b = n.businesses[i];
            if ("23vQbe4qVflA3hTBmwoD8g".compareTo(b.getBusiness_id()) == 0){
                System.out.println(b.getName());
            }
            //businesses.add(b);
        }
        if (!n.isLeaf){
            traverse(n.getChildren()[i]);
        }
    }

    public void insert(Business b){
        if (root == null){
            root = new Node(true);
            root.insertBusiness(b);
            height++;
        } else {
            if (root.isFull()){
                System.out.println("inserted when root is full");
                Node x = new Node(false);
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
        Node left = parent.getChildren()[i];
        Node right = new Node(left.isLeaf());
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

        while (i < n.getKeys() -1 && b.compareTo(n.getBusinesses()[i]) > 0) {
            i += 1;
        }
        if (i <= n.getKeys() && b.compareTo(n.getBusinesses()[i]) == 0) {
            searchResult = n.businesses[i];
        } else {
            if (b.compareTo(n.getBusinesses()[i]) < 0) {
                search(n.getChildren()[i-1], b);
            } else {
                search(n.getChildren()[i], b);
            }
        }
    }

    public void search(Node n, String k){
        int i;
        for (i = 0; i < n.keys; i++){
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


    public static void main(String[] args) throws IOException {
        DatabaseParser dp = new DatabaseParser();
        List<Business> businesses = dp.businessesParser();
        BTree bt = new BTree();
        int x = 0;
        for (Business b : businesses){
            x++;
            System.out.println(x);
            bt.insert(b);
        }
        //bt.traverse(getRoot());
        Business test = new Business("77h11eWv6HKJAgojLx8G4w", "Eggslut",null,null,null,null,0,0,null);
        bt.search(getRoot(),test);
        System.out.println(bt.getSearchResult().getName());
    }
}
