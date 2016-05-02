import java.io.Serializable;


public class LinkedStack<E> implements Stack<E>,Serializable{

    private static class Elem<T>{
        private T info;
        private Elem<T> next;
        private Elem( T info, Elem<T> next) {
            this.info = info;
            this.next = next;
        }
    }

    private Elem<E> top; // instance variable

    public LinkedStack() {
        top = null;
    }

    public boolean isEmpty() {
        return top == null;
    }

    public void push( E info ) throws NullPointerException {
    
        if (info == null){
              System.out.println("Push of a null object is not allowed.");
              
        } else {
              top = new Elem<E>( info, top );
        }
    }

    public E peek() {
        return top.info;
    }

    public E pop() throws NullPointerException {
    
        if (this.isEmpty()){
              System.out.println("The stack is empty.");
              return null; 
        } else {

        
        E savedInfo = top.info;

        Elem<E> oldTop = top;
        Elem<E> newTop = top.next;

        top = newTop;

        oldTop.info = null; // scrubbing the memory
        oldTop.next = null; // scrubbing the memory
       
       return savedInfo;
       
       }
    }
}
