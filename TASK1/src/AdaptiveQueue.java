import java.util.LinkedList;
import java.util.Queue;

// This class manages a queue of patients in a hospital, prioritizing critical cases
public class AdaptiveQueue {
    // A list to hold patients waiting for treatment
    private final Queue<CriticalPatient> patientQueue;
    // A unique ID to identify this queue
    private final int queueId;

    // Constructor to set up a new queue with a given ID
    public AdaptiveQueue(int queueId) {
        this.queueId = queueId;
        this.patientQueue = new LinkedList<>();
    }

    // Add a patient to the end of the queue
    public void enqueue(CriticalPatient patient) {
        patientQueue.add(patient);
    }

    // Remove and return the next patient, giving priority to critical cases
    public CriticalPatient dequeue() {
        // Check for critical patients first (priority level 1)
        for (CriticalPatient patient : patientQueue) {
            if (patient.getPriority() == 1) { // 1 means critical
                patientQueue.remove(patient);
                return patient;
            }
        }
        // If no critical patients, return the next patient in line
        return patientQueue.poll();
    }

    // Check if the queue has no patients
    public boolean isEmpty() {
        return patientQueue.isEmpty();
    }

    // Return the number of patients in the queue
    public int size() {
        return patientQueue.size();
    }

    // Get the ID of this queue
    public int getQueueId() {
        return queueId;
    }

    // Check if there are any critical patients in the queue
    public boolean hasCriticalPatients() {
        for (CriticalPatient patient : patientQueue) {
            if (patient.getPriority() == 1) {
                return true;
            }
        }
        return false;
    }

    // Count how many critical patients are in the queue
    public int countCriticalPatients() {
        int criticalCount = 0;
        for (CriticalPatient patient : patientQueue) {
            if (patient.getPriority() == 1) {
                criticalCount++;
            }
        }
        return criticalCount;
    }
}