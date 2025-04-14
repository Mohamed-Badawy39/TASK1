import java.util.ArrayList;
import java.util.List;

// This class coordinates a team of emergency doctors, assigning patients and managing their work
public class EmergencyDoctorManager {
    // List of emergency doctors handling patients
    private final List<EmergencyDoctor> doctorTeam = new ArrayList<>();
    // List of threads running each doctor's work
    private final List<Thread> doctorThreads = new ArrayList<>();

    // Constructor to set up the team of doctors
    public EmergencyDoctorManager() {
        // Create 5 emergency doctors, like rooms 1 to 5 in an ER
        for (int i = 1; i <= 5; i++) {
            EmergencyDoctor doctor = new EmergencyDoctor(i);
            doctorTeam.add(doctor);
            // Start a thread for each doctor to handle their patients
            Thread thread = new Thread(doctor, "Emergency-Doctor-" + i);
            doctorThreads.add(thread);
            thread.start();
        }
    }

    // Assign a patient to an available doctor, checking rooms 1 to 5
    public void assignPatient(EmergencyPatient patient) {
        boolean isAssigned = false;
        // Keep trying until the patient is assigned to a doctor
        while (!isAssigned) {
            for (EmergencyDoctor doctor : doctorTeam) {
                // If the doctor is free and accepts the patient, assign them
                if (doctor.isAvailable() && doctor.tryAssignPatient(patient)) {
                    isAssigned = true;
                    System.out.printf("Emergency patient %d assigned to Doctor %d%n",
                            patient.getId(), doctor.doctorId);
                    break;
                }
            }
            // If no doctor was free, wait briefly and try again
            if (!isAssigned) {
                try {
                    Thread.sleep(10); // Wait 10 milliseconds
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Shut down all doctors' work gracefully
    public void shutdown() {
        // Tell each doctor to stop taking new patients
        for (EmergencyDoctor doctor : doctorTeam) {
            doctor.shutdown();
        }
        // Interrupt each doctor's thread to end their work
        for (Thread thread : doctorThreads) {
            thread.interrupt();
        }
    }
}