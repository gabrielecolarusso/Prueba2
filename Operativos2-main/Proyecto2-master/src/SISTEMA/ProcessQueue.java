/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

import EDD.ListaEnlazada;
import EDD.Nodo;

public class ProcessQueue {
    private ListaEnlazada<IOProcess> queue;

    public ProcessQueue() {
        this.queue = new ListaEnlazada<>();
    }

    public void addProcess(IOProcess process) {
        process.setState(ProcessState.READY);
        queue.agregar(process);
    }

    public IOProcess getNextProcess() {
        Nodo<IOProcess> cabeza = queue.getCabeza();
        if (cabeza != null) {
            IOProcess process = cabeza.dato;
            queue.eliminar(process);
            return process;
        }
        return null;
    }

    public ListaEnlazada<IOProcess> getQueue() {
        return queue;
    }

    public int size() {
        return queue.contarElementos();
    }

    public boolean isEmpty() {
        return queue.getCabeza() == null;
    }
}
