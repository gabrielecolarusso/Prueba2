/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.*;

public class Clock extends Thread {
    private Semaphore mutex;
    private Semaphore onPlay;
    private TimeHandler timeHandler;
    private Dispatcher dispatcher;
    private W1 window;
    private int cycle;
    private MetricsCollector metrics;

    public Clock(Semaphore mutex, Semaphore onPlay, W1 window, Dispatcher dispatcher, TimeHandler timeHandler, MetricsCollector metrics) {
        this.mutex = mutex;
        this.onPlay = onPlay;
        this.window = window;
        this.cycle = 0;
        this.dispatcher = dispatcher;
        this.timeHandler = timeHandler;
        this.metrics = metrics;
    }
    
    @Override
    public void run() {
        try {
            onPlay.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Clock.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while(true){
            try {
                sleep(timeHandler.getInstructionTime());
                mutex.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.dispatcher.updateWaitingTime();
            mutex.release();
            cycle++;
            metrics.incrementCycles();
            window.updateCycle(cycle);
            window.updateMetrics(metrics);
        }
    }
}