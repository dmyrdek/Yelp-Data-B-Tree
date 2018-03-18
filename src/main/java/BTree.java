public class BTree {
    private static class Node{
        private int key;
        private int childCount;
        private Business[] businesses;
        private Node[] children;
        private boolean isLeft;

        public Node(boolean l){
            businesses = new Business[2 * minDegree - 1];
            children = new Node[2 * minDegree];
            key = 0;
            childCount = 0;
        }
    }

    private static Node root;
    private static int minDegree;

}
