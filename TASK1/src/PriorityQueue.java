import java.util.Random;

public class PriorityQueue {
    private PNode head;
    private int size;

    public PriorityQueue() {
        head = null;
        size = 0;
    }

    // Enqueue patient at the tail (FIFO)
    public void enqueue(CriticalPatient patient) {
        PNode newNode = new PNode(patient);
        if (head == null) {
            head = newNode;
        } else {
            PNode current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    // Dequeue the patient at the head
    public CriticalPatient dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        CriticalPatient patient = head.patient;
        head = head.next;
        size--;
        return patient;
    }

    // Peek at the patient at the head without removing
    public CriticalPatient peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Queue is empty");
        }
        return head.patient;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int size() {
        return size;
    }

}
