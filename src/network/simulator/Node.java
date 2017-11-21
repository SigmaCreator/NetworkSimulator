package network.simulator;

import java.util.ArrayList;
import java.util.HashMap;

public class Node extends Host {
    
    String gateway;
    
    public Node (String name, String MAC, String IP, int MTU, String gateway)
    {
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
        this.MTU = MTU;
        this.gateway = gateway;
        arpTable = new HashMap<>();
        buffer = new StringBuffer();
        
        setSubNet();
        System.out.println(subNet);
    }
    
    public ArrayList<Message> writeMessage (Message message) {
        
        if (message instanceof ICMP && message.recipient.equals(IP)) // If the recipient is me
        {
            if (message.moreFragments) // If there are more fragments on the way
            { 
                buffer.append(message.data); // Get data from the message
                return null;
            }
            else // There are no more fragments on the way
            {
                buffer.append(message.data); // Get data from the message
                    
                if (message.operation == Operation.REQUEST) // If it was an ICMP REQUEST, then I should REPLY
                {
                    return shatter(IP, message.sender, buffer.toString(), Operation.REPLY, message.moreFragments);
                } 
                else if (message.operation == Operation.REPLY) // If it was an ICMP REPLY, then I'm just gonna sit back and relax
                { 
                    return null;
                }            
            }
        }
        else if (message instanceof ARP && message.operation == Operation.REQUEST && message.data.equals(IP)) // If it's an ARP REQUEST for me
        {
            Message reply = new ARP (IP, message.sender, Operation.REPLY);
            reply.data = MAC;
            
            ArrayList<Message> list = new ArrayList<>();
            list.add(reply);
            return list;
        }
        else if (message instanceof ARP) // If it's an ARP REQUEST (not for me) or ARP REPLY
        {
            updateTable(message.sender,message.data); // Update my ARP TABLE in case I need it later
            return null;
        }
        
        if (hasMACOf(gateway) != null) // If I have the MAC of the gateway
            return shatter(message.sender, message.recipient, message.data, Operation.REQUEST, message.moreFragments); // Send it to them
        else
        {
            Message request = new ARP (IP, broadcast, Operation.REQUEST);
            request.data = gateway;
            
            ArrayList<Message> list = new ArrayList<>();
            list.add(request);
            return list;
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

    String getGatewayIP() { return gateway; }
}
