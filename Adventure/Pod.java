import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

public class Pod {
    private String name;
    private boolean kit, hasCache, useKit;
    private Trap trap;
    List<Pod> connectedPods = new ArrayList<Pod>();
    List<Item> itemsInPod = new ArrayList<Item>();
    public Pod(String n, boolean k, boolean cache){
        name = n;
        kit = k;
        hasCache = cache;
        if(kit) useKit = true;
        else useKit = false;
    }

    public void addPortalTo(Pod p){
        connectedPods.add(p);
    }
    
    public Trap getTrap(){
        return trap;
    }
    
    public void setTrap(Trap t){
        trap = t;
    }
    
    public boolean hasTrap(){
        return (trap != null);
    }
    
    public void enableKit(){
        if(kit) useKit = true;        
    }    
    
    public void disableKit(){
        if(kit) useKit = false;
    }
    
    public boolean isKitUsed(){
        return useKit;
    }
    
    public boolean hasKit(){
        return kit;
    }
    
    public void addItem(Item i){
        itemsInPod.add(i);
    }
    
    public void removeItem(Item i){
        itemsInPod.remove(i);
    }
    
    public boolean hasItem(Item i){
        return (itemsInPod.contains(i));
    }
    
    public boolean hasCache(){
        return hasCache;
    }
    
    public void removeCache(){
        hasCache = false;
    }
    
    public boolean getCache(int chance){
        int c = (int)(Math.random() * 100);
        if(c < chance && hasCache){
            hasCache = false;
            /***/
            UI.printf("c: %d\tchance: %d", c,chance);
            return true;
        }
        return false;
    }
    
    public Item getItem(String s){
        for(Item i: itemsInPod){
            if(i.getName().equals(s)) return i;
        }
        return null;
    }
    
    public Pod getNextPod(int num){
        Pod p = connectedPods.get(num);
        if(p == null) return null;
        return p;
    }
    
    public String getName(){
        return name;
    }
    
    public String getDescription(){
        String s = "Pod: "+ name;
        s +=  "\nThis Pod connects to: ";
        for(int i=0; i<connectedPods.size(); i++){
            s += (connectedPods.get(i).name) + ", ";
        }
        s += "\nThis Pod contains: ";
        if(!itemsInPod.isEmpty())
        for(int i=0; i<itemsInPod.size(); i++){ 
            s += "\n" + itemsInPod.get(i).toString();
        }
        else s += "no retrievable items";
        // if there is a recovery kit
        if(!kit) s += "\nThere doesn't seem to be a recovery kit in here";
        else s += "\nThere is a recovery kit that you can use";
        return s;
    }
    
    public String printItems(){
        String s = "This Pod contains: ";
        if(!itemsInPod.isEmpty())
        for(int i=0; i<itemsInPod.size(); i++){ 
            //s += "\n" + itemsInPod.toString();
            s += "\n" + itemsInPod.get(i).toString();
        }
        else s += "no retrievable items";
        return s;
    }
    
    public boolean isPodEmpty(){
        return itemsInPod.isEmpty();
    }
}