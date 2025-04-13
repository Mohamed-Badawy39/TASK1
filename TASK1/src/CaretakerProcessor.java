import java.util.ArrayList;
import java.util.List;

public class CaretakerProcessor implements Runnable {
    private final AdaptiveQueue queue;
    private final List<String> logMessages = new ArrayList<>();

    public CaretakerProcessor(AdaptiveQueue queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        double currentTime = 480;
        logMessages.add("Starting Caretaker #" + queue.getQueueId());
        while (!queue.isEmpty()) {
            CriticalPatient patient = queue.dequeue();
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }
            double waitTime = Math.max(0, currentTime - patient.getArrivalTime());
            patient.setWaitingTime(waitTime);
            currentTime += patient.getServiceTime();
            patient.setDepartureTime(currentTime);
            logMessages.add("Caretaker #" + queue.getQueueId() +
                    " | Patient " + patient.getPatientId() +
                    " | Priority: " + getPriorityName(patient.getPriority()) +
                    " | Arrival: " + formatTime(patient.getArrivalTime()) +
                    " | Waiting: " + String.format("%.2f", patient.getWaitingTime()) + " min" +
                    " | Service: " + String.format("%.2f", patient.getServiceTime()) + " min" +
                    " | Departure: " + formatTime(patient.getDepartureTime()));
        }
        logMessages.add("Caretaker #" + queue.getQueueId() + " finished processing all patients.");
    }

    private static String formatTime(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        return String.format("%02d:%02d", hours, mins);
    }

    private static String getPriorityName(int level) {
        switch (level) {
            case 1:
                return "Critical";
            default:
                return "Normal";
        }
    }

    public List<String> getLogs() {
        return logMessages;
    }

    public int getQueueId() {
        return queue.getQueueId();
    }
}
