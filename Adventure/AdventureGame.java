// This program is copyright VUW.
// You are granted permission to use it to construct your answer to an ENGR110 assignment.
// You may not distribute it in any other way without permission.
/* Code for COMP110 Assignment

 * Name:
 * Usercode:
 * ID:
 */
 
import ecs100.*;
import java.awt.Color;
import java.util.*;
import java.io.*;


/** AdventureGame   */
public class AdventureGame{

    //----------Fields -------------------
    
    private Player player;
    private Pod pod, dockingPod, currentPod;
    private Trap trapInPod;
    private Item item;
    private List<Pod> allPods;
    private List<Item> playerItems;
    //
    private boolean hasTrap;
    
    //---------- Constructor and interface ------------------
    /** Construct a new AdventureGame object and initialise the interface */
    public AdventureGame(){
        allPods = new ArrayList<Pod>();
        playerItems = new ArrayList<Item>();        // delete
        
        UI.initialise();
        UI.addButton("Check Health", this::doHealth);
        UI.addButton("List Pack", this::doList);
        UI.addButton("Portal A", ()->{this.goPortal(0);});
        UI.addButton("Portal B", ()->{this.goPortal(1);});
        UI.addButton("Portal C", ()->{this.goPortal(2);});
        UI.addButton("Disable Trap", this::doDisable);
        UI.addButton("Look", this::doLook);
        UI.addButton("Search", this::doSearch);
        UI.addButton("Pickup", this::doPickUp);
        UI.addButton("PutDown", this::doPutDown);
        UI.addButton("Use Kit", this::useRecoveryKit);
        UI.addButton("Quit", UI::quit);
        /** DELETE BEFORE SUBMISSION */
        UI.addButton("Defuser", this::giveDef);
        UI.addButton("Cache", this::findCache);

        UI.setDivider(1.0);  //show only the text pane
        this.initialiseGame();
    }

    //----------- Methods to respond to buttons ----------------
    
    /** Prints out the Player's current health*/
    public void doHealth(){
        if ( !player.isAlive() ){ return;}
        
        UI.println("Health: " + player.getHealth());
    }
    
    /** List the items in the player's pack */
    public void doList(){
        if ( !player.isAlive() ){ return;}
        
        if(player.isBagEmpty()){
            UI.println("There are no items in your bag\n");
            return;
        }
        player.listItems();
    }

    /** Exit the current pod going through the specified portal number */
    public void goPortal(int num){
        if ( !player.isAlive() ){ return; }
        if(currentPod.getNextPod(num) == null) {
            UI.println("That Portal does not lead to another Pod");
            return;
        }
        
        String s = "You decided to go through Portal ";
        switch(num){
            case 0:
                s += "A";
                break;
            case 1:
                s += "B";
                break;
            case 2:
                s += "C";
                break;
        }
        currentPod.enableKit();
        currentPod = currentPod.getNextPod(num);
        UI.println(s + "\nYou are now in Pod: " + currentPod.getName());        
        if(currentPod.getName().equals("Docking Pod") && player.hasCache()){
            UI.println("YOU WIN!");
            return;
        }
        /** Pod connections */
        
        /** info about pod check trap */
        trapInPod = currentPod.getTrap();
        if(trapInPod != null){
            if(trapInPod.isActive()){
                UI.println("There is an active Trap");
                UI.println("You have to disable it before looking around the Pod if you have the right items\n");
                /**  DELETE   */
                //trapInPod.desc();
            }
            else UI.println("There is a disabled Trap\n");
        }
    }

    /** Look around at the pod and report what's there (except for datacache)*/
    public void doLook(){
        if (!player.isAlive() || checkTrap()){ return; }
        
        UI.println("You look around the Pod");
        UI.println(currentPod.getDescription() + "\n");
    }

    /** Search for the data cache, and pick it up if it is found.
     *  If the player has a torch, then there is a higher probability of
     *  finding the datacache (assuming it is in the pod) than if the player
     *  doesn't have a torch.
     */
    public void doSearch(){
        if (!player.isAlive() || checkTrap() ){
            return;
        } else if( player.hasCache()){ 
            UI.println("You have already found the data cache\n");
            return;
        }
        
        int chance = (int)(Math.random() * 100);
        if( currentPod.getCache( player.getChance() ) ){
            player.setCache();
            UI.println("You found the data cache!");
            UI.println("Go back to the Docking Pod to finish your mission\n");
            return;
        }
        UI.println("You can't seem to find the cache.\nTry searching again or try a different Pod\n");
    }

