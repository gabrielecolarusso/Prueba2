/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GUI;

/**
 *
 * @author yarge
 */
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import UTILS.ColorAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import SISTEMA.SimulatedDisk;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class DiskPanel extends JPanel {
    private SimulatedDisk disk;
    private final int blockSize = 15;
    private final int blocksPerRow = 20;
    private static final String INFO_PATH = "INFO/"; 
    private static final String COLOR_FILE = INFO_PATH + "colors.json";
    private HashMap<String, Color> fileColors;

    public DiskPanel(SimulatedDisk disk) {
        this.disk = disk;
        this.fileColors = cargarColores();
        setPreferredSize(new Dimension(blocksPerRow * blockSize, (100 / blocksPerRow) * blockSize));
    }

    private HashMap<String, Color> cargarColores() {
        File file = new File(COLOR_FILE);
        if (file.exists()) {
            try (Reader reader = new FileReader(COLOR_FILE)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Color.class, new ColorAdapter()) // 📌 Se usa el adaptador
                        .create();
                return gson.fromJson(reader, new TypeToken<HashMap<String, Color>>() {}.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new HashMap<>();
    }

    private void guardarColores() {
        try (Writer writer = new FileWriter(COLOR_FILE)) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Color.class, new ColorAdapter()) // 📌 Se usa el adaptador
                    .setPrettyPrinting()
                    .create();
            gson.toJson(fileColors, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private Color obtenerColorArchivo(String fileName) {
        if (!fileColors.containsKey(fileName)) {
            Random rand = new Random();
            Color nuevoColor = new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
            fileColors.put(fileName, nuevoColor);
            guardarColores();
        }
        return fileColors.get(fileName);
    }

    public void actualizarDisco() {
        guardarColores();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        boolean[] blockMap = disk.getBlockMap();
        int totalBlocks = blockMap.length;

        for (int i = 0; i < totalBlocks; i++) {
            int x = (i % blocksPerRow) * blockSize;
            int y = (i / blocksPerRow) * blockSize;

            String archivoPropietario = disk.getArchivoPorBloque(i);
            if (archivoPropietario != null) {
                g.setColor(obtenerColorArchivo(archivoPropietario));
            } else {
                g.setColor(Color.LIGHT_GRAY);
            }

            g.fillRect(x, y, blockSize, blockSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, blockSize, blockSize);
        }
    }
}