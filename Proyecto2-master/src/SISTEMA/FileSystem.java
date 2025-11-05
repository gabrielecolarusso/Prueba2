/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

/**
 *
 * @author yarge
 */
import javax.swing.*;
import EDD.DirectoryEntry;
import EDD.FileEntry;
import EDD.ListaEnlazada;
import EDD.Nodo;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class FileSystem {
    private DirectoryEntry root;
    private SimulatedDisk disk;
    private static final String INFO_PATH = "INFO/";
    private static final String FILE_NAME = INFO_PATH + "filesystem.json";

    public FileSystem(int diskSize) {
        this.root = new DirectoryEntry("root");
        this.disk = new SimulatedDisk(diskSize);
         crearCarpetaInfo();
         cargarDesdeArchivo(); // Cargar datos guardados al iniciar
    }
    private void crearCarpetaInfo() {
        File folder = new File(INFO_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public DirectoryEntry getRoot() {
        return root;
    }

    public SimulatedDisk getDisk() {
        return disk;
    }

    /**
     * 📌 Crear un archivo en un directorio específico.
     */
    public void createFile(String path, String name, int size, String usuario) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            if (existeArchivo(path, name)) {
                JOptionPane.showMessageDialog(null, "❌ El archivo '" + name + "' ya existe en '" + path + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ListaEnlazada<Integer> allocatedBlocks = disk.allocateBlocks(size, name); // 📌 Se agregó "name" como segundo parámetro
            if (allocatedBlocks != null) {
                FileEntry file = new FileEntry(name, size);
                file.blocks = allocatedBlocks;
                dir.addFile(file);
                AuditLog.registrarAccion(usuario, "📂 Creó el archivo '" + name + "' en '" + path + "'");
                guardarEnArchivo();
            } else {
                JOptionPane.showMessageDialog(null, "❌ No hay suficiente espacio en el disco.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "❌ El directorio '" + path + "' no existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 📌 Crear un directorio dentro de otro directorio.
     */
    public void createDirectory(String path, String name, String usuario) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            if (existeDirectorio(path, name)) {
                JOptionPane.showMessageDialog(null, "❌ El directorio '" + name + "' ya existe en '" + path + "'.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DirectoryEntry newDir = new DirectoryEntry(name);
            dir.addDirectory(newDir);
            AuditLog.registrarAccion(usuario, "📂 Creó el directorio '" + name + "' en '" + path + "'");
            guardarEnArchivo();
        } else {
            JOptionPane.showMessageDialog(null, "❌ El directorio '" + path + "' no existe.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 📌 Buscar un directorio en la estructura del sistema de archivos.
     */
    private DirectoryEntry getDirectory(String path) {
        if (path.equals("/")) {
            return root;
        }

        String[] partes = path.split("/");
        DirectoryEntry actual = root;

        for (String parte : partes) {
            if (parte.isEmpty()) continue;

            actual = actual.buscarDirectorio(parte);
            if (actual == null) {
                return null;
            }
        }

        return actual;
    }

    /**
     * 📌 Verificar si un archivo existe en un directorio.
     */
    public boolean existeArchivo(String path, String name) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    return true;
                }
                actual = actual.siguiente;
            }
        }
        return false;
    }

    /**
     * 📌 Verificar si un directorio existe en otro directorio.
     */
    public boolean existeDirectorio(String path, String name) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<DirectoryEntry> actual = dir.subDirectories.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    return true;
                }
                actual = actual.siguiente;
            }
        }
        return false;
    }
    
    public int calcularTamañoDirectorio(DirectoryEntry dir) {
    int total = 0;

    // Sumar el tamaño de todos los archivos dentro del directorio
    Nodo<FileEntry> archivos = dir.files.getCabeza();
    while (archivos != null) {
        total += archivos.dato.size;
        archivos = archivos.siguiente;
        }

    // Recorrer subdirectorios y sumar sus tamaños
    Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
    while (subdirs != null) {
        total += calcularTamañoDirectorio(subdirs.dato);
        subdirs = subdirs.siguiente;
        }
        return total;
    }
    
    public int obtenerPrimerBloqueDirectorio(DirectoryEntry dir) {
        // Verificar si hay archivos y devolver el primer bloque del primero
        Nodo<FileEntry> archivos = dir.files.getCabeza();
        if (archivos != null) {
            return archivos.dato.blocks.obtener(0);
        }

        // Si no hay archivos, buscar en los subdirectorios
        Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
        while (subdirs != null) {
            int bloque = obtenerPrimerBloqueDirectorio(subdirs.dato);
            if (bloque != -1) return bloque;
            subdirs = subdirs.siguiente;
        }

        // Si no hay nada, retornar -1
        return -1;
    }
    
    /**
     * 📌 GUARDAR el estado del sistema en JSON.
     */
    public void guardarEnArchivo() {
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(root, writer);
            System.out.println("✅ Sistema guardado correctamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 📌 CARGAR el estado del sistema desde JSON.
     */
    public void cargarDesdeArchivo() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(FILE_NAME)) {
            Gson gson = new Gson();
            root = gson.fromJson(reader, DirectoryEntry.class);
            corregirEstructura(root);
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
    private void corregirEstructura(DirectoryEntry dir) {
        if (dir.files == null) {
            dir.files = new ListaEnlazada<>();
        }
        if (dir.subDirectories == null) {
            dir.subDirectories = new ListaEnlazada<>();
        }

        Nodo<DirectoryEntry> actual = dir.subDirectories.getCabeza();
        while (actual != null) {
            corregirEstructura(actual.dato);
            actual = actual.siguiente;
        }
    }
    /**
     * 📌 Restaurar una versión de un archivo usando el sistema de backups.
     */
    public String restoreFile(String fileName, String backupFile) {
        return BackupManager.restaurarVersion(fileName, backupFile);
    }

    /**
     * 📌 Registrar cambio de modo (Administrador/Usuario).
     */
    public void changeUserMode(boolean isAdmin) {
        String modo = isAdmin ? "Administrador" : "Usuario";
        AuditLog.registrarAccion("Sistema", "🔄 Cambió el modo de usuario a " + modo);
    }
    public void moverArchivo(String pathOrigen, String fileName, String pathDestino, String usuario) {
        DirectoryEntry origen = getDirectory(pathOrigen);
        DirectoryEntry destino = getDirectory(pathDestino);

        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(null, "❌ Directorio no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Nodo<FileEntry> actual = origen.files.getCabeza();
        while (actual != null) {
            if (actual.dato.name.equals(fileName)) {
                origen.files.eliminar(actual.dato);
                destino.addFile(actual.dato);
                AuditLog.registrarAccion(usuario, "📂 Movió el archivo '" + fileName + "' a '" + pathDestino + "'");
                guardarEnArchivo();
                return;
            }
            actual = actual.siguiente;
        }

        JOptionPane.showMessageDialog(null, "❌ Archivo no encontrado.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void updateFile(String path, String name, String newContent, String usuario) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    // Guardar backup antes de modificar
                    BackupManager.guardarVersion(name, newContent);
                    AuditLog.registrarAccion(usuario, "✏️ Actualizó el archivo '" + name + "'");
                    guardarEnArchivo();
                    return;
                }
                actual = actual.siguiente;
            }
        }
        JOptionPane.showMessageDialog(null, "❌ Archivo '" + name + "' no encontrado. No se puede actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
    }
    public void borrarTodo() {
        root = new DirectoryEntry("root");
        disk = new SimulatedDisk(disk.getTotalBlocks()); // 📌 Reiniciar el disco completamente
        guardarEnArchivo();
        disk.guardarEstadoDisco();
    }
    
    public void deleteFile(String path, String name, String usuario) {
        DirectoryEntry dir = getDirectory(path);
        if (dir != null) {
            Nodo<FileEntry> actual = dir.files.getCabeza();
            while (actual != null) {
                if (actual.dato.name.equals(name)) {
                    // Liberar bloques en el disco
                    disk.releaseBlocks(actual.dato.blocks);

                    // Eliminar archivo del directorio
                    dir.files.eliminar(actual.dato);

                    // Registrar en el log
                    AuditLog.registrarAccion(usuario, "🗑 Eliminó el archivo '" + name + "' en '" + path + "'");

                    // Guardar cambios
                    guardarEnArchivo();

                    JOptionPane.showMessageDialog(null, "✅ Archivo '" + name + "' eliminado correctamente.", "Eliminación exitosa", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                actual = actual.siguiente;
            }
        }
        JOptionPane.showMessageDialog(null, "❌ Archivo '" + name + "' no encontrado en '" + path + "'.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}