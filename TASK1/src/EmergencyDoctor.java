// EmergencyDoctor.java
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class EmergencyDoctor implements Runnable {
    final int doctorId;  // default access for manager usage
    private final BlockingQueue<EmergencyPatient> queue;
    private volatile boolean running = true;

    public EmergencyDoctor(int doctorId) {
        this.doctorId = doctorId;
        // Capacity of 1 to simulate a single-patient assignment.
        this.queue = new LinkedBlockingQueue<>(1);
    }

    public boolean isAvailable() {
        return queue.isEmpty();
    }

    // Non-blocking attempt to assign a patient.
    public boolean tryAssignPatient(EmergencyPatient patient) {
        return queue.offer(patient);
    }

    @Override
    public void run() {
        while (running) {
            try {
                EmergencyPatient patient = queue.take();
                // Convert simulation time (in minutes) to HH:mm format.
                int totalMinutes = (int) Math.round(patient.getArrivalTime());
                int hour = totalMinutes / 60;
                int minute = totalMinutes % 60;
                System.out.printf("[Doctor %d] Started processing emergency patient %d at time %02d:%02d%n",
                        doctorId, patient.getId(), hour, minute);

                // Simulate processing time (faster for emergency patients)
                Thread.sleep((long) (patient.getServiceTime() * 100));
                System.out.printf("[Doctor %d] Finished processing emergency patient %d%n",
                        doctorId, patient.getId());
            } catch (InterruptedException e) {
                running = false;
            }
        }
    }


    public void shutdown() {
        running = false;
    }
}
