package main;

import classes.*;
import primitivas.*;
import java.util.concurrent.Semaphore;

public class Proyecto1 {

    public static void main(String[] args) {
        String filePath = "procesos.csv";

        List<ProcessImage> readyList = ProcessImageCSV.readProcessesFromCSV(filePath);
        List<ProcessImage> allProcess = new List();
        
        NodoList<ProcessImage> current = readyList.getHead();
        while (current != null) {
            ProcessImage p = current.getValue();
            System.out.println("ID: " + p.getId() + ", Nombre: " + p.getName() + ", Estado: " + p.getStatus());
            allProcess.appendLast(p);
            current = current.getpNext();
        }
        
        Semaphore onPlay = new Semaphore(0);
        Semaphore onPlayClock = new Semaphore(0);
        
        MetricsCollector metrics = new MetricsCollector();
        
        W1 w1 = new W1(onPlay, onPlayClock, readyList, allProcess, metrics);
        w1.setVisible(true);
      
        Semaphore mutexDispatcher = new Semaphore(1);
        TimeHandler timeHandler = new TimeHandler(w1);
        
        List blockedList = new List();
        List exitList = new List();
        List suspendedReadyList = new List();
        List suspendedBlockedList = new List();
        
        Dispatcher dispatcher = new Dispatcher(readyList, blockedList, exitList, suspendedReadyList, suspendedBlockedList, allProcess, w1);
        
        w1.setDispatcher(dispatcher);
        
        Clock clock = new Clock(mutexDispatcher, onPlayClock, w1, dispatcher, timeHandler, metrics);
        CPU cpu = new CPU(timeHandler, dispatcher, mutexDispatcher, onPlay, w1, metrics);
        
        clock.start();
        cpu.start();
    }
}