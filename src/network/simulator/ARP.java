package network.simulator;

public class ARP extends Message{
            
    public ARP (String sender, String recipient, Operation operation)
    {        
        this.sender = sender; 
        this.recipient = recipient;
        this.operation = operation;
    }
    
}
