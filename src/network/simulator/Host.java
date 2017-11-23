package network.simulator;

import java.util.HashMap;

public class Host {
    String name;
    String MAC;
    String IP;
    int MTU;
    HashMap<String,String> arpTable;
    String subNet;
    String broadcast;
    
    public String hasMACOf (String IP) 
    {
      if (arpTable.containsKey(IP)) return arpTable.get(IP);
      else return null;
    }
    
    public void updateTable (String IP, String MAC) { arpTable.put(IP,MAC); }
    
    public void setSubNet ()
    {
        int range = 0;
        
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
        else if ( 128 <= range && range < 192) limit = 2;
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
                broadcast = IP.substring(0, size) + ".255.255.255";
                break;
            case 2:
                subNet = IP.substring(0, size) + ".0.0";
                broadcast = IP.substring(0, size) + ".255.255";
                break;
            default:
                subNet = IP.substring(0, size) + ".0";
                broadcast = IP.substring(0, size) + ".255";
                break;
        }
            
            
    }
    
   
}


