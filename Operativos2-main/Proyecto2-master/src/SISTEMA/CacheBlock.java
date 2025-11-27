/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

public class CacheBlock {
    private int blockNumber;
    private String fileName;
    private byte[] data;
    private long timestamp;

    public CacheBlock(int blockNumber, String fileName, byte[] data) {
        this.blockNumber = blockNumber;
        this.fileName = fileName;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getData() {
        return data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setData(byte[] data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
}
