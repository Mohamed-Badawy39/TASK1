# Hospital Queue Simulation System

A Java-based simulation of a hospital's patient queue management system, prioritizing patients based on urgency (Normal, Critical, Emergency) and processing them using multithreaded caretakers and emergency doctors.

## Project Overview

This system simulates a hospital environment where patients arrive with varying priorities and are processed by either caretakers (for Normal/Critical cases) or emergency doctors (for Emergency cases). Key features include:
- Dynamic queue management with priority-based dequeueing.
- Multithreaded processing for efficient patient handling.
- Real-time logging and statistical reporting.

## Components

### Core Classes

| Class | Description |
|-------|-------------|
| `AdaptiveQueue` | A queue prioritizing critical patients. Automatically scales when thresholds are exceeded. |
| `CaretakerProcessor` | Processes patients in an `AdaptiveQueue`, logs details, and calculates waiting times. |
| `CriticalPatient` | Base class for patients with attributes like arrival time, priority, and service time. |
| `EmergencyPatient` | Subclass of `CriticalPatient` with faster service times for emergency cases. |
| `EmergencyDoctor` | Handles emergency patients in dedicated threads. |
| `EmergencyDoctorManager` | Manages 5 emergency doctors and assigns patients to available ones. |
| `HospitalQueueSimulation` | Main driver class. Generates patients, manages queues, and runs simulations. |
| `MultiPriorityQueue` | Manages three priority queues (Normal, Critical, Emergency) for patient distribution. |
| `PriorityQueue` | FIFO queue implementation using a linked list for each priority level. |
| `QueueProcessor` | Processes patients from a specific priority queue in a thread. |

## How It Works

### Patient Generation
- **100 patients** are generated with random arrival times.
- **Priority Distribution**:
  - **Normal (80%)**: Priority 0.
  - **Critical (15%)**: Priority 1.
  - **Emergency (5%)**: Priority 2.
- Emergency patients have shorter service times (Gaussian-distributed, mean=5 min).

### Queue Management
- **Caretaker Queues**: 
  - Created dynamically when existing queues exceed 25 patients.
  - Critical patients are dequeued first.
- **Emergency Patients**: 
  - Assigned to 1 of 5 dedicated emergency doctors.
  - Processed concurrently for faster service.

### Multithreading
- **Caretakers**: Each queue is processed in a separate thread.
- **Emergency Doctors**: 5 threads handle emergency patients.

## Installation & Usage

### Requirements
- Java 8 or higher.

### Steps
1. **Compile**:
   ```bash
   javac *.java
