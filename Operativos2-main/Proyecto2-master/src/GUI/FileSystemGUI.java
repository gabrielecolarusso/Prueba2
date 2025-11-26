package GUI;

import SISTEMA.*;
import EDD.DirectoryEntry;
import EDD.FileEntry;
import EDD.ListaEnlazada;
import EDD.Nodo;
import javax.swing.table.DefaultTableModel;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import javax.swing.Timer;

public class FileSystemGUI extends JFrame {
    private FileSystem fileSystem;
    private JTree fileTree;
    private JTable fileTable, infoTable, processTable, bufferTable;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private JButton btnCrearArchivo, btnEliminarArchivo, btnActualizarArchivo, btnRestaurarArchivo, 
                    btnCrearDirectorio, btnMoverArchivo, btnCambiarUsuario, btnGuardar, btnBorrarSistema,
                    btnProcesarCola, btnLimpiarBuffer, btnVerGraficosBuffer;
    private JLabel lblModo, lblInfo, lblPolicy, lblQueueSize, lblBufferPolicy, lblBufferStats;
    private DiskPanel diskPanel;
    private boolean isAdmin = true;
    private JPanel infoPanel, processPanel, bufferPanel;
    private DefaultTableModel infoTableModel, processTableModel, bufferTableModel;
    private JComboBox<SchedulingPolicy> policyComboBox;
    private JComboBox<CachePolicy> cachePolicyComboBox;
    private Timer processTimer;

    public FileSystemGUI() {
        fileSystem = new FileSystem(100);
        setTitle("Simulador de Sistema de Archivos - Proyecto SO");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // MENÚ SUPERIOR
        JMenuBar menuBar = new JMenuBar();
        JMenu menuArchivo = new JMenu("Archivo");
        JMenu menuVer = new JMenu("Ver");
        
        JMenuItem menuSalir = new JMenuItem("Salir");
        menuSalir.addActionListener(e -> {
            fileSystem.guardarEnArchivo();
            System.exit(0);
        });
        
        JMenuItem menuEstadisticas = new JMenuItem("Ver Estadísticas");
        menuEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        JMenuItem menuGraficosBuffer = new JMenuItem("Ver Gráficos del Buffer");
        menuGraficosBuffer.addActionListener(e -> mostrarGraficosBuffer());
        
        menuArchivo.add(menuSalir);
        menuVer.add(menuEstadisticas);
        menuVer.add(menuGraficosBuffer);
        menuBar.add(menuArchivo);
        menuBar.add(menuVer);
        setJMenuBar(menuBar);

        // PANEL IZQUIERDO (JTree + Info)
        JPanel leftMainPanel = new JPanel(new BorderLayout());
        
        rootNode = new DefaultMutableTreeNode("root");
        treeModel = new DefaultTreeModel(rootNode);
        fileTree = new JTree(treeModel);
        JScrollPane treeScrollPane = new JScrollPane(fileTree);
        treeScrollPane.setPreferredSize(new Dimension(280, 300));
        
        JPanel leftTreePanel = new JPanel(new BorderLayout());
        leftTreePanel.setBorder(BorderFactory.createTitledBorder("📂 Estructura de Archivos"));
        leftTreePanel.add(treeScrollPane, BorderLayout.CENTER);
        
        actualizarJTree();

        // Tabla de asignación de ficheros
        infoTableModel = new DefaultTableModel(
            new String[]{"Nombre", "Bloque Inicial", "Longitud", "Color"},
            0
        );
        infoTable = new JTable(infoTableModel);
        infoTable.setEnabled(false);

        infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("📊 Tabla de Asignación"));
        infoPanel.add(new JScrollPane(infoTable), BorderLayout.CENTER);
        infoPanel.setPreferredSize(new Dimension(280, 200));

        leftMainPanel.add(leftTreePanel, BorderLayout.CENTER);
        leftMainPanel.add(infoPanel, BorderLayout.SOUTH);

        // PANEL CENTRAL (Disco + Políticas)
        JPanel centerPanel = new JPanel(new BorderLayout());
        
        // PANEL DE POLÍTICA DE PLANIFICACIÓN
        JPanel policyPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        policyPanel.setBorder(BorderFactory.createTitledBorder("⚙️ Configuración del Sistema"));
        
        JPanel diskPolicyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblPolicy = new JLabel("Política Disco: FIFO");
        policyComboBox = new JComboBox<>(SchedulingPolicy.values());
        policyComboBox.addActionListener(e -> cambiarPolitica());
        lblQueueSize = new JLabel("Procesos de I/O en cola: 0");
        
