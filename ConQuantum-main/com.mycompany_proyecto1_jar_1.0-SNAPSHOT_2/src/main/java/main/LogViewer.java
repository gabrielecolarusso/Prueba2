package main;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LogViewer extends javax.swing.JFrame {
    
    public LogViewer() {
        initComponents();
        setLocationRelativeTo(null);
        loadLog();
    }
    
    private void loadLog() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("simulation_log.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            logTextArea.setText(content.toString());
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        } catch (IOException e) {
            logTextArea.setText("Error loading log file: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();
        logTextArea = new javax.swing.JTextArea();
        buttonPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Simulation Event Log");

        logTextArea.setEditable(false);
        logTextArea.setColumns(20);
        logTextArea.setRows(5);
        logTextArea.setFont(new java.awt.Font("Monospaced", 0, 12));
        scrollPane.setViewportView(logTextArea);

        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(refreshButton);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        buttonPanel.add(closeButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        setSize(new java.awt.Dimension(800, 600));
        setLocationRelativeTo(null);
    }

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {
        loadLog();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LogViewer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LogViewer().setVisible(true);
            }
        });
    }

    private javax.swing.JButton closeButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextArea logTextArea;
    private javax.swing.JButton refreshButton;
    private javax.swing.JScrollPane scrollPane;
}