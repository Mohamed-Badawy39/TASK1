public class MultiPriorityQueue {
    private final PriorityQueue[] queues;

    public MultiPriorityQueue() {
        queues = new PriorityQueue[3]; // Three priority levels: 0 = Normal, 1 = Critical, 2 = Emergency
        for (int i = 0; i < 3; i++) {
            queues[i] = new PriorityQueue();
        }
    }

    // Enqueue the patient based on priority (no dependency on other queues)
    public synchronized void enqueue(CriticalPatient patient) {
        queues[patient.getPriority()].enqueue(patient);
    }

    // Dequeue the next patient from a specific priority queue
    public synchronized CriticalPatient dequeueFromQueue(int priority) {
        if (!queues[priority].isEmpty()) {
            return queues[priority].dequeue();
        }
        return null; // Return null if the queue is empty
    }

    // Dequeue the next patient from the queue with the earliest arrival time (highest priority)
    public synchronized CriticalPatient dequeue() {
        // Dequeue from the highest priority queue (Emergency > Critical > Normal)
        for (int i = 2; i >= 0; i--) {
            if (!queues[i].isEmpty()) {
                return queues[i].dequeue();
            }
        }
        return null; // All queues are empty
    }

    // Check if all queues are empty
    public synchronized boolean isEmpty() {
        for (PriorityQueue queue : queues) {
            if (!queue.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Return the queue for a specific priority level
    public PriorityQueue getQueue(int priority) {
        return queues[priority];
    }

    // Get the size of the queue for a specific priority level
    public int getQueueSize(int priority) {
        return queues[priority].size();
    }
}
