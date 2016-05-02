public class Testing{
       public static void main(String args[]){
              LinkedQueue<Integer> q = new LinkedQueue<Integer>();
              q.enqueue(null); // not allowed
              q.dequeue(); // empty queue
              LinkedStack<Integer> s = new LinkedStack<Integer>();
              s.push(null); // not allowed
              s.pop(); // empty stack 
       }
}
