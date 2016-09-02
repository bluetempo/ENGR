import java.util.*;
import ecs100.*;

public class Player{
    private List<Item> bag = new ArrayList<Item>();
    private int health, chance;
    private boolean alive, cache;
    private double sum;
    public Player(){
        health = 5;
        alive = true;
        sum = 0.0;
        chance = 20;
        cache = false;
    }
    
    public Item removeItem(String s){
        for(Item i: bag){
            if(i.getName().equals(s)){
                bag.remove(i);
                sum = Math.round( (sum - i.getWeight()) * 100.0) / 100.0;
                return i;
            }
        }        
        return null;
    }
    
    public boolean addItem(Item t){
        if(t == null) return false;
        if(sum >= 10.0 || (sum + t.getWeight()) > 10.0) return false;
        if(t.getName().equals("torch")) chance = 40;        
        bag.add(t);
        // fixes decimal point issue
        sum = Math.round( (sum + t.getWeight()) * 100.0) / 100.0;
        return true;
    }
    
    public void hit(){
        UI.println("You have been damaged by the trap");
        health--;
        if(health < 1) alive = false;
    }
    
    public void heal(){
        UI.println("You used the recovery kit");
        health++;
        if(health > 5) health = 5;
    }
    
    public boolean isAlive(){
        return alive;
    }
    
    public boolean isBagEmpty(){
        return bag.isEmpty();
    }
    
    public void listItems(){
        String s = "Items you have: ";
        /*
        for(Item i:bag){
            s += "\n" + i.toString();
        }
        */
        for(int i=0; i<bag.size(); i++){
            s += "\n" + bag.get(i).toString();
        }
        UI.println(s+"\n");
    }
    
    public List<Item> getBag(){
        return bag;
    }
    
    public int getHealth(){
        return health;
    }
    
    public double getWeight(){
        return sum;
    }
    
    public int getChance(){
        return chance;
    }
    
    public void setCache(){        
        cache = true;
    }
    
    public boolean hasCache(){
        return cache;
    }
    
    public String toString(){
        String s = "Health: " + health;
        for(int i=0; i<bag.size(); i++){
            s += "\n" + bag.get(i).toString();
        }
        return s;
    }
}
