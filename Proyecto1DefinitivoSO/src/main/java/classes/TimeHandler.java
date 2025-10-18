package classes;
import main.*;

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