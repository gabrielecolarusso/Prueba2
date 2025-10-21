/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

package classes;

import java.util.Comparator;
import primitivas.*;
import main.*;

public class Dispatcher {
    private List readyList;
    private List blockedList;
    private List exitList;
    private List suspendedReadyList;
    private List suspendedBlockedList;
    private List allProcessList;
    private W1 window;
    public int selectedAlgorithm;
    private int availableMemory;
    private static final int TOTAL_MEMORY = 1000;
    private EventLogger logger;
    private String[] algorithmNames = {"FCFS", "Round Robin", "SPN", "SRT", "HRR", "Feedback"};

    public Dispatcher(List readyList, List blockedList, List exitList, List suspendedReadyList, List suspendedBlockedList, List allProcess, W1 window) {
        this.readyList = readyList;
        this.blockedList = blockedList;
        this.exitList = exitList;
        this.suspendedReadyList = suspendedReadyList;
        this.suspendedBlockedList = suspendedBlockedList;
        this.allProcessList = allProcess;
        this.window = window;
        this.logger = EventLogger.getInstance();
        
        this.availableMemory = TOTAL_MEMORY;
        NodoList current = readyList.getHead();
        while (current != null) {
            ProcessImage p = (ProcessImage) current.getValue();
            availableMemory -= p.getMemoryRequired();
            current = current.getpNext();
        }
        
        System.out.println("Initial available memory: " + availableMemory + " MB");
        logger.logMemoryStatus(availableMemory, TOTAL_MEMORY);
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public List getReadyList() {
        return readyList;
    }

    public void setSelectedAlgorithm(int selectedAlgorithm) {
        if (this.selectedAlgorithm != selectedAlgorithm) {
            logger.logAlgorithmChange(algorithmNames[this.selectedAlgorithm], algorithmNames[selectedAlgorithm]);
        }
        this.selectedAlgorithm = selectedAlgorithm;
    }
    
    public ProcessImage getProcess(){
        ProcessImage output = null;
        
        if(!this.readyList.isEmpty()){
            int newAlgorithm = window.getSelectAlgorithm();
            if (selectedAlgorithm != newAlgorithm) {
                setSelectedAlgorithm(newAlgorithm);
            } else {
                selectedAlgorithm = newAlgorithm;
            }
            
            checkMemoryAndSuspend();
            checkMemoryAndResume();
            
            sortReadyQueue(selectedAlgorithm);
            
            switch(selectedAlgorithm){
                case 0 -> output = this.FCFS();
                case 1 -> output = this.RoundRobin();
                case 2 -> output = this.SPN();
                case 3 -> output = this.SRT();
                case 4 -> output = this.HRR();
                case 5 -> output = this.Feedback();
            }
            
            if (output != null) {
                logger.logProcessSelected(output.getId(), output.getName(), algorithmNames[selectedAlgorithm]);
            }
        }
        
        this.updateReadyList();
        this.updateProcessList();
        return output;    
    }
    
    private void sortReadyQueue(int schedulingAlgorithm) {
        switch (schedulingAlgorithm) {
            case 0:
                readyList = sortByWaitingTime(readyList);
                break;
            case 1:
                readyList = sortByWaitingTime(readyList);
                break;
            case 2:
                readyList = sortByDuration(readyList);
                break;
            case 3:
                readyList = sortByRemainingTime(readyList);
                break;
            case 4:
                readyList = sortByHRR(readyList);
                break;
            case 5:
                readyList = sortByPriority(readyList);
                break;
        }
    }

    private List sortByWaitingTime(List list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(((ProcessImage) p2).getWaitingTime(), ((ProcessImage) p1).getWaitingTime()));
    }

    private ProcessImage FCFS(){
        if(readyList.isEmpty()) return null;
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }

    public ProcessImage RoundRobin(){
        if(readyList.isEmpty()) return null;
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        int dynamicQuantum = window.getQuantum();
        output.setQuantum(dynamicQuantum);
        return output;
    }

    private ProcessImage SPN(){
        if(readyList.isEmpty()) return null;
        NodoList shortestJob = this.readyList.getHead();
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }

    private ProcessImage SRT(){
        if(readyList.isEmpty()) return null;
        NodoList shortestJob = this.readyList.getHead();
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }
    
