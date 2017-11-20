package network.simulator;

public class Port extends Host
{
    Router owner;
    Port(String name, String MAC, String IP, int MTU, Router owner) 
    {
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
        this.MTU = MTU;
        this.owner = owner;
    } 
    
    
    public Message [] writeMessage(Message [] messageList)
    {
        boolean send = true;
         
        for (Message message : messageList)
            if (message.data.length() > (MTU - 20))
                send = false;

        if (send) return messageList;
        else return null;
    }
}