        diskPolicyPanel.add(new JLabel("📋 Planificación:"));
        diskPolicyPanel.add(policyComboBox);
        diskPolicyPanel.add(lblPolicy);
        diskPolicyPanel.add(Box.createHorizontalStrut(20));
        diskPolicyPanel.add(lblQueueSize);

        JPanel bufferPolicyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblBufferPolicy = new JLabel("Política Buffer: LRU");
        cachePolicyComboBox = new JComboBox<>(CachePolicy.values());
        cachePolicyComboBox.setSelectedItem(CachePolicy.LRU);
        cachePolicyComboBox.addActionListener(e -> cambiarPoliticaBuffer());
        lblBufferStats = new JLabel("Hits: 0 | Misses: 0 | Hit Rate: 0.0%");
        btnLimpiarBuffer = new JButton("🗑️ Limpiar Buffer");
        btnLimpiarBuffer.addActionListener(e -> limpiarBuffer());
        
        bufferPolicyPanel.add(new JLabel("💾 Buffer:"));
        bufferPolicyPanel.add(cachePolicyComboBox);
        bufferPolicyPanel.add(lblBufferPolicy);
        bufferPolicyPanel.add(Box.createHorizontalStrut(10));
        bufferPolicyPanel.add(lblBufferStats);
        bufferPolicyPanel.add(btnLimpiarBuffer);

        policyPanel.add(diskPolicyPanel);
        policyPanel.add(bufferPolicyPanel);

        // Panel del disco
        diskPanel = new DiskPanel(fileSystem.getDisk());
        diskPanel.setPreferredSize(new Dimension(900, 300));        
        JPanel diskContainerPanel = new JPanel(new BorderLayout());
        diskContainerPanel.setBorder(BorderFactory.createTitledBorder("💿 Simulación del Disco"));
        diskContainerPanel.add(diskPanel, BorderLayout.CENTER);

        centerPanel.add(policyPanel, BorderLayout.NORTH);
        centerPanel.add(diskContainerPanel, BorderLayout.CENTER);

        // PANEL DERECHO (Tablas de archivos, procesos y buffer)
        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        rightPanel.setPreferredSize(new Dimension(450, 600));

        // Tabla de archivos
        String[] columnNames = {"Archivo", "Tamaño (KB)", "Bloques"};
        fileTable = new JTable(new DefaultTableModel(columnNames, 0));
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        JPanel fileTablePanel = new JPanel(new BorderLayout());
        fileTablePanel.setBorder(BorderFactory.createTitledBorder("📄 Archivos en Disco"));
        fileTablePanel.add(tableScrollPane, BorderLayout.CENTER);

        // Tabla de procesos I/O
        processTableModel = new DefaultTableModel(new String[]{"PID", "Nombre", "Estado", "Operación", "Archivo"}, 0);
        processTable = new JTable(processTableModel);
        processTable.setEnabled(false);
        JScrollPane processScrollPane = new JScrollPane(processTable);
        
        processPanel = new JPanel(new BorderLayout());
        processPanel.setBorder(BorderFactory.createTitledBorder("⚙️ Cola de Procesos I/O"));
        processPanel.add(processScrollPane, BorderLayout.CENTER);

        // Tabla de buffer
        bufferTableModel = new DefaultTableModel(new String[]{"Bloque", "Archivo", "Estado"}, 0);
        bufferTable = new JTable(bufferTableModel);
        bufferTable.setEnabled(false);
        JScrollPane bufferScrollPane = new JScrollPane(bufferTable);
        
        bufferPanel = new JPanel(new BorderLayout());
        bufferPanel.setBorder(BorderFactory.createTitledBorder("💾 Estado del Buffer"));
        bufferPanel.add(bufferScrollPane, BorderLayout.CENTER);

        rightPanel.add(fileTablePanel);
        rightPanel.add(processPanel);
        rightPanel.add(bufferPanel);

        // PANEL INFERIOR (Botones)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 8, 8));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnCrearArchivo = new JButton("📁 Crear Archivo");
        btnCrearDirectorio = new JButton("📂 Crear Directorio");
        btnEliminarArchivo = new JButton("❌ Eliminar");
        btnActualizarArchivo = new JButton("✏️ Actualizar");
        btnRestaurarArchivo = new JButton("🔄 Restaurar");
        btnMoverArchivo = new JButton("➡️ Mover");
        btnGuardar = new JButton("💾 Guardar");
        btnCambiarUsuario = new JButton("👤 Administrador");
        btnBorrarSistema = new JButton("🗑️ Borrar Todo");
        btnProcesarCola = new JButton("▶️ Procesar Cola");
        btnVerGraficosBuffer = new JButton("📊 Gráficos Buffer");

