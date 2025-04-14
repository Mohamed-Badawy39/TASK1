// This class represents a patient in a hospital, with details about their treatment and priority
public class CriticalPatient {
    // Unique ID to identify the patient
    private int patientId;
    // When the patient arrives at the hospital (in minutes past midnight)
    private double arrivalTime;
    // How long it takes to treat the patient (in minutes)
    private double serviceTime;
    // How long the patient waits before treatment starts (in minutes)
    private double waitingTime;
    // When the patient finishes treatment and leaves (in minutes past midnight)
    private double departureTime;
    // Priority level (1 for critical, higher numbers for less urgent)
    private int priority;
    // ID of the queue this patient is assigned to
    private int assignedQueueId;

    // Constructor to create a patient with a given ID
    public CriticalPatient(int patientId) {
        this.patientId = patientId;
    }

    // Get the patient's unique ID
    public int getPatientId() {
        return patientId;
    }

    // Get the time the patient arrived
    public double getArrivalTime() {
        return arrivalTime;
    }

    // Set the time the patient arrived
    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    // Get how long it takes to treat the patient
    public double getServiceTime() {
        return serviceTime;
    }

    // Set how long it takes to treat the patient
    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    // Get how long the patient waited before treatment
    public double getWaitingTime() {
        return waitingTime;
    }

    // Set how long the patient waited before treatment
    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    // Get the time the patient finished treatment
    public double getDepartureTime() {
        return departureTime;
    }

    // Set the time the patient finished treatment
    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }

    // Get the patient's priority level (1 is critical)
    public int getPriority() {
        return priority;
    }

    // Set the patient's priority level
    public void setPriority(int priority) {
        this.priority = priority;
    }

    // Get the ID of the queue this patient is in
    public int getAssignedQueueId() {
        return assignedQueueId;
    }

    // Set the ID of the queue this patient is assigned to
    public void setAssignedQueueId(int assignedQueueId) {
        this.assignedQueueId = assignedQueueId;
    }
}