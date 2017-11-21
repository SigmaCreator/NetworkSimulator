/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.simulator;

/**
 *
 * @author user
 */
public class Runner {
    
    public static void main (String [] args)
    {
        NetworkSimulator net = new NetworkSimulator();
        
        net.boot("test.txt");
    }
    
}
