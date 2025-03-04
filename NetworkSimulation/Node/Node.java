package NetworkSimulation.Node;

import NetworkSimulation.Packet.UDPPacket;

public class Node {
    String ipAddress;
    String MACAddress;

    public Node(String MACAddress) {
        ipAddress = null;
        this.MACAddress = MACAddress;
    }

    public void receive(UDPPacket packet) {
        System.out.println(packet.toString());
    }
}
