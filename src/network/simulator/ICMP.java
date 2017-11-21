package network.simulator;

public class ICMP extends Message{
    
    public ICMP (String sender, String recipient, Operation operation)
    {        
        this.sender = sender; 
        this.recipient = recipient;
        this.operation = operation;
    }
}
