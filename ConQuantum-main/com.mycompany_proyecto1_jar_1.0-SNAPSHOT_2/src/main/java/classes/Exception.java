/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;

import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import primitivas.*;

public class Exception extends Thread {
    private int exceptionCycle;
    private TimeHandler timeHandler;
    private int processId;
    private List interruptionList;
    private Semaphore mutex;

    public Exception(int exceptionCycle, TimeHandler timeHandler, int processId, List interruptionList, Semaphore mutex) {
        this.exceptionCycle = exceptionCycle;
        this.timeHandler = timeHandler;
        this.processId = processId;
        this.interruptionList = interruptionList;
        this.mutex = mutex;
    }

    public int getExceptionCycle() {
        return exceptionCycle;
    }

    public void setExceptionCycle(int exceptionCycle) {
        this.exceptionCycle = exceptionCycle;
    }

    public TimeHandler getTimeHandler() {
        return timeHandler;
    }

    public void setTimeHandler(TimeHandler timeHandler) {
        this.timeHandler = timeHandler;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public List getInterruptionList() {
        return interruptionList;
    }

    public void setInterruptionList(List interruptionList) {
        this.interruptionList = interruptionList;
    }

    public Semaphore getMutex() {
        return mutex;
    }

    public void setMutex(Semaphore mutex) {
        this.mutex = mutex;
    }
    
    @Override
    public void run(){
        for(int i = 0; i <= this.exceptionCycle; i++){
            try {
                sleep(timeHandler.getInstructionTime());
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        try {
            mutex.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.interruptionList.appendLast(this);
        mutex.release();
    }
}