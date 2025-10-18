package classes;

import primitivas.List;
import primitivas.NodoList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProcessImageCSV {

    // Método para guardar la lista de procesos en un archivo CSV
    public static void saveProcessesToCSV(List<ProcessImage> processes, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Escribir la cabecera del CSV
            writer.write("ID,Name,Type,Status,ProgramCounter,MemoryAddressRegister,Duration,Quantum,WaitingTime,Instructions");
            writer.newLine();

            // Recorrer la lista y escribir cada proceso en el archivo
            NodoList<ProcessImage> current = processes.getHead();
            while (current != null) {
                writer.write(formatProcessAsCSV(current.getValue()));
                writer.newLine();
                current = current.getpNext();
            }

            System.out.println("Procesos guardados correctamente en " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para formatear un proceso como una línea CSV
    private static String formatProcessAsCSV(ProcessImage process) {
        return process.getId() + "," +
               process.getName() + "," +
               process.getType() + "," +
               process.getStatus() + "," +
               process.getProgramCounter() + "," +
               process.getMemoryAddressRegister() + "," +
               process.getDuration() + "," +
               process.getQuantum() + "," +
               process.getWaitingTime() + "," +
               formatInstructionsAsString(process.getInstructions());
    }

    // Método para convertir la lista de instrucciones en un String separado por ";"
    private static String formatInstructionsAsString(List<Integer> instructions) {
        if (instructions == null || instructions.getSize() == 0) {
            return "[]"; // Devuelve una lista vacía si no hay instrucciones
        }

        StringBuilder sb = new StringBuilder();
        NodoList<Integer> current = instructions.getHead();
        while (current != null) {
            sb.append(current.getValue()).append(";");
            current = current.getpNext();
        }

        // Eliminar el último ";"
        return sb.substring(0, sb.length() - 1);
    }
    
    public static List<ProcessImage> readProcessesFromCSV(String filePath) {
    List<ProcessImage> processes = new List<>();

    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
        String line;
        boolean firstLine = true; // Para omitir la cabecera

        while ((line = reader.readLine()) != null) {
            if (firstLine) {
                firstLine = false; // Omitimos la primera línea (cabecera)
                continue;
            }

            String[] values = line.split(",");

            if (values.length < 10) { // Ahora hay 10 columnas
                System.out.println("Error al leer línea CSV: " + line);
                continue; // Evita errores si hay datos incompletos
            }

            int id = Integer.parseInt(values[0]);
            String name = values[1];
            String type = values[2];
            String status = values[3];
            int programCounter = Integer.parseInt(values[4]);
            int memoryAddressRegister = Integer.parseInt(values[5]);
            int duration = Integer.parseInt(values[6]);
            int quantum = Integer.parseInt(values[7]);
            int waitingTime = Integer.parseInt(values[8]);

            // Convertir las instrucciones desde String a una lista de números
            List<Integer> instructions = parseInstructionsFromString(values[9]);

            // Crear el objeto ProcessImage
            ProcessImage process = new ProcessImage(instructions, type, id, status, name, programCounter, memoryAddressRegister, duration);
            process.setQuantum(quantum);
            process.setWaitingTime(waitingTime);

            // Agregarlo a la lista personalizada
            processes.appendLast(process);
        }

        System.out.println("Procesos cargados desde " + filePath);

    } catch (IOException e) {
        e.printStackTrace();
    }

    return processes;
}

    // Método para convertir una cadena de instrucciones "1;2;3;4" en una lista de enteros
    private static List<Integer> parseInstructionsFromString(String instructionsString) {
        List<Integer> instructions = new List<>();
        if (instructionsString.equals("[]")) return instructions; // Si está vacío, retorna una lista vacía

        String[] values = instructionsString.split(";");
        for (String value : values) {
            instructions.appendLast(Integer.parseInt(value));
        }

        return instructions;
    }
}
