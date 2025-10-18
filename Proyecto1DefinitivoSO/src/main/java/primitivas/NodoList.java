/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package primitivas;

/**
 *
 * @author DELL
 * @param <T>
 */
public class NodoList <T>{
    private NodoList pNext;
    private T value;

    public NodoList(T value) {
        this.pNext = null;
        this.value = value;
    }

    public NodoList getpNext() {
        return pNext;
    }

    public void setpNext(NodoList pNext) {
        this.pNext = pNext;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T  value) {
        this.value = value;
    }
}
