/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter; 
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import EDD.ListaEnlazada;
import EDD.Nodo;
import java.util.HashMap;
import com.google.gson.Gson;
/**
 *
 * @author yarge
 */
public class SimulatedDisk {
    private boolean[] blockMap;
    private HashMap<Integer, String> blockToFileMap;
    private static final String INFO_PATH = "INFO/";
    private static final String DISK_FILE = INFO_PATH + "disk.json";
    private int totalBlocks;

    public SimulatedDisk(int totalBlocks) {
        this.blockMap = new boolean[totalBlocks];
        this.blockToFileMap = new HashMap<>();
        this.totalBlocks = totalBlocks;
        crearCarpetaInfo();
        cargarEstadoDisco();
    }

    private void crearCarpetaInfo() {
        File folder = new File(INFO_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public boolean[] getBlockMap() {
        return blockMap;
    }

    /**
     * 📌 Asigna bloques a un archivo y guarda la relación en el mapa.
     */
    public ListaEnlazada<Integer> allocateBlocks(int size, String fileName) {
        ListaEnlazada<Integer> allocatedBlocks = new ListaEnlazada<>();
        int count = 0;
        int totalBlocks = blockMap.length;

        while (count < size) {
            int index = (int) (Math.random() * totalBlocks); // 📌 Elegir bloques aleatorios
            if (!blockMap[index]) {
                blockMap[index] = true;
                allocatedBlocks.agregar(index);
                blockToFileMap.put(index, fileName); // 📌 Asociar bloque con el archivo
                count++;
            }
        }

        guardarEstadoDisco(); // 📌 Guardar el estado del disco después de asignar los bloques
        return allocatedBlocks;
    }
    /**
     * 📌 Libera los bloques ocupados por un archivo.
     */
    public void releaseBlocks(ListaEnlazada<Integer> blocks) {
        Nodo<Integer> actual = blocks.getCabeza();
        while (actual != null) {
            blockMap[actual.dato] = false; // 📌 Marcar bloque como libre
            blockToFileMap.remove(actual.dato); // 📌 Eliminar la relación del archivo con el bloque
            actual = actual.siguiente;
        }
        guardarEstadoDisco(); // 📌 Guardar cambios en JSON
    }
    /**
     * 📌 Devuelve el nombre del archivo que ocupa un bloque determinado.
     */
    public String getArchivoPorBloque(int bloque) {
        return blockToFileMap.getOrDefault(bloque, null);
    }

    /**
     * 📌 Guarda el estado del disco en INFO/disk.json.
     */
    public void guardarEstadoDisco() {
        try (Writer writer = new FileWriter(DISK_FILE)) {
            new Gson().toJson(blockMap, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 📌 Carga el estado del disco desde INFO/disk.json.
     */
    private void cargarEstadoDisco() {
        File file = new File(DISK_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(DISK_FILE)) {
                boolean[] loadedBlockMap = new Gson().fromJson(reader, boolean[].class);
                if (loadedBlockMap != null && loadedBlockMap.length == blockMap.length) {
                    System.arraycopy(loadedBlockMap, 0, blockMap, 0, loadedBlockMap.length);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 📌 Método para obtener el mapa de bloques del disco.
     */


    /**
     * @return the totalBlocks
     */
    public int getTotalBlocks() {
        return totalBlocks;
    }

    /**
     * @param totalBlocks the totalBlocks to set
     */
    public void setTotalBlocks(int totalBlocks) {
        this.totalBlocks = totalBlocks;
    }
}