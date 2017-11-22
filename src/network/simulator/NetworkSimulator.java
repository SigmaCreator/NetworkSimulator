package network.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author 15105189 と 15105048
 */
public class NetworkSimulator {
    
    ArrayList<Router> routers;
    HashMap<String,Host> getAway; // IP , HOST
    HashMap<String,Host> MACcess; // MAC , HOST
    HashMap<String,ArrayList<Host>> subNetworks; // subNet ID , subNet HOSTS
    
    public void boot(String filename) 
    {
        routers = new ArrayList<>();
        getAway = new HashMap<>();
        MACcess = new HashMap<>();
        subNetworks = new HashMap<>();
                        
        BufferedReader br = null;
        
        try 
        {
            br = new BufferedReader(new FileReader(filename));
            String line;
            String [] info;
            
            String name, MAC, IP, gateway;
            int MTU;
            
            int state = 0; // 1 - NODE , 2 - #ROUTER, 3 - #ROUTERTABLE
            
            while ((line = br.readLine()) != null) 
            {
                System.out.println(line);
                        
		if (line.equals("#NODE")) { state = 1; continue; }
                if (line.equals("#ROUTER")) { state = 2; continue; }
                if (line.equals("#ROUTERTABLE")) { state = 3; continue; }
                
                switch (state)
                {
                    case 1 :
                        
                        info = line.split(",");
                        
                        name = info[0];
                        MAC = info[1];
                        IP = info[2];
                        MTU = Integer.parseInt(info[3]);
                        gateway = info[4];
                        
                        Node N = new Node(name, MAC, IP, MTU, gateway);
                        
                        getAway.put(N.IP,N);
                        MACcess.put(N.MAC,N);
                        addToSubNetworks(N);
                        
                        break;
                        
                    case 2 :
                        
                        info = line.split(",");
                        
                        Router router = new Router(info[0], new ArrayList<>());
                        Port port = null;
                        int portCounter = 0;
                                                
                        for (int i = 2; i < info.length; i += 3)
                        {
                            name = router.name + "." + portCounter;
                            
                            MAC = info[i];
                            IP = info[i+1];
                            MTU = Integer.parseInt(info[i+2]);
                            
                            port = new Port(name, MAC, IP, MTU);
                            port.owner = router;
                            
                            router.addPort(port);
                            
                            getAway.put(port.IP,port);
                            MACcess.put(port.MAC,port);
                            addToSubNetworks(port);
                            
                            portCounter = portCounter + 1;
                        }
                        
                        routers.add(router);
                        
                        break;
                        
                    case 3 :
                        
                        info = line.split(",");
                        
                        Router currentRouter = null;
                        Port currentPort = null;
                        Gateway g = null;
                        
                        for (Router r : routers)
                        {
                            if (r.name.equals(info[0])) currentRouter = r;
                        }
                        
                        for (Port p : currentRouter.ports)
                        {
                            if (p.name.equals(currentRouter.name + "." + info[3]))
                                currentPort = p;
                        }
                        
                        g = new Gateway(info[2],currentPort);
                        currentRouter.addTableEntry(info[1], g);
                     
                    default : break;
                }
            }

        } 
        catch (IOException e) { e.printStackTrace(); } 

        Node sender = (Node) getAway.get("210.0.1.1");
        Node recipient = (Node) getAway.get("210.0.4.1");
                
        Message first = new ICMP(sender.IP, recipient.IP, Operation.NEW);
        
        first.data = "abcdefghijklmnopqrstuvwxyz";
        
        first.moreFragments = false;
        
        execute(sender, first , "", "");
        
        System.out.println("==================");
        
        System.out.println(Log.getInstance().getLog());
    }
    
