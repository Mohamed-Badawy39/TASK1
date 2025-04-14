import java.util.*;
import java.util.concurrent.*;

// This class simulates a hospital managing patients with caretakers and emergency doctors
public class HospitalQueueSimulation {
    // Thresholds to decide patient types (out of 100)
    private static final int QUEUE_THRESHOLD = 25; // Max patients per caretaker queue
    private static final int NORMAL_THRESHOLD = 80; // 80% chance for normal patients
    private static final int CRITICAL_THRESHOLD = 95; // 15% chance for critical (80-95)

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();
        int numPatients = 100; // Simulate 100 patients
        double currentArrivalTime = 480; // Start at 8:00 AM (480 minutes)
        List<AdaptiveQueue> caretakerQueues = new ArrayList<>();
        caretakerQueues.add(new AdaptiveQueue(1)); // Start with one caretaker queue
        List<EmergencyPatient> emergencyPatients = new ArrayList<>();
        List<CriticalPatient> allPatients = new ArrayList<>();

        // Create patients with random arrival times and types
        for (int i = 0; i < numPatients; i++) {
            int patientType = assignPatientType(rand);
            if (patientType == 2) { // Emergency patient
                EmergencyPatient patient = new EmergencyPatient(i + 1);
                patient.setArrivalTime(currentArrivalTime);
                patient.setFasterServiceTime(); // Short treatment time (~5 min)
                patient.setPriority(2);
                emergencyPatients.add(patient);
                allPatients.add(patient);
            } else { // Normal or critical patient
                CriticalPatient patient = new CriticalPatient(i + 1);
                patient.setArrivalTime(currentArrivalTime);
                double serviceTime = getGaussianRandom(10, 2); // ~10 min treatment
                patient.setServiceTime(serviceTime);
                patient.setPriority(patientType);
                allPatients.add(patient);
            }
            // Space out arrivals randomly (1-6 minutes apart)
            double interval = 1 + rand.nextDouble() * 5;
            currentArrivalTime += interval;
        }

        // Sort patients by arrival time to process in order
        allPatients.sort(Comparator.comparingDouble(CriticalPatient::getArrivalTime));

        // Assign non-emergency patients to caretaker queues
        for (CriticalPatient patient : allPatients) {
            if (patient.getPriority() == 2) { // Skip emergency patients
                continue;
            }
            // Check if existing queues are too full
            boolean needNewQueue = true;
            for (AdaptiveQueue queue : caretakerQueues) {
                if (queue.size() < QUEUE_THRESHOLD) {
                    needNewQueue = false;
                    break;
                }
            }
            // Open a new queue if all are at capacity
            if (needNewQueue) {
                caretakerQueues.add(new AdaptiveQueue(caretakerQueues.size() + 1));
            }
            // Put the patient in the shortest queue
            AdaptiveQueue targetQueue = findBestQueue(caretakerQueues);
            targetQueue.enqueue(patient);
        }

        // Start caretakers to process their queues
        List<CaretakerProcessor> caretakerProcessors = new ArrayList<>();
        ExecutorService caretakerExecutor = Executors.newFixedThreadPool(caretakerQueues.size());
        for (AdaptiveQueue queue : caretakerQueues) {
            CaretakerProcessor caretaker = new CaretakerProcessor(queue);
            caretakerProcessors.add(caretaker);
            caretakerExecutor.submit(caretaker);
        }

        // Start emergency doctors to handle urgent cases
        EmergencyDoctorManager emergencyManager = new EmergencyDoctorManager();
        ExecutorService emergencyExecutor = Executors.newCachedThreadPool();
        for (EmergencyPatient patient : emergencyPatients) {
            emergencyExecutor.submit(() -> emergencyManager.assignPatient(patient));
        }

        // Wait for all caretakers to finish
        caretakerExecutor.shutdown();
        caretakerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // Wait for all emergency doctors to finish
        emergencyExecutor.shutdown();
        emergencyExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // Shut down the emergency doctor team
        emergencyManager.shutdown();

        // Print detailed logs from caretakers
        caretakerProcessors.sort(Comparator.comparingInt(cp -> cp.getQueueId()));
        System.out.println("\n======= CARETAKER PROCESSING DETAILS =======");
        for (CaretakerProcessor caretaker : caretakerProcessors) {
            for (String log : caretaker.getLogs()) {
                System.out.println(log);
            }
        }

        // Print a summary of the simulation
        System.out.println("\n======= SIMULATION SUMMARY =======");
        System.out.println("Total caretakers opened: " + caretakerQueues.size());
        printFinalStatistics(allPatients);
    }

    // Find the shortest queue to assign a patient
    private static AdaptiveQueue findBestQueue(List<AdaptiveQueue> queues) {
        if (queues.isEmpty()) {
            throw new IllegalStateException("No queues available");
        }
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

    // Print wait time stats for each patient type
    private static void printFinalStatistics(List<CriticalPatient> allPatients) {
        // Group patients by priority (normal, critical, emergency)
        Map<Integer, List<CriticalPatient>> groupedByPriority = new HashMap<>();
        for (CriticalPatient patient : allPatients) {
            groupedByPriority.computeIfAbsent(patient.getPriority(), k -> new ArrayList<>()).add(patient);
        }
        // Print stats for each priority level
        for (int priority = 0; priority <= 2; priority++) {
            List<CriticalPatient> patients = groupedByPriority.get(priority);
            if (patients != null && !patients.isEmpty()) {
                printQueueStatistics(getPriorityName(priority), patients);
            }
        }
    }

    // Print average, min, and max wait times for a group of patients
    private static void printQueueStatistics(String label, List<CriticalPatient> patients) {
        double totalWait = 0;
        int count = patients.size();
        double maxWait = 0;
        double minWait = Double.MAX_VALUE;
        for (CriticalPatient patient : patients) {
            double waitTime = patient.getWaitingTime();
            totalWait += waitTime;
            maxWait = Math.max(maxWait, waitTime);
            minWait = Math.min(minWait, waitTime);
        }
        if (count > 0) {
            System.out.printf("%n%s Patients (%d):%n", label, count);
            System.out.printf("Average Wait: %.2f min | Min: %.2f min | Max: %.2f min%n",
                    totalWait / count, minWait, maxWait);
        }
    }

    // Convert priority numbers to readable names
    private static String getPriorityName(int level) {
        switch (level) {
            case 1:
                return "Critical";
            case 2:
                return "Emergency";
            default:
                return "Normal";
        }
    }

    // Decide if a patient is normal, critical, or emergency
    private static int assignPatientType(Random rand) {
        int randValue = rand.nextInt(100);
        if (randValue < NORMAL_THRESHOLD) { // 0-79: Normal
            return 0;
        } else if (randValue < CRITICAL_THRESHOLD) { // 80-94: Critical
            return 1;
        } else { // 95-99: Emergency
            return 2;
        }
    }

    // Generate random numbers for treatment times
    public static double getGaussianRandom(double mean, double stdDev) {
        Random rand = new Random();
        return mean + stdDev * rand.nextGaussian();
    }
}