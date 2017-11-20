package network.simulator;

public class Node extends Host {
    
    String gateway;
    // Port port;
    
    public Node (String name, String MAC, String IP, int MTU, String gateway)
    {
        this.name = name;
        this.MAC = MAC;
        this.IP = IP;
        this.MTU = MTU;
        this.gateway = gateway;
    }
    
    // public Port getPort() { return port; }
    
    // public void setPort(Port port) { this.port = port; }
    
    public Message [] writeMessage (Message [] messageList) {
        
//        if (recipient.equals(IP)) // Se eu sou o destinatário, quer dizer que a mensagem chegou até mim e agora tenho que responder
//        {
//            // Message reply = new ICMP (recipient, sender, Operation.REPLY);
//            // return reply;
//        }
//        
//        
//        if (hasMACOf(gateway) != null)
//        { 
//            // message = new ICMP(); 
//        }
//        
//        else
//        {
//            Message request = new ARP (sender, gateway, Operation.REQUEST);
//            return new Message [] { request };
//        }
//        
        return null;
    }
    
    public Message receiveMessage(Message message)
    {
//        if (message.isBroadcast())
//        {
//            if(message instanceof ARP)
//            {
//                if(message.getRecipient().equals(IP)) { return replyMessage(Protocol.ARP, message.getSender().IP); }
//            }    
//        }
//        return null;
        return null;
    }
    
    public Message replyMessage(Protocol protocol, String dest)
    {
//        Message reply;
//        if (protocol == Protocol.ARP) { reply = new ARP(this, dest, Operation.REPLY); }
//        
//        /* Mudar pra ICMP */
//        else reply = new ARP(this, dest, Operation.REPLY);
//        return reply;
        return null;
    }

    String getGatewayIP() { return gateway; }
}
