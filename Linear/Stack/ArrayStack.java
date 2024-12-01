package Stack;

public class ArrayStack {
    private int maxSize;
    private int top;
    private int[] arr;

    public ArrayStack(int maxSize) {
        this.maxSize = maxSize;
        arr = new int[maxSize];
        top = -1;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public void push(int data) {
        if(top == maxSize) return;
        arr[++top] = data;
    }

    public int pop() {
        if(isEmpty()) return -1;
        return arr[top--];
    }

    public int peek() {
        if(isEmpty()) return -1;
        return arr[top];
    }

    public static void main(String[] args) {
        ArrayStack stack = new ArrayStack(5);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        System.out.println(stack.peek());
        System.out.println(stack.pop());
        System.out.println(stack.peek());
    }
}
