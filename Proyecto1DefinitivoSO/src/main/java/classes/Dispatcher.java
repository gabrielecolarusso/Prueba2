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
    private List newList;
    private List allProcessList;
    private W1 window;
    public int selectedAlgorithm;
    private EventLogger logger;

    public Dispatcher(List readyList, List blockedList, List exitList, List allProcess, W1 window, EventLogger logger) {
        this.readyList = readyList;
        this.blockedList = blockedList;
        this.exitList = exitList;
        this.suspendedReadyList = new List();
        this.suspendedBlockedList = new List();
        this.newList = new List();
        this.allProcessList = allProcess;
        this.window = window;
        this.logger = logger;
    }

    public int getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public List getReadyList() {
        return readyList;
    }

    public void setSelectedAlgorithm(int selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }
    
    public ProcessImage getProcess(){
        ProcessImage output = null;
        if(this.readyList.isEmpty()){
            selectedAlgorithm = window.getSelectAlgorithm();
            
            sortReadyQueue(selectedAlgorithm);
                
            switch(selectedAlgorithm){
                case 0 -> {
                    output = this.FCFS();
                }
                case 1 -> {
                    output = this.RoundRobin();
                }
                case 2 -> {
                    output = this.SPN();
                }
                case 3 -> {
                    output = this.SRT();
                }
                case 4 -> {
                    output = this.HRR();
                }
                case 5 -> {
                    output = this.Feedback();
                }
            }
        }
        
        this.updateReadyList();
        this.updateProcessList();
        
        if(output != null) {
            logger.log("Dispatcher selecciona proceso: " + output.getName() + " (ID: " + output.getId() + ")");
        }
        
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
                // Feedback usa múltiples colas por prioridad
                readyList = sortByWaitingTime(readyList);
                break;
        }
    }
    
    private List sortByWaitingTime(List list) {
        return bubbleSort(list, (p1, p2) -> Integer.compare(((ProcessImage) p2).getWaitingTime(), ((ProcessImage) p1).getWaitingTime()));
    }

    private ProcessImage FCFS(){
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
       
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }
    
    public ProcessImage RoundRobin(){
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
        
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        output.setQuantum(5);
        return output;
    }

    private ProcessImage SPN(){
        NodoList shortestJob = this.readyList.getHead();
        
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }
    
    private ProcessImage SRT(){
        NodoList shortestJob = this.readyList.getHead();
        
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }
    
    private ProcessImage HRR(){
        NodoList bestJob = this.readyList.getHead();
        
        this.readyList.delete(bestJob);
        ProcessImage output = (ProcessImage) bestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        return output;
    }
    
    private ProcessImage Feedback(){
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
        
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        output.setQuantum(3);
        return output;
    }
    
    public boolean ifSRT(ProcessImage process){
        if(window.getSelectAlgorithm() == 3){
            NodoList current = this.readyList.getHead();
            while (current != null) {
                if (((ProcessImage) current.getValue()).getDuration() - ((ProcessImage) current.getValue()).getMemoryAddressRegister() < 
                        process.getDuration()- process.getMemoryAddressRegister()) {
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
        process.setStatus(state);
        process.setProgramCounter(programCounter);
        process.setMemoryAddressRegister(memoryAddressRegister);
        process.setWaitingTime(0);
        
        logger.log("Proceso " + process.getName() + " cambia a estado: " + state);
        
        if(state.equals("blocked")){
            this.blockedList.appendLast(process);   
        }else if(state.equals("ready")){
            this.readyList.appendLast(process);
        }else if(state.equals("exit")){
            this.exitList.appendLast(process);
        }else if(state.equals("suspended-ready")){
            process.setInMemory(false);
            this.suspendedReadyList.appendLast(process);
        }else if(state.equals("suspended-blocked")){
            process.setInMemory(false);
            this.suspendedBlockedList.appendLast(process);
        }
        
        this.updateReadyList();
        this.updateBlockedList();
        this.updateexitList();
        this.updateSuspendedLists();
        this.updateProcessList();
    }
    
    public void updatePCB(ProcessImage process, String state){
        process.setStatus(state);
        process.setWaitingTime(0);
        
        logger.log("Proceso " + process.getName() + " cambia a estado: " + state);
        
        if(state.equals("blocked")){
            this.blockedList.appendLast(process);   
        }else if(state.equals("ready")){
            this.readyList.appendLast(process);
        }else if(state.equals("exit")){
            this.exitList.appendLast(process);
        }else if(state.equals("suspended-ready")){
            process.setInMemory(false);
            this.suspendedReadyList.appendLast(process);
        }else if(state.equals("suspended-blocked")){
            process.setInMemory(false);
            this.suspendedBlockedList.appendLast(process);
        }
        
        this.updateReadyList();
        this.updateBlockedList();
        this.updateexitList();
        this.updateSuspendedLists();
        this.updateProcessList();
    }
    
    public void updateWaitingTime(){
        if(selectedAlgorithm != window.getSelectAlgorithm()){
            selectedAlgorithm = window.getSelectAlgorithm();
            sortReadyQueue(selectedAlgorithm);
            this.updateReadyList();
        }
        
        NodoList pAux = this.readyList.getHead();
        while(pAux!=null){
            ProcessImage process = (ProcessImage)pAux.getValue();
            int time = process.getWaitingTime();
            process.setWaitingTime(time+1);
            pAux = pAux.getpNext();
        }
        this.updateProcessList();
    }
    
    public void updateBlockToReady(int id){
        NodoList pAux = this.blockedList.getHead();
        while(pAux!=null){
            if(id == ((ProcessImage)pAux.getValue()).getId()){
                ((ProcessImage)pAux.getValue()).setStatus("ready");
                ((ProcessImage)pAux.getValue()).setWaitingTime(0);
                blockedList.delete(pAux);
                readyList.appendLast(pAux);
                logger.log("Proceso ID " + id + " sale de estado bloqueado y pasa a listo");
                break;                
            }
            pAux = pAux.getpNext();
        }
        
        this.updateBlockedList();
        this.updateReadyList();
        this.updateProcessList();
    }
    
    public void suspendProcess(ProcessImage process) {
        if(process.getStatus().equals("ready")) {
            updatePCB(process, "suspended-ready");
        } else if(process.getStatus().equals("blocked")) {
            updatePCB(process, "suspended-blocked");
        }
        logger.log("Proceso " + process.getName() + " suspendido por falta de memoria");
    }
    
    public void resumeProcess(int id) {
        NodoList pAux = this.suspendedReadyList.getHead();
        while(pAux != null) {
            if(id == ((ProcessImage)pAux.getValue()).getId()) {
                ProcessImage process = (ProcessImage)pAux.getValue();
                process.setInMemory(true);
                suspendedReadyList.delete(pAux);
                updatePCB(process, "ready");
                logger.log("Proceso " + process.getName() + " reanudado de suspensión");
                return;
            }
            pAux = pAux.getpNext();
        }
    }
    
    public void updateProcessList(){
        NodoList pAux = allProcessList.getHead();
        String display = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            display += this.makeString(process);
            pAux = pAux.getpNext();
        }
        window.updateProcess(display);
    }
    
    public void updateReadyList(){
        NodoList pAux = readyList.getHead();
        String display = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            
            display += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateReady(display);
    }
    
    public void updateBlockedList(){
        NodoList pAux = blockedList.getHead();
        String display = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            
            display += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateBlock(display);
    }
    
    public void updateexitList(){
        NodoList pAux = exitList.getHead();
        String display = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            
            display += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateExit(display);
    }
    
    public void updateSuspendedLists(){
        NodoList pAux = suspendedReadyList.getHead();
        String displayReady = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            displayReady += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateSuspendedReady(displayReady);
        
        pAux = suspendedBlockedList.getHead();
        String displayBlocked = "";
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            displayBlocked += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateSuspendedBlocked(displayBlocked);
    }
    
    public static String makeString(ProcessImage currentProcess){
        String display = "\n ----------------------------------\n ID: " + currentProcess.getId() + 
                "\n Status: " + currentProcess.getStatus()+ 
                "\n Nombre: " + currentProcess.getName() +
                "\n PC: " + currentProcess.getProgramCounter() + 
                "\n MAR: " + currentProcess.getMemoryAddressRegister() +
                "\n RT: " + (currentProcess.getDuration()-currentProcess.getMemoryAddressRegister()) +
                "\n WT: " + currentProcess.getWaitingTime() +
                "\n In Memory: " + currentProcess.isInMemory() +
                "\n Instructions: " + currentProcess.getInstructions().showAttribute();
        return display;
    }
}