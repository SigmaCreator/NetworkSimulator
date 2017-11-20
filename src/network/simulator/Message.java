package network.simulator;

public class Message {
    String data;
    String sender;
    String recipient;
    boolean broadcast;
    Operation operation;
    
    public boolean isBroadcast() { return broadcast; }
        
    public String getSender() { return sender; }
    
    public String getRecipient() { return recipient; }
    
    public Operation getOperation() { return operation; }
    
    public void setData(String data) { this.data = data; }
    
}
