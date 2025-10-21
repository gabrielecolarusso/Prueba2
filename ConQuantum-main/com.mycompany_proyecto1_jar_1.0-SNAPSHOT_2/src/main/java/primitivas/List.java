/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package primitivas;

public class List <T>{
    NodoList<T> head;
    NodoList<T> last;
    int size;

    public int getSize() {
        return size;
    }

    public NodoList<T> getLast() {
        return last;
    }
    
    public List() {
        head = null;
        last = null;
        size = 0;
    }
    
    // CORREGIDO: isEmpty debe devolver true cuando head ES null
    public boolean isEmpty(){
        return head == null;
    }
    
    public void delete(NodoList pDelete){
        if(!this.isEmpty()){
            NodoList pAux = head;
            if(head.getValue() != pDelete.getValue()){
                head = head.getpNext();
                delete(pDelete);
                appendFirst(pAux);
            }else{
                head = pDelete.getpNext();
                pDelete.setpNext(null);
                size--;
                if(size == 0){
                    last = null;
                }
            }
        }else{
            head = last = null;
        }
    }
    
    public void appendFirst(T x){
        NodoList <T> pNew = new NodoList <T> (x);
        if(!isEmpty()){
            pNew.setpNext(head);
            head = pNew;
        }
        else{
            head = last = pNew;
            pNew.setpNext(null);
        }
        size++;
    }
    
    public void appendFirst(NodoList pNew){
        if(!isEmpty()){
            pNew.setpNext(head);
            head = pNew;
        }
        else{
            head= last = pNew;
            pNew.setpNext(null);
        }
        size++;
    }
    
    public void appendLast(NodoList pNew){
        if(!isEmpty()){
            last.setpNext(pNew);
            last = pNew;
            pNew.setpNext(null);
        }
        else{
            head = last = pNew;
            pNew.setpNext(null);
        }
        size++;
    }
    
    public void appendLast(T x){
        NodoList <T> pNew = new NodoList <T> (x);
        if(!isEmpty()){
            last.setpNext(pNew);
            last = pNew;
            pNew.setpNext(null);
        }
        else{
            head = last = pNew;
            pNew.setpNext(null);
        }
        size++;
    }

    public NodoList getHead() {
        return head;
    }
    
    public String showAttribute(){
        NodoList pAux = head;
        String output = "[";
        while(pAux != null){
            output += pAux.getValue() + ",";
            pAux = pAux.getpNext();
        }
        output += "]";
        return output;
    }
    
    public NodoList getNodoById(int k){
        NodoList pAux = head;
        if(k >= this.size){
            return null;
        }else{
            for(int i = 0; i <= k - 1; i++){
                pAux = pAux.getpNext();
            }
            return pAux;
        }
    }
}