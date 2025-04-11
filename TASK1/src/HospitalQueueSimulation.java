import java.util.Random;

public class HospitalQueueSimulation {
    public static void main(String[] args) {
        PriorityQueue queue = new PriorityQueue();
        Random rand = new Random();
        int numPatients = 10;

        double currentArrivalTime = 480 + PriorityQueue.getPoissonRandom(5);
        double avgServiceTime = 10; 
        
        for (int i = 0; i < numPatients; i++) {
            CriticalPatient patient = new CriticalPatient(i + 1);
            queue.enqueue(patient);
            
            queue.get(i).setArrivalTime(currentArrivalTime);
            double serviceTime = PriorityQueue.getGaussianRandom(10, 2);
            queue.get(i).setServiceTime(serviceTime);
            queue.get(i).priority = rand.nextInt(2); // 0 = Normal, 1 = Critical

            double interval = 5 + rand.nextDouble() * (avgServiceTime * 0.7);
            currentArrivalTime += interval;
        }

       
        double currentTime = queue.get(0).getArrivalTime();
        
        System.out.println("Hospital Queue Simulation");
        System.out.println("========================\n");

        PriorityQueue normalPatients = new PriorityQueue();
        PriorityQueue criticalPatients = new PriorityQueue();

        while (!queue.isEmpty()) {
            CriticalPatient patient = queue.dequeue();
            
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }

            if (patient.priority == 1) {
                double waitTime = Math.max(0, currentTime - patient.getArrivalTime());
                patient.setWaitingTime(waitTime);
                criticalPatients.enqueue(patient);
            } else {
                double waitTime = currentTime - patient.getArrivalTime();
                patient.setWaitingTime(Math.max(waitTime, 5));
                normalPatients.enqueue(patient);
            }

            currentTime += patient.getServiceTime();
            patient.setDepartureTime(currentTime);

            System.out.println("\nPatient " + patient.patientId);
            System.out.println("Arrival Time: " + formatTime(patient.getArrivalTime()));
            System.out.println("Waiting Time: " + String.format("%.2f", patient.getWaitingTime()) + " minutes");
            System.out.println("Service Time: " + String.format("%.2f", patient.getServiceTime()) + " minutes");
            System.out.println("Departure Time: " + formatTime(patient.getDepartureTime()));
            System.out.println("Priority: " + (patient.priority == 1 ? "Critical" : "Normal"));
        }

       
        System.out.println("\nWaiting Time Summary");
        System.out.println("===================");
        printQueueStatistics("Critical Patients", criticalPatients);
        printQueueStatistics("Normal Patients", normalPatients);
    }

    private static void printQueueStatistics(String patientType, PriorityQueue queue) {
        double totalWait = 0;
        int count = 0;
        double maxWait = 0;
        double minWait = Double.MAX_VALUE;

        while (!queue.isEmpty()) {
            CriticalPatient patient = queue.dequeue();
            double waitTime = patient.getWaitingTime();
            totalWait += waitTime;
            count++;
            maxWait = Math.max(maxWait, waitTime);
            minWait = Math.min(minWait, waitTime);
        }

        if (count > 0) {
            System.out.printf("\n%s Statistics:", patientType);
            System.out.printf("\nAverage Wait: %.2f minutes", totalWait / count);
            System.out.printf("\nMinimum Wait: %.2f minutes", minWait);
            System.out.printf("\nMaximum Wait: %.2f minutes\n", maxWait);
        }
    }

    private static String formatTime(double minutes) {
        int hours = (int) (minutes / 60);
        int mins = (int) (minutes % 60);
        return String.format("%02d:%02d", hours, mins);
    }
}