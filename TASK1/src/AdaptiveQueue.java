import java.util.LinkedList;
import java.util.Queue;

public class AdaptiveQueue {
    private final Queue<CriticalPatient> patients;
    private final int queueId;

    public AdaptiveQueue(int queueId) {
        this.queueId = queueId;
        this.patients = new LinkedList<>();
    }

    public void enqueue(CriticalPatient patient) {
        patients.add(patient);
    }

    public CriticalPatient dequeue() {
        // First try to dequeue critical patients
        for (CriticalPatient patient : patients) {
            if (patient.getPriority() == 1) { // Critical
                patients.remove(patient);
                return patient;
            }
        }


        return patients.poll();
    }

    public boolean isEmpty() {
        return patients.isEmpty();
    }

    public int size() {
        return patients.size();
    }

    public int getQueueId() {
        return queueId;
    }


    public boolean hasCriticalPatients() {
        for (CriticalPatient patient : patients) {
            if (patient.getPriority() == 1) {
                return true;
            }
        }
        return false;
    }

    // Count critical patients in the queue
    public int countCriticalPatients() {
        int count = 0;
        for (CriticalPatient patient : patients) {
            if (patient.getPriority() == 1) {
                count++;
            }
        }
        return count;
    }
}