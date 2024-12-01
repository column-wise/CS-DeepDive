package Stack;

public class LinkedListStack {
    private Node head;

    public LinkedListStack() {
        this.head = null;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void push(int data) {
        Node node = new Node(data);
        node.next = head;
        head = node;
    }

    public int pop() {
        if (isEmpty()) return -1;
        Node node = head;
        head = head.next;
        node.next = null;
        return node.data;
    }

    public int peek() {
        if(isEmpty()) return -1;
        return head.data;
    }

    private static class Node {
        int data;
        Node next;

        private Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    public static void main(String[] args) {
        LinkedListStack stack = new LinkedListStack();
        System.out.println(stack.isEmpty());
        stack.push(1);
        stack.push(2);
        System.out.println(stack.isEmpty());
        stack.push(3);
        stack.push(4);
        stack.push(5);
        System.out.println(stack.pop());
        System.out.println(stack.peek());
        System.out.println(stack.pop());
        System.out.println(stack.peek());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
    }
}
