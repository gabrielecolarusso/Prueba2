/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto2;
import javax.swing.*;
import GUI.FileSystemGUI;
/**
 *
 * @author yarge
 */
public class Proyecto2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileSystemGUI gui = new FileSystemGUI();
            gui.setVisible(true);
        });
    }
    
}
