package network.simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Router {
    String name;
    ArrayList<Port> ports;
    HashMap<String,Gateway> routerTable; // subNet , Gateway
    
    public Router(String name, ArrayList<Port> ports)
    {
        this.name = name;
        this.ports = ports;
        routerTable = new HashMap<>();
    }
    
    public String getName () { return name; }
   
    public void addTableEntry (String dest, Gateway gateway) { routerTable.put(dest,gateway); }
    
    public void addPort (Port port) { ports.add(port); }
     
    public Gateway verifyRouterTable (String IP) {
        
        String subNet = "";
        int dotCounter = 0;
        int size = 0;
        
        for (int i = 0; i < IP.length(); i++)
        {
            if (IP.charAt(i) == '.') dotCounter = dotCounter + 1;
            if (dotCounter == 3)
            {
                size = i;
                break;
            }
        }
        
        subNet = IP.substring(0, size) + ".0";
                              
        return routerTable.get(subNet);
    }
}
