import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

public class Trap{
    private String name;
    private boolean enabled, on;
    private List<Item> defusers;
    public Trap(String n, ArrayList<Item> items) {
        name = n;
        defusers = items;
        enableTrap();
    }
    
    public boolean isActive(){
        return enabled;
    }
    
    public void enableTrap(){
        enabled = true;
    }
    
    public boolean disableTrap(Item i){
        if(defusers.contains(i)){
            enabled = false;
            UI.println("You used " + i.getName() + " to disable the trap");
        }
        return enabled;
    }
    /** DELETE  */
    public void desc(){
        for(Item i:defusers){
            UI.println(i.toString());
        }
    }
}