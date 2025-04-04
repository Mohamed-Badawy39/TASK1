import java.util.Random;

public class HospitalQueueSimulation {
    public static void main(String[] args) {
        /*        Random rand = new Random();
        for (int i = 1; i <= 5; i++) {
            double arrivalTime = Queue.getPoissonRandom(5);

                normal np = new normal(i, arrivalTime);
                np.displayInfo();

        }
 */

PriorityQueue queue = new PriorityQueue();
int numPatients = 10;
Random rand = new Random();

double[] arrivalTimes = new double[numPatients];
double[] waitingTimes = new double[numPatients];
double[] serviceTimes = new double[numPatients];
double[] departureTimes = new double[numPatients];
for (int i = 0; i < numPatients; i++) {
    double arrivalTime = PriorityQueue.getPoissonRandom(5);
    double serviceTime = PriorityQueue.getGaussianRandom(10, 2);
    int priority = rand.nextInt(2); // 0 = Normal, 1 = Critical

    CriticalPatient patient = new CriticalPatient(i + 1, arrivalTime, priority);
    patient.setServiceTime(serviceTime) ;
    queue.enqueue(patient);
        }
        double currentTime = 0;
        int index = 0;
        while (!queue.isEmpty()) {
            CriticalPatient patient = queue.dequeue();
            if (currentTime < patient.getArrivalTime()) {
                currentTime = patient.getArrivalTime();
            }

            patient.setWaitingTime(currentTime - patient.getArrivalTime());  // Calculate waiting time

            currentTime += patient.getServiceTime();   // Update current time with service time
            patient.setDepartureTime(currentTime ); // Calculate departure time

            // Store data in arrays
            arrivalTimes[index] = patient.getArrivalTime();
            waitingTimes[index] = patient.getWaitingTime();
            serviceTimes[index] = patient.getServiceTime();
            departureTimes[index] = patient.getDepartureTime();
            index++;

          

            patient.displayInfo();
        }
        System.out.println("\nSummary:");
        System.out.printf("%-10s %-15s %-15s %-15s %-15s\n", "PatientID", "Arrival Time", "Waiting Time", "Service Time", "Departure Time");
        for (int i = 0; i < numPatients; i++) {
            System.out.printf("%-10d %-15.2f %-15.2f %-15.2f %-15.2f\n",
                    i + 1, arrivalTimes[i], waitingTimes[i], serviceTimes[i], departureTimes[i]);
        }

    }
}
