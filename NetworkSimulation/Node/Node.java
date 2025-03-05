package NetworkSimulation.Node;

import NetworkSimulation.DataUnit.DataUnit;
import NetworkSimulation.Network.Network;

public class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Network network;

    public Node(String MACAddress, Network network) {
        ipAddress = null;
        this.MACAddress = MACAddress;
        this.network = network;
    }

    public void receive(DataUnit data) {
        System.out.println(data.toString());
    }
}
