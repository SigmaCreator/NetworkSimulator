package network.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author 15105189 と 15105048
 */
public class NetworkSimulator {
    
    // HashMap<String,Host> getAway; // IP , Host
    HashMap<String,Host> MACcess; // MAC , Host
    HashMap<String,ArrayList<Host>> subNetworks; // subNet ID , subNet Hosts
    
    public void boot() 
    {
        Node N1 = new Node("N1", "00:00:00:00:00:01", "210.0.1.1", 20, "210.0.1.2", "210.0.1");
        Node N2 = new Node("N2", "00:00:00:00:00:02", "210.0.2.1", 10, "210.0.2.2", "210.0.2");
        Node N3 = new Node("N3", "00:00:00:00:00:03", "210.0.3.1", 15, "210.0.3.2", "210.0.3");
        Node N4 = new Node("N4", "00:00:00:00:00:04", "210.0.4.1", 5,  "210.0.4.2", "210.0.4");
        
        Port R1_0 = new Port("R1.0", "00:00:00:00:01:01", "210.0.1.2", 20, "210.0.1");   
        Port R1_1 = new Port("R1.1", "00:00:00:00:01:02", "210.0.2.2", 10, "210.0.2");
        Port R1_2 = new Port("R1.2", "00:00:00:00:01:03", "210.0.10.1", 15, "210.0.10");
        
        ArrayList<Port> R1Ports = new ArrayList<>(Arrays.asList(new Port[]{R1_0, R1_1, R1_2}));
        Router R1 = new Router("R1", R1Ports);
        
        R1_0.setRouter(R1);
        R1_1.setRouter(R1);
        R1_2.setRouter(R1);
        
        Port R2_0 = new Port("R2.0", "00:00:00:00:02:01", "210.0.3.2", 15, "210.0.3");
        Port R2_1 = new Port("R2.1", "00:00:00:00:02:02", "210.0.4.2", 5, "210.0.4");
        Port R2_2 = new Port("R2.2", "00:00:00:00:02:03", "210.0.10.2", 15, "210.0.10");
        
        ArrayList<Port> R2Ports = new ArrayList<>(Arrays.asList(new Port[]{R2_0, R2_1, R2_2}));
        Router R2 = new Router("R2", R2Ports);    
        
        R2_0.setRouter(R2);
        R2_1.setRouter(R2);
        R2_2.setRouter(R2);
        
        Gateway R1_G1 = new Gateway("0.0.0.0", R1_0);
        R1.addTableEntry("210.0.1.0", R1_G1);
        
        Gateway R1_G2 = new Gateway("0.0.0.0", R1_1);
        R1.addTableEntry("210.0.2.0", R1_G2);
        
        Gateway R1_G3 = new Gateway("210.0.10.2", R1_2);
        R1.addTableEntry("210.0.3.0", R1_G3);
        
        R1.addTableEntry("210.0.4.0", R1_G3);
        
        Gateway R1_G4 = new Gateway("0.0.0.0", R1_2);
        R1.addTableEntry("210.0.10.0", R1_G4);
        
        Gateway R2_G1 = new Gateway("0.0.0.0", R2_0);
        R2.addTableEntry("210.0.3.0", R2_G1);
        
        Gateway R2_G2 = new Gateway("0.0.0.0", R2_1);
        R2.addTableEntry("210.0.4.0", R2_G2);
        
        Gateway R2_G3 = new Gateway("210.0.10.1", R2_2);
        R2.addTableEntry("210.0.1.0", R2_G3);
        
        R2.addTableEntry("210.0.2.0", R2_G3);
        
        Gateway R2_G4 = new Gateway("0.0.0.0", R2_2);
        R2.addTableEntry("210.0.10.0", R2_G4);
        
//        getAway = new HashMap<>();
//        
//        getAway.put(N1.IP,N1);
//        getAway.put(N2.IP,N2);
//        getAway.put(N3.IP,N3);
//        getAway.put(N4.IP,N4);
//        getAway.put(R1_0.IP,R1_0);
//        getAway.put(R1_1.IP,R1_1);
//        getAway.put(R1_2.IP,R1_2);
//        getAway.put(R2_0.IP,R2_0);
//        getAway.put(R2_1.IP,R2_1);
//        getAway.put(R2_2.IP,R2_2);
        
        MACcess = new HashMap<>();
        
        MACcess.put(N1.MAC,N1);
        MACcess.put(N2.MAC,N2);
        MACcess.put(N3.MAC,N3);
        MACcess.put(N4.MAC,N4);
        MACcess.put(R1_0.MAC,R1_0);
        MACcess.put(R1_1.MAC,R1_1);
        MACcess.put(R1_2.MAC,R1_2);
        MACcess.put(R2_0.MAC,R2_0);
        MACcess.put(R2_1.MAC,R2_1);
        MACcess.put(R2_2.MAC,R2_2);
        
        
        ArrayList<Host> hosts0 = new ArrayList<>();
        hosts0.add(N1);
        hosts0.add(R1_0);
        
        ArrayList<Host> hosts1 = new ArrayList<>();
        hosts1.add(N2);
        hosts1.add(R1_1);
        
        ArrayList<Host> hosts2 = new ArrayList<>();
        hosts2.add(N3);
        hosts2.add(R2_0);
        
        ArrayList<Host> hosts3 = new ArrayList<>();
        hosts3.add(N4);
        hosts3.add(R2_1);
        
        ArrayList<Host> hosts4 = new ArrayList<>();
        hosts4.add(R1_2);
        hosts4.add(R2_2);
        
        
        subNetworks = new HashMap<>();
        
        subNetworks.put("210.0.1.0",hosts0);
        subNetworks.put("210.0.2.0",hosts1);
        subNetworks.put("210.0.3.0",hosts2);
        subNetworks.put("210.0.4.0",hosts3);
        subNetworks.put("210.0.10.0",hosts4);
        
        Message first = new ICMP(N1.IP, N3.IP, Operation.NEW);
        
        first.data = "abcdefghijklmnopqrstuvwxyz";
        
        execute(N1, first , "", "",false);
    }
    