    public void execute (Host currentHop, Message message, String lastHopIP, String lastHopMAC)
    {
        System.out.println("==================");
        Host nextHop;
        ArrayList<Message> newMessage;
        String nextHopIP = "";
        
        System.out.println("Message arrived at " + currentHop.name + " - " + currentHop.IP);
        
        if (currentHop instanceof Port) // If it's a PORT
        {
            Port port = (Port) currentHop;
            Router router = port.owner;
            
            Gateway nextGateway = router.verifyRouterTable(message.recipient); // Check ROUTERTABLE for GATEWAY of exit
            
            currentHop = nextGateway.port; // The PORT of exit is now the currentHop
            
            System.out.println("Router redirected to " + currentHop.name);
            
            if (nextGateway.IP.equals("0.0.0.0")) nextHopIP = message.recipient; // nextHop has direct connection to this port
            else nextHopIP = nextGateway.IP; // nextHopIP is where the GATEWAY leads to
            
            System.out.println("Next hop is " + nextHopIP);
            
            newMessage = ((Port) currentHop).writeMessage(message, nextHopIP); // PORT writes the message
            
        }
        else
        {
            nextHopIP = ((Node) currentHop).gateway;
            System.out.println("Next hop is " + nextHopIP);
            newMessage = ((Node) currentHop).writeMessage(message); // HOST writes the message
        }       
        
        if (newMessage == null) 
        {
            System.out.println(currentHop.name + " received a REPLY");
            return;
        }
        
        if (newMessage.get(0) instanceof ARP)
            System.out.println(currentHop.name + " wrote an ARP " + newMessage.get(0).operation);
        else
            System.out.println(currentHop.name + " wrote an ICMP " + newMessage.get(0).operation);
        
        System.out.println("Sender: " + newMessage.get(0).sender + " | Recipient: " + newMessage.get(0).recipient + " | Data: " + newMessage.get(0).data);

        Message m = newMessage.get(0); // Get a fragment to check protocol

        if (m instanceof ARP && m.operation == Operation.REQUEST) // If it's an ARP REQUEST
        {  
            if (currentHop instanceof Node)
                Log.getInstance().writeLog(currentHop.name + " box " + currentHop.name + " :  ARP - Who has " + m.data + "? Tell " + currentHop.IP + "\n");     
            else if (currentHop instanceof Port)
                Log.getInstance().writeLog(((Port) currentHop).owner.name + " box " + ((Port) currentHop).owner.name + " :  ARP - Who has " + m.data + "? Tell " + currentHop.IP + "\n");

            ArrayList<Host> neighbors = subNetworks.get(currentHop.subNet);

            System.out.println("Current subNet : " + currentHop.subNet);
            
            for (Host h : neighbors)
            {
                if (!h.IP.equals(m.sender)) {
                    execute (h, newMessage.get(0), currentHop.IP, currentHop.MAC); // REPLY comes back from the cable
                }
            }
            
            execute(currentHop, message, "", ""); // Reexecute, because now the HOST knows where to send the message
            
            return;
        }
        else if (m instanceof ARP && m.operation == Operation.REPLY) // If it's an ARP REPLY
        {   
            if (currentHop instanceof Node)
            {
                if (getAway.get(m.recipient) instanceof Node) 
                    Log.getInstance().writeLog(currentHop.name + " => "  + getAway.get(m.recipient).name +" :  ARP - " + m.data + " is at " + currentHop.IP + "\n");
                else if (getAway.get(m.recipient) instanceof Port)
                    Log.getInstance().writeLog(currentHop.name + " => "  + ((Port) getAway.get(m.recipient)).owner.name +" :  ARP - " + m.data + " is at " + currentHop.IP + "\n");
            }
            else if (getAway.get(m.recipient) instanceof Node)
                Log.getInstance().writeLog(((Port) currentHop).owner.name + " => "  + getAway.get(m.recipient).name +" :  ARP - " + m.data + " is at " + currentHop.IP + "\n");
            else if (getAway.get(m.recipient) instanceof Port)
                Log.getInstance().writeLog(((Port) currentHop).owner.name + " => "  + ((Port) getAway.get(m.recipient)).owner.name +" :  ARP - " + m.data + " is at " + currentHop.IP + "\n");

            
            currentHop.updateTable(lastHopIP,lastHopMAC); // Updtae ARP TABLE with the info from the lastHop, just in case it's necessary later
            nextHopIP = lastHopIP;
        }
        
        if (m instanceof ICMP)
        {
            if (currentHop instanceof Node)
            {
                if (getAway.get(nextHopIP) instanceof Node)                
                    Log.getInstance().writeLog(currentHop.name + " => "  + getAway.get(nextHopIP).name +" :  ICMP - Echo (ping) " + m.operation + " (src = " + m.sender + " , dst = " + m.recipient + ", ttl = " + " , data = " + m.data + "\n");
                else if (getAway.get(m.recipient) instanceof Port)
                {   
                    Log.getInstance().writeLog(" \n AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA " + nextHopIP + "aaaa \n");
                    Log.getInstance().writeLog(currentHop.name + " => "  + ((Port) currentHop).owner.name +" :  ICMP - Echo (ping) " + m.operation + " (src = " + m.sender + " , dst = " + m.recipient + ", ttl = " + " , data = " + m.data + "\n");
                }
            }
            else if (getAway.get(nextHopIP) instanceof Node)
                Log.getInstance().writeLog(((Port) currentHop).owner.name + " => "  + getAway.get(nextHopIP).name +" :  ICMP - Echo (ping) " + m.operation + " (src = " + m.sender + " , dst = " + m.recipient + ", ttl = " + " , data = " + m.data + "\n");
            else if (getAway.get(nextHopIP) instanceof Port)
                Log.getInstance().writeLog(((Port) currentHop).owner.name + " => "  + ((Port) getAway.get(nextHopIP)).owner.name +" :  ICMP - Echo (ping) " + m.operation + " (src = " + m.sender + " , dst = " + m.recipient + ", ttl = " + " , data = " + m.data + "\n");
        }
        
        String nextHopMAC = currentHop.hasMACOf(nextHopIP); // Get MAC of the nextHop
        nextHop = MACcess.get(nextHopMAC);
        
        for (int i = 0; i < newMessage.size(); i++)
        {
           execute (nextHop, newMessage.get(i), currentHop.IP, currentHop.MAC);
         
        }
    }
    
    public void addToSubNetworks (Host host) 
    {
       if (subNetworks.containsKey(host.subNet))
           subNetworks.get(host.subNet).add(host);
       else
       {
           ArrayList<Host> hosts = new ArrayList<>();
           hosts.add(host);
           subNetworks.put(host.subNet,hosts);
       }
    }
    
}
