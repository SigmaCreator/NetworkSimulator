
package network.simulator;

public class Runner {
    
    public static void main (String [] args)
    {
        NetworkSimulator net = new NetworkSimulator();
        
        net.boot(args[0], args[1], args[2], args[3]);   

    }
    
}
