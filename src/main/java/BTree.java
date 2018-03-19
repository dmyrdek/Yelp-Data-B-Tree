import java.lang.reflect.Array;
import java.util.Arrays;

public class BTree {
    private static class Node{
        private int keys;
        private int childCount;
        private Business[] businesses;
        private Node[] children;
        private boolean isLeaf;

        public Node(boolean l){
            businesses = new Business[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            keys = 0;
            childCount = 0;
        }

        public Node[] getChildren() {
            return children;
        }

        public Business[] getBusinesses() {
            return businesses;
        }

        public boolean isLeaf(){
            return isLeaf;
        }

        public int getChildCount() {
            return childCount;
        }

        private void sortBusinesses(){
            Arrays.sort(getBusinesses(),0,keys);
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
    }

    private static Node root;
    private static int minDegree;

}
