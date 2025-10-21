/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventLogger {
    private BufferedWriter writer;
    private DateTimeFormatter formatter;
    private static EventLogger instance;
    
    private EventLogger() {
        try {
            this.writer = new BufferedWriter(new FileWriter("simulation_log.txt", true));
            this.formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
            logEvent("=== NEW SIMULATION STARTED ===");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static synchronized EventLogger getInstance() {
        if (instance == null) {
            instance = new EventLogger();
        }
        return instance;
    }
    
    public synchronized void logEvent(String message) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write(String.format("[%s] %s%n", timestamp, message));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void logProcessSelected(int processId, String processName, String algorithm) {
        logEvent(String.format("SCHEDULER: Selected Process %d (%s) using %s", processId, processName, algorithm));
    }
    
    public void logProcessStateChange(int processId, String processName, String oldState, String newState) {
        logEvent(String.format("STATE CHANGE: Process %d (%s) changed from %s to %s", processId, processName, oldState, newState));
    }
    
    public void logProcessCompleted(int processId, String processName, int totalWaitingTime) {
        logEvent(String.format("COMPLETED: Process %d (%s) finished. Total waiting time: %d cycles", processId, processName, totalWaitingTime));
    }
    
    public void logProcessBlocked(int processId, String processName, int exceptionCycles) {
        logEvent(String.format("BLOCKED: Process %d (%s) blocked for I/O. Expected duration: %d cycles", processId, processName, exceptionCycles));
    }
    
    public void logProcessResumed(int processId, String processName) {
        logEvent(String.format("RESUMED: Process %d (%s) I/O completed, moved to ready queue", processId, processName));
    }
    
    public void logProcessSuspended(int processId, String processName, String reason) {
        logEvent(String.format("SUSPENDED: Process %d (%s) suspended. Reason: %s", processId, processName, reason));
    }
    
    public void logMemoryStatus(int available, int total) {
        logEvent(String.format("MEMORY: %d MB available out of %d MB total", available, total));
    }
    
    public void logAlgorithmChange(String oldAlgorithm, String newAlgorithm) {
        logEvent(String.format("ALGORITHM CHANGE: Switched from %s to %s", oldAlgorithm, newAlgorithm));
    }
    
    public void logQuantumExpired(int processId, String processName) {
        logEvent(String.format("QUANTUM EXPIRED: Process %d (%s) quantum expired, moved to ready queue", processId, processName));
    }
    
    public void logPreemption(int oldProcessId, String oldProcessName, int newProcessId, String newProcessName) {
        logEvent(String.format("PREEMPTION: Process %d (%s) preempted by Process %d (%s)", oldProcessId, oldProcessName, newProcessId, newProcessName));
    }
    
    public void close() {
        try {
            logEvent("=== SIMULATION ENDED ===\n");
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
