/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

public class IOProcess {
    private static int nextId = 1;
    private int id;
    private String name;
    private ProcessState state;
    private IORequest ioRequest;
    private String usuario;

    public IOProcess(String name, IORequest ioRequest, String usuario) {
        this.id = nextId++;
        this.name = name;
        this.state = ProcessState.NEW;
        this.ioRequest = ioRequest;
        this.usuario = usuario;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
    }

    public IORequest getIoRequest() {
        return ioRequest;
    }

    public String getUsuario() {
        return usuario;
    }

    @Override
    public String toString() {
        return "P" + id + " [" + state + "] - " + ioRequest.getOperation();
    }
}