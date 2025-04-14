// This class represents an emergency patient who needs faster treatment
public class EmergencyPatient extends CriticalPatient {

    // Constructor to create an emergency patient with a given ID
    public EmergencyPatient(int patientId) {
        super(patientId); // Uses the CriticalPatient setup
    }

    // Shortcut to get the patient's ID, same as getPatientId()
    public int getId() {
        return getPatientId();
    }

    // Set a shorter treatment time for this emergency patient
    public void setFasterServiceTime() {
        // Use a random treatment time (average 5 minutes, varies slightly)
        double serviceTime = HospitalQueueSimulation.getGaussianRandom(5, 1);
        setServiceTime(serviceTime);
    }
}