package Linear.LinkedList;

public class DummyNodeLinkedList {
    Node head;

    public DummyNodeLinkedList () {
        head = new Node();  // 더미 노드 생성
    }

    // prevNode 뒤에 새 노드 추가
    public void insert(Node prevNode, String data) {
        if (prevNode == null) {
            return;
        }

        Node newNode = new Node(data);
        newNode.next = prevNode.next;
        prevNode.next = newNode;
    }

    public void insertFirst(String data) {
        Node newNode = new Node(data);
        newNode.next = head.next;
        head.next = newNode;
    }

    // 마지막에 새 노드 삽입
    public void insertLast(String data) {
        Node newNode = new Node(data);

        Node curNode = head;  // head가 항상 더미 노드를 가리키므로 head부터 시작
        while (curNode.next != null) {
            curNode = curNode.next;
        }
        curNode.next = newNode;
    }

    // 중간 노드 삭제
    public void delete(String data) {
        Node prevNode = head;

        if (head.next == null) {
            return;
        }

        Node curNode = head.next;

        while (curNode != null) {
            if (curNode.getData().equals(data)) {
                prevNode.next = curNode.next;
                curNode.next = null;
                return;
            } else {
                prevNode = curNode;
                curNode = curNode.next;
            }
        }
    }

    // 마지막 노드 삭제
    public void deleteLast() {
        Node prevNode = head;

        if (head.next == null) {
            return;
        }

        Node curNode = head.next;
        while (curNode.next != null) {
            prevNode = curNode;
            curNode = curNode.next;
        }
        prevNode.next = null;
    }

    public Node search(String data) {
        Node curNode = head.next; // 더미 다음 노드부터 탐색

        while (curNode != null && !curNode.getData().equals(data)) {
            curNode = curNode.next;
        }

        return curNode;
    }

    public void reverse() {
        Node prevNode = null;
        Node curNode = null;
        Node nextNode = head.next;

        while (nextNode != null) {
            prevNode = curNode;
            curNode = nextNode;
            nextNode = nextNode.next;
            curNode.next = prevNode;
        }
        head.next = curNode;
    }

    public void printList() {
        Node curNode = head.next;  // 더미 다음 노드부터 출력
        while (curNode != null) {
            System.out.print(curNode.getData() + " ");
            curNode = curNode.next;
        }
        System.out.println();
    }

    class Node {
        private String data;
        public Node next;

        public Node() {
            data = null;
            next = null;
        }

        public Node(String data) {
            this.data = data;
            this.next = null;
        }

        public Node(String data, Node next) {
            this.data = data;
            this.next = next;
        }

        String getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        DummyNodeLinkedList list = new DummyNodeLinkedList ();
        list.insertLast("2");
        list.insertFirst("1");
        list.insertLast("4");
        Node node = list.search("2");
        list.insert(node, "3");
        list.insertLast("5");

        System.out.println("Original List:");
        list.printList();

        System.out.println("Reversed List:");
        list.reverse();
        list.printList();

        System.out.println("After Deleting '3':");
        list.delete("3");
        list.printList();

        System.out.println("After Deleting Last:");
        list.deleteLast();
        list.printList();
    }
}
