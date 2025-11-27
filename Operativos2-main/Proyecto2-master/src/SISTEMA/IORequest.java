/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

public class IORequest {
    private Operation operation;
    private String path;
    private String fileName;
    private int fileSize;
    private String content;
    private int blockPosition;

    public IORequest(Operation operation, String path, String fileName, int fileSize) {
        this.operation = operation;
        this.path = path;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.blockPosition = 0;
    }

    public IORequest(Operation operation, String path, String fileName, String content) {
        this.operation = operation;
        this.path = path;
        this.fileName = fileName;
        this.content = content;
        this.blockPosition = 0;
    }

    public IORequest(Operation operation, String path, String fileName) {
        this.operation = operation;
        this.path = path;
        this.fileName = fileName;
        this.blockPosition = 0;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getPath() {
        return path;
    }

    public String getFileName() {
        return fileName;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getContent() {
        return content;
    }

    public int getBlockPosition() {
        return blockPosition;
    }

    public void setBlockPosition(int blockPosition) {
        this.blockPosition = blockPosition;
    }
}
