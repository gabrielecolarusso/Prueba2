/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

/**
 *
 * @author yarge
 */

public class DirectoryEntry {
    public String name;
    public ListaEnlazada<DirectoryEntry> subDirectories;
    public ListaEnlazada<FileEntry> files;

    public DirectoryEntry(String name) {
        this.name = name;
        this.subDirectories = new ListaEnlazada<>();
        this.files = new ListaEnlazada<>();
    }

    public void addFile(FileEntry file) {
        files.agregar(file);
    }

    public void addDirectory(DirectoryEntry dir) {
        subDirectories.agregar(dir);
    }

    /**
     * 📌 Nuevo método: Buscar un subdirectorio dentro de este directorio
     * @param dirName Nombre del directorio a buscar
     * @return El objeto DirectoryEntry si se encuentra, de lo contrario, null
     */
    public DirectoryEntry buscarDirectorio(String dirName) {
        Nodo<DirectoryEntry> actual = subDirectories.getCabeza();
        while (actual != null) {
            if (actual.dato.name.equals(dirName)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null; // No encontrado
    }

    @Override
    public String toString() {
        return "Directorio: " + name;
    }
}