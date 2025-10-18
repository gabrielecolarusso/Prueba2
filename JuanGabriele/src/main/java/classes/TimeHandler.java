/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;
import main.*;
/**
 *
 * @author DELL
 */
public class TimeHandler {
    private int instructionTime;
    private W1 window;
    public TimeHandler(W1 window) {
        this.instructionTime = 5000;
        this.window = window;
    }

    public int getInstructionTime() {
        return window.getTime();
    }

    public void setInstructionTime(int instructionTime) {
        this.instructionTime = instructionTime;
    }
    
}
