// EmergencyPatient.java
public class EmergencyPatient extends CriticalPatient {

    public EmergencyPatient(int patientId) {
        super(patientId);
    }

    // Convenience method to allow getId() calls; delegates to getPatientId()
    public int getId() {
        return getPatientId();
    }

    // Set a faster service time for emergency patients.
    public void setFasterServiceTime() {
        double serviceTime = HospitalQueueSimulation.getGaussianRandom(5, 1);
        setServiceTime(serviceTime);
    }
}
