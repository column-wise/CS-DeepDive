package NonLinear;

import java.util.*;

public class BTree {
    private int degrees;
    private int minKeys;
    private int maxKeys;
    private Node root;

    public BTree(int degrees) {
        this.root = null;
        this.degrees = degrees;
        this.maxKeys = degrees - 1;
        this.minKeys = degrees / 2;
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
            root = new Node(null, true);
            root.keys.add(key);
            root.addresses.add(address);
            return;
        }

        Node current = root;
        while (!current.isLeaf) {
            int idx = -(Collections.binarySearch(current.keys, key) + 1);
            if(idx < 0) return;
            if (idx >= current.children.size()) {
                current.children.add(new Node(current, true));
                current.isLeaf = false;
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
        int midIdx = current.keys.size() / 2;
        int midKey = current.keys.get(midIdx);
        String midAddress = current.addresses.get(midIdx);

        if(current == root) {
            current.parent = new Node(null, false);
        }
        Node parent = current.parent;

        int idx = -(Collections.binarySearch(parent.keys, midKey) + 1);
        parent.keys.add(idx, midKey);
        parent.addresses.add(idx, midAddress);

        Node left = new Node(parent, current.isLeaf);
        Node right = new Node(parent, current.isLeaf);

        for(int i = 0; i < midIdx; i++) {
            left.keys.add(current.keys.get(i));
            left.addresses.add(current.addresses.get(i));
            left.children.add(current.children.get(i));
        }

        for(int i = midIdx + 1; i < current.keys.size(); i++) {
            right.keys.add(current.keys.get(i));
            right.addresses.add(current.addresses.get(i));
            right.children.add(current.children.get(i));
        }

        parent.children.add(idx, left);
        parent.children.add(idx+1, right);

        if(parent.keys.size() > maxKeys) split(parent);
    }

    public void delete(int key) {

    }

    private class Node {
        List<Integer> keys;
        List<String> addresses;
        List<Node> children;    // children.size == keys.size + 1
        boolean isLeaf;
        Node parent;

        public Node(Node parent, boolean isLeaf) {
            keys = new ArrayList<>();
            addresses = new ArrayList<>();
            children = new ArrayList<>();
            this.isLeaf = isLeaf;
            this.parent = parent;
        }
    }

    public static void main(String[] args) {
        BTree tree = new BTree(3);

    }
}
