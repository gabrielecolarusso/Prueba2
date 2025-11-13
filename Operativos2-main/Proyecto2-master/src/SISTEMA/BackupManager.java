/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

/**
 *
 * @author yarge
 */
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupManager {
    private static final String BACKUP_DIR = "backups/";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public static void guardarVersion(String fileName, String content) {
        try {
            File dir = new File(BACKUP_DIR);
            if (!dir.exists()) dir.mkdir();

            String timestamp = LocalDateTime.now().format(FORMATTER);
            String backupFileName = BACKUP_DIR + fileName + "_" + timestamp + ".txt";

            try (FileWriter writer = new FileWriter(backupFileName)) {
                writer.write(content);
            }

            registrarBackup(fileName, backupFileName);
            System.out.println("📜 Backup guardado: " + backupFileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String restaurarVersion(String fileName, String backupFile) {
        File file = new File(backupFile);
        if (!file.exists()) return null;

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private static void registrarBackup(String fileName, String backupFile) {
        try (FileWriter writer = new FileWriter("backup_log.txt", true)) {
            writer.write(LocalDateTime.now().format(FORMATTER) + " - Backup de " + fileName + ": " + backupFile + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}