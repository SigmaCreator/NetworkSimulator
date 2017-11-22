/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package network.simulator;

public class Log {
    
    private static Log log = new Log();
    
    StringBuffer buffer;
    
    private Log () 
    {
        buffer = new StringBuffer();
    }
    
    public static Log getInstance()
    {
        return log;
    }
    
    public void writeLog(String line)
    {
        buffer.append(line);
    }
    
    public String getLog()
    {
        return buffer.toString();
    }
}
