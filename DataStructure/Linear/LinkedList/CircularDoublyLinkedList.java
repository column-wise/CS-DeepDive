package DataStructure.Linear.LinkedList;

public class CircularDoublyLinkedList {
    private Node head;
    private Node tail;
    int size;

    CircularDoublyLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    public void insertFirst(String data) {
        Node newNode = new Node(data);

        if (head == null) {
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            newNode.prev = tail;
            head.prev = newNode;
            tail.next = newNode;
            head = newNode;
        }
        size++;
    }

    public void insert(Node prev, String data) {
        if (head == null) return;

        Node newNode = new Node(data);
        newNode.next = prev.next;
        prev.next.prev = newNode;
        newNode.prev = prev;
        prev.next = newNode;

        if (tail == prev) {
            tail = newNode;
        }
        size++;
    }

    public void insertLast(String data) {
        Node newNode = new Node(data);

        if (tail == null) {
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            tail.next = newNode;
            head.prev = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    public void deleteFirst() {
        if (head == null) return;

        if (head == tail) { // 리스트에 노드가 하나만 있는 경우
            head = null;
            tail = null;
        } else {
            tail.next = head.next;
            head.next.prev = tail;
            head = head.next;
        }
        size--;
    }

    public void deleteLast() {
        if (tail == null) return;

        if (head == tail) { // 리스트에 노드가 하나만 있는 경우
            head = null;
            tail = null;
        } else {
            tail.prev.next = head;
            head.prev = tail.prev;
            tail = tail.prev;
        }
        size--;
    }

    public void delete(String data) {
        if (head == null) return;

        if (head.getData().equals(data)) {
            deleteFirst();
            return;
        }

        Node cur = head.next;
        while (cur != head) {
            if (cur.getData().equals(data)) {
                if (cur == tail) {
                    deleteLast(); // tail인 경우 deleteLast() 호출
                } else {
                    // 중간 노드를 삭제
                    cur.prev.next = cur.next;
                    cur.next.prev = cur.prev;
                    cur.next = null;
                    cur.prev = null;
                    size--;
                }
                return;
            }
            cur = cur.next;
        }
    }

    public void reverse() {
        if (head == null || head.next == head) return;

        Node cur = head;
        Node temp = null;

        do {
            temp = cur.prev;
            cur.prev = cur.next;
            cur.next = temp;
            cur = cur.prev;
        } while (cur != head);

        // head와 tail 교환
        temp = head;
        head = tail;
        tail = temp;
    }

    public void printList() {
        if (head == null) {
            System.out.println("List is empty");
            return;
        }

        Node cur = head;
        do {
            System.out.print(cur.getData() + " ");
            cur = cur.next;
        } while (cur != head);
        System.out.println();
    }

    class Node {
        private String data;
        Node prev;
        Node next;

        Node(String data) {
            this.data = data;
            prev = null;
            next = null;
        }

        public String getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        CircularDoublyLinkedList list = new CircularDoublyLinkedList();

        System.out.println("Inserting elements at the start:");
        list.insertFirst("A");
        list.insertFirst("B");
        list.insertFirst("C");
        list.printList();

        System.out.println("Inserting elements at the end:");
        list.insertLast("D");
        list.insertLast("E");
        list.printList();

        System.out.println("Inserting element after head:");
        list.insert(list.head, "X");
        list.printList();

        System.out.println("Deleting first element:");
        list.deleteFirst();
        list.printList();

        System.out.println("Deleting last element:");
        list.deleteLast();
        list.printList();

        System.out.println("Deleting element with data 'X':");
        list.delete("X");
        list.printList();

        System.out.println("Reversing the list:");
        list.reverse();
        list.printList();
    }
}
