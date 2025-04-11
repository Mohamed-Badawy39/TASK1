import java.util.ArrayList;
import java.util.List;

class QueueProcessor implements Runnable {
    private MultiPriorityQueue multiQueue;
    private int priority;
    private List<CriticalPatient> allPatients;

    public QueueProcessor(MultiPriorityQueue multiQueue, int priority, List<CriticalPatient> allPatients) {
        this.multiQueue = multiQueue;
        this.priority = priority;
        this.allPatients = allPatients;
    }

    @Override
    public void run() {
        double currentTime = 480; // Start time
        System.out.println("Processing " + getPriorityName(priority) + " Queue");

        // Filter patients by the current priority level and sort by arrival time
        List<CriticalPatient> patients = new ArrayList<>();
        for (CriticalPatient patient : allPatients) {
            if (patient.getPriority() == priority) {
                patients.add(patient);
            }
        }

        // Process each patient
        for (CriticalPatient patient : patients) {
            // Wait for the patient to arrive if necessary
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }

            // Calculate waiting time and update departure time
            double waitTime = Math.max(0, currentTime - patient.getArrivalTime());
            patient.setWaitingTime(waitTime);
            currentTime += patient.getServiceTime();
            patient.setDepartureTime(currentTime);

            // Print patient details
            System.out.println("Patient " + patient.getPatientId() +
                    " | Priority: " + getPriorityName(patient.getPriority()) +
                    " | Arrival: " + formatTime(patient.getArrivalTime()) +
                    " | Waiting: " + String.format("%.2f", patient.getWaitingTime()) + " min" +
                    " | Service: " + String.format("%.2f", patient.getServiceTime()) + " min" +
                    " | Departure: " + formatTime(patient.getDepartureTime()));
        }
    }

    private static String formatTime(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        return String.format("%02d:%02d", hours, mins);
    }

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