    public void execute (Host currentHop, Message message, String lastHopIP, String lastHopMAC, boolean moreFragments)
    {
        System.out.println("==================");
        Host nextHop;
        ArrayList<Message> newMessage;
        String nextHopIP = "";
        
        System.out.println("currentHop is " + currentHop);
        System.out.println(currentHop.name + " - " + currentHop.IP);
        
        if (currentHop instanceof Port) // If it's a PORT
        {
            Port port = (Port) currentHop;
            Router router = port.owner;
            
            Gateway nextGateway = router.verifyRouterTable(message.recipient); // Check ROUTERTABLE for GATEWAY of exit
            
            currentHop = nextGateway.port; // The PORT of exit is now the currentHop
            
            System.out.println("Router redirected to " + currentHop.name);
            
            //System.out.println(nextGateway.IP);
            
            if (nextGateway.IP.equals("0.0.0.0")) nextHopIP = message.recipient; // nextHop has direct connection to this port
            else nextHopIP = nextGateway.IP; // nextHopIP is where the GATEWAY leads to
            
            System.out.println("nextHopIP is " + nextHopIP);
            
            newMessage = ((Port) currentHop).writeMessage(message, nextHopIP); // PORT writes the message
            
        }
        else
        {
            nextHopIP = ((Node) currentHop).gateway;
            System.out.println("nextHopIP is " + nextHopIP);
            newMessage = ((Node) currentHop).writeMessage(message,moreFragments); // HOST writes the message
        }       
        
        if (newMessage == null) 
        {
            System.out.println("ICMP fragment received");
            return;
        } // The message was a non-terminal fragment of ICMP
        
        if (newMessage.get(0) instanceof ARP)
            System.out.println("ARP " + newMessage.get(0).operation);
        else
             System.out.println("ICMP " + newMessage.get(0).operation);
        
        System.out.println("Sender: " + newMessage.get(0).sender + " | Recipient: " + newMessage.get(0).recipient + " | Data: " + newMessage.get(0).data);

        
        Message m = newMessage.get(0); // Get a fragment to check protocol

        if (m instanceof ARP && m.operation == Operation.REQUEST) // If it's an ARP REQUEST
        {   
            String subNet = getSubNet(m.sender);
            
            ArrayList<Host> neighbors = subNetworks.get(subNet);
            
            for (Host h : neighbors)
            {
                if (!h.IP.equals(m.sender)) {
                    execute (h, newMessage.get(0), currentHop.IP, currentHop.MAC, false); // REPLY comes back from the cable
                }
            }
            //gatewayMAC = reply.data; // Get MAC
                                   
            //if (!gatewayMAC.equals("")) currentHop.updateTable(gatewayIP,gatewayMAC); // Update ARP TABLE
            
            execute(currentHop, message, "", "", false); // Reexecute, because now the HOST knows where to send the message
            
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
            if (i == newMessage.size() - 1)
                execute (nextHop, newMessage.get(i), currentHop.IP, currentHop.MAC, false);
            else
                execute (nextHop, newMessage.get(i), currentHop.IP, currentHop.MAC, true);
        }
    }
    
    public String getSubNet(String IP)
    {
        int dotCounter = 0;
            int size = 0;
                        
            for (int i = 0; i < IP.length(); i++)
            {
                if (IP.charAt(i) == '.') dotCounter = dotCounter + 1;
                if (dotCounter == 3)
                {
                    size = i;
                    break;
                }
            }
            
        return IP.substring(0, size) + ".0";
                
    }
}
