import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;

public class Item {
    private String name;
    private double weight;
    public Item(String object, double weight){
        name = object.toLowerCase();
        this.weight = weight;
    }
    
    public double getWeight(){
        return weight;
    }
    
    public String getName(){
        return name;
    }
    
    public String toString(){
        return name + "\t\t\t" + weight + "kg";
    }
}