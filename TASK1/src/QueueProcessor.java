import java.util.ArrayList;
import java.util.List;

// This class simulates a hospital worker processing patients from one priority queue
class QueueProcessor implements Runnable {
    // The multi-queue system holding patients by priority
    private MultiPriorityQueue patientQueueSystem;
    // The priority level this processor handles (0=Normal, 1=Critical, 2=Emergency)
    private int priorityLevel;
    // List of all patients to filter by priority
    private List<CriticalPatient> allPatients;

    // Constructor to set up the processor with a queue, priority, and patient list
    public QueueProcessor(MultiPriorityQueue patientQueueSystem, int priorityLevel, List<CriticalPatient> allPatients) {
        this.patientQueueSystem = patientQueueSystem;
        this.priorityLevel = priorityLevel;
        this.allPatients = allPatients;
    }

    // Main logic for processing patients in this priority level
    @Override
    public void run() {
        // Start the shift at 8:00 AM (480 minutes past midnight)
        double currentTime = 480;
        // Announce which priority queue is being handled
        System.out.println("Processing " + getPriorityName(priorityLevel) + " Queue");

        // Create a list of patients matching this priority level
        List<CriticalPatient> patientsToProcess = new ArrayList<>();
        for (CriticalPatient patient : allPatients) {
            if (patient.getPriority() == priorityLevel) {
                patientsToProcess.add(patient);
            }
        }

        // Treat each patient in order
        for (CriticalPatient patient : patientsToProcess) {
            // Wait for the patient to arrive if they’re not here yet
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }

            // Calculate how long the patient waited before treatment
            double waitTime = Math.max(0, currentTime - patient.getArrivalTime());
            patient.setWaitingTime(waitTime);

            // Add the treatment time and update when the patient leaves
            currentTime += patient.getServiceTime();
            patient.setDepartureTime(currentTime);

            // Log the patient’s treatment details
            System.out.println("Patient " + patient.getPatientId() +
                    " | Priority: " + getPriorityName(patient.getPriority()) +
                    " | Arrival: " + formatTime(patient.getArrivalTime()) +
                    " | Waiting: " + String.format("%.2f", patient.getWaitingTime()) + " min" +
                    " | Service: " + String.format("%.2f", patient.getServiceTime()) + " min" +
                    " | Departure: " + formatTime(patient.getDepartureTime()));
        }
    }

    // Convert time in minutes to a readable HH:MM format
    private static String formatTime(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        return String.format("%02d:%02d", hours, mins);
    }

    // Translate priority numbers to human-readable names
    private static String getPriorityName(int level) {
        switch (level) {
            case 2:
                return "Emergency";
            case 1:
                return "Critical";
            default:
                return "Normal";
        }
    }
}