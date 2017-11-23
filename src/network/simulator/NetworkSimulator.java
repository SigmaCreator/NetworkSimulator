package network.simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author 15105189 と 15105048
 */
public class NetworkSimulator {

    Log log;
    ArrayList<Router> routers;
    ArrayList<Node> nodes;
    HashMap<String,Host> getAway; // IP , HOST
    HashMap<String,Host> MACcess; // MAC , HOST
    HashMap<String,ArrayList<Host>> subNetworks; // subNet ID , subNet HOSTS
    
    public void boot(String filename, String senderName, String recipientName, String messageText) 
    {
        log = Log.getInstance();
        routers = new ArrayList<>();
        nodes = new ArrayList<>();
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
                        
                        nodes.add(N);
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
        
        
        Node sender = null;
        Node recipient = null;
        
        for (Node n : nodes)
        {
            if ( n.name.equalsIgnoreCase(senderName) )
                sender = n;
            if ( n.name.equalsIgnoreCase(recipientName) )
                recipient = n;
        }
                
        
                
        Message first = new ICMP(sender.IP, recipient.IP, Operation.NEW);
        
        first.data = messageText;
        
        first.moreFragments = false;
        
        first.ttl = 8;
        
        log.writeLog("\n");
        log.writeLog("========================================================================================================================\n");
        log.writeLog("Topologia: " + filename + " - Origem: " + senderName + " - Destino: " + recipientName + " - Mesagem: " + messageText + "\n");
        log.writeLog("========================================================================================================================\n");
        
        execute(sender, first , "", "");

        System.out.println(Log.getInstance().getLog());
        
        BufferedWriter writer;
        
        try {
            writer = new BufferedWriter(new FileWriter("output.txt"));
            writer.write(Log.getInstance().getLog());
            writer.close();
        } catch (IOException e) {}
    }
    
