package NetworkSimulation.DHCP;

import NetworkSimulation.Network.Network;
import NetworkSimulation.Node.End.DHCPServer;

public class DHCPTest {
    public static void main(String[] args) throws Exception {
        Network network = new Network("192.168.1.0", "255.255.255.0");
        DHCPServer dhcpServer = new DHCPServer("00-1A-2B-3C-4D-5E", network);
    }
}
