import java.util.*;
import java.util.concurrent.*;

public class HospitalQueueSimulation {
    private static final int QUEUE_THRESHOLD = 25; // Threshold to open a new caretaker

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();
        int numPatients = 100;
        double currentArrivalTime = 480;  // Starting time at 8:00 AM (in minutes)

        // Create a list of adaptive queues (these will be opened dynamically)
        List<AdaptiveQueue> caretakerQueues = new ArrayList<>();
        // Start with one initial queue
        caretakerQueues.add(new AdaptiveQueue(1));

        // Create all patients first
        List<CriticalPatient> allPatients = new ArrayList<>();
        for (int i = 0; i < numPatients; i++) {
            CriticalPatient patient = new CriticalPatient(i + 1);

            patient.setArrivalTime(currentArrivalTime);

            // Simulate more realistic arrival times (closer to each other)
            double interval = 1 + rand.nextDouble() * 5;  // 1 to 5 minutes interval
            currentArrivalTime += interval;

            // Randomly determine service time
            double serviceTime = getGaussianRandom(10, 2);
            patient.setServiceTime(serviceTime);

            // Assign priority based on percentage probability
            patient.setPriority(assignPriority(rand));

            allPatients.add(patient);
        }

        // Sort all patients by arrival time (ascending)
        allPatients.sort(Comparator.comparingDouble(CriticalPatient::getArrivalTime));

        // Assign each patient to a queue based on threshold logic
        for (CriticalPatient patient : allPatients) {
            // Check if we need to open a new queue before assigning
            boolean needNewQueue = true;
            for (AdaptiveQueue queue : caretakerQueues) {
                if (queue.size() < QUEUE_THRESHOLD) {
                    needNewQueue = false;
                    break;
                }
            }

            // Only open a new queue if all existing queues are at threshold
            if (needNewQueue) {
                caretakerQueues.add(new AdaptiveQueue(caretakerQueues.size() + 1));
            }

            // Find the best queue to assign this patient to
            AdaptiveQueue targetQueue = findBestQueue(caretakerQueues);
            targetQueue.enqueue(patient);
        }

        // Process all queues
        ExecutorService executor = Executors.newFixedThreadPool(caretakerQueues.size());
        for (AdaptiveQueue queue : caretakerQueues) {
            executor.submit(new CaretakerProcessor(queue));
        }

        // Shut down the executor service
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        // After all processing is done, print the statistics
        System.out.println("\n======= SIMULATION SUMMARY =======");
        System.out.println("Total caretakers opened: " + caretakerQueues.size());

        // Print statistics by priority across all queues
        printFinalStatistics(allPatients);
    }

    // Find the best queue to add a patient to (shortest queue)
    private static AdaptiveQueue findBestQueue(List<AdaptiveQueue> queues) {
        if (queues.isEmpty()) {
            throw new IllegalStateException("No queues available");
        }

        // Find the queue with the fewest patients
        AdaptiveQueue shortestQueue = queues.get(0);
        int minSize = shortestQueue.size();

        for (AdaptiveQueue queue : queues) {
            if (queue.size() < minSize) {
                shortestQueue = queue;
                minSize = queue.size();
            }
        }

        return shortestQueue;
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

        for (int priority = 0; priority < 2; priority++) {
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
            case 1: return "Critical";
            default: return "Normal";
        }
    }

    // Method to assign priority based on percentage chance (now only Normal and Critical)
    private static int assignPriority(Random rand) {
        int randValue = rand.nextInt(100); // Generate a random number between 0 and 99

        if (randValue < 70) {
            return 0; // 70% chance for Normal
        } else {
            return 1; // 30% chance for Critical
        }
    }

    // Gaussian (normal) random number generator (moved from PriorityQueue)
    public static double getGaussianRandom(double mean, double stdDev) {
        Random rand = new Random();
        return mean + stdDev * rand.nextGaussian();
    }
}