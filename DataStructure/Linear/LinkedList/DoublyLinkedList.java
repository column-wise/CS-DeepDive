package DataStructure.Linear.LinkedList;

public class DoublyLinkedList {
    Node head;
    Node tail;

    public DoublyLinkedList() {
        head = null;
        tail = null;
    }

    public void addFirst(String data) {
        Node newNode = new Node(data);

        if(head != null) {
            head.prev = newNode;
        }

        newNode.next = head;
        head = newNode;

        if(tail == null) {
            tail = newNode;
        }
    }

    public void addLast(String data) {
        Node newNode = new Node(data);

        if(tail != null) {
            tail.next = newNode;
        }

        newNode.prev = tail;
        tail = newNode;

        if(head == null) {
            head = newNode;
        }
    }

    public void add(Node prevNode, String data) {
        if (prevNode == null) {
            return;
        }

        Node newNode = new Node(data);
        newNode.next = prevNode.next;
        newNode.prev = prevNode;
        prevNode.next = newNode;

        if (newNode.next == null) {
            tail = newNode;
        } else {
            newNode.next.prev = newNode;
        }
    }

    public void deleteFirst() {
        if(head == null) {
            return;
        }

        if(head == tail) {
            tail = null;
        } else {
            head.next.prev = null;
        }

        Node temp = head;
        head = head.next;
        temp.next = null;
    }

    public void deleteLast() {
        if(tail == null) {
            return;
        }

        if(head == tail) {
            head = null;
        } else {
            tail.prev.next = null;
        }

        Node temp = tail;
        tail = tail.prev;
        temp.prev = null;
    }

    public void delete(String data) {
        if (head == null) {
            return;
        }

        // 첫 번째 노드가 삭제 대상인 경우
        if (head.getData().equals(data)) {
            deleteFirst();
            return;
        }

        // 나머지 노드 삭제
        Node curNode = head.next;

        while (curNode != null) {
            if (curNode.getData().equals(data)) {
                curNode.prev.next = curNode.next;
                if (curNode.next != null) {
                    curNode.next.prev = curNode.prev;
                } else {
                    tail = curNode.prev; // 마지막 노드를 삭제하는 경우 tail 업데이트
                }
                return;
            }
            curNode = curNode.next;
        }
    }


    public void reverse() {
        Node current = head;
        Node temp = null;

        // 모든 노드의 prev와 next를 교환
        while (current != null) {
            temp = current.prev;
            current.prev = current.next;
            current.next = temp;

            current = current.prev;  // 원래 next 방향으로 이동
        }

        // head와 tail을 교환
        if (temp != null) {
            tail = head;
            head = temp.prev;
        }
    }

    public void printList() {
        Node curNode = head;
        while(curNode != null) {
            System.out.print(curNode.getData() + " ");
            curNode = curNode.next;
        }
        System.out.println();
    }

    class Node {
        private String data;
        public Node prev;
        public Node next;

        public Node() {
            this(null);
        }

        public Node(String data) {
            this.data = data;
            prev = null;
            next = null;
        }

        public String getData() {
            return data;
        }
    }

    public static void main(String[] args) {
        DoublyLinkedList list = new DoublyLinkedList();

        list.addLast("1");
        list.addLast("2");
        list.addLast("3");
        list.addLast("4");

        System.out.println("Original List:");
        list.printList(); // 1 2 3 4

        list.reverse();
        System.out.println("Reversed List:");
        list.printList(); // 4 3 2 1

        list.delete("3");
        System.out.println("After Deleting '3':");
        list.printList(); // 4 2 1

        list.deleteFirst();
        System.out.println("After Deleting First:");
        list.printList(); // 2 1

        list.deleteLast();
        System.out.println("After Deleting Last:");
        list.printList(); // 2
    }

}




