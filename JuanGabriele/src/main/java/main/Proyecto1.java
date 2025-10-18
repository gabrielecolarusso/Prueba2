/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package main;


import classes.*;
import classes.ProcessImage;
import classes.ProcessImageCSV;
import java.util.Scanner;
import primitivas.*;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;


/**
 *
 * @author DELL
 */
public class Proyecto1 {

    public static void main(String[] args) {
//        List<ProcessImage> processes = new List<>();
//
//        // Crear procesos de ejemplo y agregarlos a la lista
//        processes.appendLast(new ProcessImage(new List<Integer>(), "CPU Bound", 1, "ready", "Process1", 1, 0, 50));
//        processes.appendLast(new ProcessImage(new List<Integer>(), "I/O Bound", 2, "ready", "Process2", 1, 0, 30));

        // Guardar en CSV
//        ProcessImageCSV.saveProcessesToCSV(processes, "procesos.csv");
        
        //aqui hay que cargar los procesos de las lista
        
        String filePath = "procesos.csv";

        // Leer procesos desde CSV
        List<ProcessImage> readyList = ProcessImageCSV.readProcessesFromCSV(filePath);
        List<ProcessImage> allProcess = new List();
        
        
        // Mostrar los procesos cargados
        NodoList<ProcessImage> current = readyList.getHead();
        while (current != null) {
            ProcessImage p = current.getValue();
            System.out.println("ID: " + p.getId() + ", Nombre: " + p.getName() + ", Estado: " + p.getStatus());
            allProcess.appendLast(p);

            current = current.getpNext();
        }
        
        //para sincronizar los cpu al iniciar la simulación
        Semaphore onPlay = new Semaphore(0);
        Semaphore onPlayClock = new Semaphore(0);
        
        W1 w1 = new W1(onPlay,onPlayClock,readyList,allProcess);
        w1.setVisible(true);
      
        Semaphore mutexDispatcher = new Semaphore(1);
        TimeHandler timeHandler = new TimeHandler(w1);
        //colas del disptcher
        List blockedList = new List();
        List exitList = new List();
        Dispatcher dispatcher = new Dispatcher(readyList,blockedList,exitList,allProcess,w1);
        
        // para los cpus
        Clock clock = new Clock(mutexDispatcher, onPlayClock, w1, dispatcher,timeHandler);
        CPU cpu1 = new CPU(timeHandler,dispatcher,1,mutexDispatcher,onPlay,w1);
        CPU cpu2 = new CPU(timeHandler,dispatcher,2,mutexDispatcher, onPlay,w1);
        CPU cpu3 = new CPU(timeHandler,dispatcher,3,mutexDispatcher, onPlay,w1);
        clock.start();
        cpu1.start();
        cpu3.start();
        cpu2.start();

    }
//    public static void main(String[] args) {
//        UtilityGraph example = new UtilityGraph("Bar Chart Example");
//        example.setSize(800, 400);
//        example.setLocationRelativeTo(null);
//        example.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        example.setVisible(true);
//        Scanner s = new Scanner(System.in);
//
//        String s1 = s.nextLine();
//        // Example of dynamically updating the dataset
//        example.updateDataset("PCPU 5", 100, 200);
//        example.updateDataset("PCPU 6", 120, 150);
//    }
}
