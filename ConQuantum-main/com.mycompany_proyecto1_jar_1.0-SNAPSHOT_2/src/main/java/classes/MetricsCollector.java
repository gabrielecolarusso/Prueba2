/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

public class MetricsCollector {
    private int totalProcessesCompleted;
    private int totalCycles;
    private int cpuBusyCycles;
    private int totalWaitingTime;
    private int totalResponseTime;
    private int processesStarted;
    private long simulationStartTime;
    
    public MetricsCollector() {
        this.totalProcessesCompleted = 0;
        this.totalCycles = 0;
        this.cpuBusyCycles = 0;
        this.totalWaitingTime = 0;
        this.totalResponseTime = 0;
        this.processesStarted = 0;
        this.simulationStartTime = System.currentTimeMillis();
    }
    
    public synchronized void incrementProcessesCompleted() {
        this.totalProcessesCompleted++;
    }
    
    public synchronized void incrementCycles() {
        this.totalCycles++;
    }
    
    public synchronized void incrementCpuBusyCycles() {
        this.cpuBusyCycles++;
    }
    
    public synchronized void addWaitingTime(int waitingTime) {
        this.totalWaitingTime += waitingTime;
    }
    
    public synchronized void addResponseTime(int responseTime) {
        this.totalResponseTime += responseTime;
        this.processesStarted++;
    }
    
    public double getThroughput() {
        if (totalCycles == 0) return 0;
        return (double) totalProcessesCompleted / totalCycles;
    }
    
    public double getCpuUtilization() {
        if (totalCycles == 0) return 0;
        return ((double) cpuBusyCycles / totalCycles) * 100;
    }
    
    public double getAverageWaitingTime() {
        if (totalProcessesCompleted == 0) return 0;
        return (double) totalWaitingTime / totalProcessesCompleted;
    }
    
    public double getAverageResponseTime() {
        if (processesStarted == 0) return 0;
        return (double) totalResponseTime / processesStarted;
    }
    
    public double getFairness() {
        if (totalProcessesCompleted <= 1) return 100.0;
        return 100.0 - (getAverageWaitingTime() * 0.1);
    }
    
    public String getMetricsReport() {
        return String.format(
            "=== PERFORMANCE METRICS ===\n" +
            "Throughput: %.4f processes/cycle\n" +
            "CPU Utilization: %.2f%%\n" +
            "Avg Waiting Time: %.2f cycles\n" +
            "Avg Response Time: %.2f cycles\n" +
            "Fairness Score: %.2f%%\n" +
            "Total Completed: %d processes\n" +
            "Total Cycles: %d\n",
            getThroughput(),
            getCpuUtilization(),
            getAverageWaitingTime(),
            getAverageResponseTime(),
            getFairness(),
            totalProcessesCompleted,
            totalCycles
        );
    }
    
    public int getTotalProcessesCompleted() {
        return totalProcessesCompleted;
    }
    
    public int getTotalCycles() {
        return totalCycles;
    }
}
