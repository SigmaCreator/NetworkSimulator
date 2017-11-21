package network.simulator;

import java.util.HashMap;

public class Host {
    String name;
    String MAC;
    String IP;
    int MTU;
    HashMap<String,String> arpTable;
    StringBuffer buffer;
    String subNet;
    String broadcast;
    
    public String hasMACOf (String IP) 
    {
      if (arpTable.containsKey(IP)) return arpTable.get(IP);
      else return null;
    }
    
    public void updateTable (String IP, String MAC) { arpTable.put(IP,MAC); 
    }
    
}


