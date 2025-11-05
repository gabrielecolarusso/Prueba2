/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author yarge
 */
public class FileEntry {
    public String name;
    public int size;
    public ListaEnlazada<Integer> blocks;

    public FileEntry(String name, int size) {
        this.name = name;
        this.size = size;
        this.blocks = new ListaEnlazada<>();
    }

    @Override
    public String toString() {
        return "Archivo: " + name + " (" + size + " KB)";
    }
}
