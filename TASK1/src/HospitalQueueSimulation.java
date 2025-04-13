import java.util.*;
import java.util.concurrent.*;

public class HospitalQueueSimulation {
    private static final int QUEUE_THRESHOLD = 25;
    private static final int NORMAL_THRESHOLD = 80;
    private static final int CRITICAL_THRESHOLD = 95;

    public static void main(String[] args) throws InterruptedException {
        Random rand = new Random();
        int numPatients = 100;
        double currentArrivalTime = 480;
        List<AdaptiveQueue> caretakerQueues = new ArrayList<>();
        caretakerQueues.add(new AdaptiveQueue(1));
        List<EmergencyPatient> emergencyPatients = new ArrayList<>();
        List<CriticalPatient> allPatients = new ArrayList<>();

        for (int i = 0; i < numPatients; i++) {
            int patientType = assignPatientType(rand);
            if (patientType == 2) {
                EmergencyPatient patient = new EmergencyPatient(i + 1);
                patient.setArrivalTime(currentArrivalTime);
                patient.setFasterServiceTime();
                patient.setPriority(2);
                emergencyPatients.add(patient);
                allPatients.add(patient);
            } else {
                CriticalPatient patient = new CriticalPatient(i + 1);
                patient.setArrivalTime(currentArrivalTime);
                double serviceTime = getGaussianRandom(10, 2);
                patient.setServiceTime(serviceTime);
                patient.setPriority(patientType);
                allPatients.add(patient);
            }
            double interval = 1 + rand.nextDouble() * 5;
            currentArrivalTime += interval;
        }

        allPatients.sort(Comparator.comparingDouble(CriticalPatient::getArrivalTime));

        for (CriticalPatient patient : allPatients) {
            if (patient.getPriority() == 2) {
                continue;
            }
            boolean needNewQueue = true;
            for (AdaptiveQueue queue : caretakerQueues) {
                if (queue.size() < QUEUE_THRESHOLD) {
                    needNewQueue = false;
                    break;
                }
            }
            if (needNewQueue) {
                caretakerQueues.add(new AdaptiveQueue(caretakerQueues.size() + 1));
            }
            AdaptiveQueue targetQueue = findBestQueue(caretakerQueues);
            targetQueue.enqueue(patient);
        }

        List<CaretakerProcessor> caretakerProcessors = new ArrayList<>();
        ExecutorService caretakerExecutor = Executors.newFixedThreadPool(caretakerQueues.size());
        for (AdaptiveQueue queue : caretakerQueues) {
            CaretakerProcessor cp = new CaretakerProcessor(queue);
            caretakerProcessors.add(cp);
            caretakerExecutor.submit(cp);
        }

        EmergencyDoctorManager emergencyManager = new EmergencyDoctorManager();
        ExecutorService emergencyExecutor = Executors.newCachedThreadPool();
        for (EmergencyPatient ep : emergencyPatients) {
            emergencyExecutor.submit(() -> emergencyManager.assignPatient(ep));
        }

        caretakerExecutor.shutdown();
        caretakerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        emergencyExecutor.shutdown();
        emergencyExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        emergencyManager.shutdown();

        caretakerProcessors.sort(Comparator.comparingInt(cp -> cp.getQueueId()));
        System.out.println("\n======= CARETAKER PROCESSING DETAILS =======");
        for (CaretakerProcessor cp : caretakerProcessors) {
            for (String log : cp.getLogs()) {
                System.out.println(log);
            }
        }

        System.out.println("\n======= SIMULATION SUMMARY =======");
        System.out.println("Total caretakers opened: " + caretakerQueues.size());
        printFinalStatistics(allPatients);
    }

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

    private static void printFinalStatistics(List<CriticalPatient> allPatients) {
        Map<Integer, List<CriticalPatient>> groupedByPriority = new HashMap<>();
        for (CriticalPatient p : allPatients) {
            groupedByPriority.computeIfAbsent(p.getPriority(), k -> new ArrayList<>()).add(p);
        }
        for (int priority = 0; priority <= 2; priority++) {
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
            System.out.printf("%n%s Patients (%d):%n", label, count);
            System.out.printf("Average Wait: %.2f min | Min: %.2f min | Max: %.2f min%n", totalWait / count, minWait, maxWait);
        }
    }

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

    private static int assignPatientType(Random rand) {
        int randValue = rand.nextInt(100);
        if (randValue < NORMAL_THRESHOLD) {
            return 0;
        } else if (randValue < CRITICAL_THRESHOLD) {
            return 1;
        } else {
            return 2;
        }
    }

    public static double getGaussianRandom(double mean, double stdDev) {
        Random rand = new Random();
        return mean + stdDev * rand.nextGaussian();
    }
}
