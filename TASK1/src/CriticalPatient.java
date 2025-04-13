public class CriticalPatient {
    private int patientId;
    private double arrivalTime;
    private double serviceTime;
    private double waitingTime;
    private double departureTime;
    private int priority;
    private int assignedQueueId;


    public CriticalPatient(int patientId) {
        this.patientId = patientId;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public double getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }

    public double getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(double waitingTime) {
        this.waitingTime = waitingTime;
    }

    public double getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(double departureTime) {
        this.departureTime = departureTime;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getAssignedQueueId() {
        return assignedQueueId;
    }

    public void setAssignedQueueId(int assignedQueueId) {
        this.assignedQueueId = assignedQueueId;
    }
}