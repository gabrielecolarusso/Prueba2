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
    public boolean isEmpty(){
        return head!=null;
    }
    
    public void delete(NodoList pDelete){
//        System.out.println(this.isEmpty());
         if(this.isEmpty()){
             NodoList pAux = head;
             if(head.getValue() != pDelete.getValue()){
                 head = head.getpNext();
                 delete(pDelete);
                 appendFirst(pAux);
             }else{
                 head = pDelete.getpNext();
//        System.out.println(this.isEmpty());
                 pDelete.setpNext(null);
                 size--;
             }
         }else{
             head = last = null;
         }
     }
    
    public void appendFirst(T x){
        NodoList <T> pNew = new NodoList <T> (x);
        if(isEmpty()){
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
//        NodoList <T> pNew = new NodoList <> (x);
        if(isEmpty()){
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
//        NodoList pNew = new NodoList (x);
//        NodoList <T> pNew = new NodoList <> (x);
        if(isEmpty()){
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
//        NodoList pNew = new NodoList (x);
        NodoList <T> pNew = new NodoList <T> (x);
        if(isEmpty()){
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
        if(k>=this.size){
            return null;
        }else{
            for(int i= 0;i<=k-1; i++){
                pAux = pAux.getpNext();
            }
            return pAux;
        }
    }
//    public void appendFirst(NodoList pNew){
////        NodoList pNew = new NodoList (x);
//        if(isEmpty()){
//            pNew.setpNext(head);
//            head = pNew;
//        }
//        else{
//            head = pNew = last;
//        }
//        size++;
//    }

//    public void setHead(NodoList head) {
//        this.head = head;
//    }
    
//    public String showAttributes(){
//        NodoList pAux = head;
//        String output = "[";
//        while(pAux != null){
//            output += pAux.getInfo() + ",";
//            pAux = pAux.getpNext();
//        }
//        output += "]";
//        return output;
//    }
//        NodoList pAux;
//    public void delete(NodoList pDele){
//        if(this.isEmpty()){
//            NodoList<T> pAux = this.getHead();
//            if(pAux != pDele){
//                head = pAux.getpNext();
//                delete(pDele);
//                appendFirst(pAux.getValue());
//            }
//            else{
//                head = pDele.getpNext();
//                pDele.setpNext(null);
//                size--;
//            }
//        }
}
