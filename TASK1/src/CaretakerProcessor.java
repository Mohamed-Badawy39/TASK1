import java.util.ArrayList;
import java.util.List;

// This class simulates a hospital caretaker processing patients from a queue
public class CaretakerProcessor implements Runnable {
    // The queue of patients this caretaker handles
    private final AdaptiveQueue patientQueue;
    // A list to store log messages about patient processing
    private final List<String> logMessages = new ArrayList<>();

    // Constructor to assign a queue to this caretaker
    public CaretakerProcessor(AdaptiveQueue patientQueue) {
        this.patientQueue = patientQueue;
    }

    // Main logic for processing patients, runs in a separate thread
    @Override
    public void run() {
        // Start the clock at 8:00 AM (480 minutes past midnight)
        double currentTime = 480;
        // Log that this caretaker is starting work
        logMessages.add("Starting Caretaker #" + patientQueue.getQueueId());

        // Process patients until the queue is empty
        while (!patientQueue.isEmpty()) {
            // Get the next patient, prioritizing critical cases
            CriticalPatient patient = patientQueue.dequeue();

            // If the patient arrived after the current time, fast-forward to their arrival
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }

            // Calculate how long the patient waited (zero if treated immediately)
            double waitTime = Math.max(0, currentTime - patient.getArrivalTime());
            patient.setWaitingTime(waitTime);

            // Add the time it takes to treat this patient
            currentTime += patient.getServiceTime();
            // Record when the patient is done
            patient.setDepartureTime(currentTime);

            // Log details about this patient's treatment
            logMessages.add("Caretaker #" + patientQueue.getQueueId() +
                    " | Patient " + patient.getPatientId() +
                    " | Priority: " + getPriorityName(patient.getPriority()) +
                    " | Arrival: " + formatTime(patient.getArrivalTime()) +
                    " | Waiting: " + String.format("%.2f", patient.getWaitingTime()) + " min" +
                    " | Service: " + String.format("%.2f", patient.getServiceTime()) + " min" +
                    " | Departure: " + formatTime(patient.getDepartureTime()));
        }

        // Log that this caretaker has finished all patients
        logMessages.add("Caretaker #" + patientQueue.getQueueId() + " finished processing all patients.");
    }

    // Convert time in minutes to a readable HH:MM format
    private static String formatTime(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        return String.format("%02d:%02d", hours, mins);
    }

    // Translate priority number to a human-readable name
    private static String getPriorityName(int level) {
        switch (level) {
            case 1:
                return "Critical";
            default:
                return "Normal";
        }
    }

    // Return the list of log messages for review
    public List<String> getLogs() {
        return logMessages;
    }

    // Get the ID of the queue this caretaker is handling
    public int getQueueId() {
        return patientQueue.getQueueId();
    }
}