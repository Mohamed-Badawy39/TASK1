import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// This class simulates an emergency doctor handling one patient at a time
public class EmergencyDoctor implements Runnable {
    // Unique ID for this doctor, accessible to the hospital system
    final int doctorId;
    // A queue holding one patient at a time for this doctor to treat
    private final BlockingQueue<EmergencyPatient> patientQueue;
    // Flag to control whether the doctor is still working
    private volatile boolean isActive = true;

    // Constructor to set up a doctor with a given ID
    public EmergencyDoctor(int doctorId) {
        this.doctorId = doctorId;
        // Queue holds only one patient to mimic focused emergency care
        this.patientQueue = new LinkedBlockingQueue<>(1);
    }

    // Check if the doctor is free to take a new patient
    public boolean isAvailable() {
        return patientQueue.isEmpty();
    }

    // Try to assign a patient to this doctor (returns false if busy)
    public boolean tryAssignPatient(EmergencyPatient patient) {
        return patientQueue.offer(patient);
    }

    // Main loop where the doctor processes patients
    @Override
    public void run() {
        // Keep working while the doctor is active
        while (isActive) {
            try {
                // Wait for and get the next patient from the queue
                EmergencyPatient patient = patientQueue.take();

                // Convert arrival time (minutes since midnight) to HH:mm format
                int totalMinutes = (int) Math.round(patient.getArrivalTime());
                int hour = totalMinutes / 60;
                int minute = totalMinutes % 60;

                // Log when the doctor starts treating the patient
                System.out.printf("[Doctor %d] Started treating emergency patient %d at %02d:%02d%n",
                        doctorId, patient.getId(), hour, minute);

                // Simulate treatment time (scaled for urgency, 1 minute = 100ms)
                Thread.sleep((long) (patient.getServiceTime() * 100));

                // Log when the doctor finishes with the patient
                System.out.printf("[Doctor %d] Finished treating emergency patient %d%n",
                        doctorId, patient.getId());

            } catch (InterruptedException e) {
                // If interrupted, stop working
                isActive = false;
            }
        }
    }

    // Signal the doctor to stop working
    public void shutdown() {
        isActive = false;
    }
}