    /** Attempt to pick up an item from the pod and put it in the pack.
     *  If item makes the pack too heavy, then puts the item back in the pod */
    public void doPickUp(){
        if (!player.isAlive() || checkTrap()){ return;}
        
        UI.println(currentPod.printItems());
        if(currentPod.isPodEmpty()){
            return;
        }
        String a = UI.askString("What item would you like to pick up? ");
        // if there is an item in the pod that conatins the String A
        Item add = currentPod.getItem(a);
        if(add != null){
            if(player.addItem(add)){
                currentPod.removeItem(add);
                UI.println(add.getName() + " has been added to your inventory");
                if(add.getName().equals("torch")) UI.println("Now you have a higher chance of finding the cache");
            } else {
                UI.println(add.getName() + " cannot be added to your inventory");
            }
            UI.println("Your bag weighs "+ player.getWeight() + "kg\n");
        } else {
            UI.println("This item cannot be added to your inventory. Try again or put down some items for your inventory\n");
        }
    }

    /** Attempt to put down an item from the pack. */
    public void doPutDown(){
        if (!player.isAlive() || checkTrap()){ return;}
        if(player.isBagEmpty()) {
            UI.println("There are no items in your bag");
            return;
        }
        doList();
        String a = UI.askString("What item would you like to put down? ");
        Item remove = player.removeItem(a);
        if(remove != null){
            currentPod.addItem(remove);
            UI.println(remove.getName() + " has been removed from your inventory");
        } else {
            UI.println("This item cannot be removed from your inventory");
        }
        UI.println("Your bag weighs "+ player.getWeight() + "kg\n");
    }                

    /** Attempt to disable the trap in the current pod.
     * If there is no such trap, or it is already disabled, return immediately.
     * If disabling the trap with the players current pack of items doesn't work,
     *  the player is damaged. If their health is now <=0, then the game is over
     */
    public void doDisable(){
        if (!player.isAlive() || trapInPod == null ){ 
            UI.println("There is no trap to disable\n");
            return;
        } else if(!trapInPod.isActive()){
            UI.println("The trap is already disabled\n");
            return;
        }
        
        for(int i=0; i<player.getBag().size(); i++){
            // if trap is disabled
            if( !trapInPod.disableTrap( player.getBag().get(i) )){
                return;
            }
        }
        UI.println("You don't have any items that can disable this trap");
        player.hit();
        doHealth();
        if(!player.isAlive()) UI.println("You have died. GAME OVER");
    }

    /** If there is a recovery kit in the pod that hasn't been already used on
     *  this visit, then use it (increase the player's health) and remember that
     *  the kit has now been used.
     */
    public void useRecoveryKit(){
        if (!player.isAlive() || checkTrap()){ return;}
        
        if(!currentPod.hasKit()){
            UI.println("There is no recovery kit in this Pod\n");
            return;
        }
        // set kit to false
        if(currentPod.isKitUsed()){            
            player.heal();
            doHealth();
            currentPod.disableKit();
            return;
        }
        UI.println("You've already used the recovery kit\n");
    }
    
    /**     DELETE BEFORE SUB   **/
    public void giveDef(){        
        player.addItem( new Item("defuser", 5.5));
    }
    
    public void findCache(){        
        UI.println(currentPod.hasCache());
    }

    // ------------ Utility methods ---------------------------
    /** Check if there is an active trap. If so, set it off and damage the player.
     *  Returns true if the player got damaged. 
     */
    private boolean checkTrap(){        
        if(trapInPod != null && trapInPod.isActive() && player.isAlive()){
            player.hit(); // already has the println
            doHealth();
            return true;
        }
        return false;
    }



    // ---------- Initialise -------------------------

