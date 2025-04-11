import java.util.Random;


public class Queue {
    private double arrivalTime;
    private int patientId;
    private Node head;
    private Node tail;

    public Queue() {
        this.head = null;
        this.tail = null;
    }

    public Queue(int patientId, double arrivalTime) {
        this.patientId = patientId;
        this.arrivalTime = arrivalTime;
    }


    public boolean isEmpty() {
        return this.head == null;
    }
    public void enqueue(int item) {
        Node new_node = new Node(item);
        if (this.tail == null) {
            this.head = this.tail = new_node;
        }
        else {
            this.tail.next = new_node; 
             this.tail = new_node;
          
        }
        System.out.println(item + " added to the queue");
    }
    public void dequeue() {
        if (this.isEmpty()) {
            System.out.println("Queue is empty");
        }
        else {
            this.head = this.head.next;
        }
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

    public void displayInfo() {
        System.out.println("Patient ID: " + patientId + ", Arrival Time: " + arrivalTime + " minutes");
    }


}
