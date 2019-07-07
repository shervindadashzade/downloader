package Test;

import java.util.Queue;

public class ThreadRemove extends Thread {
    Queue queue;
    public ThreadRemove(Queue q){
        queue = q;
    }
    public void run(){
        while(queue.size()>0){
            System.out.println(queue.remove());
        }
    }
}
