package network.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 *
 * @author 15105189 と 15105048
 */
public class NetworkSimulator {
    
    HashMap<String,Host> getAway; // IP , Host
    HashMap<String,Host> MACcess; // MAC , Host
    
    public void main(String[] args) 
    {
//        Node N1 = new Node("N1", "00:00:00:00:00:01", "210.0.1.1", 20, "210.0.1.2");
//        Node N2 = new Node("N2", "00:00:00:00:00:02", "210.0.2.1", 10, "210.0.2.2");
//        Node N3 = new Node("N3", "00:00:00:00:00:03", "210.0.3.1", 15, "210.0.3.2");
//        Node N4 = new Node("N4", "00:00:00:00:00:04", "210.0.4.1", 5,  "210.0.4.2");
//        
//        Port R1_0 = new Port("R1.0", "00:00:00:00:01:01", "210.0.1.2", 20);
//        //N1.setPort(R1_0);
//        
//        Port R1_1 = new Port("R1.1", "00:00:00:00:01:02", "210.0.2.2", 10);
//        //N2.setPort(R1_1);
//        
//        Port R1_2 = new Port("R1.2", "00:00:00:00:01:03", "210.0.10.1", 15);
//        
//        ArrayList<Port> R1Ports = new ArrayList<>(Arrays.asList(new Port[]{R1_0, R1_1, R1_2}));
//        Router R1 = new Router("R1", R1Ports);
//        
//        Port R2_0 = new Port("R2.0", "00:00:00:00:02:01", "210.0.3.2", 15);
//        //N3.setPort(R2_0);
//        
//        Port R2_1 = new Port("R2.1", "00:00:00:00:02:02", "210.0.4.2", 5);
//        //N4.setPort(R2_1);
//        
//        Port R2_2 = new Port("R2.2","00:00:00:00:02:03", "210.0.10.2", 15);
//        
//        //ArrayList<Port> R2Ports = new ArrayList<>(Arrays.asList(new Port[]{R2_0, R2_1, R2_2}));
//        Router R2 = new Router("R2", R2Ports);     
//        
//        Gateway R1_G1 = new Gateway("0.0.0.0", R1_0);
//        R1.addTableEntry("210.0.1.0", R1_G1);
//        
//        Gateway R1_G2 = new Gateway("0.0.0.0", R1_1);
//        R1.addTableEntry("210.0.2.0", R1_G2);
//        
//        Gateway R1_G3 = new Gateway("210.0.10.2", R1_2);
//        R1.addTableEntry("210.0.3.0", R1_G3);
//        
//        R1.addTableEntry("210.0.4.0", R1_G3);
//        
//        Gateway R1_G4 = new Gateway("0.0.0.0", R1_2);
//        R1.addTableEntry("210.0.10.0", R1_G4);
//        
//        Gateway R2_G1 = new Gateway("0.0.0.0", R2_0);
//        R2.addTableEntry("210.0.3.0", R2_G1);
//        
//        Gateway R2_G2 = new Gateway("0.0.0.0", R2_1);
//        R2.addTableEntry("210.0.4.0", R2_G2);
//        
//        Gateway R2_G3 = new Gateway("210.0.10.1", R2_2);
//        R2.addTableEntry("210.0.1.0", R2_G3);
//        
//        R2.addTableEntry("210.0.2.0", R2_G3);
//        
//        Gateway R2_G4 = new Gateway("0.0.0.0", R2_2);
//        R2.addTableEntry("210.0.10.0", R2_G4);
//        
//        routerList = new ArrayList<>();
//        
//        routerList.add(R1);
//        routerList.add(R2);      
    }
    
    public Message [] execute (Host currentHop, Message [] messageList, String lastHopIP, String lastHopMAC)
    {
        Host nextHop;
        Message [] newMessage;
        String nextHopIP = "";
        
        if (currentHop instanceof Port) // If it's a PORT
        {
            Port port = (Port) currentHop;
            Router router = port.owner;
            
            Gateway nextGateway = router.verifyRouterTable(messageList[0].recipient); // Check ROUTERTABLE for GATEWAY of exit
            
            currentHop = nextGateway.port; // The PORT of exit is now the currentHop
            
            nextHopIP = nextGateway.IP; // nextHopIP is where the GATEWAY leads to
            
            newMessage = currentHop.writeMessage(messageList); // PORT writes the message
        }
        else
        {
            nextHopIP = ((Node) currentHop).gateway;
            
            newMessage = currentHop.writeMessage(messageList); // HOST writes the message
        }       
        
        if (newMessage == null) return null; // The message was an ARP REQUEST, the HOST didn't have info
        
        Message m = newMessage[0]; // Get a fragment to check protocol

        if (m instanceof ARP && m.operation == Operation.REQUEST) // If it's an ARP REQUEST
        {
            String gatewayIP = m.data; // Check which gateway MAC was requested
            String gatewayMAC = "";
                    
            Host gateway = getAway.get(gatewayIP); // Send through the cable
            
            Message [] reply = execute (gateway, newMessage, currentHop.IP, currentHop.MAC); // REPLY comes back from the cable
            
            gatewayMAC = reply[0].data; // Get MAC
                                   
            if (!gatewayMAC.equals("")) currentHop.updateTable(gatewayIP,gatewayMAC); // Update ARP TABLE
            
            return execute(currentHop, messageList, "", ""); // Reexecute, because now the HOST knows where to send the message
        }
        else if (m instanceof ARP && m.operation == Operation.REPLY) // If it's an ARP REPLY
        {
            currentHop.updateTable(lastHopIP,lastHopMAC); // Updtae ARP TABLE with the info from the lastHop, just in case it's necessary later
            nextHopIP = lastHopIP;
        }
            
        String nextHopMAC = currentHop.hasMACOf(nextHopIP); // Get MAC of the nextHop
        nextHop = MACcess.get(nextHopMAC);
            
        if (currentHop instanceof Port)
            return execute (nextHop, newMessage, lastHopIP, lastHopMAC);
        else
            return execute(nextHop, messageList, currentHop.IP, currentHop.MAC); 
    }
}
