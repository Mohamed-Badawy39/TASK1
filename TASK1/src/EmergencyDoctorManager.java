// EmergencyDoctorManager.java
import java.util.ArrayList;
import java.util.List;

public class EmergencyDoctorManager {
    private final List<EmergencyDoctor> doctors = new ArrayList<>();
    private final List<Thread> doctorThreads = new ArrayList<>();

    public EmergencyDoctorManager() {
        // Create 5 doctors (rooms) numbered 1 to 5.
        for (int i = 1; i <= 5; i++) {
            EmergencyDoctor doctor = new EmergencyDoctor(i);
            doctors.add(doctor);
            Thread thread = new Thread(doctor, "Emergency-Doctor-" + i);
            doctorThreads.add(thread);
            thread.start();
        }
    }

    // Try to assign a patient to an available doctor in order (rooms 1 to 5).
    public void assignPatient(EmergencyPatient patient) {
        boolean assigned = false;
        while (!assigned) {
            for (EmergencyDoctor doctor : doctors) {
                if (doctor.isAvailable() && doctor.tryAssignPatient(patient)) {
                    assigned = true;
                    System.out.printf("Emergency patient %d assigned to Doctor %d%n",
                            patient.getId(), doctor.doctorId);
                    break;
                }
            }
            // Wait a bit before retrying if no doctor was available.
            if (!assigned) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Shutdown all doctor threads gracefully.
    public void shutdown() {
        for (EmergencyDoctor doctor : doctors) {
            doctor.shutdown();
        }
        for (Thread t : doctorThreads) {
            t.interrupt();
        }
    }
}
