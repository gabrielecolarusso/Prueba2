/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SISTEMA;

import EDD.ListaEnlazada;
import EDD.Nodo;
import java.util.HashMap;

public class BufferCache {
    private int capacity;
    private HashMap<Integer, CacheBlock> cache;
    private ListaEnlazada<Integer> accessOrder; // Para LRU
    private HashMap<Integer, Integer> accessCount; // Para LFU
    private CachePolicy policy;
    private int hits;
    private int misses;

    public BufferCache(int capacity, CachePolicy policy) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.accessOrder = new ListaEnlazada<>();
        this.accessCount = new HashMap<>();
        this.policy = policy;
        this.hits = 0;
        this.misses = 0;
    }

    public CacheBlock get(int blockNumber) {
        if (cache.containsKey(blockNumber)) {
            hits++;
            updateAccess(blockNumber);
            return cache.get(blockNumber);
        }
        misses++;
        return null;
    }

    public void put(int blockNumber, String fileName, byte[] data) {
        if (cache.size() >= capacity && !cache.containsKey(blockNumber)) {
            evict();
        }

        CacheBlock block = new CacheBlock(blockNumber, fileName, data);
        cache.put(blockNumber, block);
        updateAccess(blockNumber);
    }

    private void updateAccess(int blockNumber) {
        switch (policy) {
            case LRU:
                updateLRU(blockNumber);
                break;
            case LFU:
                updateLFU(blockNumber);
                break;
            case FIFO:
                updateFIFO(blockNumber);
                break;
        }
    }

    private void updateLRU(int blockNumber) {
        // Eliminar si ya existe
        Nodo<Integer> actual = accessOrder.getCabeza();
        while (actual != null) {
            if (actual.dato.equals(blockNumber)) {
                accessOrder.eliminar(blockNumber);
                break;
            }
            actual = actual.siguiente;
        }
        // Agregar al final (más recientemente usado)
        accessOrder.agregar(blockNumber);
    }

    private void updateLFU(int blockNumber) {
        accessCount.put(blockNumber, accessCount.getOrDefault(blockNumber, 0) + 1);
    }

    private void updateFIFO(int blockNumber) {
        if (!accessOrder.contiene(blockNumber)) {
            accessOrder.agregar(blockNumber);
        }
    }

    private void evict() {
        Integer blockToEvict = null;

        switch (policy) {
            case FIFO:
            case LRU:
                blockToEvict = accessOrder.obtener(0);
                accessOrder.eliminar(blockToEvict);
                break;
            case LFU:
                blockToEvict = findLFU();
                accessCount.remove(blockToEvict);
                break;
        }

        if (blockToEvict != null) {
            cache.remove(blockToEvict);
        }
    }

    private Integer findLFU() {
        Integer minBlock = null;
        int minCount = Integer.MAX_VALUE;

        for (Integer block : cache.keySet()) {
            int count = accessCount.getOrDefault(block, 0);
            if (count < minCount) {
                minCount = count;
                minBlock = block;
            }
        }
        return minBlock;
    }

    public void clear() {
        cache.clear();
        accessOrder = new ListaEnlazada<>();
        accessCount.clear();
        hits = 0;
        misses = 0;
    }

    public int getSize() {
        return cache.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public double getHitRate() {
        int total = hits + misses;
        return total == 0 ? 0 : (double) hits / total * 100;
    }

    public CachePolicy getPolicy() {
        return policy;
    }

    public void setPolicy(CachePolicy policy) {
        this.policy = policy;
        clear();
    }

    public HashMap<Integer, CacheBlock> getCache() {
        return cache;
    }
}