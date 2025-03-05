package NetworkSimulation.Node;

import NetworkSimulation.Network.Network;
import NetworkSimulation.DataUnit.TransportLayer.UDPDatagram;

public class Node {
    protected String ipAddress;
    protected String MACAddress;
    protected Network network;

    public Node(String MACAddress, Network network) {
        ipAddress = null;
        this.MACAddress = MACAddress;
        this.network = network;
    }

    public void receive(UDPDatagram packet) {
        System.out.println(packet.toString());
    }
}
