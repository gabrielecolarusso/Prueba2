/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author yarge
 */


import SISTEMA.FileSystem;
import SISTEMA.AuditLog;
import EDD.DirectoryEntry;
import EDD.FileEntry;
import EDD.ListaEnlazada;
import EDD.Nodo;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;

public class FileSystemGUI extends JFrame {
    private FileSystem fileSystem;
    private JTree fileTree;
    private JTable fileTable;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JButton btnCrearArchivo, btnEliminarArchivo, btnActualizarArchivo, btnRestaurarArchivo, btnCrearDirectorio, btnMoverArchivo, btnCambiarUsuario, btnGuardar, btnBorrarSistema;
    private JLabel lblModo, lblInfo;
    private DiskPanel diskPanel;
    private boolean isAdmin = true;
    private JPanel infoPanel;
    private JTable infoTable;
    private DefaultTableModel infoTableModel;

    public FileSystemGUI() {
        fileSystem = new FileSystem(100);
        setTitle("Simulador de Sistema de Archivos");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // MENÚ SUPERIOR
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(e -> {
            fileSystem.guardarEnArchivo();
            System.exit(0);
        });
        menuArchivo.add(menuSalir);
        menuBar.add(menuArchivo);
        setJMenuBar(menuBar);

        // PANEL IZQUIERDO (JTree)
        rootNode = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);
        fileTree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setPreferredSize(new Dimension(250, 400));
        
         actualizarJTree();

        // PANEL DERECHO (JTable)
        String[] columnNames = {"Archivo", "Tamaño (KB)", "Bloques asignados"};
        Object[][] data = {};
        fileTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        tableScrollPane.setPreferredSize(new Dimension(350, 200));

        // Configurar el panel de información con la tabla de asignación de ficheros
        infoTableModel = new DefaultTableModel(new String[]{"Nombre", "Bloque Inicial", "Longitud"}, 0);
        infoTable = new JTable(infoTableModel);
        infoTable.setEnabled(false); // Solo lectura

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Tabla de Asignación de Ficheros"));
        infoPanel.add(new JScrollPane(infoTable), BorderLayout.CENTER);
        infoPanel.setPreferredSize(new Dimension(250, 100));

        // Panel derecho donde estaba vacío
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(infoPanel, BorderLayout.NORTH);
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        rightPanel.setBorder(BorderFactory.createTitledBorder("📄 Archivos en Disco"));

        // Agregar el panel derecho completo
        add(rightPanel, BorderLayout.EAST);

        // PANEL SUPERIOR (Disco)
        diskPanel = new DiskPanel(fileSystem.getDisk());
        diskPanel.setPreferredSize(new Dimension(800, 100));

        // PANEL INFERIOR (Botones)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        btnCrearArchivo = new JButton("📁 Crear Archivo");
        btnCrearDirectorio = new JButton("📂 Crear Directorio");
        btnEliminarArchivo = new JButton("❌ Eliminar Archivo");
        btnActualizarArchivo = new JButton("✏️ Actualizar Archivo");
        btnRestaurarArchivo = new JButton("🔄 Restaurar Archivo");
        btnMoverArchivo = new JButton("📂 Mover Archivo");
        btnGuardar = new JButton("💾 Guardar");
        btnCambiarUsuario = new JButton("👤 Modo: Administrador");
        btnBorrarSistema = new JButton("🗑 Borrar Sistema");

