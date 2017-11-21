package network.simulator;

public class ARP extends Message{

    String senderMAC;
            
    public ARP (String sender, String recipient, String senderMAC, Operation operation)
    {        
        this.sender = sender; 
        this.recipient = recipient;
        this.operation = operation;
        this.senderMAC = senderMAC;
    }
    
}
