import java.io.Serializable;

public class LinkedQueue<E> implements Queue<E>, Serializable {

    private static class Elem<T> {

        private T value;
        private Elem<T> next;

        private Elem( T value, Elem<T> next ) {
            this.value = value;
            this.next = next;
        }
    }

    private Elem<E> front;
    private Elem<E> rear;

    public E peek() {
        return front.value;
    }

    public void enqueue( E o ) throws NullPointerException {
        if (o == null){
              System.out.println("Enqueing a null object is not allowed.");
        } else {
        Elem<E> newElem;
        newElem = new Elem<E>( o, null );
        

        if ( rear == null ) {
            front = rear = newElem;
        } else {
            rear.next = newElem;
            rear = newElem;
        }
        }
    }

    public E dequeue() throws NullPointerException {
    
        if (this.isEmpty()){
              System.out.println("Queue is empty.");
              return null;
        } else {
    
        E result = front.value;
        if ( front != null & front.next == null ) {
            front = rear = null;
        } else {
            front = front.next;
        }
        return result;
    }
    }

    public boolean isEmpty() {
        return front == null;
    }

}
