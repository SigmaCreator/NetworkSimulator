package network.simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    String name;
    ArrayList<Port> ports;
    HashMap<String,Gateway> routerTable; // subNet , Gateway
    
    public Router(String name)
    {
        this.name = name;
        this.ports = new ArrayList<>();
        routerTable = new HashMap<>();
    }
    
    public String getName () { return name; }
   
    public void addTableEntry (String dest, Gateway gateway) { routerTable.put(dest,gateway); }
    
    public void addPort (Port port) { ports.add(port); }
     
    public Gateway verifyRouterTable (String IP) {
        return null;
    }
}