        btnCrearArchivo.addActionListener(e -> crearArchivo());
        btnCrearDirectorio.addActionListener(e -> crearDirectorio());
        btnEliminarArchivo.addActionListener(e -> eliminar());
        btnActualizarArchivo.addActionListener(e -> actualizarArchivo());
        btnRestaurarArchivo.addActionListener(e -> restaurarArchivo());
        btnMoverArchivo.addActionListener(e -> moverArchivo());
        btnGuardar.addActionListener(e -> guardarSistema());
        btnCambiarUsuario.addActionListener(e -> cambiarModoUsuario());
        btnBorrarSistema.addActionListener(e -> borrarSistema());
        btnProcesarCola.addActionListener(e -> procesarCola());
        btnVerGraficosBuffer.addActionListener(e -> mostrarGraficosBuffer());
        
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
        buttonPanel.add(btnMoverArchivo);
        buttonPanel.add(btnRestaurarArchivo);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCambiarUsuario);
        buttonPanel.add(btnBorrarSistema);
        buttonPanel.add(btnProcesarCola);

        // PANEL DE INFORMACIÓN INFERIOR
        JPanel infoBottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoBottomPanel.setBorder(BorderFactory.createEtchedBorder());
        lblModo = new JLabel("🔑 Modo: Administrador");
        lblInfo = new JLabel(" | 📂 Sistema de archivos listo");
        JLabel lblNote = new JLabel(" | ℹ️ Cola de Procesos = Cola de I/O (operaciones de disco)");
        lblNote.setFont(new Font("Arial", Font.ITALIC, 11));
        lblNote.setForeground(Color.GRAY);
        infoBottomPanel.add(lblModo);
        infoBottomPanel.add(lblInfo);
        infoBottomPanel.add(lblNote);
        infoBottomPanel.add(btnVerGraficosBuffer);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        bottomPanel.add(infoBottomPanel, BorderLayout.SOUTH);

        // AGREGAR PANELES AL FRAME
        add(leftMainPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);

        // Timer para actualizar la interfaz automáticamente
        processTimer = new Timer(300, e -> {
            actualizarProcessTable();
            actualizarQueueSize();
            actualizarBufferTable();
            actualizarBufferStats();
        });
        processTimer.start();
    }

    private void cambiarPolitica() {
        SchedulingPolicy selected = (SchedulingPolicy) policyComboBox.getSelectedItem();
        fileSystem.setSchedulingPolicy(selected);
        lblPolicy.setText("Política Disco: " + selected);
    }

    private void cambiarPoliticaBuffer() {
        CachePolicy selected = (CachePolicy) cachePolicyComboBox.getSelectedItem();
        fileSystem.setCachePolicy(selected);
        lblBufferPolicy.setText("Política Buffer: " + selected);
        JOptionPane.showMessageDialog(this, "✅ Política de buffer cambiada a: " + selected + "\n⚠️ El buffer ha sido limpiado.");
    }

    private void limpiarBuffer() {
        fileSystem.getBuffer().clear();
        actualizarBufferTable();
        actualizarBufferStats();
        JOptionPane.showMessageDialog(this, "✅ Buffer limpiado correctamente.");
    }

    private void mostrarGraficosBuffer() {
        JDialog dialog = new JDialog(this, "📊 Estadísticas Gráficas del Buffer", true);
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);
        
        BufferStatsPanel statsPanel = new BufferStatsPanel(fileSystem.getBuffer());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnActualizar = new JButton("🔄 Actualizar");
        JButton btnCerrar = new JButton("✖ Cerrar");
        
        btnActualizar.addActionListener(e -> statsPanel.actualizarGraficos());
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(btnActualizar);
        buttonPanel.add(btnCerrar);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(statsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void actualizarBufferTable() {
        bufferTableModel.setRowCount(0);
        BufferCache buffer = fileSystem.getBuffer();
        
        for (Integer blockNum : buffer.getCache().keySet()) {
            CacheBlock block = buffer.getCache().get(blockNum);
            bufferTableModel.addRow(new Object[]{
                blockNum,
                block.getFileName(),
                "Cargado"
            });
        }
        
        int used = buffer.getSize();
        int capacity = buffer.getCapacity();
        bufferPanel.setBorder(BorderFactory.createTitledBorder(
            String.format("💾 Estado del Buffer (%d/%d bloques)", used, capacity)
        ));
    }

    private void actualizarBufferStats() {
        BufferCache buffer = fileSystem.getBuffer();
        lblBufferStats.setText(String.format(
            "Hits: %d | Misses: %d | Hit Rate: %.1f%%",
            buffer.getHits(),
            buffer.getMisses(),
            buffer.getHitRate()
        ));
    }

    private void mostrarEstadisticas() {
        BufferCache buffer = fileSystem.getBuffer();
        int totalArchivos = contarArchivosRecursivo(fileSystem.getRoot());
        int bloquesUsados = contarBloquesUsados();
        int bloquesLibres = fileSystem.getDisk().getTotalBlocks() - bloquesUsados;
        
        String stats = String.format(
            "═══════════════════════════════════════\n" +
            "📊 ESTADÍSTICAS DEL SISTEMA\n" +
            "═══════════════════════════════════════\n\n" +
            "📁 ARCHIVOS Y DISCO:\n" +
            "   • Archivos totales: %d\n" +
            "   • Bloques usados: %d / %d\n" +
            "   • Bloques libres: %d\n" +
            "   • Uso del disco: %.1f%%\n\n" +
            "⚙️ PROCESOS I/O:\n" +
            "   • En cola: %d\n" +
            "   • Política actual: %s\n\n" +
            "💾 BUFFER/CACHÉ:\n" +
            "   • Capacidad: %d bloques\n" +
            "   • En uso: %d bloques\n" +
            "   • Hits totales: %d\n" +
            "   • Misses totales: %d\n" +
            "   • Tasa de aciertos: %.1f%%\n" +
            "   • Política: %s\n\n" +
            "ℹ️ NOTA: La cola de procesos es la cola de I/O\n" +
            "   (todas las operaciones son de entrada/salida)",
            totalArchivos,
            bloquesUsados,
            fileSystem.getDisk().getTotalBlocks(),
            bloquesLibres,
            (bloquesUsados * 100.0 / fileSystem.getDisk().getTotalBlocks()),
            fileSystem.getProcessQueue().size(),
            fileSystem.getScheduler().getPolicy(),
            buffer.getCapacity(),
            buffer.getSize(),
            buffer.getHits(),
            buffer.getMisses(),
            buffer.getHitRate(),
            buffer.getPolicy()
        );
        
        JTextArea textArea = new JTextArea(stats);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JOptionPane.showMessageDialog(this, new JScrollPane(textArea), 
            "Estadísticas del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    private int contarArchivosRecursivo(DirectoryEntry dir) {
        int count = dir.files.contarElementos();
        Nodo<DirectoryEntry> actualDir = dir.subDirectories.getCabeza();
        while (actualDir != null) {
            count += contarArchivosRecursivo(actualDir.dato);
            actualDir = actualDir.siguiente;
        }
        return count;
    }

    private int contarBloquesUsados() {
        int count = 0;
        boolean[] blockMap = fileSystem.getDisk().getBlockMap();
        for (boolean usado : blockMap) {
            if (usado) count++;
        }
        return count;
    }

    private void procesarCola() {
        if (fileSystem.getProcessQueue().isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ No hay procesos de I/O en la cola.", "Cola vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }

        btnProcesarCola.setEnabled(false);
        btnProcesarCola.setText("⏳ Procesando...");

        Thread processThread = new Thread(() -> {
            while (!fileSystem.getProcessQueue().isEmpty()) {
                fileSystem.processNextIO();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                SwingUtilities.invokeLater(() -> actualizarInterfaz());
            }
            SwingUtilities.invokeLater(() -> {
                btnProcesarCola.setEnabled(true);
                btnProcesarCola.setText("▶️ Procesar Cola");
                JOptionPane.showMessageDialog(this, "✅ Todos los procesos I/O han sido ejecutados.", 
                                            "Procesamiento completo", JOptionPane.INFORMATION_MESSAGE);
            });
        });
        processThread.start();
    }

    private void actualizarProcessTable() {
        processTableModel.setRowCount(0);
        ListaEnlazada<IOProcess> queue = fileSystem.getProcessQueue().getQueue();
        Nodo<IOProcess> actual = queue.getCabeza();
        
        while (actual != null) {
            IOProcess p = actual.dato;
            processTableModel.addRow(new Object[]{
                "P" + p.getId(),
                p.getName(),
                p.getState().toString(),
                p.getIoRequest().getOperation().toString(),
                p.getIoRequest().getFileName()
            });
            actual = actual.siguiente;
        }
    }

    private void actualizarQueueSize() {
        int size = fileSystem.getProcessQueue().size();
        lblQueueSize.setText("Procesos de I/O en cola: " + size);
    }

    private void crearArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede crear archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta del directorio (Ejemplo: / o /documentos):");
        if (path == null) return;
        
        String fileName = JOptionPane.showInputDialog(this, "Ingrese el nombre del archivo:");
        if (fileName == null || fileName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Debes ingresar un nombre válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int fileSize;
        try {
            String input = JOptionPane.showInputDialog(this, "Ingrese el tamaño en bloques:");
            if (input == null) return;
            fileSize = Integer.parseInt(input);
            if (fileSize <= 0) {
                JOptionPane.showMessageDialog(this, "❌ El tamaño debe ser mayor a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Tamaño inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.createFile(path, fileName, fileSize, "Administrador");
        JOptionPane.showMessageDialog(this, "✅ Proceso de I/O creado. Use 'Procesar Cola' para ejecutar.", 
                                    "Proceso en cola", JOptionPane.INFORMATION_MESSAGE);
        actualizarInterfaz();
    }

    private void borrarSistema() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ ¿Estás seguro de que quieres borrar todo?\nEsta acción no se puede deshacer.",
                "Confirmar Borrado",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            fileSystem.borrarTodo();
            actualizarInterfaz();
            JOptionPane.showMessageDialog(this, "✅ Sistema borrado exitosamente.");
        }
    }

    private void crearDirectorio() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede crear directorios.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ingrese la ruta:");
        if (path == null) return;
        
        String dirName = JOptionPane.showInputDialog(this, "Ingrese el nombre del directorio:");
        if (dirName == null || dirName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "❌ Nombre inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.createDirectory(path, dirName, "Administrador");
        actualizarInterfaz();
    }

    private void eliminar() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede eliminar.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ruta del elemento:");
        if (path == null) return;
        
        String name = JOptionPane.showInputDialog(this, "Nombre del elemento (archivo o directorio):");
        if (name == null) return;

        // Verificar si es un archivo
        if (fileSystem.existeArchivo(path, name)) {
            eliminarArchivo(path, name);
            return;
        }

        // Verificar si es un directorio
        if (fileSystem.existeDirectorio(path, name)) {
            eliminarDirectorio(path, name);
            return;
        }

        JOptionPane.showMessageDialog(this, "❌ El elemento no existe.", "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void eliminarArchivo(String path, String fileName) {
        fileSystem.deleteFile(path, fileName, "Administrador");
        JOptionPane.showMessageDialog(this, "✅ Proceso de eliminación I/O creado para el archivo.", "En cola", JOptionPane.INFORMATION_MESSAGE);
        actualizarInterfaz();
    }

    private void eliminarDirectorio(String path, String dirName) {
        // Verificar que no sea root
        if (path.equals("/") && dirName.equals("root")) {
            JOptionPane.showMessageDialog(this, "❌ No se puede eliminar el directorio raíz.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠️ ¿Está seguro de eliminar el directorio '" + dirName + "'?\n" +
                "Se eliminarán todos los archivos y subdirectorios contenidos.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            fileSystem.deleteDirectory(path, dirName, "Administrador");
            actualizarInterfaz();
            JOptionPane.showMessageDialog(this, "✅ Directorio y su contenido eliminados correctamente.", "Eliminación completa", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void actualizarArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede actualizar.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String path = JOptionPane.showInputDialog(this, "Ruta del archivo:");
        if (path == null) return;
        
        String fileName = JOptionPane.showInputDialog(this, "Nombre del archivo:");
        if (fileName == null) return;

        if (!fileSystem.existeArchivo(path, fileName)) {
            JOptionPane.showMessageDialog(this, "❌ El archivo no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newContent = JOptionPane.showInputDialog(this, "Nuevo contenido:");
        if (newContent == null) return;
        
        fileSystem.updateFile(path, fileName, newContent, "Administrador");
        JOptionPane.showMessageDialog(this, "✅ Proceso de actualización I/O creado.", "En cola", JOptionPane.INFORMATION_MESSAGE);
    }

    private void restaurarArchivo() { 
        String fileName = JOptionPane.showInputDialog(this, "Nombre del archivo:");
        if (fileName == null) return;
        
        String versionFile = JOptionPane.showInputDialog(this, "Nombre del backup:");
        if (versionFile == null) return;

        String restoredContent = fileSystem.restoreFile(fileName, "backups/" + versionFile);
        if (restoredContent != null) {
            JOptionPane.showMessageDialog(this, "✅ Contenido restaurado:\n" + restoredContent);
        } else {
            JOptionPane.showMessageDialog(this, "⚠️ No se encontró el backup.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void moverArchivo() { 
        if (!isAdmin) {
            JOptionPane.showMessageDialog(this, "🚫 Solo el Administrador puede mover archivos.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pathOrigen = JOptionPane.showInputDialog(this, "Ruta origen:");
        if (pathOrigen == null) return;
        
        String fileName = JOptionPane.showInputDialog(this, "Nombre del archivo:");
        if (fileName == null) return;
        
        String pathDestino = JOptionPane.showInputDialog(this, "Ruta destino:");
        if (pathDestino == null) return;

        if (!fileSystem.existeArchivo(pathOrigen, fileName)) {
            JOptionPane.showMessageDialog(this, "❌ El archivo no existe.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        fileSystem.moverArchivo(pathOrigen, fileName, pathDestino, "Administrador");
        actualizarInterfaz();
    }

    private void cambiarModoUsuario() { 
        isAdmin = !isAdmin;
        btnCambiarUsuario.setText(isAdmin ? "👤 Administrador" : "👤 Usuario");
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
        actualizarProcessTable();
        actualizarQueueSize();
        actualizarBufferTable();
        actualizarBufferStats();
    }

    private void actualizarTabla() {
        Object[][] data = obtenerDatosTabla();
        fileTable.setModel(new DefaultTableModel(
                data, new String[]{"Archivo", "Tamaño (KB)", "Bloques"}));
    }

    private Object[][] obtenerDatosTabla() {
        ListaEnlazada<FileEntry> todosLosArchivos = new ListaEnlazada<>();
        recolectarArchivosRecursivo(fileSystem.getRoot(), todosLosArchivos);
        
        int size = todosLosArchivos.contarElementos();
        Object[][] data = new Object[size][3];

        Nodo<FileEntry> actual = todosLosArchivos.getCabeza();
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

    private void recolectarArchivosRecursivo(DirectoryEntry dir, ListaEnlazada<FileEntry> listaArchivos) {
        Nodo<FileEntry> actualArchivo = dir.files.getCabeza();
        while (actualArchivo != null) {
            listaArchivos.agregar(actualArchivo.dato);
            actualArchivo = actualArchivo.siguiente;
        }

        Nodo<DirectoryEntry> actualDir = dir.subDirectories.getCabeza();
        while (actualDir != null) {
            recolectarArchivosRecursivo(actualDir.dato, listaArchivos);
            actualDir = actualDir.siguiente;
        }
    }

    private void actualizarInfoTabla(String nombre) {
        infoTableModel.setRowCount(0);
        DirectoryEntry root = fileSystem.getRoot();

        FileEntry archivo = buscarArchivoRecursivo(root, nombre);
        if (archivo != null) {
            if (archivo.blocks.getCabeza() != null) {
                int primerBloque = archivo.blocks.obtener(0);
                int cantidadBloques = archivo.blocks.contarElementos();
                
                Color color = diskPanel.obtenerColorArchivo(archivo.name);
                String colorStr = String.format("RGB(%d, %d, %d)", 
                    color.getRed(), color.getGreen(), color.getBlue());
                
                infoTableModel.addRow(new Object[]{
                    archivo.name, 
                    primerBloque, 
                    cantidadBloques, 
                    colorStr
                });
            }
            return;
        }

        DirectoryEntry directorio = buscarDirectorioRecursivo(root, nombre);
        if (directorio != null) {
            int tamañoTotal = fileSystem.calcularTamañoDirectorio(directorio);
            int primerBloque = fileSystem.obtenerPrimerBloqueDirectorio(directorio);
            
            infoTableModel.addRow(new Object[]{
                directorio.name, 
                primerBloque == -1 ? "N/A" : primerBloque, 
                tamañoTotal,
                "Directorio"
            });
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
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            FileSystemGUI gui = new FileSystemGUI();
            gui.setVisible(true);
        });
    }
}