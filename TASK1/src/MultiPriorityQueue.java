// This class manages multiple hospital queues for patients based on their urgency
public class MultiPriorityQueue {
    // Array of queues for different priority levels: 0 = Normal, 1 = Critical, 2 = Emergency
    private final PriorityQueue[] patientQueues;

    // Constructor to set up the three priority queues
    public MultiPriorityQueue() {
        patientQueues = new PriorityQueue[3]; // One queue for each priority level
        for (int i = 0; i < 3; i++) {
            patientQueues[i] = new PriorityQueue(); // Initialize each queue
        }
    }

    // Add a patient to the right queue based on their priority
    public synchronized void enqueue(CriticalPatient patient) {
        // Put the patient in the queue matching their priority (0, 1, or 2)
        patientQueues[patient.getPriority()].enqueue(patient);
    }

    // Get the next patient from a specific priority queue
    public synchronized CriticalPatient dequeueFromQueue(int priority) {
        // Check if the chosen queue has patients
        if (!patientQueues[priority].isEmpty()) {
            // Return the next patient from that queue
            return patientQueues[priority].dequeue();
        }
        // If the queue is empty, return null
        return null;
    }

    // Get the next patient, starting with the highest priority queue
    public synchronized CriticalPatient dequeue() {
        // Check queues from highest to lowest priority: Emergency (2), Critical (1), Normal (0)
        for (int i = 2; i >= 0; i--) {
            if (!patientQueues[i].isEmpty()) {
                // Return the next patient from the first non-empty queue
                return patientQueues[i].dequeue();
            }
        }
        // If all queues are empty, return null
        return null;
    }

    // Check if all priority queues are empty
    public synchronized boolean isEmpty() {
        // Look at each queue
        for (PriorityQueue queue : patientQueues) {
            // If any queue has patients, return false
            if (!queue.isEmpty()) {
                return false;
            }
        }
        // All queues are empty
        return true;
    }

    // Get the queue for a specific priority level
    public PriorityQueue getQueue(int priority) {
        return patientQueues[priority];
    }

    // Get the number of patients in a specific priority queue
    public int getQueueSize(int priority) {
        return patientQueues[priority].size();
    }
}