        btnCrearArchivo.addActionListener(e -> crearArchivo());
        btnCrearDirectorio.addActionListener(e -> crearDirectorio());
        btnEliminarArchivo.addActionListener(e -> eliminarArchivo());
        btnActualizarArchivo.addActionListener(e -> actualizarArchivo());
        btnRestaurarArchivo.addActionListener(e -> restaurarArchivo());
        btnMoverArchivo.addActionListener(e -> moverArchivo());
        btnGuardar.addActionListener(e -> guardarSistema());
        btnCambiarUsuario.addActionListener(e -> cambiarModoUsuario());
        btnBorrarSistema.addActionListener(e -> borrarSistema());
        fileTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String nombre = selectedNode.toString();
                actualizarInfoTabla(nombre);
            }
        });
        buttonPanel.add(btnCrearArchivo);
        buttonPanel.add(btnCrearDirectorio);
        buttonPanel.add(btnEliminarArchivo);
        buttonPanel.add(btnActualizarArchivo);
        buttonPanel.add(btnRestaurarArchivo);
        buttonPanel.add(btnMoverArchivo);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCambiarUsuario);
        buttonPanel.add(btnBorrarSistema);
        


        // PANEL DE INFORMACIÓN
        JPanel infoPanel = new JPanel();
        lblModo = new JLabel("🔑 Modo: Administrador");
        lblInfo = new JLabel("📂 Seleccione un archivo o directorio.");
        infoPanel.add(lblModo);
        infoPanel.add(lblInfo);

        // ORGANIZACIÓN DE PANELES
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(treeScrollPane, BorderLayout.CENTER);
        leftPanel.setBorder(BorderFactory.createTitledBorder("📂 Estructura de Archivos"));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(infoPanel, BorderLayout.SOUTH);

        add(diskPanel, BorderLayout.NORTH);
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 📌 Métodos de gestión de archivos y directorios.
     */
    private void crearArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede crear archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta del directorio (Ejemplo: / o /documentos)");
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo:");

        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Debes ingresar un nombre de archivo válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fileSize;
        try {
            fileSize = Integer.parseInt(JOptionPane.showInputDialog(this, "Ingrese el tamaño en KB:"));
            if (fileSize <= 0) {
                JOptionPane.showMessageDialog(this, "❌ El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Tamaño inválido. Ingresa un número.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.createFile(path, fileName, fileSize, "Administrador");
        actualizarInterfaz();
    }
    private void borrarSistema() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ ¿Estás seguro de que quieres borrar todo el sistema de archivos?\nEsta acción no se puede deshacer.",
                "Confirmar Borrado",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            fileSystem.borrarTodo();
            actualizarInterfaz();
            JOptionPane.showMessageDialog(this, "✅ Sistema de archivos borrado exitosamente.",
                    "Borrado Completado", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    private void crearDirectorio() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede crear directorios.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta donde se creará el directorio:");
        String dirName = JOptionPane.showInputDialog(this, "Ingrese el nombre del nuevo directorio:");

        if (dirName == null || dirName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Debes ingresar un nombre válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.createDirectory(path, dirName, "Administrador");
        actualizarInterfaz();

    }
    private void eliminarArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede eliminar archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta del archivo:");
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo a eliminar:");

        if (!fileSystem.existeArchivo(path, fileName)) {
            JOptionPane.showMessageDialog(this, "❌ El archivo '" + fileName + "' no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.deleteFile(path, fileName, "Administrador");
        actualizarInterfaz();
    }
    private void actualizarArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede actualizar archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta del archivo:");
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo a actualizar:");

        if (!fileSystem.existeArchivo(path, fileName)) {
            JOptionPane.showMessageDialog(this, "❌ El archivo '" + fileName + "' no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newContent = JOptionPane.showInputDialog(this, "Ingrese el nuevo contenido:");
        fileSystem.updateFile(path, fileName, newContent, "Administrador");
    }
    private void restaurarArchivo() { 
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo a restaurar:");
        String versionFile = JOptionPane.showInputDialog(this, "Ingrese el nombre del backup:");

        String restoredContent = fileSystem.restoreFile(fileName, "backups/" + versionFile);
        if (restoredContent != null) {
            JOptionPane.showMessageDialog(this, "✅ Archivo restaurado:\n" + restoredContent);
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ No se encontró la versión solicitada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void moverArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede mover archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pathOrigen = JOptionPane.showInputDialog(this, "Ingrese la ruta de origen:");
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo a mover:");
        String pathDestino = JOptionPane.showInputDialog(this, "Ingrese la ruta de destino:");

        if (!fileSystem.existeArchivo(pathOrigen, fileName)) {
            JOptionPane.showMessageDialog(this, "❌ El archivo '" + fileName + "' no existe en '" + pathOrigen + "'.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.moverArchivo(pathOrigen, fileName, pathDestino, "Administrador");
        actualizarInterfaz();
    }
    private void cambiarModoUsuario() { 
        isAdmin = !isAdmin;
        btnCambiarUsuario.setText(isAdmin ? "👤 Modo: Administrador" : "👤 Modo: Usuario");
        lblModo.setText(isAdmin ? "🔑 Modo: Administrador" : "🔒 Modo: Usuario");
        fileSystem.changeUserMode(isAdmin);
    }
    private void guardarSistema() { 
        fileSystem.guardarEnArchivo();
        JOptionPane.showMessageDialog(this, "✅ Sistema guardado correctamente.");     
    }
    private void actualizarInterfaz() { 
        actualizarJTree();
        actualizarTabla();
        diskPanel.actualizarDisco();   
    }
    private void actualizarTabla() {
        Object[][] data = obtenerDatosTabla();
        fileTable.setModel(new javax.swing.table.DefaultTableModel(
                data, new String[]{"Archivo", "Tamaño (KB)", "Bloques asignados"}));
    }

    /**
     * 📌 Obtener los datos de los archivos para llenar la tabla.
     */
    private Object[][] obtenerDatosTabla() {
        ListaEnlazada<FileEntry> archivos = fileSystem.getRoot().files;
        int size = archivos.contarElementos();
        Object[][] data = new Object[size][3];

        Nodo<FileEntry> actual = archivos.getCabeza();
        int i = 0;
        while (actual != null) {
            FileEntry file = actual.dato;
            data[i][0] = file.name;
            data[i][1] = file.size;
            data[i][2] = file.blocks.contarElementos();
            i++;
            actual = actual.siguiente;
        }
        return data;
    }
    private void actualizarInfoTabla(String nombre) {
        infoTableModel.setRowCount(0); // Limpiar la tabla

        DirectoryEntry root = fileSystem.getRoot();

        // Buscar archivo
        FileEntry archivo = buscarArchivoRecursivo(root, nombre);
        if (archivo != null) {
            int primerBloque = archivo.blocks.obtener(0);
            int cantidadBloques = archivo.blocks.contarElementos();

            infoTableModel.addRow(new Object[]{archivo.name, primerBloque, cantidadBloques});
            return;
        }

        // Buscar directorio
        DirectoryEntry directorio = buscarDirectorioRecursivo(root, nombre);
        if (directorio != null) {
            int tamañoTotal = calcularTamañoDirectorio(directorio);
            int primerBloque = obtenerPrimerBloqueDirectorio(directorio);

            infoTableModel.addRow(new Object[]{directorio.name, primerBloque == -1 ? "N/A" : primerBloque, tamañoTotal});
        }
    }
    
    private FileEntry buscarArchivoRecursivo(DirectoryEntry dir, String nombre) {
        Nodo<FileEntry> actualArchivo = dir.files.getCabeza();
        while (actualArchivo != null) {
            if (actualArchivo.dato.name.equals(nombre)) {
                return actualArchivo.dato;
            }
            actualArchivo = actualArchivo.siguiente;
        }

        Nodo<DirectoryEntry> actualDir = dir.subDirectories.getCabeza();
        while (actualDir != null) {
            FileEntry encontrado = buscarArchivoRecursivo(actualDir.dato, nombre);
            if (encontrado != null) return encontrado;
            actualDir = actualDir.siguiente;
        }

        return null;
    }

    private DirectoryEntry buscarDirectorioRecursivo(DirectoryEntry dir, String nombre) {
        if (dir.name.equals(nombre)) return dir;

        Nodo<DirectoryEntry> actualDir = dir.subDirectories.getCabeza();
        while (actualDir != null) {
            DirectoryEntry encontrado = buscarDirectorioRecursivo(actualDir.dato, nombre);
            if (encontrado != null) return encontrado;
            actualDir = actualDir.siguiente;
        }

        return null;
    }

    private int calcularTamañoDirectorio(DirectoryEntry dir) {
        int total = 0;

        Nodo<FileEntry> archivos = dir.files.getCabeza();
        while (archivos != null) {
            total += archivos.dato.size;
            archivos = archivos.siguiente;
        }

        Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
        while (subdirs != null) {
            total += calcularTamañoDirectorio(subdirs.dato);
            subdirs = subdirs.siguiente;
        }

        return total;
    }

    private int obtenerPrimerBloqueDirectorio(DirectoryEntry dir) {
        Nodo<FileEntry> archivos = dir.files.getCabeza();
        if (archivos != null) {
            return archivos.dato.blocks.obtener(0);
        }

        Nodo<DirectoryEntry> subdirs = dir.subDirectories.getCabeza();
        while (subdirs != null) {
            int bloque = obtenerPrimerBloqueDirectorio(subdirs.dato);
            if (bloque != -1) return bloque;
            subdirs = subdirs.siguiente;
        }

        return -1; // Si no tiene archivos
    }
    /**
     * 📌 Actualizar la estructura del `JTree`.
     */
    private void actualizarJTree() {
        rootNode.removeAllChildren();
        construirArbolDesdeEstructura(rootNode, fileSystem.getRoot());
        treeModel.reload();
    }

    private void construirArbolDesdeEstructura(DefaultMutableTreeNode nodoPadre, DirectoryEntry directorio) {
        Nodo<FileEntry> actualArchivo = directorio.files.getCabeza();
        while (actualArchivo != null) {
            nodoPadre.add(new DefaultMutableTreeNode(actualArchivo.dato.name));
            actualArchivo = actualArchivo.siguiente;
        }

        Nodo<DirectoryEntry> actualDirectorio = directorio.subDirectories.getCabeza();
        while (actualDirectorio != null) {
            DefaultMutableTreeNode nodoDirectorio = new DefaultMutableTreeNode(actualDirectorio.dato.name);
            nodoPadre.add(nodoDirectorio);
            construirArbolDesdeEstructura(nodoDirectorio, actualDirectorio.dato);
            actualDirectorio = actualDirectorio.siguiente;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileSystemGUI gui = new FileSystemGUI();
            gui.setVisible(true);
        });
    }
}