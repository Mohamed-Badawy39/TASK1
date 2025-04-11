import java.util.Random;
public class PriorityQueue {
    PNode head;
    PNode tail;
    int length=0;
    PriorityQueue(){
        this.head = null;
        this.tail = null;

    }
    public void enqueue(CriticalPatient patient){
        PNode new_node = new PNode(patient);
        if(head == null || patient.priority > head.patient.priority){
            new_node.next = head;
            head = new_node;
            length++;
        }
        else{

        PNode temp = head;
        while(temp.next != null && temp.next.patient.priority >= patient.priority){
            temp = temp.next;
        }
        new_node.next = temp.next;
        temp.next = new_node;
        length++;
    }
    }

    public CriticalPatient  dequeue(){
        if (isEmpty()){
            throw new IllegalArgumentException("Queue is empty");
        }
     CriticalPatient patient = head.patient; // Save reference to return
    head = head.next; // Move head to next node
    length--;
    return patient;
    }

    public int peek(){

        if (isEmpty()){
            throw new IllegalArgumentException("Queue is empty");
        }
        return head.patient.patientId;
    }

    public boolean isEmpty() {
        return head == null;
    }
    public static double getPoissonRandom(double lambda) {
        Random rand = new Random();
        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;
        do {
            k++;
            p *= rand.nextDouble();
        } while (p > L);
        return k - 1;
    }

    public static double getGaussianRandom(double mean, double stdDev) {
        Random rand = new Random();
        return mean + stdDev * rand.nextGaussian();
    }

    public void remove_first(){
        if (length<2){
            head =null;
            tail =null;
            length = 0;
        }
        else{
            head = head.next ; 
            length--;
        }



    }
   
    public CriticalPatient get(int index){
        PNode temp = head ;
        if (index < length && index >= 0 ){
         for (int i = 0 ; i < index ; i++){
             temp = temp.next ;
         }
        }
        return temp.patient ;
        
    }


    

}