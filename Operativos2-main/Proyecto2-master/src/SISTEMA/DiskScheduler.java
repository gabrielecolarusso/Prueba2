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
        System.out.println("🔧 DiskScheduler inicializado: currentHead=0, scanDirection=ASCENDENTE");

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
        // LOG DE DEBUG
        System.out.println("🔍 SSTF - currentHead: " + currentHead);

        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess closest = null;
        int minDistance = Integer.MAX_VALUE;

        // Mostrar todos los procesos en cola
        System.out.println("   📋 Procesos en cola:");
        Nodo<IOProcess> temp = queue.getCabeza();
        while (temp != null) {
            int blockPos = temp.dato.getIoRequest().getBlockPosition();
            int distance = Math.abs(blockPos - currentHead);
            System.out.println("      - " + temp.dato.getName() + " en bloque " + blockPos + " (distancia: " + distance + ")");
            temp = temp.siguiente;
        }

        while (actual != null) {
            int distance = Math.abs(actual.dato.getIoRequest().getBlockPosition() - currentHead);
            if (distance < minDistance) {
                minDistance = distance;
                closest = actual.dato;
            }
            actual = actual.siguiente;
        }

        if (closest != null) {
            System.out.println("   ✅ Seleccionado: " + closest.getName() + " (distancia mínima: " + minDistance + ")");
            queue.eliminar(closest);
            currentHead = closest.getIoRequest().getBlockPosition();

            // LOG EN AUDIT
            AuditLog.registrarAccion("Sistema", 
                String.format("🔍 SSTF procesó '%s' (bloque %d, distancia: %d)", 
                    closest.getIoRequest().getFileName(), 
                    currentHead,
                    minDistance));
        }
        return closest;
    }

    private IOProcess scheduleSCAN(ListaEnlazada<IOProcess> queue) {
        // LOG DE DEBUG
        System.out.println("🔍 SCAN - currentHead: " + currentHead + ", direction: " + (scanDirection ? "ASCENDENTE ↑" : "DESCENDENTE ↓"));

        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess selected = null;
        int minDistance = Integer.MAX_VALUE;

        // Mostrar todos los procesos en cola
        System.out.println("   📋 Procesos en cola:");
        Nodo<IOProcess> temp = queue.getCabeza();
        while (temp != null) {
            System.out.println("      - " + temp.dato.getName() + " en bloque " + temp.dato.getIoRequest().getBlockPosition());
            temp = temp.siguiente;
        }

        while (actual != null) {
            int blockPos = actual.dato.getIoRequest().getBlockPosition();

            if (scanDirection) { // Dirección ASCENDENTE (hacia bloques mayores)
                if (blockPos >= currentHead) {
                    int distance = blockPos - currentHead;
                    if (distance < minDistance) {
                        minDistance = distance;
                        selected = actual.dato;
                    }
                }
            } else { // Dirección DESCENDENTE (hacia bloques menores)
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

        // Si no encontramos ningún proceso en la dirección actual, cambiamos de dirección
        if (selected == null) {
            System.out.println("   ⚠️  No hay más procesos en dirección " + (scanDirection ? "ASCENDENTE" : "DESCENDENTE") + ", cambiando dirección...");
            scanDirection = !scanDirection;
            return scheduleSCAN(queue); // Recursivamente buscar en la otra dirección
        }

        // Registrar selección
        System.out.println("   ✅ Seleccionado: " + selected.getName() + " en bloque " + selected.getIoRequest().getBlockPosition());

        queue.eliminar(selected);
        currentHead = selected.getIoRequest().getBlockPosition();

        // LOG EN AUDIT
        AuditLog.registrarAccion("Sistema", 
            String.format("🔍 SCAN procesó '%s' (bloque %d, dirección: %s)", 
                selected.getIoRequest().getFileName(), 
                currentHead,
                scanDirection ? "↑" : "↓"));

        return selected;
    }

    private IOProcess scheduleCSCAN(ListaEnlazada<IOProcess> queue) {
        // LOG DE DEBUG
        System.out.println("🔍 C-SCAN - currentHead: " + currentHead + " (siempre dirección ASCENDENTE ↑)");

        Nodo<IOProcess> actual = queue.getCabeza();
        IOProcess selected = null;
        int minDistance = Integer.MAX_VALUE;

        // Mostrar todos los procesos en cola
        System.out.println("   📋 Procesos en cola:");
        Nodo<IOProcess> temp = queue.getCabeza();
        while (temp != null) {
            System.out.println("      - " + temp.dato.getName() + " en bloque " + temp.dato.getIoRequest().getBlockPosition());
            temp = temp.siguiente;
        }

        // Buscar el proceso más cercano en dirección ascendente (>= currentHead)
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

        // Si no hay procesos adelante, volver al inicio del disco (circular)
        if (selected == null) {
            System.out.println("   🔄 No hay procesos adelante, regresando al inicio del disco (posición 0)...");
            currentHead = 0;
            actual = queue.getCabeza();
            minDistance = Integer.MAX_VALUE;

            while (actual != null) {
                int blockPos = actual.dato.getIoRequest().getBlockPosition();
                int distance = blockPos; // Distancia desde 0
                if (distance < minDistance) {
                    minDistance = distance;
                    selected = actual.dato;
                }
                actual = actual.siguiente;
            }
        }

        if (selected != null) {
            System.out.println("   ✅ Seleccionado: " + selected.getName() + " en bloque " + selected.getIoRequest().getBlockPosition());
            queue.eliminar(selected);
            currentHead = selected.getIoRequest().getBlockPosition();

            // LOG EN AUDIT
            AuditLog.registrarAccion("Sistema", 
                String.format("🔍 C-SCAN procesó '%s' (bloque %d)", 
                    selected.getIoRequest().getFileName(), 
                    currentHead));
        }

        return selected;
    }

    public int getCurrentHead() {
        return currentHead;
    }
}