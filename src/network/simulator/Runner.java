/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.simulator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author user
 */
public class Runner {
    
    public static void main (String [] args)
    {
        NetworkSimulator net = new NetworkSimulator();
        
        BufferedReader br = null;
        
        try 
        {
            br = new BufferedReader(new FileReader("executions.txt"));
            
            String line, topology, sender, recipient, message;
            
            while ((line = br.readLine()) != null)
            {
                String [] info = line.split(" ");
                topology = info[0];
                sender = info[1];
                recipient = info[2];
                message = info[3];
                
                net.boot(topology, sender, recipient, message);
                
            }
        
        } catch (IOException e) { e.printStackTrace(); } 
    }
    
}
