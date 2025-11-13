/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

import EDD.ListaEnlazada;
import EDD.Nodo;

public class DiskScheduler {
    private SchedulingPolicy policy;
    private int currentHead;
    private boolean scanDirection;

    public DiskScheduler(SchedulingPolicy policy) {
        this.policy = policy;
        this.currentHead = 0;
        this.scanDirection = true;
    }

    public void setPolicy(SchedulingPolicy policy) {
        this.policy = policy;
    }

    public SchedulingPolicy getPolicy() {
        return policy;
    }

    public IOProcess scheduleNext(ListaEnlazada<IOProcess> queue) {
        if (queue.getCabeza() == null) {
            return null;
        }

        switch (policy) {
            case FIFO:
                return scheduleFIFO(queue);
            case SSTF:
                return scheduleSSTF(queue);
            case SCAN:
                return scheduleSCAN(queue);
            case CSCAN:
                return scheduleCSCAN(queue);
            default:
                return scheduleFIFO(queue);
        }
    }

    private IOProcess scheduleFIFO(ListaEnlazada<IOProcess> queue) {
        Nodo<IOProcess> cabeza = queue.getCabeza();
        if (cabeza != null) {
            IOProcess process = cabeza.dato;
            queue.eliminar(process);
            currentHead = process.getIoRequest().getBlockPosition();
            return process;
        }
        return null;
    }

    private IOProcess scheduleSSTF(ListaEnlazada<IOProcess> queue) {
        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess closest = null;
        int minDistance = Integer.MAX_VALUE;

        while (actual != null) {
            int distance = Math.abs(actual.dato.getIoRequest().getBlockPosition() - currentHead);
            if (distance < minDistance) {
                minDistance = distance;
                closest = actual.dato;
            }
            actual = actual.siguiente;
        }

        if (closest != null) {
            queue.eliminar(closest);
            currentHead = closest.getIoRequest().getBlockPosition();
        }
        return closest;
    }

    private IOProcess scheduleSCAN(ListaEnlazada<IOProcess> queue) {
        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess selected = null;
        int minDistance = Integer.MAX_VALUE;

        while (actual != null) {
            int blockPos = actual.dato.getIoRequest().getBlockPosition();
            
            if (scanDirection) {
                if (blockPos >= currentHead) {
                    int distance = blockPos - currentHead;
                    if (distance < minDistance) {
                        minDistance = distance;
                        selected = actual.dato;
                    }
                }
            } else {
                if (blockPos <= currentHead) {
                    int distance = currentHead - blockPos;
                    if (distance < minDistance) {
                        minDistance = distance;
                        selected = actual.dato;
                    }
                }
            }
            actual = actual.siguiente;
        }

        if (selected == null) {
            scanDirection = !scanDirection;
            return scheduleSCAN(queue);
        }

        queue.eliminar(selected);
        currentHead = selected.getIoRequest().getBlockPosition();
        return selected;
    }

    private IOProcess scheduleCSCAN(ListaEnlazada<IOProcess> queue) {
        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess selected = null;
        int minDistance = Integer.MAX_VALUE;

        while (actual != null) {
            int blockPos = actual.dato.getIoRequest().getBlockPosition();
            
            if (blockPos >= currentHead) {
                int distance = blockPos - currentHead;
                if (distance < minDistance) {
                    minDistance = distance;
                    selected = actual.dato;
                }
            }
            actual = actual.siguiente;
        }

        if (selected == null) {
            currentHead = 0;
            actual = queue.getCabeza();
            while (actual != null) {
                int blockPos = actual.dato.getIoRequest().getBlockPosition();
                int distance = blockPos;
                if (distance < minDistance) {
                    minDistance = distance;
                    selected = actual.dato;
                }
                actual = actual.siguiente;
            }
        }

        if (selected != null) {
            queue.eliminar(selected);
            currentHead = selected.getIoRequest().getBlockPosition();
        }
        return selected;
    }

    public int getCurrentHead() {
        return currentHead;
    }
}