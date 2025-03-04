package NetworkSimulation.Node.End;

import NetworkSimulation.Node.Node;

public class Computer extends Node {

    public Computer(String MACAddress) {
        super(MACAddress);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
