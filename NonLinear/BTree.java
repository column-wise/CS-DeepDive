package NonLinear;

import java.util.*;

public class BTree {
    private int degrees;
    private int maxKeys;
    private Node root;

    public BTree(int degrees) {
        this.root = null;
        this.degrees = degrees;
        this.maxKeys = degrees - 1;
    }

    public String search(int key) {
        Node current = root;
        while (current != null) {
            if(current.keys.contains(key)) {
                return current.addresses.get(current.keys.indexOf(key));
            } else {
                int idx = -(Collections.binarySearch(current.keys, key) + 1);
                try {
                    current = current.children.get(idx);
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public void insert(int key, String address) {
        if(root == null) {
            root = new Node(null);
            root.keys.add(key);
            root.addresses.add(address);
            return;
        }

        Node current = root;
        while (!current.isLeaf) {
            int idx = -(Collections.binarySearch(current.keys, key) + 1);
            if(idx < 0) return;
            if (idx >= current.children.size()) {
                current.children.add(new Node(current));
            }
            current = current.children.get(idx);
        }

        int idx = -(Collections.binarySearch(current.keys, key) + 1);
        if(idx < 0) return;

        current.keys.add(idx, key);
        current.addresses.add(idx, address);

        if(current.keys.size() > maxKeys) split(current);
    }

    public void split(Node current) {

    }

    public void delete(int key) {

    }

    private class Node {
        List<Integer> keys;
        List<String> addresses;
        List<Node> children;    // children.size == keys.size + 1
        boolean isLeaf;
        Node parent;

        public Node(Node parent) {
            keys = new ArrayList<>();
            addresses = new ArrayList<>();
            children = new ArrayList<>();
            isLeaf = true;
            this.parent = parent;
        }
    }

    public static void main(String[] args) {
        BTree tree = new BTree(3);

    }
}
