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
        
        String subNet;
        int range = 0;
        
        System.out.println("Verifying: " + IP);
        
        for (int i = 0; i < IP.length(); i++)
        {
            if (IP.charAt(i) == '.')
            { 
                range = Integer.parseInt(IP.substring(0,i));
                break;
            }
        }
        
        
        int dotCounter = 0;
        int limit = 0;
        int size = 0;
        
        if ( range < 128 ) limit = 1;
        else if ( 128 < range && range < 192) limit = 2;
        else limit = 3;
        
        for (int i = 0; i < IP.length(); i++)
        {
            if (IP.charAt(i) == '.') dotCounter = dotCounter + 1;
            if (dotCounter == limit)
            {
                size = i;
                break;
            }
        }
        
        switch (limit) {
            case 1:
                subNet = IP.substring(0, size) + ".0.0.0";
                break;
            case 2:
                subNet = IP.substring(0, size) + ".0.0";
                break;
            default:
                subNet = IP.substring(0, size) + ".0";
                break;
        }
                              
        return routerTable.get(subNet);
    }
}
