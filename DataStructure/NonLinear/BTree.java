package DataStructure.NonLinear;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

    public String search(Integer key) {
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

    public void insert(Integer key, String address) {
        System.out.println("Inserting " + key + " " + address);

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

        print();

        if(current.keys.size() > maxKeys) split(current);
    }

    public void split(Node current) {
        System.out.println("Splitting");
        int midIdx = current.keys.size() / 2;
        Integer midKey = current.keys.get(midIdx);
        String midAddress = current.addresses.get(midIdx);

        Node parent;
        int childIndex;
        if(current == root) {
            parent = new Node(null, false);
            root = parent;
            childIndex = 0;
            current.parent = parent;
        } else {
            parent = current.parent;
            childIndex = parent.children.indexOf(current);
            parent.children.remove(childIndex);
        }

        parent.keys.add(childIndex, midKey);
        parent.addresses.add(childIndex, midAddress);

        Node left = new Node(parent, current.isLeaf);
        Node right = new Node(parent, current.isLeaf);

        for(int i = 0; i < midIdx; i++) {
            left.keys.add(current.keys.get(i));
            left.addresses.add(current.addresses.get(i));
            if (!current.isLeaf && i < current.children.size()) {
                left.children.add(current.children.get(i));
                current.children.get(i).parent = left;
            }
        }
        if (!current.isLeaf && current.children.size() > midIdx) {
            left.children.add(current.children.get(midIdx));
            current.children.get(midIdx).parent = left;
        }

        for(int i = midIdx + 1; i < current.keys.size(); i++) {
            right.keys.add(current.keys.get(i));
            right.addresses.add(current.addresses.get(i));
            if (!current.isLeaf && i < current.children.size()) {
                right.children.add(current.children.get(i));
                current.children.get(i).parent = right;
            }
        }
        if (!current.isLeaf && current.children.size() > current.keys.size()) {
            right.children.add(current.children.get(current.keys.size()));
            current.children.get(current.keys.size()).parent = right;
        }

        parent.children.add(childIndex, left);
        parent.children.add(childIndex + 1, right);

        print();

        if(parent.keys.size() > maxKeys) split(parent);
    }

    public void delete(Integer key) {
        System.out.println("Deleting " + key);

        if(root == null) return;
        Node current = root;

        while(current != null && !current.keys.contains(key)) {
            int idx = -(Collections.binarySearch(current.keys, key) + 1);
            current = current.children.get(idx);
        }

        if(current == null) return;
        int idx = current.keys.indexOf(key);
        if(current.isLeaf) {
            current.keys.remove(idx);
            current.addresses.remove(idx);
            if(current.keys.size() < minKeys) rotate(current);
        } else {    // predecessor 와 교환 후 삭제
            Node preNode = current.children.get(idx);
            while(!preNode.isLeaf) {
                preNode = preNode.children.getLast();
            }
            current.keys.set(idx, preNode.keys.getLast());
            current.addresses.set(idx, preNode.addresses.getLast());
            preNode.keys.removeLast();
            preNode.addresses.removeLast();
            if(preNode.keys.size() < minKeys) rotate(preNode);
        }

        print();
    }

    public void rotate(Node current) {
        if(current == root) return;
        Node parent = current.parent;
        int idx = parent.children.indexOf(current);
        Node prevSibling = null;
        Node nextSibling = null;

        if(idx != 0) {
            prevSibling = parent.children.get(idx - 1);
        }

        if(idx != parent.children.size() - 1) {
            nextSibling = parent.children.get(idx + 1);
        }

        if(prevSibling != null && prevSibling.keys.size() > minKeys) {
            System.out.println("Borrowing from previous sibling");

            Integer prevKey = prevSibling.keys.removeLast();
            String prevAddress = prevSibling.addresses.removeLast();
            Node prevChild = null;
            if(prevSibling.isLeaf && !prevSibling.children.isEmpty()) {
                prevChild = prevSibling.children.removeLast();
                prevChild.parent = current;
            }

            Integer parentKey = parent.keys.get(idx-1);
            String parentAddress = parent.addresses.get(idx-1);

            parent.keys.set(idx-1, prevKey);
            parent.addresses.set(idx-1, prevAddress);

            current.keys.addFirst(parentKey);
            current.addresses.addFirst(parentAddress);

            if(!current.isLeaf && prevChild != null) {
                current.children.add(0, prevChild);
            }

            print();

        } else if(nextSibling != null && nextSibling.keys.size() > minKeys) {
            System.out.println("Borrowing from next sibling");

            Integer nextKey = nextSibling.keys.removeFirst();
            String nextAddress = nextSibling.addresses.removeFirst();
            Node nextChild = null;
            if(!nextSibling.isLeaf && !nextSibling.children.isEmpty()) {
                nextChild = nextSibling.children.removeFirst();
                nextChild.parent = current;
            }

            Integer parentKey = parent.keys.get(idx);
            String parentAddress = parent.addresses.get(idx);

            parent.keys.set(idx, nextKey);
            parent.addresses.set(idx, nextAddress);

            current.keys.add(parentKey);
            current.addresses.add(parentAddress);

            if(!current.isLeaf && nextChild != null) {
                current.children.add(nextChild);
            }

            print();
        } else {
            System.out.println("Cant't borrow");
            if(idx > 0) {
                merge(parent, idx-1, idx);
            } else {
                merge(parent, idx, idx+1);
            }
        }
    }

    public void merge(Node parent, int leftIdx, int rightIdx) {
        System.out.println("Merging");

        Node left = parent.children.get(leftIdx);
        Node right = parent.children.get(rightIdx);

        int parentKey = parent.keys.get(leftIdx);
        String parentAddress = parent.addresses.get(leftIdx);

        parent.keys.remove(leftIdx);
        parent.addresses.remove(leftIdx);
        parent.children.remove(rightIdx);

        left.keys.add(parentKey);
        left.addresses.add(parentAddress);

        left.keys.addAll(right.keys);
        left.addresses.addAll(right.addresses);

        if (!left.isLeaf) {
            left.children.addAll(right.children);
            for(Node child : right.children) {
                child.parent = left;
            }
        }

        print();

        if(parent == root && parent.keys.isEmpty()) {
            root = left;
            left.parent = null;
        } else if(parent.keys.size() < minKeys) {
            rotate(parent);
        }
    }

    public void print() {
        System.out.println();
        printTree(root, 0);
        System.out.println();
    }

    private void printTree(Node node, int level) {
        if (node == null)
            return;
        System.out.print("Level " + level + " [");
        for (Integer key : node.keys) {
            System.out.print(key + " ");
        }
        System.out.println("]");
        if (!node.isLeaf) {
            for (Node child : node.children) {
                printTree(child, level + 1);
            }
        }
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

    public static void main(String[] args) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st;
        System.out.print("Enter the degree of B-Tree: ");
        int degree = Integer.parseInt(br.readLine());
        BTree tree = new BTree(degree);

        System.out.println("사용 가능한 명령어:");
        System.out.println("  insert <key> <value>  : 키와 값을 삽입");
        System.out.println("  delete <key>          : 키를 삭제");
        System.out.println("  search <key>          : 키를 가진 노드의 값을 출력");
        System.out.println("  quit 또는 q            : 종료");

        while (true) {
            System.out.print("명령어 입력: ");
            String line = br.readLine().trim();
            if (line.equalsIgnoreCase("q") || line.equalsIgnoreCase("quit")) {
                break;
            }
            st = new StringTokenizer(line);
            if (!st.hasMoreTokens())
                continue;
            String command = st.nextToken();
            if (command.equalsIgnoreCase("insert") || command.equalsIgnoreCase("i")) {
                if (st.hasMoreTokens()) {
                    String keyStr = st.nextToken();
                    try {
                        Integer key = Integer.parseInt(keyStr);
                        String value = "";
                        if (st.hasMoreTokens()) {
                            value = st.nextToken();
                        }
                        tree.insert(key, value);
                        System.out.println("삽입됨: key = " + key + ", value = " + value);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 key 형식입니다.");
                    }
                } else {
                    System.out.println("사용법: insert <key> <value>");
                }
            } else if (command.equalsIgnoreCase("delete") || command.equalsIgnoreCase("d")) {
                if (st.hasMoreTokens()) {
                    String keyStr = st.nextToken();
                    try {
                        Integer key = Integer.parseInt(keyStr);
                        tree.delete(key);
                        System.out.println("삭제됨: key = " + key);
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 key 형식입니다.");
                    }
                } else {
                    System.out.println("사용법: delete <key>");
                }
            } else if (command.equalsIgnoreCase("search") || command.equalsIgnoreCase("s")) {
                if (st.hasMoreTokens()) {
                    String keyStr = st.nextToken();
                    try {
                        Integer key = Integer.parseInt(keyStr);
                        String result = tree.search(key);
                        if (result != null) {
                            System.out.println("검색 결과: key = " + key + ", value = " + result);
                        } else {
                            System.out.println("키 " + key + "를 가진 노드를 찾을 수 없습니다.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("잘못된 key 형식입니다.");
                    }
                } else {
                    System.out.println("사용법: search <key>");
                }
            } else {
                System.out.println("알 수 없는 명령어입니다.");
            }
        }
        System.out.println("프로그램을 종료합니다.");
    }
}
