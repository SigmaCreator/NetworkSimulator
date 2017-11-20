package network.simulator;

public class ARP extends Message{
    
    String senderMAC;
            
    public ARP (String sender, String recipient, Operation operation)
    {
        if (operation == Operation.REQUEST) 
        {
            broadcast = true;
            
            /* Who has data? Tell sender.IP*/
            data = recipient;
        }
        else if (operation == Operation.REPLY)
        { 
            broadcast = false;
            
            /* sender.IP is at data */
        }

        this.sender = sender; 
        this.recipient = recipient;
        
    }
    
    public String getSenderMAC () { return senderMAC; }
    
}