    /** Intialise all the pods in the game and the player
     *  YOU DO NOT NEED TO USE THIS METHOD - YOU CAN REPLACE IT WITH YOUR OWN
     *  The code provided is a pretty simple initialisation process.
     *  It makes assumptions about the constructors and some methods for other classes.
     *  You will need to change it if it doesn't fit with the rest of your design
     *  It reads the pod descriptions from the game-data.txt file, and connects
     *  them in a circle, with random cross-links. 
     *  It then reads descriptions of the traps (and the items that disable them),
     *   makes Trap objects and Item objects,
     *   puts the Traps and the items in random pods 
     *  Puts a torch Item into one of the pods
     *  Makes a player
     *  Assumes constructors for Pod, Player, Item, and Trap
     *  Assumes allPods field, and several methods on traps, pods, and items
     *   You will need to modify the code if you have different constructors and methods.
     */
    public void initialiseGame(){
        Scanner data = null;
        try{
            //create pods from game-data file: 
            data = new Scanner(new File("game-data.txt"));
            //ignore comment lines, (starting with '# ')
            while (data.hasNext("#")){data.nextLine();}
            //read number of pods
            int numPods = data.nextInt(); data.nextLine();
            //read  name, has-recovery-kit,  has-data-cache
            for (int i=0; i<numPods; i++){
                // ASSUMES a Pod constructor!!!
                String podName = data.nextLine().trim();
                boolean hasRecoveryKit = data.nextBoolean();
                boolean hasDataCache = data.nextBoolean();
                Pod pod = new Pod(podName, hasRecoveryKit, hasDataCache);                //**MAY NEED TO CHANGE***
                allPods.add(pod);
                data.nextLine();
            }
            dockingPod = allPods.get(0);
            // connect them in circle, to ensure that there is a path
            for (int i=0; i<numPods; i++){
                Pod pod1 = allPods.get(i);
                Pod pod2 = allPods.get((i+1)%numPods);
                // ASSUMES one-way connections
                pod1.addPortalTo(pod2);               //**MAY NEED TO CHANGE***
            }
            // connect each pod to two random other pods.
            for (Pod pod : allPods){
                Pod podB = allPods.get((int)(Math.random()*allPods.size()));
                Pod podC = allPods.get((int)(Math.random()*allPods.size()));
                pod.addPortalTo(podB);               //**MAY NEED TO CHANGE***
                pod.addPortalTo(podC);               //**MAY NEED TO CHANGE***
            }
            UI.printf("Created %d pods\n", allPods.size());

            // Read trap name and items to disable trap, to make Traps and Item
            ArrayList<Trap> traps = new ArrayList<Trap>();
            ArrayList<Item> items = new ArrayList<Item>();
            while (data.hasNext()){
                //trap name, followed by number of items to disable trap,
                //followed by items (weight, name)
                String trapName = data.nextLine().trim();
                ArrayList<Item> itemsForTrap = new ArrayList<Item>();
                int numItems = data.nextInt(); data.nextLine();
                for (int i=0; i<numItems; i++){
                    double weight = data.nextDouble();
                    String itemName = data.nextLine().trim();
                    // ASSUMES Item contructor:
                    Item it = new Item(itemName, weight);             //**MAY NEED TO CHANGE***
                    itemsForTrap.add(it);
                }
                // ASSUMES Trap contructor:
                Trap trap = new Trap(trapName, itemsForTrap);         //**MAY NEED TO CHANGE***
                traps.add(trap);
                items.addAll(itemsForTrap);
            }
            data.close();

            // ASSUMES Item contructor:
            items.add(new Item("Torch", 0.4));
            //put the traps in random rooms (other than the dockingPod)
            //but not in rooms that already have a trap
            UI.printf("Created %d traps and %d items\n", traps.size(), items.size());
            while (!traps.isEmpty()){
                Pod pod = allPods.get(1+(int) (Math.random()*numPods-1));
                // ASSUMES methods on Pod
                if (pod.getTrap() == null){               //**MAY NEED TO CHANGE***
                    pod.setTrap(traps.remove(0));         //**MAY NEED TO CHANGE***
                }
            }
            //put the Items in random rooms.
            while (!items.isEmpty()){
                Pod pod = allPods.get((int) (Math.random()*numPods));
                // ASSUMES method on Pod
                pod.addItem(items.remove(0));            //**MAY NEED TO CHANGE***
            }
            //
            UI.printf("added traps and items to Pods\n");
            currentPod = dockingPod;
            player = new Player();                       //**MAY NEED TO CHANGE***
            UI.println("You are at the Docking Pod");
            UI.println("Your mission is to find the data cache and bring it back to the Docking Pod");
            // ASSUMES method on Pod
            UI.println(currentPod.getDescription());     //**MAY NEED TO CHANGE***
           }
        catch(InputMismatchException e){UI.println("Wrong type of data at: " + data.nextLine());}
        catch(IOException e){UI.println("Failed to read data correctly:\n" + e);}
    }

    public static void main(String[] args){
        new AdventureGame();
    }
}