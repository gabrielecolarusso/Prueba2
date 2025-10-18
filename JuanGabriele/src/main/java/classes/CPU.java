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
/**
 *
 * @author DELL
 */
public class CPU extends Thread {
    private TimeHandler timeHandler;
    private W1 window;
    private int quantum;
    private int memoryAddressRegister;
    private int programCounter;
    private List interruptionsList;
    private Dispatcher dispatcher;
    private ProcessImage currentProcess;
    private int id;
    private Semaphore mutexExceptions;
    private Semaphore onPlay;
    private Semaphore mutexCPUs;

    public CPU(TimeHandler timeHandler, Dispatcher dispatcher, int id, Semaphore mutexCPUs,Semaphore onPlay, W1 window) {
        this.timeHandler = timeHandler;
        this.dispatcher = dispatcher;
        this.id = id;
        this.mutexCPUs = mutexCPUs;
        this.mutexExceptions = new Semaphore(1);
        this.interruptionsList = new List();
        this.onPlay = onPlay;
        this.window = window;
    }
    
    
    /**
     * Hola
     */
    @Override
    public void run(){
        try {
            onPlay.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        // para arrancar
        this.getProcess();
        while(true){
            //checkear interrupciones
            if(this.interruptionsList.isEmpty()){
                Exception exception = (Exception) interruptionsList.getHead().getValue();
                interruptionsList.delete(interruptionsList.getHead());
                this.interruptHandler(exception);
            }else{
                //checkear que el pocesso aun tiene tiempo de ejcución
                //revisar si hay un proceos de mayor prioridad para ploitica expulsivas
                if((this.quantum==0 || this.checkSRT() || this.checkRR())&& dispatcher.getReadyList().isEmpty() ){
                    //quantum 0
                    if (this.quantum==0 || this.checkRR() ) {
                        this.useDispatcher("ready");
                        this.getProcess();

                        
                    }else{
                        this.window.updateCPUs("Dispatcher(OS)", id);
                        for (int i = 0; i < 4; i++) {
                        try {
                            sleep(timeHandler.getInstructionTime());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        window.updateDataset(1, "OS", id);
                        }
                        

                        this.updateInterfaceProcess();
                    }
                    
                }else{
                    if(this.currentProcess.getDuration() < this.memoryAddressRegister){
                        //aqui hay que cambiar la interfas para mostrar que se esta ejecutando
                        //una componente del sistema operativo
                        this.useDispatcher("exit");
                        this.getProcess();
                    }else{
                        //checkear si el proceso termino

                        //aqui hay que cambiar la interfaz para mostrar al proceso

                        //se ejecuta la intruccion del proceso actual
                        try {
                            sleep(timeHandler.getInstructionTime());
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        this.updateInterfaceProcess();
                        window.updateDataset(1, "User", id);
                        quantum--;
                        //checkear si hay que crear interrupción
                        if(this.isInterruption(memoryAddressRegister)){
                            this.useDispatcher("blocked");
                            this.getProcess();

                        }else{
                            // instrucciones inertes
                            //esto es si el proceso continua su ejecución
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
        for (int i = 0; i < this.currentProcess.getInstructions().getSize(); i++) {
            if(i%2==0 && this.currentProcess.getInstructions().getNodoById(i).getValue().equals(mar)){
                int j = (int) this.currentProcess.getInstructions().getNodoById(i+1).getValue();
                Exception exception = new Exception(id,j,this.timeHandler,this.currentProcess.getId(),this.interruptionsList,this.mutexExceptions);
                exception.start();
                return true;
            }
        }
        return false;
    }
    
    private void interruptHandler(Exception exception){
        System.out.println("Exception Resolved");
        System.out.println("CPU: " + exception.getOriginCPU() + " Process: " + exception.getProcessId());
        try {
            //Aqui va un semaforo
                mutexCPUs.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.dispatcher.updateBlockToReady(exception.getProcessId());
            mutexCPUs.release();
    }
    
    private void useDispatcher(String state){
        try {
            //aqui hay que actuliazar el pcb
            mutexCPUs.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        //es posible que salva sin ser ejecutado
        if(quantum != currentProcess.getQuantum()){
            //System.out.println(quantum + currentProcess.getQuantum());
            this.dispatcher.updatePCB(currentProcess, programCounter, memoryAddressRegister,state);
        }else{
            this.dispatcher.updatePCB(currentProcess,state);
        }
       mutexCPUs.release();
    }
    private boolean checkSRT(){
        try {
            //Aqui va un semaforo
                mutexCPUs.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
            boolean output = this.dispatcher.ifSRT(currentProcess);
            if(output){
                if(quantum != currentProcess.getQuantum()){
                    this.dispatcher.updatePCB(currentProcess, programCounter, memoryAddressRegister,"ready");
                }else{
                    this.dispatcher.updatePCB(currentProcess,"ready");
                }
                this.currentProcess = this.dispatcher.getProcess();
                quantum = currentProcess.getQuantum();
                programCounter = currentProcess.getProgramCounter()+1;
                memoryAddressRegister = currentProcess.getProgramCounter();
            }
            mutexCPUs.release();
            return output;
    }
    private void getProcess(){
        //esto es para nada mas que no explote el thread cuando no haya mas proceso
        currentProcess = null;
        while(currentProcess==null){
            this.window.updateCPUs("Dispatcher(OS)", id);
            for (int i = 0; i < 4; i++) {
            try {
                sleep(timeHandler.getInstructionTime());
            } catch (InterruptedException ex) {
                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
            }
            window.updateDataset(1, "OS", id);
            }
            try {
                //Aqui va un semaforo
                    mutexCPUs.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(Exception.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.currentProcess = this.dispatcher.getProcess();
            mutexCPUs.release();
            if(currentProcess != null) break;
            this.window.updateCPUs("Process System", id);
            try {
                sleep(timeHandler.getInstructionTime());
            } catch (InterruptedException ex) {
                Logger.getLogger(CPU.class.getName()).log(Level.SEVERE, null, ex);
            }
            window.updateDataset(1, "OS", id);
            if(this.interruptionsList.isEmpty()){
                Exception exception = (Exception) interruptionsList.getHead().getValue();
                interruptionsList.delete(interruptionsList.getHead());
                this.interruptHandler(exception);
            }
        }
        //
        quantum = currentProcess.getQuantum();
        programCounter = currentProcess.getProgramCounter()+1;
        memoryAddressRegister = currentProcess.getProgramCounter();
        this.updateInterfaceProcess();

        }
    
    private void updateInterfaceProcess(){
        String display = "ID: " + currentProcess.getId() + 
                "\nStatus: " + currentProcess.getStatus()+ 
                "\nName: " + currentProcess.getName() +
                "\nPC: " + programCounter + 
                "\nMAR: " + this.memoryAddressRegister ;
        this.window.updateCPUs(display, id);
    }
    private boolean checkRR(){
        if(this.dispatcher.getSelectedAlgorithm() == 1 && quantum<=-6){
            
            return true;
        }
        return false;
    }
}
