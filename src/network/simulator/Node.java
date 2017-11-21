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
    }
    
    public Message [] writeMessage (Message message, boolean moreFragments) {
        
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
            updateTable(message.sender,((ARP) message).senderMAC);
            Message reply = new ARP (IP, message.sender, MAC, Operation.REPLY);
            reply.data = MAC;
            return new Message [] { reply };
        }
        else if (message instanceof ARP && message.operation == Operation.REPLY)
        {
            updateTable(message.sender,message.data);
            return null;
        }
        
        if (hasMACOf(gateway) != null) return shatter(message.sender, message.recipient, message.data, Operation.REQUEST);
        else
        {
            Message request = new ARP (IP, IP, MAC, Operation.REQUEST);
            request.data = gateway;
            return new Message [] { request };
        }
    }
    
    private Message [] shatter (String sender, String recipient, String data, Operation op)
    {
        String content = data;
            ArrayList<String> pieces = new ArrayList<>();
            String piece = "";
            
            for (int i = 0; i < content.length(); i += MTU) 
            {
                piece = content.substring(i, Math.min(i + MTU, content.length()));
                pieces.add(piece);
            }
            
            ArrayList<Message> fragments = new ArrayList<>();
            Message frag = null;
            
            for (String p : pieces)
            {
                frag = new ICMP (sender, recipient, op);
                frag.data = p;
                fragments.add(frag);
            }
            
            return (Message []) fragments.toArray();
    }

    String getGatewayIP() { return gateway; }
}
