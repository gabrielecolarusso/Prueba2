/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import primitivas.*;
import main.*;

public class CPU extends Thread {
    private TimeHandler timeHandler;
    private W1 window;
    private int quantum;
    private int memoryAddressRegister;
    private int programCounter;
    private List interruptionsList;
    private Dispatcher dispatcher;
    private ProcessImage currentProcess;
    private Semaphore mutexExceptions;
    private Semaphore onPlay;
    private Semaphore mutexCPU;
    private MetricsCollector metrics;
    private EventLogger logger;

    public CPU(TimeHandler timeHandler, Dispatcher dispatcher, Semaphore mutexCPU, Semaphore onPlay, W1 window, MetricsCollector metrics) {
        this.timeHandler = timeHandler;
        this.dispatcher = dispatcher;
        this.mutexCPU = mutexCPU;
        this.mutexExceptions = new Semaphore(1);
        this.interruptionsList = new List();
        this.onPlay = onPlay;
        this.window = window;
        this.metrics = metrics;
        this.logger = EventLogger.getInstance();
    }
    
    @Override
    public void run(){
        try {
            onPlay.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.getProcess();
        
        while(true){
            if(!this.interruptionsList.isEmpty()){
                Exception exception = (Exception) interruptionsList.getHead().getValue();
                interruptionsList.delete(interruptionsList.getHead());
                this.interruptHandler(exception);
            } else {
                if((this.quantum == 0 || this.checkSRT() || this.checkRR()) && !dispatcher.getReadyList().isEmpty()){
                    if (this.quantum == 0 || this.checkRR()) {
                        if (this.quantum == 0) {
                            logger.logQuantumExpired(currentProcess.getId(), currentProcess.getName());
                        }
                        this.useDispatcher("ready");
                        this.getProcess();
                    } else {
                        this.window.updateCPU("Dispatcher(OS)");
                        for (int i = 0; i < 4; i++) {
                            try {
                                sleep(timeHandler.getInstructionTime());
                            } catch (InterruptedException ex) {
                                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            window.updateDataset(1, "OS");
                        }
                        this.updateInterfaceProcess();
                    }
                } else {
                    if(this.currentProcess.getDuration() <= this.memoryAddressRegister){
                        logger.logProcessCompleted(currentProcess.getId(), currentProcess.getName(), currentProcess.getWaitingTime());
                        metrics.addWaitingTime(currentProcess.getWaitingTime());
                        metrics.incrementProcessesCompleted();
                        this.useDispatcher("exit");
                        this.getProcess();
                    } else {
                        try {
                            sleep(timeHandler.getInstructionTime());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        this.updateInterfaceProcess();
                        window.updateDataset(1, "User");
                        metrics.incrementCpuBusyCycles();
                        quantum--;
                        
                        if(this.isInterruption(memoryAddressRegister)){
                            this.useDispatcher("blocked");
                            this.getProcess();
                        } else {
                            programCounter++;
                            this.memoryAddressRegister++;
                            this.updateInterfaceProcess();
                        }    
                    }
                }
            }
        }
    }

    public boolean isInterruption(int mar){
        if(this.currentProcess.getInstructions() == null || this.currentProcess.getInstructions().getSize() == 0){
            return false;
        }
        
        for (int i = 0; i < this.currentProcess.getInstructions().getSize(); i += 2) {
            if(this.currentProcess.getInstructions().getNodoById(i) != null &&
               this.currentProcess.getInstructions().getNodoById(i).getValue().equals(mar)){
                if(i + 1 < this.currentProcess.getInstructions().getSize()){
                    int j = (int) this.currentProcess.getInstructions().getNodoById(i + 1).getValue();
                    logger.logProcessBlocked(currentProcess.getId(), currentProcess.getName(), j);
                    Exception exception = new Exception(j, this.timeHandler, this.currentProcess.getId(), this.interruptionsList, this.mutexExceptions);
                    exception.start();
                    return true;
                }
            }
        }
        return false;
    }
    
    private void interruptHandler(Exception exception){
        System.out.println("Exception Resolved - Process: " + exception.getProcessId());
        try {
            mutexCPU.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.dispatcher.updateBlockToReady(exception.getProcessId());
        mutexCPU.release();
    }
    
    private void useDispatcher(String state){
        try {
            mutexCPU.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String oldState = currentProcess.getStatus();
        if(quantum != currentProcess.getQuantum()){
            this.dispatcher.updatePCB(currentProcess, programCounter, memoryAddressRegister, state);
        } else {
            this.dispatcher.updatePCB(currentProcess, state);
        }
        logger.logProcessStateChange(currentProcess.getId(), currentProcess.getName(), oldState, state);
        mutexCPU.release();
    }

    private boolean checkSRT(){
        try {
            mutexCPU.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        boolean output = this.dispatcher.ifSRT(currentProcess);
        if(output){
            if(quantum != currentProcess.getQuantum()){
                this.dispatcher.updatePCB(currentProcess, programCounter, memoryAddressRegister, "ready");
            } else {
                this.dispatcher.updatePCB(currentProcess, "ready");
            }
            ProcessImage oldProcess = this.currentProcess;
            this.currentProcess = this.dispatcher.getProcess();
            if(currentProcess != null){
                logger.logPreemption(oldProcess.getId(), oldProcess.getName(), currentProcess.getId(), currentProcess.getName());
                quantum = currentProcess.getQuantum();
                programCounter = currentProcess.getProgramCounter() + 1;
                memoryAddressRegister = currentProcess.getProgramCounter();
            }
        }
        mutexCPU.release();
        return output;
    }

    private void getProcess(){
        currentProcess = null;
        while(currentProcess == null){
            this.window.updateCPU("Dispatcher(OS)");
            for (int i = 0; i < 4; i++) {
                try {
                    sleep(timeHandler.getInstructionTime());
                } catch (InterruptedException ex) {
                    Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
                }
                window.updateDataset(1, "OS");
            }
            
            try {
                mutexCPU.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.currentProcess = this.dispatcher.getProcess();
            mutexCPU.release();
            
            if(currentProcess != null) break;
            
            this.window.updateCPU("Process System");
            try {
                sleep(timeHandler.getInstructionTime());
            } catch (InterruptedException ex) {
                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
            }
            window.updateDataset(1, "OS");
            
            if(!this.interruptionsList.isEmpty()){
                Exception exception = (Exception) interruptionsList.getHead().getValue();
                interruptionsList.delete(interruptionsList.getHead());
                this.interruptHandler(exception);
            }
        }
        
        quantum = currentProcess.getQuantum();
        programCounter = currentProcess.getProgramCounter() + 1;
        memoryAddressRegister = currentProcess.getProgramCounter();
        
        if (currentProcess.getProgramCounter() == 1) {
            metrics.addResponseTime(currentProcess.getWaitingTime());
        }
        
        this.updateInterfaceProcess();
    }
    
    private void updateInterfaceProcess(){
        String display = "ID: " + currentProcess.getId() + 
                "\nStatus: " + currentProcess.getStatus()+ 
                "\nName: " + currentProcess.getName() +
                "\nPC: " + programCounter + 
                "\nMAR: " + this.memoryAddressRegister;
        this.window.updateCPU(display);
    }

    private boolean checkRR(){
        if(this.dispatcher.getSelectedAlgorithm() == 1 && quantum <= -6){
            return true;
        }
        return false;
    }
}