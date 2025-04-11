import java.util.*;
import java.util.concurrent.*;

public class HospitalQueueSimulation {
    public static void main(String[] args) throws InterruptedException {
        MultiPriorityQueue multiQueue = new MultiPriorityQueue();
        Random rand = new Random();
        int numPatients = 100;

        double currentArrivalTime = 480;  // Starting time at 8:00 AM (in minutes)
        double avgServiceTime = 10;

        // Enqueue patients with random priority and service time
        List<CriticalPatient> allPatients = new ArrayList<>();
        for (int i = 0; i < numPatients; i++) {
            CriticalPatient patient = new CriticalPatient(i + 1);

            patient.setArrivalTime(currentArrivalTime);

            // Simulate more realistic arrival times (closer to each other)
            double interval = 1 + rand.nextDouble() * 5;  // 1 to 5 minutes interval
            currentArrivalTime += interval;

            // Randomly determine service time
            double serviceTime = PriorityQueue.getGaussianRandom(10, 2);
            patient.setServiceTime(serviceTime);

            // Assign priority based on percentage probability
            patient.setPriority(assignPriority(rand));

            multiQueue.enqueue(patient);  // Enqueue into the correct priority queue
            allPatients.add(patient);     // Collect all patients for later sorting
        }

        // Sort all patients by arrival time (ascending)
        allPatients.sort(Comparator.comparingDouble(CriticalPatient::getArrivalTime));

        // Executor for managing threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Create and submit tasks for each queue (Normal, Critical, Emergency)
        executor.submit(new QueueProcessor(multiQueue, 0, allPatients)); // Normal queue (priority 0)
        executor.submit(new QueueProcessor(multiQueue, 1, allPatients)); // Critical queue (priority 1)
        executor.submit(new QueueProcessor(multiQueue, 2, allPatients)); // Emergency queue (priority 2)

        // Shut down the executor service
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        // After all processing is done, print the statistics at the end
        printFinalStatistics(allPatients);
    }

    // Method to print final summary statistics
    private static void printFinalStatistics(List<CriticalPatient> allPatients) {
        // Group patients by priority for summary
        Map<Integer, List<CriticalPatient>> groupedByPriority = new HashMap<>();
        for (CriticalPatient p : allPatients) {
            groupedByPriority
                    .computeIfAbsent(p.getPriority(), k -> new ArrayList<>())
                    .add(p);
        }

        for (int priority = 0; priority < 3; priority++) {
            List<CriticalPatient> patients = groupedByPriority.get(priority);
            if (patients != null && !patients.isEmpty()) {
                printQueueStatistics(getPriorityName(priority), patients);
            }
        }
    }

    private static void printQueueStatistics(String label, List<CriticalPatient> patients) {
        double totalWait = 0;
        int count = patients.size();
        double maxWait = 0;
        double minWait = Double.MAX_VALUE;

        for (CriticalPatient p : patients) {
            double wt = p.getWaitingTime();
            totalWait += wt;
            maxWait = Math.max(maxWait, wt);
            minWait = Math.min(minWait, wt);
        }

        if (count > 0) {
            System.out.printf("\n%s Patients (%d):\n", label, count);
            System.out.printf("Average Wait: %.2f min | Min: %.2f min | Max: %.2f min\n",
                    totalWait / count, minWait, maxWait);
        }
    }

    private static String getPriorityName(int level) {
        switch (level) {
            case 2: return "Emergency";
            case 1: return "Critical";
            default: return "Normal";
        }
    }

    // Method to assign priority based on percentage chance
    private static int assignPriority(Random rand) {
        int randValue = rand.nextInt(100); // Generate a random number between 0 and 99

        // Assign priorities based on defined probability ranges
        if (randValue < 50) {
            return 0; // 70% chance for Normal
        } else if (randValue < 80) {
            return 1; // 20% chance for Critical
        } else {
            return 2; // 10% chance for Emergency
        }
    }
}