    private ProcessImage HRR(){
        if(readyList.isEmpty()) return null;
        NodoList bestJob = this.readyList.getHead();
        this.readyList.delete(bestJob);
        ProcessImage output = (ProcessImage) bestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }

    private ProcessImage Feedback(){
        if(readyList.isEmpty()) return null;
        NodoList bestJob = this.readyList.getHead();
        this.readyList.delete(bestJob);
        ProcessImage output = (ProcessImage) bestJob.getValue();
        output.setStatus("running");
        int dynamicQuantum = window.getQuantum();
        output.setQuantum(dynamicQuantum);
        return output;
    }
    
    public boolean ifSRT(ProcessImage process){
        if(window.getSelectAlgorithm() == 3){
            NodoList current = this.readyList.getHead();
            while (current != null) {
                if (((ProcessImage) current.getValue()).getDuration() - ((ProcessImage) current.getValue()).getMemoryAddressRegister() < 
                        process.getDuration() - process.getMemoryAddressRegister()) {
                    return true;
                }
                current = current.getpNext();
            }    
        }
        return false;
    }
    
    private List sortByDuration(List list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(((ProcessImage) p1).getDuration(), ((ProcessImage) p2).getDuration()));
    }

    private List sortByRemainingTime(List list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(
                ((ProcessImage) p1).getDuration() - ((ProcessImage) p1).getProgramCounter(),
                ((ProcessImage) p2).getDuration() - ((ProcessImage) p2).getProgramCounter()
        ));
    }

    private List sortByHRR(List list) {
        return bubbleSort(list, (p1, p2) -> Double.compare(getHRR((ProcessImage) p2), getHRR((ProcessImage) p1)));
    }

    private List sortByPriority(List list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(
                ((ProcessImage) p2).getWaitingTime(),
                ((ProcessImage) p1).getWaitingTime()
        ));
    }

    private double getHRR(ProcessImage p) {
        return (p.getWaitingTime() + p.getDuration()) / (double) p.getDuration();
    }

    private List bubbleSort(List list, Comparator comparator) {
        if (list.getSize() <= 1) return list;

        boolean swapped;
        do {
            swapped = false;
            NodoList current = list.getHead();
            while (current != null && current.getpNext() != null) {
                if (comparator.compare(current.getValue(), current.getpNext().getValue()) > 0) {
                    Object temp = current.getValue();
                    current.setValue(current.getpNext().getValue());
                    current.getpNext().setValue(temp);
                    swapped = true;
                }
                current = current.getpNext();
            }
        } while (swapped);

        return list;
    }

    public void updatePCB(ProcessImage process, int programCounter, int memoryAddressRegister, String state){ 
        String oldState = process.getStatus();
        process.setStatus(state);
        process.setProgramCounter(programCounter);
        process.setMemoryAddressRegister(memoryAddressRegister);
        process.setWaitingTime(0);
        
        if(state.equals("blocked")){
            this.blockedList.appendLast(process);   
        } else if(state.equals("ready")){
            this.readyList.appendLast(process);
        } else if(state.equals("exit")){
            this.exitList.appendLast(process);
            availableMemory += process.getMemoryRequired();
            System.out.println("Process " + process.getId() + " exited. Available memory: " + availableMemory + " MB");
            logger.logMemoryStatus(availableMemory, TOTAL_MEMORY);
        }
        
        this.updateReadyList();
        this.updateBlockedList();
        this.updateExitList();
        this.updateSuspendedLists();
        this.updateProcessList();
    }

    public void updatePCB(ProcessImage process, String state){
        String oldState = process.getStatus();
        process.setStatus(state);
        process.setWaitingTime(0);
        
        if(state.equals("blocked")){
            this.blockedList.appendLast(process);   
        } else if(state.equals("ready")){
            this.readyList.appendLast(process);
        } else if(state.equals("exit")){
            this.exitList.appendLast(process);
            availableMemory += process.getMemoryRequired();
            System.out.println("Process " + process.getId() + " exited. Available memory: " + availableMemory + " MB");
            logger.logMemoryStatus(availableMemory, TOTAL_MEMORY);
        }
        
        this.updateReadyList();
        this.updateBlockedList();
        this.updateExitList();
        this.updateSuspendedLists();
        this.updateProcessList();
    }
    
    private void checkMemoryAndSuspend() {
        if (availableMemory < 300) {
            System.out.println("\n=== MEMORY LOW: " + availableMemory + " MB available. Starting suspension... ===");
            
            NodoList pAux = readyList.getHead();
            while (pAux != null && availableMemory < 500) {
                ProcessImage process = (ProcessImage) pAux.getValue();
                NodoList next = pAux.getpNext();
                
                if (process.getMemoryRequired() > 0) {
                    readyList.delete(pAux);
                    process.setStatus("suspended-ready");
                    process.setInMemory(false);
                    suspendedReadyList.appendLast(process);
                    availableMemory += process.getMemoryRequired();
                    logger.logProcessSuspended(process.getId(), process.getName(), "Low memory - freed " + process.getMemoryRequired() + " MB");
                    System.out.println("  SUSPENDED (Ready): Process " + process.getId() + 
                                     " (" + process.getName() + "), freed " + 
                                     process.getMemoryRequired() + " MB. Available: " + availableMemory + " MB");
                }
                pAux = next;
            }
            
            pAux = blockedList.getHead();
            while (pAux != null && availableMemory < 500) {
                ProcessImage process = (ProcessImage) pAux.getValue();
                NodoList next = pAux.getpNext();
                
                if (process.getMemoryRequired() > 0) {
                    blockedList.delete(pAux);
                    process.setStatus("suspended-blocked");
                    process.setInMemory(false);
                    suspendedBlockedList.appendLast(process);
                    availableMemory += process.getMemoryRequired();
                    logger.logProcessSuspended(process.getId(), process.getName(), "Low memory (blocked) - freed " + process.getMemoryRequired() + " MB");
                    System.out.println("  SUSPENDED (Blocked): Process " + process.getId() + 
                                     " (" + process.getName() + "), freed " + 
                                     process.getMemoryRequired() + " MB. Available: " + availableMemory + " MB");
                }
                pAux = next;
            }
            
            System.out.println("=== Suspension complete. Available memory: " + availableMemory + " MB ===\n");
            logger.logMemoryStatus(availableMemory, TOTAL_MEMORY);
            updateSuspendedLists();
        }
    }
    
    private void checkMemoryAndResume() {
        NodoList pAux = suspendedReadyList.getHead();
        while (pAux != null) {
            ProcessImage process = (ProcessImage) pAux.getValue();
            NodoList next = pAux.getpNext();
            
            if (availableMemory >= process.getMemoryRequired()) {
                suspendedReadyList.delete(pAux);
                process.setStatus("ready");
                process.setInMemory(true);
                readyList.appendLast(process);
                availableMemory -= process.getMemoryRequired();
                logger.logProcessResumed(process.getId(), process.getName());
                System.out.println("  RESUMED (Ready): Process " + process.getId() + 
                                 " (" + process.getName() + "), allocated " + 
                                 process.getMemoryRequired() + " MB. Available: " + availableMemory + " MB");
            }
            pAux = next;
        }
        
        pAux = suspendedBlockedList.getHead();
        while (pAux != null) {
            ProcessImage process = (ProcessImage) pAux.getValue();
            NodoList next = pAux.getpNext();
            
            if (availableMemory >= process.getMemoryRequired()) {
                suspendedBlockedList.delete(pAux);
                process.setStatus("blocked");
                process.setInMemory(true);
                blockedList.appendLast(process);
                availableMemory -= process.getMemoryRequired();
                logger.logProcessResumed(process.getId(), process.getName());
                System.out.println("  RESUMED (Blocked): Process " + process.getId() + 
                                 " (" + process.getName() + "), allocated " + 
                                 process.getMemoryRequired() + " MB. Available: " + availableMemory + " MB");
            }
            pAux = next;
        }
        
        if (!suspendedReadyList.isEmpty() || !suspendedBlockedList.isEmpty()) {
            updateSuspendedLists();
        }
    }
    
    public void updateWaitingTime(){
        if(selectedAlgorithm != window.getSelectAlgorithm()){
            int newAlgorithm = window.getSelectAlgorithm();
            setSelectedAlgorithm(newAlgorithm);
            sortReadyQueue(selectedAlgorithm);
            this.updateReadyList();
        }
        
        NodoList pAux = this.readyList.getHead();
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            int time = process.getWaitingTime();
            process.setWaitingTime(time + 1);
            pAux = pAux.getpNext();
        }
        
        pAux = this.suspendedReadyList.getHead();
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            int time = process.getWaitingTime();
            process.setWaitingTime(time + 1);
            pAux = pAux.getpNext();
        }
        
        this.updateProcessList();
    }
    
    public void updateBlockToReady(int id){
        NodoList pAux = this.blockedList.getHead();
        while(pAux != null){
            if(id == ((ProcessImage) pAux.getValue()).getId()){
                ProcessImage process = (ProcessImage) pAux.getValue();
                process.setStatus("ready");
                process.setWaitingTime(0);
                blockedList.delete(pAux);
                readyList.appendLast(pAux);
                logger.logProcessResumed(process.getId(), process.getName());
                break;                
            }
            pAux = pAux.getpNext();
        }
        
        pAux = this.suspendedBlockedList.getHead();
        while(pAux != null){
            if(id == ((ProcessImage) pAux.getValue()).getId()){
                ProcessImage process = (ProcessImage) pAux.getValue();
                process.setStatus("suspended-ready");
                process.setWaitingTime(0);
                suspendedBlockedList.delete(pAux);
                suspendedReadyList.appendLast(pAux);
                logger.logProcessResumed(process.getId(), process.getName());
                break;                
            }
            pAux = pAux.getpNext();
        }
        
        this.updateBlockedList();
        this.updateReadyList();
        this.updateSuspendedLists();
        this.updateProcessList();
    }
    
    public void updateProcessList(){
        NodoList pAux = allProcessList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += this.makeString(process);
            pAux = pAux.getpNext();
        }
        window.updateProcess(display);
    }
    
    public void updateReadyList(){
        NodoList pAux = readyList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += "\n ----------------------------------\n " +
                    "ID: " + process.getId() +
                    "\n Nombre: " + process.getName() +
                    "\n WT: " + process.getWaitingTime();
            pAux = pAux.getpNext();
        }
        window.updateReady(display);
    }

    public void updateBlockedList(){
        NodoList pAux = blockedList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += "\n ----------------------------------\n " +
                    "ID: " + process.getId() +
                    "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateBlock(display);
    }
    
    public void updateExitList(){
        NodoList pAux = exitList.getHead();
        String display = "";
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += "\n ----------------------------------\n " +
                    "ID: " + process.getId() +
                    "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateExit(display);
    }

    public void updateSuspendedLists(){
        String display = "";
        
        NodoList pAux = suspendedReadyList.getHead();
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += "\n ----------------------------------\n " +
                    "ID: " + process.getId() +
                    "\n Nombre: " + process.getName() +
                    "\n Type: Ready" +
                    "\n WT: " + process.getWaitingTime();
            pAux = pAux.getpNext();
        }
        
        pAux = suspendedBlockedList.getHead();
        while(pAux != null){
            ProcessImage process = (ProcessImage) pAux.getValue();
            display += "\n ----------------------------------\n " +
                    "ID: " + process.getId() +
                    "\n Nombre: " + process.getName() +
                    "\n Type: Blocked";
            pAux = pAux.getpNext();
        }
        
        window.updateSuspended(display);
    }
    
    public static String makeString(ProcessImage currentProcess){
        String display = "\n ----------------------------------\n ID: " + currentProcess.getId() + 
                "\n Status: " + currentProcess.getStatus()+ 
                "\n Nombre: " + currentProcess.getName() +
                "\n PC: " + currentProcess.getProgramCounter() + 
                "\n MAR: " + currentProcess.getMemoryAddressRegister() +
                "\n RT: " + (currentProcess.getDuration() - currentProcess.getMemoryAddressRegister()) +
                "\n WT: " + currentProcess.getWaitingTime() +
                "\n Memory: " + (currentProcess.isInMemory() ? "In Memory (" + currentProcess.getMemoryRequired() + " MB)" : "Suspended") +
                "\n Instructions: " + currentProcess.getInstructions().showAttribute();
        return display;
    }
}