    public void execute (Host currentHop, Message message, String lastHopIP, String lastHopMAC)
    {
        Host nextHop;
        ArrayList<Message> newMessage;
        String nextHopIP = "";
        
        if (currentHop instanceof Port) // If it's a PORT
        {
            Port port = (Port) currentHop;
            Router router = port.owner;
            
            Gateway nextGateway = router.verifyRouterTable(message.recipient); // Check ROUTERTABLE for GATEWAY of exit

            currentHop = nextGateway.port; // The PORT of exit is now the currentHop
            
            if (nextGateway.IP.equals("0.0.0.0")) nextHopIP = message.recipient; // nextHop has direct connection to this port
            else nextHopIP = nextGateway.IP; // nextHopIP is where the GATEWAY leads to
                        
            newMessage = ((Port) currentHop).writeMessage(message, nextHopIP); // PORT writes the message
            
        }
        else
        {
            nextHopIP = ((Node) currentHop).gateway;

            newMessage = ((Node) currentHop).writeMessage(message); // HOST writes the message
        }
        
                /////////////////////////
                // LOGGING STUFF BEGIN //
                /////////////////////////
                
        if (message.operation != Operation.NEW)
        {
            Host lastIntermediateHop = getAway.get(lastHopIP);
            
            if (message instanceof ARP && message.operation == Operation.REQUEST) // OK
            {
                if (lastIntermediateHop instanceof Node)
                    log.writeLog(lastIntermediateHop.name + " box " + lastIntermediateHop.name + " :  ARP - Who has " + message.data + "? Tell " + lastHopIP + ";\n");     
                else if (lastIntermediateHop instanceof Port)
                    log.writeLog(((Port) lastIntermediateHop).owner.name + " box " + ((Port) lastIntermediateHop).owner.name + " :  ARP - Who has " + message.data + "? Tell " + lastHopIP + ";\n");
            }
            else if (message instanceof ARP && message.operation == Operation.REPLY)
            {
                
                if (lastIntermediateHop instanceof Node)
                {
                    if (getAway.get(message.recipient) instanceof Node) 
                        log.writeLog(lastIntermediateHop.name + " => "  + getAway.get(message.recipient).name + " :  ARP - " + lastHopIP + " is at " +  message.data + ";\n");
                    else if (getAway.get(message.recipient) instanceof Port)
                        log.writeLog(lastIntermediateHop.name + " => "  + ((Port) getAway.get(message.recipient)).owner.name + " :  ARP - " + lastHopIP + " is at " +  message.data + ";\n");
                }
                else if (getAway.get(message.recipient) instanceof Node)
                    log.writeLog(((Port) lastIntermediateHop).owner.name + " => "  + getAway.get(message.recipient).name + " :  ARP - " + lastHopIP + " is at " +  message.data + ";\n");
                else if (getAway.get(message.recipient) instanceof Port)
                    log.writeLog(((Port) lastIntermediateHop).owner.name + " => "  + ((Port) getAway.get(message.recipient)).owner.name + " :  ARP - " + lastHopIP + " is at " +  message.data + ";\n");
            }
            else if (message instanceof ICMP)
            {             
                if (lastIntermediateHop instanceof Node)
                {
                    if (currentHop instanceof Node)
                    {
                        log.writeLog(lastIntermediateHop.name + " => "  + currentHop.name + " :  ICMP - Echo (ping) " + message.operation + " (src=" + message.sender + " dst=" + message.recipient + " ttl=" + message.ttl +  " data=" + message.data + ");\n");
                    }
                    else if (currentHop instanceof Port)
                    {   
                        log.writeLog(lastIntermediateHop.name + " => "  + ((Port) currentHop).owner.name + " :  ICMP - Echo (ping) " + message.operation + " (src=" + message.sender + " dst=" + message.recipient + " ttl=" + message.ttl + " data=" + message.data + ");\n");
                    }
                }
                else if (currentHop instanceof Node)
                {
                    log.writeLog(((Port) lastIntermediateHop).owner.name + " => "  + currentHop.name +" :  ICMP - Echo (ping) " + message.operation + " (src=" + message.sender + " dst=" + message.recipient + " ttl=" + message.ttl + " data=" + message.data + ");\n");
                }
                else if (currentHop instanceof Port && !currentHop.IP.equals(lastHopIP))
                {
                    log.writeLog(((Port) lastIntermediateHop).owner.name + " => "  + ((Port) currentHop).owner.name + " :  ICMP - Echo (ping) " + message.operation + " (src=" + message.sender + " dst=" + message.recipient + " ttl=" + message.ttl + " data=" + message.data + ");\n");
                }
                
                if (((ICMP) message).moreFragments == false && currentHop instanceof Node)
                {
                    log.writeLog(currentHop.name + " rbox " + currentHop.name + " : Received " + ((Node) currentHop).buffer.toString() + ";\n");
                }
            }
        }
        
                ///////////////////////
                // LOGGING STUFF END //
                ///////////////////////
                
        if (newMessage == null) return; // There was no need to REPLY or whatever
        
        Message m = newMessage.get(0); // Get a fragment to check protocol

        if (m instanceof ARP && m.operation == Operation.REQUEST) // If it's an ARP REQUEST
        {  
            
            ArrayList<Host> neighbors = subNetworks.get(currentHop.subNet);
            
            for (Host h : neighbors)
            {
                
                if (!h.IP.equals(m.sender)) {
                    execute (h, newMessage.get(0), currentHop.IP, currentHop.MAC); // REPLY comes back from the cable
                }
            }

            if (currentHop instanceof Node) 
            {
                message.operation = Operation.NEW;
                execute(currentHop, message, "", ""); // Reexecute, because now the HOST knows where to send the message
            }
            else
            {
                execute(currentHop, message, currentHop.IP, currentHop.MAC);
            }
                
            
            return;
        }
        else if (m instanceof ARP && m.operation == Operation.REPLY) // If it's an ARP REPLY
        {   
            currentHop.updateTable(lastHopIP,lastHopMAC); // Updtae ARP TABLE with the info from the lastHop, just in case it's necessary later
            nextHopIP = lastHopIP;
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
