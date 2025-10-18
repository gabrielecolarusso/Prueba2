package classes;

import main.W1;

public class EventLogger {
    private W1 window;
    private String logContent;
    
    public EventLogger(W1 window) {
        this.window = window;
        this.logContent = "";
    }
    
    public synchronized void log(String message) {
        logContent += "[Ciclo " + window.getCurrentCycle() + "] " + message + "\n";
        window.updateLog(logContent);
    }
    
    public String getLogContent() {
        return logContent;
    }
    
    public void clear() {
        logContent = "";
        window.updateLog(logContent);
    }
}