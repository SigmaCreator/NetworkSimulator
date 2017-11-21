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
    } 
    
    public void setRouter (Router owner) { this.owner = owner;}
    
    public Message [] writeMessage (Message message) {
        
        if (message instanceof ICMP)
        {
            return shatter(message.sender, message.recipient, message.data, message.operation);            
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
        
        String gateway = owner.verifyRouterTable(IP).IP;
        
        if (gateway.equals("0.0.0.0")) gateway = message.recipient;
        
        if (hasMACOf(message.recipient) != null) return shatter(message.sender, message.recipient, message.data, Operation.REQUEST);
        else
        {
            Message request = new ARP (IP, gateway, MAC, Operation.REQUEST);
            request.data = message.recipient;
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
}
