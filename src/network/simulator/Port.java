package network.simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Port extends Host
{
    Router owner;
    Port(String name, String MAC, String IP, int MTU) 
    {
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
        this.MTU = MTU;
        this.arpTable = new HashMap<>();
        buffer = new StringBuffer();
        
        setSubNet();
        System.out.println(subNet);
    } 
    
    public void setRouter (Router owner) { this.owner = owner;}
    
    public ArrayList<Message> writeMessage (Message message, String nextHopIP) {
        
        if (message instanceof ARP && message.operation == Operation.REQUEST && message.data.equals(IP))
        {
            Message reply = new ARP (IP, message.sender, Operation.REPLY);
            reply.data = MAC;
            
            ArrayList<Message> list = new ArrayList<>();
            list.add(reply);
            return list;
        }
        else if (message instanceof ARP && message.operation == Operation.REPLY)
        {
            updateTable(message.sender,message.data);
            return null;
        }
        else // message instanceof ICMP
        {
            String gateway = owner.verifyRouterTable(IP).IP;
        
            if (gateway.equals("0.0.0.0")) gateway = nextHopIP;
        
            System.out.println("Next gateway : " + gateway);
        
            if (hasMACOf(gateway) != null) return shatter(message.sender, message.recipient, message.data, message.operation, message.moreFragments);
            else
            {
                Message request = new ARP (IP, broadcast, Operation.REQUEST);
                request.data = gateway;
            
                ArrayList<Message> list = new ArrayList<>();
                list.add(request);
                return list;
            }
        }
        
    }
    
    private ArrayList<Message> shatter (String sender, String recipient, String data, Operation op, boolean moreFragments)
    {
        String content = data;
            ArrayList<String> pieces = new ArrayList<>();
            String piece = "";
            
            for (int i = 0; i < content.length(); i += MTU) 
            {
                piece = content.substring(i, Math.min(i + MTU, content.length()));
                    System.out.println("Offset " + i + " : " + piece);
                pieces.add(piece);
            }
            
            ArrayList<Message> fragments = new ArrayList<>();
            Message frag = null;
            
            for (String p : pieces)
            {
                frag = new ICMP (sender, recipient, op);
                frag.data = p;
                frag.moreFragments = true;
                    System.out.println("New ICMP fragment - Sender : " + frag.sender + " | Recipient : " + frag.recipient + " | Data : " + frag.data);
                fragments.add(frag);
            }
            
            fragments.get(fragments.size()-1).moreFragments = moreFragments;
            
            return fragments;
    }
}
