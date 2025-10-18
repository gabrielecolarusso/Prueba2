package classes;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author DELL
 */

import java.util.Comparator;
import primitivas.*;
import main.*;

public class Dispatcher {
    private List readyList;
    private List blockedList;
    private List exitList;
    private List allProcessList;
    private W1 window;
    public int selectedAlgorithm;

    public Dispatcher(List readyList, List blockedList, List exitList,List allProcess, W1 window) {
        this.readyList = readyList;
        this.blockedList = blockedList;
        this.exitList = exitList;
        this.allProcessList = allProcess;
        this.window = window;
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
        
        // Ordenar la lista antes de seleccionar un proceso
        sortReadyQueue(selectedAlgorithm);
            
        switch(selectedAlgorithm){
            case 0 -> {
                //FCFS
                output = this.FCFS();
                }
            case 1 -> {
                //round robin
                 output = this.RoundRobin();
                }
            case 2 -> {
                output = this.SPN();
                // SPN
                }
            case 3 -> {
                 output = this.SRT();
                //SRT
                }
            case 4 -> {
                //HRR
                 output = this.HRR();
                }
            
        }
        }
         //aqui hay que actulizar la interfaz
        this.updateReadyList();
        this.updateProcessList();
        if(output == null){
            System.out.println("process null") ;
        }
        return output;    
    }
    
    /** Ordenar la cola de procesos antes de la selección **/
    private void sortReadyQueue(int schedulingAlgorithm) {
        switch (schedulingAlgorithm) {
            case 0: // FCFS (No requiere ordenamiento
                readyList=sortByWaitingTime(readyList);
                break;
            case 1: // Round Robin (Mantiene el orden)
                readyList=sortByWaitingTime(readyList);
                break;
            case 2: // SPN - Ordenar por menor duración
                readyList = sortByDuration(readyList);
                break;
            case 3: // SRT - Ordenar por menor tiempo restante
                readyList = sortByRemainingTime(readyList);
                break;
            case 4: // HRR - Ordenar por mayor Response Ratio
                readyList = sortByHRR(readyList);
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
        //asi nunca se saldra hasta que haya interrupción
        output.setQuantum(-1);
        //output.setWaitingTime(0);
        return output;

        //while(pAux!=null){
            
            //aplicar algoritmo
            
            //pAux = pAux.getpNext();
        //}
    }
    public ProcessImage RoundRobin(){
        NodoList pAux = this.readyList.getHead();
        this.readyList.delete(pAux);
        //aqui hay que actulizar la interfaz
        ProcessImage output = (ProcessImage) pAux.getValue();
        output.setStatus("running");
        output.setQuantum(5);
        //output.setWaitingTime(0);
        return output;
    }

    private ProcessImage SPN(){
        // Implement SPN algorithm
        NodoList shortestJob = this.readyList.getHead();
        
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        //output.setWaitingTime(0);
        return output;
    }
    private ProcessImage SRT(){
        // Implement SPN algorithm
        //es expulsiva
        NodoList shortestJob = this.readyList.getHead();
        
        this.readyList.delete(shortestJob);
        ProcessImage output = (ProcessImage) shortestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        //output.setWaitingTime(0);
        return output;
    }
    
    private ProcessImage HRR(){
        // Implement HRR algorithm
        NodoList bestJob = this.readyList.getHead();
        
        this.readyList.delete(bestJob);
        ProcessImage output = (ProcessImage) bestJob.getValue();
        output.setStatus("running");
        output.setQuantum(-1);
        //output.setWaitingTime(0);
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
    
    /** Métodos de Ordenamiento **/
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

    public void updatePCB(ProcessImage process,int programCounter,int memoryAddressRegister,String state){ 
        process.setStatus(state);
        process.setProgramCounter(programCounter);
        process.setMemoryAddressRegister(memoryAddressRegister);
        process.setWaitingTime(0);
        if(state=="blocked"){
            this.blockedList.appendLast(process);   
        }else if(state=="ready"){
            this.readyList.appendLast(process);
        }else{
            this.exitList.appendLast(process);
        }
        this.updateReadyList();
        this.updateBlockedList();
        this.updateexitList();
        this.updateProcessList();
    }
    public void updatePCB(ProcessImage process,String state){
        process.setStatus(state);
        process.setWaitingTime(0);
        if(state=="blocked"){
            this.blockedList.appendLast(process);   
        }else if(state=="ready"){
            this.readyList.appendLast(process);
        }else{
            this.exitList.appendLast(process);
        }
        this.updateReadyList();
        this.updateBlockedList();
        this.updateexitList();
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
            if(id== ((ProcessImage)pAux.getValue()).getId()){
                ((ProcessImage)pAux.getValue()).setStatus("ready");
                ((ProcessImage)pAux.getValue()).setWaitingTime(0);
                blockedList.delete(pAux);
                readyList.appendLast(pAux);
                break;                
            }
            pAux = pAux.getpNext();
        }
        
        this.updateBlockedList();
        this.updateReadyList();
        this.updateProcessList();
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
        //System.out.println(pAux);
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
        //System.out.println(pAux);
        while(pAux!=null){
            ProcessImage process=(ProcessImage) pAux.getValue();
            
            display += "\n ----------------------------------\n "
                    + "ID: " + process.getId() +
                      "\n Nombre: " + process.getName();
            pAux = pAux.getpNext();
        }
        window.updateExit(display);
    }
    
    public static String makeString(ProcessImage currentProcess){
        String display = "\n ----------------------------------\n ID: " + currentProcess.getId() + 
                "\n Status: " + currentProcess.getStatus()+ 
                "\n Nombre: " + currentProcess.getName() +
                "\n PC: " + currentProcess.getProgramCounter() + 
                "\n MAR: " + currentProcess.getMemoryAddressRegister() +
                "\n RT: " + (currentProcess.getDuration()-currentProcess.getMemoryAddressRegister()) +
                "\n WT: " + currentProcess.getWaitingTime() +
                "\n Instructions: " + currentProcess.getInstructions().showAttribute()
                ;
        return display;
    }
}
