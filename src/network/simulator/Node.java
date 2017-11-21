package network.simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Node extends Host {
    
    String gateway;
    
    public Node (String name, String MAC, String IP, int MTU, String gateway, String mask)
    {
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
        this.MTU = MTU;
        this.gateway = gateway;
        arpTable = new HashMap<>();
        this.subNet = mask + ".0";
        this.broadcast = mask + ".255";
        buffer = new StringBuffer();
    }
    
    public ArrayList<Message> writeMessage (Message message, boolean moreFragments) {
        
        if (message instanceof ICMP && message.recipient.equals(IP)) // If the recipient is me
        {
            if (moreFragments) // If there are more fragments on the way
            { 
                buffer.append(message.data);
                return null;
            }
            else 
            {
                buffer.append(message.data);
                    
                if (message.operation == Operation.REQUEST) // If it was an ICMP REQUEST
                {
                    return shatter(IP, message.sender, buffer.toString(), Operation.REPLY);
                } 
                else if (message.operation == Operation.REPLY) 
                { 
                    return null;
                }            
            }
        }
        else if (message instanceof ARP && message.operation == Operation.REQUEST && message.data.equals(IP))
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
        
        if (hasMACOf(gateway) != null) return shatter(message.sender, message.recipient, message.data, Operation.REQUEST);
        else
        {
            Message request = new ARP (IP, broadcast, Operation.REQUEST);
            request.data = gateway;
            
            ArrayList<Message> list = new ArrayList<>();
            list.add(request);
            return list;
        }
    }
    
    private ArrayList<Message> shatter (String sender, String recipient, String data, Operation op)
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
                    System.out.println("New ICMP fragment - Sender : " + frag.sender + " | Recipient : " + frag.recipient + " | Data : " + frag.data);
                fragments.add(frag);
            }
                        
            return fragments;
    }

    String getGatewayIP() { return gateway; }
}
